/*
 * Copyright 2020-2099 sa-token.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.integration.oauth2.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 集成测 HTTP 客户端：GET / POST，不自动跟跳，记下 Location 和 Cookie。
 */
public final class OAuth2Http {

	private OAuth2Http() {
	}

	/** 浏览器会话：同一组 Cookie */
	public static final class Session {
		private final Map<String, String> cookies = new LinkedHashMap<>();

		/** GET 本机路径，带上当前 Cookie */
		public Resp get(int port, String path) {
			return exchange("GET", "http://127.0.0.1:" + port + path, null, cookies, true);
		}

		/** POST form 到本机路径，带上当前 Cookie */
		public Resp post(int port, String path, Map<String, String> form) {
			return exchange("POST", "http://127.0.0.1:" + port + path, form, cookies, true);
		}
	}

	/** 职责：发请求，可选写回 Cookie，绝不 follow redirect */
	public static Resp exchange(String method, String url, Map<String, String> form,
			Map<String, String> cookies, boolean storeCookies) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod(method);
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(15000);
			if (cookies != null && !cookies.isEmpty()) {
				conn.setRequestProperty("Cookie", joinCookies(cookies));
			}
			if ("POST".equals(method)) {
				conn.setDoOutput(true);
				conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				byte[] body = encodeForm(form).getBytes(StandardCharsets.UTF_8);
				try (OutputStream os = conn.getOutputStream()) {
					os.write(body);
				}
			}
			int code = conn.getResponseCode();
			String location = conn.getHeaderField("Location");
			if (storeCookies && cookies != null) {
				List<String> setCookie = conn.getHeaderFields().get("Set-Cookie");
				if (setCookie != null) {
					for (String line : setCookie) {
						putCookie(cookies, line);
					}
				}
			}
			InputStream in = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
			if (in == null) {
				return new Resp(code, "", location);
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n;
			while ((n = in.read(buf)) >= 0) {
				out.write(buf, 0, n);
			}
			return new Resp(code, new String(out.toByteArray(), StandardCharsets.UTF_8), location);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/** 职责：拼 application/x-www-form-urlencoded */
	public static String encodeForm(Map<String, String> form) {
		if (form == null || form.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> e : form.entrySet()) {
			if (sb.length() > 0) {
				sb.append('&');
			}
			sb.append(urlEncode(e.getKey())).append('=').append(urlEncode(e.getValue() == null ? "" : e.getValue()));
		}
		return sb.toString();
	}

	/** 职责：从 Set-Cookie 里抠 name=value */
	private static void putCookie(Map<String, String> cookies, String setCookie) {
		String first = setCookie.split(";", 2)[0];
		int eq = first.indexOf('=');
		if (eq > 0) {
			cookies.put(first.substring(0, eq).trim(), first.substring(eq + 1).trim());
		}
	}

	/** 职责：拼 Cookie 头 */
	private static String joinCookies(Map<String, String> cookies) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> e : cookies.entrySet()) {
			if (sb.length() > 0) {
				sb.append("; ");
			}
			sb.append(e.getKey()).append('=').append(e.getValue());
		}
		return sb.toString();
	}

	/** 职责：URL 编码 */
	private static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** HTTP 响应：状态码 + 体 + Location */
	public static final class Resp {
		public final int status;
		public final String body;
		public final String location;

		/** 记下状态码、响应体和 Location */
		public Resp(int status, String body, String location) {
			this.status = status;
			this.body = body;
			this.location = location;
		}

		/** 是不是一次跳转 */
		public boolean isRedirect() {
			return status == 301 || status == 302 || status == 303 || status == 307 || status == 308;
		}
	}

}

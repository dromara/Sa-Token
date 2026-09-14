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
package cn.dev33.satoken.integration.sso.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 集成测 HTTP 客户端：不自动跟跳，记下 Location 和 Cookie。
 */
public final class SsoHttp {

	private SsoHttp() {
	}

	/** 浏览器会话：同一组 Cookie 打 Server / Client */
	public static final class Session {
		private final Map<String, String> cookies = new LinkedHashMap<>();

		/** GET 本机路径，带上当前 Cookie */
		public Resp get(int port, String path) {
			return exchange("http://127.0.0.1:" + port + path, cookies, true);
		}

		/** GET 完整 URL（用来跟 Location），带上当前 Cookie */
		public Resp getUrl(String url) {
			return exchange(url, cookies, true);
		}

		/** 跟一次跳转：Location 相对路径时补上本机 origin */
		public Resp follow(int port, Resp resp) {
			String loc = resp.location;
			if (loc != null && loc.startsWith("/")) {
				loc = "http://127.0.0.1:" + port + loc;
			}
			return getUrl(loc);
		}
	}

	/** 服务端互调：不带浏览器 Cookie */
	public static String plainGet(String url) {
		return exchange(url, null, false).body;
	}

	/** 职责：发 GET，可选写回 Cookie，绝不 follow redirect */
	public static Resp exchange(String url, Map<String, String> cookies, boolean storeCookies) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(15000);
			if (cookies != null && !cookies.isEmpty()) {
				conn.setRequestProperty("Cookie", joinCookies(cookies));
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

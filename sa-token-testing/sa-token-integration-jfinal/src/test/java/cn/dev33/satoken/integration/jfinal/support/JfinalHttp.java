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
package cn.dev33.satoken.integration.jfinal.support;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 集成测用的 HTTP 客户端：打本机 JFinal Undertow 端口。
 */
public final class JfinalHttp {

	private JfinalHttp() {
	}

	/** GET 指定路径，不带头，只拿响应体 */
	public static String get(int port, String path) {
		return exchange(port, path, null).body;
	}

	/** GET 指定路径，可选带 satoken 头，只拿响应体 */
	public static String get(int port, String path, String token) {
		return exchange(port, path, token).body;
	}

	/** GET 指定路径，带回状态码和响应体 */
	public static Resp exchange(int port, String path) {
		return exchange(port, path, null);
	}

	/** GET 指定路径，可选带 satoken 头，带回状态码和响应体 */
	public static Resp exchange(int port, String path, String token) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL("http://127.0.0.1:" + port + path);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(10000);
			if (token != null) {
				conn.setRequestProperty("satoken", token);
			}
			int code = conn.getResponseCode();
			InputStream in = code >= 400 ? conn.getErrorStream() : conn.getInputStream();
			if (in == null) {
				return new Resp(code, "");
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n;
			while ((n = in.read(buf)) >= 0) {
				out.write(buf, 0, n);
			}
			return new Resp(code, new String(out.toByteArray(), StandardCharsets.UTF_8));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/** HTTP 响应：状态码 + 文本体 */
	public static final class Resp {
		public final int status;
		public final String body;

		/** 记下状态码和响应体 */
		public Resp(int status, String body) {
			this.status = status;
			this.body = body;
		}
	}
}

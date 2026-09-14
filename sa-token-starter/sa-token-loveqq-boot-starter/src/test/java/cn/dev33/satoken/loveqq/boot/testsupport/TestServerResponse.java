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
package cn.dev33.satoken.loveqq.boot.testsupport;

import com.kfyty.loveqq.framework.web.core.http.ServerResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 单测用的 LoveQQ 响应：能看见状态码、头、写回内容和重定向地址。
 */
public class TestServerResponse implements ServerResponse {

	private String contentType;
	private int status = 200;
	private String redirect;
	private String forward;
	private boolean failOnOutputStream;
	private final ByteArrayOutputStream body = new ByteArrayOutputStream();
	private final Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
	private final List<HttpCookie> cookies = new ArrayList<HttpCookie>();

	/** 下次取输出流时故意失败，用来打 writeResult 的 IO 异常分支 */
	public TestServerResponse failOnOutputStream() {
		this.failOnOutputStream = true;
		return this;
	}

	/** 已经写回的响应体 */
	public String bodyText() {
		return new String(body.toByteArray(), StandardCharsets.UTF_8);
	}

	/** 重定向地址 */
	public String redirect() {
		return redirect;
	}

	/** 转发地址 */
	public String forward() {
		return forward;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String type) {
		this.contentType = type;
	}

	@Override
	public OutputStream getOutputStream() {
		if (failOnOutputStream) {
			return new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					throw new IOException("boom");
				}
			};
		}
		return body;
	}

	@Override
	public void addCookie(HttpCookie cookie) {
		cookies.add(cookie);
	}

	@Override
	public void setHeader(String name, String value) {
		List<String> values = new ArrayList<String>();
		values.add(value);
		headers.put(name, values);
	}

	@Override
	public void addHeader(String name, String value) {
		List<String> values = headers.get(name);
		if (values == null) {
			values = new ArrayList<String>();
			headers.put(name, values);
		}
		values.add(value);
	}

	@Override
	public String getHeader(String name) {
		List<String> values = headers.get(name);
		return values == null || values.isEmpty() ? null : values.get(0);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		List<String> values = headers.get(name);
		return values == null ? new ArrayList<String>() : values;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setStatus(int sc) {
		this.status = sc;
	}

	@Override
	public Object sendForward(String location) {
		this.forward = location;
		return location;
	}

	@Override
	public Object sendRedirect(String location) {
		this.redirect = location;
		this.status = 302;
		setHeader("Location", location);
		return location;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getRawResponse() {
		return (T) this;
	}

	@Override
	public void flush() throws IOException {
		body.flush();
	}

}

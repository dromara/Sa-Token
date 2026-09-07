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

import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 单测用的 LoveQQ 请求：把单测要用到的参数、头、Cookie、属性先备好。
 */
public class TestServerRequest implements ServerRequest {

	private String scheme = "http";
	private String host = "localhost";
	private Integer serverPort = 80;
	private String method = "GET";
	private String requestURI = "/";
	private String requestURL = "http://localhost/";
	private String contentType;
	private final Map<String, String> parameters = new LinkedHashMap<String, String>();
	private final Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();
	private final List<HttpCookie> cookies = new ArrayList<HttpCookie>();
	private final Map<String, Object> attributes = new LinkedHashMap<String, Object>();

	/** 指定 HTTP 方法 */
	public TestServerRequest method(String method) {
		this.method = method;
		return this;
	}

	/** 指定请求路径，同时补一份默认 URL */
	public TestServerRequest path(String path) {
		this.requestURI = path;
		this.requestURL = "http://localhost" + path;
		return this;
	}

	/** 指定完整 URL */
	public TestServerRequest url(String url) {
		this.requestURL = url;
		return this;
	}

	/** 指定 Host */
	public TestServerRequest host(String host) {
		this.host = host;
		return this;
	}

	/** 写入一个查询/表单参数 */
	public TestServerRequest param(String name, String value) {
		this.parameters.put(name, value);
		return this;
	}

	/** 写入一个请求头 */
	public TestServerRequest header(String name, String value) {
		List<String> values = this.headers.get(name);
		if (values == null) {
			values = new ArrayList<String>();
			this.headers.put(name, values);
		}
		values.add(value);
		return this;
	}

	/** 追加一个 Cookie */
	public TestServerRequest cookie(String name, String value) {
		this.cookies.add(new HttpCookie(name, value));
		return this;
	}

	@Override
	public String getScheme() {
		return scheme;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public Integer getServerPort() {
		return serverPort;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getRequestURL() {
		return requestURL;
	}

	@Override
	public String getRequestURI() {
		return requestURI;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(new byte[0]);
	}

	@Override
	public MultipartFile getMultipart(String name) {
		return null;
	}

	@Override
	public Collection<MultipartFile> getMultipart() {
		return Collections.emptyList();
	}

	@Override
	public String getParameter(String name) {
		return parameters.get(name);
	}

	@Override
	public Collection<String> getParameterNames() {
		return parameters.keySet();
	}

	@Override
	public Map<String, String> getParameterMap() {
		return parameters;
	}

	@Override
	public String getHeader(String name) {
		List<String> values = headers.get(name);
		return values == null || values.isEmpty() ? null : values.get(0);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		List<String> values = headers.get(name);
		return values == null ? Collections.<String>emptyList() : values;
	}

	@Override
	public Collection<String> getHeaderNames() {
		return headers.keySet();
	}

	@Override
	public HttpCookie getCookie(String name) {
		for (HttpCookie cookie : cookies) {
			if (name.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	@Override
	public HttpCookie[] getCookies() {
		return cookies.toArray(new HttpCookie[0]);
	}

	@Override
	public void setAttribute(String name, Object o) {
		attributes.put(name, o);
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public Map<String, Object> getAttributeMap() {
		return attributes;
	}

	@Override
	public InetSocketAddress getRemoteAddress() {
		return new InetSocketAddress("127.0.0.1", 0);
	}

	@Override
	public Locale getLocale() {
		return Locale.getDefault();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getRawRequest() {
		return (T) this;
	}

}

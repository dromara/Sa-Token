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
package cn.dev33.satoken.solon.testsupport;

import org.noear.solon.core.handle.ContextEmpty;

import java.net.URI;

/**
 * 单测用的 Solon Context：补上 ContextEmpty 里默认返回 null 的 method/path/url/uri。
 */
public class TestSolonContext extends ContextEmpty {

	private String method = "GET";
	private String path = "/";
	private String url = "http://localhost/";
	private URI uri = URI.create("http://localhost/");

	/** 指定 HTTP 方法 */
	public TestSolonContext method(String method) {
		this.method = method;
		return this;
	}

	/** 指定请求路径，同时写入 pathNew */
	public TestSolonContext path(String path) {
		this.path = path;
		pathNew(path);
		return this;
	}

	/** 指定完整 URL 和 URI */
	public TestSolonContext url(String url) {
		this.url = url;
		this.uri = URI.create(url);
		return this;
	}

	@Override
	public String method() {
		return method;
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public String url() {
		return url;
	}

	@Override
	public URI uri() {
		return uri;
	}

	/** ContextEmpty.redirect 是空实现，单测里要能看见 302 和 Location */
	@Override
	public void redirect(String url, int code) {
		status(code);
		headerSet("Location", url);
	}

}

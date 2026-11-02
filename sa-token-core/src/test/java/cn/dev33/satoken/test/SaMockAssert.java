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
package cn.dev33.satoken.test;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.model.SaCookie;
import org.junit.jupiter.api.Assertions;

/**
 * Mock 上下文上的链式断言：核对当前请求带了什么、响应写出了什么。
 * 不承载 {@code @Test}，给各用例当 DSL 用。
 *
 * @author click33
 * @since 1.46.0
 */
public final class SaMockAssert {

	private final SaRequestForMock request;
	private final SaResponseForMock response;

	private SaMockAssert(SaRequestForMock request, SaResponseForMock response) {
		this.request = request;
		this.response = response;
	}

	/** 包住当前 Mock 请求和响应，后续断言都打在这一对上 */
	public static SaMockAssert current() {
		return new SaMockAssert(
				(SaRequestForMock) SaHolder.getRequest(),
				(SaResponseForMock) SaHolder.getResponse());
	}

	/** 当前请求 method 应该等于 expected */
	public SaMockAssert assertMethodEquals(String expected) {
		Assertions.assertEquals(expected, request.getMethod());
		return this;
	}

	/** 当前请求里指定参数应该等于 expected */
	public SaMockAssert assertQueryEquals(String name, String expected) {
		Assertions.assertEquals(expected, request.getParam(name));
		return this;
	}

	/** 当前请求头应该等于 expected */
	public SaMockAssert assertRequestHeaderEquals(String name, String expected) {
		Assertions.assertEquals(expected, request.getHeader(name));
		return this;
	}

	/** 当前响应头应该等于 expected */
	public SaMockAssert assertHeaderEquals(String name, String expected) {
		Assertions.assertEquals(expected, response.headerMap.get(name));
		return this;
	}

	/** 当前请求 Cookie 应该等于 expected */
	public SaMockAssert assertRequestCookieEquals(String name, String expected) {
		Assertions.assertEquals(expected, request.getCookieValue(name));
		return this;
	}

	/** 响应 Set-Cookie 里指定名称的值应该等于 expected */
	public SaMockAssert assertCookieEquals(String name, String expected) {
		String setCookie = response.headerMap.get(SaCookie.HEADER_NAME);
		Assertions.assertNotNull(setCookie);
		Assertions.assertTrue(setCookie.startsWith(name + "=" + expected),
				() -> "Set-Cookie 应对上 " + name + "=" + expected + "，实际是：" + setCookie);
		return this;
	}

}

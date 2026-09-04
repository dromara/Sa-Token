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
package cn.dev33.satoken.core.context.mock;

import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.router.SaHttpMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * SaRequestForMock 及 SaRequest 默认方法测试
 */
public class SaRequestForMockTest {

	private SaRequestForMock request;

	@BeforeEach
	void setUp() {
		request = new SaRequestForMock();
		request.parameterMap.put("name", "zhangsan");
		request.parameterMap.put("empty", "");
		request.headerMap.put("X-Token", "abc");
		request.headerMap.put("X-Requested-With", "XMLHttpRequest");
		request.cookieMap.put("satoken", "token-value");
		request.requestPath = "/api/user";
		request.url = "http://localhost/api/user";
		request.method = "POST";
		request.host = "localhost";
	}

	/** 参数相关默认方法应正常工作 */
	@Test
	void defaultParamMethods() {
		Assertions.assertEquals("zhangsan", request.getParam("name"));
		Assertions.assertEquals("default", request.getParam("missing", "default"));
		Assertions.assertTrue(request.isParam("name", "zhangsan"));
		Assertions.assertFalse(request.isParam("name", "lisi"));
		Assertions.assertTrue(request.hasParam("name"));
		Assertions.assertFalse(request.hasParam("empty"));
		Assertions.assertEquals("zhangsan", request.getParamNotNull("name"));
	}

	/** 缺少必填参数时 getParamNotNull 应抛出异常 */
	@Test
	void getParamNotNull_throwsWhenMissing() {
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class, () -> request.getParamNotNull("missing"));
		Assertions.assertEquals(SaErrorCode.CODE_12001, ex.getCode());
	}

	/** Header 相关默认方法应正常工作 */
	@Test
	void defaultHeaderMethods() {
		Assertions.assertEquals("abc", request.getHeader("X-Token"));
		Assertions.assertEquals("fallback", request.getHeader("missing", "fallback"));
	}

	/** Cookie 相关方法应正常返回值 */
	@Test
	void cookieMethods() {
		Assertions.assertEquals("token-value", request.getCookieValue("satoken"));
		Assertions.assertEquals("token-value", request.getCookieFirstValue("satoken"));
		Assertions.assertEquals("token-value", request.getCookieLastValue("satoken"));
	}

	/** 路径、方法、URL 与 Host 默认值应正确 */
	@Test
	void pathAndMethodDefaults() {
		Assertions.assertEquals("/api/user", request.getRequestPath());
		Assertions.assertTrue(request.isPath("/api/user"));
		Assertions.assertEquals("POST", request.getMethod());
		Assertions.assertTrue(request.isMethod("POST"));
		Assertions.assertTrue(request.isMethod(SaHttpMethod.POST));
		Assertions.assertEquals("http://localhost/api/user", request.getUrl());
		Assertions.assertEquals("localhost", request.getHost());
	}

	/** isAjax 应根据 Header 或参数判断 Ajax 请求 */
	@Test
	void isAjax() {
		Assertions.assertTrue(request.isAjax());
		request.headerMap.remove("X-Requested-With");
		Assertions.assertFalse(request.isAjax());
		request.parameterMap.put("_ajax", "true");
		Assertions.assertTrue(request.isAjax());
	}

	/** forward 应记录转发目标路径 */
	@Test
	void forward() {
		request.forward("/login");
		Assertions.assertEquals("/login", request.forwardTo);
	}

	/** getParamNames/getParamMap 应返回参数集合 */
	@Test
	void getParamNamesAndMap() {
		Assertions.assertTrue(request.getParamNames().contains("name"));
		Assertions.assertEquals("zhangsan", request.getParamMap().get("name"));
		Assertions.assertNull(request.getSource());
	}

}

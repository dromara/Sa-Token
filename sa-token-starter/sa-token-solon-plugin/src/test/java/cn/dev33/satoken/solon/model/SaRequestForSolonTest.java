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
package cn.dev33.satoken.solon.model;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.solon.testsupport.SolonTestHelper;
import cn.dev33.satoken.solon.testsupport.TestSolonContext;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaRequestForSolon} 请求包装测试
 */
@SaTokenTest
public class SaRequestForSolonTest {

	/** 每个用例开始前挂上 Solon 策略 */
	@BeforeEach
	public void setUp() {
		SolonTestHelper.ensureSolonStrategy();
	}

	/** 无参构造应该读 Context.current() */
	@Test
	public void noArgConstructor_useCurrentContext() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/cur");
		SolonTestHelper.withCurrent(ctx, () -> {
			SaRequestForSolon saRequest = new SaRequestForSolon();
			Assertions.assertSame(ctx, saRequest.getSource());
			Assertions.assertEquals("/cur", saRequest.getRequestPath());
		});
	}

	/** 参数、请求头、方法、host 等基础读取应该正常 */
	@Test
	public void readBasicRequestInfo() {
		TestSolonContext ctx = new TestSolonContext()
				.method("POST")
				.path("/user/save")
				.url("http://example.com/user/save");
		ctx.paramMap().put("name", "zhang");
		ctx.headerMap().put("X-Token", "abc");

		SaRequestForSolon saRequest = new SaRequestForSolon(ctx);

		Assertions.assertSame(ctx, saRequest.getSource());
		Assertions.assertEquals("zhang", saRequest.getParam("name"));
		Assertions.assertEquals("abc", saRequest.getHeader("X-Token"));
		Assertions.assertEquals("POST", saRequest.getMethod());
		Assertions.assertEquals("example.com", saRequest.getHost());
		Assertions.assertEquals("/user/save", saRequest.getRequestPath());
	}

	/** getParamNames 和 getParamMap 应该返回请求里的全部参数 */
	@Test
	public void readParamNamesAndMap() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/");
		ctx.paramMap().put("a", "1");
		ctx.paramMap().put("b", "2");

		SaRequestForSolon saRequest = new SaRequestForSolon(ctx);

		Assertions.assertTrue(saRequest.getParamNames().contains("a"));
		Assertions.assertTrue(saRequest.getParamNames().contains("b"));
		Assertions.assertEquals("1", saRequest.getParamMap().get("a"));
		Assertions.assertEquals("2", saRequest.getParamMap().get("b"));
	}

	/** Cookie 默认值应该走最后一个同名 Cookie */
	@Test
	public void readCookieValues() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/");
		ctx.cookieMap().add("token", "first");
		ctx.cookieMap().add("token", "last");

		SaRequestForSolon saRequest = new SaRequestForSolon(ctx);

		Assertions.assertEquals("first", saRequest.getCookieFirstValue("token"));
		Assertions.assertEquals("last", saRequest.getCookieLastValue("token"));
		Assertions.assertEquals("last", saRequest.getCookieValue("token"));
	}

	/** 没有这个 Cookie 时读取应该返回 null */
	@Test
	public void readCookie_whenMissing_returnNull() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/");
		SaRequestForSolon saRequest = new SaRequestForSolon(ctx);

		Assertions.assertNull(saRequest.getCookieFirstValue("token"));
		Assertions.assertNull(saRequest.getCookieLastValue("token"));
	}

	/** 配置了 currDomain 时 getUrl 应该拼接域名和请求路径 */
	@Test
	public void getUrl_withCurrDomain_concatDomainAndPath() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api/user");
		SaManager.getConfig().setCurrDomain("https://sa-token.com");

		SaRequestForSolon saRequest = new SaRequestForSolon(ctx);

		Assertions.assertEquals("https://sa-token.com/api/user", saRequest.getUrl());
	}

	/** 未配置 currDomain 时 getUrl 应该直接返回原始请求 URL */
	@Test
	public void getUrl_withoutCurrDomain_useRequestUrl() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api/user");
		SaRequestForSolon saRequest = new SaRequestForSolon(ctx);

		Assertions.assertEquals("http://localhost/api/user", saRequest.getUrl());
	}

}

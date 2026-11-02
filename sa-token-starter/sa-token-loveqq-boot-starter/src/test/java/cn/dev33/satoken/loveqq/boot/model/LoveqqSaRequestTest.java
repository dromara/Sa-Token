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
package cn.dev33.satoken.loveqq.boot.model;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.application.ApplicationInfo;
import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerRequest;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerResponse;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link LoveqqSaRequest} 请求包装测试
 */
@SaTokenTest
public class LoveqqSaRequestTest {

	private String backupPrefix;

	/** 每个用例开始前挂上 LoveQQ 策略，并记住原来的路由前缀 */
	@BeforeEach
	public void setUp() {
		LoveqqTestHelper.ensureLoveqqStrategy();
		backupPrefix = ApplicationInfo.routePrefix;
	}

	/** 每个用例结束后把路由前缀还原 */
	@AfterEach
	public void tearDown() {
		ApplicationInfo.routePrefix = backupPrefix;
	}

	/** 参数、请求头、方法、host 等基础读取应该正常 */
	@Test
	public void readBasicRequestInfo() {
		TestServerRequest request = new TestServerRequest()
				.method("POST")
				.path("/user/save")
				.url("http://example.com/user/save")
				.host("example.com")
				.param("name", "zhang")
				.header("X-Token", "abc");

		LoveqqSaRequest saRequest = new LoveqqSaRequest(request);

		Assertions.assertSame(request, saRequest.getSource());
		Assertions.assertEquals("zhang", saRequest.getParam("name"));
		Assertions.assertEquals("abc", saRequest.getHeader("X-Token"));
		Assertions.assertEquals("POST", saRequest.getMethod());
		Assertions.assertEquals("example.com", saRequest.getHost());
		Assertions.assertEquals("/user/save", saRequest.getRequestPath());
	}

	/** getParamNames 和 getParamMap 应该返回请求里的全部参数 */
	@Test
	public void readParamNamesAndMap() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/").param("a", "1").param("b", "2");
		LoveqqSaRequest saRequest = new LoveqqSaRequest(request);

		Assertions.assertTrue(saRequest.getParamNames().contains("a"));
		Assertions.assertTrue(saRequest.getParamNames().contains("b"));
		Assertions.assertEquals("1", saRequest.getParamMap().get("a"));
		Assertions.assertEquals("2", saRequest.getParamMap().get("b"));
	}

	/** Cookie 默认值应该走最后一个同名 Cookie */
	@Test
	public void readCookieValues() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/")
				.cookie("token", "first")
				.cookie("token", "last");
		LoveqqSaRequest saRequest = new LoveqqSaRequest(request);

		Assertions.assertEquals("first", saRequest.getCookieFirstValue("token"));
		Assertions.assertEquals("last", saRequest.getCookieLastValue("token"));
		Assertions.assertEquals("first", saRequest.getCookieValue("token"));
	}

	/** 没有这个 Cookie 时读取应该返回 null */
	@Test
	public void readCookie_whenMissing_returnNull() {
		LoveqqSaRequest saRequest = new LoveqqSaRequest(LoveqqTestHelper.newGetRequest("/"));

		Assertions.assertNull(saRequest.getCookieFirstValue("token"));
		Assertions.assertNull(saRequest.getCookieLastValue("token"));
		Assertions.assertNull(saRequest.getCookieValue("token"));
	}

	/** cookies 数组为 null 时 first/last 也应该返回 null */
	@Test
	public void readCookie_whenCookiesNull_returnNull() {
		LoveqqSaRequest saRequest = new LoveqqSaRequest(new NullCookieRequest());

		Assertions.assertNull(saRequest.getCookieFirstValue("token"));
		Assertions.assertNull(saRequest.getCookieLastValue("token"));
	}

	/** 配置了 currDomain 时 getUrl 应该拼接域名和请求路径 */
	@Test
	public void getUrl_withCurrDomain_concatDomainAndPath() {
		SaManager.getConfig().setCurrDomain("https://sa-token.com");
		LoveqqSaRequest saRequest = new LoveqqSaRequest(LoveqqTestHelper.newGetRequest("/api/user"));

		Assertions.assertEquals("https://sa-token.com/api/user", saRequest.getUrl());
	}

	/** 未配置 currDomain 时 getUrl 应该直接返回原始请求 URL */
	@Test
	public void getUrl_withoutCurrDomain_useRequestUrl() {
		LoveqqSaRequest saRequest = new LoveqqSaRequest(LoveqqTestHelper.newGetRequest("/api/user"));

		Assertions.assertEquals("http://localhost/api/user", saRequest.getUrl());
	}

	/** forward 应该走 sendForward，而不是 sendRedirect */
	@Test
	public void forward_shouldSendForwardNotRedirect() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/fwd");
		TestServerResponse response = LoveqqTestHelper.newResponse();
		LoveqqTestHelper.withSaContext(request, response, () -> {
			Object result = new LoveqqSaRequest(request).forward("/open");
			Assertions.assertEquals("/open", result);
			Assertions.assertEquals("/open", response.forward());
			Assertions.assertNull(response.redirect());
		});
	}

	/** cookies 数组里夹着 null 时应该跳过，继续找同名 Cookie */
	@Test
	public void readCookie_skipNullCookieEntry() {
		LoveqqSaRequest saRequest = new LoveqqSaRequest(new SparseCookieRequest());

		Assertions.assertEquals("ok", saRequest.getCookieFirstValue("token"));
		Assertions.assertEquals("ok", saRequest.getCookieLastValue("token"));
	}

	/** 同数组里有别的名字时，不应该误把别的 Cookie 当成目标 */
	@Test
	public void readCookie_skipDifferentName() {
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/")
				.cookie("other", "nope")
				.cookie("token", "ok");
		LoveqqSaRequest saRequest = new LoveqqSaRequest(request);

		Assertions.assertEquals("ok", saRequest.getCookieFirstValue("token"));
		Assertions.assertEquals("ok", saRequest.getCookieLastValue("token"));
		Assertions.assertNull(saRequest.getCookieFirstValue("missing"));
	}

	/** getCookies 返回 null 的请求包装，用来打 Cookie 空数组分支 */
	static class NullCookieRequest extends TestServerRequest {
		@Override
		public java.net.HttpCookie[] getCookies() {
			return null;
		}
	}

	/** cookies 数组里夹着 null 的请求包装 */
	static class SparseCookieRequest extends TestServerRequest {
		@Override
		public java.net.HttpCookie[] getCookies() {
			return new java.net.HttpCookie[]{null, new java.net.HttpCookie("token", "ok")};
		}
	}

}

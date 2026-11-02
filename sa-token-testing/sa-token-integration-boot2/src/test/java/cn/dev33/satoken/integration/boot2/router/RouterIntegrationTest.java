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
package cn.dev33.satoken.integration.boot2.router;

import java.util.Arrays;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.integration.boot2.IntegrationBoot2Application;
import cn.dev33.satoken.integration.boot2.support.AbstractMockMvcIntegrationTest;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.util.SaResult;

/**
 * SaRouter 路由拦截集成测试（/rt/** 端点）。
 */
@SpringBootTest(classes = IntegrationBoot2Application.class)
public class RouterIntegrationTest extends AbstractMockMvcIntegrationTest {

	/** 基础 API 应该能正常调通 */
	@Test
	public void testApi() {
		SaRouterStaff staff = SaRouter.match(false);
		Assertions.assertFalse(staff.isHit());

		staff.reset();
		Assertions.assertTrue(staff.isHit());

		SaRouterStaff staff2 = SaRouter.match(r -> false);
		Assertions.assertFalse(staff2.isHit());

		Assertions.assertTrue(SaRouter.isMatch("/user/**", "/user/add"));
		Assertions.assertTrue(SaRouter.isMatch(new String[] {"/user/**", "/art/**", "/goods/**"}, "/art/delete"));
		Assertions.assertTrue(SaRouter.isMatch(Arrays.asList("/user/**", "/art/**", "/goods/**"), "/art/delete"));
		Assertions.assertTrue(SaRouter.isMatch(new String[] {"POST", "GET", "PUT"},  "GET"));

		Assertions.assertTrue(SaRouter.notMatch(false).isHit());
		Assertions.assertTrue(SaRouter.notMatch(r -> false).isHit());
	}

	/** 路由匹配命中和未命中时应该走到对应分支 */
	@Test
	public void testRouter() {
		Assertions.assertEquals(201, request("/rt/getInfo?name=zhang").getCode());
		Assertions.assertEquals(202, request("/rt/getInfo2").getCode());
		Assertions.assertEquals(203, request("/rt/getInfo3").getCode());
		Assertions.assertEquals(204, request("/rt/getInfo4").getCode());
		Assertions.assertEquals(205, request("/rt/getInfo5").getCode());
		Assertions.assertEquals(206, request("/rt/getInfo6").getCode());
		Assertions.assertEquals(200, request("/rt/getInfo7").getCode());
		Assertions.assertEquals(200, request("/rt/getInfo8").getCode());
		Assertions.assertEquals(209, request("/rt/getInfo9").getCode());
		Assertions.assertEquals(200, request("/rt/getInfo10").getCode());
		Assertions.assertEquals(211, request("/rt/getInfo11").getCode());
		Assertions.assertEquals(212, request("/rt/getInfo12").getCode());
		Assertions.assertEquals(213, request("/rt/getInfo13").getCode());
		Assertions.assertEquals(214, request("/rt/getInfo14").getCode());
		Assertions.assertEquals(215, request("/rt/getInfo15").getCode());
	}

	/** getUrl 应该能拿到当前请求路径，自定义域名时应该拼上去 */
	@Test
	public void testGetUrl() {
		Assertions.assertTrue(request("/rt/getInfo_101").getData().toString().endsWith("/rt/getInfo_101"));
		Assertions.assertTrue(request("/rt/getInfo_101?id=1").getData().toString().endsWith("/rt/getInfo_101"));

		SaManager.getConfig().setCurrDomain("http://xxx.com");
		Assertions.assertEquals("http://xxx.com/rt/getInfo_101", request("/rt/getInfo_101?id=1").getData().toString());
		SaManager.getConfig().setCurrDomain(null);
	}

	/** 从请求里读 Cookie 时应该能拿到对应值 */
	@Test
	public void testGetCookie() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.post("/rt/getInfo_102")
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.accept(MediaType.APPLICATION_PROBLEM_JSON)
				.cookie(new Cookie("x-token", "token-111"))
			)
			.andExpect(MockMvcResultMatchers.status().is(200))
			.andReturn();

		SaResult res = parseResult(mvcResult.getResponse().getContentAsString());
		Assertions.assertEquals("token-111", res.getData());
	}

	/** 重定向时应该返回 302 并带上 Location */
	@Test
	public void testRedirect() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.post("/rt/getInfo16")
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.accept(MediaType.APPLICATION_PROBLEM_JSON)
			)
			.andExpect(MockMvcResultMatchers.status().is(302))
			.andReturn();

		Assertions.assertEquals("/rt/getInfo3", mvcResult.getResponse().getHeader("Location"));
	}

	/** 登录后访问需登录接口时应该能通过 */
	@Test
	public void testGetInfo200() {
		String satoken = request("/rt/login?id=10001").get("token", String.class);
		Assertions.assertEquals(200, request("/rt/getInfo_202?satoken=" + satoken).getCode());
	}

	/** 请求转发时应该能转到目标接口 */
	@Test
	public void testForward() {
		Assertions.assertEquals(200, request("/rt/getInfo_103").getCode());
	}

}

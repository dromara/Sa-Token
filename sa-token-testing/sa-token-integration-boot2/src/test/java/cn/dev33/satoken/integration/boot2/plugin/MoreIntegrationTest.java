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
package cn.dev33.satoken.integration.boot2.plugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import cn.dev33.satoken.integration.boot2.IntegrationBoot2Application;
import cn.dev33.satoken.integration.boot2.support.AbstractMockMvcIntegrationTest;
import cn.dev33.satoken.util.SaResult;

/**
 * SaRequest API 与 Http Basic 认证集成测试。
 */
@SpringBootTest(classes = IntegrationBoot2Application.class)
public class MoreIntegrationTest extends AbstractMockMvcIntegrationTest {

	/** 基础 API 应该能正常调通 */
	@Test
	public void testApi() {
		Assertions.assertEquals(true, requestWithDivHeader("/more/getInfo?name=zhang").getData());
	}

	/** Http Basic 认证失败应该 401，带正确头时应该通过 */
	@Test
	public void testBasic() throws Exception {
		MvcResult unauthorized = mockMvc.perform(
				MockMvcRequestBuilders.post("/more/basicAuth")
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.accept(MediaType.APPLICATION_PROBLEM_JSON)
			)
			.andExpect(MockMvcResultMatchers.status().is(401))
			.andReturn();

		SaResult res = parseResult(unauthorized.getResponse().getContentAsString());
		Assertions.assertEquals(903, res.getCode());
		Assertions.assertEquals("Basic Realm=Sa-Token", unauthorized.getResponse().getHeader("WWW-Authenticate"));

		MvcResult authorized = mockMvc.perform(
				MockMvcRequestBuilders.post("/more/basicAuth")
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.accept(MediaType.APPLICATION_PROBLEM_JSON)
				.header("Authorization", "Basic c2E6MTIzNDU2")
			)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andReturn();

		Assertions.assertEquals(200, parseResult(authorized.getResponse().getContentAsString()).getCode());
	}

	/** 带 div 请求头发 POST */
	private SaResult requestWithDivHeader(String path) {
		try {
			MvcResult mvcResult = mockMvc.perform(
					MockMvcRequestBuilders.post(path)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.accept(MediaType.APPLICATION_PROBLEM_JSON)
					.header("div", "val")
				)
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andReturn();
			return parseResult(mvcResult.getResponse().getContentAsString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

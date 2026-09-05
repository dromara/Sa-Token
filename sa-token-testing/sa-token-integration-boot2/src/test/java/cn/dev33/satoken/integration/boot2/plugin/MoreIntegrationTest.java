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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.integration.boot2.support.AbstractMockMvcIntegrationTest;
import cn.dev33.satoken.integration.boot2.support.MockMvcSaResultClient;
import cn.dev33.satoken.integration.boot2.support.ServletPathContextSupport;
import cn.dev33.satoken.util.SaResult;

/**
 * SaRequest API 与 Http Basic 认证集成测试。
 */
public class MoreIntegrationTest extends AbstractMockMvcIntegrationTest {

	@BeforeEach
	@Override
	public void setUpMockMvc() {
		super.setUpMockMvc();
		ServletPathContextSupport.applyServletPathWorkaround();
	}

	// 基础API测试 
	@Test
	public void testApi() {
		SaResult res = requestWithDivHeader("/more/getInfo?name=zhang");
		Assertions.assertEquals(res.getData(), true);
	}

	// Http Basic 认证 
	@Test
	public void testBasic() throws Exception {
		
		// ---------------- 认证不通过
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.post("/more/basicAuth")
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.accept(MediaType.APPLICATION_PROBLEM_JSON)
			)
			.andExpect(MockMvcResultMatchers.status().is(401))
			.andReturn();
	
		// 转 SaResult 对象
		String content = mvcResult.getResponse().getContentAsString();
		SaResult res = MockMvcSaResultClient.parseBody(SaManager.getSaJsonTemplate(), content);
		Assertions.assertEquals(res.getCode(), 903);
		// 会有一个特殊响应头
		String header = mvcResult.getResponse().getHeader("WWW-Authenticate");
		Assertions.assertEquals(header, "Basic Realm=Sa-Token");
		
		
		// ---------------- 认证通过
    	MvcResult mvcResult2 = mockMvc.perform(
    				MockMvcRequestBuilders.post("/more/basicAuth")
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.accept(MediaType.APPLICATION_PROBLEM_JSON)
					.header("Authorization", "Basic c2E6MTIzNDU2")
    			)
    			.andExpect(MockMvcResultMatchers.status().isOk())
    			.andReturn();
    	
		// 转 Map 
		String content2 = mvcResult2.getResponse().getContentAsString();
		SaResult res2 = MockMvcSaResultClient.parseBody(SaManager.getSaJsonTemplate(), content2);
		Assertions.assertEquals(res2.getCode(), 200);
	}
	

    // 带 div 请求头的 POST 请求
    private SaResult requestWithDivHeader(String path) {
    	try {
    		// 发请求 
        	MvcResult mvcResult = mockMvc.perform(
        				MockMvcRequestBuilders.post(path)
    					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
    					.accept(MediaType.APPLICATION_PROBLEM_JSON)
    					.header("div", "val")
        			)
        			.andExpect(MockMvcResultMatchers.status().isOk())
        			.andReturn();

    		String content = mvcResult.getResponse().getContentAsString();
    		// 转 SaResult 对象
    		return MockMvcSaResultClient.parseBody(SaManager.getSaJsonTemplate(), content);
    		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
}

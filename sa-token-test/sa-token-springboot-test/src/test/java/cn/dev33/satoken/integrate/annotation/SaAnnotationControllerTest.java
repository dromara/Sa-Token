/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.integrate.annotation;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.integrate.StartUpApplication;
import cn.dev33.satoken.util.SaResult;

/**
 * 注解鉴权测试 
 * 
 * @author Auster 
 *
 */
@SpringBootTest(classes = StartUpApplication.class)
public class SaAnnotationControllerTest {

	@Autowired
	private WebApplicationContext wac;
	 
	private MockMvc mvc;
	
	// 每个方法前执行  
	@BeforeEach
    public void before() {
		mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
	
	// 校验通过的情况 
    @Test
    public void testPassing() {
    	// 登录拿到Token 
    	SaResult res = request("/at/login?id=10001");
    	String satoken = res.get("token", String.class);
    	Assertions.assertNotNull(satoken);
    	
    	// 登录校验，通过  
    	SaResult res2 = request("/at/checkLogin?satoken=" + satoken);
    	Assertions.assertEquals(res2.getCode(), 200);
    	
    	// 角色校验，通过  
    	SaResult res3 = request("/at/checkRole?satoken=" + satoken);
    	Assertions.assertEquals(res3.getCode(), 200);
    	
    	// 权限校验，通过  
    	SaResult res4 = request("/at/checkPermission?satoken=" + satoken);
    	Assertions.assertEquals(res4.getCode(), 200);
    	
    	// 权限校验or角色校验，通过  
    	SaResult res5 = request("/at/checkPermission2?satoken=" + satoken);
    	Assertions.assertEquals(res5.getCode(), 200);

    	// 开启二级认证 
    	SaResult res6 = request("/at/openSafe?satoken=" + satoken);
    	Assertions.assertEquals(res6.getCode(), 200);
    	
    	// 校验二级认证，通过
    	SaResult res7 = request("/at/checkSafe?satoken=" + satoken);
    	Assertions.assertEquals(res7.getCode(), 200);
    	
    	// 访问校验封禁的接口 ，通过
    	SaResult res9 = request("/at/checkDisable?satoken=" + satoken);
    	Assertions.assertEquals(res9.getCode(), 200);
    }

	// 校验不通过的情况 
    @Test
    public void testNotPassing() {
    	// 登录拿到Token 
    	SaResult res = request("/at/login?id=10002");
    	String satoken = res.get("token", String.class);
    	Assertions.assertNotNull(satoken);
    	
    	// 登录校验，不通过 
    	SaResult res2 = request("/at/checkLogin");
    	Assertions.assertEquals(res2.getCode(), 401);
    	
    	// 角色校验，不通过 
    	SaResult res3 = request("/at/checkRole?satoken=" + satoken);
    	Assertions.assertEquals(res3.getCode(), 402);
    	
    	// 权限校验，不通过  
    	SaResult res4 = request("/at/checkPermission?satoken=" + satoken);
    	Assertions.assertEquals(res4.getCode(), 403);
    	
    	// 权限校验or角色校验，不通过  
    	SaResult res5 = request("/at/checkPermission2?satoken=" + satoken);
    	Assertions.assertEquals(res5.getCode(), 403);
    	
    	// 校验二级认证，不通过
    	SaResult res7 = request("/at/checkSafe?satoken=" + satoken);
    	Assertions.assertEquals(res7.getCode(), 901);

    	// -------- 登录拿到Token 
    	String satoken10042 = request("/at/login?id=10042").get("token", String.class);
    	Assertions.assertNotNull(satoken10042);
    	
    	// 校验账号封禁 ，通过
    	SaResult res8 = request("/at/disable?id=10042");
    	Assertions.assertEquals(res8.getCode(), 200);
    	
    	// 访问校验封禁的接口 ，不通过
    	SaResult res9 = request("/at/checkDisable?satoken=" + satoken10042);
    	Assertions.assertEquals(res9.getCode(), 904);
    	
    	// 解封后就能访问了 
    	request("/at/untieDisable?id=10042");
    	SaResult res10 = request("/at/checkDisable?satoken=" + satoken10042);
    	Assertions.assertEquals(res10.getCode(), 200);
    }

	// 测试忽略认证 
    @Test
    public void testIgnore() {
    	// 必须登录才能访问的
    	SaResult res1 = request("/ig/show1");
    	Assertions.assertEquals(res1.getCode(), 401);
    	
    	// 不登录也可以访问的 
    	SaResult res2 = request("/ig/show2");
    	Assertions.assertEquals(res2.getCode(), 200);
    }

    // 封装请求 
    private SaResult request(String path) {
    	try {
    		// 发请求 
        	MvcResult mvcResult = mvc.perform(
        			MockMvcRequestBuilders.post(path)
    					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
    					.accept(MediaType.APPLICATION_PROBLEM_JSON)
        			)
        			.andExpect(MockMvcResultMatchers.status().isOk())
        			.andReturn();
        	
    		// 转 Map 
    		String content = mvcResult.getResponse().getContentAsString();
    		Map<String, Object> map = SaManager.getSaJsonTemplate().parseJsonToMap(content);
    		
    		// 转 SaResult 对象 
    		return new SaResult().setMap(map);
    		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
}

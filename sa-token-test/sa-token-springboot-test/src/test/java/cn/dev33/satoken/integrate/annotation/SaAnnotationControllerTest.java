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

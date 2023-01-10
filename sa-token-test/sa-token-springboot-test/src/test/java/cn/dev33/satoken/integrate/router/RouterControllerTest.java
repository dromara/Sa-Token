package cn.dev33.satoken.integrate.router;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.Cookie;

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
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.integrate.StartUpApplication;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.spring.SaTokenContextForSpring;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.util.SaResult;


/**
 * C Controller 测试 
 * 
 * @author kong
 *
 */
@SpringBootTest(classes = StartUpApplication.class)
public class RouterControllerTest {

	@Autowired
	private WebApplicationContext wac;
	 
	private MockMvc mvc;

	// 开始 
	@BeforeEach
    public void before() {
		mvc = MockMvcBuilders.webAppContextSetup(wac).build();
		
		// 在单元测试时，通过 request.getServletPath() 获取到的请求路径为空，导致路由拦截不正确 
		// 虽然不知道为什么会这样，但是暂时可以通过以下方式来解决 
		SaManager.setSaTokenContext(new SaTokenContextForSpring() {
			@Override
			public SaRequest getRequest() {
				return new SaRequestForServlet(SpringMVCUtil.getRequest()) {
					@Override
					public String getRequestPath() {
						return request.getRequestURI();
					}
				};
			}
		});
		
    }

	// 基础API测试 
	@Test
	public void testApi() {
		// 是否命中 
    	SaRouterStaff staff = SaRouter.match(false);
    	Assertions.assertFalse(staff.isHit());

    	// 重置 
    	staff.reset();
    	Assertions.assertTrue(staff.isHit());
    	
    	// lambda 形式 
    	SaRouterStaff staff2 = SaRouter.match(r -> false);
    	Assertions.assertFalse(staff2.isHit());
    	
    	// 匹配 
    	Assertions.assertTrue(SaRouter.isMatch("/user/**", "/user/add"));
    	Assertions.assertTrue(SaRouter.isMatch(new String[] {"/user/**", "/art/**", "/goods/**"}, "/art/delete"));
    	Assertions.assertTrue(SaRouter.isMatch(Arrays.asList("/user/**", "/art/**", "/goods/**"), "/art/delete"));
    	Assertions.assertTrue(SaRouter.isMatch(new String[] {"POST", "GET", "PUT"},  "GET"));
    	
    	// 不匹配的 
    	Assertions.assertTrue(SaRouter.notMatch(false).isHit());
    	Assertions.assertTrue(SaRouter.notMatch(r -> false).isHit());
	}
	
	// 各种路由测试 
	@Test
	public void testRouter() {
		// getInfo 
		SaResult res = request("/rt/getInfo?name=zhang");
		Assertions.assertEquals(res.getCode(), 201);
		
		// getInfo2 
		SaResult res2 = request("/rt/getInfo2");
		Assertions.assertEquals(res2.getCode(), 202);

		// getInfo3 
		SaResult res3 = request("/rt/getInfo3");
		Assertions.assertEquals(res3.getCode(), 203);

		// getInfo4 
		SaResult res4 = request("/rt/getInfo4");
		Assertions.assertEquals(res4.getCode(), 204);
		
		// getInfo5 
		SaResult res5 = request("/rt/getInfo5");
		Assertions.assertEquals(res5.getCode(), 205);
		
		// getInfo6 
		SaResult res6 = request("/rt/getInfo6");
		Assertions.assertEquals(res6.getCode(), 206);
		
		// getInfo7 
		SaResult res7 = request("/rt/getInfo7");
		Assertions.assertEquals(res7.getCode(), 200);
		
		// getInfo8 
		SaResult res8 = request("/rt/getInfo8");
		Assertions.assertEquals(res8.getCode(), 200);
		
		// getInfo9 
		SaResult res9 = request("/rt/getInfo9");
		Assertions.assertEquals(res9.getCode(), 209);
		
		// getInfo10 
		SaResult res10 = request("/rt/getInfo10");
		Assertions.assertEquals(res10.getCode(), 200);
		
		// getInfo11 
		SaResult res11 = request("/rt/getInfo11");
		Assertions.assertEquals(res11.getCode(), 211);
		
		// getInfo12
		SaResult res12 = request("/rt/getInfo12");
		Assertions.assertEquals(res12.getCode(), 212);
		
		// getInfo13
		SaResult res13 = request("/rt/getInfo13");
		Assertions.assertEquals(res13.getCode(), 213);
		
		// getInfo14
		SaResult res14 = request("/rt/getInfo14");
		Assertions.assertEquals(res14.getCode(), 214);
		
		// getInfo15
		SaResult res15 = request("/rt/getInfo15");
		Assertions.assertEquals(res15.getCode(), 215);
		
	}

	// 测试 getUrl() 
	@Test
	public void testGetUrl() {
		// getInfo_101 
		SaResult res = request("/rt/getInfo_101");
		Assertions.assertTrue(res.getData().toString().endsWith("/rt/getInfo_101"));
		
		// getInfo_101，不包括后面的参数 
		SaResult res2 = request("/rt/getInfo_101?id=1");
		Assertions.assertTrue(res2.getData().toString().endsWith("/rt/getInfo_101"));
		
		// 自定义当前域名 
		SaManager.getConfig().setCurrDomain("http://xxx.com");
		SaResult res3 = request("/rt/getInfo_101?id=1");
		Assertions.assertEquals(res3.getData().toString(), "http://xxx.com/rt/getInfo_101");
		SaManager.getConfig().setCurrDomain(null);
	}

	// 测试读取Cookie 
	@Test
	public void testGetCookie() throws Exception {
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post("/rt/getInfo_102")
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.accept(MediaType.APPLICATION_PROBLEM_JSON)
				.cookie(new Cookie("x-token", "token-111"))
			)
			.andExpect(MockMvcResultMatchers.status().is(200))
			.andReturn();
		
		// 转 Map 
		String content = mvcResult.getResponse().getContentAsString();
		Map<String, Object> map = SaManager.getSaJsonTemplate().parseJsonToMap(content);
		
		// 转 SaResult 对象 
		SaResult res = new SaResult().setMap(map);
		Assertions.assertEquals(res.getData(), "token-111");
	}
	
	// 测试重定向 
	@Test
	public void testRedirect() throws Exception {
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post("/rt/getInfo16")
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.accept(MediaType.APPLICATION_PROBLEM_JSON)
			)
			.andExpect(MockMvcResultMatchers.status().is(302))
			.andReturn();
	
		Assertions.assertEquals(mvcResult.getResponse().getHeader("Location"), "/rt/getInfo3");
	}

	// 空接口 
	@Test
	public void testGetInfo200() {
//		SaResult res = request("/rt/getInfo_200");
//		Assertions.assertEquals(res.getCode(), 200);
//		SaResult res1 = request("/rt/getInfo_201");
//		Assertions.assertEquals(res1.getCode(), 201);
//		SaResult res2 = request("/rt/getInfo_202");
//		Assertions.assertEquals(res2.getCode(), 401);
		
		// 登录拿到Token 
    	SaResult resLogin = request("/rt/login?id=10001");
    	String satoken = resLogin.get("token", String.class);
		SaResult res3 = request("/rt/getInfo_202?satoken=" + satoken);
		Assertions.assertEquals(res3.getCode(), 200);
	}

	// 测试转发 
	@Test
	public void testForward() {
		SaResult res = request("/rt/getInfo_103");
		Assertions.assertEquals(res.getCode(), 200);
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

package cn.dev33.satoken.integrate.more;

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
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.integrate.StartUpApplication;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.spring.SaTokenContextForSpring;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.util.SaResult;


/**
 * 其它测试 
 * 
 * @author click33
 *
 */
@SpringBootTest(classes = StartUpApplication.class)
public class MoreControllerTest {

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
		SaResult res = request("/more/getInfo?name=zhang");
		Assertions.assertEquals(res.getData(), true);
	}

	// Http Basic 认证 
	@Test
	public void testBasic() throws Exception {
		
		// ---------------- 认证不通过
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post("/more/basicAuth")
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.accept(MediaType.APPLICATION_PROBLEM_JSON)
			)
			.andExpect(MockMvcResultMatchers.status().is(401))
			.andReturn();
	
		// 转 Map 
		String content = mvcResult.getResponse().getContentAsString();
		Map<String, Object> map = SaManager.getSaJsonTemplate().parseJsonToMap(content);
		// 转 SaResult 对象 
		SaResult res = new SaResult().setMap(map);
		Assertions.assertEquals(res.getCode(), 903);
		// 会有一个特殊响应头
		String header = mvcResult.getResponse().getHeader("WWW-Authenticate");
		Assertions.assertEquals(header, "Basic Realm=Sa-Token");
		
		
		// ---------------- 认证通过
    	MvcResult mvcResult2 = mvc.perform(
    				MockMvcRequestBuilders.post("/more/basicAuth")
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.accept(MediaType.APPLICATION_PROBLEM_JSON)
					.header("Authorization", "Basic c2E6MTIzNDU2")
    			)
    			.andExpect(MockMvcResultMatchers.status().isOk())
    			.andReturn();
    	
		// 转 Map 
		String content2 = mvcResult2.getResponse().getContentAsString();
		Map<String, Object> map2 = SaManager.getSaJsonTemplate().parseJsonToMap(content2);
		// 转 SaResult 对象 
		SaResult res2 = new SaResult().setMap(map2);
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
    					.header("div", "val")
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

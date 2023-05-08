package cn.dev33.satoken.integrate.same;

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
import cn.dev33.satoken.exception.SameTokenInvalidException;
import cn.dev33.satoken.integrate.StartUpApplication;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * same-token Controller 测试 
 * 
 * @author click33
 *
 */
@SpringBootTest(classes = StartUpApplication.class)
public class SaSameTokenControllerTest {

	@Autowired
	private WebApplicationContext wac;
	 
	private MockMvc mvc;
	
	// 开始 
	@BeforeEach
    public void before() {
		mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
	
	// 获取信息 
	@Test
	public void testGetInfo() {
		String token = SaSameUtil.getToken();
		// 加token，能调通 
		SaResult res = request("/same/getInfo", token);
		Assertions.assertEquals(res.getCode(), 200);
		// 不加token，不能调通 
		SaResult res2 = request("/same/getInfo", "xxx");
		Assertions.assertEquals(res2.getCode(), 902);

		// 获取信息2  
		token = SaSameUtil.getTokenNh();
		// 加token，能调通 
		SaResult res3 = request("/same/getInfo2", token);
		Assertions.assertEquals(res3.getCode(), 200);
		// 不加token，不能调通 
		SaResult res4 = request("/same/getInfo2", "xxx");
		Assertions.assertEquals(res4.getCode(), 902);
	}

	// 基础测试 
	@Test
	public void testApi() {
		String token = SaSameUtil.getToken();
		
		// 刷新一下，会有变化 
		SaSameUtil.refreshToken();
		String token2 = SaSameUtil.getToken();
		Assertions.assertNotEquals(token, token2);
		
		// 旧token，变为次级token
		String pastToken = SaSameUtil.getPastTokenNh();
		Assertions.assertEquals(token, pastToken);
		
		// dao中应该有值 
		String daoToken = SaManager.getSaTokenDao().get("satoken:var:same-token");
		String daoToken2 = SaManager.getSaTokenDao().get("satoken:var:past-same-token");
		Assertions.assertEquals(token2, daoToken);
		Assertions.assertEquals(token, daoToken2);
		
		// 新旧都有效 
		Assertions.assertTrue(SaSameUtil.isValid(token));
		Assertions.assertTrue(SaSameUtil.isValid(token2));
		
		// 空的不行 
		Assertions.assertFalse(SaSameUtil.isValid(null));
		Assertions.assertFalse(SaSameUtil.isValid(""));
		
		// 不抛出异常 
		Assertions.assertDoesNotThrow(() -> SaSameUtil.checkToken(token));
		Assertions.assertDoesNotThrow(() -> SaSameUtil.checkToken(token2));
		
		// 抛出异常
		Assertions.assertThrows(SameTokenInvalidException.class, () -> SaSameUtil.checkToken(null));
		Assertions.assertThrows(SameTokenInvalidException.class, () -> SaSameUtil.checkToken(""));
		Assertions.assertThrows(SameTokenInvalidException.class, () -> SaSameUtil.checkToken("aaa"));
	}

	
	
    // 封装请求 
    private SaResult request(String path, String sameToken) {
    	try {
    		// 发请求 
        	MvcResult mvcResult = mvc.perform(
        				MockMvcRequestBuilders.post(path)
    					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
    					.accept(MediaType.APPLICATION_PROBLEM_JSON)
    					.header(SaSameUtil.SAME_TOKEN, sameToken)
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

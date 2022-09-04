package cn.dev33.satoken.integrate.id;

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
import cn.dev33.satoken.exception.IdTokenInvalidException;
import cn.dev33.satoken.id.SaIdUtil;
import cn.dev33.satoken.integrate.StartUpApplication;
import cn.dev33.satoken.util.SaResult;

/**
 * id-token Controller 测试 
 * 
 * @author kong
 *
 */
@SpringBootTest(classes = StartUpApplication.class)
public class SaIdTokenControllerTest {

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
		String token = SaIdUtil.getToken();
		// 加token，能调通 
		SaResult res = request("/id/getInfo", token);
		Assertions.assertEquals(res.getCode(), 200);
		// 不加token，不能调通 
		SaResult res2 = request("/id/getInfo", "xxx");
		Assertions.assertEquals(res2.getCode(), 902);

		// 获取信息2  
		token = SaIdUtil.getTokenNh();
		// 加token，能调通 
		SaResult res3 = request("/id/getInfo2", token);
		Assertions.assertEquals(res3.getCode(), 200);
		// 不加token，不能调通 
		SaResult res4 = request("/id/getInfo2", "xxx");
		Assertions.assertEquals(res4.getCode(), 902);
	}

	// 基础测试 
	@Test
	public void testApi() {
		String token = SaIdUtil.getToken();
		
		// 刷新一下，会有变化 
		SaIdUtil.refreshToken();
		String token2 = SaIdUtil.getToken();
		Assertions.assertNotEquals(token, token2);
		
		// 旧token，变为次级token
		String pastToken = SaIdUtil.getPastTokenNh();
		Assertions.assertEquals(token, pastToken);
		
		// dao中应该有值 
		String daoToken = SaManager.getSaTokenDao().get("satoken:var:id-token");
		String daoToken2 = SaManager.getSaTokenDao().get("satoken:var:past-id-token");
		Assertions.assertEquals(token2, daoToken);
		Assertions.assertEquals(token, daoToken2);
		
		// 新旧都有效 
		Assertions.assertTrue(SaIdUtil.isValid(token));
		Assertions.assertTrue(SaIdUtil.isValid(token2));
		
		// 空的不行 
		Assertions.assertFalse(SaIdUtil.isValid(null));
		Assertions.assertFalse(SaIdUtil.isValid(""));
		
		// 不抛出异常 
		Assertions.assertDoesNotThrow(() -> SaIdUtil.checkToken(token));
		Assertions.assertDoesNotThrow(() -> SaIdUtil.checkToken(token2));
		
		// 抛出异常
		Assertions.assertThrows(IdTokenInvalidException.class, () -> SaIdUtil.checkToken(null));
		Assertions.assertThrows(IdTokenInvalidException.class, () -> SaIdUtil.checkToken(""));
		Assertions.assertThrows(IdTokenInvalidException.class, () -> SaIdUtil.checkToken("aaa"));
	}

	
	
    // 封装请求 
    private SaResult request(String path, String idToken) {
    	try {
    		// 发请求 
        	MvcResult mvcResult = mvc.perform(
        				MockMvcRequestBuilders.post(path)
    					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
    					.accept(MediaType.APPLICATION_PROBLEM_JSON)
    					.header(SaIdUtil.ID_TOKEN, idToken)
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

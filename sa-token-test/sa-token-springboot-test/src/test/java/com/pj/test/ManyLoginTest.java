package com.pj.test;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.TokenSign;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

/**
 * Sa-Token 多端登录测试 
 * 
 * @author kong 
 *
 */
@SpringBootTest(classes = StartUpApplication.class)
public class ManyLoginTest {

	// 持久化Bean 
	@Autowired(required = false)
	SaTokenDao dao = SaManager.getSaTokenDao();
	
	// 开始 
	@BeforeAll
    public static void beforeClass() {
    	System.out.println("\n------------ 多端登录测试 star ...");
    }
	// 结束 
    @AfterAll
    public static void afterClass() {
//    	System.out.println("\n---------- 多端登录测试 end ... \n");
    }

    // 测试：并发登录、共享token、同端 
    @Test
    public void login() {
    	SaManager.setConfig(new SaTokenConfig());
    	
    	StpUtil.login(10001);
    	String token1 = StpUtil.getTokenValue();

    	StpUtil.login(10001);
    	String token2 = StpUtil.getTokenValue();
    	
    	Assertions.assertEquals(token1, token2);
    }

    // 测试：并发登录、共享token、不同端 
    @Test
    public void login2() {
    	SaManager.setConfig(new SaTokenConfig());
    	
    	StpUtil.login(10001, "APP");
    	String token1 = StpUtil.getTokenValue();

    	StpUtil.login(10001, "PC");
    	String token2 = StpUtil.getTokenValue();
    	
    	Assertions.assertNotEquals(token1, token2);
    }

    // 测试：并发登录、不共享token
    @Test
    public void login3() {
    	SaManager.setConfig(new SaTokenConfig().setIsShare(false));
    	
    	StpUtil.login(10001);
    	String token1 = StpUtil.getTokenValue();

    	StpUtil.login(10001);
    	String token2 = StpUtil.getTokenValue();
    	
    	Assertions.assertNotEquals(token1, token2);
    }

    // 测试：禁并发登录，后者顶出前者 
    @Test
    public void login4() {
    	SaManager.setConfig(new SaTokenConfig().setIsConcurrent(false));
    	
    	StpUtil.login(10001);
    	String token1 = StpUtil.getTokenValue();

    	StpUtil.login(10001);
    	String token2 = StpUtil.getTokenValue();
    	
    	// token不同 
    	Assertions.assertNotEquals(token1, token2);
    	
    	// token1会被标记为：已被顶下线 
    	Assertions.assertEquals(dao.get("satoken:login:token:" + token1), "-4");
    	
    	// User-Session里的 token1 签名会被移除 
    	List<TokenSign> tokenSignList = StpUtil.getSessionByLoginId(10001).getTokenSignList();
    	for (TokenSign tokenSign : tokenSignList) {
    		Assertions.assertNotEquals(tokenSign.getValue(), token1);
		}
    }
    
    // 测试：多端登录，一起强制注销 
    @Test
    public void login5() {
    	SaManager.setConfig(new SaTokenConfig());
    	
    	StpUtil.login(10001, "APP");
    	String token1 = StpUtil.getTokenValue();
    	
    	StpUtil.login(10001, "PC");
    	String token2 = StpUtil.getTokenValue();
    	
    	StpUtil.login(10001, "h5");
    	String token3 = StpUtil.getTokenValue();
    	
    	// 注销 
    	StpUtil.logout(10001);

    	// 三个Token应该全部无效 
    	Assertions.assertNull(dao.get("satoken:login:token:" + token1));
    	Assertions.assertNull(dao.get("satoken:login:token:" + token2));
    	Assertions.assertNull(dao.get("satoken:login:token:" + token3));
    	
    	// User-Session也应该被清除掉 
    	Assertions.assertNull(StpUtil.getSessionByLoginId(10001, false));
    	Assertions.assertNull(dao.getSession("satoken:login:session:" + 10001));
    }

    // 测试：多端登录，一起强制踢下线 
    @Test
    public void login6() {
    	SaManager.setConfig(new SaTokenConfig());
    	
    	StpUtil.login(10001, "APP");
    	String token1 = StpUtil.getTokenValue();
    	
    	StpUtil.login(10001, "PC");
    	String token2 = StpUtil.getTokenValue();
    	
    	StpUtil.login(10001, "h5");
    	String token3 = StpUtil.getTokenValue();
    	
    	// 注销 
    	StpUtil.kickout(10001);

    	// 三个Token应该全部无效 
    	Assertions.assertEquals(dao.get("satoken:login:token:" + token1), "-5");
    	Assertions.assertEquals(dao.get("satoken:login:token:" + token2), "-5");
    	Assertions.assertEquals(dao.get("satoken:login:token:" + token3), "-5");
    	
    	// User-Session也应该被清除掉 
    	Assertions.assertNull(StpUtil.getSessionByLoginId(10001, false));
    	Assertions.assertNull(dao.getSession("satoken:login:session:" + 10001));
    }

    // 测试：多账号模式，在一个账号体系里登录成功，在另一个账号体系不会校验通过 
    @Test
    public void login7() {
    	SaManager.setConfig(new SaTokenConfig());
    	
    	StpUtil.login(10001);
    	String token1 = StpUtil.getTokenValue();
    	
    	StpLogic stp = new StpLogic("user");
    	
    	Assertions.assertNotNull(StpUtil.getLoginIdByToken(token1));
    	Assertions.assertNull(stp.getLoginIdByToken(token1));
    }
}

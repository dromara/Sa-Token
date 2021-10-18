package com.pj.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.StpLogicJwtForTokenStyle;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;

/**
 * Sa-Token 整合 jwt：token-style 模式 测试
 * 
 * @author kong 
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartUpApplication.class)
public class JwtForTokenStyleTest {

	// 持久化Bean 
	static SaTokenDao dao;
	
	// 开始 
	@BeforeClass
    public static void beforeClass() {
    	System.out.println("\n\n------------------------ TokenStyleTest star ...");
    	dao = SaManager.getSaTokenDao();
    	StpUtil.setStpLogic(new StpLogicJwtForTokenStyle());
    }

	// 结束 
    @AfterClass
    public static void afterClass() {
    	System.out.println("\n\n------------------------ TokenStyleTest end ... \n");
    }

    // 测试：登录 
    @Test
    public void doLogin() {
    	// 登录
    	StpUtil.login(10001);
    	String token = StpUtil.getTokenValue();
    	
    	// API 验证 
    	Assert.assertTrue(StpUtil.isLogin());	
    	Assert.assertNotNull(token);	// token不为null
    	Assert.assertEquals(StpUtil.getLoginIdAsLong(), 10001);	// loginId=10001 
    	Assert.assertEquals(StpUtil.getLoginDevice(), SaTokenConsts.DEFAULT_LOGIN_DEVICE);	// 登录设备 

    	// token 验证 
    	JWT jwt = JWT.of(token);
    	JSONObject payloads = jwt.getPayloads();
    	Assert.assertEquals(payloads.getStr("loginId"), "10001");
    	
    	// db数据 验证  
    	// token存在 
    	Assert.assertEquals(dao.get("satoken:login:token:" + token), "10001");
    	// Session 存在 
    	SaSession session = dao.getSession("satoken:login:session:" + 10001);
    	Assert.assertNotNull(session);
    	Assert.assertEquals(session.getId(), "satoken:login:session:" + 10001);
    	Assert.assertTrue(session.getTokenSignList().size() >= 1);
    }
    
}

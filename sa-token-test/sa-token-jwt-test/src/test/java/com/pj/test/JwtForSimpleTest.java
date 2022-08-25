package com.pj.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;

/**
 * Sa-Token 整合 jwt：Simple 模式 测试
 * 
 * @author kong 
 *
 */
//@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartUpApplication.class)
public class JwtForSimpleTest {

	// 持久化Bean 
	static SaTokenDao dao;
	
	// 开始 
	@BeforeAll
    public static void beforeClass() {
    	System.out.println("\n\n------------------------ JwtForSimpleTest star ...");
    	dao = SaManager.getSaTokenDao();
    	StpUtil.setStpLogic(new StpLogicJwtForSimple());
    }

	// 结束 
    @AfterAll
    public static void afterClass() {
    	System.out.println("\n\n------------------------ JwtForSimpleTest end ... \n");
    }

    // 测试：登录 
    @Test
    public void doLogin() {
    	// 登录
    	StpUtil.login(10001);
    	String token = StpUtil.getTokenValue();
    	
    	// API 验证 
    	Assertions.assertTrue(StpUtil.isLogin());	
    	Assertions.assertNotNull(token);	// token不为null
    	Assertions.assertEquals(StpUtil.getLoginIdAsLong(), 10001);	// loginId=10001 
    	Assertions.assertEquals(StpUtil.getLoginDevice(), SaTokenConsts.DEFAULT_LOGIN_DEVICE);	// 登录设备类型

    	// token 验证 
    	JWT jwt = JWT.of(token);
    	JSONObject payloads = jwt.getPayloads();
    	Assertions.assertEquals(payloads.getStr("loginId"), "10001");
    	
    	// db数据 验证  
    	// token存在 
    	Assertions.assertEquals(dao.get("satoken:login:token:" + token), "10001");
    	// Session 存在 
    	SaSession session = dao.getSession("satoken:login:session:" + 10001);
    	Assertions.assertNotNull(session);
    	Assertions.assertEquals(session.getId(), "satoken:login:session:" + 10001);
    	Assertions.assertTrue(session.getTokenSignList().size() >= 1);
    }

    // 测试：getExtra 
    @Test
    public void getExtra() {
    	// 登录
    	StpUtil.login(10001, SaLoginConfig.setExtra("name", "zhangsan"));
    	String tokenValue = StpUtil.getTokenValue();
    	
    	// 可以取到
    	Assertions.assertEquals(StpUtil.getExtra("name"), "zhangsan");
    	Assertions.assertEquals(StpUtil.getExtra(tokenValue, "name"), "zhangsan");
    	// 取不到 
    	Assertions.assertEquals(StpUtil.getExtra("name2"), null);
    }
    
}

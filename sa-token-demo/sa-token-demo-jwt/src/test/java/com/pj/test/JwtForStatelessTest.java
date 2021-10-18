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
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.jwt.StpLogicJwtForStateless;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;

/**
 * Sa-Token 整合 jwt：stateless 模式 测试 
 * 
 * 
 * @author kong 
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartUpApplication.class)
public class JwtForStatelessTest {

	// 持久化Bean 
	static SaTokenDao dao;
	
	// 开始 
	@BeforeClass
    public static void beforeClass() {
    	System.out.println("\n\n------------------------ 基础测试 star ...");
    	dao = SaManager.getSaTokenDao();
    	StpUtil.setStpLogic(new StpLogicJwtForStateless());
    }

	// 结束 
    @AfterClass
    public static void afterClass() {
    	System.out.println("\n\n------------------------ 基础测试 end ... \n");
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
    	Assert.assertEquals(payloads.getStr(SaJwtUtil.LOGIN_ID), "10001"); // 账号 
    	Assert.assertEquals(payloads.getStr(SaJwtUtil.DEVICE), SaTokenConsts.DEFAULT_LOGIN_DEVICE);  // 登录设备 
    	Assert.assertEquals(payloads.getStr(SaJwtUtil.LOGIN_TYPE), StpUtil.TYPE);  // 账号类型 
    	
    	// 时间 
    	Assert.assertTrue(StpUtil.getTokenTimeout() <= SaManager.getConfig().getTimeout());
    	Assert.assertTrue(StpUtil.getTokenTimeout() > SaManager.getConfig().getTimeout() - 10000);
    	
    	try {
			// 尝试获取Session会抛出异常 
			StpUtil.getSession();
			Assert.assertTrue(false);
		} catch (Exception e) {
		}
    }
    
    // 测试：注销 
    @Test
    public void logout() {
    	// 登录
    	StpUtil.login(10001);
    	String token = StpUtil.getTokenValue();
    	Assert.assertEquals(JWT.of(token).getPayloads().getStr("loginId"), "10001");
    	
    	// 注销
    	StpUtil.logout();

    	// token 应该被清除 
    	Assert.assertNull(StpUtil.getTokenValue());
    	Assert.assertFalse(StpUtil.isLogin());
    }
    
    // 测试：Session会话 
    @Test(expected = SaTokenException.class)
    public void testSession() {
    	StpUtil.login(10001);
    	
    	// 会抛异常 
    	StpUtil.getSession();
    }
    
    // 测试：权限认证 
    @Test
    public void testCheckPermission() {
    	StpUtil.login(10001);
    	
    	// 权限认证 
    	Assert.assertTrue(StpUtil.hasPermission("user-add"));
    	Assert.assertTrue(StpUtil.hasPermission("user-list"));
    	Assert.assertTrue(StpUtil.hasPermission("user"));
    	Assert.assertTrue(StpUtil.hasPermission("art-add"));
    	Assert.assertFalse(StpUtil.hasPermission("get-user"));
    	// and
    	Assert.assertTrue(StpUtil.hasPermissionAnd("art-add", "art-get"));
    	Assert.assertFalse(StpUtil.hasPermissionAnd("art-add", "comment-add"));
    	// or 
    	Assert.assertTrue(StpUtil.hasPermissionOr("art-add", "comment-add"));
    	Assert.assertFalse(StpUtil.hasPermissionOr("comment-add", "comment-delete"));
    }

    // 测试：角色认证
    @Test
    public void testCheckRole() {
    	StpUtil.login(10001);
    	
    	// 角色认证 
    	Assert.assertTrue(StpUtil.hasRole("admin")); 
    	Assert.assertFalse(StpUtil.hasRole("teacher")); 
    	// and
    	Assert.assertTrue(StpUtil.hasRoleAnd("admin", "super-admin")); 
    	Assert.assertFalse(StpUtil.hasRoleAnd("admin", "ceo")); 
    	// or
    	Assert.assertTrue(StpUtil.hasRoleOr("admin", "ceo")); 
    	Assert.assertFalse(StpUtil.hasRoleOr("ceo", "cto")); 
    }
	
    // 测试：根据token强制注销 
    @Test(expected = SaTokenException.class)
    public void testLogoutByToken() {
    	
    	// 先登录上 
    	StpUtil.login(10001); 
    	Assert.assertTrue(StpUtil.isLogin());	
    	String token = StpUtil.getTokenValue();
    	
    	// 根据token注销 
    	StpUtil.logoutByTokenValue(token); 
    }

    // 测试：根据账号id强制注销 
    @Test(expected = SaTokenException.class)
    public void testLogoutByLoginId() {

    	// 先登录上 
    	StpUtil.login(10001); 
    	Assert.assertTrue(StpUtil.isLogin());	
    	
    	// 根据账号id注销 
    	StpUtil.logout(10001);
    }

}

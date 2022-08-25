package com.pj.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.jwt.StpLogicJwtForStateless;
import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;

/**
 * Sa-Token 整合 jwt：stateless 模式 测试 
 * 
 * @author kong 
 *
 */
@SpringBootTest(classes = StartUpApplication.class)
public class JwtForStatelessTest {

	// 持久化Bean 
	@Autowired(required = false)
	SaTokenDao dao = SaManager.getSaTokenDao();
	
	// 开始 
	@BeforeAll
    public static void beforeClass() {
    	System.out.println("\n\n------------------------ JwtForStatelessTest star ...");
    	StpUtil.setStpLogic(new StpLogicJwtForStateless());
    }

	// 结束 
    @AfterAll
    public static void afterClass() {
    	System.out.println("\n\n------------------------ JwtForStatelessTest end ... \n");
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
    	Assertions.assertEquals(payloads.getStr(SaJwtUtil.LOGIN_ID), "10001"); // 账号 
    	Assertions.assertEquals(payloads.getStr(SaJwtUtil.DEVICE), SaTokenConsts.DEFAULT_LOGIN_DEVICE);  // 登录设备类型 
    	Assertions.assertEquals(payloads.getStr(SaJwtUtil.LOGIN_TYPE), StpUtil.TYPE);  // 账号类型 
    	
    	// 时间 
    	Assertions.assertTrue(StpUtil.getTokenTimeout() <= SaManager.getConfig().getTimeout());
    	Assertions.assertTrue(StpUtil.getTokenTimeout() > SaManager.getConfig().getTimeout() - 10000);
    	
    	try {
			// 尝试获取Session会抛出异常 
			StpUtil.getSession();
			Assertions.assertTrue(false);
		} catch (Exception e) {
		}
    }
    
    // 测试：注销 
    @Test
    public void logout() {
    	// 登录
    	StpUtil.login(10001);
    	String token = StpUtil.getTokenValue();
    	Assertions.assertEquals(JWT.of(token).getPayloads().getStr("loginId"), "10001");
    	
    	// 注销
    	StpUtil.logout();

    	// token 应该被清除 
    	Assertions.assertNull(StpUtil.getTokenValue());
    	Assertions.assertFalse(StpUtil.isLogin());
    }
    
    // 测试：Session会话 
    @Test
    public void testSession() {
    	Assertions.assertThrows(ApiDisabledException.class, () -> {
        	StpUtil.login(10001);
        	
        	// 会抛异常 
        	StpUtil.getSession();
    	});
    }
    
    // 测试：权限认证 
    @Test
    public void testCheckPermission() {
    	StpUtil.login(10001);
    	
    	// 权限认证 
    	Assertions.assertTrue(StpUtil.hasPermission("user-add"));
    	Assertions.assertTrue(StpUtil.hasPermission("user-list"));
    	Assertions.assertTrue(StpUtil.hasPermission("user"));
    	Assertions.assertTrue(StpUtil.hasPermission("art-add"));
    	Assertions.assertFalse(StpUtil.hasPermission("get-user"));
    	// and
    	Assertions.assertTrue(StpUtil.hasPermissionAnd("art-add", "art-get"));
    	Assertions.assertFalse(StpUtil.hasPermissionAnd("art-add", "comment-add"));
    	// or 
    	Assertions.assertTrue(StpUtil.hasPermissionOr("art-add", "comment-add"));
    	Assertions.assertFalse(StpUtil.hasPermissionOr("comment-add", "comment-delete"));
    }

    // 测试：角色认证
    @Test
    public void testCheckRole() {
    	StpUtil.login(10001);
    	
    	// 角色认证 
    	Assertions.assertTrue(StpUtil.hasRole("admin")); 
    	Assertions.assertFalse(StpUtil.hasRole("teacher")); 
    	// and
    	Assertions.assertTrue(StpUtil.hasRoleAnd("admin", "super-admin")); 
    	Assertions.assertFalse(StpUtil.hasRoleAnd("admin", "ceo")); 
    	// or
    	Assertions.assertTrue(StpUtil.hasRoleOr("admin", "ceo")); 
    	Assertions.assertFalse(StpUtil.hasRoleOr("ceo", "cto")); 
    }
	
    // 测试：根据token强制注销 
    @Test
    public void testLogoutByToken() {
    	Assertions.assertThrows(ApiDisabledException.class, () -> {
        	// 先登录上 
        	StpUtil.login(10001); 
        	Assertions.assertTrue(StpUtil.isLogin());	
        	String token = StpUtil.getTokenValue();
        	
        	// 根据token注销 
        	StpUtil.logoutByTokenValue(token); 
    	});
    }

    // 测试：根据账号id强制注销 
    @Test
    public void testLogoutByLoginId() {
    	Assertions.assertThrows(ApiDisabledException.class, () -> {
        	// 先登录上 
        	StpUtil.login(10001); 
        	Assertions.assertTrue(StpUtil.isLogin());	
        	
        	// 根据账号id注销 
        	StpUtil.logout(10001);
    	});
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

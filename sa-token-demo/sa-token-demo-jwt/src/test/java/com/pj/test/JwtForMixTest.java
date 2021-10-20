package com.pj.test;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.DisableLoginException;
import cn.dev33.satoken.jwt.SaJwtUtil;
import cn.dev33.satoken.jwt.StpLogicJwtForMix;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;

/**
 * Sa-Token 整合 jwt：mix 模式 测试 
 * 
 * @author kong 
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StartUpApplication.class)
public class JwtForMixTest {

	// 持久化Bean 
	@Autowired(required = false)
	SaTokenDao dao = SaManager.getSaTokenDao();
	
	// 开始 
	@BeforeClass
    public static void beforeClass() {
    	System.out.println("\n\n------------------------ JwtForMixTest star ...");
    	StpUtil.setStpLogic(new StpLogicJwtForMix());
    }

	// 结束 
    @AfterClass
    public static void afterClass() {
    	System.out.println("\n\n------------------------ JwtForMixTest end ... \n");
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
    	
    	// db数据 验证  
    	// token不存在 
    	Assert.assertNull(dao.get("satoken:login:token:" + token));
    	// Session 存在 
    	SaSession session = dao.getSession("satoken:login:session:" + 10001);
    	Assert.assertNotNull(session);
    	Assert.assertEquals(session.getId(), "satoken:login:session:" + 10001);
    	Assert.assertTrue(session.getTokenSignList().size() >= 1);
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
    @Test
    public void testSession() {
    	StpUtil.login(10001);
    	
    	// API 应该可以获取 Session 
    	Assert.assertNotNull(StpUtil.getSession(false));
    	
    	// db中应该存在 Session
    	SaSession session = dao.getSession("satoken:login:session:" + 10001);
    	Assert.assertNotNull(session);
    	
    	// 存取值 
    	session.set("name", "zhang");
    	session.set("age", "18");
    	Assert.assertEquals(session.get("name"), "zhang");
    	Assert.assertEquals(session.getInt("age"), 18);
    	Assert.assertEquals((int)session.getModel("age", int.class), 18);
    	Assert.assertEquals((int)session.get("age", 20), 18);
    	Assert.assertEquals((int)session.get("name2", 20), 20);
    	Assert.assertEquals((int)session.get("name2", () -> 30), 30);
    	session.clear();
    	Assert.assertEquals(session.get("name"), null);
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
    @Test(expected = ApiDisabledException.class)
    public void testLogoutByToken() {
    	
    	// 先登录上 
    	StpUtil.login(10001); 
    	Assert.assertTrue(StpUtil.isLogin());	
    	String token = StpUtil.getTokenValue();
    	
    	// 根据token注销 
    	StpUtil.logoutByTokenValue(token); 
    }

    // 测试：根据账号id强制注销 
    @Test(expected = ApiDisabledException.class)
    public void testLogoutByLoginId() {

    	// 先登录上 
    	StpUtil.login(10001); 
    	Assert.assertTrue(StpUtil.isLogin());	
    	
    	// 根据账号id注销 
    	StpUtil.logout(10001);
    }

    // 测试Token-Session 
    @Test
    public void testTokenSession() {

    	// 先登录上 
    	StpUtil.login(10001); 
    	String token = StpUtil.getTokenValue();
    	
    	// 刚开始不存在 
    	Assert.assertNull(StpUtil.stpLogic.getTokenSession(false));
    	SaSession session = dao.getSession("satoken:login:token-session:" + token);
    	Assert.assertNull(session);
    	
    	// 调用一次就存在了 
    	StpUtil.getTokenSession();
    	Assert.assertNotNull(StpUtil.stpLogic.getTokenSession(false));
    	SaSession session2 = dao.getSession("satoken:login:token-session:" + token);
    	Assert.assertNotNull(session2);
    }
    
    // 测试：账号封禁 
    @Test(expected = DisableLoginException.class)
    public void testDisable() {
    	
    	// 封号 
    	StpUtil.disable(10007, 200);
    	Assert.assertTrue(StpUtil.isDisable(10007));
    	Assert.assertEquals(dao.get("satoken:login:disable:" + 10007), DisableLoginException.BE_VALUE); 
    	
    	// 解封  
    	StpUtil.untieDisable(10007);
    	Assert.assertFalse(StpUtil.isDisable(10007));
    	Assert.assertEquals(dao.get("satoken:login:disable:" + 10007), null); 
    	
    	// 封号后登陆 (会抛出 DisableLoginException 异常)
    	StpUtil.disable(10007, 200); 
    	StpUtil.login(10007);  
    }

    // 测试：身份切换 
    @Test
    public void testSwitch() {
    	// 登录
    	StpUtil.login(10001);
    	Assert.assertFalse(StpUtil.isSwitch());
    	Assert.assertEquals(StpUtil.getLoginIdAsLong(), 10001);
    	
    	// 开始身份切换 
    	StpUtil.switchTo(10044);
    	Assert.assertTrue(StpUtil.isSwitch());
    	Assert.assertEquals(StpUtil.getLoginIdAsLong(), 10044);
    	
    	// 结束切换 
    	StpUtil.endSwitch(); 
    	Assert.assertFalse(StpUtil.isSwitch());
    	Assert.assertEquals(StpUtil.getLoginIdAsLong(), 10001);
    }
    
    // 测试：会话管理
    @Test(expected = ApiDisabledException.class)
    public void testSearchTokenValue() {
    	// 登录
    	StpUtil.login(10001);
    	StpUtil.login(10002);
    	StpUtil.login(10003);
    	StpUtil.login(10004);
    	StpUtil.login(10005);
    	
    	// 查询 
    	List<String> list = StpUtil.searchTokenValue("", 0, 10);
    	Assert.assertTrue(list.size() >= 5);
    }
    
}

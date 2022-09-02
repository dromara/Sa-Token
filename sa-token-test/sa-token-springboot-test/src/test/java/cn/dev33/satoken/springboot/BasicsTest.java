package cn.dev33.satoken.springboot;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.DisableLoginException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaSessionCustomUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Sa-Token 基础API测试 
 * 
 * <p> 注解详解参考： https://www.cnblogs.com/flypig666/p/11505277.html
 * @author Auster 
 *
 */
@SpringBootTest(classes = StartUpApplication.class)
public class BasicsTest {

	// 持久化Bean 
	@Autowired(required = false)
	SaTokenDao dao = SaManager.getSaTokenDao();
	
	// 开始 
	@BeforeAll
    public static void beforeClass() {
    	System.out.println("\n\n------------------------ 基础测试 star ...");
    }

	// 结束 
    @AfterAll
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
    	Assertions.assertTrue(StpUtil.isLogin());	
    	Assertions.assertNotNull(token);	// token不为null
    	Assertions.assertEquals(StpUtil.getLoginIdAsLong(), 10001);	// loginId=10001 
    	Assertions.assertEquals(StpUtil.getLoginDevice(), SaTokenConsts.DEFAULT_LOGIN_DEVICE);	// 登录设备类型 
    	
    	// db数据 验证  
    	// token存在 
    	Assertions.assertEquals(dao.get("satoken:login:token:" + token), "10001");
    	// Session 存在 
    	SaSession session = dao.getSession("satoken:login:session:" + 10001);
    	Assertions.assertNotNull(session);
    	Assertions.assertEquals(session.getId(), "satoken:login:session:" + 10001);
    	Assertions.assertTrue(session.getTokenSignList().size() >= 1);
    }
    
    // 测试：注销 
    @Test
    public void logout() {
    	// 登录
    	StpUtil.login(10001);
    	String token = StpUtil.getTokenValue();
    	Assertions.assertEquals(dao.get("satoken:login:token:" + token), "10001");
    	
    	// 注销
    	StpUtil.logout();
    	// token 应该被清除
    	Assertions.assertNull(StpUtil.getTokenValue());
    	Assertions.assertFalse(StpUtil.isLogin());
    	Assertions.assertNull(dao.get("satoken:login:token:" + token));
    	// Session 应该被清除 
    	SaSession session = dao.getSession("satoken:login:session:" + 10001);
    	Assertions.assertNull(session);
    }
    
    // 测试：Session会话 
    @Test
    public void testSession() {
    	StpUtil.login(10001);
    	
    	// API 应该可以获取 Session 
    	Assertions.assertNotNull(StpUtil.getSession(false));
    	
    	// db中应该存在 Session
    	SaSession session = dao.getSession("satoken:login:session:" + 10001);
    	Assertions.assertNotNull(session);
    	
    	// 存取值 
    	session.set("name", "zhang");
    	session.set("age", "18");
    	Assertions.assertEquals(session.get("name"), "zhang");
    	Assertions.assertEquals(session.getInt("age"), 18);
    	Assertions.assertEquals((int)session.getModel("age", int.class), 18);
    	Assertions.assertEquals((int)session.get("age", 20), 18);
    	Assertions.assertEquals((int)session.get("name2", 20), 20);
    	Assertions.assertEquals((int)session.get("name2", () -> 30), 30);
    	session.clear();
    	Assertions.assertEquals(session.get("name"), null);
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
    	
    	// 先登录上 
    	StpUtil.login(10001); 
    	Assertions.assertTrue(StpUtil.isLogin());	
    	String token = StpUtil.getTokenValue();
    	
    	// 根据token注销 
    	StpUtil.logoutByTokenValue(token); 
    	Assertions.assertFalse(StpUtil.isLogin()); 
    	
    	// token 应该被清除
    	Assertions.assertNull(dao.get("satoken:login:token:" + token));
    	// Session 应该被清除 
    	SaSession session = dao.getSession("satoken:login:session:" + 10001);
    	Assertions.assertNull(session);

		// 场景值应该是token无效 
    	try {
    		StpUtil.checkLogin();
		} catch (NotLoginException e) {
			Assertions.assertEquals(e.getType(), NotLoginException.INVALID_TOKEN);
		}
    }

    // 测试：根据账号id强制注销 
    @Test
    public void testLogoutByLoginId() {

    	// 先登录上 
    	StpUtil.login(10001); 
    	Assertions.assertTrue(StpUtil.isLogin());	
    	String token = StpUtil.getTokenValue();
    	
    	// 根据账号id注销 
    	StpUtil.logout(10001);
    	Assertions.assertFalse(StpUtil.isLogin()); 
    	
    	// token 应该被清除
    	Assertions.assertNull(dao.get("satoken:login:token:" + token));
    	// Session 应该被清除 
    	SaSession session = dao.getSession("satoken:login:session:" + 10001);
    	Assertions.assertNull(session);

		// 场景值应该是token无效 
    	try {
    		StpUtil.checkLogin();
		} catch (NotLoginException e) {
			Assertions.assertEquals(e.getType(), NotLoginException.INVALID_TOKEN);
		}
    }

    // 测试Token-Session 
    @Test
    public void testTokenSession() {

    	// 先登录上 
    	StpUtil.login(10001); 
    	String token = StpUtil.getTokenValue();
    	
    	// 刚开始不存在 
    	Assertions.assertNull(StpUtil.stpLogic.getTokenSession(false));
    	SaSession session = dao.getSession("satoken:login:token-session:" + token);
    	Assertions.assertNull(session);
    	
    	// 调用一次就存在了 
    	StpUtil.getTokenSession();
    	Assertions.assertNotNull(StpUtil.stpLogic.getTokenSession(false));
    	SaSession session2 = dao.getSession("satoken:login:token-session:" + token);
    	Assertions.assertNotNull(session2);
    }
    
    // 测试自定义Session 
    @Test
    public void testCustomSession() {
    	// 刚开始不存在 
    	Assertions.assertFalse(SaSessionCustomUtil.isExists("art-1"));
    	SaSession session = dao.getSession("satoken:custom:session:" + "art-1");
    	Assertions.assertNull(session);
    	
    	// 调用一下 
    	SaSessionCustomUtil.getSessionById("art-1");
    	
    	// 就存在了 
    	Assertions.assertTrue(SaSessionCustomUtil.isExists("art-1"));
    	SaSession session2 = dao.getSession("satoken:custom:session:" + "art-1");
    	Assertions.assertNotNull(session2);
    	
    	// 给删除掉 
    	SaSessionCustomUtil.deleteSessionById("art-1");
    	
    	// 就又不存在了 
    	Assertions.assertFalse(SaSessionCustomUtil.isExists("art-1"));
    	SaSession session3 = dao.getSession("satoken:custom:session:" + "art-1");
    	Assertions.assertNull(session3);
    }
    
    // 测试：根据账号id踢人
    @Test
    public void kickoutByLoginId() {

    	// 踢人下线 
    	StpUtil.login(10001); 
    	String token = StpUtil.getTokenValue();
    	StpUtil.kickout(10001);
    	
    	// token 应该被打标记 
    	Assertions.assertEquals(dao.get("satoken:login:token:" + token), NotLoginException.KICK_OUT);

		// 场景值应该是token已被踢下线 
    	try {
    		StpUtil.checkLogin();
		} catch (NotLoginException e) {
			Assertions.assertEquals(e.getType(), NotLoginException.KICK_OUT);
		}
    }
    
    // 测试：账号封禁 
    @Test
    public void testDisable() {
		Assertions.assertThrows(DisableLoginException.class, () -> {
	    	// 封号 
	    	StpUtil.disable(10007, 200);
	    	Assertions.assertTrue(StpUtil.isDisable(10007));
	    	Assertions.assertEquals(dao.get("satoken:login:disable:" + 10007), DisableLoginException.BE_VALUE); 
	    	
	    	// 解封  
	    	StpUtil.untieDisable(10007);
	    	Assertions.assertFalse(StpUtil.isDisable(10007));
	    	Assertions.assertEquals(dao.get("satoken:login:disable:" + 10007), null); 
	    	
	    	// 封号后登陆 (会抛出 DisableLoginException 异常)
	    	StpUtil.disable(10007, 200); 
	    	StpUtil.login(10007);  
    	});
    }

    // 测试：身份切换 
    @Test
    public void testSwitch() {
    	// 登录
    	StpUtil.login(10001);
    	Assertions.assertFalse(StpUtil.isSwitch());
    	Assertions.assertEquals(StpUtil.getLoginIdAsLong(), 10001);
    	
    	// 开始身份切换 
    	StpUtil.switchTo(10044);
    	Assertions.assertTrue(StpUtil.isSwitch());
    	Assertions.assertEquals(StpUtil.getLoginIdAsLong(), 10044);
    	
    	// 结束切换 
    	StpUtil.endSwitch(); 
    	Assertions.assertFalse(StpUtil.isSwitch());
    	Assertions.assertEquals(StpUtil.getLoginIdAsLong(), 10001);
    }
    
    // 测试：会话管理
    @Test
    public void testSearchTokenValue() {
    	// 登录
    	StpUtil.login(10001);
    	StpUtil.login(10002);
    	StpUtil.login(10003);
    	StpUtil.login(10004);
    	StpUtil.login(10005);
    	
    	// 查询 
    	List<String> list = StpUtil.searchTokenValue("", 0, 10, true);
    	Assertions.assertTrue(list.size() >= 5);
    }
    
    // 测试：二级认证
    @Test
    public void testSafe() {
    	// 登录 
    	StpUtil.login(10001);
    	Assertions.assertFalse(StpUtil.isSafe());
    	
    	// 开启二级认证 
    	StpUtil.openSafe(2);
    	Assertions.assertTrue(StpUtil.isSafe()); 
    	Assertions.assertTrue(StpUtil.getSafeTime() > 0); 
    	
    	// 自然结束 
//    	Thread.sleep(2500);
//    	Assertions.assertFalse(StpUtil.isSafe());
    	
    	// 手动结束
//    	StpUtil.openSafe(2);
    	StpUtil.closeSafe();
    	Assertions.assertFalse(StpUtil.isSafe());
    }
    
}

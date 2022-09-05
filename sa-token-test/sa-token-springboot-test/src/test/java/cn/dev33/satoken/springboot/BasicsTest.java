package cn.dev33.satoken.springboot;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.DisableLoginException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.dev33.satoken.util.SoMap;

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
    	SaManager.getConfig().setActivityTimeout(180);
    }

	// 结束 
    @AfterAll
    public static void afterClass() {
    	System.out.println("\n\n------------------------ 基础测试 end ... \n");
    }

    // 测试：基础API
    @Test
    public void testBasicsApi() {
    	// 基本API 
    	Assertions.assertEquals(StpUtil.getLoginType(), "login");
    	Assertions.assertEquals(StpUtil.getStpLogic(), SaManager.getStpLogic("login"));
    	Assertions.assertEquals(StpUtil.getTokenName(), "satoken");
    	
    	// 安全的更新 StpUtil 的 StpLogic 对象 
    	StpLogic loginStpLogic = new StpLogic("login");
    	StpUtil.setStpLogic(loginStpLogic);
    	Assertions.assertEquals(StpUtil.getStpLogic(), loginStpLogic);
    	Assertions.assertEquals(SaManager.getStpLogic("login"), loginStpLogic);
    }
    
    // 测试：登录 
    @Test
    public void testDoLogin() {
    	// 登录
    	StpUtil.login(10001);
    	String token = StpUtil.getTokenValue();
    	
    	// token 存在 
    	Assertions.assertNotNull(token);
    	Assertions.assertEquals(token, StpUtil.getTokenValueNotCut());
    	Assertions.assertEquals(token, StpUtil.getTokenValueByLoginId(10001));
    	Assertions.assertEquals(token, StpUtil.getTokenValueByLoginId(10001, SaTokenConsts.DEFAULT_LOGIN_DEVICE));
    	
    	// token 队列 
    	List<String> tokenList = StpUtil.getTokenValueListByLoginId(10001);
    	List<String> tokenList2 = StpUtil.getTokenValueListByLoginId(10001, SaTokenConsts.DEFAULT_LOGIN_DEVICE);
    	Assertions.assertEquals(token, tokenList.get(tokenList.size() - 1));
    	Assertions.assertEquals(token, tokenList2.get(tokenList.size() - 1));
    	
    	// API 验证 
    	Assertions.assertTrue(StpUtil.isLogin());	
    	Assertions.assertDoesNotThrow(() -> StpUtil.checkLogin());
    	Assertions.assertNotNull(token);	// token不为null
    	Assertions.assertEquals(StpUtil.getLoginIdAsLong(), 10001);	// loginId=10001 
    	Assertions.assertEquals(StpUtil.getLoginIdAsInt(), 10001);	// loginId=10001 
    	Assertions.assertEquals(StpUtil.getLoginIdAsString(), "10001");	// loginId=10001 
    	Assertions.assertEquals(StpUtil.getLoginId(), "10001");	// loginId=10001 
    	Assertions.assertEquals(StpUtil.getLoginIdDefaultNull(), "10001");	// loginId=10001 
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
    public void testLogout() {
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
    	
    	// 全部客户端注销掉 
    	StpUtil.logout(10001);
    	// Session 应该被清除 
    	SaSession session = dao.getSession("satoken:login:session:" + 10001);
    	Assertions.assertNull(session);
    	
    	// 在调用 getLoginId() 就会抛出异常 
    	Assertions.assertEquals(StpUtil.getLoginId("无值"), "无值");
    	Assertions.assertThrows(NotLoginException.class, () -> StpUtil.getLoginId());
    }
    
    // 测试：Session会话 
    @Test
    public void testSession() {
    	StpUtil.login(10001);
    	
    	// API 应该可以获取 Session 
    	Assertions.assertNotNull(StpUtil.getSession());
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
    	
    	// 获取权限 
    	List<String> permissionList = StpUtil.getPermissionList();
    	List<String> permissionList2 = StpUtil.getPermissionList(10001);
    	Assertions.assertEquals(permissionList.size(), permissionList2.size());
    	
    	// 权限校验  
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
    	// more
    	Assertions.assertTrue(StpUtil.hasPermission(10001, "user-add"));
    	Assertions.assertFalse(StpUtil.hasPermission(10002, "user-add"));
    	
    	// 抛异常 
    	Assertions.assertThrows(NotPermissionException.class, () -> StpUtil.checkPermission("goods-add"));
    	Assertions.assertThrows(NotPermissionException.class, () -> StpUtil.checkPermissionAnd("goods-add", "art-add"));
    	// 不抛异常 
    	Assertions.assertDoesNotThrow(() -> StpUtil.checkPermission("user-add"));
    	Assertions.assertDoesNotThrow(() -> StpUtil.checkPermissionAnd("art-get", "art-add"));
    	Assertions.assertDoesNotThrow(() -> StpUtil.checkPermissionOr("goods-add", "art-add"));
    }

    // 测试：角色认证
    @Test
    public void testCheckRole() {
    	StpUtil.login(10001);
    	
    	// 获取角色 
    	List<String> roleList = StpUtil.getRoleList();
    	List<String> roleList2 = StpUtil.getRoleList(10001);
    	Assertions.assertEquals(roleList.size(), roleList2.size());
    	
    	// 角色校验  
    	Assertions.assertTrue(StpUtil.hasRole("admin")); 
    	Assertions.assertFalse(StpUtil.hasRole("teacher")); 
    	// and
    	Assertions.assertTrue(StpUtil.hasRoleAnd("admin", "super-admin")); 
    	Assertions.assertFalse(StpUtil.hasRoleAnd("admin", "ceo")); 
    	// or
    	Assertions.assertTrue(StpUtil.hasRoleOr("admin", "ceo")); 
    	Assertions.assertFalse(StpUtil.hasRoleOr("ceo", "cto")); 
    	// more
    	Assertions.assertTrue(StpUtil.hasRole(10001, "admin"));
    	Assertions.assertFalse(StpUtil.hasRole(10002, "admin2"));
    	
    	// 抛异常 
    	Assertions.assertThrows(NotRoleException.class, () -> StpUtil.checkRole("ceo"));
    	Assertions.assertThrows(NotRoleException.class, () -> StpUtil.checkRoleAnd("ceo", "admin"));
    	// 不抛异常 
    	Assertions.assertDoesNotThrow(() -> StpUtil.checkRole("admin"));
    	Assertions.assertDoesNotThrow(() -> StpUtil.checkRoleAnd("admin", "super-admin"));
    	Assertions.assertDoesNotThrow(() -> StpUtil.checkRoleOr("ceo", "admin"));
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

    	// 根据token踢下线 
    	StpUtil.login(10001); 
    	StpUtil.kickoutByTokenValue(StpUtil.getTokenValue());
    	
		// 场景值应该是被踢下线 
    	try {
    		StpUtil.checkLogin();
		} catch (NotLoginException e) {
			Assertions.assertEquals(e.getType(), NotLoginException.KICK_OUT);
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
    	
    	// 
    	SaSession tokenSession = StpUtil.getTokenSession();
    	SaSession tokenSession2 = StpUtil.getTokenSessionByToken(token);
    	Assertions.assertEquals(tokenSession.getId(), tokenSession2.getId());
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
    	// 封号 
    	StpUtil.disable(10007, 200);
    	Assertions.assertTrue(StpUtil.isDisable(10007));
    	Assertions.assertEquals(dao.get("satoken:login:disable:" + 10007), DisableLoginException.BE_VALUE); 
    	
    	// 封号时间 
    	long disableTime = StpUtil.getDisableTime(10007);
    	Assertions.assertTrue(disableTime <= 200 && disableTime >= 199);
    	
    	// 解封  
    	StpUtil.untieDisable(10007);
    	Assertions.assertFalse(StpUtil.isDisable(10007));
    	Assertions.assertEquals(dao.get("satoken:login:disable:" + 10007), null); 

    	// 封号后登陆 (会抛出 DisableLoginException 异常)
    	StpUtil.disable(10007, 200); 
		Assertions.assertThrows(DisableLoginException.class, () -> StpUtil.login(10007));
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

    	// 开始身份切换 Lambda 方式
    	StpUtil.switchTo(10045, () -> {
    		Assertions.assertTrue(StpUtil.isSwitch());
        	Assertions.assertEquals(StpUtil.getLoginIdAsLong(), 10045);
    	});
    	
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

    	// 查询 Token 列表 
    	List<String> list = StpUtil.searchTokenValue("", 0, 10, true);
    	Assertions.assertTrue(list.size() >= 5);
    	
    	// 查询 Session 列表 
    	List<String> list2 = StpUtil.searchSessionId("", 0, 10, true);
    	Assertions.assertTrue(list2.size() >= 5);
    	list2.stream().forEach(sessionId -> {
    		Assertions.assertNotNull(StpUtil.getSessionBySessionId(sessionId));
    	});
    }

    // 测试：会话管理(Token-Session)
    @Test
    public void testSearchTokenSession() {
    	// 登录
    	StpUtil.login(10001);
    	StpUtil.getTokenSession();
    	StpUtil.login(10002);
    	StpUtil.getTokenSession();
    	StpUtil.login(10003);
    	StpUtil.getTokenSession();
    	StpUtil.login(10004);
    	StpUtil.getTokenSession();
    	StpUtil.login(10005);
    	StpUtil.getTokenSession();

    	// 查询 Token-Session 列表 
    	List<String> list2 = StpUtil.searchTokenSessionId("", 0, 10, true);
    	Assertions.assertTrue(list2.size() >= 5);
    	list2.stream().forEach(sessionId -> {
    		Assertions.assertNotNull(StpUtil.getSessionBySessionId(sessionId));
    	});
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
    	StpUtil.checkSafe();
    	
    	// 自然结束 
//    	Thread.sleep(2500);
//    	Assertions.assertFalse(StpUtil.isSafe());
    	
    	// 手动结束
//    	StpUtil.openSafe(2);
    	StpUtil.closeSafe();
    	Assertions.assertFalse(StpUtil.isSafe());
    	
    	// 抛异常 
    	Assertions.assertThrows(NotSafeException.class, () -> StpUtil.checkSafe());
    }

    
    // ------------- 复杂点的 

    // 测试：指定设备登录 
    @Test
    public void testDoLoginByDevice() {
    	StpUtil.login(10001, "PC");
    	Assertions.assertEquals(StpUtil.getLoginDevice(), "PC");

    	// 指定一个其它的设备注销，应该注销不掉 
    	StpUtil.logout(10001, "APP");
    	Assertions.assertTrue(StpUtil.isLogin());
    	
    	// 指定当前设备踢掉，则能够踢掉 
    	StpUtil.kickout(10001, "PC");
    	Assertions.assertFalse(StpUtil.isLogin());
    	
    	// 顶掉
    	StpUtil.login(10001, "PC");
    	StpUtil.replaced(10001, "PC");
    	Assertions.assertFalse(StpUtil.isLogin());
    	try {
			StpUtil.checkLogin();
		} catch (NotLoginException e) {
			// 场景值应该为-4  
			Assertions.assertEquals(e.getType(), NotLoginException.BE_REPLACED);
		}
    }

    // 测试：指定 timeout 登录 
    @Test
    public void testDoLoginByTimeout() {
    	
    	// 指定timeout 登录 
    	StpUtil.login(10001, 100);
    	long timeout = StpUtil.getTokenTimeout();
    	Assertions.assertTrue(timeout <= 100 && timeout >= 99);
    	
    	// 续期一下
    	StpUtil.renewTimeout(200);
    	timeout = StpUtil.getTokenTimeout();
    	Assertions.assertTrue(timeout <= 200 && timeout >= 199);
    	
    	// 续期一下
    	StpUtil.renewTimeout(StpUtil.getTokenValue(), 300);
    	timeout = StpUtil.getTokenTimeout();
    	Assertions.assertTrue(timeout <= 300 && timeout >= 299);
    	
    	// Session 也会续期
    	timeout = StpUtil.getSessionTimeout();
    	Assertions.assertTrue(timeout >= 299);
    	
    	StpUtil.getTokenSession();
    	timeout = StpUtil.getTokenSessionTimeout();
    	Assertions.assertTrue(timeout >= 299);
    	
    	
    	// 注销后，就是-2
    	StpUtil.logout();
    	timeout = StpUtil.getTokenTimeout();
    	Assertions.assertTrue(timeout == SaTokenDao.NOT_VALUE_EXPIRE);
    }

    // 测试：预定 Token 登录 
    @Test
    public void testDoLoginBySetToken() {
    	// 预定 Token 登录 
    	StpUtil.login(10001, new SaLoginModel().setToken("qwer-qwer-qwer-qwer"));
    	Assertions.assertEquals(StpUtil.getTokenValue(), "qwer-qwer-qwer-qwer");

    	// 注销后，应该清除Token 
    	StpUtil.logout();
    	Assertions.assertNull(StpUtil.getTokenValue());
    }

    // 测试：无上下文注入的登录 
    @Test
    public void testCreateLoginSession() {
    	
    	// 无上下文注入的登录
    	StpUtil.createLoginSession(10001);
    	Assertions.assertNull(StpUtil.getTokenValue());

    	// 无上下文注入的登录
    	String token = StpUtil.createLoginSession(10001, new SaLoginModel());
    	Assertions.assertNull(StpUtil.getTokenValue());
    	
    	// 手动写入
    	StpUtil.setTokenValue(token);
    	Assertions.assertNotNull(StpUtil.getTokenValue());
    	
    	// 手动写入到Cookie 
    	StpUtil.setTokenValue(token, 10);
    	Assertions.assertNotNull(StpUtil.getTokenValue());
    }

    // 测试，匿名 Token-Session 
    @Test
    public void testAnonTokenSession() {
    	// token 不存在 
    	StpUtil.logout();
    	Assertions.assertNull(StpUtil.getTokenValue());
    	
    	// token 存在 
    	SaSession anonTokenSession = StpUtil.getAnonTokenSession();
    	String token = StpUtil.getTokenValue();
    	Assertions.assertNotNull(token);
    	// 写个值 
    	anonTokenSession.set("code", "123456");
    	
    	// 登录时，预定上 
    	StpUtil.login(10001, SaLoginConfig.setToken(token));
    	// token不变 
    	Assertions.assertEquals(token, StpUtil.getTokenValue());
    	
    	// Token-Session 存在，且不变 
    	SaSession tokenSession = StpUtil.getTokenSession();
    	Assertions.assertEquals(anonTokenSession.getId(), tokenSession.getId());
    	
    	// 刚才写的值，仍然在
    	Assertions.assertEquals(tokenSession.get("code"), "123456");
    }

    // 测试，临时过期 
    @Test
    public void testActivityTimeout() {
    	// 登录 
    	StpUtil.login(10001);
    	Assertions.assertNotNull(StpUtil.getTokenValue());
    	
    	// 默认跟随全局 timeout 
    	StpUtil.updateLastActivityToNow();
    	long activityTimeout = StpUtil.getTokenActivityTimeout();
    	Assertions.assertTrue(activityTimeout <=180 || activityTimeout >=179);
    	
    	// 不会抛出异常 
    	Assertions.assertDoesNotThrow(() -> StpUtil.checkActivityTimeout());
    }

    // 测试，上下文 API 
    @Test
    public void testSaTokenContext() {
    	SaTokenContext context = SaHolder.getContext();
    	Assertions.assertTrue(context.matchPath("/user/**", "/user/add"));
    }

    // 测试json转换 
    @Test
    public void testSaJsonTemplate() {
    	SaJsonTemplate saJsonTemplate = SaManager.getSaJsonTemplate();
    	
    	// map 转 json 
    	SoMap map = SoMap.getSoMap("name", "zhangsan");
    	String jsonString = saJsonTemplate.toJsonString(map);
    	Assertions.assertEquals(jsonString, "{\"name\":\"zhangsan\"}");
    	
    	// json 转 map 
    	Map<String, Object> map2 = saJsonTemplate.parseJsonToMap("{\"name\":\"zhangsan\"}");
    	Assertions.assertEquals(map2.get("name"), "zhangsan");
    }

    
}

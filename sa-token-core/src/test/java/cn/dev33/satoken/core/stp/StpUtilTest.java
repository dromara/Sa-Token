/*
 * Copyright 2020-2099 sa-token.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.core.stp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * StpUtil 门面 API 测试（含 setStpLogic 与主要委托方法）
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpUtilTest {

	/** 主测试账号 */
	private static final int USER_A = 10001;
	/** 第二个账号：用于切换身份、验证"别人的数据不受影响"等场景 */
	private static final int USER_B = 10002;
	/** 第三个账号 */
	private static final int USER_C = 10003;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	void setUp() {
		StpUtil.stpLogic = new StpLogic(StpUtil.TYPE);
		SaManager.putStpLogic(StpUtil.stpLogic);
		SaManager.setStpInterface(new StpInterface() {
			@Override
			public List<String> getPermissionList(Object loginId, String loginType) {
				return Arrays.asList("user:add", "user:view", "order:list");
			}

			@Override
			public List<String> getRoleList(Object loginId, String loginType) {
				return Arrays.asList("admin", "user");
			}
		});
	}

	/** setStpLogic 应注册到 SaManager 并发布事件 */
	@Test
	void setStpLogic_registersToSaManagerAndFiresEvent() {
		AtomicBoolean fired = new AtomicBoolean();
		SaTokenListenerForSimple listener = new SaTokenListenerForSimple() {
			@Override
			public void doSetStpLogic(StpLogic stpLogic) {
				fired.set(true);
			}
		};
		SaTokenEventCenter.registerListener(listener);

		StpLogic custom = new StpLogic("custom");
		StpUtil.setStpLogic(custom);

		Assertions.assertSame(custom, StpUtil.getStpLogic());
		Assertions.assertSame(custom, SaManager.getStpLogic("custom"));
		Assertions.assertEquals("custom", StpUtil.getLoginType());
		Assertions.assertTrue(fired.get());
	}

	/** TYPE 常量与 getLoginType 应一致 */
	@Test
	void type_and_getLoginType() {
		Assertions.assertEquals("login", StpUtil.TYPE);
		Assertions.assertEquals(StpUtil.TYPE, StpUtil.getLoginType());
	}

	/** 登录后 Token、Session、isLogin 及各 loginId 转换应全部正确 */
	@Test
	void login_setsTokenSessionAndLoginState() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(USER_A);
			String token = StpUtil.getTokenValue();

			Assertions.assertNotNull(token);
			Assertions.assertEquals(token, StpUtil.getTokenValueNotCut());
			Assertions.assertNotNull(StpUtil.getTokenInfo());
			Assertions.assertEquals(token, StpUtil.getTokenValueByLoginId(USER_A));
			Assertions.assertEquals(token,
					StpUtil.getTokenValueByLoginId(USER_A, SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE));

			List<String> tokenList = StpUtil.getTokenValueListByLoginId(USER_A);
			Assertions.assertEquals(token, tokenList.get(tokenList.size() - 1));
			Assertions.assertTrue(StpUtil.getTokenValueListByLoginId(USER_A, SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE).contains(token));

			Assertions.assertTrue(StpUtil.isLogin());
			Assertions.assertTrue(StpUtil.isLogin(USER_A));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkLogin());
			Assertions.assertEquals(String.valueOf(USER_A), StpUtil.getLoginId());
			Assertions.assertEquals(String.valueOf(USER_A), StpUtil.getLoginIdAsString());
			Assertions.assertEquals(USER_A, StpUtil.getLoginIdAsLong());
			Assertions.assertEquals(USER_A, StpUtil.getLoginIdAsInt());
			Assertions.assertEquals(String.valueOf(USER_A), StpUtil.getLoginIdDefaultNull());
			Assertions.assertEquals(SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE, StpUtil.getLoginDeviceType());
			Assertions.assertEquals(SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE, StpUtil.getLoginDevice());
			Assertions.assertNotNull(StpUtil.getLoginDeviceTypeByToken(token));
			Assertions.assertNotNull(StpUtil.getTerminalInfo());
			Assertions.assertNotNull(StpUtil.getTerminalInfoByToken(token));
			Assertions.assertFalse(StpUtil.getTerminalListByLoginId(USER_A).isEmpty());
			Assertions.assertFalse(StpUtil.getTerminalListByLoginId(USER_A, SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE).isEmpty());

			StpLogic logic = StpUtil.getStpLogic();
			SaTokenDao dao = SaManager.getSaTokenDao();
			Assertions.assertEquals(String.valueOf(USER_A), dao.get(logic.splicingKeyTokenValue(token)));
			SaSession session = dao.getSession(logic.splicingKeySession(USER_A));
			Assertions.assertNotNull(session);
		});
	}

	/** login 各重载与 createLoginSession / getOrCreateLoginSession 应可用 */
	@Test
	void login_overloads_andCreateSession() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(USER_A, "PC");
			Assertions.assertEquals("PC", StpUtil.getLoginDeviceType());
			StpUtil.logout();

			StpUtil.login(USER_A, true);
			Assertions.assertTrue(StpUtil.isLogin());
			StpUtil.logout();

			StpUtil.login(USER_A, 3600);
			Assertions.assertTrue(StpUtil.getTokenTimeout() > 0);
			StpUtil.logout();

			StpUtil.login(USER_A, new SaLoginParameter().setDeviceType("APP").setTimeout(7200));
			Assertions.assertEquals("APP", StpUtil.getLoginDeviceType());

			String token = StpUtil.createLoginSession(USER_B);
			Assertions.assertNotNull(token);
			String token2 = StpUtil.createLoginSession(USER_B, new SaLoginParameter().setDeviceType("PC"));
			Assertions.assertNotNull(token2);
			String token3 = StpUtil.getOrCreateLoginSession(USER_C);
			Assertions.assertNotNull(token3);
		});
	}

	/** setTokenValue 各重载应写入 Token */
	@Test
	void setTokenValue_overloads() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.setTokenValue("manual-token");
			Assertions.assertEquals("manual-token", StpUtil.getTokenValue());
			StpUtil.setTokenValue("manual-token-2", 3600);
			Assertions.assertEquals("manual-token-2", StpUtil.getTokenValue());
			StpUtil.setTokenValue("manual-token-3", new SaLoginParameter().setTimeout(1800));
			Assertions.assertEquals("manual-token-3", StpUtil.getTokenValue());
			StpUtil.setTokenValueToStorage("storage-token");
			Assertions.assertNotNull(StpUtil.getTokenName());
		});
	}

	/** 未登录时 isLogin 为 false 且 checkLogin 应抛出 NotLoginException */
	@Test
	void checkLogin_throwsWhenNotLoggedIn() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertFalse(StpUtil.isLogin());
			Assertions.assertNull(StpUtil.getLoginIdDefaultNull());
			Assertions.assertEquals("guest", StpUtil.getLoginId("guest"));
			Assertions.assertThrows(NotLoginException.class, () -> StpUtil.checkLogin());
		});
	}

	/** 权限与角色校验应通过 StpUtil 正确委托 */
	@Test
	void roleAndPermission_checks() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(USER_A);
			Assertions.assertTrue(StpUtil.hasRole("admin"));
			Assertions.assertTrue(StpUtil.hasRole(USER_A, "user"));
			Assertions.assertTrue(StpUtil.hasRoleAnd("admin"));
			Assertions.assertTrue(StpUtil.hasRoleOr("guest", "admin"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkRole("admin"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkRoleAnd("admin"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkRoleOr("guest", "admin"));
			Assertions.assertTrue(StpUtil.getRoleList().contains("admin"));
			Assertions.assertTrue(StpUtil.getRoleList(USER_A).contains("admin"));

			Assertions.assertTrue(StpUtil.hasPermission("user:add"));
			Assertions.assertTrue(StpUtil.hasPermission(USER_A, "user:view"));
			Assertions.assertTrue(StpUtil.hasPermissionAnd("user:add", "user:view"));
			Assertions.assertTrue(StpUtil.hasPermissionOr("user:delete", "order:list"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkPermission("user:add"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkPermissionAnd("user:add", "user:view"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkPermissionOr("user:delete", "order:list"));
			Assertions.assertTrue(StpUtil.getPermissionList().contains("user:add"));
			Assertions.assertTrue(StpUtil.getPermissionList(USER_A).contains("user:view"));
			Assertions.assertThrows(NotPermissionException.class, () -> StpUtil.checkPermission("user:delete"));
		});
	}

	/** Session、TokenSession 与活跃超时相关 API */
	@Test
	void session_andActiveTimeout() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(USER_A);
			String token = StpUtil.getTokenValue();
			Assertions.assertNotNull(StpUtil.getSession());
			Assertions.assertNotNull(StpUtil.getSession(true));
			Assertions.assertNotNull(StpUtil.getSessionByLoginId(USER_A));
			Assertions.assertNotNull(StpUtil.getSessionByLoginId(USER_A, true));
			Assertions.assertNotNull(StpUtil.getSessionBySessionId(StpUtil.getSession().getId()));
			Assertions.assertNotNull(StpUtil.getTokenSession());
			Assertions.assertNotNull(StpUtil.getTokenSessionByToken(token));
			Assertions.assertNotNull(StpUtil.getAnonTokenSession());
			Assertions.assertEquals(String.valueOf(USER_A), String.valueOf(StpUtil.getLoginIdByToken(token)));
			Assertions.assertEquals(String.valueOf(USER_A), String.valueOf(StpUtil.getLoginIdByTokenNotThinkFreeze(token)));
			StpUtil.updateLastActiveToNow();
			Assertions.assertTrue(StpUtil.getTokenLastActiveTime() >= 0
					|| StpUtil.getTokenLastActiveTime() == SaTokenDao.NOT_VALUE_EXPIRE);
			Assertions.assertTrue(StpUtil.getTokenTimeout() > 0);
			Assertions.assertTrue(StpUtil.getTokenTimeout(token) > 0);
			Assertions.assertTrue(StpUtil.getSessionTimeout() > 0);
			Assertions.assertTrue(StpUtil.getTokenSessionTimeout() > 0);
			Assertions.assertTrue(StpUtil.getTokenActiveTimeout() >= -2);
			StpUtil.renewTimeout(7200);
			StpUtil.renewTimeout(token, 3600);
			StpUtil.checkActiveTimeout();
		});
	}

	/** 封禁 / 解封：isDisable、getDisableTime、checkDisable 应随封禁状态变化 */
	@Test
	void disable_andUntie() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.disable(USER_A, 3600);
			Assertions.assertTrue(StpUtil.isDisable(USER_A));
			Assertions.assertTrue(StpUtil.getDisableTime(USER_A) > 0);
			Assertions.assertThrows(DisableServiceException.class, () -> StpUtil.checkDisable(USER_A));

			StpUtil.untieDisable(USER_A);
			Assertions.assertFalse(StpUtil.isDisable(USER_A));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkDisable(USER_A));

			StpUtil.disable(USER_A, "comment", 3600);
			Assertions.assertTrue(StpUtil.isDisable(USER_A, "comment"));
			Assertions.assertTrue(StpUtil.getDisableTime(USER_A, "comment") > 0);
			Assertions.assertThrows(DisableServiceException.class, () -> StpUtil.checkDisable(USER_A, "comment"));

			StpUtil.untieDisable(USER_A, "comment");
			Assertions.assertFalse(StpUtil.isDisable(USER_A, "comment"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkDisable(USER_A, "comment"));
		});
	}

	/** 阶梯封禁：达到封禁等级的校验应抛异常，高于封禁等级的应通过 */
	@Test
	void disableLevel_checksAgainstLevel() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.disableLevel(USER_A, 2, 3600);
			Assertions.assertTrue(StpUtil.isDisableLevel(USER_A, 2));
			Assertions.assertEquals(2, StpUtil.getDisableLevel(USER_A));
			Assertions.assertThrows(DisableServiceException.class, () -> StpUtil.checkDisableLevel(USER_A, 2));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkDisableLevel(USER_A, 3));

			StpUtil.disableLevel(USER_A, "shop", 3, 3600);
			Assertions.assertTrue(StpUtil.isDisableLevel(USER_A, "shop", 3));
			Assertions.assertEquals(3, StpUtil.getDisableLevel(USER_A, "shop"));
			Assertions.assertThrows(DisableServiceException.class, () -> StpUtil.checkDisableLevel(USER_A, "shop", 3));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkDisableLevel(USER_A, "shop", 7));
		});
	}

	/** 二级认证：openSafe 后 isSafe / checkSafe 通过，closeSafe 后恢复未认证 */
	@Test
	void safe_openAndClose() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(USER_A);
			String token = StpUtil.getTokenValue();

			StpUtil.openSafe(3600);
			Assertions.assertTrue(StpUtil.isSafe());
			Assertions.assertDoesNotThrow(() -> StpUtil.checkSafe());
			Assertions.assertTrue(StpUtil.getSafeTime() > 0);

			StpUtil.openSafe("pay", 3600);
			Assertions.assertTrue(StpUtil.isSafe("pay"));
			Assertions.assertTrue(StpUtil.isSafe(token, "pay"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkSafe("pay"));
			Assertions.assertTrue(StpUtil.getSafeTime("pay") > 0);

			StpUtil.closeSafe();
			Assertions.assertFalse(StpUtil.isSafe());
			StpUtil.closeSafe("pay");
			Assertions.assertFalse(StpUtil.isSafe("pay"));
		});
	}

	/** 身份切换：switchTo 期间 loginId 应变为目标账号，endSwitch 后恢复 */
	@Test
	void switchTo_andEndSwitch() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(USER_A);

			StpUtil.switchTo(USER_B);
			Assertions.assertTrue(StpUtil.isSwitch());
			Assertions.assertEquals(USER_B, StpUtil.getLoginIdDefaultNull());

			StpUtil.endSwitch();
			Assertions.assertFalse(StpUtil.isSwitch());
			Assertions.assertEquals(String.valueOf(USER_A), StpUtil.getLoginIdDefaultNull());

			StpUtil.switchTo(USER_C, () -> Assertions.assertEquals(USER_C, StpUtil.getLoginIdDefaultNull()));
			Assertions.assertFalse(StpUtil.isSwitch());
		});
	}

	/** 搜索 API 应能查到已登录账号的 Token / Account-Session / Token-Session，设备信任默认为 false */
	@Test
	void search_andTrustDevice() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(USER_A);
			String token = StpUtil.getTokenValue();
			StpUtil.getTokenSession();
			StpLogic logic = StpUtil.getStpLogic();

			Assertions.assertTrue(StpUtil.searchTokenValue("", 0, 10, false).contains(logic.splicingKeyTokenValue(token)));
			Assertions.assertTrue(StpUtil.searchSessionId("", 0, 10, false).contains(logic.splicingKeySession(USER_A)));
			Assertions.assertTrue(StpUtil.searchTokenSessionId("", 0, 10, false).contains(logic.splicingKeyTokenSession(token)));
			Assertions.assertFalse(StpUtil.isTrustDeviceId(USER_A, "device-1"));
		});
	}

	/** logout 各重载执行后，对应 Token 的映射应被删除、登录状态应消失 */
	@Test
	void logout_overloads_clearLoginState() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpLogic logic = StpUtil.getStpLogic();

			StpUtil.login(USER_A);
			String token = StpUtil.getTokenValue();
			StpUtil.logout(new SaLogoutParameter());
			Assertions.assertFalse(StpUtil.isLogin());
			Assertions.assertNull(logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			StpUtil.logout(USER_A);
			Assertions.assertNull(logic.getLoginIdNotHandle(token));
			Assertions.assertTrue(StpUtil.getTerminalListByLoginId(USER_A).isEmpty());

			token = StpUtil.createLoginSession(USER_A, new SaLoginParameter().setDeviceType("PC"));
			StpUtil.logout(USER_A, "PC");
			Assertions.assertNull(logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			StpUtil.logout(USER_A, new SaLogoutParameter());
			Assertions.assertNull(logic.getLoginIdNotHandle(token));
		});
	}

	/** logoutByTokenValue 各重载应删除该 Token 的映射 */
	@Test
	void logoutByTokenValue_overloads_clearToken() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpLogic logic = StpUtil.getStpLogic();

			String token = StpUtil.createLoginSession(USER_A);
			StpUtil.logoutByTokenValue(token);
			Assertions.assertNull(logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			StpUtil.logoutByTokenValue(token, new SaLogoutParameter());
			Assertions.assertNull(logic.getLoginIdNotHandle(token));
		});
	}

	/** kickout 各重载应把 Token 标记为 KICK_OUT，且用该 Token 再访问时 checkLogin 抛对应类型异常 */
	@Test
	void kickout_overloads_markTokenKickedOut() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpLogic logic = StpUtil.getStpLogic();

			String token = StpUtil.createLoginSession(USER_A);
			StpUtil.kickoutByTokenValue(token);
			Assertions.assertEquals(NotLoginException.KICK_OUT, logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			StpUtil.kickoutByTokenValue(token, new SaLogoutParameter());
			Assertions.assertEquals(NotLoginException.KICK_OUT, logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			StpUtil.kickout(USER_A);
			Assertions.assertEquals(NotLoginException.KICK_OUT, logic.getLoginIdNotHandle(token));
			Assertions.assertTrue(StpUtil.getTerminalListByLoginId(USER_A).isEmpty());

			token = StpUtil.createLoginSession(USER_A, new SaLoginParameter().setDeviceType("APP"));
			StpUtil.kickout(USER_A, "APP");
			Assertions.assertEquals(NotLoginException.KICK_OUT, logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			StpUtil.kickout(USER_A, new SaLogoutParameter());
			Assertions.assertEquals(NotLoginException.KICK_OUT, logic.getLoginIdNotHandle(token));

			StpUtil.login(USER_A);
			StpUtil.kickout(USER_A);
			NotLoginException ex = Assertions.assertThrows(NotLoginException.class, StpUtil::checkLogin);
			Assertions.assertEquals(NotLoginException.KICK_OUT, ex.getType());
		});
	}

	/** replaced 各重载应把 Token 标记为 BE_REPLACED，且用该 Token 再访问时 checkLogin 抛对应类型异常 */
	@Test
	void replaced_overloads_markTokenReplaced() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpLogic logic = StpUtil.getStpLogic();

			String token = StpUtil.createLoginSession(USER_A);
			StpUtil.replacedByTokenValue(token);
			Assertions.assertEquals(NotLoginException.BE_REPLACED, logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			StpUtil.replacedByTokenValue(token, new SaLogoutParameter());
			Assertions.assertEquals(NotLoginException.BE_REPLACED, logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			StpUtil.replaced(USER_A);
			Assertions.assertEquals(NotLoginException.BE_REPLACED, logic.getLoginIdNotHandle(token));
			Assertions.assertTrue(StpUtil.getTerminalListByLoginId(USER_A).isEmpty());

			token = StpUtil.createLoginSession(USER_A, new SaLoginParameter().setDeviceType("PC"));
			StpUtil.replaced(USER_A, "PC");
			Assertions.assertEquals(NotLoginException.BE_REPLACED, logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			StpUtil.replaced(USER_A, new SaLogoutParameter());
			Assertions.assertEquals(NotLoginException.BE_REPLACED, logic.getLoginIdNotHandle(token));

			StpUtil.login(USER_A);
			StpUtil.replaced(USER_A);
			NotLoginException ex = Assertions.assertThrows(NotLoginException.class, StpUtil::checkLogin);
			Assertions.assertEquals(NotLoginException.BE_REPLACED, ex.getType());
		});
	}

	/** removeTerminalByLogout / Kickout / Replaced 应从 Session 移除终端，并按各自语义处理 Token 映射 */
	@Test
	void removeTerminal_variants_updateSessionAndToken() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpLogic logic = StpUtil.getStpLogic();

			String token = StpUtil.createLoginSession(USER_A);
			SaSession session = StpUtil.getSessionByLoginId(USER_A);
			SaTerminalInfo terminal = session.getTerminalList().get(0);
			StpUtil.removeTerminalByLogout(session, terminal);
			Assertions.assertTrue(session.getTerminalList().isEmpty());
			Assertions.assertNull(logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			session = StpUtil.getSessionByLoginId(USER_A);
			terminal = session.getTerminalList().get(0);
			StpUtil.removeTerminalByKickout(session, terminal);
			Assertions.assertTrue(session.getTerminalList().isEmpty());
			Assertions.assertEquals(NotLoginException.KICK_OUT, logic.getLoginIdNotHandle(token));

			token = StpUtil.createLoginSession(USER_A);
			session = StpUtil.getSessionByLoginId(USER_A);
			terminal = session.getTerminalList().get(0);
			StpUtil.removeTerminalByReplaced(session, terminal);
			Assertions.assertTrue(session.getTerminalList().isEmpty());
			Assertions.assertEquals(NotLoginException.BE_REPLACED, logic.getLoginIdNotHandle(token));
		});
	}

	/** forEachTerminalList 应逐个回调该账号的终端，未登录账号不回调 */
	@Test
	void forEachTerminalList_iteratesTerminals() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.createLoginSession(USER_A, new SaLoginParameter().setDeviceType("PC"));
			StpUtil.createLoginSession(USER_A, new SaLoginParameter().setDeviceType("APP"));

			AtomicInteger count = new AtomicInteger();
			StpUtil.forEachTerminalList(USER_A, (s, t) -> count.incrementAndGet());
			Assertions.assertEquals(2, count.get());

			AtomicInteger none = new AtomicInteger();
			StpUtil.forEachTerminalList(USER_B, (s, t) -> none.incrementAndGet());
			Assertions.assertEquals(0, none.get());
		});
	}

	/** getExtra 在未集成 JWT 时应抛出 ApiDisabledException */
	@Test
	void getExtra_throwsWhenJwtNotEnabled() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(USER_A);
			Assertions.assertThrows(ApiDisabledException.class, () -> StpUtil.getExtra("k"));
			String token = StpUtil.getTokenValue();
			Assertions.assertThrows(ApiDisabledException.class, () -> StpUtil.getExtra(token, "k"));
		});
	}

	/** Header 携带 Token 时应能恢复登录状态 */
	@Test
	void headerToken_restoresLoginState() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String sessionToken = StpUtil.createLoginSession(USER_A);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put(StpUtil.getTokenName(), sessionToken);
			Assertions.assertEquals(sessionToken, StpUtil.getTokenValue());
			Assertions.assertTrue(StpUtil.isLogin());
		});
	}

}

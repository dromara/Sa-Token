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
			StpUtil.login(10001);
			String token = StpUtil.getTokenValue();

			Assertions.assertNotNull(token);
			Assertions.assertEquals(token, StpUtil.getTokenValueNotCut());
			Assertions.assertNotNull(StpUtil.getTokenInfo());
			Assertions.assertEquals(token, StpUtil.getTokenValueByLoginId(10001));
			Assertions.assertEquals(token,
					StpUtil.getTokenValueByLoginId(10001, SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE));

			List<String> tokenList = StpUtil.getTokenValueListByLoginId(10001);
			Assertions.assertEquals(token, tokenList.get(tokenList.size() - 1));
			Assertions.assertTrue(StpUtil.getTokenValueListByLoginId(10001, SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE).contains(token));

			Assertions.assertTrue(StpUtil.isLogin());
			Assertions.assertTrue(StpUtil.isLogin(10001));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkLogin());
			Assertions.assertEquals("10001", StpUtil.getLoginId());
			Assertions.assertEquals("10001", StpUtil.getLoginIdAsString());
			Assertions.assertEquals(10001L, StpUtil.getLoginIdAsLong());
			Assertions.assertEquals(10001, StpUtil.getLoginIdAsInt());
			Assertions.assertEquals("10001", StpUtil.getLoginIdDefaultNull());
			Assertions.assertEquals(SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE, StpUtil.getLoginDeviceType());
			Assertions.assertEquals(SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE, StpUtil.getLoginDevice());
			Assertions.assertNotNull(StpUtil.getLoginDeviceTypeByToken(token));
			Assertions.assertNotNull(StpUtil.getTerminalInfo());
			Assertions.assertNotNull(StpUtil.getTerminalInfoByToken(token));
			Assertions.assertFalse(StpUtil.getTerminalListByLoginId(10001).isEmpty());
			Assertions.assertFalse(StpUtil.getTerminalListByLoginId(10001, SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE).isEmpty());

			StpLogic logic = StpUtil.getStpLogic();
			SaTokenDao dao = SaManager.getSaTokenDao();
			Assertions.assertEquals("10001", dao.get(logic.splicingKeyTokenValue(token)));
			SaSession session = dao.getSession(logic.splicingKeySession(10001));
			Assertions.assertNotNull(session);
		});
	}

	/** login 各重载与 createLoginSession / getOrCreateLoginSession 应可用 */
	@Test
	void login_overloads_andCreateSession() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10002, "PC");
			Assertions.assertEquals("PC", StpUtil.getLoginDeviceType());
			StpUtil.logout();

			StpUtil.login(10003, true);
			Assertions.assertTrue(StpUtil.isLogin());
			StpUtil.logout();

			StpUtil.login(10004, 3600);
			Assertions.assertTrue(StpUtil.getTokenTimeout() > 0);
			StpUtil.logout();

			StpUtil.login(10005, new SaLoginParameter().setDeviceType("APP").setTimeout(7200));
			Assertions.assertEquals("APP", StpUtil.getLoginDeviceType());

			String token = StpUtil.createLoginSession(10006);
			Assertions.assertNotNull(token);
			String token2 = StpUtil.createLoginSession(10007, new SaLoginParameter().setDeviceType("PC"));
			Assertions.assertNotNull(token2);
			String token3 = StpUtil.getOrCreateLoginSession(10008);
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
			StpUtil.login(20001);
			Assertions.assertTrue(StpUtil.hasRole("admin"));
			Assertions.assertTrue(StpUtil.hasRole(20001, "user"));
			Assertions.assertTrue(StpUtil.hasRoleAnd("admin"));
			Assertions.assertTrue(StpUtil.hasRoleOr("guest", "admin"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkRole("admin"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkRoleAnd("admin"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkRoleOr("guest", "admin"));
			Assertions.assertTrue(StpUtil.getRoleList().contains("admin"));
			Assertions.assertTrue(StpUtil.getRoleList(20001).contains("admin"));

			Assertions.assertTrue(StpUtil.hasPermission("user:add"));
			Assertions.assertTrue(StpUtil.hasPermission(20001, "user:view"));
			Assertions.assertTrue(StpUtil.hasPermissionAnd("user:add", "user:view"));
			Assertions.assertTrue(StpUtil.hasPermissionOr("user:delete", "order:list"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkPermission("user:add"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkPermissionAnd("user:add", "user:view"));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkPermissionOr("user:delete", "order:list"));
			Assertions.assertTrue(StpUtil.getPermissionList().contains("user:add"));
			Assertions.assertTrue(StpUtil.getPermissionList(20001).contains("user:view"));
			Assertions.assertThrows(NotPermissionException.class, () -> StpUtil.checkPermission("user:delete"));
		});
	}

	/** Session、TokenSession 与活跃超时相关 API */
	@Test
	void session_andActiveTimeout() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(30001);
			String token = StpUtil.getTokenValue();
			Assertions.assertNotNull(StpUtil.getSession());
			Assertions.assertNotNull(StpUtil.getSession(true));
			Assertions.assertNotNull(StpUtil.getSessionByLoginId(30001));
			Assertions.assertNotNull(StpUtil.getSessionByLoginId(30001, true));
			Assertions.assertNotNull(StpUtil.getSessionBySessionId(StpUtil.getSession().getId()));
			Assertions.assertNotNull(StpUtil.getTokenSession());
			Assertions.assertNotNull(StpUtil.getTokenSessionByToken(token));
			Assertions.assertNotNull(StpUtil.getAnonTokenSession());
			Assertions.assertEquals("30001", String.valueOf(StpUtil.getLoginIdByToken(token)));
			Assertions.assertEquals("30001", String.valueOf(StpUtil.getLoginIdByTokenNotThinkFreeze(token)));
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

	/** 封禁、二级认证、身份切换与搜索 API */
	@Test
	void disable_safe_switch_search() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(40001);
			String token = StpUtil.getTokenValue();

			StpUtil.disable(40001, 3600);
			Assertions.assertTrue(StpUtil.isDisable(40001));
			Assertions.assertTrue(StpUtil.getDisableTime(40001) > 0);
			StpUtil.untieDisable(40001);
			Assertions.assertDoesNotThrow(() -> StpUtil.checkDisable(40001));
			StpUtil.disable(40001, "comment", 3600);
			Assertions.assertTrue(StpUtil.isDisable(40001, "comment"));
			Assertions.assertTrue(StpUtil.getDisableTime(40001, "comment") > 0);
			StpUtil.untieDisable(40001, "comment");
			Assertions.assertDoesNotThrow(() -> StpUtil.checkDisable(40001, "comment"));
			StpUtil.disableLevel(40001, 2, 3600);
			Assertions.assertTrue(StpUtil.isDisableLevel(40001, 2));
			Assertions.assertThrows(DisableServiceException.class, () -> StpUtil.checkDisableLevel(40001, 2));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkDisableLevel(40001, 3));
			Assertions.assertEquals(2, StpUtil.getDisableLevel(40001));
			StpUtil.disableLevel(40001, "shop", 3, 3600);
			Assertions.assertTrue(StpUtil.isDisableLevel(40001, "shop", 3));
			Assertions.assertThrows(DisableServiceException.class, () -> StpUtil.checkDisableLevel(40001, "shop", 3));
			Assertions.assertDoesNotThrow(() -> StpUtil.checkDisableLevel(40001, "shop", 7));
			Assertions.assertEquals(3, StpUtil.getDisableLevel(40001, "shop"));
			StpUtil.untieDisable(40001);
			StpUtil.untieDisable(40001, "comment");

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
			StpUtil.closeSafe("pay");

			StpUtil.switchTo(40002);
			Assertions.assertTrue(StpUtil.isSwitch());
			Assertions.assertEquals(40002, StpUtil.getLoginIdDefaultNull());
			StpUtil.endSwitch();
			Assertions.assertFalse(StpUtil.isSwitch());
			StpUtil.switchTo(40003, () -> Assertions.assertEquals(40003, StpUtil.getLoginIdDefaultNull()));

			Assertions.assertNotNull(StpUtil.searchTokenValue("", 0, 10, false));
			Assertions.assertNotNull(StpUtil.searchSessionId("", 0, 10, false));
			Assertions.assertNotNull(StpUtil.searchTokenSessionId("", 0, 10, false));
			Assertions.assertFalse(StpUtil.isTrustDeviceId(40001, "device-1"));
		});
	}

	/** logout / kickout / replaced 各重载应正确委托 */
	@Test
	void logout_kickout_replaced() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(50001);
			String token = StpUtil.getTokenValue();
			SaSession session = StpUtil.getSessionByLoginId(50001);
			SaTerminalInfo terminal = session.getTerminalList().get(0);

			StpUtil.logout(new SaLogoutParameter());
			StpUtil.login(50002);
			StpUtil.logout(50002);
			StpUtil.login(50003, "PC");
			StpUtil.logout(50003, "PC");
			StpUtil.login(50004);
			StpUtil.logout(50004, new SaLogoutParameter());

			String kickToken = StpUtil.createLoginSession(50005);
			StpUtil.kickoutByTokenValue(kickToken);
			StpUtil.kickoutByTokenValue(kickToken, new SaLogoutParameter());
			StpUtil.login(50006);
			StpUtil.kickout(50006);
			StpUtil.login(50007, "APP");
			StpUtil.kickout(50007, "APP");
			StpUtil.login(50008);
			StpUtil.kickout(50008, new SaLogoutParameter());

			String replacedToken = StpUtil.createLoginSession(50009);
			StpUtil.replacedByTokenValue(replacedToken);
			StpUtil.replacedByTokenValue(replacedToken, new SaLogoutParameter());
			StpUtil.login(50010);
			StpUtil.replaced(50010);
			StpUtil.login(50011, "PC");
			StpUtil.replaced(50011, "PC");
			StpUtil.login(50012);
			StpUtil.replaced(50012, new SaLogoutParameter());

			StpUtil.login(50013);
			session = StpUtil.getSessionByLoginId(50013);
			terminal = session.getTerminalList().get(0);
			StpUtil.removeTerminalByLogout(session, terminal);
			StpUtil.login(50014);
			session = StpUtil.getSessionByLoginId(50014);
			if (!session.getTerminalList().isEmpty()) {
				terminal = session.getTerminalList().get(0);
				StpUtil.removeTerminalByKickout(session, terminal);
			}
			StpUtil.login(50015);
			session = StpUtil.getSessionByLoginId(50015);
			if (!session.getTerminalList().isEmpty()) {
				terminal = session.getTerminalList().get(0);
				StpUtil.removeTerminalByReplaced(session, terminal);
			}

			StpUtil.login(50016);
			token = StpUtil.getTokenValue();
			StpUtil.logoutByTokenValue(token);
			StpUtil.login(50017);
			StpUtil.logoutByTokenValue(StpUtil.getTokenValue(), new SaLogoutParameter());

			AtomicInteger count = new AtomicInteger();
			StpUtil.forEachTerminalList(59999, (s, t) -> count.incrementAndGet());
		});
	}

	/** getExtra 在未集成 JWT 时应抛出 ApiDisabledException */
	@Test
	void getExtra_throwsWhenJwtNotEnabled() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(60001);
			Assertions.assertThrows(ApiDisabledException.class, () -> StpUtil.getExtra("k"));
			String token = StpUtil.getTokenValue();
			Assertions.assertThrows(ApiDisabledException.class, () -> StpUtil.getExtra(token, "k"));
		});
	}

	/** Header 携带 Token 时应能恢复登录状态 */
	@Test
	void headerToken_restoresLoginState() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String sessionToken = StpUtil.createLoginSession(60002);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put(StpUtil.getTokenName(), sessionToken);
			Assertions.assertEquals(sessionToken, StpUtil.getTokenValue());
			Assertions.assertTrue(StpUtil.isLogin());
		});
	}

}

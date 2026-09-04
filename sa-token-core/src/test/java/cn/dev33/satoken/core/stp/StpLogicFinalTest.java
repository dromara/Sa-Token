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
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * StpLogic 剩余覆盖率补充测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicFinalTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		SaManager.setStpInterface(new StpInterface() {
			@Override
			public List<String> getPermissionList(Object loginId, String loginType) {
				return Arrays.asList("user:add");
			}

			@Override
			public List<String> getRoleList(Object loginId, String loginType) {
				return Arrays.asList("admin");
			}
		});
		stpLogic = new StpLogic("login");
	}

	/** save/update/delete TokenToIdMapping 应正确维护 DAO 映射 */
	@Test
	void tokenToIdMapping_crud() {
		SaTokenDao dao = SaManager.getSaTokenDao();
		String token = "mapping-token-1";

		stpLogic.saveTokenToIdMapping(token, 90001, 3600);
		Assertions.assertEquals("90001", dao.get(stpLogic.splicingKeyTokenValue(token)));

		stpLogic.updateTokenToIdMapping(token, 90002);
		Assertions.assertEquals("90002", dao.get(stpLogic.splicingKeyTokenValue(token)));

		stpLogic.deleteTokenToIdMapping(token);
		Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(token)));
	}

	/** 空 loginId 更新 Token 映射应抛出 SaTokenException */
	@Test
	void updateTokenToIdMapping_emptyLoginId_throws() {
		Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.updateTokenToIdMapping("any-token", null));
	}

	/** removeTerminalByLogout/Kickout/Replaced 应按模式清除或标记 Token */
	@Test
	void removeTerminalByLogoutKickoutReplaced() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90003);
			String token = stpLogic.getTokenValue();
			SaSession session = stpLogic.getSessionByLoginId(90003);
			SaTerminalInfo terminal = session.getTerminal(token);
			SaTokenDao dao = SaManager.getSaTokenDao();

			stpLogic.getTokenSession();
			Assertions.assertNotNull(dao.getSession(stpLogic.splicingKeyTokenSession(token)));

			stpLogic.removeTerminalByLogout(session, terminal);
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(token)));

			stpLogic.login(90003);
			token = stpLogic.getTokenValue();
			session = stpLogic.getSessionByLoginId(90003);
			terminal = session.getTerminal(token);
			stpLogic.removeTerminalByKickout(session, terminal);
			Assertions.assertEquals(NotLoginException.KICK_OUT, dao.get(stpLogic.splicingKeyTokenValue(token)));

			stpLogic.login(90003);
			token = stpLogic.getTokenValue();
			session = stpLogic.getSessionByLoginId(90003);
			terminal = session.getTerminal(token);
			stpLogic.removeTerminalByReplaced(session, terminal);
			Assertions.assertEquals(NotLoginException.BE_REPLACED, dao.get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** isWriteHeader=true 时登录应将 Token 写入响应 Header */
	@Test
	void setTokenValueToResponseHeader_whenIsWriteHeaderTrue() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaLoginParameter param = new SaLoginParameter().setIsWriteHeader(true);
			stpLogic.login(90004, param);
			String token = stpLogic.getTokenValue();

			SaResponseForMock response = (SaResponseForMock) SaHolder.getResponse();
			Assertions.assertEquals(token, response.headerMap.get(stpLogic.getTokenName()));
			Assertions.assertEquals(stpLogic.getTokenName(),
					response.headerMap.get(SaResponse.ACCESS_CONTROL_EXPOSE_HEADERS));

			stpLogic.setTokenValueToResponseHeader("header-token");
			Assertions.assertEquals("header-token", response.headerMap.get(stpLogic.getTokenName()));
		});
	}

	/** renewTimeout(NEVER_EXPIRE) 后 getTokenTimeout 应返回 NEVER_EXPIRE */
	@Test
	void renewTimeout_neverExpire_rewritesCookieWithIntMax() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90005, 100);
			stpLogic.renewTimeout(SaTokenDao.NEVER_EXPIRE);
			Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, stpLogic.getTokenTimeout());
		});
	}

	/** isLogoutKeepTokenSession=true 时 logout 应保留 Token-Session */
	@Test
	void logout_keepTokenSession_whenConfigEnabled() {
		SaManager.getConfig().setIsLogoutKeepTokenSession(true);
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90006);
			String token = stpLogic.getTokenValue();
			stpLogic.getTokenSession();
			SaTokenDao dao = SaManager.getSaTokenDao();
			String tokenSessionKey = stpLogic.splicingKeyTokenSession(token);
			Assertions.assertNotNull(dao.getSession(tokenSessionKey));

			stpLogic.logout(new SaLogoutParameter().setMode(SaLogoutMode.LOGOUT));
			Assertions.assertNotNull(dao.getSession(tokenSessionKey));
		});
	}

	/** 关闭 tokenSessionCheckLogin 时无效 Token 也可创建 Token-Session */
	@Test
	void getTokenSession_whenTokenSessionCheckLoginFalse() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenSessionCheckLogin(false);
		String invalidToken = "invalid-token-for-session";
		SaSession session = stpLogic.getTokenSessionByToken(invalidToken, true);
		Assertions.assertNotNull(session);
		Assertions.assertEquals(stpLogic.splicingKeyTokenSession(invalidToken), session.getId());
	}

	/** deleteTokenSession 应从 DAO 删除指定 Token 的 Session */
	@Test
	void deleteTokenSession() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90007);
			String token = stpLogic.getTokenValue();
			stpLogic.getTokenSession();
			stpLogic.deleteTokenSession(token);
			Assertions.assertNull(stpLogic.getTokenSessionByToken(token, false));
		});
	}

	/** 未登录时 getSessionTimeout 应返回 NOT_VALUE_EXPIRE */
	@Test
	void getSessionTimeout_whenNotLogin() {
		SaTokenContextMockUtil.setMockContext(() -> {
			long timeout = stpLogic.getSessionTimeout();
			Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, timeout);
		});
	}

	/** 未登录时 hasRole/hasPermission 及各 And/Or 变体应返回 false */
	@Test
	void hasRoleAndPermission_whenNotLogin_returnsFalse() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertFalse(stpLogic.hasRole("admin"));
			Assertions.assertFalse(stpLogic.hasPermission("user:add"));
			Assertions.assertFalse(stpLogic.hasRoleAnd("admin"));
			Assertions.assertFalse(stpLogic.hasRoleOr("admin"));
			Assertions.assertFalse(stpLogic.hasPermissionAnd("user:add"));
			Assertions.assertFalse(stpLogic.hasPermissionOr("user:add"));
		});
	}

	/** 带前缀关键字 searchTokenValue 应返回匹配的 Token 键 */
	@Test
	void searchTokenValue_withKeywordPrefix() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90008);
			stpLogic.login(90009);
			String prefix = stpLogic.splicingKeyTokenValue("").substring(0, 8);
			List<String> tokens = stpLogic.searchTokenValue(prefix, 0, 10, true);
			Assertions.assertFalse(tokens.isEmpty());
			tokens.forEach(tokenKey -> Assertions.assertTrue(tokenKey.startsWith(prefix)));
		});
	}

	/** 指定 service 的二级认证 openSafe/checkSafe/closeSafe 全流程应正常 */
	@Test
	void openSafeAndCheckSafe_withService() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90010);
			Assertions.assertFalse(stpLogic.isSafe("pay"));

			stpLogic.openSafe("pay", 60);
			Assertions.assertTrue(stpLogic.isSafe("pay"));
			Assertions.assertDoesNotThrow(() -> stpLogic.checkSafe("pay"));
			Assertions.assertTrue(stpLogic.getSafeTime("pay") > 0);

			stpLogic.closeSafe("pay");
			Assertions.assertFalse(stpLogic.isSafe("pay"));
			Assertions.assertThrows(NotSafeException.class, () -> stpLogic.checkSafe("pay"));
		});
	}

	/** 动态 activeTimeout 下 getTokenUseActiveTimeout 应返回登录参数值 */
	@Test
	void getTokenUseActiveTimeout_withDynamicActiveTimeout() {
		SaTokenConfig config = SaManager.getConfig();
		config.setDynamicActiveTimeout(true);
		config.setActiveTimeout(120);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaLoginParameter param = new SaLoginParameter().setActiveTimeout(30L);
			stpLogic.login(90011, param);
			String token = stpLogic.getTokenValue();

			Long activeTimeout = stpLogic.getTokenUseActiveTimeout(token);
			Assertions.assertEquals(30L, activeTimeout);
			Assertions.assertEquals(30L, stpLogic.getTokenUseActiveTimeoutOrGlobalConfig(token));
		});
	}

	/** splicingKeyTokenSession 生成的 key 应包含 token-session 与 Token 值 */
	@Test
	void splicingKeyTokenSession() {
		String token = "abc-token";
		String key = stpLogic.splicingKeyTokenSession(token);
		Assertions.assertTrue(key.contains("token-session"));
		Assertions.assertTrue(key.endsWith(token));
	}

	/** 空 Token 调用 getTokenSessionByToken 应抛出 CODE_11073 */
	@Test
	void getTokenSessionByToken_emptyToken_throws() {
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.getTokenSessionByToken("", true));
		Assertions.assertEquals(SaErrorCode.CODE_11073, ex.getCode());
	}

	/** 无效 Token 调用 renewTimeout 应安全返回 */
	@Test
	void renewTimeout_invalidToken_returnsEarly() {
		Assertions.assertDoesNotThrow(() -> stpLogic.renewTimeout("missing-token", 100));
	}

	/** 无 Token 且 isCreate=false 时 getAnonTokenSession 应返回 null */
	@Test
	void getAnonTokenSession_notCreate_returnsNullWithoutToken() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertNull(stpLogic.getAnonTokenSession(false));
		});
	}

	/** keyword 为 null 时 searchSessionId 仍应返回结果 */
	@Test
	void searchSessionId_nullKeyword() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90012);
			List<String> list = stpLogic.searchSessionId(null, 0, 10, true);
			Assertions.assertFalse(list.isEmpty());
		});
	}

	/** 空 Token 调用 isSafe 应返回 false */
	@Test
	void isSafe_emptyToken_returnsFalse() {
		Assertions.assertFalse(stpLogic.isSafe("", "pay"));
		Assertions.assertFalse(stpLogic.isSafe(null, "pay"));
	}

	/** 无 Token 时 closeSafe 应不抛异常 */
	@Test
	void closeSafe_withoutToken_isNoOp() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertDoesNotThrow(() -> stpLogic.closeSafe("pay"));
		});
	}

	/** 关闭 dynamicActiveTimeout 时 getTokenUseActiveTimeout 应返回 null */
	@Test
	void getTokenUseActiveTimeout_whenDynamicDisabled_returnsNull() {
		SaManager.getConfig().setDynamicActiveTimeout(false);
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90013);
			Assertions.assertNull(stpLogic.getTokenUseActiveTimeout(stpLogic.getTokenValue()));
		});
	}

	/** 无效 Token 调用 logoutByTokenValue 应安全返回 */
	@Test
	void logoutByTokenValue_invalidToken_returnsEarly() {
		Assertions.assertDoesNotThrow(() -> stpLogic.logoutByTokenValue("invalid-token-value"));
	}

	/** 空参数 checkRoleAnd 应直接通过 */
	@Test
	void checkRoleAnd_skipsWhenEmptyArray() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90014);
			Assertions.assertDoesNotThrow(() -> stpLogic.checkRoleAnd());
		});
	}

	/** 空 sessionId 调用 getSessionBySessionId 应抛出 CODE_11072 */
	@Test
	void getSessionBySessionId_emptyId_throws() {
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.getSessionBySessionId("", false, null, null));
		Assertions.assertEquals(SaErrorCode.CODE_11072, ex.getCode());
	}

}

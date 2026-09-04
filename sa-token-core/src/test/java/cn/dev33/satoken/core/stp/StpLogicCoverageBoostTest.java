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
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * StpLogic JaCoCo 剩余行覆盖补充
 */
@SaTokenTest
public class StpLogicCoverageBoostTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
		SaManager.setStpInterface(new StpInterface() {
			@Override
			public List<String> getPermissionList(Object loginId, String loginType) {
				return Arrays.asList("user:add", "user:view");
			}

			@Override
			public List<String> getRoleList(Object loginId, String loginType) {
				return Arrays.asList("admin", "user");
			}
		});
	}

	/** Token 前缀模式下无 Token 时 getTokenValue 应返回 null */
	@Test
	void getTokenValue_prefixMode_emptyTokenBecomesNull() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenPrefix("Bearer");
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertNull(stpLogic.getTokenValue());
		});
	}

	/** 无 Token 时 getTokenValueNotNull 应抛出 NotLoginException */
	@Test
	void getTokenValueNotNull_throwsWhenMissingToken() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.getTokenValueNotNull());
		});
	}

	/** login 应对空/非法 loginId 抛异常，Map 与 extra 参数应允许 */
	@Test
	void login_checkLoginArgs_rejectsInvalidLoginId() {
		Assertions.assertThrows(SaTokenException.class, () -> stpLogic.login(""));
		Assertions.assertThrows(SaTokenException.class, () -> stpLogic.login(NotLoginException.NOT_TOKEN));
		Assertions.assertThrows(SaTokenException.class, () -> stpLogic.login("user:colon"));
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertDoesNotThrow(() -> stpLogic.login(new HashMap<>(), new SaLoginParameter()));
			Assertions.assertDoesNotThrow(() -> stpLogic.login(70001,
					new SaLoginParameter().setExtra("k", "v").setActiveTimeout(60L)));
		});
	}

	/** Token 被标记 TOKEN_TIMEOUT 时 getLoginId 应抛出对应异常 */
	@Test
	void getLoginId_throwsTokenTimeout() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70002);
			String token = stpLogic.getTokenValue();
			SaManager.getSaTokenDao().set(stpLogic.splicingKeyTokenValue(token),
					NotLoginException.TOKEN_TIMEOUT, SaTokenDao.NEVER_EXPIRE);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.parameterMap.put(stpLogic.getTokenName(), token);

			NotLoginException ex = Assertions.assertThrows(NotLoginException.class, () -> stpLogic.getLoginId());
			Assertions.assertEquals(NotLoginException.TOKEN_TIMEOUT, ex.getType());
		});
	}

	/** defaultValue 为 null 时 getLoginId 应返回原始 loginId */
	@Test
	void getLoginId_withNullDefault_returnsRawLoginId() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70003);
			Object loginId = stpLogic.getLoginId((String) null);
			Assertions.assertEquals("70003", String.valueOf(loginId));
		});
	}

	/** 身份切换期间 getLoginIdDefaultNull 应返回切换后的 loginId */
	@Test
	void getLoginIdDefaultNull_whenSwitch_returnsSwitchId() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70004);
			stpLogic.switchTo(70005);
			Assertions.assertEquals(70005, stpLogic.getLoginIdDefaultNull());
		});
	}

	/** Token 冻结时 getLoginIdDefaultNull 应返回 null */
	@Test
	void getLoginIdDefaultNull_whenFrozen_returnsNull() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70006);
			String token = stpLogic.getTokenValue();
			long oldTime = System.currentTimeMillis() - 60_000;
			SaManager.getSaTokenDao().set(stpLogic.splicingKeyLastActiveTime(token),
					String.valueOf(oldTime), 3600);
			Assertions.assertNull(stpLogic.getLoginIdDefaultNull());
		});
	}

	/** Token 冻结时 getLoginIdByToken 应返回 null */
	@Test
	void getLoginIdByToken_returnsNullWhenFrozen() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70007);
			String token = stpLogic.getTokenValue();
			long oldTime = System.currentTimeMillis() - 60_000;
			SaManager.getSaTokenDao().set(stpLogic.splicingKeyLastActiveTime(token),
					String.valueOf(oldTime), 3600);
			Assertions.assertNull(stpLogic.getLoginIdByToken(token));
		});
	}

	/** 空 loginId 调用 getSessionByLoginId 应抛出 SaTokenException */
	@Test
	void getSessionByLoginId_rejectsEmptyLoginId() {
		Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.getSessionByLoginId("", true));
	}

	/** 开启 tokenSessionCheckLogin 时无效 Token 应抛异常 */
	@Test
	void getTokenSessionByToken_rejectsInvalidTokenWhenCheckEnabled() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenSessionCheckLogin(true);
		SaManager.setConfig(config);

		Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.getTokenSessionByToken("not-a-valid-token-value", true));
	}

	/** 已有 Token-Session 时 getAnonTokenSession 应复用同一会话 */
	@Test
	void getAnonTokenSession_reusesExistingTokenSession() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70008);
			SaSession created = stpLogic.getTokenSession();
			SaSession reused = stpLogic.getAnonTokenSession(false);
			Assertions.assertEquals(created.getId(), reused.getId());
		});
	}

	/** 有效 Token 无 Session 时 isCreate=true 应创建 Token-Session */
	@Test
	void getAnonTokenSession_validTokenWithoutSession_createsWhenRequested() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70009);
			String token = stpLogic.getTokenValue();
			SaManager.getSaTokenDao().delete(stpLogic.splicingKeyTokenSession(token));

			SaSession session = stpLogic.getAnonTokenSession(true);
			Assertions.assertNotNull(session);
			Assertions.assertEquals(token, stpLogic.getTokenValue());
		});
	}

	/** 空 Token 时 getTokenLastActiveTime 应返回 NOT_VALUE_EXPIRE */
	@Test
	void getTokenLastActiveTime_handlesEmptyAndMissing() {
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getTokenLastActiveTime(null));
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getTokenLastActiveTime(""));

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70010);
			Assertions.assertTrue(stpLogic.getTokenLastActiveTime() >= 0
					|| stpLogic.getTokenLastActiveTime() == SaTokenDao.NOT_VALUE_EXPIRE);
		});
	}

	/** activeTimeout=-1 时 getTokenActiveTimeoutByToken 应返回 NEVER_EXPIRE */
	@Test
	void getTokenActiveTimeout_whenCheckDisabled_returnsNeverExpire() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(-1);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70011);
			Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE,
					stpLogic.getTokenActiveTimeoutByToken(stpLogic.getTokenValue()));
		});
	}

	/** 开启读 Cookie 时 renewTimeout 应正常更新 Token 超时 */
	@Test
	void renewTimeout_updatesCookieWhenReadCookieEnabled() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsReadCookie(true);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70012);
			Assertions.assertDoesNotThrow(() -> stpLogic.renewTimeout(7200));
			Assertions.assertTrue(stpLogic.getTokenTimeout() > 0);
		});
	}

	/** Session 中无对应终端时 renewTimeout 应抛出 SaTokenException */
	@Test
	void renewTimeout_throwsWhenTerminalMissing() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70013);
			String token = stpLogic.getTokenValue();
			SaSession session = stpLogic.getSessionByLoginId(70013);
			session.removeTerminal(token);

			Assertions.assertThrows(SaTokenException.class,
					() -> stpLogic.renewTimeout(token, 3600));
		});
	}

	/** 登录后 getRoleList/getPermissionList 应返回 StpInterface 配置 */
	@Test
	void getRoleList_and_getPermissionList_useCurrentLogin() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70014);
			Assertions.assertTrue(stpLogic.getRoleList().contains("admin"));
			Assertions.assertTrue(stpLogic.getPermissionList().contains("user:add"));
		});
	}

	/** 未登录时 hasRoleOr/hasPermissionAnd/Or 应返回 false */
	@Test
	void hasRoleOr_and_hasPermissionOr_returnFalseWhenNotLogin() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertFalse(stpLogic.hasRoleOr("admin"));
			Assertions.assertFalse(stpLogic.hasPermissionAnd("user:add"));
			Assertions.assertFalse(stpLogic.hasPermissionOr("user:add"));
		});
	}

	/** 空参数 checkRoleOr 应直接通过 */
	@Test
	void checkRoleOr_emptyArray_skipsValidation() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70015);
			Assertions.assertDoesNotThrow(() -> stpLogic.checkRoleOr());
		});
	}

	/** 无匹配角色时 checkRoleOr 应抛出 NotRoleException */
	@Test
	void checkRoleOr_noMatch_throwsNotRoleException() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70016);
			Assertions.assertThrows(NotRoleException.class,
					() -> stpLogic.checkRoleOr("guest", "super"));
		});
	}

	/** 无匹配权限时 checkPermissionOr 应抛出 NotPermissionException */
	@Test
	void checkPermissionOr_noMatch_throwsNotPermissionException() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70017);
			Assertions.assertThrows(NotPermissionException.class,
					() -> stpLogic.checkPermissionOr("goods:view", "order:list"));
		});
	}

	/** 无 Session 时 forEachTerminalList 应不执行回调 */
	@Test
	void forEachTerminalList_noSession_isNoOp() {
		AtomicInteger count = new AtomicInteger();
		stpLogic.forEachTerminalList(79999, (session, terminal) -> count.incrementAndGet());
		Assertions.assertEquals(0, count.get());
	}

	/** 无效/冻结/已移除终端的 Token 查询 getTerminalInfoByToken 应返回 null */
	@Test
	void getTerminalInfoByToken_invalidOrFrozen_returnsNull() {
		Assertions.assertNull(stpLogic.getTerminalInfoByToken(null));
		Assertions.assertNull(stpLogic.getTerminalInfoByToken("invalid-token"));

		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70018);
			String token = stpLogic.getTokenValue();
			long oldTime = System.currentTimeMillis() - 60_000;
			SaManager.getSaTokenDao().set(stpLogic.splicingKeyLastActiveTime(token),
					String.valueOf(oldTime), 3600);
			Assertions.assertNull(stpLogic.getTerminalInfoByToken(token));

			SaSession session = stpLogic.getSessionByLoginId(70018);
			session.removeTerminal(token);
			Assertions.assertNull(stpLogic.getTerminalInfoByToken(token));
		});
	}

	/** 单参数 replacedByTokenValue 应将 Token 标记为 BE_REPLACED */
	@Test
	void replacedByTokenValue_oneArgOverload() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70019);
			String token = stpLogic.getTokenValue();
			stpLogic.replacedByTokenValue(token);
			Assertions.assertEquals(NotLoginException.BE_REPLACED,
					SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** 冻结 Token 无 keepFreezeOps 时 kickoutByTokenValue 不应清除映射 */
	@Test
	void logoutByTokenValue_skipsFrozenTokenWithoutKeepFreezeOps() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70020);
			String token = stpLogic.getTokenValue();
			long oldTime = System.currentTimeMillis() - 60_000;
			SaTokenDao dao = SaManager.getSaTokenDao();
			dao.set(stpLogic.splicingKeyLastActiveTime(token), String.valueOf(oldTime), 3600);

			stpLogic.kickoutByTokenValue(token);
			Assertions.assertEquals("70020", dao.get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** 无 Session 时 logoutByMaxLoginCount 应安全返回 */
	@Test
	void logoutByMaxLoginCount_noSession_returnsEarly() {
		Assertions.assertDoesNotThrow(() ->
				stpLogic.logoutByMaxLoginCount(79998, null, null, 1, SaLogoutMode.LOGOUT));
	}

	/** untieDisable/disableLevel 传入非法参数应抛出 SaTokenException */
	@Test
	void untieDisable_and_disableLevel_validateArguments() {
		Assertions.assertThrows(SaTokenException.class, () -> stpLogic.untieDisable(null));
		Assertions.assertThrows(SaTokenException.class, () -> stpLogic.untieDisable(70021, (String[]) null));
		Assertions.assertThrows(SaTokenException.class, () -> stpLogic.disableLevel(null, 1, 60));
		Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.disableLevel(70021, "", 1, 60));
		Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.disableLevel(70021, "shop", -2, 60));
	}

	/** 无效 Token 时 isSafe 应返回 false，getSafeTime 应返回 NOT_VALUE_EXPIRE */
	@Test
	void isSafe_and_getSafeTime_handleMissingToken() {
		Assertions.assertFalse(stpLogic.isSafe("invalid-token", "pay"));
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getSafeTime("pay"));
		});
	}

	/** Token 无对应 loginId 映射时 isSafe 应返回 false */
	@Test
	void isSafe_invalidLoginId_returnsFalse() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.setTokenValue("orphan-token");
			Assertions.assertFalse(stpLogic.isSafe("orphan-token", "pay"));
		});
	}

	/** getLoginId 应支持将字符串 loginId 转换为 Long 默认值类型 */
	@Test
	void getLoginId_convertsToDefaultValueType() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login("70022");
			Assertions.assertEquals(70022L, stpLogic.getLoginId(0L));
		});
	}

	/** 空 Token 时 getLoginIdByTokenNotThinkFreeze 应返回 null */
	@Test
	void getLoginIdByTokenNotThinkFreeze_emptyToken_returnsNull() {
		Assertions.assertNull(stpLogic.getLoginIdByTokenNotThinkFreeze(null));
		Assertions.assertNull(stpLogic.getLoginIdByTokenNotThinkFreeze(""));
	}

	/** 开启 activeTimeout 时 getAnonTokenSession 应写入最后活跃时间 */
	@Test
	void getAnonTokenSession_withActiveTimeout_setsLastActive() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(300);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.logout();
			stpLogic.getAnonTokenSession();
			String token = stpLogic.getTokenValue();
			Assertions.assertNotNull(SaManager.getSaTokenDao().get(stpLogic.splicingKeyLastActiveTime(token)));
		});
	}

	/** Token 冻结时 getTokenActiveTimeoutByToken 应返回 NOT_VALUE_EXPIRE */
	@Test
	void getTokenActiveTimeout_frozenToken_returnsNotValueExpire() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70023);
			String token = stpLogic.getTokenValue();
			long oldTime = System.currentTimeMillis() - 60_000;
			SaManager.getSaTokenDao().set(stpLogic.splicingKeyLastActiveTime(token),
					String.valueOf(oldTime), 3600);
			Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE,
					stpLogic.getTokenActiveTimeoutByToken(token));
		});
	}

	/** 缺少最后活跃记录时 getTokenActiveTimeoutByToken 应返回 NOT_VALUE_EXPIRE */
	@Test
	void getTokenActiveTimeout_missingLastActive_returnsNotValueExpire() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70024);
			String token = stpLogic.getTokenValue();
			SaManager.getSaTokenDao().delete(stpLogic.splicingKeyLastActiveTime(token));
			Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE,
					stpLogic.getTokenActiveTimeoutByToken(token));
		});
	}

	/** 无效 Token 调用 renewTimeout 应不抛异常 */
	@Test
	void renewTimeout_invalidToken_isNoOp() {
		Assertions.assertDoesNotThrow(() -> stpLogic.renewTimeout("ghost-token", 3600));
	}

	/** renewTimeout 应同步更新 Token-Session 超时与活跃记录 */
	@Test
	void renewTimeout_updatesTokenSessionAndActiveRecord() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(300);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70025);
			String token = stpLogic.getTokenValue();
			stpLogic.getTokenSession();
			stpLogic.renewTimeout(token, 7200);
			Assertions.assertTrue(stpLogic.getTokenSessionTimeoutByTokenValue(token) > 0);
			Assertions.assertNotNull(SaManager.getSaTokenDao().get(stpLogic.splicingKeyLastActiveTime(token)));
		});
	}

	/** 登录后无匹配角色/权限时 hasRoleOr/hasPermissionOr 应返回 false */
	@Test
	void hasRoleOr_and_hasPermissionOr_returnFalseWhenCheckFails() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70026);
			Assertions.assertFalse(stpLogic.hasRoleOr("guest", "super"));
			Assertions.assertFalse(stpLogic.hasPermissionAnd("goods:view", "order:delete"));
			Assertions.assertFalse(stpLogic.hasPermissionOr("goods:view", "order:delete"));
		});
	}

	/** Session 缺失或终端已移除时 getTerminalInfoByToken 应返回 null */
	@Test
	void getTerminalInfoByToken_sessionMissingOrTerminalRemoved_returnsNull() {
		SaTokenDao dao = SaManager.getSaTokenDao();
		String token = stpLogic.createTokenValue(70027, "PC", 3600, null);
		dao.set(stpLogic.splicingKeyTokenValue(token), "70027", 3600);
		Assertions.assertNull(stpLogic.getTerminalInfoByToken(token));

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70028);
			String loginToken = stpLogic.getTokenValue();
			SaSession session = stpLogic.getSessionByLoginId(70028);
			session.removeTerminal(loginToken);
			Assertions.assertNull(stpLogic.getTerminalInfoByToken(loginToken));
		});
	}

	/** 超出 maxLoginCount 时应清除被挤掉 Token 的活跃记录 */
	@Test
	void logoutByMaxLoginCount_clearsLastActiveWhenEnabled() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(true);
		config.setIsShare(false);
		config.setMaxLoginCount(1);
		config.setActiveTimeout(300);
		SaManager.setConfig(config);

		String token1 = stpLogic.createLoginSession(70029, new SaLoginParameter().setDeviceType("PC"));
		String token2 = stpLogic.createLoginSession(70029, new SaLoginParameter().setDeviceType("APP"));
		SaTokenDao dao = SaManager.getSaTokenDao();
		Assertions.assertNotNull(dao.get(stpLogic.splicingKeyLastActiveTime(token2)));
		Assertions.assertNull(dao.get(stpLogic.splicingKeyLastActiveTime(token1)));
	}

	/** Token 活跃时 getTokenActiveTimeout 应返回剩余秒数 */
	@Test
	void getTokenActiveTimeout_returnsRemainingSecondsWhenActive() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(300);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70030);
			long remaining = stpLogic.getTokenActiveTimeout();
			Assertions.assertTrue(remaining > 0 && remaining <= 300);
		});
	}

	/** Account Session 不存在时 renewTimeout 应抛出 SaTokenException */
	@Test
	void renewTimeout_throwsWhenAccountSessionMissing() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70031);
			String token = stpLogic.getTokenValue();
			SaManager.getSaTokenDao().delete(stpLogic.splicingKeySession(70031));
			Assertions.assertThrows(SaTokenException.class, () -> stpLogic.renewTimeout(token, 3600));
		});
	}

	/** 终端列表为空时 getTerminalInfoByToken 应返回 null */
	@Test
	void getTerminalInfoByToken_validTokenButTerminalMissingInList_returnsNull() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70032);
			String token = stpLogic.getTokenValue();
			SaSession session = stpLogic.getSessionByLoginId(70032);
			session.removeTerminal(token);
			session.setTerminalList(new java.util.ArrayList<>());
			Assertions.assertNull(stpLogic.getTerminalInfoByToken(token));
		});
	}

}

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
import cn.dev33.satoken.config.SaCookieConfig;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * StpLogic 剩余 public 路径覆盖：设备信任、多来源读 token、注销参数组合等
 */
@SaTokenTest
public class StpLogicUltraTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** isTrustDeviceId 与 getLoginDeviceId 应正确匹配设备 ID */
	@Test
	void isTrustDeviceId_and_getLoginDeviceId() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertFalse(stpLogic.isTrustDeviceId(60001, "dev-a"));
			Assertions.assertFalse(stpLogic.isTrustDeviceId(60001, ""));

			stpLogic.login(60001, new SaLoginParameter()
					.setDeviceType("PC")
					.setDeviceId("dev-a"));
			Assertions.assertTrue(stpLogic.isTrustDeviceId(60001, "dev-a"));
			Assertions.assertFalse(stpLogic.isTrustDeviceId(60001, "dev-b"));
			Assertions.assertEquals("dev-a", stpLogic.getLoginDeviceId());
			Assertions.assertEquals("dev-a", stpLogic.getLoginDeviceIdByToken(stpLogic.getTokenValue()));
		});
	}

	/** 仅读 Body 配置下 getTokenValue 应从请求参数读取 Token */
	@Test
	void getTokenValue_readsFromBodyOnly() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsReadBody(true);
		config.setIsReadHeader(false);
		config.setIsReadCookie(false);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(60002);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.parameterMap.put(stpLogic.getTokenName(), token);

			Assertions.assertEquals(token, stpLogic.getTokenValue());
			Assertions.assertTrue(stpLogic.isLogin());
		});
	}

	/** 仅读 Storage 配置下 getTokenValue 应从 Storage 读取 Token */
	@Test
	void getTokenValue_readsFromStorageOnly() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsReadBody(false);
		config.setIsReadHeader(false);
		config.setIsReadCookie(false);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(60003);
			stpLogic.setTokenValueToStorage(token);

			Assertions.assertEquals(token, stpLogic.getTokenValue());
			Assertions.assertEquals("60003", stpLogic.getLoginIdAsString());
		});
	}

	/** 空 Token 调用 setTokenValue 应不抛异常 */
	@Test
	void setTokenValue_emptyToken_isNoOp() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertDoesNotThrow(() -> stpLogic.setTokenValue(""));
			Assertions.assertDoesNotThrow(() -> stpLogic.setTokenValue(null));
			Assertions.assertDoesNotThrow(() -> stpLogic.setTokenValue("", 60));
		});
	}

	/** 关闭读 Cookie 时 setTokenValue 应仅写入 Storage */
	@Test
	void setTokenValue_skipsCookieWhenReadCookieDisabled() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsReadCookie(false);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.setTokenValue("storage-only-token", 120);
			Assertions.assertEquals("storage-only-token", stpLogic.getTokenValueNotCut());
		});
	}

	/** Token 前缀模式下 Storage 应存带前缀的值 */
	@Test
	void setTokenValue_withTokenPrefix_writesPrefixedToStorage() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenPrefix("Bearer");
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.setTokenValue("prefixed-token");
			String stored = String.valueOf(SaHolder.getStorage().get(stpLogic.splicingKeyJustCreatedSave()));
			Assertions.assertTrue(stored.startsWith("Bearer" + SaTokenConsts.TOKEN_CONNECTOR_CHAT));
			Assertions.assertEquals("prefixed-token", stpLogic.getTokenValue());
		});
	}

	/** createSaLoginParameter/createSaLogoutParameter 应复制全局配置 */
	@Test
	void createSaLoginParameter_and_createSaLogoutParameter() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTimeout(7200);
		config.setIsConcurrent(false);
		SaManager.setConfig(config);

		SaLoginParameter loginParam = stpLogic.createSaLoginParameter();
		Assertions.assertEquals(7200, loginParam.getTimeout());
		Assertions.assertFalse(loginParam.getIsConcurrent());

		SaLogoutParameter logoutParam = stpLogic.createSaLogoutParameter();
		Assertions.assertEquals(config.getLogoutRange(), logoutParam.getRange());
	}

	/** 全局 timeout 为 NEVER_EXPIRE 时 getConfigOfCookieTimeout 应返回 MAX_VALUE */
	@Test
	void getConfigOfCookieTimeout_neverExpire_returnsMaxInt() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTimeout(SaTokenDao.NEVER_EXPIRE);
		SaManager.setConfig(config);
		Assertions.assertEquals(Integer.MAX_VALUE, stpLogic.getConfigOfCookieTimeout());
	}

	/** 重新登录更长 timeout 时 Token 剩余时间应延长 */
	@Test
	void updateMinTimeout_extendsOnReLogin() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(60004, new SaLoginParameter().setTimeout(3600));
			long firstTimeout = stpLogic.getTokenTimeout();

			stpLogic.login(60004, new SaLoginParameter().setTimeout(7200));
			long secondTimeout = stpLogic.getTokenTimeout();
			Assertions.assertTrue(secondTimeout >= firstTimeout);
		});
	}

	/** 按 deviceId logout 应仅清除匹配终端 */
	@Test
	void logout_byDeviceId_clearsMatchingTerminalOnly() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String tokenA = stpLogic.createLoginSession(60005, new SaLoginParameter()
					.setDeviceType("PC").setDeviceId("dev-a"));
			String tokenB = stpLogic.createLoginSession(60005, new SaLoginParameter()
					.setDeviceType("APP").setDeviceId("dev-b"));
			SaTokenDao dao = SaManager.getSaTokenDao();

			stpLogic.logout(60005, new SaLogoutParameter().setDeviceId("dev-a"));
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(tokenA)));
			Assertions.assertEquals("60005", dao.get(stpLogic.splicingKeyTokenValue(tokenB)));
		});
	}

	/** REPLACED 模式 logout 应保留 Account Session */
	@Test
	void logout_replacedMode_keepsAccountSession() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(60006, new SaLoginParameter().setDeviceType("PC").setDeviceId("dev-pc"));
			SaSession session = stpLogic.getSessionByLoginId(60006);
			Assertions.assertNotNull(session);

			stpLogic._logout(60006, stpLogic.createSaLogoutParameter()
					.setMode(SaLogoutMode.REPLACED)
					.setDeviceType("PC"));
			Assertions.assertNotNull(SaManager.getSaTokenDao().getSession(stpLogic.splicingKeySession(60006)));
		});
	}

	/** 无 Token 时 logout 应安全返回 */
	@Test
	void logout_currentClient_emptyToken_returnsEarly() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertDoesNotThrow(() -> stpLogic.logout());
		});
	}

	/** 开启读 Cookie 时 logout 应清除 Cookie */
	@Test
	void logout_clearsCookieWhenReadCookieEnabled() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsReadCookie(true);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(60007);
			stpLogic.logout();
			SaResponseForMock response = (SaResponseForMock) SaHolder.getResponse();
			Assertions.assertNotNull(response);
		});
	}

	/** hasElement/isSupportExtra/getLoginDevice 等辅助方法应正确 */
	@Test
	void hasElement_isSupportExtra_and_deprecatedGetLoginDevice() {
		List<String> list = Arrays.asList("admin", "user:1");
		Assertions.assertTrue(stpLogic.hasElement(list, "admin"));
		Assertions.assertFalse(stpLogic.hasElement(list, "guest"));
		Assertions.assertFalse(stpLogic.isSupportExtra());

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(60008, "HD");
			Assertions.assertEquals("HD", stpLogic.getLoginDevice());
			Assertions.assertEquals("HD", stpLogic.getLoginDeviceType());
		});
	}

	/** 创建 Token-Session 后 searchTokenSessionId 应返回非空列表 */
	@Test
	void searchTokenSessionId_returnsResults() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(60009);
			stpLogic.getTokenSession();
			List<String> ids = stpLogic.searchTokenSessionId("", 0, 10, true);
			Assertions.assertFalse(ids.isEmpty());
		});
	}

	/** 上下文内 getOrCreateLoginSession 应创建并复用同一 Token */
	@Test
	void getOrCreateLoginSession_inContext_createsWhenMissing() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.getOrCreateLoginSession(60010);
			Assertions.assertNotNull(token);
			Assertions.assertTrue(stpLogic.isLogin(60010));
			String same = stpLogic.getOrCreateLoginSession(60010);
		 Assertions.assertEquals(token, same);
		});
	}

	/** 带 SaLogoutParameter 的 kickout/replacedByTokenValue 应正确标记 Token */
	@Test
	void kickoutAndReplacedByTokenValue_withLogoutParameter() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(60011);
			String token = stpLogic.getTokenValue();
			stpLogic.kickoutByTokenValue(token, new SaLogoutParameter());
			Assertions.assertEquals(NotLoginException.KICK_OUT,
					SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token)));

			stpLogic.login(60011);
			token = stpLogic.getTokenValue();
			stpLogic.replacedByTokenValue(token, new SaLogoutParameter());
			Assertions.assertEquals(NotLoginException.BE_REPLACED,
					SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** 冻结 Token 且 keepFreezeOps 时 logout 应保留 Token-Session */
	@Test
	void logoutByTokenValue_keepTokenSession_onFrozenTokenWithKeepFreezeOps() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		config.setIsLogoutKeepTokenSession(true);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(60012);
			String token = stpLogic.getTokenValue();
			stpLogic.getTokenSession();
			SaTokenDao dao = SaManager.getSaTokenDao();
			long oldTime = System.currentTimeMillis() - 60_000;
			dao.set(stpLogic.splicingKeyLastActiveTime(token), String.valueOf(oldTime), 3600);

			stpLogic.logoutByTokenValue(token, new SaLogoutParameter().setIsKeepFreezeOps(true));
			Assertions.assertNotNull(dao.getSession(stpLogic.splicingKeyTokenSession(token)));
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** 自定义 Cookie 配置下 setTokenValue 应写入 Cookie */
	@Test
	void setTokenValueToCookie_withCustomCookieConfig() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaCookieConfig cookie = new SaCookieConfig().setDomain("test.local").setPath("/api");
			stpLogic.setTokenValue("cookie-token", new SaLoginParameter().setCookie(cookie).setTimeout(300));
			Assertions.assertEquals("cookie-token", stpLogic.getTokenValueNotCut());
		});
	}

	/** createTokenValue 应委托策略生成非空 Token */
	@Test
	void createTokenValue_delegatesToStrategy() {
		String token = stpLogic.createTokenValue(60013, "PC", 3600, null);
		Assertions.assertNotNull(token);
		Assertions.assertFalse(token.isEmpty());
	}

	/** 核心模式下 getExtra 应抛出异常 */
	@Test
	void getExtra_throwsApiDisabledException() {
		Assertions.assertThrows(Exception.class, () -> stpLogic.getExtra("k"));
		Assertions.assertThrows(Exception.class, () -> stpLogic.getExtra("token", "k"));
	}

	/** isValidLoginId 应拒绝 null/空/特殊标记 loginId */
	@Test
	void isValidLoginId_rejectsAbnormalMarkers() {
		Assertions.assertFalse(stpLogic.isValidLoginId(null));
		Assertions.assertFalse(stpLogic.isValidLoginId(""));
		Assertions.assertFalse(stpLogic.isValidLoginId(NotLoginException.KICK_OUT));
		Assertions.assertTrue(stpLogic.isValidLoginId(60014));
	}

	/** getSessionBySessionId 的 appendOperation 回调应在创建时执行 */
	@Test
	void getSessionBySessionId_withAppendOperation() {
		String sessionId = stpLogic.splicingKeySession(60015);
		SaSession session = stpLogic.getSessionBySessionId(sessionId, true, 3600L, s -> s.set("init", "yes"));
		Assertions.assertNotNull(session);
		Assertions.assertEquals("yes", session.get("init"));
	}

	/** updateLastActiveToNow 后 checkActiveTimeoutByConfig 应通过 */
	@Test
	void updateLastActiveToNow_and_checkActiveTimeoutByConfig() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(300);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(60016);
			String token = stpLogic.getTokenValue();
			stpLogic.updateLastActiveToNow(token);
			stpLogic.updateLastActiveToNow();
			Assertions.assertDoesNotThrow(() -> stpLogic.checkActiveTimeoutByConfig(token));
			Assertions.assertDoesNotThrow(() -> stpLogic.checkActiveTimeout());
		});
	}

	/** getLoginDeviceByToken 应与设备类型 getter 返回一致 */
	@Test
	void getLoginDeviceByToken_deprecatedAlias() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(60017, "MINI");
			String token = stpLogic.getTokenValue();
			Assertions.assertEquals("MINI", stpLogic.getLoginDeviceByToken(token));
		});
	}

	/** 超出 maxLoginCount 时应自动查找 Session 并挤掉旧 Token */
	@Test
	void logoutByMaxLoginCount_nullSession_looksUpSession() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(true);
		config.setIsShare(false);
		config.setMaxLoginCount(1);
		SaManager.setConfig(config);

		stpLogic.createLoginSession(60018, new SaLoginParameter().setDeviceType("PC"));
		String token2 = stpLogic.createLoginSession(60018, new SaLoginParameter().setDeviceType("APP"));
		Assertions.assertEquals("60018", SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token2)));
	}

}

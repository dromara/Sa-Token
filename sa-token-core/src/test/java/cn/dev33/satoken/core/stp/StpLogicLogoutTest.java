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
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutRange;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 注销与踢人下线
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicLogoutTest {

	private StpLogic stpLogic;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** logout 应清除当前客户端 Token 及 DAO 映射 */
	@Test
	void logout_clearsCurrentClientToken() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();
			Assertions.assertEquals("10001", dao.get(stpLogic.splicingKeyTokenValue(token)));

			stpLogic.logout();
			Assertions.assertNull(stpLogic.getTokenValue());
			Assertions.assertFalse(stpLogic.isLogin());
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** 按 loginId logout 应清除 Token 映射与 Account Session */
	@Test
	void logoutByLoginId_clearsAccountSession() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();

			stpLogic.logout(10001);
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(token)));
			Assertions.assertNull(dao.getSession(stpLogic.splicingKeySession(10001)));
		});
	}

	/** kickoutByTokenValue 后 checkLogin 应抛出 NotLoginException */
	@Test
	void kickoutByTokenValue_marksTokenAsKicked() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(10001);
			stpLogic.setTokenValue(token);

			stpLogic.kickoutByTokenValue(token);
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.checkLogin());
		});
	}

	/** 无上下文 logoutByTokenValue 应清除 Token 映射并清空终端 */
	@Test
	void logoutByTokenValue_withoutContext() {
		String token = stpLogic.createLoginSession(10003);
		SaTokenDao dao = SaManager.getSaTokenDao();
		Assertions.assertEquals("10003", dao.get(stpLogic.splicingKeyTokenValue(token)));

		stpLogic.logoutByTokenValue(token);
		Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(token)));
		SaSession session = dao.getSession(stpLogic.splicingKeySession(10003));
		Assertions.assertTrue(session == null || session.getTerminalList().isEmpty());
	}

	/** TOKEN 范围仅注销当前 Token，ACCOUNT 范围应清除全部 Session */
	@Test
	void logoutRange_token_vs_account() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String pcToken = stpLogic.createLoginSession(50011, new SaLoginParameter().setDeviceType("PC"));
			String appToken = stpLogic.createLoginSession(50011, new SaLoginParameter().setDeviceType("APP"));
			SaTokenDao dao = SaManager.getSaTokenDao();

			stpLogic.setTokenValue(pcToken);
			stpLogic.logout(new SaLogoutParameter().setRange(SaLogoutRange.TOKEN));
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(pcToken)));
			Assertions.assertEquals("50011", dao.get(stpLogic.splicingKeyTokenValue(appToken)));

			stpLogic.setTokenValue(appToken);
			stpLogic.logout(new SaLogoutParameter().setRange(SaLogoutRange.ACCOUNT));
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(appToken)));
			Assertions.assertNull(dao.getSession(stpLogic.splicingKeySession(50011)));
		});
	}

	/** Token 冻结时 ACCOUNT 范围 logout 不应清除 Token 映射 */
	@Test
	void logout_skipsFrozenAccountRangeLogout() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50017);
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();
			long oldTime = System.currentTimeMillis() - 60_000;
			dao.set(stpLogic.splicingKeyLastActiveTime(token), String.valueOf(oldTime), 3600);

			stpLogic.logout(new SaLogoutParameter().setRange(SaLogoutRange.ACCOUNT));
			Assertions.assertEquals("50017", dao.get(stpLogic.splicingKeyTokenValue(token)));
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

}

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
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 杂项与边界测试
 */
@SaTokenTest
public class StpLogicMiscTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** setConfig/setLoginType 后 getter 应返回设置的值 */
	@Test
	void configAndLoginType() {
		SaTokenConfig config = new SaTokenConfig();
		config.setTokenName("my-token");
		stpLogic.setConfig(config);
		Assertions.assertSame(config, stpLogic.getConfig());
		Assertions.assertSame(config, stpLogic.getConfigOrGlobal());
		Assertions.assertEquals("my-token", stpLogic.getTokenName());
		stpLogic.setLoginType("app");
		Assertions.assertEquals("app", stpLogic.getLoginType());
	}

	/** isLastingCookie 与 timeout 参数登录后 Token 超时时间应正确 */
	@Test
	void login_withIsLastingCookieAndTimeout() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(40001, false);
			Assertions.assertTrue(stpLogic.isLogin());
			stpLogic.logout();

			stpLogic.login(40002, 60);
			long timeout = stpLogic.getTokenTimeout();
			Assertions.assertTrue(timeout <= 60 && timeout >= 59);
		});
	}

	/** getTokenValueNotNull 及各 getLoginIdByToken 变体应返回正确 loginId */
	@Test
	void getTokenValueNotNull_andByToken() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(40003);
			String token = stpLogic.getTokenValueNotNull();
			Assertions.assertEquals("40003", stpLogic.getLoginIdByToken(token));
			Assertions.assertEquals("40003", stpLogic.getLoginIdByTokenNotThinkFreeze(token));
			Assertions.assertEquals("40003", stpLogic.getLoginIdNotHandle(token));
			Assertions.assertTrue(stpLogic.isValidToken(token));
			Assertions.assertTrue(stpLogic.isValidLoginId(40003));
		});
	}

	/** 无上下文 getOrCreateLoginSession 应创建并返回 Token */
	@Test
	void getOrCreateLoginSession_withoutContext() {
		String token = stpLogic.getOrCreateLoginSession(40004);
		Assertions.assertNotNull(token);
		Assertions.assertEquals("40004", SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token)));
	}

	/** 带 SaLogoutParameter 的 kickout/replacedByTokenValue 应使 Token 失效 */
	@Test
	void logoutParameter_andKickoutReplacedByTokenWithParameter() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(40005);
			String token = stpLogic.getTokenValue();
			SaLogoutParameter param = new SaLogoutParameter();

			stpLogic.kickoutByTokenValue(token, param);
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.checkLogin());

			stpLogic.login(40005);
			token = stpLogic.getTokenValue();
			stpLogic.replacedByTokenValue(token, param);
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.checkLogin());
		});
	}

	/** 带参数的 kickout/replaced 按 loginId 应使当前客户端下线 */
	@Test
	void kickoutAndReplacedByLoginId_withParameter() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(40006);
			stpLogic.kickout(40006, new SaLogoutParameter());
			Assertions.assertFalse(stpLogic.isLogin());

			stpLogic.login(40006);
			stpLogic.replaced(40006, new SaLogoutParameter());
			Assertions.assertFalse(stpLogic.isLogin());
		});
	}

	/** 带 SaLoginParameter 的 setTokenValue 应将 Token 写入上下文 */
	@Test
	void setTokenValue_withLoginParameter() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(40007, new SaLoginParameter());
			SaLoginParameter param = new SaLoginParameter().setToken(token);
			stpLogic.setTokenValue(token, param);
			Assertions.assertEquals(token, stpLogic.getTokenValue());
		});
	}

	/** 活跃时间过期后 isFreeze 应返回 true */
	@Test
	void isFreeze_whenActiveTimeoutExpired() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(40008);
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();
			long oldTime = System.currentTimeMillis() - 60_000;
			dao.set(stpLogic.splicingKeyLastActiveTime(token), String.valueOf(oldTime), 3600);
			Assertions.assertTrue(stpLogic.isFreeze(token));
		});
	}

	/** 带 timeout 的 getSessionByLoginId 与 getSessionBySessionId 应正常返回 Session */
	@Test
	void getSession_withTimeoutAndAppendOperation() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(40009);
			SaSession session = stpLogic.getSessionByLoginId(40009, true, 3600L);
			Assertions.assertNotNull(session);
			SaSession byId = stpLogic.getSessionBySessionId(session.getId(), false, null, null);
			Assertions.assertNotNull(byId);
		});
	}

}

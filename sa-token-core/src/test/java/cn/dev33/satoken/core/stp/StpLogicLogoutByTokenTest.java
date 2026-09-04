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
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 根据 Token 注销与踢人下线
 */
@SaTokenTest
public class StpLogicLogoutByTokenTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** logoutByTokenValue 应清除 DAO 映射且 checkLogin 抛出 INVALID_TOKEN */
	@Test
	void logoutByTokenValue_clearsDaoAndCheckLoginInvalidToken() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.logout(30031);
			stpLogic.login(30031);
			Assertions.assertTrue(stpLogic.isLogin());
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();
			stpLogic.logoutByTokenValue(token);
			Assertions.assertFalse(stpLogic.isLogin());
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(token)));
			Assertions.assertNull(dao.getSession(stpLogic.splicingKeySession(30031)));
			try {
				stpLogic.checkLogin();
			} catch (NotLoginException e) {
				Assertions.assertEquals(NotLoginException.INVALID_TOKEN, e.getType());
			}
		});
	}

	/** 上下文内 kickoutByTokenValue 后 checkLogin 应抛出 KICK_OUT */
	@Test
	void kickoutByTokenValue_marksKickOutInContext() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30032);
			String token = stpLogic.getTokenValue();
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put(stpLogic.getTokenName(), token);
			stpLogic.kickoutByTokenValue(token);
			try {
				stpLogic.checkLogin();
			} catch (NotLoginException e) {
				Assertions.assertEquals(NotLoginException.KICK_OUT, e.getType());
			}
		});
	}

	/** 无上下文 logoutByTokenValue 应清除 Token 映射 */
	@Test
	void logoutByTokenValue_withoutContext_clearsMappingOnly() {
		String token = stpLogic.createLoginSession(30033);
		SaTokenDao dao = SaManager.getSaTokenDao();
		Assertions.assertEquals("30033", dao.get(stpLogic.splicingKeyTokenValue(token)));
		stpLogic.logoutByTokenValue(token);
		Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(token)));
		SaSession session = dao.getSession(stpLogic.splicingKeySession(30033));
		Assertions.assertTrue(session == null || session.getTerminalList().isEmpty());
	}

}

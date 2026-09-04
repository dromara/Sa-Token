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
 * StpLogic 注销与踢人下线
 */
@SaTokenTest
public class StpLogicLogoutTest {

	private StpLogic stpLogic;

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

}

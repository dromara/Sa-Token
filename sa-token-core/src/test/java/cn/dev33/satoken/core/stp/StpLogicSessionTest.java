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
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic Session 读写
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicSessionTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** 登录后 getSession 应返回 Account Session 并支持读写数据 */
	@Test
	void getSession_afterLogin() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);

			SaSession session = stpLogic.getSession();
			Assertions.assertNotNull(session);
			Assertions.assertEquals(stpLogic.splicingKeySession(10001), session.getId());

			SaSession sessionNoCreate = stpLogic.getSession(false);
			Assertions.assertNotNull(sessionNoCreate);

			session.set("nickname", "zhangsan");
			Assertions.assertEquals("zhangsan", stpLogic.getSession().get("nickname"));
		});
	}

	/** 登录后 getTokenSession 应返回与 Token 绑定的 Session */
	@Test
	void getTokenSession_afterLogin() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			String token = stpLogic.getTokenValue();

			SaSession tokenSession = stpLogic.getTokenSession();
			Assertions.assertNotNull(tokenSession);
			Assertions.assertEquals(stpLogic.splicingKeyTokenSession(token), tokenSession.getId());

			tokenSession.set("from", "unit-test");
			Assertions.assertEquals("unit-test", stpLogic.getTokenSessionByToken(token).get("from"));
		});
	}

	/** getSessionByLoginId 在 isCreate=true 时应懒创建 Session */
	@Test
	void getSessionByLoginId_withoutLogin() {
		SaSession session = stpLogic.getSessionByLoginId(20002, false);
		Assertions.assertNull(session);

		SaSession created = stpLogic.getSessionByLoginId(20002, true);
		Assertions.assertNotNull(created);
		SaTokenDao dao = SaManager.getSaTokenDao();
		Assertions.assertNotNull(dao.getSession(stpLogic.splicingKeySession(20002)));
	}

}

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
import java.util.List;

/**
 * StpLogic Token-Session 懒加载与检索
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicTokenSessionTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** 首次访问 getTokenSession 应懒创建 Token-Session */
	@Test
	void getTokenSession_lazyCreatesOnFirstAccess() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30041);
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();
			Assertions.assertNull(stpLogic.getTokenSession(false));
			Assertions.assertNull(dao.getSession(stpLogic.splicingKeyTokenSession(token)));
			SaSession tokenSession = stpLogic.getTokenSession();
			Assertions.assertNotNull(tokenSession);
			Assertions.assertNotNull(stpLogic.getTokenSession(false));
			Assertions.assertNotNull(dao.getSession(stpLogic.splicingKeyTokenSession(token)));
			SaSession byToken = stpLogic.getTokenSessionByToken(token);
			Assertions.assertEquals(tokenSession.getId(), byToken.getId());
		});
	}

	/** isCreate=false 时 getTokenSessionByToken 不存在应返回 null */
	@Test
	void getTokenSessionByToken_withoutCreate_returnsNullWhenAbsent() {
		String token = stpLogic.createLoginSession(30042);
		Assertions.assertNull(stpLogic.getTokenSessionByToken(token, false));
		SaSession created = stpLogic.getTokenSessionByToken(token, true);
		Assertions.assertNotNull(created);
		Assertions.assertEquals(stpLogic.splicingKeyTokenSession(token), created.getId());
	}

	/** searchTokenSessionId 返回的 id 应能通过 getSessionBySessionId 解析 */
	@Test
	void getSessionBySessionId_fromSearchTokenSessionId() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30043);
			stpLogic.getTokenSession();
			stpLogic.login(30044);
			stpLogic.getTokenSession();
			stpLogic.login(30045);
			stpLogic.getTokenSession();
			List<String> sessionIds = stpLogic.searchTokenSessionId("", 0, 10, true);
			Assertions.assertTrue(sessionIds.size() >= 3);
			sessionIds.forEach(sessionId -> {
				SaSession session = stpLogic.getSessionBySessionId(sessionId);
				Assertions.assertNotNull(session);
			});
		});
	}

}

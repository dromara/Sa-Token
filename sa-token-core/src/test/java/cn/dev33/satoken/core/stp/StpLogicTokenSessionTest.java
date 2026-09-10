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
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.SaTokenException;
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

	/** 每个用例开始前准备测试现场 */
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

}

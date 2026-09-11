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
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
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

	/** 每个用例开始前准备测试现场 */
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

	/** 空 loginId 调用 getSessionByLoginId 应抛出 SaTokenException */
	@Test
	void getSessionByLoginId_rejectsEmptyLoginId() {
		Assertions.assertThrows(cn.dev33.satoken.exception.SaTokenException.class,
				() -> stpLogic.getSessionByLoginId("", true));
	}

	/** 空 sessionId 调用 getSessionBySessionId 应抛出 CODE_11072 */
	@Test
	void getSessionBySessionId_emptyId_throws() {
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.getSessionBySessionId("", false, null, null));
		Assertions.assertEquals(SaErrorCode.CODE_11072, ex.getCode());
	}

	/** getSessionBySessionId 的 appendOperation 回调应在创建时执行 */
	@Test
	void getSessionBySessionId_withAppendOperation() {
		String sessionId = stpLogic.splicingKeySession(60015);
		SaSession session = stpLogic.getSessionBySessionId(sessionId, true, 3600L, s -> s.set("init", "yes"));
		Assertions.assertNotNull(session);
		Assertions.assertEquals("yes", session.get("init"));
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

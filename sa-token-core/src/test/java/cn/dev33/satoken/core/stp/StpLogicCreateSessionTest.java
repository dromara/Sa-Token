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
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 无上下文创建会话与手动写入 Token
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicCreateSessionTest {

	private StpLogic stpLogic;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** 无上下文 createLoginSession 应写入 DAO 但不注入当前 Token */
	@Test
	void createLoginSession_withoutContext_doesNotInjectToken() {
		String token = stpLogic.createLoginSession(30021);
		SaTokenDao dao = SaManager.getSaTokenDao();
		Assertions.assertEquals("30021", dao.get(stpLogic.splicingKeyTokenValue(token)));
		SaTokenContextMockUtil.setMockContext(() -> {
			String token2 = stpLogic.createLoginSession(30021, new SaLoginParameter());
			Assertions.assertNull(stpLogic.getTokenValue());
			Assertions.assertEquals("30021", dao.get(stpLogic.splicingKeyTokenValue(token2)));
		});
	}

	/** setTokenValue 应将 Token 注入上下文并恢复登录状态 */
	@Test
	void setTokenValue_injectsTokenIntoContext() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(30022);
			Assertions.assertNull(stpLogic.getTokenValue());
			stpLogic.setTokenValue(token);
			Assertions.assertEquals(token, stpLogic.getTokenValue());
			Assertions.assertTrue(stpLogic.isLogin());
			Assertions.assertEquals("30022", stpLogic.getLoginId());
		});
	}

	/** 带 cookieTimeout 的 setTokenValue 应写入 Cookie */
	@Test
	void setTokenValue_withCookieTimeout_writesCookie() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(30023);
			stpLogic.setTokenValue(token, 10);
			Assertions.assertEquals(token, stpLogic.getTokenValue());
			Assertions.assertNotNull(SaHolder.getResponse());
		});
	}

	/** 登录时指定 Token 应使用自定义 Token 值 */
	@Test
	void login_withPresetToken_usesCustomTokenValue() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30024, new SaLoginParameter().setToken("qwer-qwer-qwer-qwer"));
			Assertions.assertEquals("qwer-qwer-qwer-qwer", stpLogic.getTokenValue());
			stpLogic.logout();
			Assertions.assertNull(stpLogic.getTokenValue());
		});
	}

	/** save/update/delete TokenToIdMapping 应正确维护 DAO 映射 */
	@Test
	void tokenToIdMapping_crud() {
		SaTokenDao dao = SaManager.getSaTokenDao();
		String token = "mapping-token-1";

		stpLogic.saveTokenToIdMapping(token, 90001, 3600);
		Assertions.assertEquals("90001", dao.get(stpLogic.splicingKeyTokenValue(token)));

		stpLogic.updateTokenToIdMapping(token, 90002);
		Assertions.assertEquals("90002", dao.get(stpLogic.splicingKeyTokenValue(token)));

		stpLogic.deleteTokenToIdMapping(token);
		Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(token)));
	}

	/** 空 loginId 更新 Token 映射应抛出 SaTokenException */
	@Test
	void updateTokenToIdMapping_emptyLoginId_throws() {
		Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.updateTokenToIdMapping("any-token", null));
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

	/** 无上下文 getOrCreateLoginSession 应创建并返回 Token */
	@Test
	void getOrCreateLoginSession_withoutContext() {
		String token = stpLogic.getOrCreateLoginSession(40004);
		Assertions.assertNotNull(token);
		Assertions.assertEquals("40004", SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token)));
	}

	/** createTokenValue 应委托策略生成非空 Token */
	@Test
	void createTokenValue_delegatesToStrategy() {
		String token = stpLogic.createTokenValue(60013, "PC", 3600, null);
		Assertions.assertNotNull(token);
		Assertions.assertFalse(token.isEmpty());
	}

}

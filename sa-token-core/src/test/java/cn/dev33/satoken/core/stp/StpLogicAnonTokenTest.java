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

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 匿名 Token-Session
 */
@SaTokenTest
public class StpLogicAnonTokenTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** getAnonTokenSession 应自动创建匿名 Token 与 Token-Session */
	@Test
	void getAnonTokenSession_createsTokenAndSession() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.logout();
			Assertions.assertNull(stpLogic.getTokenValue());

			SaSession anonTokenSession = stpLogic.getAnonTokenSession();
			String token = stpLogic.getTokenValue();
			Assertions.assertNotNull(token);
			Assertions.assertNotNull(anonTokenSession);
		});
	}

	/** 携带预设 Token 登录应保留匿名 Token-Session 中的数据 */
	@Test
	void login_withPresetToken_preservesAnonTokenSession() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.logout();
			Assertions.assertNull(stpLogic.getTokenValue());

			SaSession anonTokenSession = stpLogic.getAnonTokenSession();
			String token = stpLogic.getTokenValue();
			Assertions.assertNotNull(token);
			anonTokenSession.set("code", "123456");

			stpLogic.login(10001, new SaLoginParameter().setToken(token));
			Assertions.assertEquals(token, stpLogic.getTokenValue());

			SaSession tokenSession = stpLogic.getTokenSession();
			Assertions.assertEquals(anonTokenSession.getId(), tokenSession.getId());
			Assertions.assertEquals("123456", tokenSession.get("code"));
		});
	}

}

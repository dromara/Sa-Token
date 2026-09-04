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
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * StpLogic 登录与鉴权状态
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicLoginTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** 登录后 Token、Session、isLogin 及各 loginId 转换应全部正确 */
	@Test
	void login_setsTokenSessionAndLoginState() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			String token = stpLogic.getTokenValue();

			Assertions.assertNotNull(token);
			Assertions.assertEquals(token, stpLogic.getTokenValueNotCut());
			Assertions.assertEquals(token, stpLogic.getTokenValueByLoginId(10001));
			Assertions.assertEquals(token, stpLogic.getTokenValueByLoginId(10001, SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE));

			List<String> tokenList = stpLogic.getTokenValueListByLoginId(10001);
			Assertions.assertEquals(token, tokenList.get(tokenList.size() - 1));

			Assertions.assertTrue(stpLogic.isLogin());
			Assertions.assertTrue(stpLogic.isLogin(10001));
			Assertions.assertDoesNotThrow(() -> stpLogic.checkLogin());
			Assertions.assertEquals("10001", stpLogic.getLoginId());
			Assertions.assertEquals("10001", stpLogic.getLoginIdAsString());
			Assertions.assertEquals(10001L, stpLogic.getLoginIdAsLong());
			Assertions.assertEquals(10001, stpLogic.getLoginIdAsInt());
			Assertions.assertEquals("10001", stpLogic.getLoginIdDefaultNull());
			Assertions.assertEquals(SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE, stpLogic.getLoginDevice());

			SaTokenDao dao = SaManager.getSaTokenDao();
			Assertions.assertEquals("10001", dao.get(stpLogic.splicingKeyTokenValue(token)));
			SaSession session = dao.getSession(stpLogic.splicingKeySession(10001));
			Assertions.assertNotNull(session);
			Assertions.assertTrue(session.getTerminalList().size() >= 1);
		});
	}

	/** 未登录时 isLogin 为 false 且 checkLogin 应抛出 NotLoginException */
	@Test
	void checkLogin_throwsWhenNotLoggedIn() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertFalse(stpLogic.isLogin());
			Assertions.assertNull(stpLogic.getLoginIdDefaultNull());
			Assertions.assertEquals("guest", stpLogic.getLoginId("guest"));
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.checkLogin());
		});
	}

	/** Header 携带 Token 时 getTokenValue 应读取并恢复登录状态 */
	@Test
	void getTokenValue_readsFromHeader() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(10001);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put(stpLogic.getTokenName(), token);

			Assertions.assertEquals(token, stpLogic.getTokenValue());
			Assertions.assertTrue(stpLogic.isLogin());
			Assertions.assertEquals("10001", stpLogic.getLoginId());
		});
	}

	/** 自定义 Token 与设备类型登录后各字段应正确 */
	@Test
	void login_withCustomTokenAndDeviceType() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10002, new SaLoginParameter()
					.setToken("custom-token-value")
					.setDeviceType("PC")
					.setTimeout(3600));

			Assertions.assertEquals("custom-token-value", stpLogic.getTokenValue());
			Assertions.assertEquals("PC", stpLogic.getLoginDevice());
			Assertions.assertTrue(stpLogic.getTokenTimeout() > 0);
		});
	}

}

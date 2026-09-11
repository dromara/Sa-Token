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
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
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

	/** 每个用例开始前准备测试现场 */
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

	/** Token 前缀模式下无 Token 时 getTokenValue 应返回 null */
	@Test
	void getTokenValue_prefixMode_emptyTokenBecomesNull() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenPrefix("Bearer");
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertNull(stpLogic.getTokenValue());
		});
	}

	/** 无 Token 时 getTokenValueNotNull 应抛出 NotLoginException */
	@Test
	void getTokenValueNotNull_throwsWhenMissingToken() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.getTokenValueNotNull());
		});
	}

	/** login 应对空/非法 loginId 抛异常，Map 与 extra 参数应允许 */
	@Test
	void login_checkLoginArgs_rejectsInvalidLoginId() {
		Assertions.assertThrows(SaTokenException.class, () -> stpLogic.login(""));
		Assertions.assertThrows(SaTokenException.class, () -> stpLogic.login(NotLoginException.NOT_TOKEN));
		Assertions.assertThrows(SaTokenException.class, () -> stpLogic.login("user:colon"));
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertDoesNotThrow(() -> stpLogic.login(new HashMap<>(), new SaLoginParameter()));
			Assertions.assertDoesNotThrow(() -> stpLogic.login(70001,
					new SaLoginParameter().setExtra("k", "v").setActiveTimeout(60L)));
		});
	}

	/** Token 被标记 TOKEN_TIMEOUT 时 getLoginId 应抛出对应异常 */
	@Test
	void getLoginId_throwsTokenTimeout() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70002);
			String token = stpLogic.getTokenValue();
			SaManager.getSaTokenDao().set(stpLogic.splicingKeyTokenValue(token),
					NotLoginException.TOKEN_TIMEOUT, SaTokenDao.NEVER_EXPIRE);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.parameterMap.put(stpLogic.getTokenName(), token);

			NotLoginException ex = Assertions.assertThrows(NotLoginException.class, () -> stpLogic.getLoginId());
			Assertions.assertEquals(NotLoginException.TOKEN_TIMEOUT, ex.getType());
		});
	}

	/** defaultValue 为 null 时 getLoginId 应返回原始 loginId */
	@Test
	void getLoginId_withNullDefault_returnsRawLoginId() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70003);
			Object loginId = stpLogic.getLoginId((String) null);
			Assertions.assertEquals("70003", String.valueOf(loginId));
		});
	}

	/** 身份切换期间 getLoginIdDefaultNull 应返回切换后的 loginId */
	@Test
	void getLoginIdDefaultNull_whenSwitch_returnsSwitchId() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70004);
			stpLogic.switchTo(70005);
			Assertions.assertEquals(70005, stpLogic.getLoginIdDefaultNull());
		});
	}

	/** Token 冻结时 getLoginIdDefaultNull 应返回 null */
	@Test
	void getLoginIdDefaultNull_whenFrozen_returnsNull() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70006);
			String token = stpLogic.getTokenValue();
			long oldTime = System.currentTimeMillis() - 60_000;
			SaManager.getSaTokenDao().set(stpLogic.splicingKeyLastActiveTime(token),
					String.valueOf(oldTime), 3600);
			Assertions.assertNull(stpLogic.getLoginIdDefaultNull());
		});
	}

	/** Token 冻结时 getLoginIdByToken 应返回 null */
	@Test
	void getLoginIdByToken_returnsNullWhenFrozen() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70007);
			String token = stpLogic.getTokenValue();
			long oldTime = System.currentTimeMillis() - 60_000;
			SaManager.getSaTokenDao().set(stpLogic.splicingKeyLastActiveTime(token),
					String.valueOf(oldTime), 3600);
			Assertions.assertNull(stpLogic.getLoginIdByToken(token));
		});
	}

	/** getLoginId 应支持将字符串 loginId 转换为 Long 默认值类型 */
	@Test
	void getLoginId_convertsToDefaultValueType() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login("70022");
			Assertions.assertEquals(70022L, stpLogic.getLoginId(0L));
		});
	}

	/** 空 Token 时 getLoginIdByTokenNotThinkFreeze 应返回 null */
	@Test
	void getLoginIdByTokenNotThinkFreeze_emptyToken_returnsNull() {
		Assertions.assertNull(stpLogic.getLoginIdByTokenNotThinkFreeze(null));
		Assertions.assertNull(stpLogic.getLoginIdByTokenNotThinkFreeze(""));
	}

	/** allowLoginIdColon 开关应控制 loginId 中冒号是否允许 */
	@Test
	void allowLoginIdColon_permitsAndRejectsColon() {
		SaTokenConfig config = SaManager.getConfig();
		config.setAllowLoginIdColon(false);
		SaManager.setConfig(config);
		Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.createLoginSession("tenant:user"));

		config.setAllowLoginIdColon(true);
		SaManager.setConfig(config);
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login("tenant:user");
			Assertions.assertEquals("tenant:user", stpLogic.getLoginIdAsString());
			Assertions.assertEquals("tenant:user",
					SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(stpLogic.getTokenValue())));
			Assertions.assertNotNull(SaManager.getSaTokenDao().getSession(stpLogic.splicingKeySession("tenant:user")));
		});
	}

	/** isValidLoginId 应拒绝 null/空/特殊标记 loginId */
	@Test
	void isValidLoginId_rejectsAbnormalMarkers() {
		Assertions.assertFalse(stpLogic.isValidLoginId(null));
		Assertions.assertFalse(stpLogic.isValidLoginId(""));
		Assertions.assertFalse(stpLogic.isValidLoginId(NotLoginException.KICK_OUT));
		Assertions.assertTrue(stpLogic.isValidLoginId(60014));
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
			Assertions.assertTrue(timeout <= 60 && timeout >= 55);
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

}

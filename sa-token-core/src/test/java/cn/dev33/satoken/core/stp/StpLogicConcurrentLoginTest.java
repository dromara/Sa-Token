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
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

/**
 * StpLogic 并发登录与 Token 信息查询
 */
@SaTokenTest
public class StpLogicConcurrentLoginTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** 超出 maxLoginCount 时应移除最早终端并保留最新两个 */
	@Test
	void maxLoginCount_overflowRemovesOldestTerminal() {
		SaLoginParameter param = new SaLoginParameter()
				.setIsConcurrent(true)
				.setIsShare(false)
				.setMaxLoginCount(2);
		String tokenPc = stpLogic.createLoginSession(30051, param.setDeviceType("PC"));
		String tokenApp = stpLogic.createLoginSession(30051, param.setDeviceType("APP"));
		String tokenMini = stpLogic.createLoginSession(30051, param.setDeviceType("MINI"));
		SaTokenDao dao = SaManager.getSaTokenDao();
		Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(tokenPc)));
		Assertions.assertEquals("30051", dao.get(stpLogic.splicingKeyTokenValue(tokenApp)));
		Assertions.assertEquals("30051", dao.get(stpLogic.splicingKeyTokenValue(tokenMini)));
		List<SaTerminalInfo> terminals = stpLogic.getTerminalListByLoginId(30051);
		Assertions.assertEquals(2, terminals.size());
	}

	/** 未登录或注销后 isLogin(loginId) 应返回 false */
	@Test
	void isLoginByLoginId_falseWhenNeverLoggedInOrAfterLogout() {
		Assertions.assertFalse(stpLogic.isLogin(30052));
		String token = stpLogic.createLoginSession(30052);
		Assertions.assertTrue(stpLogic.isLogin(30052));
		stpLogic.logoutByTokenValue(token);
		Assertions.assertFalse(stpLogic.isLogin(30052));
	}

	/** 未登录时 getLoginId 应返回默认值，getLoginIdDefaultNull 应返回 null */
	@Test
	void getLoginId_returnsDefaultWhenNotLoggedIn() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertEquals("default", stpLogic.getLoginId("default"));
			Assertions.assertNull(stpLogic.getLoginIdDefaultNull());
		});
	}

	/** getTokenInfo 应在登录前后正确反映登录状态与各超时字段 */
	@Test
	void getTokenInfo_reflectsLoginState() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaTokenInfo before = stpLogic.getTokenInfo();
			Assertions.assertFalse(before.isLogin);
			Assertions.assertNull(before.loginId);
			Assertions.assertEquals("login", before.loginType);
			Assertions.assertEquals(stpLogic.getTokenName(), before.tokenName);
			stpLogic.login(30053, new SaLoginParameter().setDeviceType("PC").setTimeout(3600));
			SaTokenInfo after = stpLogic.getTokenInfo();
			Assertions.assertTrue(after.isLogin);
			Assertions.assertEquals("30053", after.loginId);
			Assertions.assertEquals("PC", after.loginDeviceType);
			Assertions.assertNotNull(after.tokenValue);
			Assertions.assertTrue(after.tokenTimeout > 0);
			Assertions.assertTrue(after.sessionTimeout > 0);
		});
	}

	/** 核心模式下 getExtra 应抛出 ApiDisabledException */
	@Test
	void getExtra_throwsApiDisabledInCoreMode() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30054);
			Assertions.assertThrows(ApiDisabledException.class, () -> stpLogic.getExtra("key"));
			Assertions.assertThrows(ApiDisabledException.class, () -> stpLogic.getExtra(stpLogic.getTokenValue(), "key"));
		});
	}

	/** 登录参数的 terminalExtra 应写入终端扩展数据 */
	@Test
	void setExtra_onLoginParameter_storesTerminalExtraData() {
		SaLoginParameter param = new SaLoginParameter()
				.setDeviceType("PC")
				.setTerminalExtra("client", "web")
				.setExtra("role", "admin");
		String token = stpLogic.createLoginSession(30055, param);
		Assertions.assertEquals("admin", param.getExtra("role"));
		List<SaTerminalInfo> terminals = stpLogic.getTerminalListByLoginId(30055, "PC");
		Assertions.assertEquals(1, terminals.size());
		Assertions.assertEquals("web", terminals.get(0).getExtra("client"));
		Assertions.assertEquals(token, terminals.get(0).getTokenValue());
	}

}

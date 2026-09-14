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
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedLoginExitMode;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedRange;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

/**
 * StpLogic 并发登录与 Token 信息查询
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicConcurrentLoginTest {

	private StpLogic stpLogic;

	/** 每个用例开始前准备测试现场 */
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

	/** 无 Session 时 logoutByMaxLoginCount 应安全返回 */
	@Test
	void logoutByMaxLoginCount_noSession_returnsEarly() {
		Assertions.assertDoesNotThrow(() ->
				stpLogic.logoutByMaxLoginCount(79998, null, null, 1, SaLogoutMode.LOGOUT));
	}

	/** 超出 maxLoginCount 时应清除被挤掉 Token 的活跃记录 */
	@Test
	void logoutByMaxLoginCount_clearsLastActiveWhenEnabled() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(true);
		config.setIsShare(false);
		config.setMaxLoginCount(1);
		config.setActiveTimeout(300);
		SaManager.setConfig(config);

		String token1 = stpLogic.createLoginSession(70029, new SaLoginParameter().setDeviceType("PC"));
		String token2 = stpLogic.createLoginSession(70029, new SaLoginParameter().setDeviceType("APP"));
		SaTokenDao dao = SaManager.getSaTokenDao();
		Assertions.assertNotNull(dao.get(stpLogic.splicingKeyLastActiveTime(token2)));
		Assertions.assertNull(dao.get(stpLogic.splicingKeyLastActiveTime(token1)));
	}

	/** isShare=true 时同设备类型重复登录应复用同一 Token */
	@Test
	void isShare_reusesTokenOnSameDeviceType() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(true);
		config.setIsShare(true);
		SaManager.setConfig(config);

		SaLoginParameter param = new SaLoginParameter().setDeviceType("PC");
		String token1 = stpLogic.createLoginSession(50001, param);
		String token2 = stpLogic.createLoginSession(50001, param);
		Assertions.assertEquals(token1, token2);
		Assertions.assertEquals("50001", SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token1)));
	}

	/** isConcurrent=false 时二次登录应将旧 Token 标记为 BE_REPLACED */
	@Test
	void isConcurrentFalse_replacesPreviousSession() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(false);
		config.setReplacedLoginExitMode(SaReplacedLoginExitMode.OLD_DEVICE);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50002, "PC");
			String oldToken = stpLogic.getTokenValue();
			stpLogic.login(50002, "PC");
			String newToken = stpLogic.getTokenValue();
			Assertions.assertNotEquals(oldToken, newToken);
			Assertions.assertEquals(NotLoginException.BE_REPLACED,
					SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(oldToken)));
			Assertions.assertTrue(stpLogic.isLogin());
		});
	}

	/** NEW_DEVICE 模式下新设备登录应拒绝并保留原 Token */
	@Test
	void replacedLoginExitMode_newDevice_rejectsSecondLogin() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(false);
		config.setReplacedLoginExitMode(SaReplacedLoginExitMode.NEW_DEVICE);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50003, "PC");
			String firstToken = stpLogic.getTokenValue();
			Assertions.assertThrows(SaTokenException.class, () -> stpLogic.login(50003, "APP"));
			Assertions.assertEquals(firstToken, stpLogic.getTokenValue());
			Assertions.assertTrue(stpLogic.isLogin(50003));
		});
	}

	/** OLD_DEVICE+ALL_DEVICE_TYPE 模式下新登录应顶掉所有旧 Token */
	@Test
	void replacedLoginExitMode_oldDevice_allDeviceTypeReplacesEverywhere() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(false);
		config.setReplacedLoginExitMode(SaReplacedLoginExitMode.OLD_DEVICE);
		config.setReplacedRange(SaReplacedRange.ALL_DEVICE_TYPE);
		SaManager.setConfig(config);

		String pcToken = stpLogic.createLoginSession(50004, new SaLoginParameter().setDeviceType("PC"));
		String appToken = stpLogic.createLoginSession(50004, new SaLoginParameter().setDeviceType("APP"));
		SaTokenDao dao = SaManager.getSaTokenDao();

		stpLogic.createLoginSession(50004, new SaLoginParameter().setDeviceType("MINI"));
		Assertions.assertEquals(NotLoginException.BE_REPLACED, dao.get(stpLogic.splicingKeyTokenValue(pcToken)));
		Assertions.assertEquals(NotLoginException.BE_REPLACED, dao.get(stpLogic.splicingKeyTokenValue(appToken)));
	}

	/** overflowLogoutMode=KICKOUT 时超出 maxLoginCount 应将旧 Token 标记 KICK_OUT */
	@Test
	void maxLoginCount_overflowLogoutMode_kickout() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(true);
		config.setIsShare(false);
		config.setMaxLoginCount(1);
		config.setOverflowLogoutMode(SaLogoutMode.KICKOUT);
		SaManager.setConfig(config);

		String token1 = stpLogic.createLoginSession(50006, new SaLoginParameter().setDeviceType("PC"));
		stpLogic.createLoginSession(50006, new SaLoginParameter().setDeviceType("APP"));
		Assertions.assertEquals(NotLoginException.KICK_OUT,
				SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token1)));
	}

	/** overflowLogoutMode=REPLACED 时超出 maxLoginCount 应将旧 Token 标记 BE_REPLACED */
	@Test
	void maxLoginCount_overflowLogoutMode_replaced() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(true);
		config.setIsShare(false);
		config.setMaxLoginCount(1);
		config.setOverflowLogoutMode(SaLogoutMode.REPLACED);
		SaManager.setConfig(config);

		String token1 = stpLogic.createLoginSession(50007, new SaLoginParameter().setDeviceType("PC"));
		stpLogic.createLoginSession(50007, new SaLoginParameter().setDeviceType("APP"));
		Assertions.assertEquals(NotLoginException.BE_REPLACED,
				SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token1)));
	}

}

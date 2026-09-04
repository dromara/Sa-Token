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
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutRange;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedLoginExitMode;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedRange;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * StpLogic 深度路径覆盖：共享 token、单点登录、溢出注销、前缀模式、活跃度等
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicDeepTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	private void config(SaTokenConfig config) {
		SaManager.setConfig(config);
	}

	/** isShare=true 时同设备类型重复登录应复用同一 Token */
	@Test
	void isShare_reusesTokenOnSameDeviceType() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(true);
		config.setIsShare(true);
		config(config);

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
		config(config);

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
		config(config);

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
		config(config);

		String pcToken = stpLogic.createLoginSession(50004, new SaLoginParameter().setDeviceType("PC"));
		String appToken = stpLogic.createLoginSession(50004, new SaLoginParameter().setDeviceType("APP"));
		SaTokenDao dao = SaManager.getSaTokenDao();

		stpLogic.createLoginSession(50004, new SaLoginParameter().setDeviceType("MINI"));
		Assertions.assertEquals(NotLoginException.BE_REPLACED, dao.get(stpLogic.splicingKeyTokenValue(pcToken)));
		Assertions.assertEquals(NotLoginException.BE_REPLACED, dao.get(stpLogic.splicingKeyTokenValue(appToken)));
	}

	/** overflowLogoutMode=LOGOUT 时超出 maxLoginCount 应直接注销旧 Token */
	@Test
	void maxLoginCount_overflowLogoutMode_logout() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(true);
		config.setIsShare(false);
		config.setMaxLoginCount(1);
		config.setOverflowLogoutMode(SaLogoutMode.LOGOUT);
		config(config);

		String token1 = stpLogic.createLoginSession(50005, new SaLoginParameter().setDeviceType("PC"));
		String token2 = stpLogic.createLoginSession(50005, new SaLoginParameter().setDeviceType("APP"));
		SaTokenDao dao = SaManager.getSaTokenDao();
		Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(token1)));
		Assertions.assertEquals("50005", dao.get(stpLogic.splicingKeyTokenValue(token2)));
	}

	/** overflowLogoutMode=KICKOUT 时超出 maxLoginCount 应将旧 Token 标记 KICK_OUT */
	@Test
	void maxLoginCount_overflowLogoutMode_kickout() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(true);
		config.setIsShare(false);
		config.setMaxLoginCount(1);
		config.setOverflowLogoutMode(SaLogoutMode.KICKOUT);
		config(config);

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
		config(config);

		String token1 = stpLogic.createLoginSession(50007, new SaLoginParameter().setDeviceType("PC"));
		stpLogic.createLoginSession(50007, new SaLoginParameter().setDeviceType("APP"));
		Assertions.assertEquals(NotLoginException.BE_REPLACED,
				SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token1)));
	}

	/** Bearer 前缀模式下 getTokenValueNotCut 应含前缀而 getTokenValue 返回裸 Token */
	@Test
	void tokenPrefix_bearer_getTokenValueAndNotCut() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenPrefix("Bearer");
		config(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50008);
			String rawToken = stpLogic.getTokenValue();
			String prefixed = stpLogic.getTokenValueNotCut();
			Assertions.assertTrue(prefixed.startsWith("Bearer" + SaTokenConsts.TOKEN_CONNECTOR_CHAT));
			Assertions.assertEquals(rawToken, stpLogic.getTokenValue());
			Assertions.assertEquals(rawToken, stpLogic.getTokenValue(false));
		});
	}

	/** Header 无前缀且 isCut=false 时 getTokenValue(true) 应抛出 NO_PREFIX */
	@Test
	void getTokenValue_noPrefixThrowException() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenPrefix("Bearer");
		config(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(50009);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.headerMap.put(stpLogic.getTokenName(), token);

			Assertions.assertNull(stpLogic.getTokenValue(false));
			try {
				stpLogic.getTokenValue(true);
				Assertions.fail("expected NO_PREFIX");
			} catch (NotLoginException e) {
				Assertions.assertEquals(NotLoginException.NO_PREFIX, e.getType());
			}
		});
	}

	/** renewTimeout 后按 loginId 与 Token 字符串查询的超时应同步更新 */
	@Test
	void renewTimeout_reflectedByLoginIdAndTokenString() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50010, 100);
			String token = stpLogic.getTokenValue();
			stpLogic.renewTimeout(400);
			long byLoginId = stpLogic.getTokenTimeoutByLoginId(50010);
			long byToken = stpLogic.getTokenTimeout(token);
			Assertions.assertTrue(byLoginId <= 400 && byLoginId >= 399);
			Assertions.assertTrue(byToken <= 400 && byToken >= 399);
			Assertions.assertNotNull(SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** TOKEN 范围仅注销当前 Token，ACCOUNT 范围应清除全部 Session */
	@Test
	void logoutRange_token_vs_account() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String pcToken = stpLogic.createLoginSession(50011, new SaLoginParameter().setDeviceType("PC"));
			String appToken = stpLogic.createLoginSession(50011, new SaLoginParameter().setDeviceType("APP"));
			SaTokenDao dao = SaManager.getSaTokenDao();

			stpLogic.setTokenValue(pcToken);
			stpLogic.logout(new SaLogoutParameter().setRange(SaLogoutRange.TOKEN));
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(pcToken)));
			Assertions.assertEquals("50011", dao.get(stpLogic.splicingKeyTokenValue(appToken)));

			stpLogic.setTokenValue(appToken);
			stpLogic.logout(new SaLogoutParameter().setRange(SaLogoutRange.ACCOUNT));
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(appToken)));
			Assertions.assertNull(dao.getSession(stpLogic.splicingKeySession(50011)));
		});
	}

	/** rightNowCreateTokenSession=true 时登录应立即创建 Token-Session */
	@Test
	void rightNowCreateTokenSession_createsOnLogin() {
		SaTokenConfig config = SaManager.getConfig();
		config.setRightNowCreateTokenSession(true);
		config(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50012);
			String token = stpLogic.getTokenValue();
			SaSession tokenSession = SaManager.getSaTokenDao().getSession(stpLogic.splicingKeyTokenSession(token));
			Assertions.assertNotNull(tokenSession);
			Assertions.assertEquals(stpLogic.splicingKeyTokenSession(token), tokenSession.getId());
		});
	}

	/** 动态 activeTimeout 下登录参数 activeTimeout 应写入 Token 级配置 */
	@Test
	void dynamicActiveTimeout_loginParameterActiveTimeout() {
		SaTokenConfig config = SaManager.getConfig();
		config.setDynamicActiveTimeout(true);
		config.setActiveTimeout(180);
		config(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50013, new SaLoginParameter().setActiveTimeout(60L));
			String token = stpLogic.getTokenValue();
			Assertions.assertEquals(60L, stpLogic.getTokenUseActiveTimeout(token));
			Assertions.assertEquals(60L, stpLogic.getTokenUseActiveTimeoutOrGlobalConfig(token));
			String lastActiveKey = stpLogic.splicingKeyLastActiveTime(token);
			Assertions.assertNotNull(SaManager.getSaTokenDao().get(lastActiveKey));
		});
	}

	/** getTerminalInfo 与 getLoginDeviceByToken 应返回设备信息与扩展数据 */
	@Test
	void getTerminalInfo_and_getLoginDeviceByToken() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50014, new SaLoginParameter()
					.setDeviceType("PC")
					.setDeviceId("device-001")
					.setTerminalExtra("channel", "web"));
			String token = stpLogic.getTokenValue();

			SaTerminalInfo terminal = stpLogic.getTerminalInfo();
			Assertions.assertNotNull(terminal);
			Assertions.assertEquals("PC", terminal.getDeviceType());
			Assertions.assertEquals("device-001", terminal.getDeviceId());
			Assertions.assertEquals("web", terminal.getExtra("channel"));

			SaTerminalInfo byToken = stpLogic.getTerminalInfoByToken(token);
			Assertions.assertEquals(token, byToken.getTokenValue());
			Assertions.assertEquals("PC", stpLogic.getLoginDeviceByToken(token));
			Assertions.assertEquals("device-001", stpLogic.getLoginDeviceIdByToken(token));
		});
	}

	/** 活跃超时后 checkActiveTimeout 应抛出 TOKEN_FREEZE 且 isFreeze 为 true */
	@Test
	void checkActiveTimeout_throwsTokenFreeze() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		config(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50015);
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();
			long oldTime = System.currentTimeMillis() - 60_000;
			dao.set(stpLogic.splicingKeyLastActiveTime(token), String.valueOf(oldTime), 3600);

			try {
				stpLogic.checkActiveTimeout(token);
				Assertions.fail("expected TOKEN_FREEZE");
			} catch (NotLoginException e) {
				Assertions.assertEquals(NotLoginException.TOKEN_FREEZE, e.getType());
			}
			Assertions.assertTrue(stpLogic.isFreeze(token));
			Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getTokenActiveTimeoutByToken(token));
		});
	}

	/** allowLoginIdColon 开关应控制 loginId 中冒号是否允许 */
	@Test
	void allowLoginIdColon_permitsAndRejectsColon() {
		SaTokenConfig config = SaManager.getConfig();
		config.setAllowLoginIdColon(false);
		config(config);
		Assertions.assertThrows(SaTokenException.class,
				() -> stpLogic.createLoginSession("tenant:user"));

		config.setAllowLoginIdColon(true);
		config(config);
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login("tenant:user");
			Assertions.assertEquals("tenant:user", stpLogic.getLoginIdAsString());
			Assertions.assertEquals("tenant:user",
					SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(stpLogic.getTokenValue())));
			Assertions.assertNotNull(SaManager.getSaTokenDao().getSession(stpLogic.splicingKeySession("tenant:user")));
		});
	}

	/** forEachTerminalList 应遍历终端，工厂方法应复制全局配置 */
	@Test
	void forEachTerminalList_and_factoryMethods() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsConcurrent(true);
		config(config);

		stpLogic.createLoginSession(50016, new SaLoginParameter().setDeviceType("PC"));
		stpLogic.createLoginSession(50016, new SaLoginParameter().setDeviceType("APP"));

		AtomicInteger count = new AtomicInteger();
		stpLogic.forEachTerminalList(50016, (session, terminal) -> {
			Assertions.assertNotNull(session.getId());
			Assertions.assertNotNull(terminal.getTokenValue());
			count.incrementAndGet();
		});
		Assertions.assertEquals(2, count.get());

		SaLoginParameter loginParam = stpLogic.createSaLoginParameter();
		SaLogoutParameter logoutParam = stpLogic.createSaLogoutParameter();
		Assertions.assertEquals(config.getIsConcurrent(), loginParam.getIsConcurrent());
		Assertions.assertEquals(config.getLogoutRange(), logoutParam.getRange());
		Assertions.assertTrue(stpLogic.isSupportShareToken() == config.getIsShare());
		Assertions.assertTrue(stpLogic.getConfigOfCookieTimeout() > 0);
	}

	/** Token 冻结时 ACCOUNT 范围 logout 不应清除 Token 映射 */
	@Test
	void logout_skipsFrozenAccountRangeLogout() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		config(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(50017);
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();
			long oldTime = System.currentTimeMillis() - 60_000;
			dao.set(stpLogic.splicingKeyLastActiveTime(token), String.valueOf(oldTime), 3600);

			stpLogic.logout(new SaLogoutParameter().setRange(SaLogoutRange.ACCOUNT));
			Assertions.assertEquals("50017", dao.get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** Cookie 自动补前缀模式下 getTokenValue 应从 Cookie 读取裸 Token */
	@Test
	void getTokenValue_readsFromCookieWithAutoFillPrefix() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTokenPrefix("Bearer");
		config.setIsReadCookie(true);
		config.setIsReadHeader(false);
		config.setIsReadBody(false);
		config.setCookieAutoFillPrefix(true);
		config(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			String token = stpLogic.createLoginSession(50018);
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.cookieMap.put(stpLogic.getTokenName(), token);

			Assertions.assertEquals(token, stpLogic.getTokenValue());
			Assertions.assertEquals("Bearer" + SaTokenConsts.TOKEN_CONNECTOR_CHAT + token,
					stpLogic.getTokenValueNotCut());
		});
	}

}

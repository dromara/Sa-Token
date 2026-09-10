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

/**
 * StpLogic Token 最低活跃频率
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicActiveTimeoutTest {

	private StpLogic stpLogic;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(180);
		SaManager.setConfig(config);
	}

	/** 更新活跃时间后 getTokenActiveTimeout 与 checkActiveTimeout 应正常 */
	@Test
	void updateLastActiveToNow_getTokenActiveTimeout_checkActiveTimeout() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			Assertions.assertNotNull(stpLogic.getTokenValue());

			stpLogic.updateLastActiveToNow();
			long activeTimeout = stpLogic.getTokenActiveTimeout();
			Assertions.assertTrue(activeTimeout <= 180 && activeTimeout >= 175);

			Assertions.assertDoesNotThrow(() -> stpLogic.checkActiveTimeout());
		});
	}

	/** 空 Token 时 getTokenLastActiveTime 应返回 NOT_VALUE_EXPIRE */
	@Test
	void getTokenLastActiveTime_handlesEmptyAndMissing() {
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getTokenLastActiveTime(null));
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getTokenLastActiveTime(""));

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70010);
			Assertions.assertTrue(stpLogic.getTokenLastActiveTime() >= 0
					|| stpLogic.getTokenLastActiveTime() == SaTokenDao.NOT_VALUE_EXPIRE);
		});
	}

	/** activeTimeout=-1 时 getTokenActiveTimeoutByToken 应返回 NEVER_EXPIRE */
	@Test
	void getTokenActiveTimeout_whenCheckDisabled_returnsNeverExpire() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(-1);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70011);
			Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE,
					stpLogic.getTokenActiveTimeoutByToken(stpLogic.getTokenValue()));
		});
	}

	/** Token 冻结时 getTokenActiveTimeoutByToken 应返回 NOT_VALUE_EXPIRE */
	@Test
	void getTokenActiveTimeout_frozenToken_returnsNotValueExpire() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70023);
			String token = stpLogic.getTokenValue();
			long oldTime = System.currentTimeMillis() - 60_000;
			SaManager.getSaTokenDao().set(stpLogic.splicingKeyLastActiveTime(token),
					String.valueOf(oldTime), 3600);
			Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE,
					stpLogic.getTokenActiveTimeoutByToken(token));
		});
	}

	/** 缺少最后活跃记录时 getTokenActiveTimeoutByToken 应返回 NOT_VALUE_EXPIRE */
	@Test
	void getTokenActiveTimeout_missingLastActive_returnsNotValueExpire() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70024);
			String token = stpLogic.getTokenValue();
			SaManager.getSaTokenDao().delete(stpLogic.splicingKeyLastActiveTime(token));
			Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE,
					stpLogic.getTokenActiveTimeoutByToken(token));
		});
	}

	/** Token 活跃时 getTokenActiveTimeout 应返回剩余秒数 */
	@Test
	void getTokenActiveTimeout_returnsRemainingSecondsWhenActive() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(300);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70030);
			long remaining = stpLogic.getTokenActiveTimeout();
			Assertions.assertTrue(remaining > 0 && remaining <= 300);
		});
	}

	/** 开启读 Cookie 时 renewTimeout 应正常更新 Token 超时 */
	@Test
	void renewTimeout_updatesCookieWhenReadCookieEnabled() {
		SaTokenConfig config = SaManager.getConfig();
		config.setIsReadCookie(true);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70012);
			Assertions.assertDoesNotThrow(() -> stpLogic.renewTimeout(7200));
			Assertions.assertTrue(stpLogic.getTokenTimeout() > 0);
		});
	}

	/** Session 中无对应终端时 renewTimeout 应抛出 SaTokenException */
	@Test
	void renewTimeout_throwsWhenTerminalMissing() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70013);
			String token = stpLogic.getTokenValue();
			SaSession session = stpLogic.getSessionByLoginId(70013);
			session.removeTerminal(token);

			Assertions.assertThrows(SaTokenException.class,
					() -> stpLogic.renewTimeout(token, 3600));
		});
	}

	/** 无效 Token 调用 renewTimeout 应不抛异常 */
	@Test
	void renewTimeout_invalidToken_isNoOp() {
		Assertions.assertDoesNotThrow(() -> stpLogic.renewTimeout("ghost-token", 3600));
	}

	/** renewTimeout 应同步更新 Token-Session 超时与活跃记录 */
	@Test
	void renewTimeout_updatesTokenSessionAndActiveRecord() {
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(300);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70025);
			String token = stpLogic.getTokenValue();
			stpLogic.getTokenSession();
			stpLogic.renewTimeout(token, 7200);
			Assertions.assertTrue(stpLogic.getTokenSessionTimeoutByTokenValue(token) > 0);
			Assertions.assertNotNull(SaManager.getSaTokenDao().get(stpLogic.splicingKeyLastActiveTime(token)));
		});
	}

	/** Account Session 不存在时 renewTimeout 应抛出 SaTokenException */
	@Test
	void renewTimeout_throwsWhenAccountSessionMissing() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70031);
			String token = stpLogic.getTokenValue();
			SaManager.getSaTokenDao().delete(stpLogic.splicingKeySession(70031));
			Assertions.assertThrows(SaTokenException.class, () -> stpLogic.renewTimeout(token, 3600));
		});
	}

}

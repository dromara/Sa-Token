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
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic Token 与 Session 过期时间
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicTimeoutTest {

	private StpLogic stpLogic;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** 指定 timeout 登录后 renew 与各 timeout 查询接口应返回正确剩余时间 */
	@Test
	void loginWithTimeout_renewAndQueryTimeouts() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30011, 100);
			long timeout = stpLogic.getTokenTimeout();
			Assertions.assertTrue(timeout <= 100 && timeout >= 95);
			stpLogic.renewTimeout(200);
			timeout = stpLogic.getTokenTimeout();
			Assertions.assertTrue(timeout <= 200 && timeout >= 195);
			String token = stpLogic.getTokenValue();
			stpLogic.renewTimeout(token, 300);
			timeout = stpLogic.getTokenTimeout();
			Assertions.assertTrue(timeout <= 300 && timeout >= 295);
			timeout = stpLogic.getSessionTimeout();
			Assertions.assertTrue(timeout >= 295);
			stpLogic.getTokenSession();
			timeout = stpLogic.getTokenSessionTimeout();
			Assertions.assertTrue(timeout >= 295);
			timeout = stpLogic.getTokenTimeoutByLoginId(30011);
			Assertions.assertTrue(timeout <= 300 && timeout >= 295);
			timeout = stpLogic.getSessionTimeoutByLoginId(30011);
			Assertions.assertTrue(timeout >= 295);
			timeout = stpLogic.getTokenSessionTimeoutByTokenValue(token);
			Assertions.assertTrue(timeout >= 295);
		});
	}

	/** 注销后 getTokenTimeout 应返回 NOT_VALUE_EXPIRE */
	@Test
	void logout_returnsNotValueExpireForTokenTimeout() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30012, 100);
			Assertions.assertTrue(stpLogic.getTokenTimeout() > 0);
			stpLogic.logout();
			long timeout = stpLogic.getTokenTimeout();
			Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, timeout);
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
			Assertions.assertTrue(byLoginId <= 400 && byLoginId >= 395);
			Assertions.assertTrue(byToken <= 400 && byToken >= 395);
			Assertions.assertNotNull(SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** renewTimeout(NEVER_EXPIRE) 后 getTokenTimeout 应返回 NEVER_EXPIRE */
	@Test
	void renewTimeout_neverExpire_rewritesCookieWithIntMax() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90005, 100);
			stpLogic.renewTimeout(SaTokenDao.NEVER_EXPIRE);
			Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, stpLogic.getTokenTimeout());
		});
	}

	/** 未登录时 getSessionTimeout 应返回 NOT_VALUE_EXPIRE */
	@Test
	void getSessionTimeout_whenNotLogin() {
		SaTokenContextMockUtil.setMockContext(() -> {
			long timeout = stpLogic.getSessionTimeout();
			Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, timeout);
		});
	}

	/** 重新登录更长 timeout 时 Token 剩余时间应延长 */
	@Test
	void updateMinTimeout_extendsOnReLogin() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(60004, new SaLoginParameter().setTimeout(3600));
			long firstTimeout = stpLogic.getTokenTimeout();

			stpLogic.login(60004, new SaLoginParameter().setTimeout(7200));
			long secondTimeout = stpLogic.getTokenTimeout();
			Assertions.assertTrue(secondTimeout >= firstTimeout);
		});
	}

}

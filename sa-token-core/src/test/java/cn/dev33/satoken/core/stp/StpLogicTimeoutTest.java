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
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpLogic;
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
			Assertions.assertTrue(timeout <= 100 && timeout >= 99);
			stpLogic.renewTimeout(200);
			timeout = stpLogic.getTokenTimeout();
			Assertions.assertTrue(timeout <= 200 && timeout >= 199);
			String token = stpLogic.getTokenValue();
			stpLogic.renewTimeout(token, 300);
			timeout = stpLogic.getTokenTimeout();
			Assertions.assertTrue(timeout <= 300 && timeout >= 299);
			timeout = stpLogic.getSessionTimeout();
			Assertions.assertTrue(timeout >= 299);
			stpLogic.getTokenSession();
			timeout = stpLogic.getTokenSessionTimeout();
			Assertions.assertTrue(timeout >= 299);
			timeout = stpLogic.getTokenTimeoutByLoginId(30011);
			Assertions.assertTrue(timeout <= 300 && timeout >= 299);
			timeout = stpLogic.getSessionTimeoutByLoginId(30011);
			Assertions.assertTrue(timeout >= 299);
			timeout = stpLogic.getTokenSessionTimeoutByTokenValue(token);
			Assertions.assertTrue(timeout >= 299);
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

}

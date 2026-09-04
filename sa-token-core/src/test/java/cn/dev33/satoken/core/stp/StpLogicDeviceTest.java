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
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 指定设备登录与注销
 */
@SaTokenTest
public class StpLogicDeviceTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** 指定设备类型登录后 getLoginDeviceType 应返回 PC */
	@Test
	void login_withDeviceType_recordsPcDevice() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30001, "PC");
			Assertions.assertEquals("PC", stpLogic.getLoginDeviceType());
			Assertions.assertEquals("PC", stpLogic.getLoginDevice());
		});
	}

	/** 注销其他设备类型不应影响当前设备登录状态 */
	@Test
	void logoutByOtherDevice_doesNotAffectCurrentDevice() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30002, "PC");
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();
			stpLogic.logout(30002, "APP");
			Assertions.assertTrue(stpLogic.isLogin());
			Assertions.assertEquals("30002", dao.get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** 按设备 kickout 应将 Token 标记为 KICK_OUT */
	@Test
	void kickoutByDevice_marksTokenAsKickOut() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30003, "PC");
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();
			stpLogic.kickout(30003, "PC");
			Assertions.assertFalse(stpLogic.isLogin());
			Assertions.assertEquals(NotLoginException.KICK_OUT, dao.get(stpLogic.splicingKeyTokenValue(token)));
			try {
				stpLogic.checkLogin();
			} catch (NotLoginException e) {
				Assertions.assertEquals(NotLoginException.KICK_OUT, e.getType());
			}
		});
	}

	/** 按设备 replaced 应将 Token 标记为 BE_REPLACED */
	@Test
	void replacedByDevice_marksTokenAsBeReplaced() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(30004, "PC");
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();
			stpLogic.replaced(30004, "PC");
			Assertions.assertFalse(stpLogic.isLogin());
			try {
				stpLogic.checkLogin();
			} catch (NotLoginException e) {
				Assertions.assertEquals(NotLoginException.BE_REPLACED, e.getType());
			}
			Assertions.assertEquals(NotLoginException.BE_REPLACED, dao.get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

	/** 按设备 logout 应仅清除匹配终端的 Token 映射 */
	@Test
	void logoutByDevice_clearsOnlyMatchingTerminal() {
		SaTokenContextMockUtil.setMockContext(() -> {
			String pcToken = stpLogic.createLoginSession(30005, new SaLoginParameter().setDeviceType("PC"));
			String appToken = stpLogic.createLoginSession(30005, new SaLoginParameter().setDeviceType("APP"));
			SaTokenDao dao = SaManager.getSaTokenDao();
			stpLogic.logout(30005, "PC");
			Assertions.assertNull(dao.get(stpLogic.splicingKeyTokenValue(pcToken)));
			Assertions.assertEquals("30005", dao.get(stpLogic.splicingKeyTokenValue(appToken)));
			Assertions.assertTrue(stpLogic.isLogin(30005));
		});
	}

}

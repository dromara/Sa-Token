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
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * StpLogic 指定设备登录与注销
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicDeviceTest {

	private StpLogic stpLogic;

	/** 每个用例开始前准备测试现场 */
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
			NotLoginException kickOut = Assertions.assertThrows(NotLoginException.class, stpLogic::checkLogin);
			Assertions.assertEquals(NotLoginException.KICK_OUT, kickOut.getType());
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
			NotLoginException replaced = Assertions.assertThrows(NotLoginException.class, stpLogic::checkLogin);
			Assertions.assertEquals(NotLoginException.BE_REPLACED, replaced.getType());
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

	/** 无 Session 时 forEachTerminalList 应不执行回调 */
	@Test
	void forEachTerminalList_noSession_isNoOp() {
		AtomicInteger count = new AtomicInteger();
		stpLogic.forEachTerminalList(79999, (session, terminal) -> count.incrementAndGet());
		Assertions.assertEquals(0, count.get());
	}

	/** 无效/冻结/已移除终端的 Token 查询 getTerminalInfoByToken 应返回 null */
	@Test
	void getTerminalInfoByToken_invalidOrFrozen_returnsNull() {
		Assertions.assertNull(stpLogic.getTerminalInfoByToken(null));
		Assertions.assertNull(stpLogic.getTerminalInfoByToken("invalid-token"));

		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(10);
		SaManager.setConfig(config);

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70018);
			String token = stpLogic.getTokenValue();
			long oldTime = System.currentTimeMillis() - 60_000;
			SaManager.getSaTokenDao().set(stpLogic.splicingKeyLastActiveTime(token),
					String.valueOf(oldTime), 3600);
			Assertions.assertNull(stpLogic.getTerminalInfoByToken(token));

			SaSession session = stpLogic.getSessionByLoginId(70018);
			session.removeTerminal(token);
			Assertions.assertNull(stpLogic.getTerminalInfoByToken(token));
		});
	}

	/** Session 缺失或终端已移除时 getTerminalInfoByToken 应返回 null */
	@Test
	void getTerminalInfoByToken_sessionMissingOrTerminalRemoved_returnsNull() {
		SaTokenDao dao = SaManager.getSaTokenDao();
		String token = stpLogic.createTokenValue(70027, "PC", 3600, null);
		dao.set(stpLogic.splicingKeyTokenValue(token), "70027", 3600);
		Assertions.assertNull(stpLogic.getTerminalInfoByToken(token));

		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70028);
			String loginToken = stpLogic.getTokenValue();
			SaSession session = stpLogic.getSessionByLoginId(70028);
			session.removeTerminal(loginToken);
			Assertions.assertNull(stpLogic.getTerminalInfoByToken(loginToken));
		});
	}

	/** 终端列表为空时 getTerminalInfoByToken 应返回 null */
	@Test
	void getTerminalInfoByToken_validTokenButTerminalMissingInList_returnsNull() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70032);
			String token = stpLogic.getTokenValue();
			SaSession session = stpLogic.getSessionByLoginId(70032);
			session.removeTerminal(token);
			session.setTerminalList(new java.util.ArrayList<>());
			Assertions.assertNull(stpLogic.getTerminalInfoByToken(token));
		});
	}

}

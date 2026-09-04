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
package cn.dev33.satoken.core.session;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SaSession 扩展测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaSessionExtendedTest {

	/** 按设备类型筛选终端列表，forEach 遍历及历史终端计数应正确 */
	@Test
	void terminalListByDeviceType_andForEach() {
		SaSession session = new SaSession("sid-ext");
		session.addTerminal(new SaTerminalInfo(1, "token-pc", "PC", null));
		session.addTerminal(new SaTerminalInfo(2, "token-app", "APP", null));

		List<SaTerminalInfo> pcList = session.getTerminalListByDeviceType("PC");
		Assertions.assertEquals(1, pcList.size());
		Assertions.assertEquals("token-pc", pcList.get(0).getTokenValue());

		AtomicInteger count = new AtomicInteger();
		session.forEachTerminalList((s, t) -> count.incrementAndGet());
		Assertions.assertEquals(2, count.get());
		Assertions.assertEquals(2, session.getHistoryTerminalCount());
	}

	/** 移除最后一个终端后 logoutByTerminalCountToZero 应从 DAO 删除 Session */
	@Test
	void logoutByTerminalCountToZero() {
		SaSession session = new SaSession("sid-logout");
		SaManager.getSaTokenDao().setSession(session, 3600);
		session.addTerminal(new SaTerminalInfo(1, "only-token", "PC", null));
		session.removeTerminal("only-token");
		session.logoutByTerminalCountToZero();
		Assertions.assertNull(SaManager.getSaTokenDao().getSession(session.getId()));
	}

}

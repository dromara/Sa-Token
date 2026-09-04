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
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * StpLogic 会话搜索
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicSearchTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** 多次登录后 searchTokenValue 应返回至少 5 条 Token */
	@Test
	void searchTokenValue_afterMultipleLogins() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			stpLogic.login(10002);
			stpLogic.login(10003);
			stpLogic.login(10004);
			stpLogic.login(10005);

			List<String> list = stpLogic.searchTokenValue("", 0, 10, true);
			Assertions.assertTrue(list.size() >= 5);
		});
	}

	/** 多次登录后 searchSessionId 应返回可解析的 SessionId 列表 */
	@Test
	void searchSessionId_afterMultipleLogins() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			stpLogic.login(10002);
			stpLogic.login(10003);
			stpLogic.login(10004);
			stpLogic.login(10005);

			List<String> list = stpLogic.searchSessionId("", 0, 10, true);
			Assertions.assertTrue(list.size() >= 5);
			list.forEach(sessionId -> Assertions.assertNotNull(stpLogic.getSessionBySessionId(sessionId)));
		});
	}

	/** 创建 Token-Session 后 searchTokenSessionId 应返回有效列表 */
	@Test
	void searchTokenSessionId_afterMultipleLogins() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			stpLogic.getTokenSession();
			stpLogic.login(10002);
			stpLogic.getTokenSession();
			stpLogic.login(10003);
			stpLogic.getTokenSession();
			stpLogic.login(10004);
			stpLogic.getTokenSession();
			stpLogic.login(10005);
			stpLogic.getTokenSession();

			List<String> list = stpLogic.searchTokenSessionId("", 0, 10, true);
			Assertions.assertTrue(list.size() >= 5);
			list.forEach(sessionId -> Assertions.assertNotNull(stpLogic.getSessionBySessionId(sessionId)));
		});
	}

}

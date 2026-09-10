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
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 踢人与顶人下线
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicKickoutReplacedTest {

	private StpLogic stpLogic;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** kickout 应将 Token 标记为 KICK_OUT 且 checkLogin 抛出对应异常 */
	@Test
	void kickout_marksTokenAsKickOut() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();

			stpLogic.kickout(10001);
			Assertions.assertEquals(NotLoginException.KICK_OUT, dao.get(stpLogic.splicingKeyTokenValue(token)));

			NotLoginException kickOut = Assertions.assertThrows(NotLoginException.class, stpLogic::checkLogin);
			Assertions.assertEquals(NotLoginException.KICK_OUT, kickOut.getType());
		});
	}

	/** replaced 应将 Token 标记为 BE_REPLACED 且 checkLogin 抛出对应异常 */
	@Test
	void replaced_marksTokenAsBeReplaced() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			String token = stpLogic.getTokenValue();
			SaTokenDao dao = SaManager.getSaTokenDao();

			stpLogic.replaced(10001);
			Assertions.assertEquals(NotLoginException.BE_REPLACED, dao.get(stpLogic.splicingKeyTokenValue(token)));

			NotLoginException replaced = Assertions.assertThrows(NotLoginException.class, stpLogic::checkLogin);
			Assertions.assertEquals(NotLoginException.BE_REPLACED, replaced.getType());
		});
	}

	/** 单参数 replacedByTokenValue 应将 Token 标记为 BE_REPLACED */
	@Test
	void replacedByTokenValue_oneArgOverload() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(70019);
			String token = stpLogic.getTokenValue();
			stpLogic.replacedByTokenValue(token);
			Assertions.assertEquals(NotLoginException.BE_REPLACED,
					SaManager.getSaTokenDao().get(stpLogic.splicingKeyTokenValue(token)));
		});
	}

}

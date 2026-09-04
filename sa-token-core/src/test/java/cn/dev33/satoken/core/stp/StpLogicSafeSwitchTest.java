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
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 二级认证与身份切换
 */
@SaTokenTest
public class StpLogicSafeSwitchTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** 二级认证 openSafe/isSafe/checkSafe/closeSafe 全流程应正常 */
	@Test
	void openSafe_isSafe_checkSafe_closeSafe() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			Assertions.assertFalse(stpLogic.isSafe());

			stpLogic.openSafe(2);
			Assertions.assertTrue(stpLogic.isSafe());
			Assertions.assertTrue(stpLogic.getSafeTime() > 0);
			Assertions.assertDoesNotThrow(() -> stpLogic.checkSafe());

			stpLogic.closeSafe();
			Assertions.assertFalse(stpLogic.isSafe());
			Assertions.assertThrows(NotSafeException.class, () -> stpLogic.checkSafe());
		});
	}

	/** switchTo/endSwitch 应切换 loginId 并正确标记 isSwitch 状态 */
	@Test
	void switchTo_endSwitch_isSwitch() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			Assertions.assertFalse(stpLogic.isSwitch());
			Assertions.assertEquals(10001L, stpLogic.getLoginIdAsLong());

			stpLogic.switchTo(10044);
			Assertions.assertTrue(stpLogic.isSwitch());
			Assertions.assertEquals(10044L, stpLogic.getLoginIdAsLong());

			stpLogic.endSwitch();
			Assertions.assertFalse(stpLogic.isSwitch());
			Assertions.assertEquals(10001L, stpLogic.getLoginIdAsLong());
		});
	}

	/** 带 Lambda 的 switchTo 应在回调内切换并在结束后恢复 */
	@Test
	void switchTo_withLambda() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			Assertions.assertFalse(stpLogic.isSwitch());

			stpLogic.switchTo(10045, () -> {
				Assertions.assertTrue(stpLogic.isSwitch());
				Assertions.assertEquals(10045L, stpLogic.getLoginIdAsLong());
			});

			Assertions.assertFalse(stpLogic.isSwitch());
			Assertions.assertEquals(10001L, stpLogic.getLoginIdAsLong());
		});
	}

}

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
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 二级认证与身份切换
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicSafeSwitchTest {

	private StpLogic stpLogic;

	/** 每个用例开始前准备测试现场 */
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

	/** 无效 Token 时 isSafe 应返回 false，getSafeTime 应返回 NOT_VALUE_EXPIRE */
	@Test
	void isSafe_and_getSafeTime_handleMissingToken() {
		Assertions.assertFalse(stpLogic.isSafe("invalid-token", "pay"));
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getSafeTime("pay"));
		});
	}

	/** Token 无对应 loginId 映射时 isSafe 应返回 false */
	@Test
	void isSafe_invalidLoginId_returnsFalse() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.setTokenValue("orphan-token");
			Assertions.assertFalse(stpLogic.isSafe("orphan-token", "pay"));
		});
	}

	/** 指定 service 的二级认证 openSafe/checkSafe/closeSafe 全流程应正常 */
	@Test
	void openSafeAndCheckSafe_withService() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(90010);
			Assertions.assertFalse(stpLogic.isSafe("pay"));

			stpLogic.openSafe("pay", 60);
			Assertions.assertTrue(stpLogic.isSafe("pay"));
			Assertions.assertDoesNotThrow(() -> stpLogic.checkSafe("pay"));
			Assertions.assertTrue(stpLogic.getSafeTime("pay") > 0);

			stpLogic.closeSafe("pay");
			Assertions.assertFalse(stpLogic.isSafe("pay"));
			Assertions.assertThrows(NotSafeException.class, () -> stpLogic.checkSafe("pay"));
		});
	}

	/** 空 Token 调用 isSafe 应返回 false */
	@Test
	void isSafe_emptyToken_returnsFalse() {
		Assertions.assertFalse(stpLogic.isSafe("", "pay"));
		Assertions.assertFalse(stpLogic.isSafe(null, "pay"));
	}

	/** 无 Token 时 closeSafe 应不抛异常 */
	@Test
	void closeSafe_withoutToken_isNoOp() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertDoesNotThrow(() -> stpLogic.closeSafe("pay"));
		});
	}

}

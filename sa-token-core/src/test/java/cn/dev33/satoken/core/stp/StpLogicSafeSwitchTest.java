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
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
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

	/** 指定 token 开启认证不依赖请求上下文，并且只影响指定 token 和业务 */
	@Test
	void openSafe_withToken_withoutContext() {
		String token = SaTokenContextMockUtil.setMockContext(() -> stpLogic.createLoginSession(90011));
		String otherToken = SaTokenContextMockUtil.setMockContext(() -> stpLogic.createLoginSession(90012));
		stpLogic.openSafe(token, "pay", 60);

		Assertions.assertTrue(stpLogic.isSafe(token, "pay"));
		Assertions.assertFalse(stpLogic.isSafe(token, "edit"));
		Assertions.assertFalse(stpLogic.isSafe(otherToken, "pay"));
		long timeout = stpLogic.getSaTokenDao().getTimeout(stpLogic.splicingKeySafe(token, "pay"));
		Assertions.assertTrue(timeout > 0 && timeout <= 60);
	}

	/** 空、未知或带异常登录标记的 token 不得写入认证状态或发布事件 */
	@Test
	void openSafe_withInvalidToken_throwsBeforeWritingOrFiringEvent() {
		int[] eventCount = {0};
		SaTokenListenerForSimple listener = new SaTokenListenerForSimple() {
			@Override
			public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
				eventCount[0]++;
			}
		};
		SaTokenEventCenter.registerListener(listener);
		try {
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.openSafe(null, "pay", 60));
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.openSafe("", "pay", 60));
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.openSafe("unknown", "pay", 60));
			Assertions.assertNull(stpLogic.getSaTokenDao().get(stpLogic.splicingKeySafe("unknown", "pay")));
			for(String marker : NotLoginException.ABNORMAL_LIST) {
				String token = "invalid-" + marker;
				stpLogic.getSaTokenDao().set(stpLogic.splicingKeyTokenValue(token), marker, 60);
				Assertions.assertThrows(NotLoginException.class, () -> stpLogic.openSafe(token, "pay", 60));
				Assertions.assertNull(stpLogic.getSaTokenDao().get(stpLogic.splicingKeySafe(token, "pay")));
			}
			Assertions.assertEquals(0, eventCount[0]);
		} finally {
			SaTokenEventCenter.removeListener(listener);
		}
	}

	/** 在另一会话中操作指定 token 时，事件应携带指定 token，当前会话不受影响 */
	@Test
	void openSafe_withToken_firesTargetEventOnce() {
		String token = SaTokenContextMockUtil.setMockContext(() -> stpLogic.createLoginSession(90013));
		int[] eventCount = {0};
		SaTokenListenerForSimple listener = new SaTokenListenerForSimple() {
			@Override
			public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
				Assertions.assertEquals(stpLogic.getLoginType(), loginType);
				Assertions.assertEquals(token, tokenValue);
				Assertions.assertEquals("pay", service);
				Assertions.assertEquals(60, safeTime);
				eventCount[0]++;
			}
		};
		SaTokenEventCenter.registerListener(listener);
		try {
			SaTokenContextMockUtil.setMockContext(() -> {
				stpLogic.login(90014);
				String currentToken = stpLogic.getTokenValue();
				stpLogic.openSafe(token, "pay", 60);
				Assertions.assertFalse(stpLogic.isSafe("pay"));
				Assertions.assertEquals(currentToken, stpLogic.getTokenValue());
			});
			Assertions.assertTrue(stpLogic.isSafe(token, "pay"));
			Assertions.assertEquals(1, eventCount[0]);
		} finally {
			SaTokenEventCenter.removeListener(listener);
		}
	}

	/** 原有当前会话版本仍应拒绝未登录及已被踢下线的会话 */
	@Test
	void openSafe_currentSession_preservesLoginChecks() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.openSafe("pay", 60));
			stpLogic.login(90015);
			String token = stpLogic.getTokenValue();
			stpLogic.kickout(90015);
			NotLoginException exception = Assertions.assertThrows(NotLoginException.class,
					() -> stpLogic.openSafe("pay", 60));
			Assertions.assertEquals(NotLoginException.KICK_OUT, exception.getType());
			Assertions.assertNull(stpLogic.getSaTokenDao().get(stpLogic.splicingKeySafe(token, "pay")));
		});
	}

	/** 临时切换身份且 token 无效时，旧入口仍写入认证标记并发布事件 */
	@Test
	void openSafe_currentSession_withSwitchedIdentityAndInvalidToken() {
		String token = "orphan-token";
		int[] eventCount = {0};
		SaTokenListenerForSimple listener = new SaTokenListenerForSimple() {
			@Override
			public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
				Assertions.assertEquals(stpLogic.getLoginType(), loginType);
				Assertions.assertEquals(token, tokenValue);
				Assertions.assertEquals("pay", service);
				Assertions.assertEquals(60, safeTime);
				eventCount[0]++;
			}
		};
		SaTokenEventCenter.registerListener(listener);
		try {
			SaTokenContextMockUtil.setMockContext(() -> {
				stpLogic.setTokenValue(token);
				Assertions.assertNull(stpLogic.getLoginIdNotHandle(token));
				stpLogic.switchTo(90016, () -> {
					Assertions.assertDoesNotThrow(() -> stpLogic.openSafe("pay", 60));
					String key = stpLogic.splicingKeySafe(token, "pay");
					Assertions.assertEquals(SaTokenConsts.SAFE_AUTH_SAVE_VALUE, stpLogic.getSaTokenDao().get(key));
					long timeout = stpLogic.getSaTokenDao().getTimeout(key);
					Assertions.assertTrue(timeout > 0 && timeout <= 60);
					Assertions.assertThrows(NotLoginException.class, () -> stpLogic.openSafe(token, "pay", 60));
				});
			});
			Assertions.assertEquals(1, eventCount[0]);
		} finally {
			SaTokenEventCenter.removeListener(listener);
		}
	}

	/** 指定 token 的完整二级认证流程（开启→校验→查询剩余时间→关闭）不依赖请求上下文 */
	@Test
	void safeLifecycle_withToken_withoutContext() {
		String token = SaTokenContextMockUtil.setMockContext(() -> stpLogic.createLoginSession(90021));
		String otherToken = SaTokenContextMockUtil.setMockContext(() -> stpLogic.createLoginSession(90022));

		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getSafeTime(token, "pay"));
		Assertions.assertThrows(NotSafeException.class, () -> stpLogic.checkSafe(token, "pay"));

		stpLogic.openSafe(token, "pay", 60);
		Assertions.assertDoesNotThrow(() -> stpLogic.checkSafe(token, "pay"));
		Assertions.assertThrows(NotSafeException.class, () -> stpLogic.checkSafe(token, "edit"));
		Assertions.assertThrows(NotSafeException.class, () -> stpLogic.checkSafe(otherToken, "pay"));
		long safeTime = stpLogic.getSafeTime(token, "pay");
		Assertions.assertTrue(safeTime > 0 && safeTime <= 60);
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getSafeTime(otherToken, "pay"));

		stpLogic.closeSafe(token, "pay");
		Assertions.assertFalse(stpLogic.isSafe(token, "pay"));
		Assertions.assertThrows(NotSafeException.class, () -> stpLogic.checkSafe(token, "pay"));
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getSafeTime(token, "pay"));
	}

	/** 指定 token 校验：空、未知或带异常登录标记的 token 抛 NotLoginException，而不是 NotSafeException */
	@Test
	void checkSafe_withInvalidToken_throwsNotLogin() {
		Assertions.assertThrows(NotLoginException.class, () -> stpLogic.checkSafe(null, "pay"));
		Assertions.assertThrows(NotLoginException.class, () -> stpLogic.checkSafe("", "pay"));
		Assertions.assertThrows(NotLoginException.class, () -> stpLogic.checkSafe("unknown", "pay"));
		for(String marker : NotLoginException.ABNORMAL_LIST) {
			String token = "invalid-check-" + marker;
			stpLogic.getSaTokenDao().set(stpLogic.splicingKeyTokenValue(token), marker, 60);
			stpLogic.getSaTokenDao().set(stpLogic.splicingKeySafe(token, "pay"), SaTokenConsts.SAFE_AUTH_SAVE_VALUE, 60);
			Assertions.assertThrows(NotLoginException.class, () -> stpLogic.checkSafe(token, "pay"));
		}
	}

	/** 指定 token 查询/关闭：空 token 静默处理，与当前会话版本一致 */
	@Test
	void getSafeTimeAndCloseSafe_withEmptyToken_noop() {
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getSafeTime(null, "pay"));
		Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, stpLogic.getSafeTime("", "pay"));
		Assertions.assertDoesNotThrow(() -> stpLogic.closeSafe(null, "pay"));
		Assertions.assertDoesNotThrow(() -> stpLogic.closeSafe("", "pay"));
	}

	/** 在另一会话中关闭指定 token 的认证：只影响目标 token，事件携带目标 token */
	@Test
	void closeSafe_withToken_firesTargetEventAndKeepsCurrentSession() {
		String token = SaTokenContextMockUtil.setMockContext(() -> stpLogic.createLoginSession(90023));
		stpLogic.openSafe(token, "pay", 60);
		int[] eventCount = {0};
		SaTokenListenerForSimple listener = new SaTokenListenerForSimple() {
			@Override
			public void doCloseSafe(String loginType, String tokenValue, String service) {
				Assertions.assertEquals(stpLogic.getLoginType(), loginType);
				Assertions.assertEquals(token, tokenValue);
				Assertions.assertEquals("pay", service);
				eventCount[0]++;
			}
		};
		SaTokenEventCenter.registerListener(listener);
		try {
			SaTokenContextMockUtil.setMockContext(() -> {
				stpLogic.login(90024);
				stpLogic.openSafe("pay", 60);
				stpLogic.closeSafe(token, "pay");
				Assertions.assertTrue(stpLogic.isSafe("pay"));
			});
			Assertions.assertFalse(stpLogic.isSafe(token, "pay"));
			Assertions.assertEquals(1, eventCount[0]);
		} finally {
			SaTokenEventCenter.removeListener(listener);
		}
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

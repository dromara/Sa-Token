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
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * StpLogic 权限校验
 */
@SaTokenTest
public class StpLogicPermissionTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		SaManager.setStpInterface(new StpInterface() {
			@Override
			public List<String> getPermissionList(Object loginId, String loginType) {
				return Arrays.asList("user:add", "user:view", "order:list");
			}
			@Override
			public List<String> getRoleList(Object loginId, String loginType) {
				return Arrays.asList("admin");
			}
		});
		stpLogic = new StpLogic("login");
	}

	/** hasPermission 与 checkPermission 应对有/无权限正确判断 */
	@Test
	void hasPermission_andCheckPermission() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);

			Assertions.assertTrue(stpLogic.hasPermission("user:add"));
			Assertions.assertTrue(stpLogic.hasPermission(10001, "user:view"));
			Assertions.assertFalse(stpLogic.hasPermission("user:delete"));
			Assertions.assertDoesNotThrow(() -> stpLogic.checkPermission("user:add"));
			Assertions.assertThrows(NotPermissionException.class, () -> stpLogic.checkPermission("user:delete"));
		});
	}

	/** checkPermissionAnd 应要求拥有全部指定权限 */
	@Test
	void checkPermissionAnd_requiresAll() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			Assertions.assertDoesNotThrow(() -> stpLogic.checkPermissionAnd("user:add", "user:view"));
			Assertions.assertThrows(NotPermissionException.class,
					() -> stpLogic.checkPermissionAnd("user:add", "user:delete"));
		});
	}

	/** checkPermissionOr 应要求拥有任一指定权限 */
	@Test
	void checkPermissionOr_requiresAny() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			Assertions.assertDoesNotThrow(() -> stpLogic.checkPermissionOr("user:delete", "order:list"));
			Assertions.assertThrows(NotPermissionException.class,
					() -> stpLogic.checkPermissionOr("user:delete", "goods:view"));
		});
	}

	/** 空参数调用 checkPermissionAnd/Or 应直接通过 */
	@Test
	void checkPermissionAnd_skipsWhenEmpty() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			Assertions.assertDoesNotThrow(() -> stpLogic.checkPermissionAnd());
			Assertions.assertDoesNotThrow(() -> stpLogic.checkPermissionOr());
		});
	}

}

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
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * StpLogic 角色校验
 */
@SaTokenTest
public class StpLogicRoleTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		SaManager.setStpInterface(new StpInterface() {
			@Override
			public List<String> getPermissionList(Object loginId, String loginType) {
				return Arrays.asList();
			}
			@Override
			public List<String> getRoleList(Object loginId, String loginType) {
				return Arrays.asList("admin", "user");
			}
		});
		stpLogic = new StpLogic("login");
	}

	/** hasRole 与 checkRole 应对有/无角色正确判断 */
	@Test
	void hasRole_andCheckRole() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);

			Assertions.assertTrue(stpLogic.hasRole("admin"));
			Assertions.assertTrue(stpLogic.hasRole(10001, "user"));
			Assertions.assertFalse(stpLogic.hasRole("super"));
			Assertions.assertDoesNotThrow(() -> stpLogic.checkRole("admin"));
			Assertions.assertThrows(NotRoleException.class, () -> stpLogic.checkRole("super"));
		});
	}

	/** checkRoleAnd 应要求拥有全部指定角色 */
	@Test
	void checkRoleAnd_requiresAll() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			Assertions.assertDoesNotThrow(() -> stpLogic.checkRoleAnd("admin", "user"));
			Assertions.assertThrows(NotRoleException.class, () -> stpLogic.checkRoleAnd("admin", "super"));
		});
	}

	/** checkRoleOr 应要求拥有任一指定角色 */
	@Test
	void checkRoleOr_requiresAny() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			Assertions.assertDoesNotThrow(() -> stpLogic.checkRoleOr("super", "user"));
			Assertions.assertThrows(NotRoleException.class, () -> stpLogic.checkRoleOr("super", "guest"));
		});
	}

}

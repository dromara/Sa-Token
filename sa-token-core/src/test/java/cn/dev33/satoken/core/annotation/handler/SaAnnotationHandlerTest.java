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
package cn.dev33.satoken.core.annotation.handler;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.annotation.handler.SaCheckLoginHandler;
import cn.dev33.satoken.annotation.handler.SaCheckPermissionHandler;
import cn.dev33.satoken.annotation.handler.SaCheckRoleHandler;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 注解鉴权处理器
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaAnnotationHandlerTest {

	@BeforeEach
	void setUp() {
		SaManager.setStpInterface(new StpInterface() {
			@Override
			public List<String> getPermissionList(Object loginId, String loginType) {
				return Arrays.asList("user:add", "user:view");
			}
			@Override
			public List<String> getRoleList(Object loginId, String loginType) {
				return Arrays.asList("admin", "user");
			}
		});
		new StpLogic("login");
	}

	/** 已登录时 SaCheckLoginHandler 应通过校验 */
	@Test
	void saCheckLoginHandler_whenLoggedIn() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() -> SaCheckLoginHandler._checkMethod(""));
		});
	}

	/** 未登录时 SaCheckLoginHandler 应抛出 NotLoginException */
	@Test
	void saCheckLoginHandler_whenNotLoggedIn() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertThrows(NotLoginException.class, () -> SaCheckLoginHandler._checkMethod(""));
		});
	}

	/** SaCheckPermission AND 模式应要求全部权限 */
	@Test
	void saCheckPermissionHandler_andMode() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() ->
					SaCheckPermissionHandler._checkMethod("", new String[] {"user:add", "user:view"}, SaMode.AND, new String[] {}));
			Assertions.assertThrows(NotPermissionException.class, () ->
					SaCheckPermissionHandler._checkMethod("", new String[] {"user:add", "user:delete"}, SaMode.AND, new String[] {}));
		});
	}

	/** SaCheckPermission OR 模式应满足任一权限即可 */
	@Test
	void saCheckPermissionHandler_orMode() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() ->
					SaCheckPermissionHandler._checkMethod("", new String[] {"user:delete", "user:view"}, SaMode.OR, new String[] {}));
		});
	}

	/** 权限不足时应回退校验 orRole 角色列表 */
	@Test
	void saCheckPermissionHandler_fallsBackToOrRole() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() ->
					SaCheckPermissionHandler._checkMethod("", new String[] {"goods:view"}, SaMode.AND, new String[] {"admin"}));
			Assertions.assertThrows(NotPermissionException.class, () ->
					SaCheckPermissionHandler._checkMethod("", new String[] {"goods:view"}, SaMode.AND, new String[] {"super"}));
		});
	}

	/** SaCheckRole AND 模式应要求全部角色 */
	@Test
	void saCheckRoleHandler_andMode() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() ->
					SaCheckRoleHandler._checkMethod("", new String[] {"admin", "user"}, SaMode.AND));
			Assertions.assertThrows(NotRoleException.class, () ->
					SaCheckRoleHandler._checkMethod("", new String[] {"admin", "super"}, SaMode.AND));
		});
	}

	/** SaCheckRole OR 模式应满足任一角色即可 */
	@Test
	void saCheckRoleHandler_orMode() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() ->
					SaCheckRoleHandler._checkMethod("", new String[] {"super", "user"}, SaMode.OR));
		});
	}

}

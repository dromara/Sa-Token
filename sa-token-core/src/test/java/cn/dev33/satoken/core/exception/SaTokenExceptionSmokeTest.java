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
package cn.dev33.satoken.core.exception;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotHttpBasicAuthException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.SaTokenPluginException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 核心异常 smoke 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenExceptionSmokeTest {

	/** SaTokenException 应支持链式设置 code */
	@Test
	void saTokenException_setCodeAndMessage() {
		SaTokenException ex = new SaTokenException("framework error").setCode(SaErrorCode.CODE_10002);
		Assertions.assertEquals("framework error", ex.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_10002, ex.getCode());
	}

	/** NotLoginException 应保存消息与错误码 */
	@Test
	void notLoginException() {
		NotLoginException ex = new NotLoginException(NotLoginException.NOT_TOKEN_MESSAGE, "login", NotLoginException.NOT_TOKEN);
		ex.setCode(SaErrorCode.CODE_11001);
		Assertions.assertEquals(NotLoginException.NOT_TOKEN_MESSAGE, ex.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_11001, ex.getCode());
	}

	/** NotPermissionException 应保存权限与错误码 */
	@Test
	void notPermissionException() {
		NotPermissionException ex = new NotPermissionException("user:add", "login");
		ex.setCode(SaErrorCode.CODE_11051);
		Assertions.assertEquals("user:add", ex.getPermission());
		Assertions.assertEquals(SaErrorCode.CODE_11051, ex.getCode());
	}

	/** NotRoleException 应保存角色与错误码 */
	@Test
	void notRoleException() {
		NotRoleException ex = new NotRoleException("admin", "login");
		ex.setCode(SaErrorCode.CODE_11041);
		Assertions.assertEquals("admin", ex.getRole());
		Assertions.assertEquals(SaErrorCode.CODE_11041, ex.getCode());
	}

	/** DisableServiceException 应保存封禁信息与错误码 */
	@Test
	void disableServiceException() {
		DisableServiceException ex = new DisableServiceException("login", 10001, "login", 2, 1, 3600);
		ex.setCode(SaErrorCode.CODE_11061);
		Assertions.assertEquals(10001, ex.getLoginId());
		Assertions.assertEquals(SaErrorCode.CODE_11061, ex.getCode());
	}

	/** NotSafeException 应保存服务名与错误码 */
	@Test
	void notSafeException() {
		NotSafeException ex = new NotSafeException("login", "token-1", "pay");
		ex.setCode(SaErrorCode.CODE_11071);
		Assertions.assertEquals("pay", ex.getService());
		Assertions.assertEquals(SaErrorCode.CODE_11071, ex.getCode());
	}

	/** NotHttpBasicAuthException 应支持设置错误码 */
	@Test
	void notHttpBasicAuthException() {
		NotHttpBasicAuthException ex = new NotHttpBasicAuthException();
		ex.setCode(SaErrorCode.CODE_10311);
		Assertions.assertEquals(SaErrorCode.CODE_10311, ex.getCode());
	}

	/** ApiDisabledException 应保存消息与错误码 */
	@Test
	void apiDisabledException() {
		ApiDisabledException ex = new ApiDisabledException("disabled api");
		ex.setCode(SaErrorCode.CODE_10003);
		Assertions.assertEquals("disabled api", ex.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_10003, ex.getCode());
	}

	/** SaTokenPluginException 应保存消息与错误码 */
	@Test
	void saTokenPluginException() {
		SaTokenPluginException ex = new SaTokenPluginException("plugin error");
		ex.setCode(SaErrorCode.CODE_UNDEFINED);
		Assertions.assertEquals("plugin error", ex.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_UNDEFINED, ex.getCode());
	}

}

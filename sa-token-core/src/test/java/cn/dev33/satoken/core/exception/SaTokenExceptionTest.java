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
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.InvalidContextException;
import cn.dev33.satoken.exception.NotHttpBasicAuthException;
import cn.dev33.satoken.exception.NotHttpDigestAuthException;
import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.exception.NotWebContextException;
import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.exception.SaTokenContextException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.SaTokenPluginException;
import cn.dev33.satoken.exception.SameTokenInvalidException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.exception.TotpAuthException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 核心异常测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenExceptionTest {

	/** SaTokenException 应支持链式设置 code */
	@Test
	void saTokenException_setCodeAndMessage() {
		SaTokenException ex = new SaTokenException("framework error").setCode(SaErrorCode.CODE_10002);
		Assertions.assertEquals("framework error", ex.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_10002, ex.getCode());
	}

	/** SaTokenException 各构造函数应正确设置 code 与 cause */
	@Test
	void saTokenException_constructors() {
		SaTokenException byCode = new SaTokenException(SaErrorCode.CODE_10002);
		Assertions.assertEquals(SaErrorCode.CODE_10002, byCode.getCode());

		SaTokenException byCodeAndMessage = new SaTokenException(SaErrorCode.CODE_10002, "coded");
		Assertions.assertEquals("coded", byCodeAndMessage.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_10002, byCodeAndMessage.getCode());

		RuntimeException cause = new RuntimeException("root");
		SaTokenException byCause = new SaTokenException(cause);
		Assertions.assertSame(cause, byCause.getCause());

		SaTokenException byMessageAndCause = new SaTokenException("wrapped", cause);
		Assertions.assertEquals("wrapped", byMessageAndCause.getMessage());
		Assertions.assertSame(cause, byMessageAndCause.getCause());
	}

	/** notTrue/notEmpty 静态方法应在条件不满足时抛出异常 */
	@Test
	void saTokenException_staticHelpers() {
		Assertions.assertDoesNotThrow(() -> SaTokenException.notTrue(false, "ok"));
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> SaTokenException.notTrue(true, "fail", SaErrorCode.CODE_10002));
		Assertions.assertEquals(SaErrorCode.CODE_10002, ex.getCode());

		Assertions.assertDoesNotThrow(() -> SaTokenException.notEmpty("value", "empty"));
		SaTokenException emptyEx = Assertions.assertThrows(SaTokenException.class,
				() -> SaTokenException.notEmpty("", "empty", SaErrorCode.CODE_10002));
		Assertions.assertEquals(SaErrorCode.CODE_10002, emptyEx.getCode());
	}

	/** NotLoginException 应保存消息与错误码 */
	@Test
	void notLoginException() {
		NotLoginException ex = new NotLoginException(NotLoginException.NOT_TOKEN_MESSAGE, "login", NotLoginException.NOT_TOKEN);
		ex.setCode(SaErrorCode.CODE_11001);
		Assertions.assertEquals(NotLoginException.NOT_TOKEN_MESSAGE, ex.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_11001, ex.getCode());
	}

	/** NotLoginException.newInstance 应填充 loginType 与 token */
	@Test
	void notLoginException_newInstance() {
		NotLoginException ex = NotLoginException.newInstance("login", NotLoginException.NOT_TOKEN,
				NotLoginException.NOT_TOKEN_MESSAGE, "tk-1");
		Assertions.assertEquals("login", ex.getLoginType());
		Assertions.assertEquals(NotLoginException.NOT_TOKEN, ex.getType());
		Assertions.assertTrue(ex.getMessage().contains("tk-1"));
	}

	/** NotPermissionException 应保存权限与错误码 */
	@Test
	void notPermissionException() {
		NotPermissionException ex = new NotPermissionException("user:add", "login");
		ex.setCode(SaErrorCode.CODE_11051);
		Assertions.assertEquals("user:add", ex.getPermission());
		Assertions.assertEquals(SaErrorCode.CODE_11051, ex.getCode());
	}

	/** NotPermissionException 单参构造应设置 permission */
	@Test
	void notPermissionException_singleArgConstructor() {
		NotPermissionException ex = new NotPermissionException("user:add");
		Assertions.assertEquals("user:add", ex.getPermission());
		Assertions.assertNotNull(ex.getLoginType());
	}

	/** NotRoleException 应保存角色与错误码 */
	@Test
	void notRoleException() {
		NotRoleException ex = new NotRoleException("admin", "login");
		ex.setCode(SaErrorCode.CODE_11041);
		Assertions.assertEquals("admin", ex.getRole());
		Assertions.assertEquals(SaErrorCode.CODE_11041, ex.getCode());
	}

	/** NotRoleException 单参构造应设置 role */
	@Test
	void notRoleException_singleArgConstructor() {
		NotRoleException ex = new NotRoleException("admin");
		Assertions.assertEquals("admin", ex.getRole());
		Assertions.assertNotNull(ex.getLoginType());
	}

	/** DisableServiceException 应保存封禁信息与错误码 */
	@Test
	void disableServiceException() {
		DisableServiceException ex = new DisableServiceException("login", 10001, "login", 2, 1, 3600);
		ex.setCode(SaErrorCode.CODE_11061);
		Assertions.assertEquals(10001, ex.getLoginId());
		Assertions.assertEquals(SaErrorCode.CODE_11061, ex.getCode());
	}

	/** DisableServiceException getter 应返回封禁信息 */
	@Test
	void disableServiceException_getters() {
		DisableServiceException ex = new DisableServiceException("login", 10001, "comment", 2, 1, 3600);
		Assertions.assertEquals("login", ex.getLoginType());
		Assertions.assertEquals(10001, ex.getLoginId());
		Assertions.assertEquals("comment", ex.getService());
		Assertions.assertEquals(2, ex.getLevel());
		Assertions.assertEquals(1, ex.getLimitLevel());
		Assertions.assertEquals(3600, ex.getDisableTime());
		Assertions.assertTrue(ex.getMessage().contains("comment"));
	}

	/** NotSafeException 应保存服务名与错误码 */
	@Test
	void notSafeException() {
		NotSafeException ex = new NotSafeException("login", "token-1", "pay");
		ex.setCode(SaErrorCode.CODE_11071);
		Assertions.assertEquals("pay", ex.getService());
		Assertions.assertEquals(SaErrorCode.CODE_11071, ex.getCode());
	}

	/** NotSafeException getter 应返回二次认证信息 */
	@Test
	void notSafeException_getters() {
		NotSafeException ex = new NotSafeException("login", "token-2", "pay");
		Assertions.assertEquals("login", ex.getLoginType());
		Assertions.assertEquals("token-2", ex.getTokenValue());
		Assertions.assertEquals("pay", ex.getService());
	}

	/** NotHttpBasicAuthException 应支持设置错误码 */
	@Test
	void notHttpBasicAuthException() {
		NotHttpBasicAuthException ex = new NotHttpBasicAuthException();
		ex.setCode(SaErrorCode.CODE_10311);
		Assertions.assertEquals(SaErrorCode.CODE_10311, ex.getCode());
	}

	/** 上下文与认证相关异常应正确携带消息 */
	@Test
	void contextAndAuthExceptions() {
		Assertions.assertEquals("invalid ctx", new InvalidContextException("invalid ctx").getMessage());
		Assertions.assertEquals("not web", new NotWebContextException("not web").getMessage());
		Assertions.assertEquals("not impl", new NotImplException("not impl").getMessage());
		Assertions.assertNotNull(new StopMatchException());
		Assertions.assertNotNull(new NotHttpDigestAuthException());
		Assertions.assertNotNull(new TotpAuthException());
		Assertions.assertEquals("same invalid", new SameTokenInvalidException("same invalid").getMessage());
		Assertions.assertEquals("ctx error", new SaTokenContextException("ctx error").getMessage());
	}

	/** ApiDisabledException 应保存消息与错误码 */
	@Test
	void apiDisabledException() {
		ApiDisabledException ex = new ApiDisabledException("disabled api");
		ex.setCode(SaErrorCode.CODE_10003);
		Assertions.assertEquals("disabled api", ex.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_10003, ex.getCode());
	}

	/** ApiDisabledException 无参构造应有默认消息 */
	@Test
	void apiDisabledException_noArgConstructor() {
		ApiDisabledException ex = new ApiDisabledException();
		Assertions.assertNotNull(ex.getMessage());
	}

	/** BackResultException 应保存 result 载荷 */
	@Test
	void backResultException() {
		BackResultException ex = new BackResultException("payload");
		Assertions.assertEquals("payload", ex.result);
	}

	/** SaJsonConvertException 应正确包装 cause */
	@Test
	void saJsonConvertException_constructors() {
		RuntimeException cause = new RuntimeException("json");
		SaJsonConvertException byCause = new SaJsonConvertException(cause);
		Assertions.assertSame(cause, byCause.getCause());

		SaJsonConvertException byMessageAndCause = new SaJsonConvertException("convert fail", cause);
		Assertions.assertEquals("convert fail", byMessageAndCause.getMessage());
	}

	/** SaTokenPluginException 应保存消息与错误码 */
	@Test
	void saTokenPluginException() {
		SaTokenPluginException ex = new SaTokenPluginException("plugin error");
		ex.setCode(SaErrorCode.CODE_UNDEFINED);
		Assertions.assertEquals("plugin error", ex.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_UNDEFINED, ex.getCode());
	}

	/** SaTokenPluginException 应正确包装 cause 与消息 */
	@Test
	void saTokenPluginException_constructors() {
		RuntimeException cause = new RuntimeException("plugin");
		Assertions.assertEquals("plugin", new SaTokenPluginException(cause).getCause().getMessage());
		SaTokenPluginException ex = new SaTokenPluginException("plugin fail", cause);
		Assertions.assertEquals("plugin fail", ex.getMessage());
		Assertions.assertSame(cause, ex.getCause());
	}

}

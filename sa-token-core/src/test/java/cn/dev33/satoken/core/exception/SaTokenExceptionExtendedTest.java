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
import cn.dev33.satoken.exception.InvalidContextException;
import cn.dev33.satoken.exception.NotHttpDigestAuthException;
import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.exception.NotWebContextException;
import cn.dev33.satoken.exception.RequestPathInvalidException;
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
 * 核心异常扩展测试
 */
public class SaTokenExceptionExtendedTest {

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

	/** NotLoginException.newInstance 应填充 loginType 与 token */
	@Test
	void notLoginException_newInstance() {
		NotLoginException ex = NotLoginException.newInstance("login", NotLoginException.NOT_TOKEN,
				NotLoginException.NOT_TOKEN_MESSAGE, "tk-1");
		Assertions.assertEquals("login", ex.getLoginType());
		Assertions.assertEquals(NotLoginException.NOT_TOKEN, ex.getType());
		Assertions.assertTrue(ex.getMessage().contains("tk-1"));
	}

	/** NotPermissionException 单参构造应设置 permission */
	@Test
	void notPermissionException_singleArgConstructor() {
		NotPermissionException ex = new NotPermissionException("user:add");
		Assertions.assertEquals("user:add", ex.getPermission());
		Assertions.assertNotNull(ex.getLoginType());
	}

	/** NotRoleException 单参构造应设置 role */
	@Test
	void notRoleException_singleArgConstructor() {
		NotRoleException ex = new NotRoleException("admin");
		Assertions.assertEquals("admin", ex.getRole());
		Assertions.assertNotNull(ex.getLoginType());
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

	/** RequestPathInvalidException 应保存非法路径 */
	@Test
	void requestPathInvalidException() {
		RequestPathInvalidException ex = new RequestPathInvalidException("bad path", "/bad");
		Assertions.assertEquals("/bad", ex.getPath());
		Assertions.assertTrue(ex.getMessage().contains("bad path"));
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

	/** NotSafeException getter 应返回二次认证信息 */
	@Test
	void notSafeException_getters() {
		NotSafeException ex = new NotSafeException("login", "token-2", "pay");
		Assertions.assertEquals("login", ex.getLoginType());
		Assertions.assertEquals("token-2", ex.getTokenValue());
		Assertions.assertEquals("pay", ex.getService());
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

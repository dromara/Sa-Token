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

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckHttpBasic;
import cn.dev33.satoken.annotation.SaCheckHttpDigest;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.annotation.handler.SaCheckDisableHandler;
import cn.dev33.satoken.annotation.handler.SaCheckHttpBasicHandler;
import cn.dev33.satoken.annotation.handler.SaCheckHttpDigestHandler;
import cn.dev33.satoken.annotation.handler.SaCheckOrHandler;
import cn.dev33.satoken.annotation.handler.SaCheckSafeHandler;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotHttpBasicAuthException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestUtil;
import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * 扩展注解鉴权处理器测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaAnnotationHandlerExtendedTest {

	static class AnnotatedMethods {
		@SaCheckLogin
		void loginMethod() {
		}
	}

	/** 账号未封禁时 SaCheckDisableHandler 应通过校验 */
	@Test
	void saCheckDisableHandler_whenNotDisabled() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() ->
					SaCheckDisableHandler._checkMethod("", new String[] {"login"}, SaTokenConsts.MIN_DISABLE_LEVEL));
		});
	}

	/** 账号已封禁时 SaCheckDisableHandler 应抛出 DisableServiceException */
	@Test
	void saCheckDisableHandler_whenDisabled() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			StpUtil.disable(10001, "login", 3600);
			Assertions.assertThrows(DisableServiceException.class, () ->
					SaCheckDisableHandler._checkMethod("", new String[] {"login"}, SaTokenConsts.MIN_DISABLE_LEVEL));
		});
	}

	/** 未开启二级认证时 SaCheckSafeHandler 应抛出 NotSafeException */
	@Test
	void saCheckSafeHandler_whenNotSafe() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertThrows(NotSafeException.class, () ->
					SaCheckSafeHandler._checkMethod("", "pay"));
		});
	}

	/** 已开启二级认证时 SaCheckSafeHandler 应通过校验 */
	@Test
	void saCheckSafeHandler_whenSafe() {
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			StpUtil.openSafe("pay", 3600);
			Assertions.assertDoesNotThrow(() -> SaCheckSafeHandler._checkMethod("", "pay"));
		});
	}

	/** SaCheckOr 无任何子注解时不应抛异常 */
	@Test
	void saCheckOrHandler_emptyAnnotations() {
		Assertions.assertDoesNotThrow(() ->
				SaCheckOrHandler._checkMethod(new SaCheckLogin[0], new SaCheckRole[0], new SaCheckPermission[0],
						new SaCheckSafe[0], new SaCheckHttpBasic[0], new SaCheckHttpDigest[0], new SaCheckDisable[0],
						new Class[0], AnnotatedMethods.class));
	}

	/** SaCheckOr 首个子注解校验通过时应放行 */
	@Test
	void saCheckOrHandler_firstMatchPasses() throws Exception {
		Method method = AnnotatedMethods.class.getDeclaredMethod("loginMethod");
		SaCheckLogin loginAt = method.getAnnotation(SaCheckLogin.class);
		SaTokenContextMockUtil.setMockContext(() -> {
			StpUtil.login(10001);
			Assertions.assertDoesNotThrow(() ->
					SaCheckOrHandler._checkMethod(new SaCheckLogin[] {loginAt}, new SaCheckRole[0], new SaCheckPermission[0],
							new SaCheckSafe[0], new SaCheckHttpBasic[0], new SaCheckHttpDigest[0], new SaCheckDisable[0],
							new Class[0], method));
		});
	}

	/** SaCheckOr 全部子注解校验失败时应抛出异常 */
	@Test
	void saCheckOrHandler_allFailThrows() throws Exception {
		Method method = AnnotatedMethods.class.getDeclaredMethod("loginMethod");
		SaCheckLogin loginAt = method.getAnnotation(SaCheckLogin.class);
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertThrows(NotLoginException.class, () ->
					SaCheckOrHandler._checkMethod(new SaCheckLogin[] {loginAt}, new SaCheckRole[0], new SaCheckPermission[0],
							new SaCheckSafe[0], new SaCheckHttpBasic[0], new SaCheckHttpDigest[0], new SaCheckDisable[0],
							new Class[0], method));
		});
	}

	/** 携带 Basic 请求头时 SaCheckHttpBasicHandler 应通过校验 */
	@Test
	void saCheckHttpBasicHandler_withMockRequest() {
		SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock request = (SaRequestForMock) SaHolder.getRequest();
			request.headerMap.put("Authorization", "Basic " + SaBase64Util.encode("user:pass"));
			Assertions.assertDoesNotThrow(() -> SaCheckHttpBasicHandler._checkMethod("realm", "user:pass"));
		});
	}

	/** 缺少 Authorization 头时 SaCheckHttpBasicHandler 应抛出异常 */
	@Test
	void saCheckHttpBasicHandler_withoutAuthHeader() {
		SaTokenContextMockUtil.setMockContext(() -> {
			Assertions.assertThrows(NotHttpBasicAuthException.class, () ->
					SaCheckHttpBasicHandler._checkMethod("realm", "user:pass"));
		});
	}

	/** Digest value 格式非法时应抛出 SaTokenException */
	@Test
	void saCheckHttpDigestHandler_invalidValueFormat() {
		Assertions.assertThrows(SaTokenException.class, () ->
				SaCheckHttpDigestHandler._checkMethod("", "", "", "badformat"));
	}

	/** Mock 模板下 SaCheckHttpDigestHandler 应正常校验 */
	@Test
	void saCheckHttpDigestHandler_withMockTemplate() {
		SaHttpDigestTemplate original = SaHttpDigestUtil.saHttpDigestTemplate;
		try {
			SaHttpDigestUtil.saHttpDigestTemplate = new SaHttpDigestTemplate() {
				@Override
				public void check(String username, String password) {
				}
			};
			Assertions.assertDoesNotThrow(() ->
					SaCheckHttpDigestHandler._checkMethod("", "", "", "user:pass"));
		} finally {
			SaHttpDigestUtil.saHttpDigestTemplate = original;
		}
	}

}

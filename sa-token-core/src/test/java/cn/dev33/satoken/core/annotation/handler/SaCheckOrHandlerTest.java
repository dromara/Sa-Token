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
import cn.dev33.satoken.annotation.handler.SaCheckOrHandler;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * SaCheckOrHandler 注解处理器测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaCheckOrHandlerTest {

	static class AnnotatedMethods {
		@SaCheckLogin
		void loginMethod() {
		}
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

}

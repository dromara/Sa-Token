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
package cn.dev33.satoken.core.strategy;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.handler.SaIgnoreHandler;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * SaAnnotationStrategy 扩展测试
 */
@SaTokenTest
public class SaAnnotationStrategyExtendedTest {

	@SaIgnore
	static class IgnoredController {
		@SaCheckLogin
		public void needLogin() {}
	}

	static class LoginController {
		@SaCheckLogin
		public void needLogin() {}

		@SaCheckOr(login = {}, append = {SaCheckLogin.class})
		public void checkOrAppend() {}
	}

	/** registerAnnotationHandlerToFirst 应将处理器插入链表头部 */
	@Test
	void registerAnnotationHandlerToFirst_putsHandlerAtHead() {
		SaAnnotationStrategy strategy = SaAnnotationStrategy.instance;
		SaIgnoreHandler handler = new SaIgnoreHandler();
		strategy.registerAnnotationHandlerToFirst(handler);
		Assertions.assertSame(handler, strategy.annotationHandlerMap.values().iterator().next());
		strategy.removeAnnotationHandler(handler.getHandlerAnnotationClass());
	}

	/** isAnnotationPresent 应能检测方法或类上的注解 */
	@Test
	void isAnnotationPresent_onMethodOrClass() throws Exception {
		SaAnnotationStrategy strategy = SaAnnotationStrategy.instance;
		Method ignoredMethod = IgnoredController.class.getMethod("needLogin");
		Assertions.assertTrue(strategy.isAnnotationPresent.apply(ignoredMethod, SaIgnore.class));

		Method loginMethod = LoginController.class.getMethod("needLogin");
		Assertions.assertFalse(strategy.isAnnotationPresent.apply(loginMethod, SaIgnore.class));
		Assertions.assertNotNull(strategy.getAnnotation.apply(loginMethod, SaCheckLogin.class));
	}

	/** 类标注 SaIgnore 时 checkMethodAnnotation 应跳过后续校验 */
	@Test
	void checkMethodAnnotation_skipsWhenSaIgnoreOnClass() throws Exception {
		SaAnnotationStrategy strategy = SaAnnotationStrategy.instance;
		Method method = IgnoredController.class.getMethod("needLogin");
		Assertions.assertThrows(StopMatchException.class,
				() -> strategy.checkMethodAnnotation.accept(method));
	}

	/** 未登录时 checkElementAnnotation 应抛出 NotLoginException */
	@Test
	void checkElementAnnotation_throwsWhenNotLogin() throws Exception {
		SaAnnotationStrategy strategy = SaAnnotationStrategy.instance;
		Method method = LoginController.class.getMethod("needLogin");
		SaTokenContextMockUtil.setMockContext(() ->
				Assertions.assertThrows(NotLoginException.class,
						() -> strategy.checkElementAnnotation.accept(method)));
	}

	/** SaCheckOr 中 append 注解应被跳过不重复校验 */
	@Test
	void checkElementAnnotation_skipsAppendInSaCheckOr() throws Exception {
		SaAnnotationStrategy strategy = SaAnnotationStrategy.instance;
		Method method = LoginController.class.getMethod("checkOrAppend");
		SaTokenContextMockUtil.setMockContext(() ->
				Assertions.assertDoesNotThrow(() -> strategy.checkElementAnnotation.accept(method)));
	}

	/** checkELRootMapExtendFunction 默认实现应无副作用 */
	@Test
	void checkELRootMapExtendFunction_defaultNoOp() {
		Map<String, Object> rootMap = new HashMap<>();
		Assertions.assertDoesNotThrow(() ->
				SaAnnotationStrategy.instance.checkELRootMapExtendFunction.accept(rootMap));
	}

}

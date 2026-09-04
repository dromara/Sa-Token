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

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckHttpBasic;
import cn.dev33.satoken.annotation.SaCheckHttpDigest;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckOr;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.annotation.handler.SaIgnoreHandler;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaAnnotationStrategy 注解策略测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaAnnotationStrategyTest {

	/** 默认注解处理器应已注册到 annotationHandlerMap */
	@Test
	void registerDefaultAnnotationHandler_exists() {
		SaAnnotationStrategy strategy = SaAnnotationStrategy.instance;
		Assertions.assertTrue(strategy.annotationHandlerMap.containsKey(SaCheckLogin.class));
		Assertions.assertTrue(strategy.annotationHandlerMap.containsKey(SaCheckRole.class));
		Assertions.assertTrue(strategy.annotationHandlerMap.containsKey(SaCheckPermission.class));
		Assertions.assertTrue(strategy.annotationHandlerMap.containsKey(SaCheckSafe.class));
		Assertions.assertTrue(strategy.annotationHandlerMap.containsKey(SaCheckDisable.class));
		Assertions.assertTrue(strategy.annotationHandlerMap.containsKey(SaCheckHttpBasic.class));
		Assertions.assertTrue(strategy.annotationHandlerMap.containsKey(SaCheckHttpDigest.class));
		Assertions.assertTrue(strategy.annotationHandlerMap.containsKey(SaCheckOr.class));
	}

	/** 注册与移除注解处理器应正确增删映射 */
	@Test
	void registerAnnotationHandler_andRemoveAnnotationHandler() {
		SaAnnotationStrategy strategy = SaAnnotationStrategy.instance;
		SaAnnotationHandlerInterface<?> handler = new SaIgnoreHandler();

		strategy.registerAnnotationHandler(handler);
		Assertions.assertSame(handler, strategy.annotationHandlerMap.get(handler.getHandlerAnnotationClass()));

		strategy.removeAnnotationHandler(handler.getHandlerAnnotationClass());
		Assertions.assertFalse(strategy.annotationHandlerMap.containsKey(handler.getHandlerAnnotationClass()));
	}

}

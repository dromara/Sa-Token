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

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.annotation.handler.SaCheckDisableHandler;
import cn.dev33.satoken.annotation.handler.SaCheckHttpBasicHandler;
import cn.dev33.satoken.annotation.handler.SaCheckHttpDigestHandler;
import cn.dev33.satoken.annotation.handler.SaCheckLoginHandler;
import cn.dev33.satoken.annotation.handler.SaCheckOrHandler;
import cn.dev33.satoken.annotation.handler.SaCheckPermissionHandler;
import cn.dev33.satoken.annotation.handler.SaCheckRoleHandler;
import cn.dev33.satoken.annotation.handler.SaCheckSafeHandler;
import cn.dev33.satoken.annotation.handler.SaIgnoreHandler;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * 注解处理器 getHandlerAnnotationClass 与 SaIgnore 路径测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaAnnotationHandlerFullTest {

	/** 各注解处理器 getHandlerAnnotationClass 应返回对应注解类型 */
	@Test
	void allHandlers_exposeAnnotationClass() {
		Assertions.assertNotNull(new SaCheckLoginHandler().getHandlerAnnotationClass());
		Assertions.assertNotNull(new SaCheckRoleHandler().getHandlerAnnotationClass());
		Assertions.assertNotNull(new SaCheckPermissionHandler().getHandlerAnnotationClass());
		Assertions.assertNotNull(new SaCheckSafeHandler().getHandlerAnnotationClass());
		Assertions.assertNotNull(new SaCheckDisableHandler().getHandlerAnnotationClass());
		Assertions.assertNotNull(new SaCheckHttpBasicHandler().getHandlerAnnotationClass());
		Assertions.assertNotNull(new SaCheckHttpDigestHandler().getHandlerAnnotationClass());
		Assertions.assertNotNull(new SaCheckOrHandler().getHandlerAnnotationClass());
		Assertions.assertNotNull(new SaIgnoreHandler().getHandlerAnnotationClass());
	}

	/** SaIgnoreHandler 校验时应抛出 StopMatchException 终止路由 */
	@Test
	void saIgnoreHandler_checkMethod_stopsRouter() throws Exception {
		Method method = Sample.class.getMethod("ignored");
		SaIgnore annotation = method.getAnnotation(SaIgnore.class);
		SaTokenContextMockUtil.setMockContext(() -> {
			SaIgnoreHandler handler = new SaIgnoreHandler();
			Assertions.assertThrows(StopMatchException.class,
					() -> handler.checkMethod(annotation, method));
		});
	}

	static class Sample {
		@SaIgnore
		public void ignored() {
		}
	}

}

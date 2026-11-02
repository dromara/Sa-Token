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
package cn.dev33.satoken.plugin;

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.oauth2.annotation.SaCheckAccessToken;
import cn.dev33.satoken.oauth2.annotation.SaCheckClientIdSecret;
import cn.dev33.satoken.oauth2.annotation.SaCheckClientToken;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * OAuth2 插件安装：三个鉴权注解处理器要挂到 SaAnnotationStrategy
 */
public class SaTokenPluginForOAuth2Test {

	/** 用例结束后把三个处理器卸掉，别污染别的测试 */
	@AfterEach
	public void cleanup() {
		SaAnnotationStrategy.instance.removeAnnotationHandler(SaCheckAccessToken.class);
		SaAnnotationStrategy.instance.removeAnnotationHandler(SaCheckClientToken.class);
		SaAnnotationStrategy.instance.removeAnnotationHandler(SaCheckClientIdSecret.class);
	}

	/** install 应该注册三个 handler，并且 getHandlerAnnotationClass 对得上 */
	@Test
	public void install_registersThreeHandlers() {
		new SaTokenPluginForOAuth2().install();
		SaAnnotationHandlerInterface<?> at = SaAnnotationStrategy.instance.annotationHandlerMap.get(SaCheckAccessToken.class);
		SaAnnotationHandlerInterface<?> ct = SaAnnotationStrategy.instance.annotationHandlerMap.get(SaCheckClientToken.class);
		SaAnnotationHandlerInterface<?> cs = SaAnnotationStrategy.instance.annotationHandlerMap.get(SaCheckClientIdSecret.class);
		Assertions.assertNotNull(at);
		Assertions.assertNotNull(ct);
		Assertions.assertNotNull(cs);
		Assertions.assertEquals(SaCheckAccessToken.class, at.getHandlerAnnotationClass());
		Assertions.assertEquals(SaCheckClientToken.class, ct.getHandlerAnnotationClass());
		Assertions.assertEquals(SaCheckClientIdSecret.class, cs.getHandlerAnnotationClass());
	}

}

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
package cn.dev33.satoken.solon;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.solon.integration.SaFirewallCheckFilterForSolon;
import cn.dev33.satoken.solon.integration.SaTokenContextFilterForSolon;
import cn.dev33.satoken.solon.integration.SaTokenCorsFilterForSolon;
import cn.dev33.satoken.solon.model.SaRequestForSolon;
import cn.dev33.satoken.solon.testsupport.TestSolonContext;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.Filter;

/**
 * {@link SaBeanRegister} 策略重写与默认 Bean 工厂测试
 */
@SaTokenTest
public class SaBeanRegisterTest {

	/** 构造时应该把路由匹配和 Request/Response/Storage 创建策略改成 Solon 实现 */
	@Test
	public void constructor_shouldOverrideSaStrategy() {
		new SaBeanRegister();
		Assertions.assertTrue(SaStrategy.instance.routeMatcher.apply("/user/**", "/user/1"));
		Assertions.assertFalse(SaStrategy.instance.routeMatcher.apply("/user/**", "/admin/1"));

		TestSolonContext ctx = new TestSolonContext().path("/x");
		Assertions.assertInstanceOf(SaRequestForSolon.class, SaStrategy.instance.createSaRequest.apply(ctx));
		Assertions.assertInstanceOf(cn.dev33.satoken.solon.model.SaResponseForSolon.class,
				SaStrategy.instance.createSaResponse.apply(ctx));
		Assertions.assertInstanceOf(cn.dev33.satoken.solon.model.SaStorageForSolon.class,
				SaStrategy.instance.createSaStorage.apply(ctx));
	}

	/** 没传入配置时应该 new 一份默认 SaTokenConfig */
	@Test
	public void getSaTokenConfig_null_returnDefault() {
		SaTokenConfig config = new SaBeanRegister().getSaTokenConfig(null);
		Assertions.assertNotNull(config);
	}

	/** 传入配置时应该原样返回 */
	@Test
	public void getSaTokenConfig_notNull_returnSame() {
		SaTokenConfig input = new SaTokenConfig();
		Assertions.assertSame(input, new SaBeanRegister().getSaTokenConfig(input));
	}

	/** 三个内置 Filter 工厂方法应该能创建对应实例 */
	@Test
	public void filterFactories_shouldCreateFilters() {
		SaBeanRegister register = new SaBeanRegister();
		Filter context = register.saTokenContextFilterForSolon();
		Filter cors = register.saTokenCorsFilterForSolon();
		Filter firewall = register.saFirewallCheckFilterForSolon();
		Assertions.assertInstanceOf(SaTokenContextFilterForSolon.class, context);
		Assertions.assertInstanceOf(SaTokenCorsFilterForSolon.class, cors);
		Assertions.assertInstanceOf(SaFirewallCheckFilterForSolon.class, firewall);
	}

}

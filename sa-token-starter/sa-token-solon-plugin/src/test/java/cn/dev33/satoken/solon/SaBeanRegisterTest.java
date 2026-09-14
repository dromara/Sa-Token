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

import cn.dev33.satoken.solon.model.SaRequestForSolon;
import cn.dev33.satoken.solon.testsupport.TestSolonContext;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaBeanRegister} 策略重写测试（默认 Config/Filter 工厂由 integration-beaninject-solon 覆盖）
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

}

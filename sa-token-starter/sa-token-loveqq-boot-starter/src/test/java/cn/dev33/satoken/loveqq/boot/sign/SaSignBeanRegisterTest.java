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
package cn.dev33.satoken.loveqq.boot.sign;

import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.config.SaSignManyConfigWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaSignBeanRegister} 配置工厂测试
 */
public class SaSignBeanRegisterTest {

	/** 配置工厂应该能 new 出单套和 many 配置 */
	@Test
	public void configFactories_notNull() {
		SaSignBeanRegister register = new SaSignBeanRegister();
		Assertions.assertNotNull(register.getSaSignConfig());
		Assertions.assertNotNull(register.getSaSignManyConfigWrapper());
		Assertions.assertTrue(register.getSaSignConfig() instanceof SaSignConfig);
		Assertions.assertTrue(register.getSaSignManyConfigWrapper() instanceof SaSignManyConfigWrapper);
	}

}

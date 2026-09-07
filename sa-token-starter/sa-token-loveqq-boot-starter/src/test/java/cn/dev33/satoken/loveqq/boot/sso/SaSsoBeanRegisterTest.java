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
package cn.dev33.satoken.loveqq.boot.sso;

import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaSsoBeanRegister} 默认配置与模板工厂测试
 */
public class SaSsoBeanRegisterTest {

	/** 配置工厂应该能 new 出 Server/Client 配置 */
	@Test
	public void configFactories_notNull() {
		SaSsoBeanRegister register = new SaSsoBeanRegister();
		Assertions.assertNotNull(register.getSaSsoServerConfig());
		Assertions.assertNotNull(register.getSaSsoClientConfig());
		Assertions.assertTrue(register.getSaSsoServerConfig() instanceof SaSsoServerConfig);
		Assertions.assertTrue(register.getSaSsoClientConfig() instanceof SaSsoClientConfig);
	}

	/** 默认模板工厂方法应该能拿到 Processor 上的模板 */
	@Test
	public void defaultTemplateFactories_callable() {
		SaSsoBeanRegister register = new SaSsoBeanRegister();
		Assertions.assertNotNull(register.getSaSsoServerTemplate());
		Assertions.assertNotNull(register.getSaSsoClientTemplate());
	}

}

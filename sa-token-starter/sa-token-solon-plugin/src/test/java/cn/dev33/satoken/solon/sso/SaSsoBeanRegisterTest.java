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
package cn.dev33.satoken.solon.sso;

import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaSsoBeanRegister} 默认配置与模板工厂测试
 */
public class SaSsoBeanRegisterTest {

	/** 没传入配置时应该 new 默认 Server/Client 配置，有传入就原样返回 */
	@Test
	public void configFactories_nullAndNotNull() {
		SaSsoBeanRegister register = new SaSsoBeanRegister();
		Assertions.assertNotNull(register.getSaSsoServerConfig(null));
		Assertions.assertNotNull(register.getSaSsoClientConfig(null));

		SaSsoServerConfig server = new SaSsoServerConfig();
		SaSsoClientConfig client = new SaSsoClientConfig();
		Assertions.assertSame(server, register.getSaSsoServerConfig(server));
		Assertions.assertSame(client, register.getSaSsoClientConfig(client));
	}

	/** 默认模板工厂方法应该能拿到 Processor 上的模板 */
	@Test
	public void defaultTemplateFactories_callable() {
		SaSsoBeanRegister register = new SaSsoBeanRegister();
		Assertions.assertNotNull(register.getSaSsoServerTemplate());
		Assertions.assertNotNull(register.getSaSsoClientTemplate());
	}

}

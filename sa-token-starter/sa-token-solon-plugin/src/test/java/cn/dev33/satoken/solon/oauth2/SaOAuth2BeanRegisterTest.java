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
package cn.dev33.satoken.solon.oauth2;

import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaOAuth2BeanRegister} 配置工厂测试
 */
public class SaOAuth2BeanRegisterTest {

	/** 没传入配置时应该 new 默认配置，有传入就原样返回 */
	@Test
	public void getSaOAuth2Config_nullAndNotNull() {
		SaOAuth2BeanRegister register = new SaOAuth2BeanRegister();
		Assertions.assertNotNull(register.getSaOAuth2Config(null));
		SaOAuth2ServerConfig input = new SaOAuth2ServerConfig();
		Assertions.assertSame(input, register.getSaOAuth2Config(input));
	}

}

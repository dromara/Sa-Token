/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.solon.sign;

import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.config.SaSignManyConfigWrapper;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * 注册 Sa-Token API 参数签名所需要的 Bean
 *
 * @author click33
 * @since 1.43.0
 */
@Configuration
@Condition(onClass= SaSignManager.class)
public class SaSignBeanRegister {

	/**
	 * 获取 API 参数签名配置对象
	 * @return 配置对象
	 */
	@Bean
	public SaSignConfig getSaSignConfig(@Inject(value = "${sa-token.sign}", required = false) SaSignConfig saSignConfig) {
		if (saSignConfig == null) {
			return new SaSignConfig();
		} else {
			return saSignConfig;
		}
	}

	/**
	 * 获取 API 参数签名 Many 配置对象
	 * @return 配置对象
	 */
	@Bean
	public SaSignManyConfigWrapper getSaSignManyConfigWrapper(@Inject(value = "${sa-token}", required = false) SaSignManyConfigWrapper signManyConfigWrapper) {
		if (signManyConfigWrapper == null) {
			return new SaSignManyConfigWrapper();
		} else {
			return signManyConfigWrapper;
		}
	}

}

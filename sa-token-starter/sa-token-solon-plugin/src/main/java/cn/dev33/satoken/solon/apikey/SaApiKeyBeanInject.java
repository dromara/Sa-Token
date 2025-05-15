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
package cn.dev33.satoken.solon.apikey;

import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

/**
 * 注入 Sa-Token API Key 所需要的 Bean
 * 
 * @author click33
 * @since 1.43.0
 */
@Condition(onClass=SaApiKeyManager.class)
@Configuration
public class SaApiKeyBeanInject {

	/**
	 * 注入 API Key 配置对象
	 *
	 * @param saApiKeyConfig 配置对象
	 */
	@Bean
	@Condition(onBean = SaApiKeyConfig.class)
	public void setSaApiKeyConfig(SaApiKeyConfig saApiKeyConfig) {
		SaApiKeyManager.setConfig(saApiKeyConfig);
	}

	/**
	 * 注入自定义的 API Key 模版方法 Bean
	 *
	 * @param apiKeyTemplate /
	 */
	@Bean
	@Condition(onBean = SaApiKeyTemplate.class)
	public void setSaApiKeyTemplate(SaApiKeyTemplate apiKeyTemplate) {
		SaApiKeyManager.setSaApiKeyTemplate(apiKeyTemplate);
	}

	/**
	 * 注入自定义的 API Key 数据加载器 Bean
	 *
	 * @param apiKeyDataLoader /
	 */
	@Bean
	@Condition(onBean = SaApiKeyDataLoader.class)
	public void setSaApiKeyDataLoader(SaApiKeyDataLoader apiKeyDataLoader) {
		SaApiKeyManager.setSaApiKeyDataLoader(apiKeyDataLoader);
	}

}

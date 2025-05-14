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
package cn.dev33.satoken.spring.apikey;

import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

/**
 * 注入 Sa-Token API Key 所需要的 Bean
 * 
 * @author click33
 * @since 1.43.0
 */
@ConditionalOnClass(SaApiKeyManager.class)
public class SaApiKeyBeanInject {

	/**
	 * 注入 API Key 配置对象
	 *
	 * @param saApiKeyConfig 配置对象
	 */
	@Autowired(required = false)
	public void setSaOAuth2Config(SaApiKeyConfig saApiKeyConfig) {
		SaApiKeyManager.setConfig(saApiKeyConfig);
	}

	/**
	 * 注入自定义的 API Key 模版方法 Bean
	 *
	 * @param apiKeyTemplate /
	 */
	@Autowired(required = false)
	public void setSaApiKeyTemplate(SaApiKeyTemplate apiKeyTemplate) {
		SaApiKeyManager.setSaApiKeyTemplate(apiKeyTemplate);
	}

	/**
	 * 注入自定义的 API Key 数据加载器 Bean
	 *
	 * @param apiKeyDataLoader /
	 */
	@Autowired(required = false)
	public void setSaApiKeyDataLoader(SaApiKeyDataLoader apiKeyDataLoader) {
		SaApiKeyManager.setSaApiKeyDataLoader(apiKeyDataLoader);
	}

}

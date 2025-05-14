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
package cn.dev33.satoken.apikey;

import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoaderDefaultImpl;
import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import cn.dev33.satoken.listener.SaTokenEventCenter;

/**
 * 管理 Sa-Token API Key 所有全局组件
 *
 * @author click33
 * @since 1.43.0
 */
public class SaApiKeyManager {

	/**
	 * API Key 配置 Bean
	 */
	private static volatile SaApiKeyConfig config;
	public static SaApiKeyConfig getConfig() {
		if (config == null) {
			// 初始化默认值
			synchronized (SaApiKeyManager.class) {
				if (config == null) {
					setConfig(new SaApiKeyConfig());
				}
			}
		}
		return config;
	}
	public static void setConfig(SaApiKeyConfig config) {
		SaApiKeyManager.config = config;
	}

	/**
	 * ApiKey 数据加载器
	 */
	private volatile static SaApiKeyDataLoader apiKeyDataLoader;
	public static void setSaApiKeyDataLoader(SaApiKeyDataLoader apiKeyDataLoader) {
		SaApiKeyManager.apiKeyDataLoader = apiKeyDataLoader;
		SaTokenEventCenter.doRegisterComponent("SaApiKeyDataLoader", apiKeyDataLoader);
	}
	public static SaApiKeyDataLoader getSaApiKeyDataLoader() {
		if (apiKeyDataLoader == null) {
			synchronized (SaApiKeyManager.class) {
				if (apiKeyDataLoader == null) {
					SaApiKeyManager.apiKeyDataLoader = new SaApiKeyDataLoaderDefaultImpl();
				}
			}
		}
		return apiKeyDataLoader;
	}

	/**
	 * ApiKey 操作类
	 */
	private volatile static SaApiKeyTemplate apiKeyTemplate;
	public static void setSaApiKeyTemplate(SaApiKeyTemplate apiKeyTemplate) {
		SaApiKeyManager.apiKeyTemplate = apiKeyTemplate;
		SaTokenEventCenter.doRegisterComponent("SaApiKeyTemplate", apiKeyTemplate);
	}
	public static SaApiKeyTemplate getSaApiKeyTemplate() {
		if (apiKeyTemplate == null) {
			synchronized (SaApiKeyManager.class) {
				if (apiKeyTemplate == null) {
					SaApiKeyManager.apiKeyTemplate = new SaApiKeyTemplate();
				}
			}
		}
		return apiKeyTemplate;
	}

}

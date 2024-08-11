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
package cn.dev33.satoken.oauth2;

import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.dataloader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.dataloader.SaOAuth2DataLoaderDefaultImpl;

/**
 * Sa-Token-OAuth2 模块 总控类
 * 
 * @author click33
 * @since 1.19.0
 */
public class SaOAuth2Manager {

	/**
	 * OAuth2 配置 Bean 
	 */
	private static volatile SaOAuth2Config config;
	public static SaOAuth2Config getConfig() {
		if (config == null) {
			// 初始化默认值
			synchronized (SaOAuth2Manager.class) {
				if (config == null) {
					setConfig(new SaOAuth2Config());
				}
			}
		}
		return config;
	}
	public static void setConfig(SaOAuth2Config config) {
		SaOAuth2Manager.config = config;
	}

	/**
	 * OAuth2 数据加载器 Bean
	 */
	private static volatile SaOAuth2DataLoader dataLoader;
	public static SaOAuth2DataLoader getDataLoader() {
		if (dataLoader == null) {
			synchronized (SaOAuth2Manager.class) {
				if (dataLoader == null) {
					setDataLoader(new SaOAuth2DataLoaderDefaultImpl());
				}
			}
		}
		return dataLoader;
	}
	public static void setDataLoader(SaOAuth2DataLoader dataLoader) {
		SaOAuth2Manager.dataLoader = dataLoader;
	}

}

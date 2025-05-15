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
package cn.dev33.satoken.sign;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.template.SaSignTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理 Sa-Token API 参数签名 所有全局组件
 *
 * @author click33
 * @since 1.43.0
 */
public class SaSignManager {

	/**
	 * API 参数签名 配置 Bean
	 */
	private static volatile SaSignConfig config;
	public static SaSignConfig getConfig() {
		if (config == null) {
			// 初始化默认值
			synchronized (SaSignManager.class) {
				if (config == null) {
					setConfig(new SaSignConfig());
				}
			}
		}
		return config;
	}
	public static void setConfig(SaSignConfig config) {
		SaSignManager.config = config;
	}

	/**
	 * API 签名配置 多实例 配置 Bean
	 */
	private static volatile Map<String, SaSignConfig> signMany;
	public static Map<String, SaSignConfig> getSignMany() {
		if (signMany == null) {
			// 初始化默认值
			synchronized (SaSignManager.class) {
				if (signMany == null) {
					setSignMany(new LinkedHashMap<>());
				}
			}
		}
		return signMany;
	}
	public static void setSignMany(Map<String, SaSignConfig> signMany) {
		SaSignManager.signMany = signMany;
	}

	/**
	 * API 参数签名
	 */
	private volatile static SaSignTemplate saSignTemplate;
	public static void setSaSignTemplate(SaSignTemplate saSignTemplate) {
		SaSignManager.saSignTemplate = saSignTemplate;
		SaTokenEventCenter.doRegisterComponent("SaSignTemplate", saSignTemplate);
	}
	public static SaSignTemplate getSaSignTemplate() {
		if (saSignTemplate == null) {
			synchronized (SaManager.class) {
				if (saSignTemplate == null) {
					SaSignManager.saSignTemplate = new SaSignTemplate();
				}
			}
		}
		return saSignTemplate;
	}

}

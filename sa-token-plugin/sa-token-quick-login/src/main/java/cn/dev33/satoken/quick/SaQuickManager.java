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
package cn.dev33.satoken.quick;

import cn.dev33.satoken.quick.config.SaQuickConfig;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * SaQuickManager，持有 SaQuickConfig 配置对象全局引用
 *
 * @author click33
 * @since 1.19.0
 */
public class SaQuickManager {

	/**
	 * 配置文件 Bean 
	 */
	private static volatile SaQuickConfig config;
	public static void setConfig(SaQuickConfig config) {
		SaQuickManager.config = config;
		// 如果配置了 auto=true，则随机生成账号名密码
		if(config.getAuto()) {
			config.setName(SaFoxUtil.getRandomString(8));
			config.setPwd(SaFoxUtil.getRandomString(8));
		}
	}
	public static SaQuickConfig getConfig() {
		if (config == null) {
			synchronized (SaQuickManager.class) {
				if (config == null) {
					setConfig(new SaQuickConfig());
				}
			}
		}
		return config;
	}
	
}

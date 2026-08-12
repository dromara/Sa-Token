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
package cn.dev33.satoken.dao.alone;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Sa-Token 独立 Redisson 连接配置（Redisson 原生 yaml）
 *
 * @author click33
 * @since 1.45.0
 */
@ConfigurationProperties(prefix = SaAloneRedissonProperties.PREFIX)
public class SaAloneRedissonProperties {

	public static final String PREFIX = "sa-token.alone-redisson";

	/**
	 * Redisson 配置文件，例如 classpath:sa-redisson.yml
	 */
	private String file;

	/**
	 * Redisson 原生 yaml 配置内容（与 file 二选一，优先 config）
	 */
	private String config;

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

}

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
package cn.dev33.satoken.config;

/**
 * Sa-Token API Key 相关配置
 *
 * @author click33
 * @since 1.42.0
 */
public class SaApiKeyConfig {

	/**
	 * API Key 前缀
	 */
	private String prefix = "AK-";

	/**
	 * API Key 有效期，-1=永久有效，默认30天 （修改此配置项不会影响到已创建的 API Key）
	 */
	private long timeout = 2592000;

	/**
	 * 框架是否记录索引信息
	 */
	private Boolean isRecordIndex = true;

	/**
	 * 获取 API Key 前缀
	 *
	 * @return /
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * 设置 API Key 前缀
	 *
	 * @param prefix /
	 * @return 对象自身
	 */
	public SaApiKeyConfig setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	/**
	 * 获取 API Key 有效期，-1=永久有效，默认30天 （修改此配置项不会影响到已创建的 API Key）
	 *
	 * @return /
	 */
	public long getTimeout() {
		return this.timeout;
	}

	/**
	 * 设置 API Key 有效期，-1=永久有效，默认30天 （修改此配置项不会影响到已创建的 API Key）
	 *
	 * @param timeout /
	 * @return 对象自身
	 */
	public SaApiKeyConfig setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * 获取 框架是否保存索引信息
	 *
	 * @return /
	 */
	public Boolean getIsRecordIndex() {
		return this.isRecordIndex;
	}

	/**
	 * 设置 框架是否保存索引信息
	 *
	 * @param isRecordIndex /
	 * @return 对象自身
	 */
	public SaApiKeyConfig setIsRecordIndex(Boolean isRecordIndex) {
		this.isRecordIndex = isRecordIndex;
		return this;
	}

	@Override
	public String toString() {
		return "SaApiKeyConfig{" +
				"prefix='" + prefix + '\'' +
				", timeout=" + timeout +
				", isRecordIndex=" + isRecordIndex +
				'}';
	}

}

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
package cn.dev33.satoken.apikey.error;

/**
 * 定义 sa-token-apikey 模块所有异常细分状态码
 * 
 * @author click33
 * @since 1.43.0
 */
public interface SaApiKeyErrorCode {

	/** 无效 API Key */
	int CODE_12301 = 12301;

	/** API Key 已过期 */
	int CODE_12302 = 12302;

	/** API Key 已被禁用 */
	int CODE_12303 = 12303;

	/** API Key 字段自检未通过 */
	int CODE_12304 = 12304;

	/** 未开启索引记录功能却调用了相关 API */
	int CODE_12305 = 12305;

	/** API Key 不具有指定 Scope */
	int CODE_12311 = 12311;

	/** API Key 不属于指定用户 */
	int CODE_12312 = 12312;

}

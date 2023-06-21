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
package cn.dev33.satoken.temp.jwt.error;

/**
 * 定义 sa-token-temp-jwt 所有异常细分状态码 
 * 
 * @author click33
 * @since 1.33.0
 */
public interface SaTempJwtErrorCode {

	/** jwt 模式没有提供秘钥 */
	int CODE_30301 = 30301;

	/** jwt 模式不可以删除 Token */
	int CODE_30302 = 30302;

	/** Token已超时 */
	int CODE_30303 = 30303;

}

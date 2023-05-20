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
package cn.dev33.satoken.jwt.error;

/**
 * 定义 sa-token-jwt 所有异常细分状态码 
 * 
 * @author click33
 * @since 1.33.0
 */
public interface SaJwtErrorCode {

	/** 对 jwt 字符串解析失败 */
	int CODE_30201 = 30201;

	/** 此 jwt 的签名无效 */
	int CODE_30202 = 30202;

	/** 此 jwt 的 loginType 字段不符合预期 */
	int CODE_30203 = 30203;

	/** 此 jwt 已超时 */
	int CODE_30204 = 30204;

	/** 没有配置jwt秘钥 */
	int CODE_30205 = 30205;

	/** 登录时提供的账号id为空 */
	int CODE_30206 = 30206;

}

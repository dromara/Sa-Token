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
package cn.dev33.satoken.sign.error;

/**
 * 定义 sa-token-sign 模块所有异常细分状态码
 * 
 * @author click33
 * @since 1.43.0
 */
public interface SaSignErrorCode {

	/** 参与参数签名的秘钥不可为空 */
	int CODE_12201 = 12201;

	/** 给定的签名无效 */
	int CODE_12202 = 12202;

	/** timestamp 超出允许的范围 */
	int CODE_12203 = 12203;

	/** 未找到对应 appid 的 SaSignConfig */
	int CODE_12211 = 12211;

}

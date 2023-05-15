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
package cn.dev33.satoken.reactor.error;

/**
 * 定义 sa-token-reactor3-spring-boot-starter 所有异常细分状态码
 * 
 * @author click33
 * @since <= 1.34.0
 */
public interface SaReactorSpringBootErrorCode {
	
	/** 对象转 JSON 字符串失败 */
	int CODE_20203 = 20203;

	/** JSON 字符串转 Map 失败 */
	int CODE_20204 = 20204;

	/** 默认的 Filter 异常处理函数 */
	int CODE_20205 = 20205;

}

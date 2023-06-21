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
package cn.dev33.satoken.sso.error;

/**
 * 定义 sa-token-sso 所有异常细分状态码 
 * 
 * @author click33
 * @since 1.33.0
 */
public interface SaSsoErrorCode {

	/** redirect 重定向 url 是一个无效地址 */
	int CODE_30001 = 30001;

	/** redirect 重定向 url 不在 allowUrl 允许的范围内 */
	int CODE_30002 = 30002;

	/** 接口调用方提供的 secretkey 秘钥无效 */
	int CODE_30003 = 30003;

	/** 提供的 ticket 是无效的 */
	int CODE_30004 = 30004;

	/** 在模式三下，sso-client 调用 sso-server 端 校验ticket接口 时，得到的响应是校验失败 */
	int CODE_30005 = 30005;

	/** 在模式三下，sso-client 调用 sso-server 端 单点注销接口 时，得到的响应是注销失败 */
	int CODE_30006 = 30006;

	/** http 请求调用 提供的 timestamp 与当前时间的差距超出允许的范围 */
	int CODE_30007 = 30007;

	/** http 请求调用 提供的 sign 无效 */
	int CODE_30008 = 30008;

	/** 本地系统没有配置 secretkey 字段 */
	int CODE_30009 = 30009;

	/** 本地系统没有配置 http 请求处理器 */
	int CODE_30010 = 30010;

	/** 该 ticket 不属于当前 client */
	int CODE_30011 = 30011;

	/** 当前缺少配置 server-url 地址 */
	int CODE_30012 = 30012;

}

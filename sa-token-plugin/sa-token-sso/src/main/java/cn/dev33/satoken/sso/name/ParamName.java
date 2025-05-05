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
package cn.dev33.satoken.sso.name;

/**
 * SSO 模块所有参数名称定义 
 * 
 * @author click33
 * @since 1.32.0
 */
public class ParamName {

	/** redirect 参数名称 */
	public String redirect = "redirect";
	
	/** ticket 参数名称 */
	public String ticket = "ticket";

	/** back 参数名称 */
	public String back = "back";

	/** mode 参数名称 */
	public String mode = "mode";
	
	/** 账号 id */
	public String loginId = "loginId";

	/** client 应用标识 */
	public String client = "client";

	/** token 名称 */
	public String tokenName = "tokenName";

	/** token 值 */
	public String tokenValue = "tokenValue";

	/** 设备 id */
	public String deviceId = "deviceId";

	/** 接口参数签名秘钥 */
	public String secretkey = "secretkey";
	
	/** Client 端单点注销时 - 回调 URL 参数名称 */
	public String ssoLogoutCall = "ssoLogoutCall";

	/** 是否为超过 maxRegClient 触发的自动注销 */
	public String autoLogout = "autoLogout";

	public String name = "name";
	public String pwd = "pwd";
	
	public String timestamp = "timestamp";
	public String nonce = "nonce";
	public String sign = "sign";

	/** Session 剩余有效期 参数名称 */
	public String remainSessionTimeout = "remainSessionTimeout";

	/** token 剩余有效期 参数名称 */
	public String remainTokenTimeout = "remainTokenTimeout";

	/** 是否单设备 id 注销 */
	public String singleDeviceIdLogout = "singleDeviceIdLogout";


}

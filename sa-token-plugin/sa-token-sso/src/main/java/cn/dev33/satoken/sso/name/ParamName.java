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

	/** redirect参数名称 */
	public String redirect = "redirect";
	
	/** ticket参数名称 */
	public String ticket = "ticket";

	/** back参数名称 */
	public String back = "back";

	/** mode参数名称 */
	public String mode = "mode";
	
	/** loginId参数名称 */
	public String loginId = "loginId";

	/** client参数名称 */
	public String client = "client";

	/** tokenValue 参数 */
	public String tokenValue = "tokenValue";

	/** deviceId 参数名称 */
	public String deviceId = "deviceId";

	/** secretkey参数名称 */
	public String secretkey = "secretkey";
	
	/** Client端单点注销时-回调URL 参数名称 */
	public String ssoLogoutCall = "ssoLogoutCall";

	/** 是否为超过 maxRegClient 的自动注销 */
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

	/** singleDeviceIdLogout 参数 */
	public String singleDeviceIdLogout = "singleDeviceIdLogout";

}

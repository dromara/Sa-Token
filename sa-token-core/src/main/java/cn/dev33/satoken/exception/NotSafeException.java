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
package cn.dev33.satoken.exception;

/**
 * 一个异常：代表会话未能通过二级认证校验 
 * 
 * @author click33
 * @since <= 1.34.0
 */
public class NotSafeException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130144L;
	
	/** 异常提示语 */
	public static final String BE_MESSAGE = "二级认证校验失败";

	/**
	 * 账号类型 
	 */
	private final String loginType;

	/**
	 * 未通过校验的 Token 值 
	 */
	private final Object tokenValue;

	/**
	 * 未通过校验的服务 
	 */
	private final String service;

	/**
	 * 获取：账号类型 
	 * 
	 * @return / 
	 */
	public String getLoginType() {
		return loginType;
	}

	/**
	 * 获取: 未通过校验的 Token 值  
	 * 
	 * @return / 
	 */
	public Object getTokenValue() {
		return tokenValue;
	}

	/**
	 * 获取: 未通过校验的服务 
	 * 
	 * @return / 
	 */
	public Object getService() {
		return service;
	}

	/**
	 * 一个异常：代表会话未能通过二级认证校验
	 * 
	 * @param loginType 账号类型
	 * @param tokenValue  未通过校验的 Token 值  
	 * @param service  未通过校验的服务 
	 */
	public NotSafeException(String loginType, String tokenValue, String service) {
		super(BE_MESSAGE + "：" + service);
		this.tokenValue = tokenValue;
		this.loginType = loginType;
		this.service = service;
	}

}

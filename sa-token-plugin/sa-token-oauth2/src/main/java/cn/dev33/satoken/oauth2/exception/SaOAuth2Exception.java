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
package cn.dev33.satoken.oauth2.exception;

import cn.dev33.satoken.exception.SaTokenException;

/**
 * 一个异常：代表OAuth2认证流程错误 
 * 
 * @author click33
 * @since 1.33.0
 */
public class SaOAuth2Exception extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130114L;

	/**
	 * 一个异常：代表OAuth2认证流程错误
	 * @param cause 根异常原因
	 */
	public SaOAuth2Exception(Throwable cause) {
		super(cause);
	}

	/**
	 * 一个异常：代表OAuth2认证流程错误 
	 * @param message 异常描述 
	 */
	public SaOAuth2Exception(String message) {
		super(message);
	}

	/**
	 * 如果flag==true，则抛出message异常 
	 * @param flag 标记
	 * @param message 异常信息 
	 * @param code 异常细分码 
	 */
	public static void throwBy(boolean flag, String message, int code) {
		if(flag) {
			throw new SaOAuth2Exception(message).setCode(code);
		}
	}
	
}

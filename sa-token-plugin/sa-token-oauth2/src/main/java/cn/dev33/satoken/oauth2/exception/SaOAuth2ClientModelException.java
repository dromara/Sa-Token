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

/**
 * 一个异常：代表 ClientModel 相关错误
 * 
 * @author click33
 * @since 1.39.0
 */
public class SaOAuth2ClientModelException extends SaOAuth2Exception {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130114L;

	/**
	 * 一个异常：代表 ClientModel 相关错误
	 * @param cause 根异常原因
	 */
	public SaOAuth2ClientModelException(Throwable cause) {
		super(cause);
	}

	/**
	 * 一个异常：代表 ClientModel 相关错误
	 * @param message 异常描述
	 */
	public SaOAuth2ClientModelException(String message) {
		super(message);
	}

	/**
     * 具体引起异常的 ClientId 值
	 */
	public String clientId;

	public String getClientId() {
		return clientId;
	}

	public SaOAuth2ClientModelException setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	/**
	 * 如果 flag==true，则抛出 message 异常
	 * @param flag 标记
	 * @param message 异常信息 
	 * @param code 异常细分码 
	 */
	public static void throwBy(boolean flag, String message, int code) {
		if(flag) {
			throw new SaOAuth2ClientModelException(message).setCode(code);
		}
	}

	/**
	 * 如果 flag==true，则抛出 message 异常
	 * @param flag 标记
	 * @param message 异常信息
	 * @param clientId 应用id
	 * @param code 异常细分码
	 */
	public static void throwBy(boolean flag, String message, String clientId, int code) {
		if(flag) {
			throw new SaOAuth2ClientModelException(message).setClientId(clientId).setCode(code);
		}
	}

}

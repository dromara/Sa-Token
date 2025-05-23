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
package cn.dev33.satoken.oauth2.data.model.request;

import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.util.SaFoxUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 请求授权参数的 Model
 *
 * @author click33
 * @since 1.23.0
 */
public class RequestAuthModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * 应用id 
	 */
	public String clientId;
	 
	/**
	 * 授权范围
	 */
	public List<String> scopes;
	
	/**
	 * 对应的账号id 
	 */
	public Object loginId;
	
	/**
	 * 待重定向URL
	 */
	public String redirectUri; 
	
	/**
	 * 授权类型, 非必填 
	 */
	public String responseType;

	/**
	 * 状态标识, 可为null 
	 */
	public String state;

	/**
	 * 随机数
	 */
	public String nonce;

	
	/**
	 * @return clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId 要设置的 clientId
	 * @return 对象自身
	 */
	public RequestAuthModel setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	/**
	 * @return scopes
	 */
	public List<String> getScopes() {
		return scopes;
	}

	/**
	 * @param scopes 要设置的 scopes
	 * @return 对象自身
	 */
	public RequestAuthModel setScopes(List<String> scopes) {
		this.scopes = scopes;
		return this;
	}

	/**
	 * @return loginId
	 */
	public Object getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId 要设置的 loginId
	 * @return 对象自身
	 */
	public RequestAuthModel setLoginId(Object loginId) {
		this.loginId = loginId;
		return this;
	}

	/**
	 * @return redirectUri
	 */
	public String getRedirectUri() {
		return redirectUri;
	}

	/**
	 * @param redirectUri 要设置的 redirectUri
	 * @return 对象自身
	 */
	public RequestAuthModel setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
		return this;
	}

	/**
	 * @return responseType
	 */
	public String getResponseType() {
		return responseType;
	}

	/**
	 * @param responseType 要设置的 responseType
	 * @return 对象自身
	 */
	public RequestAuthModel setResponseType(String responseType) {
		this.responseType = responseType;
		return this;
	}
	
	/**
	 * @return state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state 要设置的 state
	 * @return 对象自身
	 */
	public RequestAuthModel setState(String state) {
		this.state = state;
		return this;
	}

	/**
	 * @return nonce
	 */
	public String getNonce() {
		return nonce;
	}

	/**
	 * @param nonce 要设置的随机数
	 * @return 对象自身
	 */
	public RequestAuthModel setNonce(String nonce) {
		this.nonce = nonce;
		return this;
	}

	/**
	 * 数据自检
	 * @return 对象自身
	 */
	public RequestAuthModel checkModel() {
		if(SaFoxUtil.isEmpty(clientId)) {
			throw new SaOAuth2Exception("client_id 不可为空").setCode(SaOAuth2ErrorCode.CODE_30101);
		}
		if(SaFoxUtil.isEmpty(scopes)) {
			throw new SaOAuth2Exception("scope 不可为空").setCode(SaOAuth2ErrorCode.CODE_30102);
		}
		if(SaFoxUtil.isEmpty(redirectUri)) {
			throw new SaOAuth2Exception("redirect_uri 不可为空").setCode(SaOAuth2ErrorCode.CODE_30103);
		}
		if(SaFoxUtil.isEmpty(String.valueOf(loginId))) {
			throw new SaOAuth2Exception("LoginId 不可为空").setCode(SaOAuth2ErrorCode.CODE_30104);
		}
		return this;
	}

	@Override
	public String toString() {
		return "RequestAuthModel{" +
				"clientId='" + clientId + '\'' +
				", scopes=" + scopes +
				", loginId=" + loginId +
				", redirectUri='" + redirectUri + '\'' +
				", responseType='" + responseType + '\'' +
				", state='" + state + '\'' +
				", nonce='" + nonce + '\'' +
				'}';
	}

}

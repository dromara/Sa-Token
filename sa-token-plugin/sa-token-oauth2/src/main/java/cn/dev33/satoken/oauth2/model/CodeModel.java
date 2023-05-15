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
package cn.dev33.satoken.oauth2.model;

import java.io.Serializable;

/**
 * Model: 授权码
 *
 * @author click33
 * @since <= 1.34.0
 */
public class CodeModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/** 
	 * 授权码 
	 */
	public String code;
	
	/**
	 * 应用id 
	 */
	public String clientId;
	
	/**
	 * 授权范围
	 */
	public String scope;

	/**
	 * 对应账号id 
	 */
	public Object loginId;

	/**
	 * 重定向的地址 
	 */
	public String redirectUri;
	
	/**
	 * 构建一个 
	 */
	public CodeModel() {
		
	}
	/**
	 * 构建一个 
	 * @param code 授权码 
	 * @param clientId 应用id 
	 * @param scope 请求授权范围 
	 * @param loginId 对应的账号id 
	 * @param redirectUri 重定向地址 
	 */
	public CodeModel(String code, String clientId, String scope, Object loginId, String redirectUri) {
		super();
		this.code = code;
		this.clientId = clientId;
		this.scope = scope;
		this.loginId = loginId;
		this.redirectUri = redirectUri;
	}

	
	/**
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code 要设置的 code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId 要设置的 clientId
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param scope 要设置的 scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @return loginId
	 */
	public Object getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId 要设置的 loginId
	 */
	public void setLoginId(Object loginId) {
		this.loginId = loginId;
	}
	
	/**
	 * @return redirectUri
	 */
	public String getRedirectUri() {
		return redirectUri;
	}
	
	/**
	 * @param redirectUri 要设置的 redirectUri
	 */
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}
	
	@Override
	public String toString() {
		return "CodeModel [code=" + code + ", clientId=" + clientId + ", scope=" + scope + ", loginId=" + loginId
				+ ", redirectUri=" + redirectUri + "]";
	}
	
}

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
package cn.dev33.satoken.oauth2.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Model: 授权码
 *
 * @author click33
 * @since 1.23.0
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
	public List<String> scopes;

	/**
	 * 对应账号id 
	 */
	public Object loginId;

	/**
	 * 重定向的地址 
	 */
	public String redirectUri;

	/**
	 * 随机数
	 */
	public String nonce;

	/**
	 * 创建时间，13位时间戳
	 */
	public long createTime;
	
	/**
	 * 构建一个 
	 */
	public CodeModel() {
		this.createTime = System.currentTimeMillis();
	}
	/**
	 * 构建一个 
	 * @param code 授权码 
	 * @param clientId 应用id 
	 * @param scopes 请求授权范围
	 * @param loginId 对应的账号id 
	 * @param redirectUri 重定向地址 
	 */
	public CodeModel(String code, String clientId, List<String> scopes, Object loginId, String redirectUri, String nonce) {
		this();
		this.code = code;
		this.clientId = clientId;
		this.scopes = scopes;
		this.loginId = loginId;
		this.redirectUri = redirectUri;
		this.nonce = nonce;
	}

	public String getCode() {
		return code;
	}

	public CodeModel setCode(String code) {
		this.code = code;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public CodeModel setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public CodeModel setScopes(List<String> scopes) {
		this.scopes = scopes;
		return this;
	}

	public Object getLoginId() {
		return loginId;
	}

	public CodeModel setLoginId(Object loginId) {
		this.loginId = loginId;
		return this;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public CodeModel setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
		return this;
	}

	public String getNonce() {
		return nonce;
	}

	public CodeModel setNonce(String nonce) {
		this.nonce = nonce;
		return this;
	}

	public long getCreateTime() {
		return createTime;
	}

	public CodeModel setCreateTime(long createTime) {
		this.createTime = createTime;
		return this;
	}

	@Override
	public String toString() {
		return "CodeModel{" +
				"code='" + code + '\'' +
				", clientId='" + clientId + '\'' +
				", scopes=" + scopes +
				", loginId=" + loginId +
				", redirectUri='" + redirectUri + '\'' +
				", nonce='" + nonce + '\'' +
				", createTime=" + createTime +
				'}';
	}

}

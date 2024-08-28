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
import java.util.Map;

/**
 * Model: Refresh-Token
 *
 * @author click33
 * @since 1.23.0
 */
public class RefreshTokenModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;
 
	/**
	 * Refresh-Token 值
	 */
	public String refreshToken;

	/**
	 * Refresh-Token 到期时间 
	 */
	public long expiresTime;
	
	/**
	 * 应用id 
	 */
	public String clientId;

	/**
	 * 对应账号id 
	 */
	public Object loginId;

	/**
	 * 授权范围
	 */
	public List<String> scopes;

	/**
	 * 扩展数据
	 */
	public Map<String, Object> extraData;


	public String getRefreshToken() {
		return refreshToken;
	}

	public RefreshTokenModel setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public RefreshTokenModel setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public RefreshTokenModel setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public RefreshTokenModel setScopes(List<String> scopes) {
		this.scopes = scopes;
		return this;
	}

	public Object getLoginId() {
		return loginId;
	}

	public RefreshTokenModel setLoginId(Object loginId) {
		this.loginId = loginId;
		return this;
	}

	public Map<String, Object> getExtraData() {
		return extraData;
	}

	public RefreshTokenModel setExtraData(Map<String, Object> extraData) {
		this.extraData = extraData;
		return this;
	}

	@Override
	public String toString() {
		return "RefreshTokenModel [refreshToken=" + refreshToken + ", expiresTime=" + expiresTime
				+ ", clientId=" + clientId + ", loginId=" + loginId + ", scopes=" + scopes + ", extraData=" + extraData + "]";
	}

	/**
	 * 获取：此 Refresh-Token 的剩余有效期（秒）
	 * @return /
	 */
	public long getExpiresIn() {
		long s = (expiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}
	
}

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Model: Access-Token
 *
 * @author click33
 * @since 1.23.0
 */
public class AccessTokenModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * Access-Token 值
	 */
	public String accessToken;
	
	/**
	 * Refresh-Token 值
	 */
	public String refreshToken;
	
	/**
	 * Access-Token 到期时间 
	 */
	public long expiresTime;

	/**
	 * Refresh-Token 到期时间   
	 */
	public long refreshExpiresTime;

	/**
	 * 应用id 
	 */
	public String clientId;

	/**
	 * 账号id 
	 */
	public Object loginId;
	
	/**
	 * 开放账号id 
	 */
	public String openid;

	/**
	 * 授权范围
	 */
	public List<String> scopes;

	public AccessTokenModel() {}

	/**
	 * 构建一个 
	 * @param accessToken accessToken
	 * @param clientId 应用id 
	 * @param scopes 请求授权范围
	 * @param loginId 对应的账号id 
	 */
	public AccessTokenModel(String accessToken, String clientId, Object loginId, List<String> scopes) {
		super();
		this.accessToken = accessToken;
		this.clientId = clientId;
		this.loginId = loginId;
		this.scopes = scopes;
	}


	public String getAccessToken() {
		return accessToken;
	}

	public AccessTokenModel setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		return this;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public AccessTokenModel setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public AccessTokenModel setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
		return this;
	}

	public long getRefreshExpiresTime() {
		return refreshExpiresTime;
	}

	public AccessTokenModel setRefreshExpiresTime(long refreshExpiresTime) {
		this.refreshExpiresTime = refreshExpiresTime;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public AccessTokenModel setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public Object getLoginId() {
		return loginId;
	}

	public AccessTokenModel setLoginId(Object loginId) {
		this.loginId = loginId;
		return this;
	}

	public String getOpenid() {
		return openid;
	}

	public AccessTokenModel setOpenid(String openid) {
		this.openid = openid;
		return this;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public AccessTokenModel setScopes(List<String> scopes) {
		this.scopes = scopes;
		return this;
	}

	@Override
	public String toString() {
		return "AccessTokenModel [accessToken=" + accessToken + ", refreshToken=" + refreshToken
				+ ", accessTokenTimeout=" + expiresTime + ", refreshTokenTimeout=" + refreshExpiresTime
				+ ", clientId=" + clientId + ", scopes=" + scopes + ", openid=" + openid + "]";
	}

	// 追加只读属性

	/**
	 * 获取：此 Access-Token 的剩余有效期（秒）
	 * @return see note 
	 */
	public long getExpiresIn() {
		long s = (expiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}

	/**
	 * 获取：此 Refresh-Token 的剩余有效期（秒）
	 * @return see note 
	 */
	public long getRefreshExpiresIn() {
		long s = (refreshExpiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}
	
	/**
	 * 将所有属性转换为下划线形式的Map 
	 * @return 属性转Map
	 */
	@Deprecated
	public Map<String, Object> toLineMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("access_token", accessToken);
		map.put("refresh_token", refreshToken);
		map.put("expires_in", getExpiresIn());
		map.put("refresh_expires_in", getRefreshExpiresIn());
		map.put("client_id", clientId);
		map.put("scopes", scopes);
		map.put("openid", openid);
		return map;
	}
	
}

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
 * Model: Client-Token
 *
 * @author click33
 * @since 1.23.0
 */
public class ClientTokenModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * Client-Token 值
	 */
	public String clientToken;
	
	/**
	 * Client-Token 到期时间 
	 */
	public long expiresTime;

	/**
	 * 应用id 
	 */
	public String clientId;

	/**
	 * 授权范围
	 */
	public List<String> scopes;

	/**
	 * Token 类型
	 */
	public String tokenType;

	/**
	 * 授权类型
	 */
	public String grantType;

	/**
	 * 扩展数据
	 */
	public Map<String, Object> extraData;

	/**
	 * 创建时间，13位时间戳
	 */
	public long createTime;

	public ClientTokenModel(){
		this.createTime = System.currentTimeMillis();
	}

	/**
	 * 构建一个 ClientTokenModel
	 * @param clientToken clientToken
	 * @param clientId 应用id
	 * @param scopes 请求授权范围
	 */
	public ClientTokenModel(String clientToken, String clientId, List<String> scopes) {
		this();
		this.clientToken = clientToken;
		this.clientId = clientId;
		this.scopes = scopes;
	}

	// 额外追加方法

	/**
	 * 获取：此 Client-Token 的剩余有效期（秒）
	 * @return /
	 */
	public long getExpiresIn() {
		long s = (expiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}


	// get set

	public String getClientToken() {
		return clientToken;
	}

	public ClientTokenModel setClientToken(String clientToken) {
		this.clientToken = clientToken;
		return this;
	}

	public long getExpiresTime() {
		return expiresTime;
	}

	public ClientTokenModel setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public ClientTokenModel setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public List<String> getScopes() {
		return scopes;
	}

	public ClientTokenModel setScopes(List<String> scopes) {
		this.scopes = scopes;
		return this;
	}

	public String getTokenType() {
		return tokenType;
	}

	public ClientTokenModel setTokenType(String tokenType) {
		this.tokenType = tokenType;
		return this;
	}

	public String getGrantType() {
		return grantType;
	}

	public ClientTokenModel setGrantType(String grantType) {
		this.grantType = grantType;
		return this;
	}

	public Map<String, Object> getExtraData() {
		return extraData;
	}

	public ClientTokenModel setExtraData(Map<String, Object> extraData) {
		this.extraData = extraData;
		return this;
	}

	public long getCreateTime() {
		return createTime;
	}

	public ClientTokenModel setCreateTime(long createTime) {
		this.createTime = createTime;
		return this;
	}

	@Override
	public String toString() {
		return "ClientTokenModel{" +
				"clientToken='" + clientToken +
				", expiresTime=" + expiresTime +
				", clientId='" + clientId +
				", scopes=" + scopes +
				", tokenType=" + tokenType +
				", grantType=" + grantType +
				", extraData=" + extraData +
				", createTime=" + createTime +
				'}';
	}

}

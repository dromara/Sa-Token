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
	public String scope;  

	public ClientTokenModel() {}
	
	/**
	 * 构建一个 
	 * @param accessToken accessToken
	 * @param clientId 应用id 
	 * @param scope 请求授权范围 
	 */
	public ClientTokenModel(String accessToken, String clientId, String scope) {
		super();
		this.clientToken = accessToken;
		this.clientId = clientId;
		this.scope = scope;
	}
	
	@Override
	public String toString() {
		return "ClientTokenModel [clientToken=" + clientToken + ", expiresTime=" + expiresTime + ", clientId="
				+ clientId + ", scope=" + scope + "]";
	}
	
	/**
	 * 获取：此 Client-Token 的剩余有效期（秒）
	 * @return see note 
	 */
	public long getExpiresIn() {
		long s = (expiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}
	
	/**
	 * 将所有属性转换为下划线形式的Map 
	 * @return 属性转Map 
	 */
	public Map<String, Object> toLineMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("client_token", clientToken);
		map.put("expires_in", getExpiresIn());
		map.put("client_id", clientId);
		map.put("scope", scope);	
		return map;
	}
	
	
}

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
	 * 授权范围
	 */
	public String scope;

	/**
	 * 对应账号id 
	 */
	public Object loginId;

	/**
	 * 对应账号id 
	 */
	public String openid;

	@Override
	public String toString() {
		return "RefreshTokenModel [refreshToken=" + refreshToken + ", expiresTime=" + expiresTime
				+ ", clientId=" + clientId + ", scope=" + scope + ", loginId=" + loginId + ", openid=" + openid + "]";
	}

	/**
	 * 获取：此 Refresh-Token 的剩余有效期（秒）
	 * @return see note 
	 */
	public long getExpiresIn() {
		long s = (expiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}
	
}

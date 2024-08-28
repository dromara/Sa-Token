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
package cn.dev33.satoken.oauth2.config;

import java.io.Serializable;

/**
 * Sa-Token OAuth2 Server 端 Oidc 配置类 Model
 *
 * @author click33
 * @since 1.39.0
 */
public class SaOAuth2OidcConfig implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/** iss 值，如不配置则自动计算 */
	public String iss;

	/** idToken 有效期（单位秒） 默认十分钟 */
	public long idTokenTimeout = 60 * 10;

	public String getIss() {
		return iss;
	}

	public SaOAuth2OidcConfig setIss(String iss) {
		this.iss = iss;
		return this;
	}

	public long getIdTokenTimeout() {
		return idTokenTimeout;
	}

	public SaOAuth2OidcConfig setIdTokenTimeout(long idTokenTimeout) {
		this.idTokenTimeout = idTokenTimeout;
		return this;
	}

	@Override
	public String toString() {
		return "SaOAuth2OidcConfig{" +
				"iss='" + iss + '\'' +
				", idTokenTimeout=" + idTokenTimeout +
				'}';
	}

}

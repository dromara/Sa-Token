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

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;

/**
 * Client应用信息 Model
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaClientModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * 应用id 
	 */
	public String clientId;
	
	/**
	 * 应用秘钥 
	 */
	public String clientSecret;

	/**
	 * 应用签约的所有权限, 多个用逗号隔开 
	 */
	public String contractScope;
	
	/**
	 * 应用允许授权的所有URL, 多个用逗号隔开 
	 */
	public String allowUrl;
	
	/** 此 Client 是否打开模式：授权码（Authorization Code） */
	public Boolean isCode = false;

	/** 此 Client 是否打开模式：隐藏式（Implicit） */
	public Boolean isImplicit = false;

	/** 此 Client 是否打开模式：密码式（Password） */
	public Boolean isPassword = false;

	/** 此 Client 是否打开模式：凭证式（Client Credentials） */
	public Boolean isClient = false;

	/** 
	 * 是否自动判断此 Client 开放的授权模式 
	 * <br> 此值为true时：四种模式（isCode、isImplicit、isPassword、isClient）是否生效，依靠全局设置
	 * <br> 此值为false时：四种模式（isCode、isImplicit、isPassword、isClient）是否生效，依靠局部配置+全局配置 
	 */
	public Boolean isAutoMode = true;

	/** 单独配置此Client：是否在每次 Refresh-Token 刷新 Access-Token 时，产生一个新的 Refresh-Token [默认取全局配置] */
	public Boolean isNewRefresh;

	/** 单独配置此Client：Access-Token 保存的时间(单位秒)  [默认取全局配置] */
	public long accessTokenTimeout;

	/** 单独配置此Client：Refresh-Token 保存的时间(单位秒) [默认取全局配置] */
	public long refreshTokenTimeout;

	/** 单独配置此Client：Client-Token 保存的时间(单位秒) [默认取全局配置] */
	public long clientTokenTimeout;

	/** 单独配置此Client：Past-Client-Token 保存的时间(单位：秒) [默认取全局配置] */
	public long pastClientTokenTimeout;

	
	public SaClientModel() {
		SaOAuth2Config config = SaOAuth2Manager.getConfig();
		this.isNewRefresh = config.getIsNewRefresh();
		this.accessTokenTimeout = config.getAccessTokenTimeout();
		this.refreshTokenTimeout = config.getRefreshTokenTimeout();
		this.clientTokenTimeout = config.getClientTokenTimeout();
		this.pastClientTokenTimeout = config.getPastClientTokenTimeout();
	}
	public SaClientModel(String clientId, String clientSecret, String contractScope, String allowUrl) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.contractScope = contractScope;
		this.allowUrl = allowUrl;
	}

	/**
	 * @return 应用id
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId 应用id 
	 * @return 对象自身 
	 */
	public SaClientModel setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	/**
	 * @return 应用秘钥 
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * @param clientSecret 应用秘钥 
	 * @return 对象自身 
	 */
	public SaClientModel setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	/**
	 * @return 应用签约的所有权限, 多个用逗号隔开
	 */
	public String getContractScope() {
		return contractScope;
	}

	/**
	 * @param contractScope 应用签约的所有权限, 多个用逗号隔开
	 * @return 对象自身 
	 */
	public SaClientModel setContractScope(String contractScope) {
		this.contractScope = contractScope;
		return this;
	}

	/**
	 * @return 应用允许授权的所有URL, 多个用逗号隔开
	 */
	public String getAllowUrl() {
		return allowUrl;
	}

	/**
	 * @param allowUrl 应用允许授权的所有URL, 多个用逗号隔开
	 * @return 对象自身 
	 */
	public SaClientModel setAllowUrl(String allowUrl) {
		this.allowUrl = allowUrl;
		return this;
	}

	/**
	 * @return 此 Client 是否打开模式：授权码（Authorization Code）
	 */
	public Boolean getIsCode() {
		return isCode;
	}
	
	/**
	 * @param isCode 此 Client 是否打开模式：授权码（Authorization Code）
	 * @return 对象自身 
	 */
	public SaClientModel setIsCode(Boolean isCode) {
		this.isCode = isCode;
		return this;
	}
	
	/**
	 * @return 此 Client 是否打开模式：隐藏式（Implicit）
	 */
	public Boolean getIsImplicit() {
		return isImplicit;
	}
	
	/**
	 * @param isImplicit 此 Client 是否打开模式：隐藏式（Implicit）
	 * @return 对象自身 
	 */
	public SaClientModel setIsImplicit(Boolean isImplicit) {
		this.isImplicit = isImplicit;
		return this;
	}
	
	/**
	 * @return 此 Client 是否打开模式：密码式（Password）
	 */
	public Boolean getIsPassword() {
		return isPassword;
	}
	
	/**
	 * @param isPassword 此 Client 是否打开模式：密码式（Password）
	 * @return 对象自身 
	 */
	public SaClientModel setIsPassword(Boolean isPassword) {
		this.isPassword = isPassword;
		return this;
	}
	
	/**
	 * @return 此 Client 是否打开模式：凭证式（Client Credentials）
	 */
	public Boolean getIsClient() {
		return isClient;
	}
	
	/**
	 * @param isClient 此 Client 是否打开模式：凭证式（Client Credentials）
	 * @return 对象自身 
	 */
	public SaClientModel setIsClient(Boolean isClient) {
		this.isClient = isClient;
		return this;
	}

	/**
	 * @return 是否自动判断此 Client 开放的授权模式
	 */
	public Boolean getIsAutoMode() {
		return isAutoMode;
	}
	
	/**
	 * @param isAutoMode 是否自动判断此 Client 开放的授权模式
	 * @return 对象自身 
	 */
	public SaClientModel setIsAutoMode(Boolean isAutoMode) {
		this.isAutoMode = isAutoMode;
		return this;
	}
	
	
	/**
	 * @return 此Client：是否在每次 Refresh-Token 刷新 Access-Token 时，产生一个新的 Refresh-Token [默认取全局配置]
	 */
	public Boolean getIsNewRefresh() {
		return isNewRefresh;
	}
	
	/**
	 * @param isNewRefresh 单独配置此Client：是否在每次 Refresh-Token 刷新 Access-Token 时，产生一个新的 Refresh-Token [默认取全局配置]
	 * @return 对象自身 
	 */
	public SaClientModel setIsNewRefresh(Boolean isNewRefresh) {
		this.isNewRefresh = isNewRefresh;
		return this;
	}
	
	/**
	 * @return 此Client：Access-Token 保存的时间(单位秒)  [默认取全局配置]
	 */
	public long getAccessTokenTimeout() {
		return accessTokenTimeout;
	}
	
	/**
	 * @param accessTokenTimeout 单独配置此Client：Access-Token 保存的时间(单位秒)  [默认取全局配置]
	 * @return 对象自身 
	 */
	public SaClientModel setAccessTokenTimeout(long accessTokenTimeout) {
		this.accessTokenTimeout = accessTokenTimeout;
		return this;
	}
	
	/**
	 * @return 此Client：Refresh-Token 保存的时间(单位秒) [默认取全局配置]
	 */
	public long getRefreshTokenTimeout() {
		return refreshTokenTimeout;
	}
	
	/**
	 * @param refreshTokenTimeout 单独配置此Client：Refresh-Token 保存的时间(单位秒) [默认取全局配置]
	 * @return 对象自身 
	 */
	public SaClientModel setRefreshTokenTimeout(long refreshTokenTimeout) {
		this.refreshTokenTimeout = refreshTokenTimeout;
		return this;
	}
	
	/**
	 * @return 此Client：Client-Token 保存的时间(单位秒) [默认取全局配置]
	 */
	public long getClientTokenTimeout() {
		return clientTokenTimeout;
	}
	
	/**
	 * @param clientTokenTimeout 单独配置此Client：Client-Token 保存的时间(单位秒) [默认取全局配置]
	 * @return 对象自身 
	 */
	public SaClientModel setClientTokenTimeout(long clientTokenTimeout) {
		this.clientTokenTimeout = clientTokenTimeout;
		return this;
	}
	
	/**
	 * @return 此Client：Past-Client-Token 保存的时间(单位：秒) [默认取全局配置]
	 */
	public long getPastClientTokenTimeout() {
		return pastClientTokenTimeout;
	}
	
	/**
	 * @param pastClientTokenTimeout 单独配置此Client：Past-Client-Token 保存的时间(单位：秒) [默认取全局配置]
	 * @return 对象自身 
	 */
	public SaClientModel setPastClientTokenTimeout(long pastClientTokenTimeout) {
		this.pastClientTokenTimeout = pastClientTokenTimeout;
		return this;
	}
	
	// 
	@Override
	public String toString() {
		return "SaClientModel [clientId=" + clientId + ", clientSecret=" + clientSecret + ", contractScope="
				+ contractScope + ", allowUrl=" + allowUrl + ", isCode=" + isCode + ", isImplicit=" + isImplicit
				+ ", isPassword=" + isPassword + ", isClient=" + isClient + ", isAutoMode=" + isAutoMode
				+ ", isNewRefresh=" + isNewRefresh + ", accessTokenTimeout=" + accessTokenTimeout
				+ ", refreshTokenTimeout=" + refreshTokenTimeout + ", clientTokenTimeout=" + clientTokenTimeout
				+ ", pastClientTokenTimeout=" + pastClientTokenTimeout + "]";
	}
	
	
}

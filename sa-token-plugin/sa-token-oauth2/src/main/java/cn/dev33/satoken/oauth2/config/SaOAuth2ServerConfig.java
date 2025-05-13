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

import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sa-Token OAuth2 Server 端 配置类 Model
 *
 * @author click33
 * @since 1.19.0
 */
public class SaOAuth2ServerConfig implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/** 是否打开模式：授权码（Authorization Code） */
	public Boolean enableAuthorizationCode = true;

	/** 是否打开模式：隐藏式（Implicit） */
	public Boolean enableImplicit = true;

	/** 是否打开模式：密码式（Password） */
	public Boolean enablePassword = true;

	/** 是否打开模式：凭证式（Client Credentials） */
	public Boolean enableClientCredentials = true;

	/** Code授权码 保存的时间(单位：秒) 默认五分钟 */
	public long codeTimeout = 60 * 5;

	/** 全局默认配置所有应用：Access-Token 保存的时间(单位：秒) 默认两个小时 */
	public long accessTokenTimeout = 60 * 60 * 2;

	/** 全局默认配置所有应用：Refresh-Token 保存的时间(单位：秒) 默认30 天 */
	public long refreshTokenTimeout = 60 * 60 * 24 * 30;

	/** 全局默认配置所有应用：Client-Token 保存的时间(单位：秒) 默认两个小时 */
	public long clientTokenTimeout = 60 * 60 * 2;

	/** 全局默认配置所有应用：单个应用单个用户最多同时存在的 Access-Token 数量 */
	public int maxAccessTokenCount = 12;

	/** 全局默认配置所有应用：单个应用单个用户最多同时存在的 Refresh-Token 数量 */
	public int maxRefreshTokenCount = 12;

	/** 全局默认配置所有应用：单个应用最多同时存在的 Client-Token 数量 */
	public int maxClientTokenCount = 12;

	/** 全局默认配置所有应用：是否在每次 Refresh-Token 刷新 Access-Token 时，产生一个新的 Refresh-Token */
	public Boolean isNewRefresh = false;

	/** 默认 openid 生成算法中使用的摘要前缀 */
	public String openidDigestPrefix = SaOAuth2Consts.OPENID_DEFAULT_DIGEST_PREFIX;

	/** 默认 unionid 生成算法中使用的摘要前缀 */
	public String unionidDigestPrefix = SaOAuth2Consts.UNIONID_DEFAULT_DIGEST_PREFIX;

	/** 指定高级权限，多个用逗号隔开 */
	public String higherScope;

	/** 指定低级权限，多个用逗号隔开 */
	public String lowerScope;

	/** 模式4是否返回 AccessToken 字段，以使其更符合 OAuth2 RFC 规范 */
	public Boolean mode4ReturnAccessToken = false;

	/** 是否在返回值中隐藏默认的状态字段 (code、msg、data) */
	public Boolean hideStatusField = false;

	/**
	 * oidc 相关配置
	 */
	SaOAuth2OidcConfig oidc = new SaOAuth2OidcConfig();

	/** client 列表 */
	public Map<String, SaClientModel> clients = new LinkedHashMap<>();

	// 额外方法

	/**
	 * 注册 client
	 * @return /
	 */
	public SaOAuth2ServerConfig addClient(SaClientModel client) {
		if(this.clients == null) {
			this.clients = new LinkedHashMap<>();
		}
		this.clients.put(client.getClientId(), client);
		return this;
	}


	// get set

	/**
	 * 是否打开模式：授权码（Authorization Code）
	 * @return enableAuthorizationCode
	 */
	public Boolean getEnableAuthorizationCode() {
		return enableAuthorizationCode;
	}

	/**
	 * 设置是否打开模式：授权码（Authorization Code）
	 * @param enableAuthorizationCode 是否开启
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setEnableAuthorizationCode(Boolean enableAuthorizationCode) {
		this.enableAuthorizationCode = enableAuthorizationCode;
		return this;
	}

	/**
	 * 是否打开模式：隐藏式（Implicit）
	 * @return enableImplicit
	 */
	public Boolean getEnableImplicit() {
		return enableImplicit;
	}

	/**
	 * 设置是否打开模式：隐藏式（Implicit）
	 * @param enableImplicit 是否开启
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setEnableImplicit(Boolean enableImplicit) {
		this.enableImplicit = enableImplicit;
		return this;
	}

	/**
	 * 是否打开模式：密码式（Password）
	 * @return enablePassword
	 */
	public Boolean getEnablePassword() {
		return enablePassword;
	}

	/**
	 * 设置是否打开模式：密码式（Password）
	 * @param enablePassword 是否开启
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setEnablePassword(Boolean enablePassword) {
		this.enablePassword = enablePassword;
		return this;
	}

	/**
	 * 是否打开模式：凭证式（Client Credentials）
	 * @return enableClientCredentials
	 */
	public Boolean getEnableClientCredentials() {
		return enableClientCredentials;
	}

	/**
	 * 设置是否打开模式：凭证式（Client Credentials）
	 * @param enableClientCredentials 是否开启
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setEnableClientCredentials(Boolean enableClientCredentials) {
		this.enableClientCredentials = enableClientCredentials;
		return this;
	}

	/**
	 * 全局默认配置所有应用：是否在每次 Refresh-Token 刷新 Access-Token 时，产生一个新的 Refresh-Token
	 * @return isNewRefresh
	 */
	public Boolean getIsNewRefresh() {
		return isNewRefresh;
	}

	/**
	 * 全局默认配置所有应用：设置是否在每次 Refresh-Token 刷新 Access-Token 时，产生一个新的 Refresh-Token
	 * @param isNewRefresh 是否开启
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setIsNewRefresh(Boolean isNewRefresh) {
		this.isNewRefresh = isNewRefresh;
		return this;
	}

	/**
	 * Code授权码 保存的时间(单位：秒) 默认五分钟
	 * @return codeTimeout
	 */
	public long getCodeTimeout() {
		return codeTimeout;
	}

	/**
	 * 设置Code授权码保存的时间(单位：秒)
	 * @param codeTimeout 保存时间(秒)
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setCodeTimeout(long codeTimeout) {
		this.codeTimeout = codeTimeout;
		return this;
	}

	/**
	 * 全局默认配置所有应用：Access-Token 保存的时间(单位：秒) 默认两个小时
	 * @return accessTokenTimeout
	 */
	public long getAccessTokenTimeout() {
		return accessTokenTimeout;
	}

	/**
	 * 全局默认配置所有应用：设置Access-Token保存的时间(单位：秒)
	 * @param accessTokenTimeout 保存时间(秒)
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setAccessTokenTimeout(long accessTokenTimeout) {
		this.accessTokenTimeout = accessTokenTimeout;
		return this;
	}

	/**
	 * 全局默认配置所有应用：Refresh-Token 保存的时间(单位：秒) 默认30天
	 * @return refreshTokenTimeout
	 */
	public long getRefreshTokenTimeout() {
		return refreshTokenTimeout;
	}

	/**
	 * 全局默认配置所有应用：设置Refresh-Token保存的时间(单位：秒)
	 * @param refreshTokenTimeout 保存时间(秒)
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setRefreshTokenTimeout(long refreshTokenTimeout) {
		this.refreshTokenTimeout = refreshTokenTimeout;
		return this;
	}

	/**
	 * 全局默认配置所有应用：Client-Token 保存的时间(单位：秒) 默认两个小时
	 * @return clientTokenTimeout
	 */
	public long getClientTokenTimeout() {
		return clientTokenTimeout;
	}

	/**
	 * 全局默认配置所有应用：设置Client-Token保存的时间(单位：秒)
	 * @param clientTokenTimeout 保存时间(秒)
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setClientTokenTimeout(long clientTokenTimeout) {
		this.clientTokenTimeout = clientTokenTimeout;
		return this;
	}

	/**
	 * 全局默认配置所有应用：单个应用单个用户最多同时存在的 Access-Token 数量
	 * @return maxAccessTokenCount
	 */
	public int getMaxAccessTokenCount() {
		return maxAccessTokenCount;
	}

	/**
	 * 设置单个应用单个用户最多同时存在的 Access-Token 数量
	 * @param maxAccessTokenCount 最大数量
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setMaxAccessTokenCount(int maxAccessTokenCount) {
		this.maxAccessTokenCount = maxAccessTokenCount;
		return this;
	}

	/**
	 * 全局默认配置所有应用：单个应用单个用户最多同时存在的 Refresh-Token 数量
	 * @return maxRefreshTokenCount
	 */
	public int getMaxRefreshTokenCount() {
		return maxRefreshTokenCount;
	}

	/**
	 * 设置单个应用单个用户最多同时存在的 Refresh-Token 数量
	 * @param maxRefreshTokenCount 最大数量
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setMaxRefreshTokenCount(int maxRefreshTokenCount) {
		this.maxRefreshTokenCount = maxRefreshTokenCount;
		return this;
	}

	/**
	 * 全局默认配置所有应用：单个应用最多同时存在的 Client-Token 数量
	 * @return maxClientTokenCount
	 */
	public int getMaxClientTokenCount() {
		return maxClientTokenCount;
	}

	/**
	 * 设置单个应用最多同时存在的 Client-Token 数量
	 * @param maxClientTokenCount 最大数量
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setMaxClientTokenCount(int maxClientTokenCount) {
		this.maxClientTokenCount = maxClientTokenCount;
		return this;
	}

	/**
	 * 默认 openid 生成算法中使用的摘要前缀
	 * @return openidDigestPrefix
	 */
	public String getOpenidDigestPrefix() {
		return openidDigestPrefix;
	}

	/**
	 * 设置默认 openid 生成算法中使用的摘要前缀
	 * @param openidDigestPrefix 摘要前缀
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setOpenidDigestPrefix(String openidDigestPrefix) {
		this.openidDigestPrefix = openidDigestPrefix;
		return this;
	}

	/**
	 * 默认 unionid 生成算法中使用的摘要前缀
	 * @return unionidDigestPrefix
	 */
	public String getUnionidDigestPrefix() {
		return unionidDigestPrefix;
	}

	/**
	 * 设置默认 unionid 生成算法中使用的摘要前缀
	 * @param unionidDigestPrefix 摘要前缀
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setUnionidDigestPrefix(String unionidDigestPrefix) {
		this.unionidDigestPrefix = unionidDigestPrefix;
		return this;
	}

	/**
	 * 指定高级权限，多个用逗号隔开
	 * @return higherScope
	 */
	public String getHigherScope() {
		return higherScope;
	}

	/**
	 * 设置高级权限，多个用逗号隔开
	 * @param higherScope 权限字符串
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setHigherScope(String higherScope) {
		this.higherScope = higherScope;
		return this;
	}

	/**
	 * 指定低级权限，多个用逗号隔开
	 * @return lowerScope
	 */
	public String getLowerScope() {
		return lowerScope;
	}

	/**
	 * 设置低级权限，多个用逗号隔开
	 * @param lowerScope 权限字符串
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setLowerScope(String lowerScope) {
		this.lowerScope = lowerScope;
		return this;
	}

	/**
	 * 模式4是否返回 AccessToken 字段，以使其更符合 OAuth2 RFC 规范
	 * @return mode4ReturnAccessToken
	 */
	public Boolean getMode4ReturnAccessToken() {
		return mode4ReturnAccessToken;
	}

	/**
	 * 设置模式4是否返回 AccessToken 字段，以使其更符合 OAuth2 RFC 规范
	 * @param mode4ReturnAccessToken 是否返回
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setMode4ReturnAccessToken(Boolean mode4ReturnAccessToken) {
		this.mode4ReturnAccessToken = mode4ReturnAccessToken;
		return this;
	}

	/**
	 * 是否在返回值中隐藏默认的状态字段 (code、msg、data)
	 * @return hideStatusField
	 */
	public Boolean getHideStatusField() {
		return hideStatusField;
	}

	/**
	 * 设置是否在返回值中隐藏默认的状态字段 (code、msg、data)
	 * @param hideStatusField 是否隐藏
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setHideStatusField(Boolean hideStatusField) {
		this.hideStatusField = hideStatusField;
		return this;
	}

	/**
	 * 获取oidc相关配置
	 * @return oidc配置对象
	 */
	public SaOAuth2OidcConfig getOidc() {
		return oidc;
	}

	/**
	 * 设置oidc相关配置
	 * @param oidc oidc配置对象
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setOidc(SaOAuth2OidcConfig oidc) {
		this.oidc = oidc;
		return this;
	}

	/**
	 * 获取client列表
	 * @return client列表
	 */
	public Map<String, SaClientModel> getClients() {
		return clients;
	}

	/**
	 * 设置client列表
	 * @param clients client列表
	 * @return 对象自身
	 */
	public SaOAuth2ServerConfig setClients(Map<String, SaClientModel> clients) {
		this.clients = clients;
		return this;
	}

	@Override
	public String toString() {
		return "SaOAuth2ServerConfig {" +
				"enableAuthorizationCode=" + enableAuthorizationCode +
				", enableImplicit=" + enableImplicit +
				", enablePassword=" + enablePassword +
				", enableClientCredentials=" + enableClientCredentials +
				", isNewRefresh=" + isNewRefresh +
				", codeTimeout=" + codeTimeout +
				", accessTokenTimeout=" + accessTokenTimeout +
				", refreshTokenTimeout=" + refreshTokenTimeout +
				", clientTokenTimeout=" + clientTokenTimeout +
				", maxAccessTokenCount=" + maxAccessTokenCount +
				", maxRefreshTokenCount=" + maxRefreshTokenCount +
				", maxClientTokenCount=" + maxClientTokenCount +
				", openidDigestPrefix=" + openidDigestPrefix +
				", unionidDigestPrefix=" + unionidDigestPrefix +
				", higherScope=" + higherScope +
				", lowerScope=" + lowerScope +
				", mode4ReturnAccessToken=" + mode4ReturnAccessToken +
				", hideStatusField=" + hideStatusField +
				", oidc=" + oidc +
				", clients=" + clients +
				'}';
	}


}

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
package cn.dev33.satoken.oauth2.template;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;

import java.util.List;

/**
 * Sa-Token-OAuth2 模块 工具类
 *
 * @author click33
 * @since 1.23.0
 */
public class SaOAuth2Util {
	
	// ------------------- 资源校验API  
	
	/**
	 * 根据id获取Client信息, 如果Client为空，则抛出异常 
	 * @param clientId 应用id
	 * @return ClientModel 
	 */
	public static SaClientModel checkClientModel(String clientId) {
		return SaOAuth2Manager.getTemplate().checkClientModel(clientId);
	}
	
	/**
	 * 获取 Access-Token，如果AccessToken为空则抛出异常 
	 * @param accessToken . 
	 * @return .
	 */
	public static AccessTokenModel checkAccessToken(String accessToken) {
		return SaOAuth2Manager.getTemplate().checkAccessToken(accessToken);
	}
	
	/**
	 * 获取 Client-Token，如果ClientToken为空则抛出异常 
	 * @param clientToken . 
	 * @return .
	 */
	public static ClientTokenModel checkClientToken(String clientToken) {
		return SaOAuth2Manager.getTemplate().checkClientToken(clientToken);
	}
	
	/**
	 * 获取 Access-Token 所代表的LoginId 
	 * @param accessToken Access-Token 
	 * @return LoginId 
	 */
	public static Object getLoginIdByAccessToken(String accessToken) {
		return SaOAuth2Manager.getTemplate().getLoginIdByAccessToken(accessToken);
	}
	
	/**
	 * 校验：指定 Access-Token 是否具有指定 Scope  
	 * @param accessToken Access-Token
	 * @param scopes 需要校验的权限列表 
	 */
	public static void checkScope(String accessToken, String... scopes) {
		SaOAuth2Manager.getTemplate().checkScope(accessToken, scopes);
	}
	
	/**
	 * 校验：指定 Client-Token 是否具有指定 Scope
	 * @param clientToken Client-Token
	 * @param scopes 需要校验的权限列表
	 */
	public static void checkClientTokenScope(String clientToken, String... scopes) {
		SaOAuth2Manager.getTemplate().checkClientTokenScope(clientToken, scopes);
	}


	// ------------------- 数据校验 
	
	/**
	 * 判断：指定 loginId 是否对一个 Client 授权给了指定 Scope 
	 * @param loginId 账号id 
	 * @param clientId 应用id 
	 * @param scopes 权限
	 * @return 是否已经授权
	 */
	public static boolean isGrant(Object loginId, String clientId, List<String> scopes) {
		return SaOAuth2Manager.getTemplate().isGrant(loginId, clientId, scopes);
	}
	
	/**
	 * 校验：该Client是否签约了指定的Scope 
	 * @param clientId 应用id
	 * @param scopes 权限(多个用逗号隔开)
	 */
	public static void checkContract(String clientId, List<String> scopes) {
		SaOAuth2Manager.getTemplate().checkContract(clientId, scopes);
	}
	
	/**
	 * 校验：该Client使用指定url作为回调地址，是否合法 
	 * @param clientId 应用id 
	 * @param url 指定url
	 */
	public static void checkRightUrl(String clientId, String url) {
		SaOAuth2Manager.getTemplate().checkRightUrl(clientId, url);
	}
	
	/**
	 * 校验：clientId 与 clientSecret 是否正确
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @return SaClientModel对象 
	 */
	public static SaClientModel checkClientSecret(String clientId, String clientSecret) {
		return SaOAuth2Manager.getTemplate().checkClientSecret(clientId, clientSecret);
	}
	
	/**
	 * 校验：clientId 与 clientSecret 是否正确，并且是否签约了指定 scopes 
	 * @param clientId 应用id
	 * @param clientSecret 秘钥
	 * @param scopes 权限
	 * @return SaClientModel对象
	 */
	public static SaClientModel checkClientSecretAndScope(String clientId, String clientSecret, List<String> scopes) {
		return SaOAuth2Manager.getTemplate().checkClientSecretAndScope(clientId, clientSecret, scopes);
	}
	
	/**
	 * 校验：使用 code 获取 token 时提供的参数校验 
	 * @param code 授权码
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @param redirectUri 重定向地址 
	 * @return CodeModel对象 
	 */
	public static CodeModel checkGainTokenParam(String code, String clientId, String clientSecret, String redirectUri) {
		return SaOAuth2Manager.getTemplate().checkGainTokenParam(code, clientId, clientSecret, redirectUri);
	}

	/**
	 * 校验：使用 Refresh-Token 刷新 Access-Token 时提供的参数校验 
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @param refreshToken Refresh-Token
	 * @return CodeModel对象 
	 */
	public static RefreshTokenModel checkRefreshTokenParam(String clientId, String clientSecret, String refreshToken) {
		return SaOAuth2Manager.getTemplate().checkRefreshTokenParam(clientId, clientSecret, refreshToken);
	}
	
	/**
	 * 校验：Access-Token、clientId、clientSecret 三者是否匹配成功 
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @param accessToken Access-Token 
	 * @return SaClientModel对象 
	 */
	public static AccessTokenModel checkAccessTokenParam(String clientId, String clientSecret, String accessToken) {
		return SaOAuth2Manager.getTemplate().checkAccessTokenParam(clientId, clientSecret, accessToken);
	}

	/**
	 * 回收指定的 ClientToken
	 *
	 * @param clientToken /
	 */
	public static void revokeClientToken(String clientToken) {
		SaOAuth2Manager.getTemplate().revokeClientToken(clientToken);
	}

	/**
	 * 回收指定的 ClientToken，根据索引：clientId
	 *
	 * @param clientId /
	 */
	public static void revokeClientTokenByIndex(String clientId) {
		SaOAuth2Manager.getTemplate().revokeClientTokenByIndex(clientId);
	}

	// ------------------- save 数据 
	
	/**
	 * 持久化：用户授权记录 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @param scopes 权限列表
	 */
	public static void saveGrantScope(String clientId, Object loginId, List<String> scopes) {
		SaOAuth2Manager.getTemplate().saveGrantScope(clientId, loginId, scopes);
	}
	
	
	// ------------------- get 数据
	
	/**
	 * 获取：Code Model  
	 * @param code .
	 * @return .
	 */
	public static CodeModel getCode(String code) {
		return SaOAuth2Manager.getDao().getCode(code);
	}

	/**
	 * 获取：Access-Token Model 
	 * @param accessToken . 
	 * @return .
	 */
	public static AccessTokenModel getAccessToken(String accessToken) {
		return SaOAuth2Manager.getDao().getAccessToken(accessToken);
	}
	
	/**
	 * 获取：Refresh-Token Model 
	 * @param refreshToken . 
	 * @return . 
	 */
	public static RefreshTokenModel getRefreshToken(String refreshToken) {
		return SaOAuth2Manager.getDao().getRefreshToken(refreshToken);
	}
	
	/**
	 * 获取：Client-Token Model 
	 * @param clientToken . 
	 * @return .
	 */
	public static ClientTokenModel getClientToken(String clientToken) {
		return SaOAuth2Manager.getDao().getClientToken(clientToken);
	}
	
	/**
	 * 获取：用户授权记录 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return 权限 
	 */
	public static List<String> getGrantScope(String clientId, Object loginId) {
		return SaOAuth2Manager.getDao().getGrantScope(clientId, loginId);
	}

}

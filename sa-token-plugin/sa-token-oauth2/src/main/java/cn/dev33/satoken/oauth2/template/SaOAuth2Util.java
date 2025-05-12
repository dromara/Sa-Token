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
 * Sa-Token OAuth2 模块 工具类
 *
 * @author click33
 * @since 1.23.0
 */
public class SaOAuth2Util {

	// ----------------- ClientModel 相关 -----------------

	/**
	 * 获取 ClientModel，根据 clientId
	 *
	 * @param clientId /
	 * @return /
	 */
	public static SaClientModel getClientModel(String clientId) {
		return SaOAuth2Manager.getTemplate().getClientModel(clientId);
	}

	/**
	 * 校验 clientId 信息并返回 ClientModel，如果找不到对应 Client 信息则抛出异常
	 * @param clientId /
	 * @return /
	 */
	public static SaClientModel checkClientModel(String clientId) {
		return SaOAuth2Manager.getTemplate().checkClientModel(clientId);
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
	 * 判断：该 Client 是否签约了指定的 Scope，返回 true 或 false
	 * @param clientId 应用id
	 * @param scopes 权限
	 * @return /
	 */
	public static boolean isContractScope(String clientId, List<String> scopes) {
		return SaOAuth2Manager.getTemplate().isContractScope(clientId, scopes);
	}

	/**
	 * 校验：该 Client 是否签约了指定的 Scope，如果没有则抛出异常
	 * @param clientId 应用id
	 * @param scopes 权限列表
	 * @return /
	 */
	public static SaClientModel checkContractScope(String clientId, List<String> scopes) {
		return SaOAuth2Manager.getTemplate().checkContractScope(clientId, scopes);
	}

	/**
	 * 校验：该 Client 是否签约了指定的 Scope，如果没有则抛出异常
	 * @param cm 应用
	 * @param scopes 权限列表
	 * @return /
	 */
	public static SaClientModel checkContractScope(SaClientModel cm, List<String> scopes) {
		return SaOAuth2Manager.getTemplate().checkContractScope(cm, scopes);
	}

	// --------- redirect_uri 相关

	/**
	 * 校验：该 Client 使用指定 url 作为回调地址，是否合法
	 * @param clientId 应用id
	 * @param url 指定url
	 */
	public static void checkRedirectUri(String clientId, String url) {
		SaOAuth2Manager.getTemplate().checkRedirectUri(clientId, url);
	}

	// --------- 授权相关

	/**
	 * 判断：指定 loginId 是否对一个 Client 授权给了指定 Scope
	 * @param loginId 账号id
	 * @param clientId 应用id
	 * @param scopes 权限
	 * @return 是否已经授权
	 */
	public static boolean isGrantScope(Object loginId, String clientId, List<String> scopes) {
		return SaOAuth2Manager.getTemplate().isGrantScope(loginId, clientId, scopes);
	}


	// ----------------- Code 相关 -----------------

	/**
	 * 获取 CodeModel，无效的 code 会返回 null
	 * @param code /
	 * @return /
	 */
	public static CodeModel getCode(String code) {
		return SaOAuth2Manager.getTemplate().getCode(code);
	}

	/**
	 * 校验 Code，成功返回 CodeModel，失败则抛出异常
	 * @param code /
	 * @return /
	 */
	public static CodeModel checkCode(String code) {
		return SaOAuth2Manager.getTemplate().checkCode(code);
	}

	/**
	 * 获取 Code，根据索引： clientId、loginId
	 * @param clientId /
	 * @param loginId /
	 * @return /
	 */
	public static String getCodeValue(String clientId, Object loginId) {
		return SaOAuth2Manager.getTemplate().getCodeValue(clientId, loginId);
	}


	// ----------------- Access-Token 相关 -----------------

	/**
	 * 获取 AccessTokenModel，无效的 AccessToken 会返回 null
	 * @param accessToken /
	 * @return /
	 */
	public static AccessTokenModel getAccessToken(String accessToken) {
		return SaOAuth2Manager.getTemplate().getAccessToken(accessToken);
	}

	/**
	 * 校验 Access-Token，成功返回 AccessTokenModel，失败则抛出异常
	 * @param accessToken /
	 * @return /
	 */
	public static AccessTokenModel checkAccessToken(String accessToken) {
		return SaOAuth2Manager.getTemplate().checkAccessToken(accessToken);
	}

	/**
	 * 获取 Access-Token 列表：此应用下 对 某个用户 签发的所有 Access-token
	 * @param clientId /
	 * @param loginId /
	 * @return /
	 */
	public static List<String> getAccessTokenValueList(String clientId, Object loginId) {
		return SaOAuth2Manager.getTemplate().getAccessTokenValueList(clientId, loginId);
	}

	/**
	 * 判断：指定 Access-Token 是否具有指定 Scope 列表，返回 true 或 false
	 * @param accessToken Access-Token
	 * @param scopes 需要校验的权限列表
	 */
	public static boolean hasAccessTokenScope(String accessToken, String... scopes) {
		return SaOAuth2Manager.getTemplate().hasAccessTokenScope(accessToken, scopes);
	}

	/**
	 * 校验：指定 Access-Token 是否具有指定 Scope 列表，如果不具备则抛出异常
	 * @param accessToken Access-Token
	 * @param scopes 需要校验的权限列表
	 */
	public static void checkAccessTokenScope(String accessToken, String... scopes) {
		SaOAuth2Manager.getTemplate().checkAccessTokenScope(accessToken, scopes);
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
	 * 获取 Access-Token 所代表的 clientId
	 * @param accessToken Access-Token
	 * @return LoginId
	 */
	public static Object getClientIdByAccessToken(String accessToken) {
		return SaOAuth2Manager.getTemplate().getClientIdByAccessToken(accessToken);
	}

	/**
	 * 回收一个 Access-Token
	 * @param accessToken Access-Token值
	 */
	public static void revokeAccessToken(String accessToken) {
		SaOAuth2Manager.getTemplate().revokeAccessToken(accessToken);
	}

	/**
	 * 回收全部 Access-Token：指定应用下 指定用户 的全部 Access-Token
	 * @param clientId /
	 * @param loginId /
	 */
	public static void revokeAccessTokenByIndex(String clientId, Object loginId) {
		SaOAuth2Manager.getTemplate().revokeAccessTokenByIndex(clientId, loginId);
	}


	// ----------------- Refresh-Token 相关 -----------------

	/**
	 * 获取 RefreshTokenModel，无效的 RefreshToken 会返回 null
	 * @param refreshToken /
	 * @return /
	 */
	public static RefreshTokenModel getRefreshToken(String refreshToken) {
		return SaOAuth2Manager.getTemplate().getRefreshToken(refreshToken);
	}

	/**
	 * 校验 Refresh-Token，成功返回 RefreshTokenModel，失败则抛出异常
	 * @param refreshToken /
	 * @return /
	 */
	public static RefreshTokenModel checkRefreshToken(String refreshToken) {
		return SaOAuth2Manager.getTemplate().checkRefreshToken(refreshToken);
	}

	/**
	 * 获取 Refresh-Token 列表：此应用下 对 某个用户 签发的所有 Refresh-Token
	 *
	 * @param clientId /
	 * @param loginId /
	 * @return /
	 */
	public static List<String> getRefreshTokenValueList(String clientId, Object loginId) {
		return SaOAuth2Manager.getTemplate().getRefreshTokenValueList(clientId, loginId);
	}

	/**
	 * 回收一个 Refresh-Token
	 *
	 * @param refreshToken Refresh-Token 值
	 */
	public static void revokeRefreshToken(String refreshToken) {
		SaOAuth2Manager.getTemplate().revokeRefreshToken(refreshToken);
	}

	/**
	 * 回收全部 Refresh-Token：指定应用下 指定用户 的全部 Refresh-Token
	 * @param clientId /
	 * @param loginId /
	 */
	public static void revokeRefreshTokenByIndex(String clientId, Object loginId) {
		SaOAuth2Manager.getTemplate().revokeRefreshTokenByIndex(clientId, loginId);
	}

	/**
	 * 根据 RefreshToken 刷新出一个 AccessToken
	 * @param refreshToken /
	 * @return /
	 */
	public static AccessTokenModel refreshAccessToken(String refreshToken) {
		return SaOAuth2Manager.getTemplate().refreshAccessToken(refreshToken);
	}


	// ----------------- Client-Token 相关 -----------------

	/**
	 * 获取 ClientTokenModel，无效的 ClientToken 会返回 null
	 * @param clientToken /
	 * @return /
	 */
	public static ClientTokenModel getClientToken(String clientToken) {
		return SaOAuth2Manager.getTemplate().getClientToken(clientToken);
	}

	/**
	 * 校验 Client-Token，成功返回 ClientTokenModel，失败则抛出异常
	 * @param clientToken /
	 * @return /
	 */
	public static ClientTokenModel checkClientToken(String clientToken) {
		return SaOAuth2Manager.getTemplate().checkClientToken(clientToken);
	}

	/**
	 * 获取 Client-Token 列表：此应用下 对 某个用户 签发的所有 Client-token
	 *
	 * @param clientId /
	 * @return /
	 */
	public static List<String> getClientTokenValueList(String clientId) {
		return SaOAuth2Manager.getTemplate().getClientTokenValueList(clientId);
	}

	/**
	 * 判断：指定 Client-Token 是否具有指定 Scope 列表，返回 true 或 false
	 * @param clientToken Client-Token
	 * @param scopes 需要校验的权限列表
	 */
	public static boolean hasClientTokenScope(String clientToken, String... scopes) {
		return SaOAuth2Manager.getTemplate().hasClientTokenScope(clientToken, scopes);
	}

	/**
	 * 校验：指定 Client-Token 是否具有指定 Scope 列表，如果不具备则抛出异常
	 * @param clientToken Client-Token
	 * @param scopes 需要校验的权限列表
	 */
	public static void checkClientTokenScope(String clientToken, String... scopes) {
		SaOAuth2Manager.getTemplate().checkClientTokenScope(clientToken, scopes);
	}

	/**
	 * 回收一个 ClientToken
	 *
	 * @param clientToken /
	 */
	public static void revokeClientToken(String clientToken) {
		SaOAuth2Manager.getTemplate().revokeClientToken(clientToken);
	}

	/**
	 * 回收全部 Client-Token：指定应用下的全部 Client-Token
	 *
	 * @param clientId /
	 */
	public static void revokeClientTokenByIndex(String clientId) {
		SaOAuth2Manager.getTemplate().revokeClientTokenByIndex(clientId);
	}

}

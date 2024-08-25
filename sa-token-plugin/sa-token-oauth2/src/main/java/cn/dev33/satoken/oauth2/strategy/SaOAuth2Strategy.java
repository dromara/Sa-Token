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
package cn.dev33.satoken.oauth2.strategy;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.ClientIdAndSecretModel;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.function.strategy.*;
import cn.dev33.satoken.oauth2.granttype.handler.AuthorizationCodeGrantTypeHandler;
import cn.dev33.satoken.oauth2.granttype.handler.PasswordGrantTypeHandler;
import cn.dev33.satoken.oauth2.granttype.handler.RefreshTokenGrantTypeHandler;
import cn.dev33.satoken.oauth2.granttype.handler.SaOAuth2GrantTypeHandlerInterface;
import cn.dev33.satoken.oauth2.scope.CommonScope;
import cn.dev33.satoken.oauth2.scope.handler.OidcScopeHandler;
import cn.dev33.satoken.oauth2.scope.handler.OpenIdScopeHandler;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import cn.dev33.satoken.oauth2.scope.handler.UserIdScopeHandler;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sa-Token OAuth2 相关策略
 *
 * @author click33
 * @since 1.39.0
 */
public final class SaOAuth2Strategy {

	private SaOAuth2Strategy() {
		registerDefaultScopeHandler();
		registerDefaultGrantTypeHandler();
	}

	/**
	 * 全局单例引用
	 */
	public static final SaOAuth2Strategy instance = new SaOAuth2Strategy();

	// 权限处理器

	/**
	 * 权限处理器集合
	 */
	public Map<String, SaOAuth2ScopeHandlerInterface> scopeHandlerMap = new LinkedHashMap<>();

	/**
	 * 注册所有默认的权限处理器
	 */
	public void registerDefaultScopeHandler() {
		scopeHandlerMap.put(CommonScope.OPENID, new OpenIdScopeHandler());
		scopeHandlerMap.put(CommonScope.USERID, new UserIdScopeHandler());
		scopeHandlerMap.put(CommonScope.OIDC, new OidcScopeHandler());
	}

	/**
	 * 注册一个权限处理器
	 */
	public void registerScopeHandler(SaOAuth2ScopeHandlerInterface handler) {
		scopeHandlerMap.put(handler.getHandlerScope(), handler);
		SaManager.getLog().info("自定义 SCOPE [{}] (处理器: {})", handler.getHandlerScope(), handler.getClass().getCanonicalName());
	}

	/**
	 * 移除一个权限处理器
	 */
	public void removeScopeHandler(String scope) {
		scopeHandlerMap.remove(scope);
	}

	/**
	 * 根据 scope 信息对一个 AccessTokenModel 进行加工处理
	 */
	public SaOAuth2ScopeWorkAccessTokenFunction workAccessTokenByScope = (at) -> {
		if(at.scopes != null && !at.scopes.isEmpty()) {
			for (String scope : at.scopes) {
				SaOAuth2ScopeHandlerInterface handler = scopeHandlerMap.get(scope);
				if(handler != null) {
					handler.workAccessToken(at);
				}
			}
		}
		SaOAuth2ScopeHandlerInterface finallyWorkScopeHandler = scopeHandlerMap.get(SaOAuth2Consts._FINALLY_WORK_SCOPE);
		if(finallyWorkScopeHandler != null) {
			finallyWorkScopeHandler.workAccessToken(at);
		}
	};

	/**
	 * 根据 scope 信息对一个 ClientTokenModel 进行加工处理
	 */
	public SaOAuth2ScopeWorkClientTokenFunction workClientTokenByScope = (ct) -> {
		if(ct.scopes != null && !ct.scopes.isEmpty()) {
			for (String scope : ct.scopes) {
				SaOAuth2ScopeHandlerInterface handler = scopeHandlerMap.get(scope);
				if(handler != null) {
					handler.workClientToken(ct);
				}
			}
		}
		SaOAuth2ScopeHandlerInterface finallyWorkScopeHandler = scopeHandlerMap.get(SaOAuth2Consts._FINALLY_WORK_SCOPE);
		if(finallyWorkScopeHandler != null) {
			finallyWorkScopeHandler.workClientToken(ct);
		}
	};

	// grant_type 处理器

	/**
	 * grant_type 处理器集合
	 */
	public Map<String, SaOAuth2GrantTypeHandlerInterface> grantTypeHandlerMap = new LinkedHashMap<>();

	/**
	 * 注册所有默认的权限处理器
	 */
	public void registerDefaultGrantTypeHandler() {
		grantTypeHandlerMap.put(GrantType.authorization_code, new AuthorizationCodeGrantTypeHandler());
		grantTypeHandlerMap.put(GrantType.password, new PasswordGrantTypeHandler());
		grantTypeHandlerMap.put(GrantType.refresh_token, new RefreshTokenGrantTypeHandler());
	}

	/**
	 * 注册一个权限处理器
	 */
	public void registerGrantTypeHandler(SaOAuth2GrantTypeHandlerInterface handler) {
		grantTypeHandlerMap.put(handler.getHandlerGrantType(), handler);
		SaManager.getLog().info("自定义 GRANT_TYPE [{}] (处理器: {})", handler.getHandlerGrantType(), handler.getClass().getCanonicalName());
	}

	/**
	 * 移除一个权限处理器
	 */
	public void removeGrantTypeHandler(String scope) {
		scopeHandlerMap.remove(scope);
	}

	/**
	 * 根据 scope 信息对一个 AccessTokenModel 进行加工处理
	 */
	public SaOAuth2GrantTypeAuthFunction grantTypeAuth = (req) -> {
		String grantType = req.getParamNotNull(SaOAuth2Consts.Param.grant_type);
		SaOAuth2GrantTypeHandlerInterface grantTypeHandler = grantTypeHandlerMap.get(grantType);
		if(grantTypeHandler == null) {
			throw new RuntimeException("无效 grant_type: " + grantType);
		}

		// 看看全局是否开启了此 grantType
		SaOAuth2ServerConfig config = SaOAuth2Manager.getServerConfig();
		if(grantType.equals(GrantType.authorization_code) && !config.getEnableAuthorizationCode() ) {
			throw new SaOAuth2Exception("系统未开放的 grant_type: " + grantType);
		}
		if(grantType.equals(GrantType.password) && !config.getEnablePassword() ) {
			throw new SaOAuth2Exception("系统未开放的 grant_type: " + grantType);
		}

		// 校验 clientSecret 和 scope
		ClientIdAndSecretModel clientIdAndSecretModel = SaOAuth2Manager.getDataResolver().readClientIdAndSecret(req);
		List<String> scopes = SaOAuth2Manager.getDataConverter().convertScopeStringToList(req.getParam(SaOAuth2Consts.Param.scope));
		SaClientModel clientModel = SaOAuth2Manager.getTemplate().checkClientSecretAndScope(clientIdAndSecretModel.getClientId(), clientIdAndSecretModel.getClientSecret(), scopes);

		// 检测应用是否开启此 grantType
		if(!clientModel.getAllowGrantTypes().contains(grantType)) {
			throw new SaOAuth2Exception("应用未开放的 grant_type: " + grantType);
		}

		// 调用 处理器
		return grantTypeHandler.getAccessToken(req, clientIdAndSecretModel.getClientId(), scopes);
	};


	// ----------------------- 所有策略

	/**
	 * 创建一个 code value
	 */
	public SaOAuth2CreateCodeValueFunction createCodeValue = (clientId, loginId, scopes) -> {
		return SaFoxUtil.getRandomString(60);
	};

	/**
	 * 创建一个 AccessToken value
	 */
	public SaOAuth2CreateAccessTokenValueFunction createAccessToken = (clientId, loginId, scopes) -> {
		return SaFoxUtil.getRandomString(60);
	};

	/**
	 * 创建一个 RefreshToken value
	 */
	public SaOAuth2CreateRefreshTokenValueFunction createRefreshToken = (clientId, loginId, scopes) -> {
		return SaFoxUtil.getRandomString(60);
	};

	/**
	 * 创建一个 ClientToken value
	 */
	public SaOAuth2CreateClientTokenValueFunction createClientToken = (clientId, scopes) -> {
		return SaFoxUtil.getRandomString(60);
	};

}

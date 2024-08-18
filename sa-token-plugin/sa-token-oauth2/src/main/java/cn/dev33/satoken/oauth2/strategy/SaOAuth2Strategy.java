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
import cn.dev33.satoken.oauth2.function.strategy.*;
import cn.dev33.satoken.oauth2.scope.CommonScope;
import cn.dev33.satoken.oauth2.scope.handler.OpenIdScopeHandler;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeAbstractHandler;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.LinkedHashMap;
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
	}

	/**
	 * 全局单例引用
	 */
	public static final SaOAuth2Strategy instance = new SaOAuth2Strategy();

	// 权限处理器

	/**
	 * 权限处理器集合
	 */
	public Map<String, SaOAuth2ScopeAbstractHandler> scopeHandlerMap = new LinkedHashMap<>();

	/**
	 * 注册所有默认的权限处理器
	 */
	public void registerDefaultScopeHandler() {
		scopeHandlerMap.put(CommonScope.OPENID, new OpenIdScopeHandler());
	}

	/**
	 * 注册一个权限处理器
	 */
	public void registerScopeHandler(SaOAuth2ScopeAbstractHandler handler) {
		scopeHandlerMap.put(handler.getHandlerScope(), handler);
		// TODO 优化日志输出
		SaManager.getLog().info("新增权限处理器：" + handler.getHandlerScope());
		//		SaTokenEventCenter.doRegisterAnnotationHandler(handler);
	}

	/**
	 * 移除一个权限处理器
	 */
	public void removeScopeHandler(String scope) {
		scopeHandlerMap.remove(scope);
	}


	// ----------------------- 所有策略

	/**
	 * 根据 scope 信息对一个 AccessTokenModel 进行加工处理
	 */
	public SaScopeWorkFunction workAccessTokenByScope = (at) -> {
		System.out.println("增强：" + at.accessToken);
		System.out.println("权限：" + at.scopes);
		// 遍历所有的权限处理器，如果此 AccessToken 具有这些权限，则开始加工
		if(at.scopes != null && !at.scopes.isEmpty()) {
			for (Map.Entry<String, SaOAuth2ScopeAbstractHandler> entry: scopeHandlerMap.entrySet()) {
				if(at.scopes.contains(entry.getKey())) {
					entry.getValue().work(at);
				}
			}
		}

	};

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

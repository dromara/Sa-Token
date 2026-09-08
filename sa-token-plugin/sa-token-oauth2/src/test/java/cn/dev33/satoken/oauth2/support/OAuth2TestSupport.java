/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.oauth2.support;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.fun.SaRetGenericFunction;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.dao.SaOAuth2Dao;
import cn.dev33.satoken.oauth2.data.convert.SaOAuth2DataConverterDefaultImpl;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerateDefaultImpl;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoaderDefaultImpl;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.resolver.SaOAuth2DataResolverDefaultImpl;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OAuth2 插件单测现场：换干净 Manager Bean、装默认 client、挂 mock 请求。
 */
public final class OAuth2TestSupport {

	/** 测试用 client_id */
	public static final String CLIENT_ID = "1001";

	/** 测试用 client_secret */
	public static final String CLIENT_SECRET = "aaaa-bbbb-cccc-dddd-eeee";

	/** 测试用回调地址 */
	public static final String REDIRECT = "http://oauth-client.com/callback";

	/** 登录账号 */
	public static final Object LOGIN_ID = 10001;

	private OAuth2TestSupport() {
	}

	/** 装一套能走完授权码 / 隐藏式 / 密码 / 凭证 / 刷新的默认配置 */
	public static void installDefaultConfig() {
		SaOAuth2Manager.setDao(new SaOAuth2Dao());
		SaOAuth2Manager.setTemplate(new SaOAuth2Template());
		SaOAuth2Manager.setDataGenerate(new SaOAuth2DataGenerateDefaultImpl());
		SaOAuth2Manager.setDataResolver(new SaOAuth2DataResolverDefaultImpl());
		SaOAuth2Manager.setDataConverter(new SaOAuth2DataConverterDefaultImpl());
		SaOAuth2Manager.setDataLoader(new SaOAuth2DataLoaderDefaultImpl());
		SaOAuth2Manager.setStpLogic(StpUtil.stpLogic);
		SaManager.getConfig().setJwtSecretKey("oauth2-unit-jwt-secret-key-32chars");

		SaOAuth2ServerConfig cfg = new SaOAuth2ServerConfig();
		SaOAuth2Manager.setServerConfig(cfg);
		cfg.addClient(defaultClient());

		SaOAuth2Strategy.instance.registerDefaultGrantTypeHandler();
		SaOAuth2Strategy.instance.registerDefaultScopeHandler();
		SaOAuth2Strategy.instance.notLoginView = () -> "OAUTH2-LOGIN-VIEW";
		SaOAuth2Strategy.instance.confirmView = (clientId, scopes) -> "OAUTH2-CONFIRM-VIEW";
		SaOAuth2Strategy.instance.doLoginHandle = (name, pwd) -> {
			if ("sa".equals(name) && "123456".equals(pwd)) {
				StpUtil.login(LOGIN_ID);
				return SaResult.ok("登录成功");
			}
			return SaResult.error("登录失败");
		};
		SaOAuth2Strategy.instance.userAuthorizeClientCheck = (loginId, clientId) -> {
		};
	}

	/** 默认测试 client：全授权模式、自动确认、签约常用 scope */
	public static SaClientModel defaultClient() {
		return new SaClientModel()
				.setClientId(CLIENT_ID)
				.setClientSecret(CLIENT_SECRET)
				.addAllowRedirectUris(REDIRECT, "http://oauth-client.com/*")
				.addContractScopes("openid", "unionid", "userid", "oidc", "userinfo")
				.addAllowGrantTypes(
						GrantType.authorization_code,
						GrantType.implicit,
						GrantType.refresh_token,
						GrantType.password,
						GrantType.client_credentials)
				.setIsAutoConfirm(true);
	}

	/** 当前 mock 响应里记下的跳转地址 */
	public static String redirectTo() {
		return ((SaResponseForMock) SaHolder.getResponse()).redirectTo;
	}

	/** 挂 mock 请求（只给 path）再跑一段代码 */
	public static <T> T withPath(String path, SaRetGenericFunction<T> fun) {
		return withRequest(path, "GET", null, null, fun);
	}

	/** 挂 mock 请求（path + GET 参数）再跑一段代码 */
	public static <T> T withRequest(String path, Map<String, String> params, SaRetGenericFunction<T> fun) {
		return withRequest(path, "GET", params, null, fun);
	}

	/** 挂 mock POST 请求再跑一段代码 */
	public static <T> T withPost(String path, Map<String, String> params, SaRetGenericFunction<T> fun) {
		return withRequest(path, "POST", params, null, fun);
	}

	/** 挂 mock 请求（path + 方法 + 参数 + 头）再跑一段代码 */
	public static <T> T withRequest(String path, String method, Map<String, String> params,
			Map<String, String> headers, SaRetGenericFunction<T> fun) {
		return SaTokenContextMockUtil.setMockContext(() -> {
			SaRequestForMock req = (SaRequestForMock) SaHolder.getRequest();
			req.requestPath = path;
			req.url = "http://127.0.0.1" + path;
			req.method = method;
			if (params != null) {
				req.parameterMap.putAll(params);
			}
			if (headers != null) {
				req.headerMap.putAll(headers);
			}
			return fun.run();
		});
	}

	/** 方便拼请求参数 */
	public static Map<String, String> params(String... kv) {
		Map<String, String> map = new LinkedHashMap<>();
		for (int i = 0; i < kv.length; i += 2) {
			map.put(kv[i], kv[i + 1]);
		}
		return map;
	}

}

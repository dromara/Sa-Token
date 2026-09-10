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
package cn.dev33.satoken.oauth2.processor;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

/**
 * Server 处理器：authorize / token / refresh / revoke / doLogin / doConfirm / client_token
 */
@OAuth2Test
public class SaOAuth2ServerProcessorTest {

	/** 每个用例前换干净 OAuth2 现场 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** 不认识的 path 应该回 NOT_HANDLE */
	@Test
	public void dister_unknownPath_notHandle() {
		Object out = OAuth2TestSupport.withPath("/oauth2/unknown", () -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertEquals(SaOAuth2Consts.NOT_HANDLE, out);
	}

	/** 没登录访问 authorize 应该返回登录视图 */
	@Test
	public void authorize_notLogin_returnsView() {
		Object out = OAuth2TestSupport.withRequest("/oauth2/authorize", authorizeParams("code"),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertEquals("OAUTH2-LOGIN-VIEW", out);
	}

	/** 已登录授权码模式应该跳回调并带上 code */
	@Test
	public void authorize_code_redirectContainsCode() {
		String loc = OAuth2TestSupport.withRequest("/oauth2/authorize", authorizeParams("code"), () -> {
			StpUtil.login(OAuth2TestSupport.LOGIN_ID);
			SaOAuth2ServerProcessor.instance.dister();
			return OAuth2TestSupport.redirectTo();
		});
		Assertions.assertTrue(loc.startsWith(OAuth2TestSupport.REDIRECT));
		Assertions.assertTrue(loc.contains("code="));
	}

	/** 授权码模式带 state 时回调里也应该带上 state */
	@Test
	public void authorize_code_withState() {
		Map<String, String> params = authorizeParams("code");
		params.put("state", "abc");
		String loc = OAuth2TestSupport.withRequest("/oauth2/authorize", params, () -> {
			StpUtil.login(OAuth2TestSupport.LOGIN_ID);
			SaOAuth2ServerProcessor.instance.dister();
			return OAuth2TestSupport.redirectTo();
		});
		Assertions.assertTrue(loc.contains("code="));
		Assertions.assertTrue(loc.contains("state="));
	}

	/** 隐藏式回调片段参数应该是 token= 而不是 access_token= */
	@Test
	public void authorize_implicit_fragmentIsToken() {
		String loc = OAuth2TestSupport.withRequest("/oauth2/authorize", authorizeParams("token"), () -> {
			StpUtil.login(OAuth2TestSupport.LOGIN_ID);
			SaOAuth2ServerProcessor.instance.dister();
			return OAuth2TestSupport.redirectTo();
		});
		Assertions.assertTrue(loc.startsWith(OAuth2TestSupport.REDIRECT));
		Assertions.assertTrue(loc.contains("#token="));
		Assertions.assertFalse(loc.contains("access_token="));
	}

	/** 无效 response_type 应该抛 30125 */
	@Test
	public void authorize_invalidResponseType_30125() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withRequest("/oauth2/authorize", OAuth2TestSupport.params(
						"response_type", "foo",
						"client_id", OAuth2TestSupport.CLIENT_ID),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30125, ex.getCode());
	}

	/** 系统关掉授权码模式时 authorize 应该抛 30141 */
	@Test
	public void authorize_code_systemDisabled_30141() {
		SaOAuth2Manager.getServerConfig().setEnableAuthorizationCode(false);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withRequest("/oauth2/authorize", authorizeParams("code"),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30141, ex.getCode());
	}

	/** 应用没开授权码模式时 authorize 应该抛 30142 */
	@Test
	public void authorize_code_clientMissingGrant_30142() {
		addClientWithoutGrant("no-code", GrantType.password);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withRequest("/oauth2/authorize", OAuth2TestSupport.params(
						"response_type", "code",
						"client_id", "no-code",
						"redirect_uri", OAuth2TestSupport.REDIRECT),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30142, ex.getCode());
	}

	/** 系统关掉隐藏式时 authorize 应该抛 30141 */
	@Test
	public void authorize_implicit_systemDisabled_30141() {
		SaOAuth2Manager.getServerConfig().setEnableImplicit(false);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withRequest("/oauth2/authorize", authorizeParams("token"),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30141, ex.getCode());
	}

	/** 应用没开隐藏式时 authorize 应该抛 30142 */
	@Test
	public void authorize_implicit_clientMissingGrant_30142() {
		addClientWithoutGrant("no-implicit", GrantType.authorization_code);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withRequest("/oauth2/authorize", OAuth2TestSupport.params(
						"response_type", "token",
						"client_id", "no-implicit",
						"redirect_uri", OAuth2TestSupport.REDIRECT),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30142, ex.getCode());
	}

	/** 没开自动确认且用户还没授权时应该返回确认页 */
	@Test
	public void authorize_needConfirm_returnsView() {
		SaOAuth2Manager.getServerConfig().getClients().get(OAuth2TestSupport.CLIENT_ID).setIsAutoConfirm(false);
		Object out = OAuth2TestSupport.withRequest("/oauth2/authorize", OAuth2TestSupport.params(
				"response_type", "code",
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"redirect_uri", OAuth2TestSupport.REDIRECT,
				"scope", "openid"), () -> {
			StpUtil.login(OAuth2TestSupport.LOGIN_ID);
			return SaOAuth2ServerProcessor.instance.dister();
		});
		Assertions.assertEquals("OAUTH2-CONFIRM-VIEW", out);
	}

	/** 授权码换 token 成功后返回值里应该有 access_token */
	@Test
	public void token_authorizationCode_success() {
		CodeModel cm = generateCode();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
				"grant_type", GrantType.authorization_code,
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET,
				"code", cm.code,
				"redirect_uri", OAuth2TestSupport.REDIRECT),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertNotNull(map.get("access_token"));
	}

	/** 密码式用 sa/123456 应该能拿到 access_token */
	@Test
	public void token_password_success() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
				"grant_type", GrantType.password,
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET,
				"username", "sa",
				"password", "123456"),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertNotNull(map.get("access_token"));
	}

	/** 密码式密码错了应该抛 30161 */
	@Test
	public void token_password_wrongPwd_30161() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"grant_type", GrantType.password,
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET,
						"username", "sa",
						"password", "wrong"),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30161, ex.getCode());
	}

	/** 无效 grant_type 应该抛 30126 */
	@Test
	public void token_invalidGrantType_30126() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"grant_type", "foo",
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30126, ex.getCode());
	}

	/** 系统关掉密码式时 grant_type=password 应该抛 30126 */
	@Test
	public void token_password_systemDisabled_30126() {
		SaOAuth2Manager.getServerConfig().setEnablePassword(false);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"grant_type", GrantType.password,
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET,
						"username", "sa",
						"password", "123456"),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30126, ex.getCode());
	}

	/** 应用没开密码式时目前走策略校验抛 30141 */
	@Test
	public void token_password_clientNotAllow_30141() {
		addClientWithoutGrant("no-pwd", GrantType.authorization_code);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"grant_type", GrantType.password,
						"client_id", "no-pwd",
						"client_secret", OAuth2TestSupport.CLIENT_SECRET,
						"username", "sa",
						"password", "123456"),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30141, ex.getCode());
	}

	/** refresh 的 grant_type 不是 refresh_token 应该抛 30126 */
	@Test
	public void refresh_wrongGrantType_30126() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/refresh", OAuth2TestSupport.params(
						"grant_type", GrantType.password,
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30126, ex.getCode());
	}

	/** refresh 成功后应该返回新的 access_token */
	@Test
	public void refresh_success_returnsNewAccessToken() {
		AccessTokenModel at = generateAccessTokenWithRt();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) OAuth2TestSupport.withPost("/oauth2/refresh", OAuth2TestSupport.params(
				"grant_type", GrantType.refresh_token,
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET,
				"refresh_token", at.refreshToken),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertNotNull(map.get("access_token"));
		Assertions.assertNotEquals(at.accessToken, map.get("access_token"));
	}

	/** 回收一个不存在的 token 应该返回 ok 提示 */
	@Test
	public void revoke_missingToken_okMessage() {
		SaResult result = (SaResult) OAuth2TestSupport.withPost("/oauth2/revoke", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET,
				"access_token", "no-such-token"),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertEquals(200, result.getCode());
		Assertions.assertTrue(result.getMsg().contains("不存在"));
	}

	/** 回收已有 token 后再查应该是 null */
	@Test
	public void revoke_existingToken_thenNull() {
		AccessTokenModel at = generateAccessTokenWithRt();
		OAuth2TestSupport.withPost("/oauth2/revoke", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET,
				"access_token", at.accessToken),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertNull(SaOAuth2Manager.getTemplate().getAccessToken(at.accessToken));
	}

	/** doLogin 用 sa/123456 应该登录成功 */
	@Test
	public void doLogin_success() {
		SaResult result = (SaResult) OAuth2TestSupport.withPost("/oauth2/doLogin", OAuth2TestSupport.params(
				"name", "sa",
				"pwd", "123456"), () -> {
			Object out = SaOAuth2ServerProcessor.instance.dister();
			Assertions.assertTrue(StpUtil.isLogin());
			return out;
		});
		Assertions.assertEquals(200, result.getCode());
	}

	/** doLogin 密码错了应该返回错误 */
	@Test
	public void doLogin_badPwd() {
		SaResult result = (SaResult) OAuth2TestSupport.withPost("/oauth2/doLogin", OAuth2TestSupport.params(
				"name", "sa",
				"pwd", "bad"),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertEquals(500, result.getCode());
	}

	/** doConfirm 用 GET 应该抛 30151 */
	@Test
	public void doConfirm_get_30151() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withRequest("/oauth2/doConfirm", confirmParams("code", false), () -> {
					StpUtil.login(OAuth2TestSupport.LOGIN_ID);
					return SaOAuth2ServerProcessor.instance.dister();
				}));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30151, ex.getCode());
	}

	/** doConfirm POST 不要求拼 redirect_uri 时应该只回 ok */
	@Test
	public void doConfirm_post_okWithoutRedirect() {
		SaResult result = (SaResult) OAuth2TestSupport.withPost("/oauth2/doConfirm", confirmParams("code", false), () -> {
			StpUtil.login(OAuth2TestSupport.LOGIN_ID);
			return SaOAuth2ServerProcessor.instance.dister();
		});
		Assertions.assertEquals(200, result.getCode());
		Assertions.assertNull(result.get("redirect_uri"));
	}

	/** doConfirm POST 拼授权码回调时 redirect_uri 里应该有 code= */
	@Test
	public void doConfirm_post_buildCodeRedirect() {
		Map<String, String> params = confirmParams("code", true);
		SaResult result = (SaResult) OAuth2TestSupport.withPost("/oauth2/doConfirm", params, () -> {
			StpUtil.login(OAuth2TestSupport.LOGIN_ID);
			return SaOAuth2ServerProcessor.instance.dister();
		});
		String redirect = (String) result.get("redirect_uri");
		Assertions.assertTrue(redirect.contains("code="));
	}

	/** doConfirm POST 拼隐藏式回调时 redirect_uri 里应该有 #token= */
	@Test
	public void doConfirm_post_buildImplicitRedirect() {
		Map<String, String> params = confirmParams("token", true);
		SaResult result = (SaResult) OAuth2TestSupport.withPost("/oauth2/doConfirm", params, () -> {
			StpUtil.login(OAuth2TestSupport.LOGIN_ID);
			return SaOAuth2ServerProcessor.instance.dister();
		});
		String redirect = (String) result.get("redirect_uri");
		Assertions.assertTrue(redirect.contains("#token="));
	}

	/** doConfirm 拼回调时 response_type 无效应该抛 30125 */
	@Test
	public void doConfirm_invalidResponseType_30125() {
		Map<String, String> params = confirmParams("foo", true);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/doConfirm", params, () -> {
					StpUtil.login(OAuth2TestSupport.LOGIN_ID);
					return SaOAuth2ServerProcessor.instance.dister();
				}));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30125, ex.getCode());
	}

	/** client_token 的 grant_type 必须是 client_credentials 否则 30126 */
	@Test
	public void clientToken_wrongGrantType_30126() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/client_token", OAuth2TestSupport.params(
						"grant_type", GrantType.password,
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30126, ex.getCode());
	}

	/** 系统关掉凭证式时应该抛 30141 */
	@Test
	public void clientToken_systemDisabled_30141() {
		SaOAuth2Manager.getServerConfig().setEnableClientCredentials(false);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/client_token", OAuth2TestSupport.params(
						"grant_type", GrantType.client_credentials,
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30141, ex.getCode());
	}

	/** 应用没开凭证式时应该抛 30142 */
	@Test
	public void clientToken_clientNotAllow_30142() {
		addClientWithoutGrant("no-ct", GrantType.password);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/client_token", OAuth2TestSupport.params(
						"grant_type", GrantType.client_credentials,
						"client_id", "no-ct",
						"client_secret", OAuth2TestSupport.CLIENT_SECRET),
						() -> SaOAuth2ServerProcessor.instance.dister()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30142, ex.getCode());
	}

	/** 凭证式成功时 JSON 里应该有 client_token */
	@Test
	public void clientToken_success_hasClientToken() {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) OAuth2TestSupport.withPost("/oauth2/client_token", OAuth2TestSupport.params(
				"grant_type", GrantType.client_credentials,
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET,
				"scope", "openid"),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertNotNull(map.get("client_token"));
		Assertions.assertNull(map.get("access_token"));
	}

	/** mode4ReturnAccessToken=true 时凭证式也会带回 access_token */
	@Test
	public void clientToken_mode4AlsoHasAccessToken() {
		SaOAuth2Manager.getServerConfig().setMode4ReturnAccessToken(true);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) OAuth2TestSupport.withPost("/oauth2/client_token", OAuth2TestSupport.params(
				"grant_type", GrantType.client_credentials,
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertNotNull(map.get("client_token"));
		Assertions.assertEquals(map.get("client_token"), map.get("access_token"));
	}

	/** hideStatusField=true 时 token 返回值应该藏掉 code/msg */
	@Test
	public void token_hideStatusField() {
		SaOAuth2Manager.getServerConfig().setHideStatusField(true);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
				"grant_type", GrantType.password,
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET,
				"username", "sa",
				"password", "123456"),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertFalse(map.containsKey("code"));
		Assertions.assertFalse(map.containsKey("msg"));
		Assertions.assertNotNull(map.get("access_token"));
	}

	/** checkCurrClientSecret 对上了应该返回 client，对不上抛 30115 */
	@Test
	public void checkCurrClientSecret_successAndFail() {
		OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET), () -> {
			SaClientModel cm = SaOAuth2ServerProcessor.instance.checkCurrClientSecret();
			Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, cm.getClientId());
			return null;
		});
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", "wrong"),
						() -> SaOAuth2ServerProcessor.instance.checkCurrClientSecret()));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30115, ex.getCode());
	}

	/** currClientModel 应该能按请求里的 client_id 取出应用 */
	@Test
	public void currClientModel() {
		OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID), () -> {
			SaClientModel cm = SaOAuth2ServerProcessor.instance.currClientModel();
			Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, cm.getClientId());
			return null;
		});
	}

	/** Basic 头带 client_id:secret 时凭证式也应该能签发 */
	@Test
	public void clientToken_basicAuthorizationHeader() {
		String basic = "Basic " + SaBase64Util.encode(OAuth2TestSupport.CLIENT_ID + ":" + OAuth2TestSupport.CLIENT_SECRET);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) OAuth2TestSupport.withRequest("/oauth2/client_token", "POST",
				OAuth2TestSupport.params("grant_type", GrantType.client_credentials),
				OAuth2TestSupport.params("Authorization", basic),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertNotNull(map.get("client_token"));
	}

	/** Basic 头走 token 密码式也应该能签发 */
	@Test
	public void token_password_basicAuthorizationHeader() {
		String basic = "Basic " + SaBase64Util.encode(OAuth2TestSupport.CLIENT_ID + ":" + OAuth2TestSupport.CLIENT_SECRET);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) OAuth2TestSupport.withRequest("/oauth2/token", "POST",
				OAuth2TestSupport.params(
						"grant_type", GrantType.password,
						"username", "sa",
						"password", "123456"),
				OAuth2TestSupport.params("Authorization", basic),
				() -> SaOAuth2ServerProcessor.instance.dister());
		Assertions.assertNotNull(map.get("access_token"));
	}

	/** 直接调系统未开放模式应该抛 30141 */
	@Test
	public void throwErrorSystemNotEnableModel() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> SaOAuth2ServerProcessor.instance.throwErrorSystemNotEnableModel());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30141, ex.getCode());
	}

	/** 直接调应用未开放模式应该抛 30142 */
	@Test
	public void throwErrorClientNotEnableModel() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> SaOAuth2ServerProcessor.instance.throwErrorClientNotEnableModel());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30142, ex.getCode());
	}

	/** 拼一套 authorize 常用参数 */
	private static Map<String, String> authorizeParams(String responseType) {
		return OAuth2TestSupport.params(
				"response_type", responseType,
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"redirect_uri", OAuth2TestSupport.REDIRECT);
	}

	/** 拼一套 doConfirm 常用参数 */
	private static Map<String, String> confirmParams(String responseType, boolean buildRedirect) {
		Map<String, String> params = OAuth2TestSupport.params(
				"response_type", responseType,
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"redirect_uri", OAuth2TestSupport.REDIRECT,
				"scope", "openid");
		if (buildRedirect) {
			params.put("build_redirect_uri", "true");
		}
		return params;
	}

	/** 造一个没开指定模式的 client */
	private static void addClientWithoutGrant(String clientId, String... grants) {
		SaClientModel cm = new SaClientModel()
				.setClientId(clientId)
				.setClientSecret(OAuth2TestSupport.CLIENT_SECRET)
				.addAllowRedirectUris(OAuth2TestSupport.REDIRECT)
				.addContractScopes("openid")
				.addAllowGrantTypes(grants);
		SaOAuth2Manager.getServerConfig().addClient(cm);
	}

	/** 造一个授权码 */
	private static CodeModel generateCode() {
		RequestAuthModel ra = new RequestAuthModel()
				.setClientId(OAuth2TestSupport.CLIENT_ID)
				.setLoginId(OAuth2TestSupport.LOGIN_ID)
				.setRedirectUri(OAuth2TestSupport.REDIRECT)
				.setResponseType("code")
				.setScopes(Collections.singletonList("openid"));
		return SaOAuth2Manager.getDataGenerate().generateCode(ra);
	}

	/** 造一个带 refresh_token 的 access_token */
	private static AccessTokenModel generateAccessTokenWithRt() {
		RequestAuthModel ra = new RequestAuthModel()
				.setClientId(OAuth2TestSupport.CLIENT_ID)
				.setLoginId(OAuth2TestSupport.LOGIN_ID)
				.setRedirectUri(OAuth2TestSupport.REDIRECT)
				.setScopes(Collections.singletonList("openid"));
		return SaOAuth2Manager.getDataGenerate().generateAccessToken(ra, true, null);
	}

}

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
package cn.dev33.satoken.oauth2.template;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * OAuth2 模板：client / redirect / 授权记录 / token 校验与回收
 */
@OAuth2Test
public class SaOAuth2TemplateTest {

	private SaOAuth2Template tpl;

	/** 每个用例前换干净模板 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
		tpl = SaOAuth2Manager.getTemplate();
	}

	/** 找不到的 client_id 应该抛 30105 */
	@Test
	public void checkClientModel_30105() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> tpl.checkClientModel("no-such"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30105, ex.getCode());
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, tpl.getClientModel(OAuth2TestSupport.CLIENT_ID).getClientId());
	}

	/** 密钥不对应该抛 30115 */
	@Test
	public void checkClientSecret_30115() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkClientSecret(OAuth2TestSupport.CLIENT_ID, "wrong"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30115, ex.getCode());
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID,
				tpl.checkClientSecret(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET).getClientId());
	}

	/** 密钥和 scope 都对才过，没签约的 scope 抛 30112 */
	@Test
	public void checkClientSecretAndScope() {
		Assertions.assertDoesNotThrow(() -> tpl.checkClientSecretAndScope(
				OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET, Collections.singletonList("openid")));
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> tpl.checkClientSecretAndScope(
				OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET, Collections.singletonList("no-scope")));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30112, ex.getCode());
	}

	/** 签约了返回 true，没签约返回 false */
	@Test
	public void isContractScope_trueFalse() {
		Assertions.assertTrue(tpl.isContractScope(OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid")));
		Assertions.assertFalse(tpl.isContractScope(OAuth2TestSupport.CLIENT_ID, Collections.singletonList("no-scope")));
	}

	/** 空 scope 校验应该直接过，没签约的抛 30112 */
	@Test
	public void checkContractScope_emptyOkUnsigned30112() {
		Assertions.assertDoesNotThrow(() -> tpl.checkContractScope(OAuth2TestSupport.CLIENT_ID, null));
		Assertions.assertDoesNotThrow(() -> tpl.checkContractScope(OAuth2TestSupport.CLIENT_ID, Collections.emptyList()));
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkContractScope(OAuth2TestSupport.CLIENT_ID, Collections.singletonList("no-scope")));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30112, ex.getCode());
	}

	/** 非法 url 应该抛 30113 */
	@Test
	public void checkRedirectUri_invalidUrl_30113() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkRedirectUri(OAuth2TestSupport.CLIENT_ID, "not-a-url"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30113, ex.getCode());
	}

	/** 回调地址带 @ / %40 / %2540 应该抛 30113 */
	@Test
	public void checkRedirectUri_atVariants_30113() {
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30113, Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkRedirectUri(OAuth2TestSupport.CLIENT_ID, "http://oauth-client.com:123@evil.com")).getCode());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30113, Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkRedirectUri(OAuth2TestSupport.CLIENT_ID, "http://oauth-client.com:123%40evil.com")).getCode());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30113, Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkRedirectUri(OAuth2TestSupport.CLIENT_ID, "http://oauth-client.com:123%2540evil.com")).getCode());
	}

	/** 不在允许列表里的回调应该抛 30114 */
	@Test
	public void checkRedirectUri_notInList_30114() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkRedirectUri(OAuth2TestSupport.CLIENT_ID, "http://evil.com/callback"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30114, ex.getCode());
	}

	/** 通配符允许列表应该能匹配子路径 */
	@Test
	public void checkRedirectUri_wildcardSuccess() {
		Assertions.assertDoesNotThrow(() -> tpl.checkRedirectUri(OAuth2TestSupport.CLIENT_ID, "http://oauth-client.com/foo"));
	}

	/** 回调带 query 时应该先剥掉问号再校验 */
	@Test
	public void checkRedirectUri_queryStringStripped() {
		Assertions.assertDoesNotThrow(() ->
				tpl.checkRedirectUri(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.REDIRECT + "?x=1"));
	}

	/** 实例方法校验允许列表：合法通配符过，非法抛 30114 */
	@Test
	public void checkRedirectUriListNormal() {
		Assertions.assertDoesNotThrow(() -> tpl.checkRedirectUriListNormal(Arrays.asList(
				"http://a.com/*", "http://a.com:*", "*")));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30114, Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkRedirectUriListNormal(Collections.singletonList("http://a.com*"))).getCode());
	}

	/** 静态方法：合法过，* 前一位不对或 * 不在末尾抛 30114 */
	@Test
	public void checkRedirectUriListNormalStaticMethod() {
		Assertions.assertDoesNotThrow(() -> SaOAuth2Template.checkRedirectUriListNormalStaticMethod(Arrays.asList(
				"http://a.com/*", "http://a.com:*", "*")));
		Assertions.assertThrows(SaOAuth2Exception.class, () ->
				SaOAuth2Template.checkRedirectUriListNormalStaticMethod(Collections.singletonList("http://a.com*")));
		Assertions.assertThrows(SaOAuth2Exception.class, () ->
				SaOAuth2Template.checkRedirectUriListNormalStaticMethod(Collections.singletonList("http://*.a.com/")));
		Assertions.assertThrows(SaOAuth2Exception.class, () ->
				SaOAuth2Template.checkRedirectUriListNormalStaticMethod(Collections.singletonList("http://a.com:9003*")));
	}

	/** IPv6 带中括号的回调应该能过 */
	@Test
	public void checkRedirectUri_ipv6_success() {
		SaClientModel client = new SaClientModel()
				.setClientId("ipv6-oauth2")
				.addAllowRedirectUris("http://[::1]:9003/*", "http://[2001:db8::1]:8080/callback");
		SaOAuth2Manager.getServerConfig().addClient(client);
		Assertions.assertDoesNotThrow(() -> tpl.checkRedirectUri("ipv6-oauth2", "http://[::1]:9003/sso/login"));
		Assertions.assertDoesNotThrow(() -> tpl.checkRedirectUri("ipv6-oauth2", "http://[2001:db8::1]:8080/callback"));
	}

	/** IPv6 没中括号或不在列表里必须拒绝 */
	@Test
	public void checkRedirectUri_ipv6_reject() {
		SaClientModel client = new SaClientModel()
				.setClientId("ipv6-oauth2")
				.addAllowRedirectUris("http://[::1]:9003/*");
		SaOAuth2Manager.getServerConfig().addClient(client);
		SaOAuth2Exception ex1 = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkRedirectUri("ipv6-oauth2", "http://2001:db8::1:8080/callback"));
		Assertions.assertTrue(ex1.getMessage().contains("无效 redirect_url"));
		SaOAuth2Exception ex2 = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkRedirectUri("ipv6-oauth2", "http://[::2]:9003/sso/login"));
		Assertions.assertTrue(ex2.getMessage().contains("非法 redirect_url"));
		Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkRedirectUri("ipv6-oauth2", "http://[::1]:9003%40evil.com/sso/login"));
	}

	/** 授权记录保存后 isGrantScope 为 true，删掉就没了 */
	@Test
	public void grantScope_saveGetDelete() {
		List<String> scopes = Collections.singletonList("openid");
		Assertions.assertFalse(tpl.isGrantScope(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID, scopes));
		tpl.saveGrantScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, scopes);
		Assertions.assertTrue(tpl.isGrantScope(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID, scopes));
		tpl.deleteGrantScope(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID);
		Assertions.assertFalse(tpl.isGrantScope(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID, scopes));
	}

	/** 空 scope 不需要确认；高级权限强制确认；低级权限剔掉后为空则不确认；已授权则不确认 */
	@Test
	public void isNeedCarefulConfirm() {
		Assertions.assertFalse(tpl.isNeedCarefulConfirm(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID, null));
		Assertions.assertFalse(tpl.isNeedCarefulConfirm(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID, Collections.emptyList()));

		SaOAuth2Manager.getServerConfig().setHigherScope("openid");
		Assertions.assertTrue(tpl.isNeedCarefulConfirm(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID,
				Collections.singletonList("openid")));

		SaOAuth2Manager.getServerConfig().setHigherScope(null);
		SaOAuth2Manager.getServerConfig().setLowerScope("openid");
		Assertions.assertFalse(tpl.isNeedCarefulConfirm(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID,
				Collections.singletonList("openid")));

		SaOAuth2Manager.getServerConfig().setLowerScope(null);
		tpl.saveGrantScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, Collections.singletonList("userid"));
		Assertions.assertFalse(tpl.isNeedCarefulConfirm(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID,
				Collections.singletonList("userid")));
	}

	/** 用坏 code 换 token 应该抛 30110 */
	@Test
	public void checkGainTokenParam_badCode_30110() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				tpl.checkGainTokenParam("bad", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET, OAuth2TestSupport.REDIRECT));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30110, ex.getCode());
	}

	/** code 对应的 client 对不上应该抛 30105 */
	@Test
	public void checkGainTokenParam_clientMismatch_30105() {
		CodeModel cm = generateCode();
		addExtraClient("other");
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				tpl.checkGainTokenParam(cm.code, "other", "other-secret", OAuth2TestSupport.REDIRECT));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30105, ex.getCode());
	}

	/** code 换 token 时密钥错了应该抛 30115 */
	@Test
	public void checkGainTokenParam_badSecret_30115() {
		CodeModel cm = generateCode();
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				tpl.checkGainTokenParam(cm.code, OAuth2TestSupport.CLIENT_ID, "wrong", OAuth2TestSupport.REDIRECT));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30115, ex.getCode());
	}

	/** redirect 对不上应该抛 30120，对得上或空 redirect 都能过 */
	@Test
	public void checkGainTokenParam_redirectMismatch_30120() {
		CodeModel cm = generateCode();
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				tpl.checkGainTokenParam(cm.code, OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET, "http://other.com/cb"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30120, ex.getCode());
		Assertions.assertEquals(cm.code, tpl.checkGainTokenParam(cm.code, OAuth2TestSupport.CLIENT_ID,
				OAuth2TestSupport.CLIENT_SECRET, OAuth2TestSupport.REDIRECT).code);
		Assertions.assertEquals(cm.code, tpl.checkGainTokenParam(cm.code, OAuth2TestSupport.CLIENT_ID,
				OAuth2TestSupport.CLIENT_SECRET, null).code);
	}

	/** 坏 refresh_token 抛 30111，client 对不上抛 30122，密钥错抛 30115 */
	@Test
	public void checkRefreshTokenParam() {
		SaOAuth2Exception badRt = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				tpl.checkRefreshTokenParam(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET, "no-rt"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30111, badRt.getCode());

		AccessTokenModel at = generateAt(true);
		addExtraClient("other");
		SaOAuth2Exception mismatch = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				tpl.checkRefreshTokenParam("other", "other-secret", at.refreshToken));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30122, mismatch.getCode());

		SaOAuth2Exception secret = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				tpl.checkRefreshTokenParam(OAuth2TestSupport.CLIENT_ID, "wrong", at.refreshToken));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30115, secret.getCode());

		Assertions.assertEquals(at.refreshToken, tpl.checkRefreshTokenParam(
				OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET, at.refreshToken).refreshToken);
	}

	/** AccessToken 参数三件套：坏 token、client 对不上、密钥错 */
	@Test
	public void checkAccessTokenParam() {
		SaOAuth2Exception badAt = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				tpl.checkAccessTokenParam(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET, "no-at"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30106, badAt.getCode());

		AccessTokenModel at = generateAt(false);
		addExtraClient("other");
		SaOAuth2Exception mismatch = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				tpl.checkAccessTokenParam("other", "other-secret", at.accessToken));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30122, mismatch.getCode());

		SaOAuth2Exception secret = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				tpl.checkAccessTokenParam(OAuth2TestSupport.CLIENT_ID, "wrong", at.accessToken));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30115, secret.getCode());

		Assertions.assertEquals(at.accessToken, tpl.checkAccessTokenParam(
				OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET, at.accessToken).accessToken);
	}

	/** Code 的 get/check/索引取值 */
	@Test
	public void code_getCheckIndex() {
		Assertions.assertNull(tpl.getCode("no"));
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> tpl.checkCode("no"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30110, ex.getCode());
		CodeModel cm = generateCode();
		Assertions.assertEquals(cm.code, tpl.checkCode(cm.code).code);
		Assertions.assertEquals(cm.code, tpl.getCodeValue(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID));
	}

	/** AccessToken 的 get/check/列表/scope/loginId/clientId/回收 */
	@Test
	public void accessToken_lifecycle() {
		Assertions.assertNull(tpl.getAccessToken("no"));
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> tpl.checkAccessToken("no"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30106, ex.getCode());

		AccessTokenModel at = generateAt(false);
		Assertions.assertEquals(at.accessToken, tpl.checkAccessToken(at.accessToken).accessToken);
		Assertions.assertTrue(tpl.getAccessTokenValueList(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).contains(at.accessToken));
		Assertions.assertTrue(tpl.hasAccessTokenScope(at.accessToken, "openid"));
		Assertions.assertFalse(tpl.hasAccessTokenScope(at.accessToken, "no-scope"));
		Assertions.assertDoesNotThrow(() -> tpl.checkAccessTokenScope(at.accessToken));
		Assertions.assertDoesNotThrow(() -> tpl.checkAccessTokenScope(at.accessToken, "openid"));
		SaOAuth2Exception scopeEx = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkAccessTokenScope(at.accessToken, "no-scope"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30108, scopeEx.getCode());
		Assertions.assertEquals(OAuth2TestSupport.LOGIN_ID, tpl.getLoginIdByAccessToken(at.accessToken));
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, tpl.getClientIdByAccessToken(at.accessToken));

		tpl.revokeAccessToken("no-such");
		tpl.revokeAccessToken(at.accessToken);
		Assertions.assertNull(tpl.getAccessToken(at.accessToken));

		AccessTokenModel at2 = generateAt(false);
		tpl.revokeAccessTokenByIndex(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
		Assertions.assertNull(tpl.getAccessToken(at2.accessToken));
		tpl.revokeAccessTokenByIndex(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
	}

	/** RefreshToken 的 get/check/列表/回收 */
	@Test
	public void refreshToken_lifecycle() {
		Assertions.assertNull(tpl.getRefreshToken("no"));
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> tpl.checkRefreshToken("no"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30111, ex.getCode());

		AccessTokenModel at = generateAt(true);
		Assertions.assertEquals(at.refreshToken, tpl.checkRefreshToken(at.refreshToken).refreshToken);
		Assertions.assertTrue(tpl.getRefreshTokenValueList(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).contains(at.refreshToken));

		tpl.revokeRefreshToken("no-such");
		tpl.revokeRefreshToken(at.refreshToken);
		Assertions.assertNull(tpl.getRefreshToken(at.refreshToken));

		AccessTokenModel at2 = generateAt(true);
		tpl.revokeRefreshTokenByIndex(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
		Assertions.assertNull(tpl.getRefreshToken(at2.refreshToken));
		tpl.revokeRefreshTokenByIndex(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
	}

	/** ClientToken 的 get/check/列表/scope/回收 */
	@Test
	public void clientToken_lifecycle() {
		Assertions.assertNull(tpl.getClientToken("no"));
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () -> tpl.checkClientToken("no"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30107, ex.getCode());

		ClientTokenModel ct = SaOAuth2Manager.getDataGenerate().generateClientToken(
				OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid"));
		Assertions.assertEquals(ct.clientToken, tpl.checkClientToken(ct.clientToken).clientToken);
		Assertions.assertTrue(tpl.getClientTokenValueList(OAuth2TestSupport.CLIENT_ID).contains(ct.clientToken));
		Assertions.assertTrue(tpl.hasClientTokenScope(ct.clientToken, "openid"));
		Assertions.assertFalse(tpl.hasClientTokenScope(ct.clientToken, "no-scope"));
		Assertions.assertDoesNotThrow(() -> tpl.checkClientTokenScope(ct.clientToken));
		Assertions.assertDoesNotThrow(() -> tpl.checkClientTokenScope(ct.clientToken, "openid"));
		SaOAuth2Exception scopeEx = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkClientTokenScope(ct.clientToken, "no-scope"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30109, scopeEx.getCode());

		tpl.revokeClientToken("no-such");
		tpl.revokeClientToken(ct.clientToken);
		Assertions.assertNull(tpl.getClientToken(ct.clientToken));

		ClientTokenModel ct2 = SaOAuth2Manager.getDataGenerate().generateClientToken(
				OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid"));
		tpl.revokeClientTokenByIndex(OAuth2TestSupport.CLIENT_ID);
		Assertions.assertNull(tpl.getClientToken(ct2.clientToken));
		tpl.revokeClientTokenByIndex(OAuth2TestSupport.CLIENT_ID);
	}

	/** 当前请求参数里带 access_token 时 currentAccessToken 应该能读到 */
	@Test
	public void currentAccessToken_fromParam() {
		AccessTokenModel at = generateAt(false);
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params("access_token", at.accessToken), () -> {
			Assertions.assertEquals(at.accessToken, tpl.currentAccessToken().accessToken);
			return null;
		});
	}

	/** Bearer 头里的 access_token 也应该能读到 */
	@Test
	public void currentAccessToken_fromBearer() {
		AccessTokenModel at = generateAt(false);
		OAuth2TestSupport.withRequest("/api", "GET", null,
				OAuth2TestSupport.params("Authorization", "Bearer " + at.accessToken), () -> {
			Assertions.assertEquals(at.accessToken, tpl.currentAccessToken().accessToken);
			return null;
		});
	}

	/** 当前请求参数里带 client_token 时 currentClientToken 应该能读到 */
	@Test
	public void currentClientToken_fromParam() {
		ClientTokenModel ct = SaOAuth2Manager.getDataGenerate().generateClientToken(
				OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid"));
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params("client_token", ct.clientToken), () -> {
			Assertions.assertEquals(ct.clientToken, tpl.currentClientToken().clientToken);
			return null;
		});
	}

	/** Bearer 头里的 client_token 也应该能读到 */
	@Test
	public void currentClientToken_fromBearer() {
		ClientTokenModel ct = SaOAuth2Manager.getDataGenerate().generateClientToken(
				OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid"));
		OAuth2TestSupport.withRequest("/api", "GET", null,
				OAuth2TestSupport.params("Authorization", "Bearer " + ct.clientToken), () -> {
			Assertions.assertEquals(ct.clientToken, tpl.currentClientToken().clientToken);
			return null;
		});
	}

	/** 高级/低级权限列表应该跟着配置走 */
	@Test
	public void higherAndLowerScopeList() {
		SaOAuth2Manager.getServerConfig().setHigherScope("openid,userid");
		SaOAuth2Manager.getServerConfig().setLowerScope("userinfo");
		Assertions.assertEquals(Arrays.asList("openid", "userid"), tpl.getHigherScopeList());
		Assertions.assertEquals(Collections.singletonList("userinfo"), tpl.getLowerScopeList());
	}

	/** 模板 refreshAccessToken 应该能刷出新 AT */
	@Test
	public void refreshAccessToken_viaTemplate() {
		AccessTokenModel at = generateAt(true);
		AccessTokenModel neu = tpl.refreshAccessToken(at.refreshToken);
		Assertions.assertNotNull(neu.accessToken);
		Assertions.assertNotEquals(at.accessToken, neu.accessToken);
	}

	/** 密钥为 null 的 client 校验 secret 也应该抛 30115 */
	@Test
	public void checkClientSecret_nullSecretOnModel_30115() {
		SaClientModel cm = new SaClientModel().setClientId("null-secret").setClientSecret(null);
		SaOAuth2Manager.getServerConfig().addClient(cm);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> tpl.checkClientSecret("null-secret", "any"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30115, ex.getCode());
	}

	/** 造一个授权码 */
	private CodeModel generateCode() {
		return SaOAuth2Manager.getDataGenerate().generateCode(ra());
	}

	/** 造一个 AccessToken，可选带 RefreshToken */
	private AccessTokenModel generateAt(boolean withRt) {
		return SaOAuth2Manager.getDataGenerate().generateAccessToken(ra(), withRt, null);
	}

	/** 默认请求授权模型 */
	private RequestAuthModel ra() {
		return new RequestAuthModel()
				.setClientId(OAuth2TestSupport.CLIENT_ID)
				.setLoginId(OAuth2TestSupport.LOGIN_ID)
				.setRedirectUri(OAuth2TestSupport.REDIRECT)
				.setResponseType("code")
				.setScopes(Collections.singletonList("openid"));
	}

	/** 再注册一个额外 client，方便测 client 对不上 */
	private void addExtraClient(String clientId) {
		SaOAuth2Manager.getServerConfig().addClient(new SaClientModel()
				.setClientId(clientId)
				.setClientSecret("other-secret")
				.addAllowRedirectUris(OAuth2TestSupport.REDIRECT)
				.addContractScopes("openid"));
	}

}

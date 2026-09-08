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
import cn.dev33.satoken.oauth2.exception.SaOAuth2AccessTokenException;
import cn.dev33.satoken.oauth2.exception.SaOAuth2AuthorizationCodeException;
import cn.dev33.satoken.oauth2.exception.SaOAuth2ClientModelException;
import cn.dev33.satoken.oauth2.exception.SaOAuth2ClientTokenException;
import cn.dev33.satoken.oauth2.exception.SaOAuth2RefreshTokenException;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * SaOAuth2Util 每个公开静态方法都要走到模板上，并且断言结果
 */
@SaTokenTest
public class SaOAuth2UtilTest {

	/** 每个用例前换干净 OAuth2 Bean 和默认 client */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** client 查询、密钥、签约 scope、回调地址都应该能委托出去 */
	@Test
	public void clientApis_delegateAndAssert() {
		SaClientModel cm = SaOAuth2Util.getClientModel(OAuth2TestSupport.CLIENT_ID);
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_SECRET, cm.getClientSecret());
		Assertions.assertNull(SaOAuth2Util.getClientModel("no-such"));
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, SaOAuth2Util.checkClientModel(OAuth2TestSupport.CLIENT_ID).getClientId());
		SaOAuth2ClientModelException badId = Assertions.assertThrows(SaOAuth2ClientModelException.class,
				() -> SaOAuth2Util.checkClientModel("no-such"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30105, badId.getCode());

		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID,
				SaOAuth2Util.checkClientSecret(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET).getClientId());
		SaOAuth2ClientModelException badSecret = Assertions.assertThrows(SaOAuth2ClientModelException.class,
				() -> SaOAuth2Util.checkClientSecret(OAuth2TestSupport.CLIENT_ID, "wrong"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30115, badSecret.getCode());

		List<String> userinfo = Arrays.asList("userinfo");
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID,
				SaOAuth2Util.checkClientSecretAndScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.CLIENT_SECRET, userinfo).getClientId());
		Assertions.assertTrue(SaOAuth2Util.isContractScope(OAuth2TestSupport.CLIENT_ID, userinfo));
		Assertions.assertFalse(SaOAuth2Util.isContractScope(OAuth2TestSupport.CLIENT_ID, Arrays.asList("admin")));
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID,
				SaOAuth2Util.checkContractScope(OAuth2TestSupport.CLIENT_ID, userinfo).getClientId());
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID,
				SaOAuth2Util.checkContractScope(cm, userinfo).getClientId());

		SaOAuth2Util.checkRedirectUri(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.REDIRECT);
		SaOAuth2ClientModelException badUri = Assertions.assertThrows(SaOAuth2ClientModelException.class,
				() -> SaOAuth2Util.checkRedirectUri(OAuth2TestSupport.CLIENT_ID, "not-a-url"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30113, badUri.getCode());
	}

	/** 用户授权记录存进去后 isGrantScope 应该是 true，删掉后又变 false */
	@Test
	public void grantScope_saveThenDelete() {
		List<String> scopes = Arrays.asList("userinfo");
		Assertions.assertFalse(SaOAuth2Util.isGrantScope(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID, scopes));
		SaOAuth2Manager.getTemplate().saveGrantScope(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, scopes);
		Assertions.assertTrue(SaOAuth2Util.isGrantScope(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID, scopes));
		SaOAuth2Util.deleteGrantScope(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID);
		Assertions.assertFalse(SaOAuth2Util.isGrantScope(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID, scopes));
	}

	/** Code 签发后 get/check/索引都能拿到，无效 code 走 30110 */
	@Test
	public void codeApis_afterGenerate() {
		CodeModel cm = SaOAuth2Manager.getDataGenerate().generateCode(ra());
		Assertions.assertEquals(cm.getCode(), SaOAuth2Util.getCode(cm.getCode()).getCode());
		Assertions.assertEquals(cm.getCode(), SaOAuth2Util.checkCode(cm.getCode()).getCode());
		Assertions.assertEquals(cm.getCode(), SaOAuth2Util.getCodeValue(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID));
		Assertions.assertNull(SaOAuth2Util.getCode("no-such"));
		SaOAuth2AuthorizationCodeException ex = Assertions.assertThrows(SaOAuth2AuthorizationCodeException.class,
				() -> SaOAuth2Util.checkCode("no-such"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30110, ex.getCode());
	}

	/** Access-Token 签发后校验、scope、回收、按索引回收都应该能观察到 */
	@Test
	public void accessTokenApis_afterGenerate() {
		AccessTokenModel at = SaOAuth2Manager.getDataGenerate().generateAccessToken(ra(), true, null);
		String token = at.getAccessToken();
		Assertions.assertEquals(token, SaOAuth2Util.getAccessToken(token).getAccessToken());
		Assertions.assertEquals(token, SaOAuth2Util.checkAccessToken(token).getAccessToken());
		Assertions.assertTrue(SaOAuth2Util.getAccessTokenValueList(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).contains(token));
		Assertions.assertTrue(SaOAuth2Util.hasAccessTokenScope(token, "userinfo"));
		Assertions.assertFalse(SaOAuth2Util.hasAccessTokenScope(token, "admin"));
		SaOAuth2Util.checkAccessTokenScope(token, "userinfo");
		SaOAuth2AccessTokenException badScope = Assertions.assertThrows(SaOAuth2AccessTokenException.class,
				() -> SaOAuth2Util.checkAccessTokenScope(token, "admin"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30108, badScope.getCode());
		Assertions.assertEquals(OAuth2TestSupport.LOGIN_ID, SaOAuth2Util.getLoginIdByAccessToken(token));
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, SaOAuth2Util.getClientIdByAccessToken(token));

		SaOAuth2Util.revokeAccessToken(token);
		Assertions.assertNull(SaOAuth2Util.getAccessToken(token));

		AccessTokenModel at2 = SaOAuth2Manager.getDataGenerate().generateAccessToken(ra(), true, null);
		SaOAuth2Util.revokeAccessTokenByIndex(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
		Assertions.assertNull(SaOAuth2Util.getAccessToken(at2.getAccessToken()));

		SaOAuth2AccessTokenException badAt = Assertions.assertThrows(SaOAuth2AccessTokenException.class,
				() -> SaOAuth2Util.checkAccessToken("no-such"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30106, badAt.getCode());
	}

	/** Refresh-Token 签发后校验、刷新、回收都应该能观察到 */
	@Test
	public void refreshTokenApis_afterGenerate() {
		AccessTokenModel at = SaOAuth2Manager.getDataGenerate().generateAccessToken(ra(), true, null);
		String rt = at.getRefreshToken();
		Assertions.assertEquals(rt, SaOAuth2Util.getRefreshToken(rt).getRefreshToken());
		Assertions.assertEquals(rt, SaOAuth2Util.checkRefreshToken(rt).getRefreshToken());
		Assertions.assertTrue(SaOAuth2Util.getRefreshTokenValueList(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID).contains(rt));
		AccessTokenModel refreshed = SaOAuth2Util.refreshAccessToken(rt);
		Assertions.assertNotNull(refreshed.getAccessToken());
		Assertions.assertNotEquals(at.getAccessToken(), refreshed.getAccessToken());

		SaOAuth2Util.revokeRefreshToken(rt);
		Assertions.assertNull(SaOAuth2Util.getRefreshToken(rt));

		AccessTokenModel at2 = SaOAuth2Manager.getDataGenerate().generateAccessToken(ra(), true, null);
		SaOAuth2Util.revokeRefreshTokenByIndex(OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID);
		Assertions.assertNull(SaOAuth2Util.getRefreshToken(at2.getRefreshToken()));

		SaOAuth2RefreshTokenException ex = Assertions.assertThrows(SaOAuth2RefreshTokenException.class,
				() -> SaOAuth2Util.checkRefreshToken("no-such"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30111, ex.getCode());
	}

	/** Client-Token 签发后校验、scope、回收都应该能观察到 */
	@Test
	public void clientTokenApis_afterGenerate() {
		ClientTokenModel ct = SaOAuth2Manager.getDataGenerate().generateClientToken(OAuth2TestSupport.CLIENT_ID, Arrays.asList("userinfo"));
		String token = ct.getClientToken();
		Assertions.assertEquals(token, SaOAuth2Util.getClientToken(token).getClientToken());
		Assertions.assertEquals(token, SaOAuth2Util.checkClientToken(token).getClientToken());
		Assertions.assertTrue(SaOAuth2Util.getClientTokenValueList(OAuth2TestSupport.CLIENT_ID).contains(token));
		Assertions.assertTrue(SaOAuth2Util.hasClientTokenScope(token, "userinfo"));
		Assertions.assertFalse(SaOAuth2Util.hasClientTokenScope(token, "admin"));
		SaOAuth2Util.checkClientTokenScope(token, "userinfo");
		SaOAuth2ClientTokenException badScope = Assertions.assertThrows(SaOAuth2ClientTokenException.class,
				() -> SaOAuth2Util.checkClientTokenScope(token, "admin"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30109, badScope.getCode());

		SaOAuth2Util.revokeClientToken(token);
		Assertions.assertNull(SaOAuth2Util.getClientToken(token));

		ClientTokenModel ct2 = SaOAuth2Manager.getDataGenerate().generateClientToken(OAuth2TestSupport.CLIENT_ID, Arrays.asList("userinfo"));
		SaOAuth2Util.revokeClientTokenByIndex(OAuth2TestSupport.CLIENT_ID);
		Assertions.assertNull(SaOAuth2Util.getClientToken(ct2.getClientToken()));

		SaOAuth2ClientTokenException badCt = Assertions.assertThrows(SaOAuth2ClientTokenException.class,
				() -> SaOAuth2Util.checkClientToken("no-such"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30107, badCt.getCode());
	}

	/** 挂 mock 请求后 currentAccessToken / currentClientToken 应该能从参数里读出来 */
	@Test
	public void currentToken_fromMockRequest() {
		AccessTokenModel at = SaOAuth2Manager.getDataGenerate().generateAccessToken(ra(), true, null);
		ClientTokenModel ct = SaOAuth2Manager.getDataGenerate().generateClientToken(OAuth2TestSupport.CLIENT_ID, Arrays.asList("userinfo"));
		OAuth2TestSupport.withRequest("/oauth2/userinfo",
				OAuth2TestSupport.params("access_token", at.getAccessToken()),
				() -> {
					Assertions.assertEquals(at.getAccessToken(), SaOAuth2Util.currentAccessToken().getAccessToken());
					return null;
				});
		OAuth2TestSupport.withRequest("/oauth2/clientinfo",
				OAuth2TestSupport.params("client_token", ct.getClientToken()),
				() -> {
					Assertions.assertEquals(ct.getClientToken(), SaOAuth2Util.currentClientToken().getClientToken());
					return null;
				});
	}

	private RequestAuthModel ra() {
		return new RequestAuthModel()
				.setClientId(OAuth2TestSupport.CLIENT_ID)
				.setScopes(Arrays.asList("userinfo"))
				.setLoginId(OAuth2TestSupport.LOGIN_ID)
				.setRedirectUri(OAuth2TestSupport.REDIRECT);
	}

}

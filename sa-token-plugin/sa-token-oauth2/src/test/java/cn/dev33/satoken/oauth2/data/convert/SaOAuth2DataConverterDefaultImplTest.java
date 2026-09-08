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
package cn.dev33.satoken.oauth2.data.convert;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 默认数据转换器：scope 分隔、各 Model 互转
 */
@SaTokenTest
public class SaOAuth2DataConverterDefaultImplTest {

	private SaOAuth2DataConverter converter;

	/** 每个用例前换干净转换器 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
		converter = SaOAuth2Manager.getDataConverter();
	}

	/** 空 scope 字符串应该转成空列表 */
	@Test
	public void convertScopeStringToList_empty() {
		Assertions.assertTrue(converter.convertScopeStringToList(null).isEmpty());
		Assertions.assertTrue(converter.convertScopeStringToList("").isEmpty());
	}

	/** 空格、%20、加号都该当成分隔符 */
	@Test
	public void convertScopeStringToList_separators() {
		Assertions.assertEquals(Arrays.asList("openid", "userid"), converter.convertScopeStringToList("openid userid"));
		Assertions.assertEquals(Arrays.asList("openid", "userid"), converter.convertScopeStringToList("openid%20userid"));
		Assertions.assertEquals(Arrays.asList("openid", "userid"), converter.convertScopeStringToList("openid+userid"));
		Assertions.assertEquals(Arrays.asList("openid", "userid"), converter.convertScopeStringToList("openid,userid"));
	}

	/** 列表转字符串应该用逗号拼起来，空列表给空串 */
	@Test
	public void convertScopeListToString() {
		Assertions.assertEquals("", converter.convertScopeListToString(Collections.emptyList()));
		Assertions.assertEquals("openid,userid", converter.convertScopeListToString(Arrays.asList("openid", "userid")));
	}

	/** redirect_uri 字符串转列表：空给空，逗号分隔 */
	@Test
	public void convertRedirectUriStringToList() {
		Assertions.assertTrue(converter.convertRedirectUriStringToList(null).isEmpty());
		Assertions.assertTrue(converter.convertRedirectUriStringToList("").isEmpty());
		Assertions.assertEquals(Arrays.asList("http://a.com/cb", "http://b.com/cb"),
				converter.convertRedirectUriStringToList("http://a.com/cb,http://b.com/cb"));
	}

	/** RequestAuth 转 Code 应该带上 nonce 和 scopes */
	@Test
	public void convertRequestAuthToCode() {
		RequestAuthModel ra = ra();
		CodeModel cm = converter.convertRequestAuthToCode(ra);
		Assertions.assertNotNull(cm.code);
		Assertions.assertEquals(ra.clientId, cm.clientId);
		Assertions.assertEquals(ra.loginId, cm.loginId);
		Assertions.assertEquals(ra.redirectUri, cm.redirectUri);
		Assertions.assertEquals(ra.scopes, cm.scopes);
		Assertions.assertEquals("n-1", cm.nonce);
	}

	/** RequestAuth 转 AccessToken 应该填 Bearer 和过期时间 */
	@Test
	public void convertRequestAuthToAccessToken() {
		AccessTokenModel at = converter.convertRequestAuthToAccessToken(ra(), 3600);
		Assertions.assertNotNull(at.accessToken);
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, at.clientId);
		Assertions.assertEquals(OAuth2TestSupport.LOGIN_ID, at.loginId);
		Assertions.assertEquals(SaOAuth2Consts.TokenType.Bearer, at.tokenType);
		Assertions.assertTrue(at.expiresTime > System.currentTimeMillis());
		Assertions.assertNotNull(at.extraData);
	}

	/** Code 转 AccessToken 的 grantType 应该是 authorization_code */
	@Test
	public void convertCodeToAccessToken() {
		CodeModel cm = converter.convertRequestAuthToCode(ra());
		AccessTokenModel at = converter.convertCodeToAccessToken(cm, 3600);
		Assertions.assertEquals(GrantType.authorization_code, at.grantType);
		Assertions.assertEquals(cm.clientId, at.clientId);
		Assertions.assertEquals(cm.loginId, at.loginId);
		Assertions.assertEquals(cm.scopes, at.scopes);
		Assertions.assertEquals(SaOAuth2Consts.TokenType.Bearer, at.tokenType);
	}

	/** AccessToken 转 RefreshToken 应该拷贝 extraData */
	@Test
	public void convertAccessTokenToRefreshToken() {
		AccessTokenModel at = converter.convertRequestAuthToAccessToken(ra(), 3600);
		at.extraData.put("k", "v");
		RefreshTokenModel rt = converter.convertAccessTokenToRefreshToken(at, 7200);
		Assertions.assertNotNull(rt.refreshToken);
		Assertions.assertEquals(at.clientId, rt.clientId);
		Assertions.assertEquals(at.loginId, rt.loginId);
		Assertions.assertEquals(at.scopes, rt.scopes);
		Assertions.assertEquals("v", rt.extraData.get("k"));
		Assertions.assertTrue(rt.expiresTime > System.currentTimeMillis());
	}

	/** RefreshToken 转 AccessToken 的 grantType 应该是 refresh_token */
	@Test
	public void convertRefreshTokenToAccessToken() {
		AccessTokenModel at = converter.convertRequestAuthToAccessToken(ra(), 3600);
		RefreshTokenModel rt = converter.convertAccessTokenToRefreshToken(at, 7200);
		rt.extraData.put("from-rt", true);
		AccessTokenModel neu = converter.convertRefreshTokenToAccessToken(rt, 1800);
		Assertions.assertNotEquals(at.accessToken, neu.accessToken);
		Assertions.assertEquals(rt.refreshToken, neu.refreshToken);
		Assertions.assertEquals(GrantType.refresh_token, neu.grantType);
		Assertions.assertEquals(true, neu.extraData.get("from-rt"));
		Assertions.assertEquals(rt.expiresTime, neu.refreshExpiresTime);
	}

	/** RefreshToken 再转一个新 RefreshToken 时值应该变、字段该拷贝 */
	@Test
	public void convertRefreshTokenToRefreshToken() {
		AccessTokenModel at = converter.convertRequestAuthToAccessToken(ra(), 3600);
		RefreshTokenModel rt = converter.convertAccessTokenToRefreshToken(at, 7200);
		rt.extraData.put("keep", 1);
		RefreshTokenModel neu = converter.convertRefreshTokenToRefreshToken(rt, 7200);
		Assertions.assertNotEquals(rt.refreshToken, neu.refreshToken);
		Assertions.assertEquals(rt.clientId, neu.clientId);
		Assertions.assertEquals(rt.loginId, neu.loginId);
		Assertions.assertEquals(rt.scopes, neu.scopes);
		Assertions.assertEquals(1, neu.extraData.get("keep"));
	}

	/** SaClient 转 ClientToken 应该带上 client_credentials */
	@Test
	public void convertSaClientToClientToken() {
		SaClientModel cm = SaOAuth2Manager.getServerConfig().getClients().get(OAuth2TestSupport.CLIENT_ID);
		List<String> scopes = Collections.singletonList("openid");
		ClientTokenModel ct = converter.convertSaClientToClientToken(cm, scopes);
		Assertions.assertNotNull(ct.clientToken);
		Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, ct.clientId);
		Assertions.assertEquals(scopes, ct.scopes);
		Assertions.assertEquals(SaOAuth2Consts.TokenType.Bearer, ct.tokenType);
		Assertions.assertEquals(GrantType.client_credentials, ct.grantType);
		Assertions.assertNotNull(ct.extraData);
		Assertions.assertTrue(ct.expiresTime > System.currentTimeMillis());
	}

	/** never-expire 超时转出来的 expiresTime 应该是 -1 */
	@Test
	public void convertWithNeverExpireTimeout() {
		AccessTokenModel at = converter.convertRequestAuthToAccessToken(ra(), -1);
		Assertions.assertEquals(-1, at.expiresTime);
		Assertions.assertEquals(-1, at.getExpiresIn());
	}

	/** extraData 为独立 map 时拷贝不该改到原对象 */
	@Test
	public void extraDataIsCopied() {
		AccessTokenModel at = converter.convertRequestAuthToAccessToken(ra(), 3600);
		at.extraData = new LinkedHashMap<>();
		at.extraData.put("a", 1);
		RefreshTokenModel rt = converter.convertAccessTokenToRefreshToken(at, 7200);
		rt.extraData.put("b", 2);
		Assertions.assertFalse(at.extraData.containsKey("b"));
	}

	/** 默认请求授权模型 */
	private RequestAuthModel ra() {
		return new RequestAuthModel()
				.setClientId(OAuth2TestSupport.CLIENT_ID)
				.setLoginId(OAuth2TestSupport.LOGIN_ID)
				.setRedirectUri(OAuth2TestSupport.REDIRECT)
				.setResponseType("code")
				.setScopes(Arrays.asList("openid", "userid"))
				.setNonce("n-1");
	}

}

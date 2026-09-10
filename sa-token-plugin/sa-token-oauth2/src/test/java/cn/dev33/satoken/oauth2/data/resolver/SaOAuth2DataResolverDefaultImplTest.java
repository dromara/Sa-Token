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
package cn.dev33.satoken.oauth2.data.resolver;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.request.ClientIdAndSecretModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.secure.SaBase64Util;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 默认数据解析器：读 client/token、组返回值
 */
@OAuth2Test
public class SaOAuth2DataResolverDefaultImplTest {

	private SaOAuth2DataResolver resolver;

	/** 每个用例前换干净解析器 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
		resolver = SaOAuth2Manager.getDataResolver();
	}

	/** 参数里同时有 client_id 和 secret 时应该直接用参数 */
	@Test
	public void readClientIdAndSecret_fromParams() {
		OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET), () -> {
			ClientIdAndSecretModel m = resolver.readClientIdAndSecret(SaHolder.getRequest());
			Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, m.clientId);
			Assertions.assertEquals(OAuth2TestSupport.CLIENT_SECRET, m.clientSecret);
			return null;
		});
	}

	/** Basic 头应该能解出 client_id:secret */
	@Test
	public void readClientIdAndSecret_fromBasic() {
		String basic = "Basic " + SaBase64Util.encode(OAuth2TestSupport.CLIENT_ID + ":" + OAuth2TestSupport.CLIENT_SECRET);
		OAuth2TestSupport.withRequest("/oauth2/token", "POST", null,
				OAuth2TestSupport.params("Authorization", basic), () -> {
			ClientIdAndSecretModel m = resolver.readClientIdAndSecret(SaHolder.getRequest());
			Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, m.clientId);
			Assertions.assertEquals(OAuth2TestSupport.CLIENT_SECRET, m.clientSecret);
			return null;
		});
	}

	/** Basic 头只有 clientId 没有冒号时 secret 应该是 null */
	@Test
	public void readClientIdAndSecret_fromBasicClientIdOnly() {
		String basic = "Basic " + SaBase64Util.encode(OAuth2TestSupport.CLIENT_ID);
		OAuth2TestSupport.withRequest("/oauth2/token", "POST", null,
				OAuth2TestSupport.params("Authorization", basic), () -> {
			ClientIdAndSecretModel m = resolver.readClientIdAndSecret(SaHolder.getRequest());
			Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, m.clientId);
			Assertions.assertNull(m.clientSecret);
			return null;
		});
	}

	/** 只给了 client_id 没给 secret 时 secret 应该是 null */
	@Test
	public void readClientIdAndSecret_clientIdOnlySecretNull() {
		OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID), () -> {
			ClientIdAndSecretModel m = resolver.readClientIdAndSecret(SaHolder.getRequest());
			Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, m.clientId);
			Assertions.assertNull(m.clientSecret);
			return null;
		});
	}

	/** 什么 client 信息都没有应该抛 30191 */
	@Test
	public void readClientIdAndSecret_missing_30191() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", Collections.emptyMap(),
						() -> resolver.readClientIdAndSecret(SaHolder.getRequest())));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30191, ex.getCode());
	}

	/** access_token 优先读参数，其次 Bearer，前缀不对就返回 null */
	@Test
	public void readAccessToken() {
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params("access_token", "from-param"), () -> {
			Assertions.assertEquals("from-param", resolver.readAccessToken(SaHolder.getRequest()));
			return null;
		});
		OAuth2TestSupport.withRequest("/api", "GET", null,
				OAuth2TestSupport.params("Authorization", "Bearer from-header"), () -> {
			Assertions.assertEquals("from-header", resolver.readAccessToken(SaHolder.getRequest()));
			return null;
		});
		OAuth2TestSupport.withPath("/api", () -> {
			Assertions.assertNull(resolver.readAccessToken(SaHolder.getRequest()));
			return null;
		});
		OAuth2TestSupport.withRequest("/api", "GET", null,
				OAuth2TestSupport.params("Authorization", "Token xxx"), () -> {
			Assertions.assertNull(resolver.readAccessToken(SaHolder.getRequest()));
			return null;
		});
	}

	/** client_token 优先读参数，其次 Bearer，前缀不对就返回 null */
	@Test
	public void readClientToken() {
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params("client_token", "from-param"), () -> {
			Assertions.assertEquals("from-param", resolver.readClientToken(SaHolder.getRequest()));
			return null;
		});
		OAuth2TestSupport.withRequest("/api", "GET", null,
				OAuth2TestSupport.params("Authorization", "Bearer from-header"), () -> {
			Assertions.assertEquals("from-header", resolver.readClientToken(SaHolder.getRequest()));
			return null;
		});
		OAuth2TestSupport.withPath("/api", () -> {
			Assertions.assertNull(resolver.readClientToken(SaHolder.getRequest()));
			return null;
		});
		OAuth2TestSupport.withRequest("/api", "GET", null,
				OAuth2TestSupport.params("Authorization", "Basic xxx"), () -> {
			Assertions.assertNull(resolver.readClientToken(SaHolder.getRequest()));
			return null;
		});
	}

	/** readRequestAuthModel 应该把请求参数填进模型 */
	@Test
	public void readRequestAuthModel() {
		OAuth2TestSupport.withRequest("/oauth2/authorize", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"response_type", "code",
				"redirect_uri", OAuth2TestSupport.REDIRECT,
				"state", "st",
				"nonce", "n1",
				"scope", "openid userid"), () -> {
			RequestAuthModel ra = resolver.readRequestAuthModel(SaHolder.getRequest(), OAuth2TestSupport.LOGIN_ID);
			Assertions.assertEquals(OAuth2TestSupport.CLIENT_ID, ra.clientId);
			Assertions.assertEquals("code", ra.responseType);
			Assertions.assertEquals(OAuth2TestSupport.REDIRECT, ra.redirectUri);
			Assertions.assertEquals("st", ra.state);
			Assertions.assertEquals("n1", ra.nonce);
			Assertions.assertEquals(Arrays.asList("openid", "userid"), ra.scopes);
			Assertions.assertEquals(OAuth2TestSupport.LOGIN_ID, ra.loginId);
			return null;
		});
	}

	/** buildAccessTokenReturnValue 应该合并 extraData，hideStatusField 时藏掉 code/msg */
	@Test
	public void buildAccessTokenReturnValue() {
		AccessTokenModel at = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
				Collections.singletonList("openid"));
		at.refreshToken = "rt";
		at.tokenType = SaOAuth2Consts.TokenType.Bearer;
		at.expiresTime = System.currentTimeMillis() + 3600_000;
		at.refreshExpiresTime = System.currentTimeMillis() + 7200_000;
		at.extraData = new LinkedHashMap<>();
		at.extraData.put("openid", "oid");
		Map<String, Object> map = resolver.buildAccessTokenReturnValue(at);
		Assertions.assertEquals("at", map.get("access_token"));
		Assertions.assertEquals("rt", map.get("refresh_token"));
		Assertions.assertEquals("oid", map.get("openid"));
		Assertions.assertEquals(200, map.get("code"));

		SaOAuth2Manager.getServerConfig().setHideStatusField(true);
		Map<String, Object> hidden = resolver.buildAccessTokenReturnValue(at);
		Assertions.assertFalse(hidden.containsKey("code"));
		Assertions.assertFalse(hidden.containsKey("msg"));
		Assertions.assertEquals("at", hidden.get("access_token"));
	}

	/** mode4ReturnAccessToken=true 时 client_token 返回值也带 access_token */
	@Test
	public void buildClientTokenReturnValue_mode4() {
		ClientTokenModel ct = new ClientTokenModel("ct", OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid"));
		ct.tokenType = SaOAuth2Consts.TokenType.Bearer;
		ct.expiresTime = System.currentTimeMillis() + 3600_000;
		ct.extraData = new LinkedHashMap<>();
		ct.extraData.put("k", "v");
		Map<String, Object> map = resolver.buildClientTokenReturnValue(ct);
		Assertions.assertEquals("ct", map.get("client_token"));
		Assertions.assertNull(map.get("access_token"));
		Assertions.assertEquals("v", map.get("k"));

		SaOAuth2Manager.getServerConfig().setMode4ReturnAccessToken(true);
		Map<String, Object> mode4 = resolver.buildClientTokenReturnValue(ct);
		Assertions.assertEquals("ct", mode4.get("access_token"));

		SaOAuth2Manager.getServerConfig().setHideStatusField(true);
		Map<String, Object> hidden = resolver.buildClientTokenReturnValue(ct);
		Assertions.assertFalse(hidden.containsKey("code"));
		Assertions.assertFalse(hidden.containsKey("msg"));
	}

	/** 默认刷新返回值应该复用 access_token 那套 */
	@Test
	public void buildRefreshTokenReturnValue() {
		AccessTokenModel at = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
				Collections.emptyList());
		at.tokenType = SaOAuth2Consts.TokenType.Bearer;
		at.expiresTime = System.currentTimeMillis() + 3600_000;
		at.refreshExpiresTime = System.currentTimeMillis() + 7200_000;
		at.extraData = new LinkedHashMap<>();
		Map<String, Object> map = resolver.buildRefreshTokenReturnValue(at);
		Assertions.assertEquals("at", map.get("access_token"));
	}

	/** 默认回收返回值应该是 SaResult.ok */
	@Test
	public void buildRevokeTokenReturnValue() {
		Map<String, Object> map = resolver.buildRevokeTokenReturnValue();
		Assertions.assertEquals(200, ((SaResult) map).getCode());
	}

}

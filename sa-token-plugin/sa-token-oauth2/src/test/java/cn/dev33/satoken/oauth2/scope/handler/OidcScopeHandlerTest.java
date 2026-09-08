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
package cn.dev33.satoken.oauth2.scope.handler;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.oidc.IdTokenModel;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.scope.CommonScope;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OIDC 权限处理器：iss、nonce、jwt id_token、extra 去 null
 */
@SaTokenTest
public class OidcScopeHandlerTest {

	private final OidcScopeHandler handler = new OidcScopeHandler();

	/** 每个用例前装默认配置，jwt 秘钥已经在 support 里 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** 处理的 scope 应该就是 oidc */
	@Test
	public void getHandlerScope_isOidc() {
		Assertions.assertEquals(CommonScope.OIDC, handler.getHandlerScope());
	}

	/** 刷新 AccessToken 时应该重新跑 workAccessToken */
	@Test
	public void refreshAccessTokenIsWork_true() {
		Assertions.assertTrue(handler.refreshAccessTokenIsWork());
	}

	/** workClientToken 目前是空实现 */
	@Test
	public void workClientToken_noOp() {
		handler.workClientToken(new ClientTokenModel("ct", OAuth2TestSupport.CLIENT_ID, Arrays.asList("oidc")));
	}

	/** workExtraData 默认应该原样返回 */
	@Test
	public void workExtraData_identity() {
		IdTokenModel id = new IdTokenModel();
		Assertions.assertSame(id, handler.workExtraData(id));
	}

	/** 配了 iss 时 getIss 应该直接用配置值 */
	@Test
	public void getIss_fromConfig() {
		SaOAuth2Manager.getServerConfig().getOidc().setIss("http://cfg-iss");
		Assertions.assertEquals("http://cfg-iss", handler.getIss());
	}

	/** 没配 iss 时应该按请求 url 算出协议+主机，带端口就带上 */
	@Test
	public void getIss_fromRequestUrl() {
		OAuth2TestSupport.withPath("/abc", () -> {
			Assertions.assertEquals("http://127.0.0.1", handler.getIss());
			((SaRequestForMock) SaHolder.getRequest()).url = "http://localhost:8081/abc/xyz?name=a";
			Assertions.assertEquals("http://localhost:8081", handler.getIss());
			return null;
		});
	}

	/** url 非法时应该包一层 SaOAuth2Exception */
	@Test
	public void getIss_malformedUrl_throws() {
		OAuth2TestSupport.withPath("/x", () -> {
			((SaRequestForMock) SaHolder.getRequest()).url = "not-a-url";
			SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, handler::getIss);
			Assertions.assertTrue(ex.getCause() instanceof MalformedURLException);
			return null;
		});
	}

	/** 请求参数里有 nonce 时应该用参数值 */
	@Test
	public void getNonce_fromParam() {
		OAuth2TestSupport.withRequest("/x", OAuth2TestSupport.params("nonce", "nonce-from-param"), () -> {
			Assertions.assertEquals("nonce-from-param", handler.getNonce());
			return null;
		});
	}

	/** 参数没有 nonce 时应该按 code 去 dao 里找 */
	@Test
	public void getNonce_fromCodeDao() {
		CodeModel cm = new CodeModel("code-1", OAuth2TestSupport.CLIENT_ID, Arrays.asList("oidc"),
				OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.REDIRECT, "nonce-from-dao");
		SaOAuth2Manager.getDao().saveCodeNonceIndex(cm);
		OAuth2TestSupport.withRequest("/x", OAuth2TestSupport.params("code", "code-1"), () -> {
			Assertions.assertEquals("nonce-from-dao", handler.getNonce());
			return null;
		});
	}

	/** 参数和 dao 都没有时应该随机出 32 位 */
	@Test
	public void getNonce_randomWhenMissing() {
		OAuth2TestSupport.withPath("/x", () -> {
			String nonce = handler.getNonce();
			Assertions.assertEquals(32, nonce.length());
			return null;
		});
	}

	/** convertIdTokenToMap 应该丢掉 extra 里值为 null 的项 */
	@Test
	public void convertIdTokenToMap_dropsNullExtra() {
		IdTokenModel id = new IdTokenModel();
		id.iss = "http://iss";
		id.sub = 10001;
		id.aud = "1001";
		id.exp = 10;
		id.iat = 1;
		id.authTime = 2;
		id.nonce = "n";
		id.azp = "1001";
		id.extraData = new LinkedHashMap<>();
		id.extraData.put("keep", "yes");
		id.extraData.put("drop", null);
		Map<String, Object> map = handler.convertIdTokenToMap(id);
		Assertions.assertEquals("http://iss", map.get("iss"));
		Assertions.assertEquals(10001, map.get("sub"));
		Assertions.assertEquals("yes", map.get("keep"));
		Assertions.assertFalse(map.containsKey("drop"));
		Assertions.assertFalse(id.extraData.containsKey("drop"));
	}

	/** generateJwtIdToken 应该签出三段 jwt */
	@Test
	public void generateJwtIdToken_returnsJwt() {
		IdTokenModel id = new IdTokenModel();
		id.iss = "http://iss";
		id.sub = 10001;
		id.aud = "1001";
		id.exp = System.currentTimeMillis() / 1000 + 600;
		id.iat = System.currentTimeMillis() / 1000;
		id.authTime = id.iat;
		id.nonce = "n-jwt";
		id.azp = "1001";
		id.extraData = new LinkedHashMap<>();
		String jwt = handler.generateJwtIdToken(id);
		Assertions.assertEquals(3, jwt.split("\\.").length);
	}

	/** workAccessToken 挂上 mock 请求和登录会话后应该把 id_token 塞进 extra */
	@Test
	public void workAccessToken_putsIdToken() {
		OAuth2TestSupport.withRequest("/oauth2/token", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"nonce", "nonce-work-at"), () -> {
			StpUtil.login(OAuth2TestSupport.LOGIN_ID);
			AccessTokenModel at = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID,
					OAuth2TestSupport.LOGIN_ID, Arrays.asList("oidc"));
			at.extraData = new LinkedHashMap<>();
			handler.workAccessToken(at);
			Object idToken = at.extraData.get("id_token");
			Assertions.assertNotNull(idToken);
			Assertions.assertEquals(3, String.valueOf(idToken).split("\\.").length);
			return null;
		});
	}

}

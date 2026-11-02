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
package cn.dev33.satoken.oauth2.granttype.handler;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 刷新令牌 grant_type 处理器
 */
@OAuth2Test
public class RefreshTokenGrantTypeHandlerTest {

	private final RefreshTokenGrantTypeHandler handler = new RefreshTokenGrantTypeHandler();

	/** 每个用例前换干净现场 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** 处理器声明的 grant_type 应该是 refresh_token */
	@Test
	public void getHandlerGrantType() {
		Assertions.assertEquals(GrantType.refresh_token, handler.getHandlerGrantType());
	}

	/** 用有效 refresh_token 应该能刷出新的 access_token */
	@Test
	public void getAccessToken_success() {
		AccessTokenModel old = generateAt();
		AccessTokenModel neu = OAuth2TestSupport.withPost("/oauth2/refresh", OAuth2TestSupport.params(
				"refresh_token", old.refreshToken),
				() -> handler.getAccessToken(SaHolder.getRequest(), OAuth2TestSupport.CLIENT_ID,
						Collections.singletonList("openid")));
		Assertions.assertNotNull(neu.accessToken);
		Assertions.assertNotEquals(old.accessToken, neu.accessToken);
	}

	/** 无效 refresh_token 应该抛 30111 */
	@Test
	public void getAccessToken_invalidRt_30111() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/refresh", OAuth2TestSupport.params(
						"refresh_token", "no-rt"),
						() -> handler.getAccessToken(SaHolder.getRequest(), OAuth2TestSupport.CLIENT_ID,
								Collections.singletonList("openid"))));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30111, ex.getCode());
	}

	/** refresh_token 对应的 clientId 对不上应该抛 30122 */
	@Test
	public void getAccessToken_clientIdMismatch_30122() {
		AccessTokenModel old = generateAt();
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/refresh", OAuth2TestSupport.params(
						"refresh_token", old.refreshToken),
						() -> handler.getAccessToken(SaHolder.getRequest(), "other-client",
								Collections.singletonList("openid"))));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30122, ex.getCode());
	}

	/** 造一个带 RT 的 AT */
	private AccessTokenModel generateAt() {
		RequestAuthModel ra = new RequestAuthModel()
				.setClientId(OAuth2TestSupport.CLIENT_ID)
				.setLoginId(OAuth2TestSupport.LOGIN_ID)
				.setRedirectUri(OAuth2TestSupport.REDIRECT)
				.setScopes(Collections.singletonList("openid"));
		return SaOAuth2Manager.getDataGenerate().generateAccessToken(ra, true, null);
	}

}

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
import cn.dev33.satoken.oauth2.data.model.CodeModel;
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
 * 授权码 grant_type 处理器
 */
@OAuth2Test
public class AuthorizationCodeGrantTypeHandlerTest {

	private final AuthorizationCodeGrantTypeHandler handler = new AuthorizationCodeGrantTypeHandler();

	/** 每个用例前换干净现场 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** 处理器声明的 grant_type 应该是 authorization_code */
	@Test
	public void getHandlerGrantType() {
		Assertions.assertEquals(GrantType.authorization_code, handler.getHandlerGrantType());
	}

	/** 用有效 code 应该能换出 access_token */
	@Test
	public void getAccessToken_success() {
		CodeModel cm = SaOAuth2Manager.getDataGenerate().generateCode(ra());
		AccessTokenModel at = OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET,
				"code", cm.code,
				"redirect_uri", OAuth2TestSupport.REDIRECT),
				() -> handler.getAccessToken(SaHolder.getRequest(),
						OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid")));
		Assertions.assertNotNull(at.accessToken);
		Assertions.assertEquals(GrantType.authorization_code, at.grantType);
	}

	/** 无效 code 应该抛 30110 */
	@Test
	public void getAccessToken_badCode_30110() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET,
						"code", "no-code",
						"redirect_uri", OAuth2TestSupport.REDIRECT),
						() -> handler.getAccessToken(SaHolder.getRequest(),
								OAuth2TestSupport.CLIENT_ID, Collections.singletonList("openid"))));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30110, ex.getCode());
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

}

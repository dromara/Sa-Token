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
package cn.dev33.satoken.oauth2.annotation.handler;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.annotation.SaCheckAccessToken;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2AccessTokenException;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Access-Token 注解处理器：有效 token、缺 token、scope 不对
 */
@OAuth2Test
public class SaCheckAccessTokenHandlerTest {

	private final SaCheckAccessTokenHandler handler = new SaCheckAccessTokenHandler();

	/** 每个用例前装默认配置 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** getHandlerAnnotationClass 应该指向 SaCheckAccessToken */
	@Test
	public void getHandlerAnnotationClass_isSaCheckAccessToken() {
		Assertions.assertEquals(SaCheckAccessToken.class, handler.getHandlerAnnotationClass());
	}

	/** 请求里带着有效 AT 且 scope 对得上时应该放过 */
	@Test
	public void _checkMethod_validTokenAndScope() {
		AccessTokenModel at = generateAt();
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params("access_token", at.getAccessToken()), () -> {
			SaCheckAccessTokenHandler._checkMethod(new String[]{"userinfo"});
			return null;
		});
	}

	/** 请求里没有 AT 时应该抛 30106 */
	@Test
	public void _checkMethod_missingToken_throws30106() {
		OAuth2TestSupport.withPath("/api", () -> {
			SaOAuth2AccessTokenException ex = Assertions.assertThrows(SaOAuth2AccessTokenException.class,
					() -> SaCheckAccessTokenHandler._checkMethod(new String[]{"userinfo"}));
			Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30106, ex.getCode());
			return null;
		});
	}

	/** token 有效但缺指定 scope 时应该抛 30108 */
	@Test
	public void _checkMethod_wrongScope_throws30108() {
		AccessTokenModel at = generateAt();
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params("access_token", at.getAccessToken()), () -> {
			SaOAuth2AccessTokenException ex = Assertions.assertThrows(SaOAuth2AccessTokenException.class,
					() -> SaCheckAccessTokenHandler._checkMethod(new String[]{"admin"}));
			Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30108, ex.getCode());
			return null;
		});
	}

	/** checkMethod 应该能吃到类上真实注解实例，而不是自己拼 */
	@Test
	public void checkMethod_usesRealAnnotation() {
		AccessTokenModel at = generateAt();
		SaCheckAccessToken anno = DummyAccessTokenApi.class.getAnnotation(SaCheckAccessToken.class);
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params("access_token", at.getAccessToken()), () -> {
			handler.checkMethod(anno, DummyAccessTokenApi.class);
			return null;
		});
	}

	private AccessTokenModel generateAt() {
		return SaOAuth2Manager.getDataGenerate().generateAccessToken(new RequestAuthModel()
				.setClientId(OAuth2TestSupport.CLIENT_ID)
				.setScopes(Arrays.asList("userinfo"))
				.setLoginId(OAuth2TestSupport.LOGIN_ID)
				.setRedirectUri(OAuth2TestSupport.REDIRECT), true, null);
	}

	@SaCheckAccessToken(scope = "userinfo")
	private static class DummyAccessTokenApi {
	}

}

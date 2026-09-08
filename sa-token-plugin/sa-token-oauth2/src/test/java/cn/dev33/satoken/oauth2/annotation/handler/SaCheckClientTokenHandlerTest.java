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
import cn.dev33.satoken.oauth2.annotation.SaCheckClientToken;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2ClientTokenException;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Client-Token 注解处理器：有效 token、缺 token、scope 不对
 */
@SaTokenTest
public class SaCheckClientTokenHandlerTest {

	private final SaCheckClientTokenHandler handler = new SaCheckClientTokenHandler();

	/** 每个用例前装默认配置 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** getHandlerAnnotationClass 应该指向 SaCheckClientToken */
	@Test
	public void getHandlerAnnotationClass_isSaCheckClientToken() {
		Assertions.assertEquals(SaCheckClientToken.class, handler.getHandlerAnnotationClass());
	}

	/** 请求里带着有效 CT 且 scope 对得上时应该放过 */
	@Test
	public void _checkMethod_validTokenAndScope() {
		ClientTokenModel ct = generateCt();
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params("client_token", ct.getClientToken()), () -> {
			SaCheckClientTokenHandler._checkMethod(new String[]{"userinfo"});
			return null;
		});
	}

	/** 请求里没有 CT 时应该抛 30107 */
	@Test
	public void _checkMethod_missingToken_throws30107() {
		OAuth2TestSupport.withPath("/api", () -> {
			SaOAuth2ClientTokenException ex = Assertions.assertThrows(SaOAuth2ClientTokenException.class,
					() -> SaCheckClientTokenHandler._checkMethod(new String[]{"userinfo"}));
			Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30107, ex.getCode());
			return null;
		});
	}

	/** token 有效但缺指定 scope 时应该抛 30109 */
	@Test
	public void _checkMethod_wrongScope_throws30109() {
		ClientTokenModel ct = generateCt();
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params("client_token", ct.getClientToken()), () -> {
			SaOAuth2ClientTokenException ex = Assertions.assertThrows(SaOAuth2ClientTokenException.class,
					() -> SaCheckClientTokenHandler._checkMethod(new String[]{"admin"}));
			Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30109, ex.getCode());
			return null;
		});
	}

	/** checkMethod 应该能吃到类上真实注解实例 */
	@Test
	public void checkMethod_usesRealAnnotation() {
		ClientTokenModel ct = generateCt();
		SaCheckClientToken anno = DummyClientTokenApi.class.getAnnotation(SaCheckClientToken.class);
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params("client_token", ct.getClientToken()), () -> {
			handler.checkMethod(anno, DummyClientTokenApi.class);
			return null;
		});
	}

	private ClientTokenModel generateCt() {
		return SaOAuth2Manager.getDataGenerate().generateClientToken(OAuth2TestSupport.CLIENT_ID, Arrays.asList("userinfo"));
	}

	@SaCheckClientToken(scope = "userinfo")
	private static class DummyClientTokenApi {
	}

}

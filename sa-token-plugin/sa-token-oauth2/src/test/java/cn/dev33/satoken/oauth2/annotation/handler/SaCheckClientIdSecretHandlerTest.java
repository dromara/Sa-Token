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

import cn.dev33.satoken.oauth2.annotation.SaCheckClientIdSecret;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2ClientModelException;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ClientId+Secret 注解处理器：对得上、密钥错 30115、完全没带 30191
 */
@OAuth2Test
public class SaCheckClientIdSecretHandlerTest {

	private final SaCheckClientIdSecretHandler handler = new SaCheckClientIdSecretHandler();

	/** 每个用例前装默认配置 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** getHandlerAnnotationClass 应该指向 SaCheckClientIdSecret */
	@Test
	public void getHandlerAnnotationClass_isSaCheckClientIdSecret() {
		Assertions.assertEquals(SaCheckClientIdSecret.class, handler.getHandlerAnnotationClass());
	}

	/** 请求里带着对得上的 client_id + secret 时应该放过 */
	@Test
	public void _checkMethod_validClientSecret() {
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET), () -> {
			SaCheckClientIdSecretHandler._checkMethod();
			return null;
		});
	}

	/** secret 不对时应该抛 30115 */
	@Test
	public void _checkMethod_wrongSecret_throws30115() {
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", "wrong"), () -> {
			SaOAuth2ClientModelException ex = Assertions.assertThrows(SaOAuth2ClientModelException.class,
					SaCheckClientIdSecretHandler::_checkMethod);
			Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30115, ex.getCode());
			return null;
		});
	}

	/** 请求里完全没带 client 信息时应该抛 30191 */
	@Test
	public void _checkMethod_missingClient_throws30191() {
		OAuth2TestSupport.withPath("/api", () -> {
			SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class,
					SaCheckClientIdSecretHandler::_checkMethod);
			Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30191, ex.getCode());
			return null;
		});
	}

	/** checkMethod 应该能吃到类上真实注解实例 */
	@Test
	public void checkMethod_usesRealAnnotation() {
		SaCheckClientIdSecret anno = DummyClientSecretApi.class.getAnnotation(SaCheckClientIdSecret.class);
		OAuth2TestSupport.withRequest("/api", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET), () -> {
			handler.checkMethod(anno, DummyClientSecretApi.class);
			return null;
		});
	}

	@SaCheckClientIdSecret
	private static class DummyClientSecretApi {
	}

}

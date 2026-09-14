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
package cn.dev33.satoken.oauth2.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * OIDC 配置：默认 iss 空、超时 600、setter、toString
 */
public class SaOAuth2OidcConfigTest {

	/** 默认 iss 应该是 null，idTokenTimeout 十分钟 */
	@Test
	public void defaults_issNullTimeout600() {
		SaOAuth2OidcConfig cfg = new SaOAuth2OidcConfig();
		Assertions.assertNull(cfg.getIss());
		Assertions.assertEquals(600, cfg.getIdTokenTimeout());
	}

	/** setter 应该能连缀写回去 */
	@Test
	public void gettersAndSetters_roundTrip() {
		SaOAuth2OidcConfig cfg = new SaOAuth2OidcConfig()
				.setIss("http://oauth-server.com")
				.setIdTokenTimeout(120);
		Assertions.assertEquals("http://oauth-server.com", cfg.getIss());
		Assertions.assertEquals(120, cfg.getIdTokenTimeout());
	}

	/** toString 里应该能看到 iss 和超时 */
	@Test
	public void toString_containsFields() {
		String text = new SaOAuth2OidcConfig().setIss("http://a.com").toString();
		Assertions.assertTrue(text.startsWith("SaOAuth2OidcConfig{"));
		Assertions.assertTrue(text.contains("iss='http://a.com'"));
		Assertions.assertTrue(text.contains("idTokenTimeout="));
	}

}

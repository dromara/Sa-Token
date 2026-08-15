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
package cn.dev33.satoken.plugin;

import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * SSO / OAuth2 allow-url 通配符配置校验
 */
public class AllowUrlWildcardPatternTest {

	@Test
	public void oauth2AllowUrlWildcardPattern() {
		Assertions.assertDoesNotThrow(() ->
				SaOAuth2Template.checkRedirectUriListNormalStaticMethod(
						Arrays.asList("http://sa-sso-client1.com/sso/login", "http://sa-sso-client1.com/*", "http://sa-sso-client1.com:9003/*", "http://sa-sso-client1.com:*")));
		Assertions.assertThrows(SaOAuth2Exception.class, () ->
				SaOAuth2Template.checkRedirectUriListNormalStaticMethod(
						Collections.singletonList("http://sa-sso-client1.com*")));
		Assertions.assertThrows(SaOAuth2Exception.class, () ->
				SaOAuth2Template.checkRedirectUriListNormalStaticMethod(
						Collections.singletonList("http://sa-sso-client1.com:9003*")));
		Assertions.assertThrows(SaOAuth2Exception.class, () ->
				SaOAuth2Template.checkRedirectUriListNormalStaticMethod(
						Collections.singletonList("http://*.sa-sso-client1.com/")));
	}

	@Test
	public void ssoAllowUrlWildcardPattern() {
		Assertions.assertDoesNotThrow(() ->
				SaSsoServerTemplate.checkAllowUrlListStaticMethod(
						Arrays.asList("*", "http://sa-sso-client1.com:9003/*", "http://sa-sso-client1.com:*")));
		Assertions.assertThrows(SaSsoException.class, () ->
				SaSsoServerTemplate.checkAllowUrlListStaticMethod(
						Collections.singletonList("http://sa-sso-client1.com*")));
		Assertions.assertThrows(SaSsoException.class, () ->
				SaSsoServerTemplate.checkAllowUrlListStaticMethod(
						Collections.singletonList("http://sa-sso-client1.com:9003*")));
	}

	@Test
	public void oauth2RejectAtInRedirectUri() {
		SaOAuth2Template template = new SaOAuth2Template();
		Assertions.assertThrows(SaOAuth2Exception.class, () ->
				template.checkRedirectUri("1001", "http://sa-oauth-client.com:123@sa-token.com"));
		Assertions.assertThrows(SaOAuth2Exception.class, () ->
				template.checkRedirectUri("1001", "http://sa-oauth-client.com:123%40sa-token.com"));
		Assertions.assertThrows(SaOAuth2Exception.class, () ->
				template.checkRedirectUri("1001", "http://sa-oauth-client.com:123%2540sa-token.com"));
	}

	@Test
	public void ssoRejectAtInRedirectUrl() {
		SaSsoServerTemplate template = new SaSsoServerTemplate();
		Assertions.assertThrows(SaSsoException.class, () ->
				template.checkRedirectUrl("sso-client1", "http://sa-sso-client1.com:9003@sa-token.com"));
		Assertions.assertThrows(SaSsoException.class, () ->
				template.checkRedirectUrl("sso-client1", "http://sa-sso-client1.com:9003%40sa-token.com"));
		Assertions.assertThrows(SaSsoException.class, () ->
				template.checkRedirectUrl("sso-client1", "http://sa-sso-client1.com:9003%2540sa-token.com"));
	}

}

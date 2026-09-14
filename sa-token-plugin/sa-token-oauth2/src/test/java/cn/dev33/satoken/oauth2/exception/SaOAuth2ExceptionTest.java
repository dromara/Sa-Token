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
package cn.dev33.satoken.oauth2.exception;

import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * OAuth2 各异常：构造、setCode、throwBy 真假分支
 */
public class SaOAuth2ExceptionTest {

	/** 只传 message 时应该把文案带上，setCode 也能写回 */
	@Test
	public void base_messageAndSetCode() {
		SaOAuth2Exception ex = new SaOAuth2Exception("oauth boom");
		ex.setCode(SaOAuth2ErrorCode.CODE_30191);
		Assertions.assertEquals("oauth boom", ex.getMessage());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30191, ex.getCode());
	}

	/** 传 cause 时应该把根因挂上 */
	@Test
	public void base_causeCtor() {
		RuntimeException cause = new RuntimeException("root");
		SaOAuth2Exception ex = new SaOAuth2Exception(cause);
		Assertions.assertSame(cause, ex.getCause());
	}

	/** throwBy 在 flag=true 时应该抛，false 时放过 */
	@Test
	public void base_throwBy() {
		SaOAuth2Exception.throwBy(false, "no", SaOAuth2ErrorCode.CODE_30191);
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class,
				() -> SaOAuth2Exception.throwBy(true, "by", SaOAuth2ErrorCode.CODE_30191));
		Assertions.assertEquals("by", ex.getMessage());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30191, ex.getCode());
	}

	/** AccessToken 异常应该能记下 token 值，throwBy 真假都能走 */
	@Test
	public void accessToken_setAndThrowBy() {
		RuntimeException cause = new RuntimeException("c");
		Assertions.assertSame(cause, new SaOAuth2AccessTokenException(cause).getCause());
		SaOAuth2AccessTokenException ex = new SaOAuth2AccessTokenException("bad at");
		Assertions.assertSame(ex, ex.setAccessToken("at-1"));
		Assertions.assertEquals("at-1", ex.getAccessToken());
		ex.setCode(SaOAuth2ErrorCode.CODE_30106);
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30106, ex.getCode());
		SaOAuth2AccessTokenException.throwBy(false, "no", SaOAuth2ErrorCode.CODE_30106);
		SaOAuth2AccessTokenException thrown = Assertions.assertThrows(SaOAuth2AccessTokenException.class,
				() -> SaOAuth2AccessTokenException.throwBy(true, "at boom", SaOAuth2ErrorCode.CODE_30106));
		Assertions.assertEquals("at boom", thrown.getMessage());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30106, thrown.getCode());
	}

	/** AccessToken Scope 异常应该能记下 token 和 scope */
	@Test
	public void accessTokenScope_setAndThrowBy() {
		RuntimeException cause = new RuntimeException("c");
		Assertions.assertSame(cause, new SaOAuth2AccessTokenScopeException(cause).getCause());
		SaOAuth2AccessTokenScopeException ex = new SaOAuth2AccessTokenScopeException("no scope");
		Assertions.assertSame(ex, ex.setAccessToken("at-2"));
		Assertions.assertSame(ex, ex.setScope("userinfo"));
		Assertions.assertEquals("at-2", ex.getAccessToken());
		Assertions.assertEquals("userinfo", ex.getScope());
		SaOAuth2AccessTokenScopeException.throwBy(false, "no", SaOAuth2ErrorCode.CODE_30108);
		SaOAuth2AccessTokenScopeException thrown = Assertions.assertThrows(SaOAuth2AccessTokenScopeException.class,
				() -> SaOAuth2AccessTokenScopeException.throwBy(true, "scope boom", SaOAuth2ErrorCode.CODE_30108));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30108, thrown.getCode());
	}

	/** 授权码异常 throwBy 应该把 code 值一起带上 */
	@Test
	public void authorizationCode_setAndThrowBy() {
		RuntimeException cause = new RuntimeException("c");
		Assertions.assertSame(cause, new SaOAuth2AuthorizationCodeException(cause).getCause());
		SaOAuth2AuthorizationCodeException ex = new SaOAuth2AuthorizationCodeException("bad code");
		Assertions.assertSame(ex, ex.setAuthorizationCode("abc"));
		Assertions.assertEquals("abc", ex.getAuthorizationCode());
		SaOAuth2AuthorizationCodeException.throwBy(false, "no", "x", SaOAuth2ErrorCode.CODE_30110);
		SaOAuth2AuthorizationCodeException thrown = Assertions.assertThrows(SaOAuth2AuthorizationCodeException.class,
				() -> SaOAuth2AuthorizationCodeException.throwBy(true, "code boom", "c-1", SaOAuth2ErrorCode.CODE_30110));
		Assertions.assertEquals("c-1", thrown.getAuthorizationCode());
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30110, thrown.getCode());
	}

	/** RefreshToken 两个 throwBy 重载都应该能抛 */
	@Test
	public void refreshToken_bothThrowBy() {
		RuntimeException cause = new RuntimeException("c");
		Assertions.assertSame(cause, new SaOAuth2RefreshTokenException(cause).getCause());
		SaOAuth2RefreshTokenException ex = new SaOAuth2RefreshTokenException("bad rt");
		Assertions.assertSame(ex, ex.setRefreshToken("rt-1"));
		Assertions.assertEquals("rt-1", ex.getRefreshToken());
		SaOAuth2RefreshTokenException.throwBy(false, "no", SaOAuth2ErrorCode.CODE_30111);
		SaOAuth2RefreshTokenException.throwBy(false, "no", "rt", SaOAuth2ErrorCode.CODE_30111);
		SaOAuth2RefreshTokenException a = Assertions.assertThrows(SaOAuth2RefreshTokenException.class,
				() -> SaOAuth2RefreshTokenException.throwBy(true, "rt boom", SaOAuth2ErrorCode.CODE_30111));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30111, a.getCode());
		SaOAuth2RefreshTokenException b = Assertions.assertThrows(SaOAuth2RefreshTokenException.class,
				() -> SaOAuth2RefreshTokenException.throwBy(true, "rt boom2", "rt-2", SaOAuth2ErrorCode.CODE_30111));
		Assertions.assertEquals("rt-2", b.getRefreshToken());
	}

	/** ClientToken 异常应该能记下 token 值 */
	@Test
	public void clientToken_setAndThrowBy() {
		RuntimeException cause = new RuntimeException("c");
		Assertions.assertSame(cause, new SaOAuth2ClientTokenException(cause).getCause());
		SaOAuth2ClientTokenException ex = new SaOAuth2ClientTokenException("bad ct");
		Assertions.assertSame(ex, ex.setClientToken("ct-1"));
		Assertions.assertEquals("ct-1", ex.getClientToken());
		SaOAuth2ClientTokenException.throwBy(false, "no", SaOAuth2ErrorCode.CODE_30107);
		SaOAuth2ClientTokenException thrown = Assertions.assertThrows(SaOAuth2ClientTokenException.class,
				() -> SaOAuth2ClientTokenException.throwBy(true, "ct boom", SaOAuth2ErrorCode.CODE_30107));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30107, thrown.getCode());
	}

	/** ClientToken Scope 异常应该能记下 token 和 scope */
	@Test
	public void clientTokenScope_setAndThrowBy() {
		RuntimeException cause = new RuntimeException("c");
		Assertions.assertSame(cause, new SaOAuth2ClientTokenScopeException(cause).getCause());
		SaOAuth2ClientTokenScopeException ex = new SaOAuth2ClientTokenScopeException("no scope");
		Assertions.assertSame(ex, ex.setClientToken("ct-2"));
		Assertions.assertSame(ex, ex.setScope("userinfo"));
		Assertions.assertEquals("ct-2", ex.getClientToken());
		Assertions.assertEquals("userinfo", ex.getScope());
		SaOAuth2ClientTokenScopeException.throwBy(false, "no", SaOAuth2ErrorCode.CODE_30109);
		SaOAuth2ClientTokenScopeException thrown = Assertions.assertThrows(SaOAuth2ClientTokenScopeException.class,
				() -> SaOAuth2ClientTokenScopeException.throwBy(true, "ct scope", SaOAuth2ErrorCode.CODE_30109));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30109, thrown.getCode());
	}

	/** ClientModel 两个 throwBy 重载都应该能抛，还能记下 clientId */
	@Test
	public void clientModel_bothThrowBy() {
		RuntimeException cause = new RuntimeException("c");
		Assertions.assertSame(cause, new SaOAuth2ClientModelException(cause).getCause());
		SaOAuth2ClientModelException ex = new SaOAuth2ClientModelException("bad client");
		Assertions.assertSame(ex, ex.setClientId("1001"));
		Assertions.assertEquals("1001", ex.getClientId());
		SaOAuth2ClientModelException.throwBy(false, "no", SaOAuth2ErrorCode.CODE_30105);
		SaOAuth2ClientModelException.throwBy(false, "no", "c", SaOAuth2ErrorCode.CODE_30105);
		SaOAuth2ClientModelException a = Assertions.assertThrows(SaOAuth2ClientModelException.class,
				() -> SaOAuth2ClientModelException.throwBy(true, "cm boom", SaOAuth2ErrorCode.CODE_30105));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30105, a.getCode());
		SaOAuth2ClientModelException b = Assertions.assertThrows(SaOAuth2ClientModelException.class,
				() -> SaOAuth2ClientModelException.throwBy(true, "cm boom2", "cid", SaOAuth2ErrorCode.CODE_30105));
		Assertions.assertEquals("cid", b.getClientId());
	}

	/** ClientModel Scope 异常应该能记下 clientId 和 scope */
	@Test
	public void clientModelScope_setAndThrowBy() {
		RuntimeException cause = new RuntimeException("c");
		Assertions.assertSame(cause, new SaOAuth2ClientModelScopeException(cause).getCause());
		SaOAuth2ClientModelScopeException ex = new SaOAuth2ClientModelScopeException("no scope");
		Assertions.assertSame(ex, ex.setClientId("1001"));
		Assertions.assertSame(ex, ex.setScope("admin"));
		Assertions.assertEquals("1001", ex.getClientId());
		Assertions.assertEquals("admin", ex.getScope());
		SaOAuth2ClientModelScopeException.throwBy(false, "no", SaOAuth2ErrorCode.CODE_30112);
		SaOAuth2ClientModelScopeException thrown = Assertions.assertThrows(SaOAuth2ClientModelScopeException.class,
				() -> SaOAuth2ClientModelScopeException.throwBy(true, "cm scope", SaOAuth2ErrorCode.CODE_30112));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30112, thrown.getCode());
	}

}

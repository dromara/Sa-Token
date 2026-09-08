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
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.granttype.handler.model.PasswordAuthResult;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 密码式 grant_type 处理器，以及 PasswordAuthResult
 */
@SaTokenTest
public class PasswordGrantTypeHandlerTest {

	private final PasswordGrantTypeHandler handler = new PasswordGrantTypeHandler();

	/** 每个用例前换干净现场 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** 处理器声明的 grant_type 应该是 password */
	@Test
	public void getHandlerGrantType() {
		Assertions.assertEquals(GrantType.password, handler.getHandlerGrantType());
	}

	/** sa/123456 应该能换出 access_token */
	@Test
	public void getAccessToken_success() {
		AccessTokenModel at = OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
				"username", "sa",
				"password", "123456"),
				() -> handler.getAccessToken(SaHolder.getRequest(), OAuth2TestSupport.CLIENT_ID,
						Collections.singletonList("openid")));
		Assertions.assertNotNull(at.accessToken);
		Assertions.assertEquals(GrantType.password, at.grantType);
		Assertions.assertEquals(String.valueOf(OAuth2TestSupport.LOGIN_ID), String.valueOf(at.loginId));
	}

	/** 密码错了应该抛 30161 */
	@Test
	public void getAccessToken_wrongPwd_30161() {
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"username", "sa",
						"password", "wrong"),
						() -> handler.getAccessToken(SaHolder.getRequest(), OAuth2TestSupport.CLIENT_ID,
								Collections.singletonList("openid"))));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30161, ex.getCode());
	}

	/** 默认 loginByUsernamePassword：对了能拿到 loginId，错了是 null */
	@Test
	public void loginByUsernamePassword_defaultImpl() {
		OAuth2TestSupport.withPath("/oauth2/token", () -> {
			PasswordAuthResult ok = handler.loginByUsernamePassword("sa", "123456");
			Assertions.assertEquals(String.valueOf(OAuth2TestSupport.LOGIN_ID), String.valueOf(ok.getLoginId()));
			StpUtil.logout();
			PasswordAuthResult bad = handler.loginByUsernamePassword("sa", "wrong");
			Assertions.assertNull(bad.getLoginId());
			return null;
		});
	}

	/** 子类返回 loginId=null 时 getAccessToken 也应该抛 30161 */
	@Test
	public void getAccessToken_nullLoginId_30161() {
		PasswordGrantTypeHandler custom = new PasswordGrantTypeHandler() {
			@Override
			public PasswordAuthResult loginByUsernamePassword(String username, String password) {
				return new PasswordAuthResult(null);
			}
		};
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"username", "sa",
						"password", "123456"),
						() -> custom.getAccessToken(SaHolder.getRequest(), OAuth2TestSupport.CLIENT_ID,
								Collections.singletonList("openid"))));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30161, ex.getCode());
	}

	/** PasswordAuthResult 空构造、带 loginId 构造、get/set、toString 都该能用 */
	@Test
	public void passwordAuthResult_ctorGetSetToString() {
		PasswordAuthResult empty = new PasswordAuthResult();
		Assertions.assertNull(empty.getLoginId());
		Assertions.assertSame(empty, empty.setLoginId(99));
		Assertions.assertEquals(99, empty.getLoginId());
		PasswordAuthResult withId = new PasswordAuthResult(10001);
		Assertions.assertEquals(10001, withId.getLoginId());
		Assertions.assertTrue(withId.toString().contains("10001"));
	}

}

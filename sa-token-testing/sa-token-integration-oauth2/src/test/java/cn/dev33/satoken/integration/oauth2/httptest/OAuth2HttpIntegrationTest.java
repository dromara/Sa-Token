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
package cn.dev33.satoken.integration.oauth2.httptest;

import cn.dev33.satoken.integration.oauth2.IntegrationOAuth2Application;
import cn.dev33.satoken.integration.oauth2.support.OAuth2Http;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.template.SaOAuth2Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OAuth2 真 HTTP：302 Location、token JSON、确认授权和非法回调。
 */
@SpringBootTest(classes = IntegrationOAuth2Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OAuth2HttpIntegrationTest {

	@LocalServerPort
	int port;

	/** 没登录访问 authorize 应该 200 返回登录视图，不是 302 */
	@Test
	public void authorize_notLogin_returnsView() {
		OAuth2Http.Session browser = new OAuth2Http.Session();
		OAuth2Http.Resp resp = browser.get(port, authorizePath("code", "1001"));
		Assertions.assertEquals(200, resp.status);
		Assertions.assertEquals("OAUTH2-LOGIN-VIEW", resp.body);
		Assertions.assertNull(resp.location);
	}

	/** 账号密码错了 doLogin 应该失败 JSON */
	@Test
	public void doLogin_badPassword_returnsError() {
		OAuth2Http.Session browser = new OAuth2Http.Session();
		OAuth2Http.Resp resp = browser.get(port, "/oauth2/doLogin?name=sa&pwd=bad");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertTrue(resp.body.contains("登录失败"));
	}

	/** 登录后授权码模式应该 302 到回调，Location 带 code= */
	@Test
	public void authorize_code_redirectContainsCode() {
		OAuth2Http.Session browser = login(new OAuth2Http.Session());
		OAuth2Http.Resp resp = browser.get(port, authorizePath("code", "1001") + "&state=xyz");
		Assertions.assertTrue(resp.isRedirect());
		Assertions.assertTrue(resp.location.startsWith("http://oauth-client.com/callback"));
		Assertions.assertTrue(resp.location.contains("code="));
		Assertions.assertTrue(resp.location.contains("state="));
		Assertions.assertFalse(resp.location.contains("#token="));
	}

	/** code 换 token 应该返回 access_token JSON */
	@Test
	public void token_authorizationCode_returnsAccessToken() {
		OAuth2Http.Session browser = login(new OAuth2Http.Session());
		OAuth2Http.Resp auth = browser.get(port, authorizePath("code", "1001"));
		String code = queryValue(auth.location, "code");
		Assertions.assertNotNull(code);

		Map<String, String> form = form(
				"grant_type", "authorization_code",
				"client_id", "1001",
				"client_secret", "aaaa-bbbb-cccc-dddd-eeee",
				"code", code,
				"redirect_uri", "http://oauth-client.com/callback");
		OAuth2Http.Resp token = browser.post(port, "/oauth2/token", form);
		Assertions.assertEquals(200, token.status);
		Assertions.assertTrue(token.body.contains("access_token"));
		Assertions.assertTrue(token.body.contains("refresh_token"));
	}

	/** 隐藏式登录后应该 302，Location 片段是 #token= */
	@Test
	public void authorize_implicit_fragmentIsToken() {
		OAuth2Http.Session browser = login(new OAuth2Http.Session());
		OAuth2Http.Resp resp = browser.get(port, authorizePath("token", "1001"));
		Assertions.assertTrue(resp.isRedirect());
		Assertions.assertTrue(resp.location.contains("#token="));
		Assertions.assertFalse(resp.location.contains("access_token="));
	}

	/** 密码式应该直接吐 access_token */
	@Test
	public void token_password_returnsAccessToken() {
		OAuth2Http.Session browser = new OAuth2Http.Session();
		Map<String, String> form = form(
				"grant_type", "password",
				"client_id", "1001",
				"client_secret", "aaaa-bbbb-cccc-dddd-eeee",
				"username", "sa",
				"password", "123456");
		OAuth2Http.Resp token = browser.post(port, "/oauth2/token", form);
		Assertions.assertEquals(200, token.status);
		Assertions.assertTrue(token.body.contains("access_token"));
	}

	/** 密码错了密码式应该 30161 JSON，不是 500 */
	@Test
	public void token_password_badPwd_30161() {
		OAuth2Http.Session browser = new OAuth2Http.Session();
		Map<String, String> form = form(
				"grant_type", "password",
				"client_id", "1001",
				"client_secret", "aaaa-bbbb-cccc-dddd-eeee",
				"username", "sa",
				"password", "bad");
		OAuth2Http.Resp token = browser.post(port, "/oauth2/token", form);
		Assertions.assertEquals(200, token.status);
		Assertions.assertTrue(token.body.contains(String.valueOf(SaOAuth2ErrorCode.CODE_30161)));
	}

	/** 凭证式走 /oauth2/client_token，字段是 client_token */
	@Test
	public void clientToken_returnsClientToken() {
		OAuth2Http.Session browser = new OAuth2Http.Session();
		Map<String, String> form = form(
				"grant_type", "client_credentials",
				"client_id", "1001",
				"client_secret", "aaaa-bbbb-cccc-dddd-eeee");
		OAuth2Http.Resp resp = browser.post(port, "/oauth2/client_token", form);
		Assertions.assertEquals(200, resp.status);
		Assertions.assertTrue(resp.body.contains("client_token"));
	}

	/** refresh 应该换出新的 access_token */
	@Test
	public void refresh_returnsNewAccessToken() {
		OAuth2Http.Session browser = new OAuth2Http.Session();
		Map<String, String> pwd = form(
				"grant_type", "password",
				"client_id", "1001",
				"client_secret", "aaaa-bbbb-cccc-dddd-eeee",
				"username", "sa",
				"password", "123456");
		OAuth2Http.Resp token = browser.post(port, "/oauth2/token", pwd);
		String refresh = jsonField(token.body, "refresh_token");
		Assertions.assertNotNull(refresh);

		Map<String, String> form = form(
				"grant_type", "refresh_token",
				"client_id", "1001",
				"client_secret", "aaaa-bbbb-cccc-dddd-eeee",
				"refresh_token", refresh);
		OAuth2Http.Resp refreshed = browser.post(port, "/oauth2/refresh", form);
		Assertions.assertEquals(200, refreshed.status);
		Assertions.assertTrue(refreshed.body.contains("access_token"));
	}

	/** revoke 应该成功 JSON */
	@Test
	public void revoke_existingToken_ok() {
		OAuth2Http.Session browser = new OAuth2Http.Session();
		Map<String, String> pwd = form(
				"grant_type", "password",
				"client_id", "1001",
				"client_secret", "aaaa-bbbb-cccc-dddd-eeee",
				"username", "sa",
				"password", "123456");
		OAuth2Http.Resp token = browser.post(port, "/oauth2/token", pwd);
		String at = jsonField(token.body, "access_token");

		Map<String, String> form = form(
				"client_id", "1001",
				"client_secret", "aaaa-bbbb-cccc-dddd-eeee",
				"access_token", at);
		OAuth2Http.Resp revoked = browser.post(port, "/oauth2/revoke", form);
		Assertions.assertEquals(200, revoked.status);
	}

	/** 未自动确认的 client 登录后 authorize 应该返回确认页 */
	@Test
	public void authorize_needConfirm_returnsConfirmView() {
		SaOAuth2Util.deleteGrantScope(10001, "1003");
		OAuth2Http.Session browser = login(new OAuth2Http.Session());
		OAuth2Http.Resp resp = browser.get(port, authorizePath("code", "1003") + "&scope=userinfo");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertEquals("OAUTH2-CONFIRM-VIEW", resp.body);
	}

	/** doConfirm GET 应该 30151，不是下发 code */
	@Test
	public void doConfirm_get_30151() {
		OAuth2Http.Session browser = login(new OAuth2Http.Session());
		OAuth2Http.Resp resp = browser.get(port,
				"/oauth2/doConfirm?response_type=code&client_id=1002&redirect_uri=http://oauth-client.com/callback&scope=userinfo");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertTrue(resp.body.contains(String.valueOf(SaOAuth2ErrorCode.CODE_30151)));
		Assertions.assertNull(resp.location);
	}

	/** doConfirm POST 带 build_redirect_uri 应该在 JSON 里给出带 code 的地址 */
	@Test
	public void doConfirm_post_returnsRedirectUri() {
		OAuth2Http.Session browser = login(new OAuth2Http.Session());
		Map<String, String> form = form(
				"response_type", "code",
				"client_id", "1002",
				"redirect_uri", "http://oauth-client.com/callback",
				"scope", "userinfo",
				"build_redirect_uri", "true");
		OAuth2Http.Resp resp = browser.post(port, "/oauth2/doConfirm", form);
		Assertions.assertEquals(200, resp.status);
		Assertions.assertTrue(resp.body.contains("redirect_uri"));
		Assertions.assertTrue(resp.body.contains("code="));
	}

	/** 回调带 @ 时必须拒绝，不能 302 到第三方 */
	@Test
	public void authorize_atInRedirect_not302ToEvil() {
		OAuth2Http.Session browser = login(new OAuth2Http.Session());
		OAuth2Http.Resp resp = browser.get(port,
				"/oauth2/authorize?response_type=code&client_id=1001&redirect_uri=http://oauth-client.com:123@sa-token.com");
		Assertions.assertFalse(resp.isRedirect());
		if (resp.location != null) {
			Assertions.assertFalse(resp.location.contains("sa-token.com"));
		}
		Assertions.assertTrue(resp.body.contains(String.valueOf(SaOAuth2ErrorCode.CODE_30113))
				|| resp.body.contains("redirect"));
	}

	/** 不认识的 path 应该回 not handle */
	@Test
	public void unknownPath_notHandle() {
		OAuth2Http.Session browser = new OAuth2Http.Session();
		OAuth2Http.Resp resp = browser.get(port, "/oauth2/unknown");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertEquals(SaOAuth2Consts.NOT_HANDLE, resp.body);
	}

	/** 无效 grant_type 应该 30126 JSON */
	@Test
	public void token_invalidGrantType_30126() {
		OAuth2Http.Session browser = new OAuth2Http.Session();
		Map<String, String> form = form(
				"grant_type", "foo",
				"client_id", "1001",
				"client_secret", "aaaa-bbbb-cccc-dddd-eeee");
		OAuth2Http.Resp resp = browser.post(port, "/oauth2/token", form);
		Assertions.assertEquals(200, resp.status);
		Assertions.assertTrue(resp.body.contains(String.valueOf(SaOAuth2ErrorCode.CODE_30126)));
	}

	/** 职责：先 doLogin */
	private OAuth2Http.Session login(OAuth2Http.Session browser) {
		OAuth2Http.Resp resp = browser.get(port, "/oauth2/doLogin?name=sa&pwd=123456");
		Assertions.assertEquals(200, resp.status);
		return browser;
	}

	/** 职责：拼 authorize 查询串 */
	private String authorizePath(String responseType, String clientId) {
		return "/oauth2/authorize?response_type=" + responseType
				+ "&client_id=" + clientId
				+ "&redirect_uri=http://oauth-client.com/callback";
	}

	/** 职责：拼 form map */
	private static Map<String, String> form(String... kv) {
		Map<String, String> map = new LinkedHashMap<>();
		for (int i = 0; i < kv.length; i += 2) {
			map.put(kv[i], kv[i + 1]);
		}
		return map;
	}

	/** 职责：从 Location query 里抠一个参数 */
	private static String queryValue(String location, String name) {
		if (location == null) {
			return null;
		}
		int q = location.indexOf('?');
		String query = q >= 0 ? location.substring(q + 1) : location;
		int hash = query.indexOf('#');
		if (hash >= 0) {
			query = query.substring(0, hash);
		}
		for (String part : query.split("&")) {
			String[] kv = part.split("=", 2);
			if (kv.length == 2 && name.equals(kv[0])) {
				return kv[1];
			}
		}
		return null;
	}

	/** 职责：从 JSON 里抠一个字符串字段（够用就行） */
	private static String jsonField(String body, String name) {
		Matcher m = Pattern.compile("\"" + name + "\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
		return m.find() ? m.group(1) : null;
	}

}

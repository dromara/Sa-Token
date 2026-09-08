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
package cn.dev33.satoken.oauth2.consts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * OAuth2 常量字面量要对得上生产定义
 */
public class SaOAuth2ConstsTest {

	/** 顺手把无参构造点一下，别让覆盖率漏掉 */
	@Test
	public void ctors_canNew() {
		new SaOAuth2Consts();
		new SaOAuth2Consts.Api();
		new SaOAuth2Consts.Param();
		new SaOAuth2Consts.ResponseType();
		new SaOAuth2Consts.TokenType();
		new SaOAuth2Consts.ExtraField();
		new GrantType();
	}

	/** API 路径应该就是 /oauth2/ 这一套 */
	@Test
	public void api_keepLiteralPaths() {
		Assertions.assertEquals("/oauth2/authorize", SaOAuth2Consts.Api.authorize);
		Assertions.assertEquals("/oauth2/token", SaOAuth2Consts.Api.token);
		Assertions.assertEquals("/oauth2/refresh", SaOAuth2Consts.Api.refresh);
		Assertions.assertEquals("/oauth2/revoke", SaOAuth2Consts.Api.revoke);
		Assertions.assertEquals("/oauth2/client_token", SaOAuth2Consts.Api.client_token);
		Assertions.assertEquals("/oauth2/doLogin", SaOAuth2Consts.Api.doLogin);
		Assertions.assertEquals("/oauth2/doConfirm", SaOAuth2Consts.Api.doConfirm);
	}

	/** 参数名应该就是协议里那些单词 */
	@Test
	public void param_keepLiteralNames() {
		Assertions.assertEquals("response_type", SaOAuth2Consts.Param.response_type);
		Assertions.assertEquals("client_id", SaOAuth2Consts.Param.client_id);
		Assertions.assertEquals("client_secret", SaOAuth2Consts.Param.client_secret);
		Assertions.assertEquals("redirect_uri", SaOAuth2Consts.Param.redirect_uri);
		Assertions.assertEquals("scope", SaOAuth2Consts.Param.scope);
		Assertions.assertEquals("state", SaOAuth2Consts.Param.state);
		Assertions.assertEquals("code", SaOAuth2Consts.Param.code);
		Assertions.assertEquals("token", SaOAuth2Consts.Param.token);
		Assertions.assertEquals("access_token", SaOAuth2Consts.Param.access_token);
		Assertions.assertEquals("refresh_token", SaOAuth2Consts.Param.refresh_token);
		Assertions.assertEquals("client_token", SaOAuth2Consts.Param.client_token);
		Assertions.assertEquals("grant_type", SaOAuth2Consts.Param.grant_type);
		Assertions.assertEquals("username", SaOAuth2Consts.Param.username);
		Assertions.assertEquals("password", SaOAuth2Consts.Param.password);
		Assertions.assertEquals("name", SaOAuth2Consts.Param.name);
		Assertions.assertEquals("pwd", SaOAuth2Consts.Param.pwd);
		Assertions.assertEquals("build_redirect_uri", SaOAuth2Consts.Param.build_redirect_uri);
		Assertions.assertEquals("Authorization", SaOAuth2Consts.Param.Authorization);
		Assertions.assertEquals("nonce", SaOAuth2Consts.Param.nonce);
	}

	/** response_type 应该就是 code / token */
	@Test
	public void responseType_keepLiteralValues() {
		Assertions.assertEquals("code", SaOAuth2Consts.ResponseType.code);
		Assertions.assertEquals("token", SaOAuth2Consts.ResponseType.token);
	}

	/** token 类型大小写两套都应该在 */
	@Test
	public void tokenType_keepLiteralValues() {
		Assertions.assertEquals("basic", SaOAuth2Consts.TokenType.basic);
		Assertions.assertEquals("digest", SaOAuth2Consts.TokenType.digest);
		Assertions.assertEquals("bearer", SaOAuth2Consts.TokenType.bearer);
		Assertions.assertEquals("Basic", SaOAuth2Consts.TokenType.Basic);
		Assertions.assertEquals("Digest", SaOAuth2Consts.TokenType.Digest);
		Assertions.assertEquals("Bearer", SaOAuth2Consts.TokenType.Bearer);
	}

	/** 扩展字段名应该就是 openid / unionid / userid / id_token */
	@Test
	public void extraField_keepLiteralValues() {
		Assertions.assertEquals("unionid", SaOAuth2Consts.ExtraField.unionid);
		Assertions.assertEquals("openid", SaOAuth2Consts.ExtraField.openid);
		Assertions.assertEquals("userid", SaOAuth2Consts.ExtraField.userid);
		Assertions.assertEquals("id_token", SaOAuth2Consts.ExtraField.id_token);
	}

	/** 摘要前缀、OK、NOT_HANDLE、最终权限标识应该都是固定串 */
	@Test
	public void topLevelConsts_keepLiteralValues() {
		Assertions.assertEquals("openid_default_digest_prefix", SaOAuth2Consts.OPENID_DEFAULT_DIGEST_PREFIX);
		Assertions.assertEquals("unionid_default_digest_prefix", SaOAuth2Consts.UNIONID_DEFAULT_DIGEST_PREFIX);
		Assertions.assertEquals("ok", SaOAuth2Consts.OK);
		Assertions.assertEquals("{\"msg\": \"not handle\"}", SaOAuth2Consts.NOT_HANDLE);
		Assertions.assertEquals("_FINALLY_WORK_SCOPE", SaOAuth2Consts._FINALLY_WORK_SCOPE);
	}

	/** GrantType 五个授权模式名字应该对得上 */
	@Test
	public void grantType_keepLiteralValues() {
		Assertions.assertEquals("authorization_code", GrantType.authorization_code);
		Assertions.assertEquals("refresh_token", GrantType.refresh_token);
		Assertions.assertEquals("password", GrantType.password);
		Assertions.assertEquals("client_credentials", GrantType.client_credentials);
		Assertions.assertEquals("implicit", GrantType.implicit);
	}

}

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
package cn.dev33.satoken.oauth2.strategy;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.granttype.handler.SaOAuth2GrantTypeHandlerInterface;
import cn.dev33.satoken.oauth2.scope.CommonScope;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import cn.dev33.satoken.oauth2.support.OAuth2TestSupport;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.oauth2.support.OAuth2Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OAuth2 策略：scope / grantType 处理器、凭证创建、默认字段
 */
@OAuth2Test
public class SaOAuth2StrategyTest {

	/** 每个用例前把单例策略恢复成默认实现 */
	@BeforeEach
	public void reset() {
		OAuth2TestSupport.installDefaultConfig();
	}

	/** 注册再移除 scope 处理器后 map 里应该没有它 */
	@Test
	public void registerRemoveScopeHandler() {
		SaOAuth2ScopeHandlerInterface custom = new SimpleScopeHandler("custom-scope");
		SaOAuth2Strategy.instance.registerScopeHandler(custom);
		Assertions.assertSame(custom, SaOAuth2Strategy.instance.scopeHandlerMap.get("custom-scope"));
		SaOAuth2Strategy.instance.removeScopeHandler("custom-scope");
		Assertions.assertNull(SaOAuth2Strategy.instance.scopeHandlerMap.get("custom-scope"));
	}

	/** 注册再移除 grant 处理器后 map 里应该没有它 */
	@Test
	public void registerRemoveGrantTypeHandler() {
		SaOAuth2GrantTypeHandlerInterface named = new SaOAuth2GrantTypeHandlerInterface() {
			@Override
			public String getHandlerGrantType() {
				return "custom_grant";
			}
			@Override
			public AccessTokenModel getAccessToken(cn.dev33.satoken.context.model.SaRequest req, String clientId,
					java.util.List<String> scopes) {
				return new AccessTokenModel();
			}
		};
		SaOAuth2Strategy.instance.registerGrantTypeHandler(named);
		Assertions.assertNotNull(SaOAuth2Strategy.instance.grantTypeHandlerMap.get("custom_grant"));
		SaOAuth2Strategy.instance.removeGrantTypeHandler("custom_grant");
		Assertions.assertNull(SaOAuth2Strategy.instance.grantTypeHandlerMap.get("custom_grant"));
	}

	/** workAccessTokenByScope 遇到 openid/userid/unionid 应该往 extraData 里塞字段 */
	@Test
	public void workAccessTokenByScope_openidUseridUnionid() {
		AccessTokenModel at = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
				Arrays.asList(CommonScope.OPENID, CommonScope.USERID, CommonScope.UNIONID));
		at.extraData = new LinkedHashMap<>();
		SaOAuth2Strategy.instance.workAccessTokenByScope.accept(at);
		Assertions.assertNotNull(at.extraData.get(SaOAuth2Consts.ExtraField.openid));
		Assertions.assertEquals(OAuth2TestSupport.LOGIN_ID, at.extraData.get(SaOAuth2Consts.ExtraField.userid));
		Assertions.assertNotNull(at.extraData.get(SaOAuth2Consts.ExtraField.unionid));
	}

	/** 没对应处理器的 scope 应该被跳过，空 scopes 也不该炸 */
	@Test
	public void workAccessTokenByScope_unknownAndEmpty() {
		AccessTokenModel empty = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
				Collections.emptyList());
		empty.extraData = new LinkedHashMap<>();
		SaOAuth2Strategy.instance.workAccessTokenByScope.accept(empty);

		AccessTokenModel unknown = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
				Collections.singletonList("userinfo"));
		unknown.extraData = new LinkedHashMap<>();
		SaOAuth2Strategy.instance.workAccessTokenByScope.accept(unknown);
		Assertions.assertTrue(unknown.extraData.isEmpty());
	}

	/** _FINALLY_WORK_SCOPE 处理器在所有 scope 之后都该跑一遍 */
	@Test
	public void workAccessTokenByScope_finallyWorkScope() {
		AtomicInteger calls = new AtomicInteger();
		SaOAuth2Strategy.instance.registerScopeHandler(new SaOAuth2ScopeHandlerInterface() {
			@Override
			public String getHandlerScope() {
				return SaOAuth2Consts._FINALLY_WORK_SCOPE;
			}
			@Override
			public void workAccessToken(AccessTokenModel at) {
				calls.incrementAndGet();
				at.extraData.put("finally", true);
			}
			@Override
			public void workClientToken(ClientTokenModel ct) {
			}
		});
		AccessTokenModel at = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
				Collections.singletonList(CommonScope.USERID));
		at.extraData = new LinkedHashMap<>();
		SaOAuth2Strategy.instance.workAccessTokenByScope.accept(at);
		Assertions.assertEquals(1, calls.get());
		Assertions.assertEquals(true, at.extraData.get("finally"));
	}

	/** 刷新时 openid 默认不重跑，oidc 会重跑 */
	@Test
	public void refreshAccessTokenWorkByScope() {
		Assertions.assertFalse(SaOAuth2Strategy.instance.scopeHandlerMap.get(CommonScope.OPENID).refreshAccessTokenIsWork());
		Assertions.assertTrue(SaOAuth2Strategy.instance.scopeHandlerMap.get(CommonScope.OIDC).refreshAccessTokenIsWork());

		AccessTokenModel openidAt = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
				Collections.singletonList(CommonScope.OPENID));
		openidAt.extraData = new LinkedHashMap<>();
		SaOAuth2Strategy.instance.refreshAccessTokenWorkByScope.accept(openidAt);
		Assertions.assertNull(openidAt.extraData.get(SaOAuth2Consts.ExtraField.openid));

		OAuth2TestSupport.withRequest("/oauth2/token", OAuth2TestSupport.params(
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET,
				"nonce", "n-" + System.nanoTime()), () -> {
			StpUtil.login(OAuth2TestSupport.LOGIN_ID);
			AccessTokenModel oidcAt = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
					Collections.singletonList(CommonScope.OIDC));
			oidcAt.extraData = new LinkedHashMap<>();
			SaOAuth2Strategy.instance.refreshAccessTokenWorkByScope.accept(oidcAt);
			Assertions.assertNotNull(oidcAt.extraData.get("id_token"));
			return null;
		});
	}

	/** 刷新时 finally 处理器 refreshAccessTokenIsWork=false 就不该跑 */
	@Test
	public void refreshAccessTokenWorkByScope_finallySkipWhenFalse() {
		AtomicBoolean ran = new AtomicBoolean();
		SaOAuth2Strategy.instance.registerScopeHandler(new SaOAuth2ScopeHandlerInterface() {
			@Override
			public String getHandlerScope() {
				return SaOAuth2Consts._FINALLY_WORK_SCOPE;
			}
			@Override
			public void workAccessToken(AccessTokenModel at) {
				ran.set(true);
			}
			@Override
			public void workClientToken(ClientTokenModel ct) {
			}
		});
		AccessTokenModel at = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
				Collections.singletonList(CommonScope.OPENID));
		at.extraData = new LinkedHashMap<>();
		SaOAuth2Strategy.instance.refreshAccessTokenWorkByScope.accept(at);
		Assertions.assertFalse(ran.get());
	}

	/** 刷新时 finally 处理器 refreshAccessTokenIsWork=true 就该跑 */
	@Test
	public void refreshAccessTokenWorkByScope_finallyWhenTrue() {
		AtomicBoolean ran = new AtomicBoolean();
		SaOAuth2Strategy.instance.registerScopeHandler(new SaOAuth2ScopeHandlerInterface() {
			@Override
			public String getHandlerScope() {
				return SaOAuth2Consts._FINALLY_WORK_SCOPE;
			}
			@Override
			public void workAccessToken(AccessTokenModel at) {
				ran.set(true);
			}
			@Override
			public void workClientToken(ClientTokenModel ct) {
			}
			@Override
			public boolean refreshAccessTokenIsWork() {
				return true;
			}
		});
		AccessTokenModel at = new AccessTokenModel("at", OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID,
				Collections.emptyList());
		at.extraData = new LinkedHashMap<>();
		SaOAuth2Strategy.instance.refreshAccessTokenWorkByScope.accept(at);
		Assertions.assertTrue(ran.get());
	}

	/** workClientTokenByScope 应该调到对应处理器和 finally */
	@Test
	public void workClientTokenByScope() {
		AtomicBoolean customRan = new AtomicBoolean();
		AtomicBoolean finallyRan = new AtomicBoolean();
		SaOAuth2Strategy.instance.registerScopeHandler(new SaOAuth2ScopeHandlerInterface() {
			@Override
			public String getHandlerScope() {
				return "userinfo";
			}
			@Override
			public void workAccessToken(AccessTokenModel at) {
			}
			@Override
			public void workClientToken(ClientTokenModel ct) {
				customRan.set(true);
				ct.extraData.put("from-userinfo", true);
			}
		});
		SaOAuth2Strategy.instance.registerScopeHandler(new SaOAuth2ScopeHandlerInterface() {
			@Override
			public String getHandlerScope() {
				return SaOAuth2Consts._FINALLY_WORK_SCOPE;
			}
			@Override
			public void workAccessToken(AccessTokenModel at) {
			}
			@Override
			public void workClientToken(ClientTokenModel ct) {
				finallyRan.set(true);
			}
		});
		ClientTokenModel ct = new ClientTokenModel("ct", OAuth2TestSupport.CLIENT_ID, Collections.singletonList("userinfo"));
		ct.extraData = new LinkedHashMap<>();
		SaOAuth2Strategy.instance.workClientTokenByScope.accept(ct);
		Assertions.assertTrue(customRan.get());
		Assertions.assertTrue(finallyRan.get());

		ClientTokenModel empty = new ClientTokenModel("ct", OAuth2TestSupport.CLIENT_ID, Collections.emptyList());
		empty.extraData = new LinkedHashMap<>();
		SaOAuth2Strategy.instance.workClientTokenByScope.accept(empty);

		ClientTokenModel unknown = new ClientTokenModel("ct", OAuth2TestSupport.CLIENT_ID, Collections.singletonList("nope"));
		unknown.extraData = new LinkedHashMap<>();
		SaOAuth2Strategy.instance.workClientTokenByScope.accept(unknown);
	}

	/** grantTypeAuth：无效类型 30126，系统未开密码式 30126，应用未开 30141，自定义类型能过 */
	@Test
	public void grantTypeAuth() {
		SaOAuth2Exception invalid = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"grant_type", "nope",
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET),
						() -> SaOAuth2Strategy.instance.grantTypeAuth.apply(SaHolder.getRequest())));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30126, invalid.getCode());

		SaOAuth2Manager.getServerConfig().setEnablePassword(false);
		SaOAuth2Exception disabled = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"grant_type", GrantType.password,
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET),
						() -> SaOAuth2Strategy.instance.grantTypeAuth.apply(SaHolder.getRequest())));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30126, disabled.getCode());

		SaOAuth2Manager.getServerConfig().setEnablePassword(true);
		SaOAuth2Manager.getServerConfig().getClients().get(OAuth2TestSupport.CLIENT_ID).getAllowGrantTypes()
				.remove(GrantType.password);
		SaOAuth2Exception clientOff = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"grant_type", GrantType.password,
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET),
						() -> SaOAuth2Strategy.instance.grantTypeAuth.apply(SaHolder.getRequest())));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30141, clientOff.getCode());

		SaOAuth2Manager.getServerConfig().setEnableAuthorizationCode(false);
		SaOAuth2Exception codeOff = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
						"grant_type", GrantType.authorization_code,
						"client_id", OAuth2TestSupport.CLIENT_ID,
						"client_secret", OAuth2TestSupport.CLIENT_SECRET),
						() -> SaOAuth2Strategy.instance.grantTypeAuth.apply(SaHolder.getRequest())));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30126, codeOff.getCode());

		OAuth2TestSupport.installDefaultConfig();
		SaOAuth2Strategy.instance.registerGrantTypeHandler(new SaOAuth2GrantTypeHandlerInterface() {
			@Override
			public String getHandlerGrantType() {
				return "custom_grant";
			}
			@Override
			public AccessTokenModel getAccessToken(cn.dev33.satoken.context.model.SaRequest req, String clientId,
					java.util.List<String> scopes) {
				AccessTokenModel at = new AccessTokenModel("custom-at", clientId, OAuth2TestSupport.LOGIN_ID, scopes);
				at.extraData = new LinkedHashMap<>();
				return at;
			}
		});
		SaOAuth2Manager.getServerConfig().getClients().get(OAuth2TestSupport.CLIENT_ID)
				.addAllowGrantTypes("custom_grant");
		AccessTokenModel custom = OAuth2TestSupport.withPost("/oauth2/token", OAuth2TestSupport.params(
				"grant_type", "custom_grant",
				"client_id", OAuth2TestSupport.CLIENT_ID,
				"client_secret", OAuth2TestSupport.CLIENT_SECRET),
				() -> SaOAuth2Strategy.instance.grantTypeAuth.apply(SaHolder.getRequest()));
		Assertions.assertEquals("custom-at", custom.accessToken);
	}

	/** 默认 create*Value 应该能吐出非空随机串 */
	@Test
	public void createValueFunctions() {
		Assertions.assertEquals(60, SaOAuth2Strategy.instance.createCodeValue.execute(
				OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, Collections.emptyList()).length());
		Assertions.assertEquals(60, SaOAuth2Strategy.instance.createAccessToken.execute(
				OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, Collections.emptyList()).length());
		Assertions.assertEquals(60, SaOAuth2Strategy.instance.createRefreshToken.execute(
				OAuth2TestSupport.CLIENT_ID, OAuth2TestSupport.LOGIN_ID, Collections.emptyList()).length());
		Assertions.assertEquals(60, SaOAuth2Strategy.instance.createClientToken.execute(
				OAuth2TestSupport.CLIENT_ID, Collections.emptyList()).length());
	}

	/** setSaClientModelDefaultFields 应该把全局超时抄到 client 上 */
	@Test
	public void setSaClientModelDefaultFields() {
		SaOAuth2Manager.getServerConfig().setAccessTokenTimeout(11);
		SaOAuth2Manager.getServerConfig().setRefreshTokenTimeout(22);
		SaOAuth2Manager.getServerConfig().setClientTokenTimeout(33);
		SaOAuth2Manager.getServerConfig().setMaxAccessTokenCount(1);
		SaOAuth2Manager.getServerConfig().setMaxRefreshTokenCount(2);
		SaOAuth2Manager.getServerConfig().setMaxClientTokenCount(3);
		SaOAuth2Manager.getServerConfig().setIsNewRefresh(true);
		SaClientModel cm = new SaClientModel();
		Assertions.assertEquals(11, cm.accessTokenTimeout);
		Assertions.assertEquals(22, cm.refreshTokenTimeout);
		Assertions.assertEquals(33, cm.clientTokenTimeout);
		Assertions.assertEquals(1, cm.maxAccessTokenCount);
		Assertions.assertEquals(2, cm.maxRefreshTokenCount);
		Assertions.assertEquals(3, cm.maxClientTokenCount);
		Assertions.assertEquals(true, cm.isNewRefresh);
	}

	/** userAuthorizeClientCheck 默认是空实现，换成抛异常后生成 AT 就该炸 */
	@Test
	public void userAuthorizeClientCheck() {
		Assertions.assertDoesNotThrow(() ->
				SaOAuth2Strategy.instance.userAuthorizeClientCheck.run(OAuth2TestSupport.LOGIN_ID, OAuth2TestSupport.CLIENT_ID));
		SaOAuth2Strategy.instance.userAuthorizeClientCheck = (loginId, clientId) -> {
			throw new SaOAuth2Exception("blocked").setCode(SaOAuth2ErrorCode.CODE_30191);
		};
		SaOAuth2Exception ex = Assertions.assertThrows(SaOAuth2Exception.class, () ->
				SaOAuth2Strategy.instance.userAuthorizeClientCheck.run(1, "c"));
		Assertions.assertEquals(SaOAuth2ErrorCode.CODE_30191, ex.getCode());
	}

	/** 简单 scope 处理器，方便注册/移除 */
	private static class SimpleScopeHandler implements SaOAuth2ScopeHandlerInterface {
		private final String scope;
		private SimpleScopeHandler(String scope) {
			this.scope = scope;
		}
		@Override
		public String getHandlerScope() {
			return scope;
		}
		@Override
		public void workAccessToken(AccessTokenModel at) {
		}
		@Override
		public void workClientToken(ClientTokenModel ct) {
		}
	}

}

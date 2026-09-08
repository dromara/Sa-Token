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

import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Server 端配置：默认值、getter/setter、clients 为 null 时 addClient、toString
 */
@SaTokenTest
public class SaOAuth2ServerConfigTest {

	/** 默认值应该是授权码/隐藏/密码/凭证全开、超时这一套 */
	@Test
	public void defaults_matchProduction() {
		SaOAuth2ServerConfig cfg = new SaOAuth2ServerConfig();
		Assertions.assertEquals(Boolean.TRUE, cfg.getEnableAuthorizationCode());
		Assertions.assertEquals(Boolean.TRUE, cfg.getEnableImplicit());
		Assertions.assertEquals(Boolean.TRUE, cfg.getEnablePassword());
		Assertions.assertEquals(Boolean.TRUE, cfg.getEnableClientCredentials());
		Assertions.assertEquals(300, cfg.getCodeTimeout());
		Assertions.assertEquals(7200, cfg.getAccessTokenTimeout());
		Assertions.assertEquals(2592000, cfg.getRefreshTokenTimeout());
		Assertions.assertEquals(7200, cfg.getClientTokenTimeout());
		Assertions.assertEquals(12, cfg.getMaxAccessTokenCount());
		Assertions.assertEquals(12, cfg.getMaxRefreshTokenCount());
		Assertions.assertEquals(12, cfg.getMaxClientTokenCount());
		Assertions.assertEquals(Boolean.FALSE, cfg.getIsNewRefresh());
		Assertions.assertEquals(SaOAuth2Consts.OPENID_DEFAULT_DIGEST_PREFIX, cfg.getOpenidDigestPrefix());
		Assertions.assertEquals(SaOAuth2Consts.UNIONID_DEFAULT_DIGEST_PREFIX, cfg.getUnionidDigestPrefix());
		Assertions.assertNull(cfg.getHigherScope());
		Assertions.assertNull(cfg.getLowerScope());
		Assertions.assertEquals(Boolean.FALSE, cfg.getMode4ReturnAccessToken());
		Assertions.assertEquals(Boolean.FALSE, cfg.getHideStatusField());
		Assertions.assertNotNull(cfg.getOidc());
		Assertions.assertNotNull(cfg.getClients());
		Assertions.assertTrue(cfg.getClients().isEmpty());
	}

	/** 各 setter 应该能连缀写回去 */
	@Test
	public void gettersAndSetters_roundTrip() {
		SaOAuth2OidcConfig oidc = new SaOAuth2OidcConfig().setIss("http://iss");
		Map<String, SaClientModel> clients = new LinkedHashMap<>();
		SaClientModel client = new SaClientModel().setClientId("c1");
		clients.put("c1", client);
		SaOAuth2ServerConfig cfg = new SaOAuth2ServerConfig()
				.setEnableAuthorizationCode(false)
				.setEnableImplicit(false)
				.setEnablePassword(false)
				.setEnableClientCredentials(false)
				.setCodeTimeout(10)
				.setAccessTokenTimeout(20)
				.setRefreshTokenTimeout(30)
				.setClientTokenTimeout(40)
				.setMaxAccessTokenCount(1)
				.setMaxRefreshTokenCount(2)
				.setMaxClientTokenCount(3)
				.setIsNewRefresh(true)
				.setOpenidDigestPrefix("op")
				.setUnionidDigestPrefix("un")
				.setHigherScope("admin")
				.setLowerScope("user")
				.setMode4ReturnAccessToken(true)
				.setHideStatusField(true)
				.setOidc(oidc)
				.setClients(clients);
		Assertions.assertEquals(Boolean.FALSE, cfg.getEnableAuthorizationCode());
		Assertions.assertEquals(Boolean.FALSE, cfg.getEnableImplicit());
		Assertions.assertEquals(Boolean.FALSE, cfg.getEnablePassword());
		Assertions.assertEquals(Boolean.FALSE, cfg.getEnableClientCredentials());
		Assertions.assertEquals(10, cfg.getCodeTimeout());
		Assertions.assertEquals(20, cfg.getAccessTokenTimeout());
		Assertions.assertEquals(30, cfg.getRefreshTokenTimeout());
		Assertions.assertEquals(40, cfg.getClientTokenTimeout());
		Assertions.assertEquals(1, cfg.getMaxAccessTokenCount());
		Assertions.assertEquals(2, cfg.getMaxRefreshTokenCount());
		Assertions.assertEquals(3, cfg.getMaxClientTokenCount());
		Assertions.assertEquals(Boolean.TRUE, cfg.getIsNewRefresh());
		Assertions.assertEquals("op", cfg.getOpenidDigestPrefix());
		Assertions.assertEquals("un", cfg.getUnionidDigestPrefix());
		Assertions.assertEquals("admin", cfg.getHigherScope());
		Assertions.assertEquals("user", cfg.getLowerScope());
		Assertions.assertEquals(Boolean.TRUE, cfg.getMode4ReturnAccessToken());
		Assertions.assertEquals(Boolean.TRUE, cfg.getHideStatusField());
		Assertions.assertSame(oidc, cfg.getOidc());
		Assertions.assertSame(clients, cfg.getClients());
	}

	/** clients 被置空后再 addClient，应该自己 new 一个 map 再放进去 */
	@Test
	public void addClient_whenClientsNull_createsMap() {
		SaClientModel client = new SaClientModel().setClientId("app");
		SaOAuth2ServerConfig cfg = new SaOAuth2ServerConfig().setClients(null);
		Assertions.assertNull(cfg.getClients());
		cfg.addClient(client);
		Assertions.assertSame(client, cfg.getClients().get("app"));
	}

	/** addClient 平时应该按 clientId 放进已有 map */
	@Test
	public void addClient_putsByClientId() {
		SaClientModel client = new SaClientModel().setClientId("app");
		SaOAuth2ServerConfig cfg = new SaOAuth2ServerConfig().addClient(client);
		Assertions.assertSame(client, cfg.getClients().get("app"));
	}

	/** toString 里应该能看到主要字段名 */
	@Test
	public void toString_containsFieldNames() {
		String text = new SaOAuth2ServerConfig().toString();
		Assertions.assertTrue(text.startsWith("SaOAuth2ServerConfig {"));
		Assertions.assertTrue(text.contains("enableAuthorizationCode="));
		Assertions.assertTrue(text.contains("accessTokenTimeout="));
		Assertions.assertTrue(text.contains("openidDigestPrefix="));
		Assertions.assertTrue(text.contains("oidc="));
		Assertions.assertTrue(text.contains("clients="));
	}

}

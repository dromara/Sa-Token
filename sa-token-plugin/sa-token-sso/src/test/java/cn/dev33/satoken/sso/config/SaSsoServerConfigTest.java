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
package cn.dev33.satoken.sso.config;

import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Server 端配置：getter/setter、setAllow、addClient、非法 allowUrl
 */
public class SaSsoServerConfigTest {

	/** 默认值应该是 ticketTimeout=300、isSlo=true 这一套 */
	@Test
	public void defaults_matchProduction() {
		SaSsoServerConfig cfg = new SaSsoServerConfig();
		Assertions.assertEquals("", cfg.getMode());
		Assertions.assertEquals(300, cfg.getTicketTimeout());
		Assertions.assertNull(cfg.getHomeRoute());
		Assertions.assertEquals(Boolean.TRUE, cfg.getIsSlo());
		Assertions.assertEquals(Boolean.FALSE, cfg.getAutoRenewTimeout());
		Assertions.assertEquals(32, cfg.getMaxRegClient());
		Assertions.assertEquals(Boolean.TRUE, cfg.getIsCheckSign());
		Assertions.assertEquals(Boolean.FALSE, cfg.getAllowAnonClient());
		Assertions.assertEquals("", cfg.getAllowUrl());
		Assertions.assertNull(cfg.getSecretKey());
		Assertions.assertNotNull(cfg.getClients());
		Assertions.assertTrue(cfg.getClients().isEmpty());
	}

	/** 各 setter 应该能写回去，void 的 setMode 也要点一下 */
	@Test
	public void gettersAndSetters_roundTrip() {
		SaSsoServerConfig cfg = new SaSsoServerConfig();
		cfg.setMode("ticket");
		cfg.setTicketTimeout(10)
				.setHomeRoute("/home")
				.setIsSlo(false)
				.setAutoRenewTimeout(true)
				.setMaxRegClient(2)
				.setIsCheckSign(false)
				.setAllowAnonClient(true)
				.setSecretKey("k")
				.setAllowUrl("http://a.com/*");
		Map<String, SaSsoClientModel> clients = new LinkedHashMap<>();
		SaSsoClientModel client = new SaSsoClientModel().setClient("c1");
		clients.put("c1", client);
		cfg.setClients(clients);
		Assertions.assertEquals("ticket", cfg.getMode());
		Assertions.assertEquals(10, cfg.getTicketTimeout());
		Assertions.assertEquals("/home", cfg.getHomeRoute());
		Assertions.assertEquals(Boolean.FALSE, cfg.getIsSlo());
		Assertions.assertEquals(Boolean.TRUE, cfg.getAutoRenewTimeout());
		Assertions.assertEquals(2, cfg.getMaxRegClient());
		Assertions.assertEquals(Boolean.FALSE, cfg.getIsCheckSign());
		Assertions.assertEquals(Boolean.TRUE, cfg.getAllowAnonClient());
		Assertions.assertEquals("k", cfg.getSecretKey());
		Assertions.assertEquals("http://a.com/*", cfg.getAllowUrl());
		Assertions.assertSame(clients, cfg.getClients());
	}

	/** setAllow 应该把多个地址拼进 allowUrl */
	@Test
	public void setAllow_joinsUrls() {
		SaSsoServerConfig cfg = new SaSsoServerConfig().setAllow("http://a.com/*", "http://b.com/*");
		Assertions.assertTrue(cfg.getAllowUrl().contains("http://a.com/*"));
		Assertions.assertTrue(cfg.getAllowUrl().contains("http://b.com/*"));
	}

	/** addClient 应该按 client 名放进 map */
	@Test
	public void addClient_putsByName() {
		SaSsoClientModel client = new SaSsoClientModel().setClient("app");
		SaSsoServerConfig cfg = new SaSsoServerConfig().addClient(client);
		Assertions.assertSame(client, cfg.getClients().get("app"));
	}

	/** setAllowUrl 空串应该直接记下，不走校验 */
	@Test
	public void setAllowUrl_emptySkipsCheck() {
		Assertions.assertEquals("", new SaSsoServerConfig().setAllowUrl("").getAllowUrl());
	}

	/** allowUrl 中间带 * 应该直接抛 30015 */
	@Test
	public void setAllowUrl_invalidThrows30015() {
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> new SaSsoServerConfig().setAllowUrl("http://*.example.com"));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30015, ex.getCode());
	}

	/** toString 里应该能看到主要配置项 */
	@Test
	public void toString_containsFields() {
		String text = new SaSsoServerConfig().setSecretKey("k").toString();
		Assertions.assertTrue(text.startsWith("SaSsoServerConfig ["));
		Assertions.assertTrue(text.contains("secretKey=k"));
		Assertions.assertTrue(text.contains("ticketTimeout="));
	}

}

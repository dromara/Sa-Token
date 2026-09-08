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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Client 端配置：getter/setter、splicing*、toString
 */
public class SaSsoClientConfigTest {

	/** 默认值应该是 isHttp=false、isSlo=true、各 path 指向 /sso/ */
	@Test
	public void defaults_matchProduction() {
		SaSsoClientConfig cfg = new SaSsoClientConfig();
		Assertions.assertEquals("", cfg.getMode());
		Assertions.assertNull(cfg.getClient());
		Assertions.assertNull(cfg.getServerUrl());
		Assertions.assertEquals("/sso/auth", cfg.getAuthUrl());
		Assertions.assertEquals("/sso/signout", cfg.getSignoutUrl());
		Assertions.assertEquals("/sso/pushS", cfg.getPushUrl());
		Assertions.assertEquals("/sso/getData", cfg.getGetDataUrl());
		Assertions.assertNull(cfg.getCurrSsoLogin());
		Assertions.assertNull(cfg.getCurrSsoLogoutCall());
		Assertions.assertEquals(Boolean.FALSE, cfg.getIsHttp());
		Assertions.assertEquals(Boolean.TRUE, cfg.getIsSlo());
		Assertions.assertEquals(Boolean.FALSE, cfg.getRegLogoutCall());
		Assertions.assertNull(cfg.getSecretKey());
		Assertions.assertEquals(Boolean.TRUE, cfg.getIsCheckSign());
	}

	/** 各 setter 应该能写回去 */
	@Test
	public void gettersAndSetters_roundTrip() {
		SaSsoClientConfig cfg = new SaSsoClientConfig();
		cfg.setMode("3");
		cfg.setClient("c")
				.setServerUrl("http://sso-server.com")
				.setAuthUrl("/a")
				.setSignoutUrl("/s")
				.setPushUrl("/p")
				.setGetDataUrl("/g")
				.setCurrSsoLogin("http://c/login")
				.setCurrSsoLogoutCall("http://c/logoutCall")
				.setIsHttp(true)
				.setIsSlo(false)
				.setRegLogoutCall(true)
				.setSecretKey("k")
				.setIsCheckSign(false);
		Assertions.assertEquals("3", cfg.getMode());
		Assertions.assertEquals("c", cfg.getClient());
		Assertions.assertEquals("http://sso-server.com", cfg.getServerUrl());
		Assertions.assertEquals("/a", cfg.getAuthUrl());
		Assertions.assertEquals("/s", cfg.getSignoutUrl());
		Assertions.assertEquals("/p", cfg.getPushUrl());
		Assertions.assertEquals("/g", cfg.getGetDataUrl());
		Assertions.assertEquals("http://c/login", cfg.getCurrSsoLogin());
		Assertions.assertEquals("http://c/logoutCall", cfg.getCurrSsoLogoutCall());
		Assertions.assertEquals(Boolean.TRUE, cfg.getIsHttp());
		Assertions.assertEquals(Boolean.FALSE, cfg.getIsSlo());
		Assertions.assertEquals(Boolean.TRUE, cfg.getRegLogoutCall());
		Assertions.assertEquals("k", cfg.getSecretKey());
		Assertions.assertEquals(Boolean.FALSE, cfg.getIsCheckSign());
	}

	/** splicing* 应该把 serverUrl 和相对 path 拼成绝对地址 */
	@Test
	public void splicing_joinsServerUrl() {
		SaSsoClientConfig cfg = new SaSsoClientConfig().setServerUrl("http://sso-server.com");
		Assertions.assertEquals("http://sso-server.com/sso/auth", cfg.splicingAuthUrl());
		Assertions.assertEquals("http://sso-server.com/sso/getData", cfg.splicingGetDataUrl());
		Assertions.assertEquals("http://sso-server.com/sso/signout", cfg.splicingSignoutUrl());
		Assertions.assertEquals("http://sso-server.com/sso/pushS", cfg.splicingPushUrl());
	}

	/** toString 里应该能看到 client、serverUrl */
	@Test
	public void toString_containsFields() {
		String text = new SaSsoClientConfig().setClient("c1").toString();
		Assertions.assertTrue(text.startsWith("SaSsoClientConfig ["));
		Assertions.assertTrue(text.contains("client=c1"));
		Assertions.assertTrue(text.contains("isHttp="));
	}

}

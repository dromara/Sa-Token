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
package cn.dev33.satoken.integration.sso.httptest;

import cn.dev33.satoken.integration.sso.IntegrationSsoApplication;
import cn.dev33.satoken.integration.sso.support.SsoHttp;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

/**
 * SSO 真 HTTP：302 + Location，模式一 / 二 / 三跳转和单点注销。
 */
@SpringBootTest(classes = IntegrationSsoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SsoHttpIntegrationTest {

	@LocalServerPort
	int port;

	/** 没登录访问 Client /sso/login 应该 302 去 Server /sso/auth */
	@Test
	public void clientLogin_withoutTicket_redirectsToServerAuth() {
		SsoHttp.Session browser = new SsoHttp.Session();
		SsoHttp.Resp resp = browser.get(port, "/sso-client/sso/login?back=/client/home");
		Assertions.assertTrue(resp.isRedirect());
		Assertions.assertTrue(resp.location.contains("/sso/auth"));
		Assertions.assertTrue(resp.location.contains("redirect="));
		Assertions.assertTrue(resp.location.contains("client=sso-client3"));
	}

	/** Server 没登录访问 /sso/auth 应该 200 返回登录视图，不是 302 */
	@Test
	public void serverAuth_notLogin_returnsView() {
		SsoHttp.Session browser = new SsoHttp.Session();
		SsoHttp.Resp resp = browser.get(port, "/sso/auth?redirect=http://127.0.0.1:" + port + "/sso-client/sso/login");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertEquals("SSO-LOGIN-VIEW", resp.body);
		Assertions.assertNull(resp.location);
	}

	/** 账号密码错了 doLogin 应该失败 JSON */
	@Test
	public void doLogin_badPassword_returnsError() {
		SsoHttp.Session browser = new SsoHttp.Session();
		SsoHttp.Resp resp = browser.get(port, "/sso/doLogin?name=sa&pwd=bad");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertTrue(resp.body.contains("登录失败"));
	}

	/** 模式三：Client 登录 → Server 登录 → 带 ticket 回来 → Client 登录成功 */
	@Test
	public void mode3_fullLogin_ticketInLocationThenClientLoggedIn() {
		SsoHttp.Session browser = new SsoHttp.Session();
		loginMode3(browser);

		SsoHttp.Resp home = browser.get(port, "/client/home");
		Assertions.assertEquals(200, home.status);
		Assertions.assertTrue(home.body.contains("client-login=true"));
		Assertions.assertTrue(home.body.contains("id=10001"));

		SsoHttp.Resp serverHome = browser.get(port, "/server/home");
		Assertions.assertTrue(serverHome.body.contains("server-login=true"));
	}

	/** 模式三登录后再走 /sso/login 没 ticket，已经登录应该直接 302 回 back */
	@Test
	public void mode3_alreadyLogin_redirectsBack() {
		SsoHttp.Session browser = new SsoHttp.Session();
		loginMode3(browser);

		SsoHttp.Resp resp = browser.get(port, "/sso-client/sso/login?back=/client/home");
		Assertions.assertTrue(resp.isRedirect());
		Assertions.assertTrue(resp.location.contains("/client/home"));
		Assertions.assertFalse(resp.location.contains("ticket="));
	}

	/** 模式一：已在 Server 登录后 /sso/auth?mode=simple 应该 302 回 redirect，不带 ticket */
	@Test
	public void mode1_simpleRedirect_noTicket() {
		SsoHttp.Session browser = new SsoHttp.Session();
		Assertions.assertEquals(200, browser.get(port, "/sso/doLogin?name=sa&pwd=123456").status);

		String back = "http://127.0.0.1:" + port + "/client/home";
		SsoHttp.Resp resp = browser.get(port, "/sso/auth?mode=simple&client=sso-client3&redirect=" + back);
		Assertions.assertTrue(resp.isRedirect());
		Assertions.assertTrue(resp.location.contains("/client/home"));
		Assertions.assertFalse(resp.location.contains("ticket="));
	}

	/** 模式二：ticket 会写进共享 Dao；Client HTTP 登录要共用 StpLogic，和本模块拆会话冲突，Dao 校验在这里测 */
	@Test
	public void mode2_ticketWrittenToDao_canCheckAndDelete() {
		boolean backup = SaSsoManager.getClientConfig().getIsHttp();
		SaSsoManager.getClientConfig().setIsHttp(false);
		try {
			SsoHttp.Session browser = new SsoHttp.Session();
			SsoHttp.Resp toAuth = browser.get(port, "/sso-client/sso/login?back=/client/home");
			browser.follow(port, toAuth);
			browser.get(port, "/sso/doLogin?name=sa&pwd=123456");
			SsoHttp.Resp withTicket = browser.follow(port, toAuth);
			Assertions.assertTrue(withTicket.isRedirect());
			Assertions.assertTrue(withTicket.location.contains("ticket="));

			String ticket = extractQuery(withTicket.location, "ticket");
			Assertions.assertNotNull(SaSsoServerProcessor.instance.ssoServerTemplate.checkTicket(ticket));
		} finally {
			SaSsoManager.getClientConfig().setIsHttp(backup);
		}
	}

	/** 非法 redirect 不应该 302 到恶人域名 */
	@Test
	public void illegalRedirect_not302ToEvil() {
		SsoHttp.Session browser = new SsoHttp.Session();
		browser.get(port, "/sso/doLogin?name=sa&pwd=123456");
		SsoHttp.Resp resp = browser.get(port, "/sso/auth?client=sso-client3&redirect=http://evil.com/steal");
		Assertions.assertFalse(resp.isRedirect());
		Assertions.assertTrue(resp.location == null || !resp.location.contains("evil.com"));
		Assertions.assertTrue(resp.body.contains("非法redirect") || resp.body.contains(String.valueOf(SaSsoErrorCode.CODE_30002)));
	}

	/** 带 @ 的 redirect 应该被拒绝 */
	@Test
	public void atInRedirect_rejected() {
		SsoHttp.Session browser = new SsoHttp.Session();
		browser.get(port, "/sso/doLogin?name=sa&pwd=123456");
		SsoHttp.Resp resp = browser.get(port, "/sso/auth?client=sso-client3&redirect=http://127.0.0.1:" + port + "%40evil.com/steal");
		Assertions.assertFalse(resp.isRedirect());
		Assertions.assertTrue(resp.body.contains("无效redirect") || resp.body.contains(String.valueOf(SaSsoErrorCode.CODE_30001)));
	}

	/** 模式三全端注销：Client /sso/logout 之后两端都应该下线 */
	@Test
	public void mode3_slo_bothEndsLoggedOut() {
		SsoHttp.Session browser = new SsoHttp.Session();
		loginMode3(browser);

		SsoHttp.Resp logout = browser.get(port, "/sso-client/sso/logout?back=/client/home");
		Assertions.assertTrue(logout.isRedirect());
		Assertions.assertTrue(logout.location.contains("/client/home"));

		SsoHttp.Resp clientHome = browser.get(port, "/client/home");
		Assertions.assertTrue(clientHome.body.contains("client-login=false"));

		SsoHttp.Resp serverHome = browser.get(port, "/server/home");
		Assertions.assertTrue(serverHome.body.contains("server-login=false"));
	}

	/** 注销不带 back 应该 200 JSON */
	@Test
	public void logout_withoutBack_returnsJson() {
		SsoHttp.Session browser = new SsoHttp.Session();
		SsoHttp.Resp resp = browser.get(port, "/sso-client/sso/logout");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertTrue(resp.body.contains("单点注销成功"));
	}

	/** 注销 back=self 应该回一段 script，Content-Type 是 html */
	@Test
	public void logout_backSelf_returnsScript() {
		SsoHttp.Session browser = new SsoHttp.Session();
		SsoHttp.Resp resp = browser.get(port, "/sso-client/sso/logout?back=self");
		Assertions.assertEquals(200, resp.status);
		Assertions.assertTrue(resp.body.contains("document.referrer"));
	}

	/** 不认识的 path 应该回 not handle */
	@Test
	public void unknownPath_notHandle() {
		SsoHttp.Session browser = new SsoHttp.Session();
		SsoHttp.Resp server = browser.get(port, "/sso/unknown");
		Assertions.assertEquals(200, server.status);
		Assertions.assertEquals(SaSsoConsts.NOT_HANDLE, server.body);

		SsoHttp.Resp client = browser.get(port, "/sso-client/sso/unknown");
		Assertions.assertEquals(200, client.status);
		Assertions.assertEquals(SaSsoConsts.NOT_HANDLE, client.body);
	}

	/** Server 没 redirect 也没要去 Client 时，已登录应该跳 homeRoute */
	@Test
	public void serverAuth_noRedirect_goesHomeRoute() {
		SsoHttp.Session browser = new SsoHttp.Session();
		browser.get(port, "/sso/doLogin?name=sa&pwd=123456");
		SsoHttp.Resp resp = browser.get(port, "/sso/auth");
		Assertions.assertTrue(resp.isRedirect());
		Assertions.assertTrue(resp.location.contains("/server/home"));
	}

	/** 职责：走完模式三登录，停在 Client 已登录 */
	private void loginMode3(SsoHttp.Session browser) {
		SsoHttp.Resp toAuth = browser.get(port, "/sso-client/sso/login?back=/client/home");
		Assertions.assertTrue(toAuth.isRedirect());
		Assertions.assertTrue(toAuth.location.contains("/sso/auth"));

		SsoHttp.Resp view = browser.follow(port, toAuth);
		Assertions.assertEquals("SSO-LOGIN-VIEW", view.body);

		SsoHttp.Resp login = browser.get(port, "/sso/doLogin?name=sa&pwd=123456");
		Assertions.assertEquals(200, login.status);
		Assertions.assertTrue(login.body.contains("登录成功"));

		SsoHttp.Resp withTicket = browser.follow(port, toAuth);
		Assertions.assertTrue(withTicket.isRedirect());
		Assertions.assertTrue(withTicket.location.contains("ticket="));
		Assertions.assertTrue(withTicket.location.contains("/sso-client/sso/login"));

		SsoHttp.Resp back = browser.follow(port, withTicket);
		Assertions.assertTrue(back.isRedirect());
		Assertions.assertTrue(back.location.contains("/client/home"));
	}

	/** 职责：从 Location 里抠指定 query */
	private static String extractQuery(String url, String name) {
		int from = url.indexOf(name + "=");
		if (from < 0) {
			return null;
		}
		from += name.length() + 1;
		int amp = url.indexOf('&', from);
		return amp < 0 ? url.substring(from) : url.substring(from, amp);
	}

	/** 两端 StpLogic 登录类型必须分开，别共用 StpUtil */
	@Test
	public void twoStpLogic_areIsolated() {
		StpLogic server = SaSsoServerProcessor.instance.ssoServerTemplate.getStpLogic();
		StpLogic client = SaSsoClientProcessor.instance.ssoClientTemplate.getStpLogic();
		Assertions.assertNotSame(server, client);
		Assertions.assertEquals("sso-server", server.getLoginType());
		Assertions.assertEquals("sso-client", client.getLoginType());
	}

}

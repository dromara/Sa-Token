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
package cn.dev33.satoken.sso.processor;

import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.model.SaCheckTicketResult;
import cn.dev33.satoken.sso.support.SsoTestSupport;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Client 处理器：/sso/login、logout、pushC、logoutCall、checkTicket
 */
@SaTokenTest
public class SaSsoClientProcessorTest {

	/** 每个用例前换干净 Processor，默认把推送回 200 */
	@BeforeEach
	public void reset() {
		SsoTestSupport.resetProcessors();
		SsoTestSupport.installDefaultConfig();
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
	}

	private String okTicketJson() {
		return "{\"code\":200,\"msg\":\"ok\",\"loginId\":10001,\"tokenValue\":\"tok\",\"deviceId\":\"dev\",\"remainTokenTimeout\":100,\"remainSessionTimeout\":200}";
	}

	/** 不认识的 path 应该回 NOT_HANDLE */
	@Test
	public void dister_unknownPath_notHandle() {
		Object out = SsoTestSupport.withPath("/nope", () -> SaSsoClientProcessor.instance.dister());
		Assertions.assertEquals(SaSsoConsts.NOT_HANDLE, out);
	}

	/** 没 ticket 也没登录时应该跳去服务端 /sso/auth，并带上 redirect= */
	@Test
	public void ssoLogin_noTicketNotLogin_redirectsServerAuth() {
		String to = SsoTestSupport.withPath("/sso/login", () -> {
			SaSsoClientProcessor.instance.dister();
			return SsoTestSupport.redirectTo();
		});
		Assertions.assertTrue(to.contains("/sso/auth"));
		Assertions.assertTrue(to.contains("redirect="));
	}

	/** 没 ticket 但已经登录时应该直接跳 back */
	@Test
	public void ssoLogin_noTicketAlreadyLogin_redirectsBack() {
		String to = SsoTestSupport.withRequest("/sso/login", SsoTestSupport.params("back", "/home"), () -> {
			StpUtil.login(10001);
			SaSsoClientProcessor.instance.dister();
			return SsoTestSupport.redirectTo();
		});
		Assertions.assertEquals("/home", to);
	}

	/** 配了 currSsoLogin 时，跳认证中心的 redirect 应该用这个地址 */
	@Test
	public void ssoLogin_noTicket_usesCurrSsoLogin() {
		SaSsoManager.getClientConfig().setCurrSsoLogin("http://sso-client.com/custom-login");
		String to = SsoTestSupport.withPath("/sso/login", () -> {
			SaSsoClientProcessor.instance.dister();
			return SsoTestSupport.redirectTo();
		});
		Assertions.assertTrue(to.contains("custom-login") || to.contains("custom%2Dlogin") || to.contains("custom-login") || to.contains("custom%2dlogin") || to.contains("http://sso-client.com/custom-login") || to.contains("http%3A%2F%2Fsso-client.com%2Fcustom-login"));
	}

	/** isHttp=true 且 ticket 校验成功时应该本地登录并跳 back */
	@Test
	public void ssoLogin_ticketHttpSuccess_loginAndRedirect() {
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> okTicketJson();
		boolean login = SsoTestSupport.withRequest("/sso/login", SsoTestSupport.params("ticket", "t1", "back", "/home"), () -> {
			SaSsoClientProcessor.instance.dister();
			Assertions.assertEquals("/home", SsoTestSupport.redirectTo());
			return StpUtil.isLogin();
		});
		Assertions.assertTrue(login);
	}

	/** ticket 校验 code!=200 应该抛 30005 */
	@Test
	public void ssoLogin_ticketHttpFail_throws30005() {
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> "{\"code\":500,\"msg\":\"校验失败\"}";
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class, () ->
				SsoTestSupport.withRequest("/sso/login", SsoTestSupport.params("ticket", "bad"),
						() -> SaSsoClientProcessor.instance.dister()));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30005, ex.getCode());
		Assertions.assertEquals("校验失败", ex.getMessage());
	}

	/** 自定义 ticketResultHandle 时应该用它的返回值，不再走默认登录 */
	@Test
	public void ssoLogin_customTicketResultHandle_skipsDefaultLogin() {
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> okTicketJson();
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.ticketResultHandle = (ctr, back) -> "CUSTOM:" + back;
		Object out = SsoTestSupport.withRequest("/sso/login", SsoTestSupport.params("ticket", "t1", "back", "/home"),
				() -> SaSsoClientProcessor.instance.dister());
		Assertions.assertEquals("CUSTOM:/home", out);
		SsoTestSupport.withRequest("/sso/login", SsoTestSupport.params("ticket", "t1", "back", "/home"), () -> {
			Assertions.assertFalse(StpUtil.isLogin());
			return null;
		});
	}

	/** convertCenterIdToLoginId 应该作用在本地登录 id 上 */
	@Test
	public void ssoLogin_convertCenterIdToLoginId() {
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> okTicketJson();
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.convertCenterIdToLoginId = id -> "L-" + id;
		Object loginId = SsoTestSupport.withRequest("/sso/login", SsoTestSupport.params("ticket", "t1", "back", "/"), () -> {
			SaSsoClientProcessor.instance.dister();
			return StpUtil.getLoginId();
		});
		Assertions.assertEquals("L-10001", loginId);
	}

	/** isHttp=false 时应该走 redis 校验 ticket */
	@Test
	public void ssoLogin_isHttpFalse_checkTicketByRedis() {
		SaSsoManager.getClientConfig().setIsHttp(false);
		String ticket = SsoTestSupport.withPath("/tmp", () -> {
			StpUtil.login(10001);
			return SaSsoServerProcessor.instance.ssoServerTemplate.createTicketAndSave(
					SsoTestSupport.CLIENT, 10001, StpUtil.getTokenValue());
		});
		boolean login = SsoTestSupport.withRequest("/sso/login", SsoTestSupport.params("ticket", ticket, "back", "/home"), () -> {
			SaSsoClientProcessor.instance.dister();
			Assertions.assertEquals("/home", SsoTestSupport.redirectTo());
			return StpUtil.isLogin();
		});
		Assertions.assertTrue(login);
	}

	/** ticket 上的 client 对不上时应该抛 30011 */
	@Test
	public void checkTicket_clientMismatch_throws30011() {
		SaSsoManager.getClientConfig().setIsHttp(false);
		String ticket = SaSsoServerProcessor.instance.ssoServerTemplate.createTicketAndSave("other-client", 10001, "tok");
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> SaSsoClientProcessor.instance.checkTicket(ticket));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30011, ex.getCode());
	}

	/** isSlo=false 时 /sso/logout 应该 NOT_HANDLE */
	@Test
	public void ssoLogout_isSloFalse_notHandle() {
		SaSsoManager.getClientConfig().setIsSlo(false);
		Object out = SsoTestSupport.withPath("/sso/logout", () -> SaSsoClientProcessor.instance.dister());
		Assertions.assertEquals(SaSsoConsts.NOT_HANDLE, out);
	}

	/** 没登录就 logout 应该直接回 JSON */
	@Test
	public void ssoLogout_notLogin_returnsOkJson() {
		Object out = SsoTestSupport.withPath("/sso/logout", () -> SaSsoClientProcessor.instance.dister());
		Assertions.assertTrue(out instanceof SaResult);
		Assertions.assertEquals(SaResult.CODE_SUCCESS, ((SaResult) out).getCode());
	}

	/** 已登录且服务端回 200 时应该本地也注销 */
	@Test
	public void ssoLogout_loggedInServerOk_localLogout() {
		boolean stillLogin = SsoTestSupport.withPath("/sso/logout", () -> {
			StpUtil.login(10001);
			SaSsoClientProcessor.instance.dister();
			return StpUtil.isLogin();
		});
		Assertions.assertFalse(stillLogin);
	}

	/** 服务端拒绝注销时应该抛 30006 */
	@Test
	public void ssoLogout_serverReject_throws30006() {
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> "{\"code\":500,\"msg\":\"注销失败\"}";
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class, () ->
				SsoTestSupport.withPath("/sso/logout", () -> {
					StpUtil.login(10001);
					return SaSsoClientProcessor.instance.dister();
				}));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30006, ex.getCode());
		Assertions.assertEquals("注销失败", ex.getMessage());
	}

	/** singleDeviceIdLogout=true 时推给服务端的地址里应该带 deviceId */
	@Test
	public void ssoLogout_singleDeviceIdLogout_putsDeviceId() {
		AtomicReference<String> captured = new AtomicReference<>();
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> {
			captured.set(url);
			return "{\"code\":200,\"msg\":\"ok\"}";
		};
		SsoTestSupport.withRequest("/sso/logout", SsoTestSupport.params("singleDeviceIdLogout", "true"), () -> {
			StpUtil.login(10001, new SaLoginParameter().setDeviceId("dev-9"));
			return SaSsoClientProcessor.instance.dister();
		});
		Assertions.assertNotNull(captured.get());
		Assertions.assertTrue(captured.get().contains("deviceId=dev-9"));
	}

	/** 签过名的 logoutCall 推送应该能把本地会话注销 */
	@Test
	public void ssoPushC_signedLogoutCall_logsOut() {
		SaSsoClientTemplate tpl = SaSsoClientProcessor.instance.ssoClientTemplate;
		Map<String, Object> raw = new LinkedHashMap<>();
		raw.put("msgType", SaSsoConsts.MESSAGE_LOGOUT_CALL);
		raw.put("loginId", "10001");
		boolean stillLogin = SsoTestSupport.withRequest("/sso/pushC", SsoTestSupport.signedByClient(tpl, raw), () -> {
			StpUtil.login("10001");
			SaSsoClientProcessor.instance.dister();
			return StpUtil.isLogin();
		});
		Assertions.assertFalse(stillLogin);
	}

	/** isCheckSign=false 时 pushC 应该走警告再处理 */
	@Test
	public void ssoPushC_isCheckSignFalse_handlesWithoutSign() {
		SaSsoManager.getClientConfig().setIsCheckSign(false);
		boolean stillLogin = SsoTestSupport.withRequest("/sso/pushC", SsoTestSupport.params(
				"msgType", SaSsoConsts.MESSAGE_LOGOUT_CALL,
				"loginId", "10001"
		), () -> {
			StpUtil.login("10001");
			SaSsoClientProcessor.instance.dister();
			return StpUtil.isLogin();
		});
		Assertions.assertFalse(stillLogin);
	}

	/** 注册了 logoutCall 且签过名时应该按 convert 后的 id 注销 */
	@Test
	public void ssoLogoutCall_registeredSigned_convertsAndLogsOut() {
		SaSsoManager.getClientConfig().setRegLogoutCall(true);
		SaSsoClientTemplate tpl = SaSsoClientProcessor.instance.ssoClientTemplate;
		tpl.strategy.convertCenterIdToLoginId = id -> "L-" + id;
		Map<String, Object> raw = new LinkedHashMap<>();
		raw.put("loginId", "10001");
		boolean stillLogin = SsoTestSupport.withRequest("/sso/logoutCall", SsoTestSupport.signedByClient(tpl, raw), () -> {
			StpUtil.login("L-10001");
			SaSsoClientProcessor.instance.dister();
			return StpUtil.isLogin();
		});
		Assertions.assertFalse(stillLogin);
	}

	/** logoutCall 关闭验签时也应该能注销，走警告分支 */
	@Test
	public void ssoLogoutCall_isCheckSignFalse_logsOut() {
		SaSsoManager.getClientConfig().setRegLogoutCall(true).setIsCheckSign(false);
		boolean stillLogin = SsoTestSupport.withRequest("/sso/logoutCall", SsoTestSupport.params("loginId", "10001"), () -> {
			StpUtil.login("10001");
			SaSsoClientProcessor.instance.dister();
			return StpUtil.isLogin();
		});
		Assertions.assertFalse(stillLogin);
	}

	/** 没开 regLogoutCall 时 /sso/logoutCall 应该 NOT_HANDLE */
	@Test
	public void ssoLogoutCall_notRegistered_notHandle() {
		SaSsoManager.getClientConfig().setRegLogoutCall(false);
		Object out = SsoTestSupport.withPath("/sso/logoutCall", () -> SaSsoClientProcessor.instance.dister());
		Assertions.assertEquals(SaSsoConsts.NOT_HANDLE, out);
	}

	/** 配了 currSsoLogoutCall 时，校验 ticket 的请求里应该带上它 */
	@Test
	public void checkTicketHttp_usesCurrSsoLogoutCall() {
		SaSsoManager.getClientConfig().setRegLogoutCall(true);
		SaSsoManager.getClientConfig().setCurrSsoLogoutCall("http://sso-client.com/sso/logoutCall");
		AtomicReference<String> captured = new AtomicReference<>();
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> {
			captured.set(url);
			return okTicketJson();
		};
		SsoTestSupport.withPath("/sso/login", () -> SaSsoClientProcessor.instance.checkTicket("t1", "/sso/login"));
		Assertions.assertTrue(captured.get().contains("ssoLogoutCall="));
		Assertions.assertTrue(captured.get().contains("logoutCall"));
	}

	/** currUri 有值时应该用当前 url 把 path 换成 logoutCall */
	@Test
	public void checkTicketHttp_replacesCurrUriWithLogoutCall() {
		SaSsoManager.getClientConfig().setRegLogoutCall(true);
		SaSsoManager.getClientConfig().setCurrSsoLogoutCall("");
		AtomicReference<String> captured = new AtomicReference<>();
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> {
			captured.set(url);
			return okTicketJson();
		};
		SsoTestSupport.withPath("/sso/login", () -> SaSsoClientProcessor.instance.checkTicket("t1", "/sso/login"));
		Assertions.assertTrue(captured.get().contains("ssoLogoutCall="));
		Assertions.assertTrue(captured.get().contains("/sso/logoutCall") || captured.get().contains("%2Fsso%2FlogoutCall"));
	}

	/** regLogoutCall=true 但 currUri 和 currSsoLogoutCall 都空时，走 else 不注册回调 */
	@Test
	public void checkTicketHttp_regLogoutCallButNoUrl_elseBranch() {
		SaSsoManager.getClientConfig().setRegLogoutCall(true);
		SaSsoManager.getClientConfig().setCurrSsoLogoutCall("");
		AtomicReference<String> captured = new AtomicReference<>();
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> {
			captured.set(url);
			return okTicketJson();
		};
		SaCheckTicketResult ctr = SsoTestSupport.withPath("/sso/login",
				() -> SaSsoClientProcessor.instance.checkTicket("t1"));
		Assertions.assertEquals(10001, ctr.loginId);
		Assertions.assertEquals(10001, ctr.centerId);
		Assertions.assertFalse(captured.get().contains("http://sso-client.com/sso/logoutCall"));
	}

	/** 校验 ticket 的响应没有 code 时也应该抛 30005 */
	@Test
	public void checkTicketHttp_nullCode_throws30005() {
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> "{\"msg\":\"no-code\"}";
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> SaSsoClientProcessor.instance.checkTicket("t-null"));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30005, ex.getCode());
	}

	/** checkTicket(ticket) 单参重载应该也能校验成功 */
	@Test
	public void checkTicket_singleArgOverload() {
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> okTicketJson();
		SaCheckTicketResult ctr = SaSsoClientProcessor.instance.checkTicket("t-only");
		Assertions.assertEquals(10001, ctr.loginId);
		Assertions.assertEquals("tok", ctr.tokenValue);
		Assertions.assertEquals("dev", ctr.deviceId);
		Assertions.assertEquals(100L, ctr.remainTokenTimeout);
		Assertions.assertEquals(200L, ctr.remainSessionTimeout);
	}

	/** 新 new 一个 Client Processor 也应该能用 */
	@Test
	public void ctor_newInstanceWorks() {
		Assertions.assertNotNull(new SaSsoClientProcessor());
	}

}

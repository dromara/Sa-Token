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
import cn.dev33.satoken.sso.support.SsoTestSupport;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Server 处理器：/sso/auth、doLogin、signout、pushS
 */
@SaTokenTest
public class SaSsoServerProcessorTest {

	/** 每个用例前换干净 Processor，并给推送请求垫一个 200 */
	@BeforeEach
	public void reset() {
		SsoTestSupport.resetProcessors();
		SsoTestSupport.installDefaultConfig();
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
	}

	/** 不认识的 path 应该回 NOT_HANDLE */
	@Test
	public void dister_unknownPath_notHandle() {
		Object out = SsoTestSupport.withPath("/sso/unknown", () -> SaSsoServerProcessor.instance.dister());
		Assertions.assertEquals(SaSsoConsts.NOT_HANDLE, out);
	}

	/** 没登录时 /sso/auth 应该返回登录视图 */
	@Test
	public void ssoAuth_notLogin_returnsView() {
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.notLoginView = () -> "LOGIN-VIEW";
		Object out = SsoTestSupport.withPath("/sso/auth", () -> SaSsoServerProcessor.instance.dister());
		Assertions.assertEquals("LOGIN-VIEW", out);
	}

	/** 已登录但没 redirect 也没 homeRoute 应该抛 30014 */
	@Test
	public void ssoAuth_loggedInEmptyRedirectNoHome_throws30014() {
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class, () ->
				SsoTestSupport.withPath("/sso/auth", () -> {
					StpUtil.login(10001);
					return SaSsoServerProcessor.instance.dister();
				}));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30014, ex.getCode());
	}

	/** 已登录没 redirect 但配了 homeRoute 应该跳到首页 */
	@Test
	public void ssoAuth_loggedInEmptyRedirectHasHome_redirectsHome() {
		SaSsoManager.getServerConfig().setHomeRoute("http://sso-server.com/index");
		String to = SsoTestSupport.withPath("/sso/auth", () -> {
			StpUtil.login(10001);
			SaSsoServerProcessor.instance.dister();
			return SsoTestSupport.redirectTo();
		});
		Assertions.assertEquals("http://sso-server.com/index", to);
	}

	/** mode=simple 应该原样跳回 redirect，不带 ticket */
	@Test
	public void ssoAuth_modeSimple_redirectEqualsRedirect() {
		String redirect = "http://sso-client.com/app";
		String to = SsoTestSupport.withRequest("/sso/auth", SsoTestSupport.params(
				"mode", SaSsoConsts.MODE_SIMPLE,
				"redirect", redirect,
				"client", SsoTestSupport.CLIENT
		), () -> {
			StpUtil.login(10001);
			SaSsoServerProcessor.instance.dister();
			return SsoTestSupport.redirectTo();
		});
		Assertions.assertEquals(redirect, to);
		Assertions.assertFalse(to.contains("ticket="));
	}

	/** 默认 ticket 模式应该在 redirect 上拼 ticket= */
	@Test
	public void ssoAuth_modeTicket_containsTicketAndRedirect() {
		String redirect = "http://sso-client.com/sso/login";
		String to = SsoTestSupport.withRequest("/sso/auth", SsoTestSupport.params(
				"redirect", redirect,
				"client", SsoTestSupport.CLIENT
		), () -> {
			StpUtil.login(10001);
			SaSsoServerProcessor.instance.dister();
			return SsoTestSupport.redirectTo();
		});
		Assertions.assertTrue(to.startsWith(redirect));
		Assertions.assertTrue(to.contains("ticket="));
	}

	/** autoRenewTimeout=true 时也应该能正常跳转 */
	@Test
	public void ssoAuth_autoRenewTimeout_stillRedirects() {
		SaSsoManager.getServerConfig().setAutoRenewTimeout(true);
		String to = SsoTestSupport.withRequest("/sso/auth", SsoTestSupport.params(
				"redirect", "http://sso-client.com/sso/login",
				"client", SsoTestSupport.CLIENT
		), () -> {
			StpUtil.login(10001);
			SaSsoServerProcessor.instance.dister();
			return SsoTestSupport.redirectTo();
		});
		Assertions.assertTrue(to.contains("ticket="));
	}

	/** redirect 整段编码时应该先解码再跳 */
	@Test
	public void ssoAuth_encodedRedirect_decodesThenRedirects() {
		String to = SsoTestSupport.withRequest("/sso/auth", SsoTestSupport.params(
				"redirect", "http%3A%2F%2Fsso-client.com%2Fsso%2Flogin",
				"client", SsoTestSupport.CLIENT
		), () -> {
			StpUtil.login(10001);
			SaSsoServerProcessor.instance.dister();
			return SsoTestSupport.redirectTo();
		});
		Assertions.assertTrue(to.startsWith("http://sso-client.com/sso/login"));
		Assertions.assertTrue(to.contains("ticket="));
	}

	/** /sso/doLogin 应该把 name/pwd 交给 doLoginHandle */
	@Test
	public void ssoDoLogin_usesDoLoginHandle() {
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.doLoginHandle = (name, pwd) -> name + ":" + pwd;
		Object out = SsoTestSupport.withRequest("/sso/doLogin", SsoTestSupport.params("name", "zhang", "pwd", "123"),
				() -> SaSsoServerProcessor.instance.dister());
		Assertions.assertEquals("zhang:123", out);
	}

	/** 没登录就 /sso/signout 应该直接回注销成功 JSON */
	@Test
	public void ssoSignout_notLogin_returnsOkJson() {
		Object out = SsoTestSupport.withPath("/sso/signout", () -> SaSsoServerProcessor.instance.dister());
		Assertions.assertTrue(out instanceof SaResult);
		Assertions.assertEquals(SaResult.CODE_SUCCESS, ((SaResult) out).getCode());
	}

	/** 已登录且 back=url 应该跳到这个 url */
	@Test
	public void ssoSignout_loggedInWithBack_redirects() {
		String to = SsoTestSupport.withRequest("/sso/signout", SsoTestSupport.params("back", "http://app.com/bye"), () -> {
			StpUtil.login(10001);
			SaSsoServerProcessor.instance.dister();
			Assertions.assertFalse(StpUtil.isLogin());
			return SsoTestSupport.redirectTo();
		});
		Assertions.assertEquals("http://app.com/bye", to);
	}

	/** back=self 应该回脚本并带 html Content-Type */
	@Test
	public void ssoSignout_backSelf_returnsScript() {
		Object out = SsoTestSupport.withRequest("/sso/signout", SsoTestSupport.params("back", SaSsoConsts.SELF), () -> {
			StpUtil.login(10001);
			Object r = SaSsoServerProcessor.instance.dister();
			Assertions.assertEquals("text/html; charset=utf-8", SsoTestSupport.header("Content-Type"));
			return r;
		});
		Assertions.assertTrue(String.valueOf(out).contains("document.referrer"));
	}

	/** singleDeviceIdLogout=true 时推送地址里应该带上当前 deviceId */
	@Test
	public void ssoSignout_singleDeviceIdLogout_putsDeviceId() {
		AtomicReference<String> captured = new AtomicReference<>();
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.sendRequest = url -> {
			captured.set(url);
			return "{\"code\":200,\"msg\":\"ok\"}";
		};
		SsoTestSupport.withRequest("/sso/signout", SsoTestSupport.params("singleDeviceIdLogout", "true"), () -> {
			StpUtil.login(10001, new SaLoginParameter().setDeviceId("dev-1"));
			return SaSsoServerProcessor.instance.dister();
		});
		Assertions.assertNotNull(captured.get());
		Assertions.assertTrue(captured.get().contains("deviceId=dev-1"));
	}

	/** pushS 的 client=* 应该回错误 SaResult */
	@Test
	public void ssoPushS_clientWildcard_returnsError() {
		Object out = SsoTestSupport.withRequest("/sso/pushS", SsoTestSupport.params("client", SaSsoConsts.CLIENT_WILDCARD),
				() -> SaSsoServerProcessor.instance.dister());
		Assertions.assertTrue(out instanceof SaResult);
		SaResult r = (SaResult) out;
		Assertions.assertEquals(SaResult.CODE_ERROR, r.getCode());
		Assertions.assertTrue(r.getMsg().contains("无效 client"));
	}

	/** isCheckSign=false 时应该走警告再处理消息 */
	@Test
	public void ssoPushS_isCheckSignFalse_handlesWithoutSign() {
		SaSsoManager.getServerConfig().setIsCheckSign(false);
		SaSsoServerTemplate tpl = SaSsoServerProcessor.instance.ssoServerTemplate;
		SsoTestSupport.withPath("/tmp", () -> {
			StpUtil.login(10001);
			return null;
		});
		String ticket = tpl.createTicketAndSave(SsoTestSupport.CLIENT, 10001, "tok");
		Object out = SsoTestSupport.withRequest("/sso/pushS", SsoTestSupport.params(
				"msgType", SaSsoConsts.MESSAGE_CHECK_TICKET,
				"client", SsoTestSupport.CLIENT,
				"ticket", ticket
		), () -> SaSsoServerProcessor.instance.dister());
		Assertions.assertTrue(out instanceof SaResult);
		Assertions.assertEquals(10001, ((SaResult) out).get("loginId"));
	}

	/** 签过名的 checkTicket 消息应该能换出 loginId */
	@Test
	public void ssoPushS_signedCheckTicket_returnsLoginId() {
		SaSsoServerTemplate tpl = SaSsoServerProcessor.instance.ssoServerTemplate;
		SsoTestSupport.withPath("/tmp", () -> {
			StpUtil.login(10001);
			return null;
		});
		String ticket = tpl.createTicketAndSave(SsoTestSupport.CLIENT, 10001, "tok");
		Map<String, Object> raw = new LinkedHashMap<>();
		raw.put("msgType", SaSsoConsts.MESSAGE_CHECK_TICKET);
		raw.put("client", SsoTestSupport.CLIENT);
		raw.put("ticket", ticket);
		Object out = SsoTestSupport.withRequest("/sso/pushS", SsoTestSupport.signed(tpl, SsoTestSupport.CLIENT, raw),
				() -> SaSsoServerProcessor.instance.dister());
		Assertions.assertTrue(out instanceof SaResult);
		SaResult r = (SaResult) out;
		Assertions.assertEquals(SaResult.CODE_SUCCESS, r.getCode());
		Assertions.assertEquals(10001, r.get("loginId"));
	}

	/** 不认识的 msgType 应该回「未能找到消息处理器」 */
	@Test
	public void ssoPushS_unknownMsgType_returnsError() {
		SaSsoServerTemplate tpl = SaSsoServerProcessor.instance.ssoServerTemplate;
		Map<String, Object> raw = new LinkedHashMap<>();
		raw.put("msgType", "no-such-type");
		raw.put("client", SsoTestSupport.CLIENT);
		Object out = SsoTestSupport.withRequest("/sso/pushS", SsoTestSupport.signed(tpl, SsoTestSupport.CLIENT, raw),
				() -> SaSsoServerProcessor.instance.dister());
		Assertions.assertTrue(out instanceof SaResult);
		Assertions.assertTrue(((SaResult) out).getMsg().contains("未能找到消息处理器"));
	}

	/** 授权成功跳转前应该调到 jumpToRedirectUrlNotice */
	@Test
	public void ssoAuth_invokesJumpToRedirectUrlNotice() {
		AtomicBoolean noticed = new AtomicBoolean(false);
		AtomicReference<String> url = new AtomicReference<>();
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.jumpToRedirectUrlNotice = u -> {
			noticed.set(true);
			url.set(u);
		};
		SsoTestSupport.withRequest("/sso/auth", SsoTestSupport.params(
				"redirect", "http://sso-client.com/sso/login",
				"client", SsoTestSupport.CLIENT
		), () -> {
			StpUtil.login(10001);
			return SaSsoServerProcessor.instance.dister();
		});
		Assertions.assertTrue(noticed.get());
		Assertions.assertTrue(url.get().contains("ticket="));
	}

	/** 新 new 一个 Processor 也应该能分发 */
	@Test
	public void ctor_newInstanceWorks() {
		Assertions.assertNotNull(new SaSsoServerProcessor());
	}

}

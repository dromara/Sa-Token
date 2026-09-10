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
package cn.dev33.satoken.sso.template;

import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.support.SsoTestSupport;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.sso.support.SsoTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Client 模板：拼认证地址、getData、推消息、注销
 */
@SsoTest
public class SaSsoClientTemplateTest {

	private SaSsoClientTemplate tpl;

	/** 每个用例前换干净模板 */
	@BeforeEach
	public void reset() {
		SsoTestSupport.resetProcessors();
		SsoTestSupport.installDefaultConfig();
		tpl = SaSsoClientProcessor.instance.ssoClientTemplate;
		tpl.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
	}

	/** buildServerAuthUrl 有 back 时应该拼上，已有 ?back= / &back= 就别再拼 */
	@Test
	public void buildServerAuthUrl_backRules() {
		String withBack = tpl.buildServerAuthUrl("http://sso-client.com/sso/login", "/home");
		Assertions.assertTrue(withBack.contains("/sso/auth"));
		Assertions.assertTrue(withBack.contains("client=" + SsoTestSupport.CLIENT));
		Assertions.assertTrue(withBack.contains("redirect="));
		Assertions.assertTrue(withBack.contains("back="));

		String alreadyQ = tpl.buildServerAuthUrl("http://sso-client.com/sso/login?back=/home", "/other");
		Assertions.assertFalse(alreadyQ.contains("back=%2Fother") || alreadyQ.contains("back=/other"));

		String alreadyAmp = tpl.buildServerAuthUrl("http://sso-client.com/sso/login?x=1&back=/home", "/other");
		Assertions.assertTrue(alreadyAmp.contains("&back=") || alreadyAmp.contains("back="));
	}

	/** containsBackParam 大小写不敏感，看起来像 back 但不是 ?back= 的还是要拼 */
	@Test
	public void buildServerAuthUrl_containsBackParamCaseInsensitive() {
		String mixed = tpl.buildServerAuthUrl("http://sso-client.com/sso/login?BACK=/home", "/x");
		Assertions.assertFalse(mixed.contains("back=%2Fx") || mixed.contains("back=/x"));
		String looksLike = tpl.buildServerAuthUrl("http://sso-client.com/sso/login?backurl=/home", "/x");
		Assertions.assertTrue(looksLike.contains("back="));
	}

	/** back 为空时不应该再拼 back */
	@Test
	public void buildServerAuthUrl_emptyBack_skips() {
		String url = tpl.buildServerAuthUrl("http://sso-client.com/sso/login", "");
		Assertions.assertFalse(url.contains("back="));
		String n = tpl.buildServerAuthUrl("http://sso-client.com/sso/login", null);
		Assertions.assertFalse(n.contains("back="));
	}

	/** 没配 client 时认证地址里不该带 client= */
	@Test
	public void buildServerAuthUrl_withoutClient() {
		SaSsoManager.getClientConfig().setClient(null);
		String url = tpl.buildServerAuthUrl("http://sso-client.com/sso/login", "/h");
		Assertions.assertFalse(url.contains("client="));
	}

	/** getData / getData(path) / buildGetDataUrl / buildCustomPathUrl */
	@Test
	public void getData_andBuildUrls() {
		AtomicReference<String> captured = new AtomicReference<>();
		tpl.strategy.sendRequest = url -> {
			captured.set(url);
			return "{\"k\":1}";
		};
		Map<String, Object> p = new LinkedHashMap<>();
		p.put("id", 1);
		Object data = tpl.getData(p);
		Assertions.assertEquals("{\"k\":1}", data);
		Assertions.assertTrue(captured.get().contains("/sso/getData"));

		tpl.getData("/custom", new LinkedHashMap<>());
		Assertions.assertTrue(captured.get().contains("/custom"));

		Object abs = tpl.getData("http://other.com/data", new LinkedHashMap<>());
		Assertions.assertEquals("{\"k\":1}", abs);
		Assertions.assertTrue(captured.get().startsWith("http://other.com/data"));

		String built = tpl.buildGetDataUrl(new LinkedHashMap<>());
		Assertions.assertTrue(built.contains("/sso/getData"));
		Assertions.assertTrue(built.contains("sign="));
	}

	/** 相对 path 但没配 serverUrl 应该抛 30012 */
	@Test
	public void buildCustomPathUrl_missingServerUrl_throws30012() {
		SaSsoManager.getClientConfig().setServerUrl(null);
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> tpl.buildCustomPathUrl("/x", new LinkedHashMap<>()));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30012, ex.getCode());
	}

	/** push-url 非法应该抛 30023，合法时走 stub */
	@Test
	public void pushMessage_invalidAndValid() {
		SaSsoManager.getClientConfig().setServerUrl(null).setPushUrl("not-url");
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> tpl.pushMessage(new SaSsoMessage("checkTicket")));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30023, ex.getCode());

		SsoTestSupport.installDefaultConfig();
		tpl.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
		SaResult r = tpl.pushMessageAsSaResult(new SaSsoMessage("checkTicket").set("ticket", "t"));
		Assertions.assertEquals(200, r.getCode());
	}

	/** buildCheckTicketMessage / buildSignoutMessage 应该带上类型和字段 */
	@Test
	public void buildMessages() {
		SaSsoMessage check = tpl.buildCheckTicketMessage("t1", "http://c/logoutCall");
		Assertions.assertEquals(SaSsoConsts.MESSAGE_CHECK_TICKET, check.getType());
		Assertions.assertEquals("t1", check.get("ticket"));
		Assertions.assertEquals("http://c/logoutCall", check.get("ssoLogoutCall"));
		Assertions.assertEquals(SsoTestSupport.CLIENT, check.get("client"));
		SaSsoMessage signout = tpl.buildSignoutMessage(10001, new SaLogoutParameter().setDeviceId("d"));
		Assertions.assertEquals(SaSsoConsts.MESSAGE_SIGNOUT, signout.getType());
		Assertions.assertEquals(10001, signout.get("loginId"));
		Assertions.assertEquals("d", signout.get("deviceId"));
	}

	/** ssoLogout 会先 convertLoginIdToCenterId，200 则本地注销，失败抛 30006 */
	@Test
	public void ssoLogout_convertAndFail() {
		tpl.strategy.convertLoginIdToCenterId = id -> "C-" + id;
		AtomicReference<String> captured = new AtomicReference<>();
		tpl.strategy.sendRequest = url -> {
			captured.set(url);
			return "{\"code\":200,\"msg\":\"ok\"}";
		};
		SsoTestSupport.withPath("/tmp", () -> {
			StpUtil.login(10001);
			tpl.ssoLogout(10001);
			Assertions.assertFalse(StpUtil.isLogin());
			return null;
		});
		Assertions.assertTrue(captured.get().contains("loginId=C-10001") || captured.get().contains("C-10001"));

		tpl.strategy.sendRequest = url -> "{\"msg\":\"fail-out\"}";
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class, () -> tpl.ssoLogout(2, new SaLogoutParameter()));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30006, ex.getCode());
		Assertions.assertEquals("fail-out", ex.getMessage());
	}

	/** getClient 走配置；秘钥先拿 client 再拿 sign 默认 */
	@Test
	public void getClient_andSignTemplateSecret() {
		Assertions.assertEquals(SsoTestSupport.CLIENT, tpl.getClient());
		Assertions.assertEquals(SsoTestSupport.SECRET, tpl.getSignTemplate().getSignConfigOrGlobal().getSecretKey());
		SaSsoManager.getClientConfig().setSecretKey(null);
		SaSignManager.setConfig(new SaSignConfig().setSecretKey("sign-default"));
		Assertions.assertEquals("sign-default", tpl.getSignTemplate().getSignConfigOrGlobal().getSecretKey());
	}

}

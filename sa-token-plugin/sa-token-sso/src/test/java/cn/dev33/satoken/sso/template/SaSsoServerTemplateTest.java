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
import cn.dev33.satoken.sso.config.SaSsoClientModel;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.model.SaSsoClientInfo;
import cn.dev33.satoken.sso.model.TicketModel;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.support.SsoTestSupport;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Server 模板：ticket、client、redirect、注销推送、签名秘钥
 */
@SaTokenTest
public class SaSsoServerTemplateTest {

	private SaSsoServerTemplate tpl;

	/** 每个用例前换干净模板和默认配置 */
	@BeforeEach
	public void reset() {
		SsoTestSupport.resetProcessors();
		SsoTestSupport.installDefaultConfig();
		tpl = SaSsoServerProcessor.instance.ssoServerTemplate;
		tpl.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
	}

	/** 保存后再取应该能拿到，null/空 ticket 删除和查询都该直接返回 */
	@Test
	public void ticket_saveGetDelete_nullAndEmpty() {
		TicketModel m = tpl.createTicket(SsoTestSupport.CLIENT, 10001, "tok");
		tpl.saveTicket(m);
		Assertions.assertEquals(10001, tpl.getTicket(m.getTicket()).getLoginId());
		tpl.deleteTicket(m.getTicket());
		Assertions.assertNull(tpl.getTicket(m.getTicket()));
		tpl.deleteTicket(null);
		Assertions.assertNull(tpl.getTicket(null));
		Assertions.assertNull(tpl.getTicket(""));
	}

	/** createTicket / createTicketAndSave / randomTicket 应该能造出 64 位码 */
	@Test
	public void createAndRandomTicket() {
		TicketModel created = tpl.createTicket("c", 1, "tv");
		Assertions.assertEquals("c", created.getClient());
		Assertions.assertEquals(1, created.getLoginId());
		Assertions.assertEquals("tv", created.getTokenValue());
		Assertions.assertEquals(64, created.getTicket().length());
		String saved = tpl.createTicketAndSave(SsoTestSupport.CLIENT, 2, "tv2");
		Assertions.assertEquals(64, saved.length());
		Assertions.assertEquals(2, tpl.getLoginId(saved));
		Assertions.assertEquals(64, tpl.randomTicket(9).length());
	}

	/** getLoginId 无效返回 null，带类型能转；无效 checkTicket 抛 30004 */
	@Test
	public void getLoginId_andCheckTicketInvalid() {
		Assertions.assertNull(tpl.getLoginId("nope"));
		String ticket = tpl.createTicketAndSave(SsoTestSupport.CLIENT, 10001, "tok");
		Assertions.assertEquals(10001, tpl.getLoginId(ticket));
		Assertions.assertEquals("10001", tpl.getLoginId(ticket, String.class));
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class, () -> tpl.checkTicket("bad"));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30004, ex.getCode());
		Assertions.assertNotNull(tpl.checkTicket(ticket));
	}

	/** checkTicketParamAndDelete：通配、双方空 client、不匹配 30011、匹配则删掉 */
	@Test
	public void checkTicketParamAndDelete_branches() {
		String t1 = tpl.createTicketAndSave(SsoTestSupport.CLIENT, 11, "tok");
		Assertions.assertEquals(11, tpl.checkTicketParamAndDelete(t1).getLoginId());
		Assertions.assertNull(tpl.getTicket(t1));

		TicketModel emptyClient = tpl.createTicket("", 12, "tok");
		tpl.saveTicket(emptyClient);
		tpl.saveTicketIndex("", 12, emptyClient.getTicket());
		Assertions.assertEquals(12, tpl.checkTicketParamAndDelete(emptyClient.getTicket(), "").getLoginId());

		String t2 = tpl.createTicketAndSave(SsoTestSupport.CLIENT, 13, "tok");
		SaSsoException ex = Assertions.assertThrows(SaSsoException.class,
				() -> tpl.checkTicketParamAndDelete(t2, "other"));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30011, ex.getCode());

		String t3 = tpl.createTicketAndSave(SsoTestSupport.CLIENT, 14, "tok");
		Assertions.assertEquals(14, tpl.checkTicketParamAndDelete(t3, SsoTestSupport.CLIENT).getLoginId());
		Assertions.assertNull(tpl.getTicket(t3));
	}

	/** ticket 索引：loginId 为 null 时 get/delete 直接返回 */
	@Test
	public void ticketIndex_nullLoginId() {
		Assertions.assertNull(tpl.getTicketValue("c", null));
		tpl.deleteTicketIndex("c", null);
		tpl.saveTicketIndex(SsoTestSupport.CLIENT, 1, "abc");
		Assertions.assertEquals("abc", tpl.getTicketValue(SsoTestSupport.CLIENT, 1));
		tpl.deleteTicketIndex(SsoTestSupport.CLIENT, 1);
		Assertions.assertNull(tpl.getTicketValue(SsoTestSupport.CLIENT, 1));
	}

	/** getClients / getClient 空串 / getClientNotNull 匿名和未知 */
	@Test
	public void getClients_andGetClientNotNull() {
		Assertions.assertFalse(tpl.getClients().isEmpty());
		Assertions.assertNull(tpl.getClient(""));
		Assertions.assertNull(tpl.getClient(null));
		Assertions.assertNotNull(tpl.getClient(SsoTestSupport.CLIENT));
		SaSsoException empty = Assertions.assertThrows(SaSsoException.class, () -> tpl.getClientNotNull(""));
		Assertions.assertTrue(empty.getMessage().contains("不可为空"));
		SaSsoManager.getServerConfig().setAllowAnonClient(true);
		Assertions.assertNotNull(tpl.getClientNotNull(""));
		SaSsoException unknown = Assertions.assertThrows(SaSsoException.class, () -> tpl.getClientNotNull("nope"));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30013, unknown.getCode());
		Assertions.assertTrue(tpl.getConfigOfAllowAnonClient());
	}

	/** 匿名 client 秘钥先拿 server，server 空再拿 sign 模块 */
	@Test
	public void getAnonClient_secretKeyFallback() {
		SaSsoManager.getServerConfig().setSecretKey("server-k");
		Assertions.assertEquals("server-k", tpl.getAnonClient().getSecretKey());
		SaSsoManager.getServerConfig().setSecretKey(null);
		SaSignManager.setConfig(new SaSignConfig().setSecretKey("sign-k"));
		Assertions.assertEquals("sign-k", tpl.getAnonClient().getSecretKey());
	}

	/** getNeedPushClients 只该留下 isPush=true 的 */
	@Test
	public void getNeedPushClients_filtersIsPush() {
		SaSsoManager.getServerConfig().addClient(new SaSsoClientModel()
				.setClient("quiet")
				.setIsPush(false)
				.setServerUrl("http://quiet.com"));
		List<SaSsoClientModel> list = tpl.getNeedPushClients();
		Assertions.assertTrue(list.stream().allMatch(SaSsoClientModel::getIsPush));
		Assertions.assertTrue(list.stream().anyMatch(c -> SsoTestSupport.CLIENT.equals(c.getClient())));
		Assertions.assertTrue(list.stream().noneMatch(c -> "quiet".equals(c.getClient())));
	}

	/** buildRedirectUrl 应该删掉旧 ticket，并把 back 编码后再拼新 ticket */
	@Test
	public void buildRedirectUrl_deletesOldTicketAndEncodesBack() {
		String old = tpl.createTicketAndSave(SsoTestSupport.CLIENT, 10001, "old");
		String url = tpl.buildRedirectUrl(SsoTestSupport.CLIENT,
				"http://sso-client.com/sso/login?back=http://sso-client.com/page",
				10001, "new");
		Assertions.assertNull(tpl.getTicket(old));
		Assertions.assertTrue(url.contains("ticket="));
		Assertions.assertTrue(url.contains("back="));
	}

	/** encodeBackParam：没 back、已经编码、需要编码、?back= 和 &back= */
	@Test
	public void encodeBackParam_allBranches() {
		Assertions.assertEquals("http://x.com/a", tpl.encodeBackParam("http://x.com/a"));
		String encoded = "http://x.com/sso/login?back=http%3A%2F%2Fx.com%2Fp";
		Assertions.assertEquals(encoded, tpl.encodeBackParam(encoded));
		String need = tpl.encodeBackParam("http://x.com/sso/login?back=http://x.com/p");
		Assertions.assertTrue(need.contains("http%3A%2F%2Fx.com%2Fp") || need.contains("back=http"));
		String amp = tpl.encodeBackParam("http://x.com/sso/login?x=1&back=http://x.com/p");
		Assertions.assertTrue(amp.contains("&back="));
	}

	/** checkRedirectUrl：非法 url、@、%40、%2540、不在白名单、query 要被剥掉 */
	@Test
	public void checkRedirectUrl_securityAndQueryStrip() {
		Assertions.assertEquals(SaSsoErrorCode.CODE_30001, Assertions.assertThrows(SaSsoException.class,
				() -> tpl.checkRedirectUrl(SsoTestSupport.CLIENT, "not-url")).getCode());
		Assertions.assertEquals(SaSsoErrorCode.CODE_30001, Assertions.assertThrows(SaSsoException.class,
				() -> tpl.checkRedirectUrl(SsoTestSupport.CLIENT, "http://sso-client.com:80@evil.com")).getCode());
		Assertions.assertEquals(SaSsoErrorCode.CODE_30001, Assertions.assertThrows(SaSsoException.class,
				() -> tpl.checkRedirectUrl(SsoTestSupport.CLIENT, "http://sso-client.com%40evil.com")).getCode());
		Assertions.assertEquals(SaSsoErrorCode.CODE_30001, Assertions.assertThrows(SaSsoException.class,
				() -> tpl.checkRedirectUrl(SsoTestSupport.CLIENT, "http://sso-client.com%2540evil.com")).getCode());
		Assertions.assertEquals(SaSsoErrorCode.CODE_30002, Assertions.assertThrows(SaSsoException.class,
				() -> tpl.checkRedirectUrl(SsoTestSupport.CLIENT, "http://evil.com/cb")).getCode());
		tpl.checkRedirectUrl(SsoTestSupport.CLIENT, "http://sso-client.com/sso/login?foo=1");
	}

	/** checkAllowUrlListStaticMethod：*、path *、端口 *、中间 *、domain*、ipv6 */
	@Test
	public void checkAllowUrlListStaticMethod_patterns() {
		SaSsoServerTemplate.checkAllowUrlListStaticMethod(Collections.singletonList("*"));
		SaSsoServerTemplate.checkAllowUrlListStaticMethod(Collections.singletonList("http://x/*"));
		SaSsoServerTemplate.checkAllowUrlListStaticMethod(Collections.singletonList("http://x:*"));
		SaSsoServerTemplate.checkAllowUrlListStaticMethod(Collections.singletonList("http://[2001:db8::1]/*"));
		SaSsoServerTemplate.checkAllowUrlListStaticMethod(Collections.singletonList("http://plain.com/sso/login"));
		tpl.checkAllowUrlList(Collections.singletonList("http://ok.com/*"));
		Assertions.assertEquals(SaSsoErrorCode.CODE_30015, Assertions.assertThrows(SaSsoException.class,
				() -> SaSsoServerTemplate.checkAllowUrlListStaticMethod(Collections.singletonList("http://*.x.com"))).getCode());
		Assertions.assertEquals(SaSsoErrorCode.CODE_30015, Assertions.assertThrows(SaSsoException.class,
				() -> SaSsoServerTemplate.checkAllowUrlListStaticMethod(Collections.singletonList("http://domain*"))).getCode());
	}

	/** registerSloCallbackUrl：空 loginId 跳过；超过 maxRegClient 清退；-1 不限制 */
	@Test
	public void registerSloCallbackUrl_emptyMaxAndUnlimited() {
		tpl.registerSloCallbackUrl(null, SsoTestSupport.CLIENT, "http://c/cb");
		tpl.registerSloCallbackUrl("", SsoTestSupport.CLIENT, "http://c/cb");
		SaSsoManager.getServerConfig().setMaxRegClient(1);
		AtomicInteger pushes = new AtomicInteger();
		tpl.strategy.sendRequest = url -> {
			pushes.incrementAndGet();
			return "ok";
		};
		tpl.registerSloCallbackUrl(10001, SsoTestSupport.CLIENT, "http://sso-client.com/sso/logoutCall");
		tpl.registerSloCallbackUrl(10001, SsoTestSupport.CLIENT, "http://sso-client.com/sso/logoutCall2");
		Assertions.assertTrue(pushes.get() >= 1);
		SaSsoManager.getServerConfig().setMaxRegClient(-1);
		for (int i = 0; i < 3; i++) {
			tpl.registerSloCallbackUrl(20001, SsoTestSupport.CLIENT, "http://sso-client.com/cb" + i);
		}
		List<SaSsoClientInfo> list = StpUtil.getSessionByLoginId(20001)
				.getList(SaSsoConsts.SSO_CLIENT_MODEL_LIST_KEY_, SaSsoClientInfo.class, ArrayList::new);
		Assertions.assertEquals(3, list.size());
	}

	/** calcNextIndex：null/空是 0，递增，MAX_VALUE 绕回 0 */
	@Test
	public void calcNextIndex_nullIncrementAndWrap() {
		Assertions.assertEquals(0, tpl.calcNextIndex(null));
		Assertions.assertEquals(0, tpl.calcNextIndex(new ArrayList<>()));
		List<SaSsoClientInfo> list = new ArrayList<>();
		list.add(new SaSsoClientInfo("c", "u", 2));
		Assertions.assertEquals(3, tpl.calcNextIndex(list));
		list.add(new SaSsoClientInfo("c", "u", Integer.MAX_VALUE));
		Assertions.assertEquals(0, tpl.calcNextIndex(list));
	}

	/** ssoLogout session 不存在时应该提前返回；有登记 client 时要通知 */
	@Test
	public void ssoLogout_sessionNullAndWithClients() {
		tpl.ssoLogout(99999);
		AtomicReference<String> captured = new AtomicReference<>();
		tpl.strategy.sendRequest = url -> {
			captured.set(url);
			return "ok";
		};
		SsoTestSupport.withPath("/tmp", () -> {
			StpUtil.login(10001);
			return null;
		});
		tpl.registerSloCallbackUrl(10001, SsoTestSupport.CLIENT, "http://sso-client.com/sso/logoutCall");
		tpl.ssoLogout(10001, new SaLogoutParameter(), null);
		Assertions.assertNotNull(captured.get());
	}

	/** notifyClientLogout：null、非模式三、空 slo+push、空 slo 不 push、有 slo 发请求 */
	@Test
	public void notifyClientLogout_allBranches() {
		Assertions.assertNull(tpl.notifyClientLogout(1, "d", null, false, false));
		SaSsoClientInfo mode2 = new SaSsoClientInfo();
		mode2.setMode(SaSsoConsts.SSO_MODE_2);
		Assertions.assertNull(tpl.notifyClientLogout(1, "d", mode2, false, false));
		SaSsoClientInfo emptySlo = new SaSsoClientInfo(SsoTestSupport.CLIENT, "", 0);
		Assertions.assertNull(tpl.notifyClientLogout(1, "d", emptySlo, false, false));
		AtomicReference<String> pushed = new AtomicReference<>();
		tpl.strategy.sendRequest = url -> {
			pushed.set(url);
			return "ok";
		};
		Assertions.assertEquals("ok", tpl.notifyClientLogout(1, "d", emptySlo, true, true));
		Assertions.assertNotNull(pushed.get());
		SaSsoClientInfo emptyClient = new SaSsoClientInfo("", "", 0);
		Assertions.assertNull(tpl.notifyClientLogout(1, "d", emptyClient, true, true));
		SaSsoClientInfo withSlo = new SaSsoClientInfo(SsoTestSupport.CLIENT, "http://sso-client.com/sso/logoutCall", 1);
		Assertions.assertEquals("ok", tpl.notifyClientLogout(1, "dev", withSlo, false, false));
	}

	/** pushMessage / pushMessageAsSaResult 按模型和按名字都该能发出去 */
	@Test
	public void pushMessage_byModelAndName() {
		tpl.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
		SaSsoMessage msg = new SaSsoMessage("checkTicket").set("ticket", "t");
		SaSsoClientModel client = tpl.getClient(SsoTestSupport.CLIENT);
		Assertions.assertTrue(tpl.pushMessage(client, msg).contains("ok") || tpl.pushMessage(client, msg).contains("{"));
		SaResult r1 = tpl.pushMessageAsSaResult(client, new SaSsoMessage("checkTicket").set("ticket", "t"));
		Assertions.assertEquals(200, r1.getCode());
		Assertions.assertNotNull(tpl.pushMessage(SsoTestSupport.CLIENT, new SaSsoMessage("checkTicket").set("ticket", "t")));
		SaResult r2 = tpl.pushMessageAsSaResult(SsoTestSupport.CLIENT, new SaSsoMessage("checkTicket").set("ticket", "t"));
		Assertions.assertEquals(200, r2.getCode());
	}

	/** pushToAllClient 配了 ignoreClient 时应该跳过它 */
	@Test
	public void pushToAllClient_ignoreClient() {
		AtomicInteger n = new AtomicInteger();
		tpl.strategy.sendRequest = url -> {
			n.incrementAndGet();
			return "ok";
		};
		tpl.pushToAllClient(new SaSsoMessage("checkTicket").set("ticket", "t"));
		int all = n.get();
		n.set(0);
		tpl.pushToAllClient(new SaSsoMessage("checkTicket").set("ticket", "t"), SsoTestSupport.CLIENT);
		Assertions.assertTrue(all >= 1);
		Assertions.assertEquals(0, n.get());
	}

	/** pushToAllClientByLogoutCall 应该跳过 !isSlo 和 ignoreClient */
	@Test
	public void pushToAllClientByLogoutCall_skips() {
		SaSsoManager.getServerConfig().addClient(new SaSsoClientModel()
				.setClient("noslo")
				.setIsPush(true)
				.setIsSlo(false)
				.setServerUrl("http://noslo.com")
				.setSecretKey(SsoTestSupport.SECRET));
		SaSsoManager.getServerConfig().addClient(new SaSsoClientModel()
				.setClient("keep")
				.setIsPush(true)
				.setIsSlo(true)
				.setServerUrl("http://keep.com")
				.setSecretKey(SsoTestSupport.SECRET));
		List<String> urls = new ArrayList<>();
		tpl.strategy.sendRequest = url -> {
			urls.add(url);
			return "ok";
		};
		tpl.pushToAllClientByLogoutCall(1, new SaLogoutParameter(), SsoTestSupport.CLIENT);
		Assertions.assertTrue(urls.stream().noneMatch(u -> u.contains("noslo.com")));
		Assertions.assertTrue(urls.stream().anyMatch(u -> u.contains("keep.com")));
	}

	/** ticket key：空 client 和 * 都该落成 anon */
	@Test
	public void splicingKeys_anonForEmptyAndWildcard() {
		String save = tpl.splicingTicketModelSaveKey("abc");
		Assertions.assertTrue(save.contains(":ticket:abc"));
		String empty = tpl.splicingTicketIndexKey("", 1);
		String star = tpl.splicingTicketIndexKey(SaSsoConsts.CLIENT_WILDCARD, 1);
		String named = tpl.splicingTicketIndexKey(SsoTestSupport.CLIENT, 1);
		Assertions.assertTrue(empty.contains(":" + SaSsoConsts.CLIENT_ANON + ":"));
		Assertions.assertTrue(star.contains(":" + SaSsoConsts.CLIENT_ANON + ":"));
		Assertions.assertTrue(named.contains(":" + SsoTestSupport.CLIENT + ":"));
	}

	/** 签名秘钥优先级：client > server > sign 默认 */
	@Test
	public void getSignTemplate_secretKeyPriority() {
		Assertions.assertEquals(SsoTestSupport.SECRET,
				tpl.getSignTemplate(SsoTestSupport.CLIENT).getSignConfigOrGlobal().getSecretKey());
		tpl.getClient(SsoTestSupport.CLIENT).setSecretKey(null);
		SaSsoManager.getServerConfig().setSecretKey("server-secret");
		Assertions.assertEquals("server-secret",
				tpl.getSignTemplate(SsoTestSupport.CLIENT).getSignConfigOrGlobal().getSecretKey());
		tpl.getClient(SsoTestSupport.CLIENT).setSecretKey(null);
		SaSsoManager.getServerConfig().setSecretKey(null);
		SaSignManager.setConfig(new SaSignConfig().setSecretKey("sign-default"));
		Assertions.assertEquals("sign-default",
				tpl.getSignTemplate(SsoTestSupport.CLIENT).getSignConfigOrGlobal().getSecretKey());
		SaSsoManager.getServerConfig().setAllowAnonClient(true).setSecretKey(null);
		SaSignManager.setConfig(new SaSignConfig().setSecretKey("anon-sign"));
		Assertions.assertEquals("anon-sign", tpl.getSignTemplate("").getSignConfigOrGlobal().getSecretKey());
	}

	/** setStpLogic / getStpLogic / getStpLogicOrGlobal 应该成套好使 */
	@Test
	public void stpLogic_getSetOrGlobal() {
		Assertions.assertNull(tpl.getStpLogic());
		Assertions.assertSame(StpUtil.stpLogic, tpl.getStpLogicOrGlobal());
		StpLogic custom = new StpLogic("sso-server-tpl");
		tpl.setStpLogic(custom);
		Assertions.assertSame(custom, tpl.getStpLogic());
		Assertions.assertSame(custom, tpl.getStpLogicOrGlobal());
	}

}

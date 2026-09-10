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

import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.support.SsoTestSupport;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.sso.support.SsoTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Server 工具类每个公开静态方法都要走到模板上
 */
@SsoTest
public class SaSsoServerUtilTest {

	/** 每个用例前换干净 Processor */
	@BeforeEach
	public void reset() {
		SsoTestSupport.resetProcessors();
		SsoTestSupport.installDefaultConfig();
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
	}

	/** 公开静态方法应该都能委托到当前 Server 模板 */
	@Test
	public void publicMethods_delegateToTemplate() {
		Assertions.assertSame(SaSsoServerProcessor.instance.ssoServerTemplate, SaSsoServerUtil.getSsoTemplate());
		String ticket = SaSsoServerUtil.createTicketAndSave(SsoTestSupport.CLIENT, 10001, "tok");
		Assertions.assertNotNull(SaSsoServerUtil.getTicket(ticket));
		Assertions.assertEquals(10001, SaSsoServerUtil.getLoginId(ticket));
		Assertions.assertEquals("10001", SaSsoServerUtil.getLoginId(ticket, String.class));
		Assertions.assertNotNull(SaSsoServerUtil.checkTicket(ticket));
		Assertions.assertNotNull(SaSsoServerUtil.getTicketValue(SsoTestSupport.CLIENT, 10001));
		SaSsoServerUtil.deleteTicket(ticket);
		Assertions.assertNull(SaSsoServerUtil.getTicket(ticket));

		String t2 = SaSsoServerUtil.createTicketAndSave(SsoTestSupport.CLIENT, 2, "tok");
		SaSsoServerUtil.checkTicketParamAndDelete(t2);
		String t3 = SaSsoServerUtil.createTicketAndSave(SsoTestSupport.CLIENT, 3, "tok");
		SaSsoServerUtil.checkTicketParamAndDelete(t3, SsoTestSupport.CLIENT);

		Assertions.assertFalse(SaSsoServerUtil.getClients().isEmpty());
		Assertions.assertNotNull(SaSsoServerUtil.getClient(SsoTestSupport.CLIENT));
		Assertions.assertNotNull(SaSsoServerUtil.getClientNotNull(SsoTestSupport.CLIENT));
		Assertions.assertNotNull(SaSsoServerUtil.getAnonClient());
		Assertions.assertFalse(SaSsoServerUtil.getNeedPushClients().isEmpty());

		SaSsoServerUtil.checkRedirectUrl(SsoTestSupport.CLIENT, "http://sso-client.com/sso/login");
		String redirect = SaSsoServerUtil.buildRedirectUrl(SsoTestSupport.CLIENT, "http://sso-client.com/sso/login", 4, "tok");
		Assertions.assertTrue(redirect.contains("ticket="));

		SaSsoMessage msg = new SaSsoMessage(SaSsoConsts.MESSAGE_CHECK_TICKET).set("ticket", "t");
		Assertions.assertNotNull(SaSsoServerUtil.pushMessage(SaSsoServerUtil.getClient(SsoTestSupport.CLIENT), msg));
		Assertions.assertEquals(200, SaSsoServerUtil.pushMessageAsSaResult(
				SaSsoServerUtil.getClient(SsoTestSupport.CLIENT), new SaSsoMessage("checkTicket").set("ticket", "t")).getCode());
		Assertions.assertNotNull(SaSsoServerUtil.pushMessage(SsoTestSupport.CLIENT, new SaSsoMessage("checkTicket").set("ticket", "t")));
		Assertions.assertEquals(200, SaSsoServerUtil.pushMessageAsSaResult(SsoTestSupport.CLIENT,
				new SaSsoMessage("checkTicket").set("ticket", "t")).getCode());
		SaSsoServerUtil.pushToAllClient(new SaSsoMessage("checkTicket").set("ticket", "t"));
		SaSsoServerUtil.pushToAllClient(new SaSsoMessage("checkTicket").set("ticket", "t"), "ignore");

		SaSsoServerUtil.ssoLogout(888);
		SaSsoServerUtil.ssoLogout(887, new SaLogoutParameter(), null);
	}

}

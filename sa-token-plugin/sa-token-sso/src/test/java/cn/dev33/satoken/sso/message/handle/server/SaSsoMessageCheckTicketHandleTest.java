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
package cn.dev33.satoken.sso.message.handle.server;

import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.support.SsoTestSupport;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.sso.support.SsoTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Server 端 checkTicket 消息处理器
 */
@SsoTest
public class SaSsoMessageCheckTicketHandleTest {

	/** 每个用例前换干净模板 */
	@BeforeEach
	public void reset() {
		SsoTestSupport.resetProcessors();
		SsoTestSupport.installDefaultConfig();
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.sendRequest = url -> "ok";
	}

	/** 处理 checkTicket 应该校验并删 ticket，再把 loginId 放进结果 */
	@Test
	public void handle_checkTicketReturnsLoginId() {
		SaSsoServerTemplate tpl = SaSsoServerProcessor.instance.ssoServerTemplate;
		SsoTestSupport.withPath("/tmp", () -> {
			StpUtil.login(10001);
			return null;
		});
		String ticket = tpl.createTicketAndSave(SsoTestSupport.CLIENT, 10001, "tok");
		SaSsoMessage msg = new SaSsoMessage(SaSsoConsts.MESSAGE_CHECK_TICKET)
				.set("client", SsoTestSupport.CLIENT)
				.set("ticket", ticket)
				.set("ssoLogoutCall", "http://sso-client.com/sso/logoutCall");
		SaSsoMessageCheckTicketHandle handle = new SaSsoMessageCheckTicketHandle();
		Assertions.assertEquals(SaSsoConsts.MESSAGE_CHECK_TICKET, handle.getHandlerType());
		Object out = handle.handle(tpl, msg);
		Assertions.assertTrue(out instanceof SaResult);
		SaResult r = (SaResult) out;
		Assertions.assertEquals(10001, r.get("loginId"));
		Assertions.assertEquals("tok", r.get("tokenValue"));
		Assertions.assertNull(tpl.getTicket(ticket));
	}

}

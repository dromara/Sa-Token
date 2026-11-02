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

import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.support.SsoTestSupport;
import cn.dev33.satoken.sso.support.SsoTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

/**
 * 过时的 SaSsoUtil 每个公开静态方法至少走一遍
 */
@SsoTest
public class SaSsoUtilTest {

	/** 每个用例前换干净 Processor */
	@BeforeEach
	public void reset() {
		SsoTestSupport.resetProcessors();
		SsoTestSupport.installDefaultConfig();
		SaSsoServerProcessor.instance.ssoServerTemplate.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
	}

	/** 过时工具类的公开方法应该都能委托出去 */
	@Test
	public void publicMethods_stillDelegate() {
		String ticket = SaSsoUtil.createTicket(SsoTestSupport.CLIENT, 10001, "tok");
		Assertions.assertEquals(10001, SaSsoUtil.getLoginId(ticket));
		Assertions.assertEquals("10001", SaSsoUtil.getLoginId(ticket, String.class));
		SaSsoUtil.checkTicket(ticket);
		String t2 = SaSsoUtil.createTicket(SsoTestSupport.CLIENT, 2, "tok");
		SaSsoUtil.checkTicket(t2, SsoTestSupport.CLIENT);
		String t3 = SaSsoUtil.createTicket(SsoTestSupport.CLIENT, 3, "tok");
		SaSsoUtil.deleteTicketIndex(SsoTestSupport.CLIENT, 3);
		SaSsoUtil.deleteTicket(t3);
		SaSsoUtil.checkRedirectUrl(SsoTestSupport.CLIENT, "http://sso-client.com/sso/login");
		SaSsoUtil.registerSloCallbackUrl(4, SsoTestSupport.CLIENT, "http://sso-client.com/cb");
		SaSsoUtil.ssoLogout(4);
		Assertions.assertNotNull(SaSsoUtil.getData(new LinkedHashMap<>()));
		Assertions.assertNotNull(SaSsoUtil.getData("/x", new LinkedHashMap<>()));
		Assertions.assertTrue(SaSsoUtil.buildServerAuthUrl("http://sso-client.com/sso/login", "/h").contains("/sso/auth"));
		Assertions.assertTrue(SaSsoUtil.buildRedirectUrl(SsoTestSupport.CLIENT, "http://sso-client.com/sso/login", 5, "tok").contains("ticket="));
		Assertions.assertTrue(SaSsoUtil.buildGetDataUrl(new LinkedHashMap<>()).contains("sign="));
		Assertions.assertTrue(SaSsoUtil.buildCustomPathUrl("/p", new LinkedHashMap<>()).contains("/p"));
	}

}

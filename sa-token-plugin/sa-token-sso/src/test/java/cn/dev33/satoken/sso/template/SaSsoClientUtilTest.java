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
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.support.SsoTestSupport;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.sso.support.SsoTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

/**
 * Client 工具类每个公开静态方法都要走到模板上
 */
@SsoTest
public class SaSsoClientUtilTest {

	/** 每个用例前换干净 Processor */
	@BeforeEach
	public void reset() {
		SsoTestSupport.resetProcessors();
		SsoTestSupport.installDefaultConfig();
		SaSsoClientProcessor.instance.ssoClientTemplate.strategy.sendRequest = url -> "{\"code\":200,\"msg\":\"ok\"}";
	}

	/** 公开静态方法应该都能委托到当前 Client 模板 */
	@Test
	public void publicMethods_delegateToTemplate() {
		Assertions.assertSame(SaSsoClientProcessor.instance.ssoClientTemplate, SaSsoClientUtil.getSsoTemplate());
		Assertions.assertNotNull(SaSsoClientUtil.getData(new LinkedHashMap<>()));
		Assertions.assertNotNull(SaSsoClientUtil.getData("/x", new LinkedHashMap<>()));
		String auth = SaSsoClientUtil.buildServerAuthUrl("http://sso-client.com/sso/login", "/h");
		Assertions.assertTrue(auth.contains("/sso/auth"));
		SaSsoMessage msg = new SaSsoMessage(SaSsoConsts.MESSAGE_CHECK_TICKET).set("ticket", "t");
		Assertions.assertNotNull(SaSsoClientUtil.pushMessage(msg));
		Assertions.assertEquals(200, SaSsoClientUtil.pushMessageAsSaResult(new SaSsoMessage("checkTicket").set("ticket", "t")).getCode());
		Assertions.assertEquals(SaSsoConsts.MESSAGE_CHECK_TICKET,
				SaSsoClientUtil.buildCheckTicketMessage("t", "http://c/cb").getType());
		Assertions.assertEquals(SaSsoConsts.MESSAGE_SIGNOUT,
				SaSsoClientUtil.buildSignoutMessage(1, new SaLogoutParameter()).getType());
		SsoTestSupport.withPath("/tmp", () -> {
			StpUtil.login(10001);
			SaSsoClientUtil.ssoLogout(10001);
			return null;
		});
		SaSsoClientUtil.ssoLogout(2, new SaLogoutParameter());
	}

}

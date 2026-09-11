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
package cn.dev33.satoken.sso.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SSO 常量字面量要对得上生产定义
 */
public class SaSsoConstsTest {

	/** 应测尽测：测试 SSO 公开常量字面量与无参构造 */
	@Test
	public void constants_keepLiteralValues() {
		new SaSsoConsts();
		Assertions.assertEquals("SLO_CALLBACK_SET_KEY_", SaSsoConsts.SLO_CALLBACK_SET_KEY);
		Assertions.assertEquals("SSO_CLIENT_MODEL_LIST_KEY_", SaSsoConsts.SSO_CLIENT_MODEL_LIST_KEY_);
		Assertions.assertEquals("ok", SaSsoConsts.OK);
		Assertions.assertEquals("self", SaSsoConsts.SELF);
		Assertions.assertEquals("simple", SaSsoConsts.MODE_SIMPLE);
		Assertions.assertEquals("ticket", SaSsoConsts.MODE_TICKET);
		Assertions.assertEquals("{\"msg\": \"not handle\"}", SaSsoConsts.NOT_HANDLE);
		Assertions.assertEquals("*", SaSsoConsts.CLIENT_WILDCARD);
		Assertions.assertEquals("anon", SaSsoConsts.CLIENT_ANON);
		Assertions.assertEquals(1, SaSsoConsts.SSO_MODE_1);
		Assertions.assertEquals(2, SaSsoConsts.SSO_MODE_2);
		Assertions.assertEquals(3, SaSsoConsts.SSO_MODE_3);
		Assertions.assertEquals("checkTicket", SaSsoConsts.MESSAGE_CHECK_TICKET);
		Assertions.assertEquals("signout", SaSsoConsts.MESSAGE_SIGNOUT);
		Assertions.assertEquals("logoutCall", SaSsoConsts.MESSAGE_LOGOUT_CALL);
	}

}

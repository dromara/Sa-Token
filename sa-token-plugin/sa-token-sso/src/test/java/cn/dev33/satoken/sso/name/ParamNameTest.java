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
package cn.dev33.satoken.sso.name;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SSO 参数名默认值
 */
public class ParamNameTest {

	/** 字段默认名应该就是 redirect、ticket、back 这一套 */
	@Test
	public void defaults_matchExpectedNames() {
		ParamName p = new ParamName();
		Assertions.assertEquals("redirect", p.redirect);
		Assertions.assertEquals("ticket", p.ticket);
		Assertions.assertEquals("back", p.back);
		Assertions.assertEquals("mode", p.mode);
		Assertions.assertEquals("loginId", p.loginId);
		Assertions.assertEquals("client", p.client);
		Assertions.assertEquals("tokenName", p.tokenName);
		Assertions.assertEquals("tokenValue", p.tokenValue);
		Assertions.assertEquals("deviceId", p.deviceId);
		Assertions.assertEquals("secretkey", p.secretkey);
		Assertions.assertEquals("ssoLogoutCall", p.ssoLogoutCall);
		Assertions.assertEquals("autoLogout", p.autoLogout);
		Assertions.assertEquals("name", p.name);
		Assertions.assertEquals("pwd", p.pwd);
		Assertions.assertEquals("timestamp", p.timestamp);
		Assertions.assertEquals("nonce", p.nonce);
		Assertions.assertEquals("sign", p.sign);
		Assertions.assertEquals("remainSessionTimeout", p.remainSessionTimeout);
		Assertions.assertEquals("remainTokenTimeout", p.remainTokenTimeout);
		Assertions.assertEquals("singleDeviceIdLogout", p.singleDeviceIdLogout);
	}

}

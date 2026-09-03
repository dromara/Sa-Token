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
package cn.dev33.satoken.core.stp;

import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Token 参数扩展 
 * 
 * @author click33
 * @since 2022-9-5
 */
public class TokenInfoTest {

	@Test
	public void gettersAndSetters() {
		SaTokenInfo info = new SaTokenInfo();
		info.setTokenName("satoken");
		info.setTokenValue("xxxxx-xxxxx-xxxxx-xxxxx");
		info.setIsLogin(true);
		info.setLoginId(10001);
		info.setLoginType("login");
		info.setTokenTimeout(1800);
		info.setSessionTimeout(120);
		info.setTokenSessionTimeout(1800);
		info.setTokenActiveTimeout(120);
		info.setLoginDeviceType("PC");
		info.setTag("xxx");

		Assertions.assertEquals("satoken", info.getTokenName());
		Assertions.assertEquals("xxxxx-xxxxx-xxxxx-xxxxx", info.getTokenValue());
		Assertions.assertEquals(true, info.getIsLogin());
		Assertions.assertEquals(10001, info.getLoginId());
		Assertions.assertEquals("login", info.getLoginType());
		Assertions.assertEquals(1800, info.getTokenTimeout());
		Assertions.assertEquals(120, info.getSessionTimeout());
		Assertions.assertEquals(1800, info.getTokenSessionTimeout());
		Assertions.assertEquals(120, info.getTokenActiveTimeout());
		Assertions.assertEquals("PC", info.getLoginDeviceType());
		Assertions.assertEquals("xxx", info.getTag());

		Assertions.assertNotNull(info.toString());
	}

	@Test
	public void testLoginParameter() {
		Assertions.assertEquals(new SaLoginParameter().setDeviceType("PC").getDeviceType(), "PC");
		Assertions.assertEquals(new SaLoginParameter().setIsLastingCookie(false).getIsLastingCookie(), false);
		Assertions.assertEquals(new SaLoginParameter().setTimeout(1600).getTimeout(), 1600);
		Assertions.assertEquals(new SaLoginParameter().setToken("token-xxx").getToken(), "token-xxx");
		Assertions.assertEquals(new SaLoginParameter().setExtra("age", 18).getExtra("age"), 18);
		
		Map<String, Object> extraData = new HashMap<>();
		extraData.put("age", 20);
		SaLoginParameter lm = new SaLoginParameter().setExtraData(extraData);
		Assertions.assertEquals(lm.getExtraData(), extraData);
		Assertions.assertEquals(lm.getExtra("age"), 20);
		Assertions.assertTrue(lm.haveExtraData());
		Assertions.assertNotNull(lm.toString());
		
		// 计算 CookieTimeout 
		SaLoginParameter loginParameter = SaLoginParameter
				.create()
				.setTimeout(-1);
		Assertions.assertEquals(loginParameter.getCookieTimeout(), Integer.MAX_VALUE);
		Assertions.assertEquals(loginParameter.getDeviceType(), SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE);
	}
	
}

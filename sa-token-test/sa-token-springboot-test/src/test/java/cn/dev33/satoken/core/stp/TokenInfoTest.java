/*
 * Copyright 2020-2099 sa-token.cc
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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Token 参数扩展 
 * 
 * @author click33
 * @since: 2022-9-5
 */
public class TokenInfoTest {

	@Test
	public void test() {
		SaTokenInfo info = new SaTokenInfo();
		info.setTokenName("satoken");
		info.setTokenValue("xxxxx-xxxxx-xxxxx-xxxxx");
		info.setIsLogin(true);
		info.setLoginId(10001);
		info.setLoginType("login");
		info.setTokenTimeout(1800);
		info.setSessionTimeout(120);
		info.setTokenSessionTimeout(1800);
		info.setTokenActivityTimeout(120);
		info.setLoginDevice("PC");
		info.setTag("xxx");

		Assertions.assertEquals(info.getTokenName(), "satoken");
		Assertions.assertEquals(info.getTokenValue(), "xxxxx-xxxxx-xxxxx-xxxxx");
		Assertions.assertEquals(info.getIsLogin(), true);
		Assertions.assertEquals(info.getLoginId(), 10001);
		Assertions.assertEquals(info.getLoginType(), "login");
		Assertions.assertEquals(info.getTokenTimeout(), 1800);
		Assertions.assertEquals(info.getSessionTimeout(), 120);
		Assertions.assertEquals(info.getTokenSessionTimeout(), 1800);
		Assertions.assertEquals(info.getTokenActivityTimeout(), 120);
		Assertions.assertEquals(info.getLoginDevice(), "PC");
		Assertions.assertEquals(info.getTag(), "xxx");
		
		Assertions.assertNotNull(info.toString());
	}

	@Test
	public void testLoginModel() {
		Assertions.assertEquals(SaLoginConfig.setDevice("PC").getDevice(), "PC");
		Assertions.assertEquals(SaLoginConfig.setIsLastingCookie(false).getIsLastingCookie(), false);
		Assertions.assertEquals(SaLoginConfig.setTimeout(1600).getTimeout(), 1600);
		Assertions.assertEquals(SaLoginConfig.setToken("token-xxx").getToken(), "token-xxx");
		Assertions.assertEquals(SaLoginConfig.setExtra("age", 18).getExtra("age"), 18);
		
		Map<String, Object> extraData = new HashMap<>();
		extraData.put("age", 20);
		SaLoginModel lm = SaLoginConfig.setExtraData(extraData);
		Assertions.assertEquals(lm.getExtraData(), extraData);
		Assertions.assertEquals(lm.getExtra("age"), 20);
		Assertions.assertTrue(lm.isSetExtraData());
		Assertions.assertNotNull(lm.toString());
		
		// 计算 CookieTimeout 
		SaLoginModel loginModel = SaLoginModel
				.create()
				.setTimeout(-1);
		loginModel.build();
		Assertions.assertEquals(loginModel.getCookieTimeout(), Integer.MAX_VALUE);
		Assertions.assertEquals(loginModel.getDeviceOrDefault(), SaTokenConsts.DEFAULT_LOGIN_DEVICE);
	}
	
}

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
package cn.dev33.satoken.core.config;

import cn.dev33.satoken.config.SaCookieConfig;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutRange;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedLoginExitMode;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

/**
 * SaTokenConfig 扩展字段测试（SaTokenConfigTest 未覆盖部分）
 */
public class SaTokenConfigExtendedTest {

	/** 扩展字段的 getter/setter 应正常工作 */
	@Test
	void remainingFieldsGetSet() {
		SaTokenConfig config = new SaTokenConfig();

		config.setDynamicActiveTimeout(true);
		Assertions.assertTrue(config.getDynamicActiveTimeout());

		config.setReplacedLoginExitMode(SaReplacedLoginExitMode.NEW_DEVICE);
		Assertions.assertEquals(SaReplacedLoginExitMode.NEW_DEVICE, config.getReplacedLoginExitMode());

		config.setReplacedRange(SaReplacedRange.ALL_DEVICE_TYPE);
		Assertions.assertEquals(SaReplacedRange.ALL_DEVICE_TYPE, config.getReplacedRange());

		config.setOverflowLogoutMode(SaLogoutMode.KICKOUT);
		Assertions.assertEquals(SaLogoutMode.KICKOUT, config.getOverflowLogoutMode());

		config.setMaxTryTimes(7);
		Assertions.assertEquals(7, config.getMaxTryTimes());

		config.setIsLastingCookie(false);
		Assertions.assertFalse(config.getIsLastingCookie());

		config.setIsWriteHeader(true);
		Assertions.assertTrue(config.getIsWriteHeader());

		config.setLogoutRange(SaLogoutRange.ACCOUNT);
		Assertions.assertEquals(SaLogoutRange.ACCOUNT, config.getLogoutRange());

		config.setIsLogoutKeepFreezeOps(true);
		Assertions.assertTrue(config.getIsLogoutKeepFreezeOps());

		config.setIsLogoutKeepTokenSession(true);
		Assertions.assertTrue(config.getIsLogoutKeepTokenSession());

		config.setRightNowCreateTokenSession(true);
		Assertions.assertTrue(config.getRightNowCreateTokenSession());

		config.setCookieAutoFillPrefix(true);
		Assertions.assertTrue(config.getCookieAutoFillPrefix());

		config.setLogLevel("warn");
		Assertions.assertEquals("warn", config.getLogLevel());
		Assertions.assertEquals(4, config.getLogLevelInt());

		config.setLogLevelInt(2);
		Assertions.assertEquals("debug", config.getLogLevel());
		Assertions.assertEquals(2, config.getLogLevelInt());

		config.setIsColorLog(true);
		Assertions.assertTrue(config.getIsColorLog());

		config.setHttpDigest("digest:secret");
		Assertions.assertEquals("digest:secret", config.getHttpDigest());

		config.getCookie()
				.setDomain("sso.example.com")
				.setPath("/sso")
				.setSecure(true)
				.setHttpOnly(true)
				.setSameSite("none")
				.setExtraAttrs(new LinkedHashMap<>());
		SaCookieConfig cookie = config.getCookie();
		Assertions.assertEquals("sso.example.com", cookie.getDomain());
		Assertions.assertEquals("/sso", cookie.getPath());
		Assertions.assertTrue(cookie.getSecure());
		Assertions.assertTrue(cookie.getHttpOnly());
		Assertions.assertEquals("none", cookie.getSameSite());
		Assertions.assertNotNull(cookie.getExtraAttrs());

		Assertions.assertTrue(config.toString().contains("httpDigest=digest:secret"));
		Assertions.assertTrue(config.toString().contains("logoutRange=ACCOUNT"));
	}

	/** 废弃别名方法应映射到对应新字段 */
	@Test
	void deprecatedAliasMethods() {
		SaTokenConfig config = new SaTokenConfig();
		config.setActivityTimeout(900);
		Assertions.assertEquals(900, config.getActivityTimeout());
		Assertions.assertEquals(900, config.getActiveTimeout());

		config.setBasic("admin:pwd");
		Assertions.assertEquals("admin:pwd", config.getBasic());
		Assertions.assertEquals("admin:pwd", config.getHttpBasic());
	}

}

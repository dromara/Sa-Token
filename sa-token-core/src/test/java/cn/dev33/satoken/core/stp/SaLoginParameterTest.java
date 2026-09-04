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

import cn.dev33.satoken.config.SaCookieConfig;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedLoginExitMode;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedRange;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaTokenConsts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SaLoginParameter 参数对象测试
 */
@SaTokenTest
public class SaLoginParameterTest {

	/** 从 SaTokenConfig 构造后 getter/setter 及链式方法应正确读写各登录参数 */
	@Test
	void gettersSettersAndChainMethods() {
		SaTokenConfig config = new SaTokenConfig();
		config.setTimeout(7200);
		config.setIsConcurrent(false);
		config.setIsShare(true);
		config.setMaxLoginCount(5);
		config.setMaxTryTimes(3);
		config.setIsLastingCookie(false);
		config.setIsWriteHeader(true);
		config.setReplacedRange(SaReplacedRange.ALL_DEVICE_TYPE);
		config.setOverflowLogoutMode(SaLogoutMode.KICKOUT);
		config.setRightNowCreateTokenSession(true);
		config.setReplacedLoginExitMode(SaReplacedLoginExitMode.NEW_DEVICE);
		config.getCookie().setDomain("example.com").setPath("/app");

		SaLoginParameter param = new SaLoginParameter(config);
		Assertions.assertEquals(SaTokenConsts.DEFAULT_LOGIN_DEVICE_TYPE, param.getDeviceType());
		Assertions.assertEquals(7200, param.getTimeout());
		Assertions.assertFalse(param.getIsConcurrent());
		Assertions.assertTrue(param.getIsShare());
		Assertions.assertEquals(5, param.getMaxLoginCount());
		Assertions.assertEquals(3, param.getMaxTryTimes());
		Assertions.assertFalse(param.getIsLastingCookie());
		Assertions.assertTrue(param.getIsWriteHeader());
		Assertions.assertEquals(SaReplacedRange.ALL_DEVICE_TYPE, param.getReplacedRange());
		Assertions.assertEquals(SaLogoutMode.KICKOUT, param.getOverflowLogoutMode());
		Assertions.assertTrue(param.getRightNowCreateTokenSession());
		Assertions.assertEquals(SaReplacedLoginExitMode.NEW_DEVICE, param.getReplacedLoginExitMode());
		Assertions.assertEquals("example.com", param.getCookie().getDomain());

		SaCookieConfig cookie = new SaCookieConfig().setDomain("test.cn").setPath("/");
		param.setDeviceType("pc")
				.setDeviceId("device-100")
				.setTimeout(3600)
				.setActiveTimeout(1800)
				.setIsConcurrent(true)
				.setIsShare(false)
				.setMaxLoginCount(10)
				.setMaxTryTimes(8)
				.setIsLastingCookie(true)
				.setIsWriteHeader(false)
				.setReplacedRange(SaReplacedRange.CURR_DEVICE_TYPE)
				.setOverflowLogoutMode(SaLogoutMode.REPLACED)
				.setRightNowCreateTokenSession(false)
				.setReplacedLoginExitMode(SaReplacedLoginExitMode.OLD_DEVICE)
				.setToken("preset-token")
				.setCookie(cookie);

		Assertions.assertEquals("pc", param.getDeviceType());
		Assertions.assertEquals("device-100", param.getDeviceId());
		Assertions.assertEquals(3600, param.getTimeout());
		Assertions.assertEquals(1800, param.getActiveTimeout());
		Assertions.assertTrue(param.getIsConcurrent());
		Assertions.assertFalse(param.getIsShare());
		Assertions.assertEquals(10, param.getMaxLoginCount());
		Assertions.assertEquals(8, param.getMaxTryTimes());
		Assertions.assertTrue(param.getIsLastingCookie());
		Assertions.assertFalse(param.getIsWriteHeader());
		Assertions.assertEquals(SaReplacedRange.CURR_DEVICE_TYPE, param.getReplacedRange());
		Assertions.assertEquals(SaLogoutMode.REPLACED, param.getOverflowLogoutMode());
		Assertions.assertFalse(param.getRightNowCreateTokenSession());
		Assertions.assertEquals(SaReplacedLoginExitMode.OLD_DEVICE, param.getReplacedLoginExitMode());
		Assertions.assertEquals("preset-token", param.getToken());
		Assertions.assertSame(cookie, param.getCookie());
		Assertions.assertSame(param, param.setDeviceType("mobile"));
	}

	/** extra 与 terminalExtra 的读写、haveExtraData 判断及批量 set 应正常工作 */
	@Test
	void extraDataAndTerminalExtra() {
		SaLoginParameter param = new SaLoginParameter();
		Assertions.assertFalse(param.haveExtraData());
		Assertions.assertNull(param.getExtra("k1"));
		Assertions.assertFalse(param.haveTerminalExtraData());
		Assertions.assertNull(param.getTerminalExtra("t1"));

		param.setExtra("role", "admin")
				.setExtra("level", 9)
				.setTerminalExtra("tag", "vip");

		Assertions.assertTrue(param.haveExtraData());
		Assertions.assertEquals("admin", param.getExtra("role"));
		Assertions.assertEquals(9, param.getExtra("level"));
		Assertions.assertTrue(param.haveTerminalExtraData());
		Assertions.assertEquals("vip", param.getTerminalExtra("tag"));

		Map<String, Object> extraData = new LinkedHashMap<>();
		extraData.put("a", 1);
		Map<String, Object> terminalExtra = new LinkedHashMap<>();
		terminalExtra.put("b", 2);
		param.setExtraData(extraData).setTerminalExtraData(terminalExtra);
		Assertions.assertSame(extraData, param.getExtraData());
		Assertions.assertSame(terminalExtra, param.getTerminalExtraData());
	}

	/** getCookieTimeout 应根据 isLastingCookie 与 timeout 返回正确 Cookie 过期秒数 */
	@Test
	void getCookieTimeout() {
		SaLoginParameter lasting = new SaLoginParameter().setIsLastingCookie(true).setTimeout(120);
		Assertions.assertEquals(120, lasting.getCookieTimeout());

		SaLoginParameter sessionCookie = new SaLoginParameter().setIsLastingCookie(false).setTimeout(120);
		Assertions.assertEquals(-1, sessionCookie.getCookieTimeout());

		SaLoginParameter neverExpire = new SaLoginParameter()
				.setIsLastingCookie(true)
				.setTimeout(SaTokenDao.NEVER_EXPIRE);
		Assertions.assertEquals(Integer.MAX_VALUE, neverExpire.getCookieTimeout());
	}

	/** setupCookieConfig 与 setDefaultValues 应正确合并 Cookie 配置 */
	@Test
	void setupCookieConfigAndSetDefaultValues() {
		SaTokenConfig config = new SaTokenConfig();
		config.getCookie().setDomain("global.cn").setSameSite("strict");

		SaLoginParameter param = new SaLoginParameter();
		param.setupCookieConfig(c -> c.setDomain("local.cn").setPath("/api"));
		Assertions.assertEquals("local.cn", param.getCookie().getDomain());
		Assertions.assertEquals("/api", param.getCookie().getPath());

		param.setDefaultValues(config);
		Assertions.assertEquals("global.cn", param.getCookie().getDomain());
		Assertions.assertEquals("strict", param.getCookie().getSameSite());
	}

	/** create 工厂方法应填充默认值，setDevice 应与 setDeviceType 互通 */
	@Test
	void createFactoryAndDeprecatedDeviceAlias() {
		SaLoginParameter param = SaLoginParameter.create();
		Assertions.assertNotNull(param.getTimeout());
		Assertions.assertNotNull(param.getIsConcurrent());

		param.setDevice("pad");
		Assertions.assertEquals("pad", param.getDeviceType());
		Assertions.assertEquals("pad", param.getDevice());
		Assertions.assertTrue(param.toString().contains("deviceType=pad"));
	}

}

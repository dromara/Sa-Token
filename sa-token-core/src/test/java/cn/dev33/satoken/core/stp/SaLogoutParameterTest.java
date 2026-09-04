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

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutRange;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaLogoutParameter 参数对象测试
 */
@SaTokenTest
public class SaLogoutParameterTest {

	/** 从 SaTokenConfig 构造后 getter/setter 及链式方法应正确读写注销参数 */
	@Test
	void gettersAndSetters() {
		SaTokenConfig config = new SaTokenConfig();
		config.setLogoutRange(SaLogoutRange.ACCOUNT);
		config.setIsLogoutKeepFreezeOps(true);
		config.setIsLogoutKeepTokenSession(false);

		SaLogoutParameter param = new SaLogoutParameter(config);
		Assertions.assertEquals(SaLogoutRange.ACCOUNT, param.getRange());
		Assertions.assertTrue(param.getIsKeepFreezeOps());
		Assertions.assertFalse(param.getIsKeepTokenSession());
		Assertions.assertEquals(SaLogoutMode.LOGOUT, param.getMode());

		param.setDeviceType("pc")
				.setDeviceId("device-1")
				.setMode(SaLogoutMode.KICKOUT)
				.setRange(SaLogoutRange.TOKEN)
				.setIsKeepFreezeOps(false)
				.setIsKeepTokenSession(true);

		Assertions.assertEquals("pc", param.getDeviceType());
		Assertions.assertEquals("device-1", param.getDeviceId());
		Assertions.assertEquals(SaLogoutMode.KICKOUT, param.getMode());
		Assertions.assertEquals(SaLogoutRange.TOKEN, param.getRange());
		Assertions.assertFalse(param.getIsKeepFreezeOps());
		Assertions.assertTrue(param.getIsKeepTokenSession());
		Assertions.assertTrue(param.toString().contains("deviceType=pc"));
	}

	/** create 工厂方法应返回带默认 range 与 mode 的实例 */
	@Test
	void createFactory() {
		SaLogoutParameter param = SaLogoutParameter.create();
		Assertions.assertNotNull(param.getRange());
		Assertions.assertNotNull(param.getMode());
	}

}

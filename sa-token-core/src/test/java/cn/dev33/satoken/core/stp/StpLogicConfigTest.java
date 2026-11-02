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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic 配置读取与参数工厂方法
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicConfigTest {

	private StpLogic stpLogic;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
	}

	/** setConfig/setLoginType 后 getter 应返回设置的值 */
	@Test
	void configAndLoginType() {
		SaTokenConfig config = new SaTokenConfig();
		config.setTokenName("my-token");
		stpLogic.setConfig(config);
		Assertions.assertSame(config, stpLogic.getConfig());
		Assertions.assertSame(config, stpLogic.getConfigOrGlobal());
		Assertions.assertEquals("my-token", stpLogic.getTokenName());
		stpLogic.setLoginType("app");
		Assertions.assertEquals("app", stpLogic.getLoginType());
	}

	/** createSaLoginParameter/createSaLogoutParameter 应复制全局配置 */
	@Test
	void createSaLoginParameter_and_createSaLogoutParameter() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTimeout(7200);
		config.setIsConcurrent(false);
		SaManager.setConfig(config);

		SaLoginParameter loginParam = stpLogic.createSaLoginParameter();
		Assertions.assertEquals(7200, loginParam.getTimeout());
		Assertions.assertFalse(loginParam.getIsConcurrent());

		SaLogoutParameter logoutParam = stpLogic.createSaLogoutParameter();
		Assertions.assertEquals(config.getLogoutRange(), logoutParam.getRange());
	}

	/** 全局 timeout 为 NEVER_EXPIRE 时 getConfigOfCookieTimeout 应返回 MAX_VALUE */
	@Test
	void getConfigOfCookieTimeout_neverExpire_returnsMaxInt() {
		SaTokenConfig config = SaManager.getConfig();
		config.setTimeout(SaTokenDao.NEVER_EXPIRE);
		SaManager.setConfig(config);
		Assertions.assertEquals(Integer.MAX_VALUE, stpLogic.getConfigOfCookieTimeout());
	}

}

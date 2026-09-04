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
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * StpLogic Token 最低活跃频率
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class StpLogicActiveTimeoutTest {

	private StpLogic stpLogic;

	@BeforeEach
	void setUp() {
		stpLogic = new StpLogic("login");
		SaTokenConfig config = SaManager.getConfig();
		config.setActiveTimeout(180);
		SaManager.setConfig(config);
	}

	/** 更新活跃时间后 getTokenActiveTimeout 与 checkActiveTimeout 应正常 */
	@Test
	void updateLastActiveToNow_getTokenActiveTimeout_checkActiveTimeout() {
		SaTokenContextMockUtil.setMockContext(() -> {
			stpLogic.login(10001);
			Assertions.assertNotNull(stpLogic.getTokenValue());

			stpLogic.updateLastActiveToNow();
			long activeTimeout = stpLogic.getTokenActiveTimeout();
			Assertions.assertTrue(activeTimeout <= 180 && activeTimeout >= 179);

			Assertions.assertDoesNotThrow(() -> stpLogic.checkActiveTimeout());
		});
	}

}

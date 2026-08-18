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
import cn.dev33.satoken.model.wrapperInfo.SaDisableWrapperInfo;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * StpInterface.isDisabled 按 loginType 分流
 */
public class StpInterfaceIsDisabledLoginTypeTest {

	private static final String USER_TYPE = "stp-if-user";
	private static final String ADMIN_TYPE = "stp-if-admin";

	private StpInterface backup;
	private StpLogic userLogic;
	private StpLogic adminLogic;

	@BeforeEach
	public void setUp() {
		backup = SaManager.getStpInterface();
		SaManager.setStpInterface(new StpInterface() {
			@Override
			public List<String> getPermissionList(Object loginId, String loginType) {
				return new ArrayList<>();
			}
			@Override
			public List<String> getRoleList(Object loginId, String loginType) {
				return new ArrayList<>();
			}
			@Override
			public SaDisableWrapperInfo isDisabled(Object loginId, String service, String loginType) {
				if (ADMIN_TYPE.equals(loginType)) {
					return SaDisableWrapperInfo.createDisabled(60, 1);
				}
				return SaDisableWrapperInfo.createNotDisabled();
			}
		});
		userLogic = new StpLogic(USER_TYPE);
		adminLogic = new StpLogic(ADMIN_TYPE);
	}

	@AfterEach
	public void tearDown() {
		userLogic.untieDisable(10001);
		adminLogic.untieDisable(10001);
		SaManager.removeStpLogic(USER_TYPE);
		SaManager.removeStpLogic(ADMIN_TYPE);
		SaManager.setStpInterface(backup);
	}

	@Test
	public void isDisabled_splitByLoginType() {
		Assertions.assertEquals(-2, userLogic.getDisableLevel(10001));
		Assertions.assertEquals(1, adminLogic.getDisableLevel(10001));
	}

}

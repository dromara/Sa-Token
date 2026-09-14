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

import cn.dev33.satoken.stp.StpInterfaceDefaultImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * StpInterface 默认实现测试
 *
 * @author click33
 * @since 1.46.0
 */
public class StpInterfaceDefaultImplTest {

	/** 未配置权限接口时，默认实现应拒绝所有权限和角色 */
	@Test
	void returnsEmptyPermissionAndRoleLists() {
		StpInterfaceDefaultImpl stpInterface = new StpInterfaceDefaultImpl();
		Assertions.assertTrue(stpInterface.getPermissionList(1001, "login").isEmpty());
		Assertions.assertTrue(stpInterface.getRoleList(1001, "login").isEmpty());
	}

}

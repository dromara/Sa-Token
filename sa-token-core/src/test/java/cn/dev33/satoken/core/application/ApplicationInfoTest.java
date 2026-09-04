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
package cn.dev33.satoken.core.application;

import cn.dev33.satoken.application.ApplicationInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ApplicationInfo 测试
 *
 * @author click33
 * @since 1.46.0
 */
public class ApplicationInfoTest {

	@AfterEach
	void resetRoutePrefix() {
		ApplicationInfo.routePrefix = null;
	}

	/** routePrefix 字段应能正常读写 */
	@Test
	void routePrefixField() {
		ApplicationInfo.routePrefix = "/api/v1";
		Assertions.assertEquals("/api/v1", ApplicationInfo.routePrefix);
	}

	/** cutPathPrefix 应按路由前缀裁剪请求路径 */
	@Test
	void cutPathPrefix() {
		ApplicationInfo.routePrefix = "/api/v1";
		Assertions.assertEquals("/user/list", ApplicationInfo.cutPathPrefix("/api/v1/user/list"));
		Assertions.assertEquals("/other", ApplicationInfo.cutPathPrefix("/other"));

		ApplicationInfo.routePrefix = "/";
		Assertions.assertEquals("/user/list", ApplicationInfo.cutPathPrefix("/user/list"));

		ApplicationInfo.routePrefix = null;
		Assertions.assertEquals("/user/list", ApplicationInfo.cutPathPrefix("/user/list"));

		ApplicationInfo.routePrefix = "";
		Assertions.assertEquals("/user/list", ApplicationInfo.cutPathPrefix("/user/list"));
	}

}

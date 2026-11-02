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
package cn.dev33.satoken.loveqq.boot.context.path;

import cn.dev33.satoken.application.ApplicationInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link ApplicationContextPathLoading} 空上下文路径时不应该改路由前缀
 */
public class ApplicationContextPathLoadingTest {

	private String backupPrefix;

	/** 每个用例开始前记住原来的路由前缀 */
	@BeforeEach
	public void setUp() {
		backupPrefix = ApplicationInfo.routePrefix;
		ApplicationInfo.routePrefix = null;
	}

	/** 每个用例结束后把路由前缀还原 */
	@AfterEach
	public void tearDown() {
		ApplicationInfo.routePrefix = backupPrefix;
	}

	/** 没配 contextPath 时 run 完路由前缀应该还是空的 */
	@Test
	public void run_emptyContextPath_keepPrefixUnset() throws Exception {
		new ApplicationContextPathLoading().run();
		Assertions.assertNull(ApplicationInfo.routePrefix);
	}

}

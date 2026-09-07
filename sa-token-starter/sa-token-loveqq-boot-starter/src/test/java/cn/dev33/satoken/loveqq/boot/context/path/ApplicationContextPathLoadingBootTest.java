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
import com.kfyty.loveqq.framework.boot.K;
import com.kfyty.loveqq.framework.core.autoconfig.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 真实 LoveQQ 启动后，contextPath 应该规范化并写进路由前缀
 */
public class ApplicationContextPathLoadingBootTest {

	/** 缺前导斜杠、带尾斜杠时应该归一成 /app */
	@Test
	public void contextPath_withoutLeadingSlash_shouldNormalize() throws Exception {
		assertRoutePrefix("app/", "/app");
	}

	/** 已经是规范路径时应该原样写入 */
	@Test
	public void contextPath_alreadyNormalized_shouldKeep() throws Exception {
		assertRoutePrefix("/foo", "/foo");
	}

	/** 归一化后只剩根路径时不应该改路由前缀 */
	@Test
	public void contextPath_rootOnly_shouldKeepPrefixUnset() throws Exception {
		assertRoutePrefix("//", null);
	}

	/** 起一次容器，断言路由前缀，测完关掉 */
	private void assertRoutePrefix(String rawContextPath, String expectedPrefix) throws Exception {
		String backup = ApplicationInfo.routePrefix;
		ApplicationInfo.routePrefix = null;
		ApplicationContext ctx = K.start(ContextPathApp.class, "--k.mvc.tomcat.contextPath=" + rawContextPath);
		try {
			Assertions.assertEquals(expectedPrefix, ApplicationInfo.routePrefix);
		} finally {
			ctx.close();
			ApplicationInfo.routePrefix = backup;
		}
	}

}

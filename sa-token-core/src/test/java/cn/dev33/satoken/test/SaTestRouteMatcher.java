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
package cn.dev33.satoken.test;

import cn.dev33.satoken.strategy.SaStrategy;

/**
 * 单测用简易 Ant 风格路由匹配器（core 默认 {@link SaStrategy#routeMatcher} 未实现）。
 *
 * @author click33
 * @since 1.46.0
 */
public final class SaTestRouteMatcher {

	private SaTestRouteMatcher() {
	}

	public static void installAntStyleMatcher() {
		SaStrategy.instance.routeMatcher = SaTestRouteMatcher::match;
	}

	static boolean match(String pattern, String path) {
		if (pattern.equals(path)) {
			return true;
		}
		if (pattern.endsWith("/**")) {
			String prefix = pattern.substring(0, pattern.length() - 3);
			return path.equals(prefix) || path.startsWith(prefix + "/");
		}
		if (pattern.endsWith("/*")) {
			String prefix = pattern.substring(0, pattern.length() - 2);
			if (!path.startsWith(prefix + "/")) {
				return false;
			}
			return !path.substring(prefix.length() + 1).contains("/");
		}
		return false;
	}

}

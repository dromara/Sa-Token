/*
 * Copyright 2020-2099 sa-token.cc
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
package cn.dev33.satoken.spring;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 路由匹配工具类：使用 PathPatternParser 模式匹配
 *
 * @author click33
 * @since 1.35.1
 */
public class SaPathPatternParserUtil {

	private SaPathPatternParserUtil() {
	}

	/**
	 * 判断：指定路由匹配符是否可以匹配成功指定路径
	 * @param pattern 路由匹配符
	 * @param path 要匹配的路径
	 * @return 是否匹配成功
	 */
	public static boolean match(String pattern, String path) {
		PathPattern pathPattern = PathPatternParser.defaultInstance.parse(pattern);
		PathContainer pathContainer = PathContainer.parsePath(path);
		return pathPattern.matches(pathContainer);
    }

	/*
		表现：
			springboot 2.x SpringMVC	match("/test/test", "/test/test/")  // true
			springboot 2.x WebFlux		match("/test/test", "/test/test/")  // true
			springboot 3.x SpringMVC	match("/test/test", "/test/test/")  // false
			springboot 3.x WebFlux		match("/test/test", "/test/test/")  // false
	 */

}

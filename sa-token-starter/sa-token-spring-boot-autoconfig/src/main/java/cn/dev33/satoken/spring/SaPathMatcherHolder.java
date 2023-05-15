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

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * 持有 PathMatcher 全局引用，方便快捷的调用 PathMatcher 相关方法
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaPathMatcherHolder {
	
	private SaPathMatcherHolder() {
	}

	/**
	 * 路由匹配器
	 */
	public static PathMatcher pathMatcher;

	/**
	 * 获取路由匹配器
	 * @return 路由匹配器
	 */
	public static PathMatcher getPathMatcher() {
		if(pathMatcher == null) {
			pathMatcher = new AntPathMatcher();
		}
		return pathMatcher;
	}
	
	/**
	 * 写入路由匹配器
	 * @param pathMatcher 路由匹配器
	 */
	public static void setPathMatcher(PathMatcher pathMatcher) {
		SaPathMatcherHolder.pathMatcher = pathMatcher;
	}
	
}

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

import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;

/**
 * Sa-Token 上下文处理器 [ SpringMVC版本实现 ]。在 SpringMVC、SpringBoot 中使用 Sa-Token 时，必须注入此实现类，否则会出现上下文无效异常
 * 
 * @author click33
 * @since <= 1.34.0
 */
public class SaTokenContextForSpring implements SaTokenContext {

	/**
	 * 获取当前请求的 Request 包装对象
	 */
	@Override
	public SaRequest getRequest() {
		return new SaRequestForServlet(SpringMVCUtil.getRequest());
	}

	/**
	 * 获取当前请求的 Response 包装对象
	 */
	@Override
	public SaResponse getResponse() {
		return new SaResponseForServlet(SpringMVCUtil.getResponse());
	}

	/**
	 * 获取当前请求的 Storage 包装对象
	 */
	@Override
	public SaStorage getStorage() {
		return new SaStorageForServlet(SpringMVCUtil.getRequest());
	}

	/**
	 * 判断：指定路由匹配符是否可以匹配成功指定路径
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		return SaPathMatcherHolder.getPathMatcher().match(pattern, path);
	}

	/**
	 * 判断：在本次请求中，此上下文是否可用。
	 */
	@Override
	public boolean isValid() {
		return SpringMVCUtil.isWeb();
	}

}

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
package cn.dev33.satoken.fun;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;

/**
 * 路由拦截器验证方法的函数式接口，方便开发者进行 lambda 表达式风格调用
 * 
 * @author click33
 * @since 1.34.0
 */
@FunctionalInterface
public interface SaRouteFunction {

	/**
	 * 执行验证的方法
	 * 
	 * @param request  Request 包装对象
	 * @param response Response 包装对象
	 * @param handler  处理对象
	 */
	void run(SaRequest request, SaResponse response, Object handler);

}

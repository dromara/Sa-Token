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
package cn.dev33.satoken.solon.integration;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import cn.dev33.satoken.filter.SaFilterErrorStrategy;
import cn.dev33.satoken.filter.SaFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.strategy.SaStrategy;
import org.noear.solon.Solon;
import org.noear.solon.core.handle.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * sa-token 基于路由的过滤式鉴权（增加了注解的处理）；使用优先级要低些
 *
 * 对静态文件有处理效果
 *
 * order: -100 (SaTokenInterceptor 和 SaTokenFilter 二选一；不要同时用)
 *
 * @author noear
 * @since 1.10
 */
public class SaTokenFilter implements SaFilter, Filter { //之所以改名，为了跟 SaTokenInterceptor 形成一对

	/**
	 * 是否打开注解鉴权
	 */
	public boolean isAnnotation = true;

	// ------------------------ 设置此过滤器 拦截 & 放行 的路由

	/**
	 * 拦截路由
	 */
	public List<String> includeList = new ArrayList<>();

	/**
	 * 放行路由
	 */
	public List<String> excludeList = new ArrayList<>();

	@Override
	public SaTokenFilter addInclude(String... paths) {
		includeList.addAll(Arrays.asList(paths));
		return this;
	}

	@Override
	public SaTokenFilter addExclude(String... paths) {
		excludeList.addAll(Arrays.asList(paths));
		return this;
	}

	@Override
	public SaTokenFilter setIncludeList(List<String> pathList) {
		includeList = pathList;
		return this;
	}

	@Override
	public SaTokenFilter setExcludeList(List<String> pathList) {
		excludeList = pathList;
		return this;
	}


	// ------------------------ 钩子函数

	/**
	 * 认证函数：每次请求执行
	 */
	public SaFilterAuthStrategy auth = r -> {
	};

	/**
	 * 异常处理函数：每次[认证函数]发生异常时执行此函数
	 */
	public SaFilterErrorStrategy error = e -> {
		if (e instanceof SaTokenException) {
			throw (SaTokenException) e;
		} else {
			throw new SaTokenException(e);
		}
	};

	/**
	 * 前置函数：在每次[认证函数]之前执行
	 *      <b>注意点：前置认证函数将不受 includeList 与 excludeList 的限制，所有路由的请求都会进入 beforeAuth</b>
	 */
	public SaFilterAuthStrategy beforeAuth = r -> {
	};

	@Override
	public SaTokenFilter setAuth(SaFilterAuthStrategy auth) {
		this.auth = auth;
		return this;
	}

	@Override
	public SaTokenFilter setError(SaFilterErrorStrategy error) {
		this.error = error;
		return this;
	}

	@Override
	public SaTokenFilter setBeforeAuth(SaFilterAuthStrategy beforeAuth) {
		this.beforeAuth = beforeAuth;
		return this;
	}


	@Override
	public void doFilter(Context ctx, FilterChain chain) throws Throwable {
		try {
			//查找当前主处理
			Handler mainHandler = Solon.app().router().matchMain(ctx);
			Action action = (mainHandler instanceof Action ? (Action) mainHandler : null);

			//1.执行前置处理（主要是一些跨域之类的）
			if(beforeAuth != null) {
				beforeAuth.run(mainHandler);
			}

			//先路径过滤下（包括了静态文件）
			SaRouter.match(includeList).notMatch(excludeList).check(r -> {
				//2.执行注解处理
				if(authAnno(action)) {
					//3.执行规则处理（如果没有被 @SaIgnore 忽略）
					auth.run(mainHandler);
				}
			});
		} catch (StopMatchException e) {

		} catch (SaTokenException e) {
			// 1. 获取异常处理策略结果
			Object result;
			if (e instanceof BackResultException) {
				result = e.getMessage();
			} else {
				result = error.run(e);
			}

			// 2. 写入输出流
			if (result != null) {
				ctx.render(result);
			}
			ctx.setHandled(true);
			return;
		}

		chain.doFilter(ctx);
	}

	private boolean authAnno(Action action) {
		//2.验证注解处理
		if (isAnnotation && action != null) {
			// 获取此请求对应的 Method 处理函数
			Method method = action.method().getMethod();

			// 如果此 Method 或其所属 Class 标注了 @SaIgnore，则忽略掉鉴权
			if (SaStrategy.me.isAnnotationPresent.apply(method, SaIgnore.class)) {
				return false;
			}

			// 注解校验
			SaStrategy.me.checkMethodAnnotation.accept(method);
		}

		return true;
	}
}

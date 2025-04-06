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

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.filter.SaFilter;
import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import cn.dev33.satoken.filter.SaFilterErrorStrategy;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.solon.util.SaSolonOperateUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;
import org.noear.solon.core.route.RoutingTable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * sa-token 基于路由的过滤式鉴权（增加了注解的处理）；使用优先级要低些
 * <p>
 * 对静态文件无处理效果
 * <p>
 * order: -100 (SaTokenInterceptor 和 SaTokenFilter 二选一；不要同时用)
 *
 * @author noear
 * @since 1.12
 */
public class SaTokenInterceptor implements SaFilter, RouterInterceptor {
	/**
	 * 是否打开注解鉴权
	 */
	public boolean isAnnotation = true;

	// ------------------------ 设置此过滤器 拦截 & 放行 的路由

	/**
	 * 拦截路由
	 */
	protected List<String> includeList = new ArrayList<>();

	/**
	 * 放行路由
	 */
	protected List<String> excludeList = new ArrayList<>();

	/**
	 * 添加 [拦截路由]
	 *
	 * @param paths 路由
	 * @return 对象自身
	 */
	public SaTokenInterceptor addInclude(String... paths) {
		includeList.addAll(Arrays.asList(paths));
		return this;
	}

	/**
	 * 添加 [放行路由]
	 *
	 * @param paths 路由
	 * @return 对象自身
	 */
	public SaTokenInterceptor addExclude(String... paths) {
		excludeList.addAll(Arrays.asList(paths));
		return this;
	}

	/**
	 * 写入 [拦截路由] 集合
	 *
	 * @param pathList 路由集合
	 * @return 对象自身
	 */
	public SaTokenInterceptor setIncludeList(List<String> pathList) {
		includeList = pathList;
		return this;
	}

	/**
	 * 写入 [放行路由] 集合
	 *
	 * @param pathList 路由集合
	 * @return 对象自身
	 */
	public SaTokenInterceptor setExcludeList(List<String> pathList) {
		excludeList = pathList;
		return this;
	}

	/**
	 * 获取 [拦截路由] 集合
	 *
	 * @return see note
	 */
	public List<String> getIncludeList() {
		return includeList;
	}

	/**
	 * 获取 [放行路由] 集合
	 *
	 * @return see note
	 */
	public List<String> getExcludeList() {
		return excludeList;
	}


	// ------------------------ 钩子函数

	/**
	 * 认证函数：每次请求执行
	 */
	protected SaFilterAuthStrategy auth = r -> {
	};

	/**
	 * 异常处理函数：每次[认证函数]发生异常时执行此函数
	 */
	protected SaFilterErrorStrategy error = e -> {
		if (e instanceof SaTokenException) {
			throw (SaTokenException) e;
		} else {
			throw new SaTokenException(e);
		}
	};

	/**
	 * 前置函数：在每次[认证函数]之前执行
	 */
	protected SaFilterAuthStrategy beforeAuth = r -> {
	};

	/**
	 * 写入[认证函数]: 每次请求执行
	 *
	 * @param auth see note
	 * @return 对象自身
	 */
	public SaTokenInterceptor setAuth(SaFilterAuthStrategy auth) {
		this.auth = auth;
		return this;
	}

	/**
	 * 写入[异常处理函数]：每次[认证函数]发生异常时执行此函数
	 *
	 * @param error see note
	 * @return 对象自身
	 */
	public SaTokenInterceptor setError(SaFilterErrorStrategy error) {
		this.error = error;
		return this;
	}

	/**
	 * 写入[前置函数]：在每次[认证函数]之前执行
	 *
	 * @param beforeAuth see note
	 * @return 对象自身
	 */
	public SaTokenInterceptor setBeforeAuth(SaFilterAuthStrategy beforeAuth) {
		this.beforeAuth = beforeAuth;
		return this;
	}


	@Override
	public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
		try {
			if (mainHandler instanceof Gateway) {
				//支持网关处理
				Gateway gateway = (Gateway) mainHandler;
				RoutingTable<Handler> mainRouting = gateway.getMainRouting();
				MethodType method = MethodTypeUtil.valueOf(ctx.method());
				mainHandler = mainRouting.matchOne(ctx.pathNew(), method);
			}

			Action action = (mainHandler instanceof Action ? (Action) mainHandler : null);

			//1.执行前置处理（主要是一些跨域之类的）
			if(beforeAuth != null) {
				beforeAuth.run(mainHandler);
			}

			//先路径过滤下（不包括静态文件）
			Handler finalMainHandler = mainHandler;
			SaRouter.match(includeList).notMatch(excludeList).check(r -> {
				//2.执行注解处理
				if(authAnno(action)) {
					//3.执行规则处理（如果没有被 @SaIgnore 忽略）
					auth.run(finalMainHandler);
				}
			});

		}
		catch (StopMatchException ignored) {}
		catch (BackResultException e) {
			SaSolonOperateUtil.writeResult(ctx, e.getMessage());
			return;
		}
		catch (SaTokenException e) {
			SaSolonOperateUtil.writeResult(ctx, error.run(e));
			return;
		}

		chain.doIntercept(ctx, mainHandler);
	}

	private boolean authAnno(Action action) {
		//2.验证注解处理
		if (isAnnotation && action != null) {
			// 注解校验
			try{
				Method method = action.method().getMethod();
				SaAnnotationStrategy.instance.checkMethodAnnotation.accept(method);
			} catch (StopMatchException ignored) {
				return false;
			}
		}

		return true;
	}
}

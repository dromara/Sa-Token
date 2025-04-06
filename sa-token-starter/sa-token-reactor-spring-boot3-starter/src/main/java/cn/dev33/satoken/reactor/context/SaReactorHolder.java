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
package cn.dev33.satoken.reactor.context;

import cn.dev33.satoken.fun.SaRetGenericFunction;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

/**
 * Reactor 上下文操作（异步），持有当前请求的 ServerWebExchange 全局引用
 *
 * @author click33
 * @since 1.19.0
 */
public class SaReactorHolder {

	/**
	 * ServerWebExchange key
	 */
	public static final String EXCHANGE_KEY = "SA_REACTOR_EXCHANGE_KEY";

	/**
	 * WebFilterChain key
	 */
	public static final String CHAIN_KEY = "SA_REACTOR__CHAIN_KEY";

	/**
	 * 在流式上下文写入 ServerWebExchange
	 * @param ctx 必填
	 * @param exchange 必填
	 * @param chain 非必填
	 * @return /
	 */
	public static Context setContext(Context ctx, ServerWebExchange exchange, WebFilterChain chain) {
		return ctx
				.put(EXCHANGE_KEY, exchange)
				.put(CHAIN_KEY, chain);
	}

	/**
	 * 在流式上下文获取 ServerWebExchange
	 * @param ctx /
	 * @return /
	 */
	public static ServerWebExchange getExchange(ContextView ctx) {
		return ctx.get(EXCHANGE_KEY);
	}

	/**
	 * 在流式上下文获取 WebFilterChain
	 * @param ctx /
	 * @return /
	 */
	public static WebFilterChain getChain(ContextView ctx) {
		return ctx.get(CHAIN_KEY);
	}

	/**
	 * 获取 Mono < ServerWebExchange >
	 * @return /
	 */
	public static Mono<ServerWebExchange> getMonoExchange() {
		return Mono.deferContextual(ctx -> Mono.just(getExchange(ctx)));
	}

	/**
	 * 将 exchange 写入到同步上下文中，并执行一段代码，执行完毕清除上下文
	 *
	 * @return /
	 */
	public static <R> Mono<R> sync(SaRetGenericFunction<R> fun) {
		return Mono.deferContextual(ctx -> {
			try {
				SaReactorSyncHolder.setContext(ctx.get(EXCHANGE_KEY));
				return Mono.just(fun.run());
			} finally {
				SaReactorSyncHolder.clearContext();
			}
		});
	}

}

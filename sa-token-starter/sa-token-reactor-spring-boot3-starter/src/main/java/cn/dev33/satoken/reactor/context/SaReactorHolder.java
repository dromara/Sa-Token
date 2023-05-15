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

import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Reactor 上下文操作（异步），持有当前请求的 ServerWebExchange 全局引用
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaReactorHolder {
	
	/**
	 * key 
	 */
	public static final Class<ServerWebExchange> CONTEXT_KEY = ServerWebExchange.class;

	/**
	 * chain_key 
	 */
	public static final String CHAIN_KEY = "WEB_FILTER_CHAIN_KEY";
	
	/**
	 * 获取上下文对象 
	 * @return see note 
	 */
	public static Mono<ServerWebExchange> getContext() {
		// 从全局 Mono<Context> 获取 
		return Mono.deferContextual(Mono::just).map(ctx -> ctx.get(CONTEXT_KEY));
	}
	
	/**
	 * 获取上下文对象, 并设置到同步上下文中 
	 * @return see note 
	 */
	public static Mono<ServerWebExchange> getContextAndSetSync() {
		// 从全局 Mono<Context> 获取 
		return Mono.deferContextual(Mono::just).map(ctx -> {
			// 设置到sync中 
			SaReactorSyncHolder.setContext(ctx.get(CONTEXT_KEY));
			return ctx.get(CONTEXT_KEY);
		}).doFinally(r->{
			// 从sync中清除 
			SaReactorSyncHolder.clearContext();
		});
	}

}

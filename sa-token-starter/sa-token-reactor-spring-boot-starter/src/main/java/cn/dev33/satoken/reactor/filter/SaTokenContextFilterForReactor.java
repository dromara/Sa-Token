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
package cn.dev33.satoken.reactor.filter;

import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.util.SaTokenConsts;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * SaTokenContext 上下文初始化过滤器 (基于 Reactor)
 *
 * @author click33
 * @since 1.42.0
 */
@Order(SaTokenConsts.SA_TOKEN_CONTEXT_FILTER_ORDER)
public class SaTokenContextFilterForReactor implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
			return chain.filter(exchange)
					.contextWrite(ctx -> SaReactorHolder.setContext(ctx, exchange, chain))
					.doFinally(r -> {
						// 在流式上下文中保存的数据会随着流式操作的结束而销毁，所以此处无需手动清除数据
					});
	}

}


	/*
	 * 这种写法有问题：
			@Override
			public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
				try {
					SaReactorSyncHolder.setContext(exchange);
					return chain.filter(exchange);
				} finally {
					SaReactorSyncHolder.clearContext();
				}
			}
		这种写法会先执行 finally，然后进入具体的 Controller
	 */

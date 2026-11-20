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
package cn.dev33.satoken.reactor.filter;

import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.util.SaTokenConsts;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * SaTokenContext 上下文初始化过滤器 (基于 Reactor)
 *
 * <p>
 * 	除向 Reactor 流式上下文写入 exchange 外，还将请求上下文绑定到当前线程，
 * 	使阻塞式端点（含虚拟线程阻塞执行桥）与 AOP 注解鉴权可以同步读取上下文；
 * 	请求结束时按 Box 身份清除，避免 HTTP/2 同连接并发流之间互相覆盖上下文
 * </p>
 *
 * @author click33
 * @since 1.42.0
 */
@Order(SaTokenConsts.SA_TOKEN_CONTEXT_FILTER_ORDER)
public class SaTokenContextFilterForReactor implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return Mono.defer(() -> {
			// 将请求上下文绑定到当前处理线程（幂等覆盖），并记录本请求的 Box 用于结束时条件清除
			SaReactorSyncHolder.setContext(exchange);
			SaTokenContextModelBox box = SaReactorSyncHolder.getCurrentBoxOrNull();
			return chain.filter(exchange)
					.contextWrite(ctx -> SaReactorHolder.setContext(ctx, exchange, chain))
					.doFinally(r -> {
						// 仅当当前线程的 Box 仍是本请求的 Box 时才清除，流式上下文中的数据随订阅结束自行销毁
						SaReactorSyncHolder.clearContextIfCurrent(box);
					});
		});
	}

}

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

import cn.dev33.satoken.exception.RequestPathInvalidException;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 校验请求 path 是否合法
 *
 * @author click33
 * @since 1.37.0
 */
@Order(SaTokenConsts.PATH_CHECK_FILTER_ORDER)
public class SaPathCheckFilterForReactor implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		// 校验本次请求 path 是否合法
		try {
			SaStrategy.instance.checkRequestPath.run(exchange.getRequest().getPath().toString(), exchange, null);
		} catch (RequestPathInvalidException e) {
			if(SaStrategy.instance.requestPathInvalidHandle == null) {
				exchange.getResponse().getHeaders().set(SaTokenConsts.CONTENT_TYPE_KEY, SaTokenConsts.CONTENT_TYPE_TEXT_PLAIN);
				return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(e.getMessage().getBytes())));
			} else {
				SaStrategy.instance.requestPathInvalidHandle.run(e, exchange, null);
			}
			return Mono.empty();
		}

		// 向下执行
		return chain.filter(exchange);
	}

}

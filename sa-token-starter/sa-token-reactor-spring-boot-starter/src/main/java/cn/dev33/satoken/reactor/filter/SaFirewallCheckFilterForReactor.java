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

import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.reactor.model.SaRequestForReactor;
import cn.dev33.satoken.reactor.model.SaResponseForReactor;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.util.SaTokenConsts;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 防火墙校验过滤器 (Reactor版)
 *
 * @author click33
 * @since 1.37.0
 */
@Order(SaTokenConsts.FIREWALL_CHECK_FILTER_ORDER)
public class SaFirewallCheckFilterForReactor implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		SaRequestForReactor saRequest = new SaRequestForReactor(exchange.getRequest());
		SaResponseForReactor saResponse = new SaResponseForReactor(exchange.getResponse());

		try {
			SaFirewallStrategy.instance.check.execute(saRequest, saResponse, exchange);
		}
		catch (StopMatchException e) {
			// 如果是 StopMatchException 异常，代表通过了防火墙验证，进入 Controller
		}
		catch (FirewallCheckException e) {
			// FirewallCheckException 异常则交由异常处理策略处理
			if(SaFirewallStrategy.instance.checkFailHandle == null) {
				exchange.getResponse().getHeaders().set(SaTokenConsts.CONTENT_TYPE_KEY, SaTokenConsts.CONTENT_TYPE_TEXT_PLAIN);
				return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(e.getMessage().getBytes())));
			} else {
				SaFirewallStrategy.instance.checkFailHandle.run(e, saRequest, saResponse, null);
				return Mono.empty();
			}
		}
		// 更多异常则不处理，交由 Web 框架处理

		// 向下执行
		return chain.filter(exchange);
	}

}

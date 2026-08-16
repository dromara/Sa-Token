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
package cn.dev33.satoken.reactor.spring;

import cn.dev33.satoken.reactor.filter.SaFirewallCheckFilterForReactor;
import cn.dev33.satoken.reactor.filter.SaTokenContextFilterForReactor;
import cn.dev33.satoken.reactor.filter.SaTokenCorsFilterForReactor;
import cn.dev33.satoken.reactor.model.SaRequestForReactor;
import cn.dev33.satoken.reactor.model.SaResponseForReactor;
import cn.dev33.satoken.reactor.model.SaStorageForReactor;
import cn.dev33.satoken.spring.pathmatch.SaPathPatternParserUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

/**
 * 注册 Sa-Token 所需要的 Bean
 *
 * @author click33
 * @since 1.34.0
 */
public class SaTokenContextRegister {

	public SaTokenContextRegister() {
		// 重写路由匹配算法
		SaStrategy.instance.routeMatcher = (pattern, path) -> {
			return SaPathPatternParserUtil.match(pattern, path);
		};
		// 重写 SaRequest 创建策略
		SaStrategy.instance.createSaRequest = source -> new SaRequestForReactor((ServerHttpRequest) source);
		// 重写 SaResponse 创建策略
		SaStrategy.instance.createSaResponse = source -> new SaResponseForReactor((ServerHttpResponse) source);
		// 重写 SaStorage 创建策略
		SaStrategy.instance.createSaStorage = source -> new SaStorageForReactor((ServerWebExchange) source);
	}

	/**
	 * 上下文过滤器
	 *
	 * @return /
	 */
	@Bean
	public SaTokenContextFilterForReactor saTokenContextFilterForServlet() {
		return new SaTokenContextFilterForReactor();
	}

	/**
	 * CORS 跨域策略过滤器
	 *
	 * @return /
	 */
	@Bean
	public SaTokenCorsFilterForReactor saTokenCorsFilterForReactor() {
		return new SaTokenCorsFilterForReactor();
	}

	/**
	 * 防火墙过滤器
	 *
	 * @return /
	 */
	@Bean
	public SaFirewallCheckFilterForReactor saFirewallCheckFilterForReactor() {
		return new SaFirewallCheckFilterForReactor();
	}

}

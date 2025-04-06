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
package cn.dev33.satoken.reactor.util;

import cn.dev33.satoken.util.SaTokenConsts;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactor 操作工具类
 *
 * @author click33
 * @since 1.42.0
 */
public class SaReactorOperateUtil {

	/**
	 * 写入结果到输出流
	 * @param exchange /
	 * @param result /
	 * @return /
	 */
	public static Mono<Void> writeResult(ServerWebExchange exchange, String result) {
		// 写入输出流
		// 		请注意此处默认 Content-Type 为 text/plain，如果需要返回 JSON 信息，需要在 return 前自行设置 Content-Type 为 application/json
		// 		例如：SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
		if(exchange.getResponse().getHeaders().getFirst(SaTokenConsts.CONTENT_TYPE_KEY) == null) {
			exchange.getResponse().getHeaders().set(SaTokenConsts.CONTENT_TYPE_KEY, SaTokenConsts.CONTENT_TYPE_TEXT_PLAIN);
		}
		return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(result.getBytes())));
	}

}

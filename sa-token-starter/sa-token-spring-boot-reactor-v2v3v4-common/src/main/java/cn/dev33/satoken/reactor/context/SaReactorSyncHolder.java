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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.fun.SaRetGenericFunction;
import cn.dev33.satoken.reactor.model.SaRequestForReactor;
import cn.dev33.satoken.reactor.model.SaResponseForReactor;
import cn.dev33.satoken.reactor.model.SaStorageForReactor;
import org.springframework.web.server.ServerWebExchange;

/**
 * Reactor上下文操作（同步），持有当前请求的 ServerWebExchange 全局引用
 *
 * @author click33
 * @since 1.19.0
 */
public class SaReactorSyncHolder {

	/**
	 * 在同步上下文写入 ServerWebExchange
	 * @param exchange /
	 */
	public static void setContext(ServerWebExchange exchange) {
		SaRequest request = new SaRequestForReactor(exchange.getRequest());
		SaResponse response = new SaResponseForReactor(exchange.getResponse());
		SaStorage storage = new SaStorageForReactor(exchange);
		SaManager.getSaTokenContext().setContext(request, response, storage);
	}

	/**
	 * 在同步上下文清除 ServerWebExchange
	 */
	public static void clearContext() {
		SaManager.getSaTokenContext().clearContext();
	}

	/**
	 * 在同步上下文获取 ServerWebExchange
	 * @return /
	 */
	public static ServerWebExchange getExchange() {
		SaTokenContextModelBox box = SaManager.getSaTokenContext().getModelBox();
		return (ServerWebExchange)box.getStorage().getSource();
	}

	/**
	 * 将 exchange 写入到同步上下文中，并执行一段代码，执行完毕清除上下文
	 * @param exchange /
	 * @param fun /
	 */
	public static <R>R setContext(ServerWebExchange exchange, SaRetGenericFunction<R> fun) {
		try {
			setContext(exchange);
			return fun.run();
		} finally {
			clearContext();
		}
	}

}

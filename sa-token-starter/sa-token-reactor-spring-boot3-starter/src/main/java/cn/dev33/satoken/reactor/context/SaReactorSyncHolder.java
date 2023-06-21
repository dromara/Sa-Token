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

import cn.dev33.satoken.context.SaTokenContextForThreadLocalStorage;
import cn.dev33.satoken.context.SaTokenContextForThreadLocalStorage.Box;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.reactor.model.SaRequestForReactor;
import cn.dev33.satoken.reactor.model.SaResponseForReactor;
import cn.dev33.satoken.reactor.model.SaStorageForReactor;

/**
 * Reactor上下文操作（同步），持有当前请求的 ServerWebExchange 全局引用
 *
 * @author click33
 * @since 1.34.0
 */
public class SaReactorSyncHolder {
	
	/**
	 * 写入上下文对象 
	 * @param exchange see note 
	 */
	public static void setContext(ServerWebExchange exchange) {
		SaRequest request = new SaRequestForReactor(exchange.getRequest());
		SaResponse response = new SaResponseForReactor(exchange.getResponse());
		SaStorage storage = new SaStorageForReactor(exchange);
		SaTokenContextForThreadLocalStorage.setBox(request, response, storage);
	}
	
	/**
	 * 获取上下文对象 
	 * @return see note 
	 */
	public static ServerWebExchange getContext() {
		Box box = SaTokenContextForThreadLocalStorage.getBoxNotNull();
		return (ServerWebExchange)box.getStorage().getSource();
	}
	
	/**
	 * 清除上下文对象
	 */
	public static void clearContext() {
		SaTokenContextForThreadLocalStorage.clearBox();
	}
	
	/**
	 * 写入上下文对象, 并在执行函数后将其清除  
	 * @param exchange see note 
	 * @param fun see note 
	 */
	public static void setContext(ServerWebExchange exchange, SaFunction fun) {
		try {
			setContext(exchange);
			fun.run();
		} finally {
			clearContext();
		}
	}
	
}

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
package cn.dev33.satoken.reactor.context;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaTokenContextForThreadLocal;
import cn.dev33.satoken.context.SaTokenContextForThreadLocalStaff;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.model.SaTokenContextModelBox;
import cn.dev33.satoken.fun.SaRetGenericFunction;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.strategy.SaStrategy;
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
		SaRequest request = SaStrategy.instance.createSaRequest.apply(exchange.getRequest());
		SaResponse response = SaStrategy.instance.createSaResponse.apply(exchange.getResponse());
		SaStorage storage = SaStrategy.instance.createSaStorage.apply(exchange);
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
	 * 在同步上下文写入 ServerWebExchange，并返回写入前的旧 Box（用于嵌套场景恢复）
	 *
	 * <p> 当前 SaTokenContext 为 ThreadLocal 实现时返回旧 Box（可能为 null），否则返回 null </p>
	 *
	 * @param exchange /
	 * @return 绑定前的旧 Box，可能为 null
	 * @since 1.46.1
	 */
	public static SaTokenContextModelBox bindContext(ServerWebExchange exchange) {
		SaTokenContextModelBox prevBox = null;
		if(SaManager.getSaTokenContext() instanceof SaTokenContextForThreadLocal) {
			prevBox = SaTokenContextForThreadLocalStaff.getModelBoxOrNull();
		}
		setContext(exchange);
		return prevBox;
	}

	/**
	 * 恢复 {@link #bindContext} 返回的旧 Box；prevBox 为 null 时执行清除
	 * @param prevBox bindContext 返回的旧 Box
	 * @since 1.46.1
	 */
	public static void restoreContext(SaTokenContextModelBox prevBox) {
		if(prevBox != null) {
			SaTokenContextForThreadLocalStaff.setModelBoxRaw(prevBox);
		} else {
			clearContext();
		}
	}

	/**
	 * 读取当前线程的 Box；当前 SaTokenContext 非 ThreadLocal 实现时返回 null
	 * @return /
	 * @since 1.46.1
	 */
	public static SaTokenContextModelBox getCurrentBoxOrNull() {
		if(SaManager.getSaTokenContext() instanceof SaTokenContextForThreadLocal) {
			return SaTokenContextForThreadLocalStaff.getModelBoxOrNull();
		}
		return null;
	}

	/**
	 * 仅当当前线程的 Box 恰好是 box 时才清除（避免并发场景下误删其它请求的上下文）
	 * @param box 期望被清除的 Box
	 * @since 1.46.1
	 */
	public static void clearContextIfCurrent(SaTokenContextModelBox box) {
		if(SaManager.getSaTokenContext() instanceof SaTokenContextForThreadLocal) {
			SaTokenContextForThreadLocalStaff.clearModelBoxIfCurrent(box);
		}
	}

	/**
	 * 将 exchange 写入到同步上下文中，并执行一段代码，执行完毕恢复上下文
	 * @param exchange /
	 * @param fun /
	 */
	public static <R>R setContext(ServerWebExchange exchange, SaRetGenericFunction<R> fun) {
		SaTokenContextModelBox prevBox = bindContext(exchange);
		try {
			return fun.run();
		} finally {
			restoreContext(prevBox);
		}
	}

}

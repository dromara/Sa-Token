package cn.dev33.satoken.reactor.context;

import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

/**
 * Reactor上下文操作 [异步] 
 * @author click33
 *
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
		return Mono.subscriberContext().map(ctx -> ctx.get(CONTEXT_KEY));
	}
	
	/**
	 * 获取上下文对象, 并设置到同步上下文中 
	 * @return see note 
	 */
	public static Mono<ServerWebExchange> getContextAndSetSync() {
		// 从全局 Mono<Context> 获取 
		return Mono.subscriberContext().map(ctx -> {
			// 设置到sync中 
			SaReactorSyncHolder.setContext(ctx.get(CONTEXT_KEY));
			return ctx.get(CONTEXT_KEY);
		}).doFinally(r->{
			// 从sync中清除 
			SaReactorSyncHolder.clearContext();
		});
	}

}

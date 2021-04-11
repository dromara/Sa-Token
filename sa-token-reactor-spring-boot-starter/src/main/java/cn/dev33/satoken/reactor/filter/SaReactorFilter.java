package cn.dev33.satoken.reactor.filter;

import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.util.SaTokenConsts;
import reactor.core.publisher.Mono;

/**
 * Reactor全局过滤器 
 * @author kong
 *
 */
@Order(SaTokenConsts.ASSEMBLY_ORDER)
public class SaReactorFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		// ---------- 全局认证处理 
		try {
			// 写入全局上下文 (同步) 
			SaReactorSyncHolder.setContent(exchange);
			
			// 执行全局过滤器 
			SaTokenManager.getSaFilterStrategy().run(null);
			
		} catch (Throwable e) {
			// 1. 获取异常处理策略结果 
			Object result = SaTokenManager.getSaFilterErrorStrategy().run(e);
			String resultString = String.valueOf(result);
			
			// 2. 写入输出流
			if(exchange.getResponse().getHeaders().getFirst("Content-Type") == null) {
				exchange.getResponse().getHeaders().set("Content-Type", "text/plain; charset=utf-8");
			}
			return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(resultString.getBytes())));
			
		} finally {
			// 清除上下文 
			SaReactorSyncHolder.clearContent();
		}

		// ---------- 执行

		// 写入全局上下文 (同步) 
		SaReactorSyncHolder.setContent(exchange);
		
		// 执行 
		return chain.filter(exchange).subscriberContext(ctx -> {
			// 写入全局上下文 (异步) 
			ctx = ctx.put(SaReactorHolder.CONTEXT_KEY, exchange);
			return ctx;
		}).doFinally(r -> {
			// 清除上下文 
			SaReactorSyncHolder.clearContent();
		});
	}
	
	
}

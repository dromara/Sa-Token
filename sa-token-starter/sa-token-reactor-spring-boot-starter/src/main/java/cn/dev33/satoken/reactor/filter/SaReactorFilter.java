package cn.dev33.satoken.reactor.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.dev33.satoken.filter.SaFilter;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import cn.dev33.satoken.filter.SaFilterErrorStrategy;
import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.reactor.error.SaReactorSpringBootErrorCode;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.util.SaTokenConsts;
import reactor.core.publisher.Mono;

/**
 * Reactor全局过滤器 
 * @author click33
 *
 */
@Order(SaTokenConsts.ASSEMBLY_ORDER)
public class SaReactorFilter implements SaFilter, WebFilter {

	// ------------------------ 设置此过滤器 拦截 & 放行 的路由 

	/**
	 * 拦截路由 
	 */
	public List<String> includeList = new ArrayList<>();

	/**
	 * 放行路由 
	 */
	public List<String> excludeList = new ArrayList<>();

	@Override
	public SaReactorFilter addInclude(String... paths) {
		includeList.addAll(Arrays.asList(paths));
		return this;
	}

	@Override
	public SaReactorFilter addExclude(String... paths) {
		excludeList.addAll(Arrays.asList(paths));
		return this;
	}

	@Override
	public SaReactorFilter setIncludeList(List<String> pathList) {
		includeList = pathList;
		return this;
	}

	@Override
	public SaReactorFilter setExcludeList(List<String> pathList) {
		excludeList = pathList;
		return this;
	}


	// ------------------------ 钩子函数
	
	/**
	 * 认证函数：每次请求执行 
	 */
	public SaFilterAuthStrategy auth = r -> {};

	/**
	 * 异常处理函数：每次[认证函数]发生异常时执行此函数
	 */
	public SaFilterErrorStrategy error = e -> {
		throw new SaTokenException(e).setCode(SaReactorSpringBootErrorCode.CODE_20205);
	};

	/**
	 * 前置函数：在每次[认证函数]之前执行
	 *      <b>注意点：前置认证函数将不受 includeList 与 excludeList 的限制，所有路由的请求都会进入 beforeAuth</b>
	 */
	public SaFilterAuthStrategy beforeAuth = r -> {};

	@Override
	public SaReactorFilter setAuth(SaFilterAuthStrategy auth) {
		this.auth = auth;
		return this;
	}

	@Override
	public SaReactorFilter setError(SaFilterErrorStrategy error) {
		this.error = error;
		return this;
	}

	@Override
	public SaReactorFilter setBeforeAuth(SaFilterAuthStrategy beforeAuth) {
		this.beforeAuth = beforeAuth;
		return this;
	}

	
	// ------------------------ filter

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		
		// 写入WebFilterChain对象 
		exchange.getAttributes().put(SaReactorHolder.CHAIN_KEY, chain);
		
		// ---------- 全局认证处理 
		try {
			// 写入全局上下文 (同步) 
			SaReactorSyncHolder.setContext(exchange);
			
			// 执行全局过滤器
			beforeAuth.run(null);
			SaRouter.match(includeList).notMatch(excludeList).check(r -> {
				auth.run(null);
			});
			
		} catch (StopMatchException e) {
			
		} catch (Throwable e) {
			// 1. 获取异常处理策略结果 
			String result = (e instanceof BackResultException) ? e.getMessage() : String.valueOf(error.run(e));
			
			// 2. 写入输出流
			if(exchange.getResponse().getHeaders().getFirst("Content-Type") == null) {
				exchange.getResponse().getHeaders().set("Content-Type", "text/plain; charset=utf-8");
			}
			return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(result.getBytes())));
			
		} finally {
			// 清除上下文 
			SaReactorSyncHolder.clearContext();
		}

		// ---------- 执行

		// 写入全局上下文 (同步) 
		SaReactorSyncHolder.setContext(exchange);
		
		// 执行 
		return chain.filter(exchange).subscriberContext(ctx -> {
			// 写入全局上下文 (异步) 
			ctx = ctx.put(SaReactorHolder.CONTEXT_KEY, exchange);
			return ctx;
		}).doFinally(r -> {
			// 清除上下文 
			SaReactorSyncHolder.clearContext();
		});
	}
	
}

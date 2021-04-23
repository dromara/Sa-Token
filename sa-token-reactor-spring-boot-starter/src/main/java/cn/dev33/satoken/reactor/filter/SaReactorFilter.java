package cn.dev33.satoken.reactor.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import cn.dev33.satoken.filter.SaFilterErrorStrategy;
import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.router.SaRouterUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import reactor.core.publisher.Mono;

/**
 * Reactor全局过滤器 
 * @author kong
 *
 */
@Order(SaTokenConsts.ASSEMBLY_ORDER)
public class SaReactorFilter implements WebFilter {

	// ------------------------ 设置此过滤器 拦截 & 放行 的路由 

	/**
	 * 拦截路由 
	 */
	private List<String> includeList = new ArrayList<>();

	/**
	 * 放行路由 
	 */
	private List<String> excludeList = new ArrayList<>();

	/**
	 * 添加 [拦截路由] 
	 * @param paths 路由
	 * @return 对象自身
	 */
	public SaReactorFilter addInclude(String... paths) {
		includeList.addAll(Arrays.asList(paths));
		return this;
	}
	
	/**
	 * 添加 [放行路由]
	 * @param paths 路由
	 * @return 对象自身
	 */
	public SaReactorFilter addExclude(String... paths) {
		excludeList.addAll(Arrays.asList(paths));
		return this;
	}

	/**
	 * 写入 [拦截路由] 集合
	 * @param pathList 路由集合 
	 * @return 对象自身
	 */
	public SaReactorFilter setIncludeList(List<String> pathList) {
		includeList = pathList;
		return this;
	}
	
	/**
	 * 写入 [放行路由] 集合
	 * @param pathList 路由集合 
	 * @return 对象自身
	 */
	public SaReactorFilter setExcludeList(List<String> pathList) {
		excludeList = pathList;
		return this;
	}
	
	/**
	 * 获取 [拦截路由] 集合
	 * @return see note 
	 */
	public List<String> getIncludeList() {
		return includeList;
	}
	
	/**
	 * 获取 [放行路由] 集合
	 * @return see note 
	 */
	public List<String> getExcludeList() {
		return excludeList;
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
		throw new SaTokenException(e);
	};

	/**
	 * 前置函数：在每次[认证函数]之前执行 
	 */
	public SaFilterAuthStrategy beforeAuth = r -> {};

	/**
	 * 写入[认证函数]: 每次请求执行 
	 * @param auth see note 
	 * @return 对象自身
	 */
	public SaReactorFilter setAuth(SaFilterAuthStrategy auth) {
		this.auth = auth;
		return this;
	}

	/**
	 * 写入[异常处理函数]：每次[认证函数]发生异常时执行此函数 
	 * @param error see note 
	 * @return 对象自身
	 */
	public SaReactorFilter setError(SaFilterErrorStrategy error) {
		this.error = error;
		return this;
	}

	/**
	 * 写入[前置函数]：在每次[认证函数]之前执行
	 * @param beforeAuth see note 
	 * @return 对象自身
	 */
	public SaReactorFilter setBeforeAuth(SaFilterAuthStrategy beforeAuth) {
		this.beforeAuth = beforeAuth;
		return this;
	}

	
	// ------------------------ filter

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		// ---------- 全局认证处理 
		try {
			// 写入全局上下文 (同步) 
			SaReactorSyncHolder.setContent(exchange);
			
			// 执行全局过滤器 
			SaRouterUtil.match(includeList, excludeList, () -> {
				beforeAuth.run(null);
				auth.run(null);
			});
			
		} catch (Throwable e) {
			// 1. 获取异常处理策略结果 
			Object result = error.run(e);
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

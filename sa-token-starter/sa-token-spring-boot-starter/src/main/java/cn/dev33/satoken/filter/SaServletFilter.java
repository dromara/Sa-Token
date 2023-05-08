package cn.dev33.satoken.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.core.annotation.Order;

import cn.dev33.satoken.error.SaSpringBootErrorCode;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Servlet全局过滤器 
 * @author click33
 *
 */
@Order(SaTokenConsts.ASSEMBLY_ORDER)
public class SaServletFilter implements SaFilter, Filter {

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
	public SaServletFilter addInclude(String... paths) {
		includeList.addAll(Arrays.asList(paths));
		return this;
	}

	@Override
	public SaServletFilter addExclude(String... paths) {
		excludeList.addAll(Arrays.asList(paths));
		return this;
	}

	@Override
	public SaServletFilter setIncludeList(List<String> pathList) {
		includeList = pathList;
		return this;
	}

	@Override
	public SaServletFilter setExcludeList(List<String> pathList) {
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
		throw new SaTokenException(e).setCode(SaSpringBootErrorCode.CODE_20105);
	};

	/**
	 * 前置函数：在每次[认证函数]之前执行
	 *      <b>注意点：前置认证函数将不受 includeList 与 excludeList 的限制，所有路由的请求都会进入 beforeAuth</b>
	 */
	public SaFilterAuthStrategy beforeAuth = r -> {};

	@Override
	public SaServletFilter setAuth(SaFilterAuthStrategy auth) {
		this.auth = auth;
		return this;
	}

	@Override
	public SaServletFilter setError(SaFilterErrorStrategy error) {
		this.error = error;
		return this;
	}

	@Override
	public SaServletFilter setBeforeAuth(SaFilterAuthStrategy beforeAuth) {
		this.beforeAuth = beforeAuth;
		return this;
	}

	
	// ------------------------ doFilter

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		try {
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
			if(response.getContentType() == null) {
				response.setContentType("text/plain; charset=utf-8"); 
			}
			response.getWriter().print(result);
			return;
		}
		
		// 执行 
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	@Override
	public void destroy() {
	}

	
	
}

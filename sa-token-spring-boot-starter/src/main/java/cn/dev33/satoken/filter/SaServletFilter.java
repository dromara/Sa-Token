package cn.dev33.satoken.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.core.annotation.Order;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Servlet全局过滤器 
 * @author kong
 *
 */
@Order(SaTokenConsts.ASSEMBLY_ORDER)
public class SaServletFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		try {
			// 执行全局过滤器 
			SaTokenManager.getSaFilterStrategy().run(null);
			
		} catch (Throwable e) {
			// 1. 获取异常处理策略结果 
			Object result = SaTokenManager.getSaFilterErrorStrategy().run(e);
			String resultString = String.valueOf(result);
			
			// 2. 写入输出流 
			response.setContentType("text/plain; charset=utf-8"); 
			response.getWriter().print(resultString);
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

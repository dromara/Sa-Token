package com.pj.satoken;

import cn.dev33.satoken.servlet.util.SaTokenContextServletUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sa-Token 上下文初始化过滤器（SSM / 非 SpringBoot 场景）
 * <p> 逻辑与 {@link cn.dev33.satoken.filter.SaTokenContextFilterForServlet} 一致；
 * 放在 demo 工程内注册，避免 web.xml 直接引用 starter 中的 Filter 时与 Tomcat 类加载器冲突。 </p>
 */
public class SaTokenContextFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			SaTokenContextServletUtil.setContext((HttpServletRequest) request, (HttpServletResponse) response);
			chain.doFilter(request, response);
		} finally {
			SaTokenContextServletUtil.clearContext();
		}
	}

	@Override
	public void destroy() {
	}

}

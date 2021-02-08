package com.pj;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * 跨域过滤器
 * @author kong 
 */
@Component
public class CorsFilter implements Filter {

	static final String OPTIONS = "OPTIONS";

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		// 获得客户端domain
		String origin = request.getHeader("Origin");
		if (origin == null) {
			origin = request.getHeader("Referer");
		}
		// 允许指定域访问跨域资源
		response.setHeader("Access-Control-Allow-Origin", origin);
		// 允许客户端携带跨域cookie，此时origin值不能为“*”，只能为指定单一域名
		response.setHeader("Access-Control-Allow-Credentials", "true");
		// 允许所有请求方式
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		// 有效时间
		response.setHeader("Access-Control-Max-Age", "3600");
		// 允许的header参数
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with,satoken");
		// 允许的header参数
//      response.setHeader("Access-Control-Allow-Headers", "*");  

		// 如果是预检请求，直接返回
		if (OPTIONS.equals(request.getMethod())) {
			System.out.println("=======================浏览器发来了OPTIONS预检请求==========");
			response.getWriter().print("");
			return;
		}

		// System.out.println("*********************************过滤器被使用**************************2233");
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}

}

package cn.dev33.satoken.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import cn.dev33.satoken.context.model.servlet.SaRequestForServlet;
import cn.dev33.satoken.context.model.servlet.SaResponseForServlet;
import cn.dev33.satoken.router.SaRouteFunction;
import cn.dev33.satoken.stp.StpUtil;

/**
 * sa-token基于路由的拦截式鉴权 
 * @author kong
 */
public class SaRouteInterceptor implements HandlerInterceptor {

	/**
	 * 每次进入拦截器的[执行函数]
	 */
	public SaRouteFunction function;

	/**
	 * 创建一个路由拦截器
	 */
	public SaRouteInterceptor() {
	}

	/**
	 * 创建, 并指定[执行函数]
	 * @param function [执行函数]
	 */
	public SaRouteInterceptor(SaRouteFunction function) {
		this.function = function;
	}

	/**
	 * 静态方法快速构建一个 
	 * @param function 自定义模式下的执行函数
	 * @return sa路由拦截器 
	 */
	public static SaRouteInterceptor newInstance(SaRouteFunction function) {
		return new SaRouteInterceptor(function);
	}
	
	
	// ----------------- 验证方法 ----------------- 

	/**
	 * 每次请求之前触发的方法 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		// 如果未提供function，默认进行登录验证 
		if(function == null) {
			StpUtil.checkLogin();
		} else {
			// 否则执行函数 
			function.run(new SaRequestForServlet(request), new SaResponseForServlet(response), handler);
		}
		
		// 通过验证 
		return true;
	}

	
}

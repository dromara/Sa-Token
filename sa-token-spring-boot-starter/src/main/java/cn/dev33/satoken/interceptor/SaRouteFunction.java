package cn.dev33.satoken.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 执行验证方法的辅助类
 * 
 * @author kong
 *
 */
public interface SaRouteFunction {

	/**
	 * 执行验证的方法
	 * 
	 * @param request  request对象
	 * @param response response对象
	 * @param handler  处理对象
	 */
	public void run(HttpServletRequest request, HttpServletResponse response, Object handler);

}

package cn.dev33.satoken.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 执行验证方法的辅助类
 * @author kong
 *
 */
public interface SaFunction {

	/**
	 * 执行验证的方法 
	 */
	public void run(HttpServletRequest request, HttpServletResponse response, Object handler);

}

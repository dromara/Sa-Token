package cn.dev33.satoken.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet相关操作接口
 * 
 * @author kong
 *
 */
public interface SaTokenServlet {

	/**
	 * 获取当前请求的 Request 对象
	 * 
	 * @return 当前请求的Request对象
	 */
	public HttpServletRequest getRequest();

	/**
	 * 获取当前请求的 Response 对象
	 * 
	 * @return 当前请求的response对象
	 */
	public HttpServletResponse getResponse();

}

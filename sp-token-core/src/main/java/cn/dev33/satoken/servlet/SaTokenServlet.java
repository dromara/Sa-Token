package cn.dev33.satoken.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet相关操作 
 * @author kong
 *
 */
public interface SaTokenServlet {


	/**
	 * 获取当前请求的Request对象
	 * @return 当前请求的Request对象
	 */
	public HttpServletRequest getRequest();
	
	/**
	 * 获取当前会话的  response
	 * @return 当前请求的response
	 */
	public HttpServletResponse getResponse();
	
	
}

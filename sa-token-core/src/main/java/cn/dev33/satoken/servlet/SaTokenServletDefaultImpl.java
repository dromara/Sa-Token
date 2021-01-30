package cn.dev33.satoken.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.exception.SaTokenException;

/**
 * sa-token 对SaTokenServlet接口默认实现类
 * 
 * @author kong
 *
 */
public class SaTokenServletDefaultImpl implements SaTokenServlet {

	/**
	 * 获取当前请求的Request对象
	 */
	@Override
	public HttpServletRequest getRequest() {
		throw new SaTokenException("请实现SaTokenServlet接口后进行Servlet相关操作");
	}

	/**
	 * 获取当前请求的Response对象
	 */
	@Override
	public HttpServletResponse getResponse() {
		throw new SaTokenException("请实现SaTokenServlet接口后进行Servlet相关操作");
	}

}

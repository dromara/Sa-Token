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
		throw new SaTokenException("SaTokenServlet接口未实现");
	}

	/**
	 * 获取当前请求的Response对象
	 */
	@Override
	public HttpServletResponse getResponse() {
		throw new SaTokenException("SaTokenServlet接口未实现");
	}

	
	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径 
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		throw new SaTokenException("SaTokenServlet接口未实现");
	}

}

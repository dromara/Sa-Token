package cn.dev33.satoken.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.servlet.SaTokenServlet;

/**
 * sa-token 对cookie的相关操作 接口实现类 
 * @author kong
 *
 */
public class SaTokenServletSpringImpl implements SaTokenServlet {

	/**
	 * 获取当前请求的Request对象 
	 */
	@Override
	public HttpServletRequest getRequest() {
		return SpringMVCUtil.getRequest();
	}


	/**
	 * 获取当前请求的Response对象 
	 */
	@Override
	public HttpServletResponse getResponse() {
		return SpringMVCUtil.getResponse();
	}
	
}

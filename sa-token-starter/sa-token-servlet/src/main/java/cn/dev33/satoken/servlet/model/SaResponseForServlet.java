package cn.dev33.satoken.servlet.model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Response for Servlet
 * @author kong
 *
 */
public class SaResponseForServlet implements SaResponse {

	/**
	 * 底层Request对象
	 */
	HttpServletResponse response;
	
	/**
	 * 实例化
	 * @param response response对象 
	 */
	public SaResponseForServlet(HttpServletResponse response) {
		this.response = response;
	}
	
	/**
	 * 获取底层源对象 
	 */
	@Override
	public Object getSource() {
		return response;
	}

	/**
	 * 删除指定Cookie 
	 */
	@Override
	public void deleteCookie(String name) {
		addCookie(name, null, null, null, 0);
	}

	/**
	 * 写入指定Cookie 
	 */
	@Override
	public void addCookie(String name, String value, String path, String domain, int timeout) {
		Cookie cookie = new Cookie(name, value);
		if(SaFoxUtil.isEmpty(path) == true) {
			path = "/";
		}
		if(SaFoxUtil.isEmpty(domain) == false) {
			cookie.setDomain(domain);
		}
		cookie.setPath(path);
		cookie.setMaxAge(timeout);
		response.addCookie(cookie);
	}

	
	/**
	 * 在响应头里写入一个值 
	 */
	@Override
	public SaResponse setHeader(String name, String value) {
		response.setHeader(name, value);
		return this;
	}

}

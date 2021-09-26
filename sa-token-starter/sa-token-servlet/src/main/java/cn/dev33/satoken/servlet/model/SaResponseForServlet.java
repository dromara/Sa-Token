package cn.dev33.satoken.servlet.model;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.SaTokenException;
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
	protected HttpServletResponse response;
	
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
		addCookie(name, null, null, null, 0, false, false);
	}

	/**
	 * 写入指定Cookie
	 */
	@Override
	public void addCookie(String name, String value, String path, String domain, int timeout, boolean isHttpOnly, boolean isSecure) {
		Cookie cookie = new Cookie(name, value);
		if(SaFoxUtil.isEmpty(path) == true) {
			path = "/";
		}
		if(SaFoxUtil.isEmpty(domain) == false) {
			cookie.setDomain(domain);
		}
		cookie.setPath(path);
		cookie.setMaxAge(timeout);
		cookie.setHttpOnly(isHttpOnly);
		cookie.setSecure(isSecure);
		response.addCookie(cookie);
	}

	/**
	 * 设置响应状态码 
	 */
	@Override
	public SaResponse setStatus(int sc) {
		response.setStatus(sc);
		return this;
	}
	
	/**
	 * 在响应头里写入一个值 
	 */
	@Override
	public SaResponse setHeader(String name, String value) {
		response.setHeader(name, value);
		return this;
	}

	/**
	 * 重定向 
	 */
	@Override
	public Object redirect(String url) {
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			throw new SaTokenException(e);
		}
		return null;
	}

	
}

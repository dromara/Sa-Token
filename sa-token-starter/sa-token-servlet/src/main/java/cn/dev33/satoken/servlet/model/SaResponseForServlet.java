package cn.dev33.satoken.servlet.model;

import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.servlet.error.SaServletErrorCode;

/**
 * Response for Servlet
 * @author click33
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
	 * 在响应头里添加一个值 
	 * @param name 名字
	 * @param value 值 
	 * @return 对象自身 
	 */
	public SaResponse addHeader(String name, String value) {
		response.addHeader(name, value);
		return this;
	}
	
	/**
	 * 重定向 
	 */
	@Override
	public Object redirect(String url) {
		try {
			response.sendRedirect(url);
		} catch (Exception e) {
			throw new SaTokenException(e).setCode(SaServletErrorCode.CODE_20002);
		}
		return null;
	}

	
}

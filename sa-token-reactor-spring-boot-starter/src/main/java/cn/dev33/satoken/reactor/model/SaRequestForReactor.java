package cn.dev33.satoken.reactor.model;


import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;

import cn.dev33.satoken.context.model.SaRequest;

/**
 * Request for Reactor 
 * @author kong
 *
 */
public class SaRequestForReactor implements SaRequest {

	/**
	 * 底层Request对象
	 */
	ServerHttpRequest request;
	
	/**
	 * 实例化
	 * @param request request对象 
	 */
	public SaRequestForReactor(ServerHttpRequest request) {
		this.request = request;
	}
	
	/**
	 * 获取底层源对象 
	 */
	@Override
	public Object getSource() {
		return request;
	}

	/**
	 * 在 [请求体] 里获取一个值 
	 */
	@Override
	public String getParameter(String name) {
		return request.getQueryParams().getFirst(name);
	}

	/**
	 * 在 [请求头] 里获取一个值 
	 */
	@Override
	public String getHeader(String name) {
		return request.getHeaders().getFirst(name);
	}

	/**
	 * 在 [Cookie作用域] 里获取一个值 
	 */
	@Override
	public String getCookieValue(String name) {
		HttpCookie cookie = request.getCookies().getFirst(name);
		if(cookie == null) {
			return null;
		}
		return cookie.getValue();
	}

	/**
	 * 返回当前请求的URL 
	 */
	@Override
	public String getRequestURI() {
		return request.getURI().getPath();
	}

}

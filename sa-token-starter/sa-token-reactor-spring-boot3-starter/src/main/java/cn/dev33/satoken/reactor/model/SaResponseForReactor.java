package cn.dev33.satoken.reactor.model;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;

import cn.dev33.satoken.context.model.SaResponse;

/**
 * Response for Reactor
 * @author kong
 *
 */
public class SaResponseForReactor implements SaResponse {

	/**
	 * 底层Response对象
	 */
	protected ServerHttpResponse response;
	
	/**
	 * 实例化
	 * @param response response对象 
	 */
	public SaResponseForReactor(ServerHttpResponse response) {
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
		response.setStatusCode(HttpStatus.valueOf(sc));
		return this;
	}
	
	/**
	 * 在响应头里写入一个值 
	 */
	@Override
	public SaResponse setHeader(String name, String value) {
		response.getHeaders().set(name, value);
		return this;
	}

	/**
	 * 在响应头里添加一个值 
	 * @param name 名字
	 * @param value 值 
	 * @return 对象自身 
	 */
	public SaResponse addHeader(String name, String value) {
		response.getHeaders().add(name, value);
		return this;
	}
	
	/**
	 * 重定向 
	 */
	@Override
	public Object redirect(String url) {
		response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create(url));
		return null;
	}

}

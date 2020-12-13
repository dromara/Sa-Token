package cn.dev33.satoken.action;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.util.SpringMvcUtil;

/**
 * 对 SaTokenAction 接口的默认实现 
 * @author kong
 *
 */
public class SaTokenActionDefaultImpl implements SaTokenAction {

	/**
	 * 获取当前请求的Request对象 
	 */
	@Override
	public HttpServletRequest getCurrRequest() {
		return SpringMvcUtil.getRequest();
	}


	/**
	 * 获取当前请求的Response对象 
	 */
	@Override
	public HttpServletResponse getResponse() {
		return SpringMvcUtil.getResponse();
	}
	
	
	/**
	 * 生成一个token 
	 */
	@Override
	public String createToken(Object loginId, String loginKey) {
		return UUID.randomUUID().toString();
	}


	
	
	
}

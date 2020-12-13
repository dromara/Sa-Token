package cn.dev33.satoken.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SaTokenAction {

	
	/**
	 * 获取当前请求的Request对象
	 * @return 当前请求的Request对象
	 */
	public HttpServletRequest getCurrRequest();
	
	/**
	 * 获取当前会话的  response
	 * @return
	 */
	public HttpServletResponse getResponse();
	
	/**
	 * 生成一个token 
	 * @param loginId 账号id
	 * @param loginKey 登录标识key 
	 * @return
	 */
	public String createToken(Object loginId, String loginKey); 
	
	
}

package com.pj.more;

public interface DemoService {
	
	/**
	 * 登录 
	 * @param loginId 账号id 
	 */
	void doLogin(Object loginId);
	
	/**
	 * 判断是否登录，打印状态 
	 */
	void isLogin(String str); 
	
}

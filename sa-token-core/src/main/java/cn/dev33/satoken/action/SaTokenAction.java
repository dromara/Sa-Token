package cn.dev33.satoken.action;

public interface SaTokenAction {

	
	/**
	 * 生成一个token 
	 * @param loginId 账号id
	 * @param loginKey 登录标识key 
	 * @return 一个token
	 */
	public String createToken(Object loginId, String loginKey); 
	
	
}

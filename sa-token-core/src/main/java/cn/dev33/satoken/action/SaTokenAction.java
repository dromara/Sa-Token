package cn.dev33.satoken.action;

/**
 * sa-token内置操作接口 
 * @author kong
 *
 */
public interface SaTokenAction {

	/**
	 * 生成一个token 
	 * @param loginId 账号id
	 * @param loginKey 账号标识key 
	 * @return 一个token
	 */
	public String createToken(Object loginId, String loginKey); 
	
		
}

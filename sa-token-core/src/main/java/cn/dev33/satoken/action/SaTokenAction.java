package cn.dev33.satoken.action;

/**
 * sa-token逻辑代理接口 
 * <p>此接口将会代理框架内部的一些关键性逻辑，方便开发者进行按需重写</p> 
 * @author kong
 *
 */
public interface SaTokenAction {

	/**
	 * 根据一定的算法生成一个token 
	 * @param loginId 账号id 
	 * @param loginKey 账号体系key 
	 * @return 一个token
	 */
	public String createToken(Object loginId, String loginKey); 
	
		
}

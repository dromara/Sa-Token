package cn.dev33.satoken.sso;

import cn.dev33.satoken.sso.SaSsoInterface.CallSloUrlFunction;

/**
 * Sa-Token-SSO 单点登录工具类 
 * @author kong
 *
 */
public class SaSsoUtil {

	/**
	 * 底层 SaSsoServerInterface 对象 
	 */
	public static SaSsoInterface saSsoInterface = new SaSsoInterface() {};
	
	/**
	 * 创建一个 Ticket票据 
	 * @param loginId 账号id 
	 * @return 票据 
	 */
	public static String createTicket(Object loginId) {
		return saSsoInterface.createTicket(loginId);
	}
	
	/**
	 * 删除一个 Ticket码
	 * @param ticket Ticket码
	 */
	public static void deleteTicket(String ticket) {
		saSsoInterface.deleteTicket(ticket);
	}

	/**
	 * 构建URL：Server端向Client下放ticke的地址
	 * @param loginId 账号id 
	 * @param redirect Client端提供的重定向地址
	 * @return see note 
	 */
	public static String buildRedirectUrl(Object loginId, String redirect) {
		return saSsoInterface.buildRedirectUrl(loginId, redirect);
	}
	
	/**
	 * 根据 Ticket码 获取账号id，如果Ticket码无效则返回null 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public static Object getLoginId(String ticket) {
		return saSsoInterface.getLoginId(ticket);
	}

	/**
	 * 根据 Ticket码 获取账号id，并转换为指定类型 
	 * @param <T> 要转换的类型 
	 * @param ticket Ticket码
	 * @param cs 要转换的类型 
	 * @return 账号id 
	 */
	public static <T> T getLoginId(String ticket, Class<T> cs) {
		return saSsoInterface.getLoginId(ticket, cs);
	}

	/**
	 * 校验ticket码，获取账号id，如果ticket可以有效，则立刻删除 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public static Object checkTicket(String ticket) {
		return saSsoInterface.checkTicket(ticket);
	}
	
	/**
	 * 校验重定向url合法性
	 * @param url 下放ticket的url地址 
	 */
	public static void checkAuthUrl(String url) {
		saSsoInterface.checkRedirectUrl(url);
	}

	/**
	 * 构建URL：Server端 单点登录地址
	 * @param clientLoginUrl Client端登录地址 
	 * @param back 回调路径 
	 * @return [SSO-Server端-认证地址 ]
	 */
	public static String buildServerAuthUrl(String clientLoginUrl, String back) {
		return saSsoInterface.buildServerAuthUrl(clientLoginUrl, back);
	}

	
	// ------------------- SSO 模式三 ------------------- 
	
	/**
	 * 校验secretkey秘钥是否有效 
	 * @param secretkey 秘钥 
	 */
	public static void checkSecretkey(String secretkey) {
		saSsoInterface.checkSecretkey(secretkey);
	}

	/**
	 * 构建URL：校验ticket的URL 
	 * @param ticket ticket码
	 * @param sloCallbackUrl 单点注销时的回调URL (如果不需要单点注销功能，此值可以填null)
	 * @return 构建完毕的URL 
	 */
	public static String buildCheckTicketUrl(String ticket, String sloCallbackUrl) {
		return saSsoInterface.buildCheckTicketUrl(ticket, sloCallbackUrl);
	}

	/**
	 * 为指定账号id注册单点注销回调URL 
	 * @param loginId 账号id
	 * @param sloCallbackUrl 单点注销时的回调URL 
	 */
	public static void registerSloCallbackUrl(Object loginId, String sloCallbackUrl) {
		saSsoInterface.registerSloCallbackUrl(loginId, sloCallbackUrl);
	}

	/**
	 * 循环调用Client端单点注销回调 
	 * @param loginId 账号id
	 * @param fun 调用方法 
	 */
	public static void forEachSloUrl(Object loginId, CallSloUrlFunction fun) {
		saSsoInterface.forEachSloUrl(loginId, fun);
	}

	/**
	 * 构建URL：单点注销URL 
	 * @param loginId 要注销的账号id
	 * @return 单点注销URL 
	 */
	public static String buildSloUrl(Object loginId) {
		return saSsoInterface.buildSloUrl(loginId);
	}

	/**
	 * 指定账号单点注销 
	 * @param secretkey 校验秘钥
	 * @param loginId 指定账号 
	 * @param fun 调用方法 
	 */
	public static void singleLogout(String secretkey, Object loginId, CallSloUrlFunction fun) {
		saSsoInterface.singleLogout(secretkey, loginId, fun); 
	}
	
}

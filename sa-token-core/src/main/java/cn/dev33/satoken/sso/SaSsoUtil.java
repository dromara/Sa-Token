package cn.dev33.satoken.sso;

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
	 * 根据 账号id & 重定向地址，构建[SSO-Client端-重定向地址] 
	 * @param loginId 账号id 
	 * @param redirect 重定向地址
	 * @return [SSO-Client端-重定向地址]
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
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public static <T> T getLoginId(String ticket, Class<T> cs) {
		return saSsoInterface.getLoginId(ticket, cs);
	}
	
	/**
	 * 校验url合法性
	 * @param url 地址
	 */
	public static void checkAuthUrl(String url) {
		saSsoInterface.checkAuthUrl(url);
	}

	/**
	 * 根据 Client端登录地址 & back地址 ，构建[SSO-Server端-认证地址] 
	 * @param clientLoginUrl Client端登录地址 
	 * @param back 回调路径 
	 * @return [SSO-Server端-认证地址 ]
	 */
	public static String buildServerAuthUrl(String clientLoginUrl, String back) {
		return saSsoInterface.buildServerAuthUrl(clientLoginUrl, back);
	}
	
}

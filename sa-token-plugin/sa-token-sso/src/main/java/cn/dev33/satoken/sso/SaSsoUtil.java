package cn.dev33.satoken.sso;

import cn.dev33.satoken.util.SaResult;

import java.util.Map;

/**
 * Sa-Token-SSO 单点登录模块 工具类 
 * @author click33
 *
 */
public class SaSsoUtil {

	/**
	 * 底层 SaSsoTemplate 对象 
	 */
	public static SaSsoTemplate ssoTemplate = new SaSsoTemplate();
	

	// ---------------------- Ticket 操作 ---------------------- 
	
	/**
	 * 根据 账号id 创建一个 Ticket码 
	 * @param loginId 账号id 
	 * @param client 客户端标识 
	 * @return Ticket码 
	 */
	public static String createTicket(Object loginId, String client) {
		return ssoTemplate.createTicket(loginId, client);
	}
	
	/**
	 * 删除 Ticket 
	 * @param ticket Ticket码
	 */
	public static void deleteTicket(String ticket) {
		ssoTemplate.deleteTicket(ticket);
	}
	
	/**
	 * 删除 Ticket索引 
	 * @param loginId 账号id 
	 */
	public static void deleteTicketIndex(Object loginId) {
		ssoTemplate.deleteTicketIndex(loginId);
	}

	/**
	 * 根据 Ticket码 获取账号id，如果Ticket码无效则返回null 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public static Object getLoginId(String ticket) {
		return ssoTemplate.getLoginId(ticket);
	}

	/**
	 * 根据 Ticket码 获取账号id，并转换为指定类型 
	 * @param <T> 要转换的类型 
	 * @param ticket Ticket码
	 * @param cs 要转换的类型 
	 * @return 账号id 
	 */
	public static <T> T getLoginId(String ticket, Class<T> cs) {
		return ssoTemplate.getLoginId(ticket, cs);
	}

	/**
	 * 校验 Ticket 码，获取账号id，如果此ticket是有效的，则立即删除 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public static Object checkTicket(String ticket) {
		return ssoTemplate.checkTicket(ticket);
	}
	
	/**
	 * 校验ticket码，获取账号id，如果此ticket是有效的，则立即删除 
	 * @param ticket Ticket码
	 * @param client client 标识 
	 * @return 账号id 
	 */
	public static Object checkTicket(String ticket, String client) {
		return ssoTemplate.checkTicket(ticket, client);
	}

	/**
	 * 获取：所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) 
	 * @return see note 
	 */
	public static String getAllowUrl() {
		return ssoTemplate.getAllowUrl();
	}

	/**
	 * 校验重定向url合法性
	 * @param url 下放ticket的url地址 
	 */
	public static void checkRedirectUrl(String url) {
		ssoTemplate.checkRedirectUrl(url);
	}

	
	// ------------------- SSO 模式三 ------------------- 
	
	/**
	 * 构建URL：校验ticket的URL 
	 * @param ticket ticket码
	 * @param ssoLogoutCallUrl 单点注销时的回调URL 
	 * @return 构建完毕的URL 
	 */
	public static String buildCheckTicketUrl(String ticket, String ssoLogoutCallUrl) {
		return ssoTemplate.buildCheckTicketUrl(ticket, ssoLogoutCallUrl);
	}

	/**
	 * 为指定账号id注册单点注销回调URL 
	 * @param loginId 账号id
	 * @param sloCallbackUrl 单点注销时的回调URL 
	 */
	public static void registerSloCallbackUrl(Object loginId, String sloCallbackUrl) {
		ssoTemplate.registerSloCallbackUrl(loginId, sloCallbackUrl);
	}

	/**
	 * 构建URL：单点注销URL 
	 * @param loginId 要注销的账号id
	 * @return 单点注销URL 
	 */
	public static String buildSloUrl(Object loginId) {
		return ssoTemplate.buildSloUrl(loginId);
	}

	/**
	 * 指定账号单点注销 
	 * @param loginId 指定账号 
	 */
	public static void ssoLogout(Object loginId) {
		ssoTemplate.ssoLogout(loginId);
	}

	/**
	 * 获取：查询数据
	 * @param paramMap 查询参数
	 * @return 查询结果
	 */
	public static Object getData(Map<String, Object> paramMap) {
		return ssoTemplate.getData(paramMap);
	}

	/**
	 * 根据自定义 path 查询数据 （此方法需要配置 sa-token.sso.server-url 地址）
	 * @param path 自定义 path
	 * @param paramMap 查询参数
	 * @return 查询结果
	 */
	public static Object getData(String path, Map<String, Object> paramMap) {
		return ssoTemplate.getData(path, paramMap);
	}


	// ---------------------- 构建URL ---------------------- 

	/**
	 * 构建URL：Server端 单点登录地址
	 * @param clientLoginUrl Client端登录地址 
	 * @param back 回调路径 
	 * @return [SSO-Server端-认证地址 ]
	 */
	public static String buildServerAuthUrl(String clientLoginUrl, String back) {
		return ssoTemplate.buildServerAuthUrl(clientLoginUrl, back);
	}

	/**
	 * 构建URL：Server端向Client下放ticket的地址
	 * @param loginId 账号id 
	 * @param client 客户端标识 
	 * @param redirect Client端提供的重定向地址 
	 * @return see note 
	 */
	public static String buildRedirectUrl(Object loginId, String client, String redirect) {
		return ssoTemplate.buildRedirectUrl(loginId, client, redirect);
	}

	/**
	 * 构建URL：Server端 getData 地址，带签名等参数
	 * @param paramMap 查询参数
	 * @return /
	 */
	public static String buildGetDataUrl(Map<String, Object> paramMap) {
		return ssoTemplate.buildGetDataUrl(paramMap);
	}

	/**
	 * 构建URL：Server 端自定义 path 地址，带签名等参数 （此方法需要配置 sa-token.sso.server-url 地址）
	 * @param paramMap 请求参数
	 * @return /
	 */
	public static String buildCustomPathUrl(String path, Map<String, Object> paramMap) {
		return ssoTemplate.buildCustomPathUrl(path, paramMap);
	}

	
	// ------------------- 发起请求 -------------------

	/**
	 * 发出请求，并返回 SaResult 结果 
	 * @param url 请求地址 
	 * @return 返回的结果 
	 */
	public static SaResult request(String url) {
		return ssoTemplate.request(url);
	}

	/**
	 * 给 paramMap 追加 sign 等参数，并序列化为kv字符串，拼接到url后面
	 * @param url 请求地址
	 * @param paramMap 请求原始参数列表
	 * @return 加工后的url
	 */
	public static String joinParamMapAndSign(String url, Map<String, Object> paramMap) {
		return ssoTemplate.joinLoginIdAndSign(url, paramMap);
	}

	/**
	 * 给 url 拼接 loginId 参数，并拼接 sign 等参数
	 * @param url 链接
	 * @param loginId 账号id
	 * @return 加工后的url
	 */
	public static String joinLoginIdAndSign(String url, Object loginId) {
		return ssoTemplate.joinLoginIdAndSign(url, loginId);
	}


	// -------- 以下方法已废弃，仅为兼容旧版本而保留 --------

	/**
	 * 构建URL：Server端 账号资料查询地址
	 * @param loginId 账号id
	 * @return Server端 账号资料查询地址
	 */
	@Deprecated
	public static String buildUserinfoUrl(Object loginId) {
		return ssoTemplate.buildUserinfoUrl(loginId);
	}

	/**
	 * 获取：账号资料
	 * @param loginId 账号id
	 * @return 账号资料
	 */
	@Deprecated
	public static Object getUserinfo(Object loginId) {
		return ssoTemplate.getUserinfo(loginId);
	}

}

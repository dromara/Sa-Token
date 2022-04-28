package cn.dev33.satoken.sso;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token-SSO 单点登录模块 工具类 
 * @author kong
 *
 */
public class SaSsoUtil {

	/**
	 * 底层 SaSsoTemplate 对象 
	 */
	public static SaSsoTemplate saSsoTemplate = new SaSsoTemplate(StpUtil.stpLogic);
	

	// ---------------------- Ticket 操作 ---------------------- 
	
	/**
	 * 根据 账号id 创建一个 Ticket码 
	 * @param loginId 账号id 
	 * @return Ticket码 
	 */
	public static String createTicket(Object loginId) {
		return saSsoTemplate.createTicket(loginId);
	}
	
	/**
	 * 删除 Ticket 
	 * @param ticket Ticket码
	 */
	public static void deleteTicket(String ticket) {
		saSsoTemplate.deleteTicket(ticket);
	}
	
	/**
	 * 删除 Ticket索引 
	 * @param loginId 账号id 
	 */
	public static void deleteTicketIndex(Object loginId) {
		saSsoTemplate.deleteTicketIndex(loginId);
	}

	/**
	 * 根据 Ticket码 获取账号id，如果Ticket码无效则返回null 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public static Object getLoginId(String ticket) {
		return saSsoTemplate.getLoginId(ticket);
	}

	/**
	 * 根据 Ticket码 获取账号id，并转换为指定类型 
	 * @param <T> 要转换的类型 
	 * @param ticket Ticket码
	 * @param cs 要转换的类型 
	 * @return 账号id 
	 */
	public static <T> T getLoginId(String ticket, Class<T> cs) {
		return saSsoTemplate.getLoginId(ticket, cs);
	}

	/**
	 * 校验ticket码，获取账号id，如果此ticket是有效的，则立即删除 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public static Object checkTicket(String ticket) {
		return saSsoTemplate.checkTicket(ticket);
	}

	/**
	 * 获取：所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) 
	 * @return see note 
	 */
	public static String getAllowUrl() {
		return saSsoTemplate.getAllowUrl();
	}

	/**
	 * 校验重定向url合法性
	 * @param url 下放ticket的url地址 
	 */
	public static void checkRedirectUrl(String url) {
		saSsoTemplate.checkRedirectUrl(url);
	}

	
	// ------------------- SSO 模式三 ------------------- 
	
	/**
	 * 构建URL：校验ticket的URL 
	 * @param ticket ticket码
	 * @param ssoLogoutCallUrl 单点注销时的回调URL 
	 * @return 构建完毕的URL 
	 */
	public static String buildCheckTicketUrl(String ticket, String ssoLogoutCallUrl) {
		return saSsoTemplate.buildCheckTicketUrl(ticket, ssoLogoutCallUrl);
	}

	/**
	 * 为指定账号id注册单点注销回调URL 
	 * @param loginId 账号id
	 * @param sloCallbackUrl 单点注销时的回调URL 
	 */
	public static void registerSloCallbackUrl(Object loginId, String sloCallbackUrl) {
		saSsoTemplate.registerSloCallbackUrl(loginId, sloCallbackUrl);
	}

	/**
	 * 构建URL：单点注销URL 
	 * @param loginId 要注销的账号id
	 * @return 单点注销URL 
	 */
	public static String buildSloUrl(Object loginId) {
		return saSsoTemplate.buildSloUrl(loginId);
	}

	/**
	 * 指定账号单点注销 
	 * @param loginId 指定账号 
	 */
	public static void ssoLogout(Object loginId) {
		saSsoTemplate.ssoLogout(loginId);
	}

	/**
	 * 获取：账号资料 
	 * @param loginId 账号id
	 * @return 账号资料 
	 */
	public static Object getUserinfo(Object loginId) {
		return saSsoTemplate.getUserinfo(loginId);
	}


	// ---------------------- 构建URL ---------------------- 

	/**
	 * 构建URL：Server端 单点登录地址
	 * @param clientLoginUrl Client端登录地址 
	 * @param back 回调路径 
	 * @return [SSO-Server端-认证地址 ]
	 */
	public static String buildServerAuthUrl(String clientLoginUrl, String back) {
		return saSsoTemplate.buildServerAuthUrl(clientLoginUrl, back);
	}

	/**
	 * 构建URL：Server端向Client下放ticke的地址 
	 * @param loginId 账号id 
	 * @param redirect Client端提供的重定向地址 
	 * @return see note 
	 */
	public static String buildRedirectUrl(Object loginId, String redirect) {
		return saSsoTemplate.buildRedirectUrl(loginId, redirect);
	}

	/**
	 * 构建URL：Server端 账号资料查询地址 
	 * @param loginId 账号id
	 * @return Server端 账号资料查询地址 
	 */
	public static String buildUserinfoUrl(Object loginId) {
		return saSsoTemplate.buildUserinfoUrl(loginId);
	}
	
	
	// ------------------- 请求相关 ------------------- 

	/**
	 * 发出请求，并返回 SaResult 结果 
	 * @param url 请求地址 
	 * @return 返回的结果 
	 */
	public static SaResult request(String url) {
		return saSsoTemplate.request(url);
	}

	/**
	 * 校验secretkey秘钥是否有效 （API已过期，请更改为更安全的 sign 式校验）
	 * @param secretkey 秘钥 
	 */
	@Deprecated
	public static void checkSecretkey(String secretkey) {
		saSsoTemplate.checkSecretkey(secretkey);
	}

	/**
	 * 根据参数计算签名 
	 * @param loginId 账号id
	 * @param timestamp 当前时间戳，13位
	 * @param nonce 随机字符串 
	 * @param secretkey 账号id
	 * @return 签名 
	 */
	public static String getSign(Object loginId, String timestamp, String nonce, String secretkey) {
		return saSsoTemplate.getSign(loginId, timestamp, nonce, secretkey);
	}

	/**
	 * 给 url 追加 sign 等参数 
	 * @param loginId
	 * @return 加工后的url 
	 */
	public static String addSignParams(String url, Object loginId) {
		return saSsoTemplate.addSignParams(url, loginId);
	}

	/**
	 * 校验签名
	 * @param req request 
	 */
	public static void checkSign(SaRequest req) {
		saSsoTemplate.checkSign(req);
	}

	/**
	 * 校验时间戳与当前时间的差距是否超出限制 
	 * @param timestamp 时间戳 
	 */
	public static void checkTimestamp(long timestamp) {
		saSsoTemplate.checkTimestamp(timestamp);
	}
	
}

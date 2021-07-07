package cn.dev33.satoken.sso;

/**
 * Sa-Token-SSO模块相关常量
 * @author kong
 *
 */
public class SaSsoConsts {

	/**
	 * 所有API接口 
	 * @author kong 
	 */
	public static final class Api {
		
		/** SSO-Server端：授权地址 */ 
		public static String ssoAuth = "/ssoAuth";

		/** SSO-Server端：RestAPI 登录接口 */ 
		public static String ssoDoLogin = "/ssoDoLogin";

		/** SSO-Server端：校验ticket 获取账号id */ 
		public static String ssoCheckTicket = "/ssoCheckTicket";

		/** SSO-Server端 (and Client端)：单点注销 */ 
		public static String ssoLogout = "/ssoLogout";

		/** SSO-Client端：登录地址 */ 
		public static String ssoLogin = "/ssoLogin";

		/** SSO-Client端：单点注销的回调 */ 
		public static String ssoLogoutCall = "/ssoLogoutCall";
		
	}
	
	/**
	 * 所有参数名称 
	 * @author kong 
	 */
	public static final class ParamName {

		/** redirect参数名称 */
		public static String redirect = "redirect";
		
		/** ticket参数名称 */
		public static String ticket = "ticket";

		/** back参数名称 */
		public static String back = "back";

		/** loginId参数名称 */
		public static String loginId = "loginId";

		/** secretkey参数名称 */
		public static String secretkey = "secretkey";
		
		/** Client端单点注销时-回调URL 参数名称 */
		public static String ssoLogoutCall = "ssoLogoutCall";

	}

	/** Client端单点注销回调URL的Set集合，存储在Session中使用的key */
	public static final String SLO_CALLBACK_SET_KEY = "SLO_CALLBACK_SET_KEY_";

	/** 表示OK的返回结果 */
	public static final String OK = "ok";

	/** 表示请求没有得到任何有效处理 */
	public static final String NOT_HANDLE = "not handle";
	
}

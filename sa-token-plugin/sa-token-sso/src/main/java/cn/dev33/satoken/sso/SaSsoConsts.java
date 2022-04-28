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
		public static String ssoAuth = "/sso/auth";

		/** SSO-Server端：RestAPI 登录接口 */ 
		public static String ssoDoLogin = "/sso/doLogin";

		/** SSO-Server端：校验ticket 获取账号id */ 
		public static String ssoCheckTicket = "/sso/checkTicket";

		/** SSO-Server端 (and Client端)：单点注销地址 */ 
		public static String ssoLogout = "/sso/logout";

		/** SSO-Client端：登录地址 */ 
		public static String ssoLogin = "/sso/login";

		/** SSO-Client端：单点注销的回调 */ 
		public static String ssoLogoutCall = "/sso/logoutCall";
		
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

		/** mode参数名称 */
		public static String mode = "mode";
		
		/** loginId参数名称 */
		public static String loginId = "loginId";

		/** secretkey参数名称 */
		public static String secretkey = "secretkey";
		
		/** Client端单点注销时-回调URL 参数名称 */
		public static String ssoLogoutCall = "ssoLogoutCall";

		public static String name = "name";
		public static String pwd = "pwd";
		
		public static String timestamp = "timestamp";
		public static String nonce = "nonce";
		public static String sign = "sign";

	}

	/** Client端单点注销回调URL的Set集合，存储在Session中使用的key */
	public static final String SLO_CALLBACK_SET_KEY = "SLO_CALLBACK_SET_KEY_";

	/** 表示OK的返回结果 */
	public static final String OK = "ok";

	/** 表示自己 */
	public static final String SELF = "self";

	/** 表示简单模式（SSO模式一） */
	public static final String MODE_SIMPLE = "simple";

	/** 表示ticket模式（SSO模式二和模式三） */
	public static final String MODE_TICKET = "ticket";
	
	/** 表示请求没有得到任何有效处理 {msg: "not handle"} */
	public static final String NOT_HANDLE = "{\"msg\": \"not handle\"}";

}

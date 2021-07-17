package cn.dev33.satoken.oauth2.logic;

/**
 * Sa-Token-OAuth2 所有常量 
 * @author kong
 *
 */
public class SaOAuth2Consts {

	/**
	 * 所有API接口 
	 * @author kong 
	 */
	public static final class Api {
		
		/** OAuth-Server端：授权地址 */ 
		public static String authorize = "/oauth2/authorize";

	}
	
	/**
	 * 所有参数名称 
	 * @author kong 
	 */
	public static final class Param {
		
		/** authorize 的 返回值类型 */
		public static String response_type = "response_type";

		/** client_id 参数名称 */
		public static String client_id = "client_id";
		
		/** client_secret 参数名称 */
		public static String client_secret = "client_secret";
		
		/** redirect_uri 参数名称 */
		public static String redirect_uri = "redirect_uri";

		/** scope 参数名称 */
		public static String scope = "scope";
		
		/** state */
		public static String state = "state";

		/** code 参数名称 */
		public static String code = "code";

		/** token 参数名称 */
		public static String token = "token";
		
		/** grant_type 参数名称 */
		public static String grant_type = "grant_type";
		
	}

	/**
	 * 所有授权类型 
	 */
	public static final class AuthType {

		/** 方式一：授权码 */ 
		public static String code = "code";

		/** 方式二：隐藏式 */ 
		public static String token = "token";

		/** 方式三：密码式 */ 
		public static String password = "password";

		/** 方式四：凭证式 */ 
		public static String client_credentials = "client_credentials";

		public static String authorization_code = "authorization_code";
		
	}
	
	

	/**
	 * 在保存授权码时用到的key 
	 */
	public static final String UNLIMITED_DOMAIN = "*";
	

	/** 表示OK的返回结果 */
	public static final String OK = "ok";

	/** 表示请求没有得到任何有效处理 */
	public static final String NOT_HANDLE = "not handle";
	
	
}

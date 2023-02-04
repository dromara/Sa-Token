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
		public static final String authorize = "/oauth2/authorize";
		public static final String token = "/oauth2/token";
		public static final String refresh = "/oauth2/refresh";
		public static final String revoke = "/oauth2/revoke";
		public static final String client_token = "/oauth2/client_token";
		public static final String doLogin = "/oauth2/doLogin";
		public static final String doConfirm = "/oauth2/doConfirm";
	}
	
	/**
	 * 所有参数名称 
	 * @author kong 
	 */
	public static final class Param {
		public static final String response_type = "response_type";
		public static final String client_id = "client_id";
		public static final String client_secret = "client_secret";
		public static final String redirect_uri = "redirect_uri";
		public static final String scope = "scope";
		public static final String state = "state";
		public static final String code = "code";
		public static final String token = "token";
		public static final String access_token = "access_token";
		public static final String refresh_token = "refresh_token";
		public static final String grant_type = "grant_type";
		public static final String username = "username";
		public static final String password = "password";
		public static final String name = "name";
		public static final String pwd = "pwd";
	}

	/**
	 * 所有返回类型 
	 */
	public static final class ResponseType {
		public static final String code = "code";
		public static final String token = "token";
	}

	/**
	 * 所有授权类型 
	 */
	public static final class GrantType {
		public static final String authorization_code = "authorization_code";
		public static final String refresh_token = "refresh_token";
		public static final String password = "password";
		public static final String client_credentials = "client_credentials";
		public static final String implicit = "implicit";
	}
	
	/** 表示OK的返回结果 */
	public static final String OK = "ok";

	/** 表示请求没有得到任何有效处理 {msg: "not handle"} */
	public static final String NOT_HANDLE = "{\"msg\": \"not handle\"}";
	
}

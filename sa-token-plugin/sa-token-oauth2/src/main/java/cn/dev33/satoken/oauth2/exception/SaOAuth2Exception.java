package cn.dev33.satoken.oauth2.exception;

import cn.dev33.satoken.exception.SaTokenException;

/**
 * 一个异常：代表OAuth2认证流程错误 
 * 
 * @author kong
 */
public class SaOAuth2Exception extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130114L;
	
	/**
	 * 一个异常：代表OAuth2认证流程错误 
	 * @param message 异常描述 
	 */
	public SaOAuth2Exception(String message) {
		super(message);
	}

	/**
	 * 如果flag==true，则抛出message异常 
	 * @param flag 标记
	 * @param message 异常信息 
	 * @param code 异常细分码 
	 */
	public static void throwBy(boolean flag, String message, int code) {
		if(flag) {
			throw new SaOAuth2Exception(message).setCode(code);
		}
	}
	
}

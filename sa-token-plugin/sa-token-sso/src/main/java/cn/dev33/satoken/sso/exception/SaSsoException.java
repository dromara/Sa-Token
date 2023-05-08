package cn.dev33.satoken.sso.exception;

import cn.dev33.satoken.exception.SaTokenException;


/**
 * 一个异常：代表 SSO 认证流程错误 
 * 
 * @author click33
 */
public class SaSsoException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130114L;
	
	/**
	 * 一个异常：代表 SSO 认证流程错误 
	 * @param message 异常描述 
	 */
	public SaSsoException(String message) {
		super(message);
	}

	/**
	 * 一个异常：代表 SSO 认证流程错误 
	 * @param code 异常细分状态码 
	 * @param message 异常描述 
	 */
	public SaSsoException(int code, String message) {
		super(code, message);
	}

	/**
	 * 写入异常细分状态码 
	 * @param code 异常细分状态码
	 * @return 对象自身 
	 */
	public SaSsoException setCode(int code) {
		super.setCode(code);
		return this;
	}
	
	/**
	 * 如果flag==true，则抛出message异常 
	 * @param flag 标记
	 * @param message 异常信息 
	 */
	public static void throwBy(boolean flag, String message) {
		if(flag) {
			throw new SaSsoException(message);
		}
	}
	
}

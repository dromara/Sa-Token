package cn.dev33.satoken.jwt.exception;

import cn.dev33.satoken.exception.SaTokenException;


/**
 * 一个异常：代表 jwt 解析错误  
 * 
 * @author kong
 */
public class SaJwtException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129555290130114L;
	
	/**
	 * jwt 解析错误 
	 * @param message 异常描述 
	 */
	public SaJwtException(String message) {
		super(message);
	}

	/**
	 * jwt 解析错误
	 * @param message 异常描述 
	 * @param cause 异常对象 
	 */
	public SaJwtException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 写入异常细分状态码 
	 * @param code 异常细分状态码
	 * @return 对象自身 
	 */
	public SaJwtException setCode(int code) {
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
			throw new SaJwtException(message);
		}
	}
	
}

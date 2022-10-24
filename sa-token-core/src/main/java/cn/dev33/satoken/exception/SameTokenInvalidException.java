package cn.dev33.satoken.exception;

/**
 * 一个异常：代表提供的 Same-Token 无效 
 * 
 * @author kong
 * @since 2022-10-24
 */
public class SameTokenInvalidException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130144L;
	
	/**
	 * 一个异常：代表提供的 Same-Token 无效 
	 * @param message 异常描述 
	 */
	public SameTokenInvalidException(String message) {
		super(message);
	}

}

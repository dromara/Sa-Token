package cn.dev33.satoken.exception;

/**
 * 一个异常：代表不是 Web 上下文 
 * 
 * @author kong
 * @since 2022-10-29
 */
public class NotWebContextException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130144L;

	/**
	 * 一个异常：代表不是 Web 上下文
	 * @param message 异常描述 
	 */
	public NotWebContextException(String message) {
		super(message);
	}

}

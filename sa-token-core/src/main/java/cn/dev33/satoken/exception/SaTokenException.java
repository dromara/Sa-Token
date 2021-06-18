package cn.dev33.satoken.exception;

/**
 * Sa-Token框架内部逻辑发生错误抛出的异常 
 * (自定义此异常方便开发者在做全局异常处理时分辨异常类型)
 * 
 * @author kong
 *
 */
public class SaTokenException extends RuntimeException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130132L;

	/**
	 * 构建一个异常
	 * 
	 * @param message 异常描述信息
	 */
	public SaTokenException(String message) {
		super(message);
	}

	/**
	 * 构建一个异常
	 * 
	 * @param cause 异常对象
	 */
	public SaTokenException(Throwable cause) {
		super(cause);
	}

	/**
	 * 构建一个异常
	 * 
	 * @param message 异常信息
	 * @param cause 异常对象
	 */
	public SaTokenException(String message, Throwable cause) {
		super(message, cause);
	}

}

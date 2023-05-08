package cn.dev33.satoken.exception;

/**
 * 一个异常：代表 API 已被禁用
 *
 * <p> 一般在 API 不合适调用的时候抛出，例如在集成 jwt 模块后调用数据持久化相关方法 </p>
 *
 * @author click33
 * @since <= 1.34.0
 */
public class ApiDisabledException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130133L;
	
	/** 异常提示语 */
	public static final String BE_MESSAGE = "this api is disabled";

	/**
	 * 一个异常：代表 API 已被禁用  
	 */
	public ApiDisabledException() {
		super(BE_MESSAGE);
	}

	/**
	 * 一个异常：代表 API 已被禁用  
	 * @param message 异常描述 
	 */
	public ApiDisabledException(String message) {
		super(message);
	}
}

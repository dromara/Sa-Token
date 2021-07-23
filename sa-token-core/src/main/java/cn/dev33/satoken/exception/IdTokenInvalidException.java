package cn.dev33.satoken.exception;

/**
 * 一个异常：代表提供的 Id-Token 无效 
 * 
 * @author kong
 */
public class IdTokenInvalidException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130144L;
	
	/**
	 * 一个异常：代表提供的 Id-Token 无效 
	 */
	public IdTokenInvalidException(String message) {
		super(message);
	}

}

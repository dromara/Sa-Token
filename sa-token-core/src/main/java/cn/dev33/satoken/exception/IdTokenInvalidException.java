package cn.dev33.satoken.exception;

/**
 * <h1> 本类设计已过时，未来版本可能移除此类，请及时更换为 SameTokenInvalidException ，使用方式保持不变 </h1>
 * 一个异常：代表提供的 Id-Token 无效 
 * 
 * @author kong
 */
@Deprecated
public class IdTokenInvalidException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130144L;
	
	/**
	 * 一个异常：代表提供的 Id-Token 无效 
	 * @param message 异常描述 
	 */
	public IdTokenInvalidException(String message) {
		super(message);
	}

}

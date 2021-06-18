package cn.dev33.satoken.exception;

/**
 * 一个异常：代表会话未能通过二级认证 
 * 
 * @author kong
 */
public class NotSafeException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130144L;
	
	/** 异常提示语 */
	public static final String BE_MESSAGE = "二级认证失败";

	/**
	 * 一个异常：代表会话未通过二级认证 
	 */
	public NotSafeException() {
		super(BE_MESSAGE);
	}

}

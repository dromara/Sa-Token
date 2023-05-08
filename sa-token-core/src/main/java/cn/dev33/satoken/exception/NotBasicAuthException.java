package cn.dev33.satoken.exception;

/**
 * 一个异常：代表会话未能通过 Http Basic 认证校验
 *
 * @author click33
 * @since 2022-10-29
 */
public class NotBasicAuthException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130144L;
	
	/** 异常提示语 */
	public static final String BE_MESSAGE = "no basic auth";

	/**
	 * 一个异常：代表会话未通过 Http Basic 认证 
	 */
	public NotBasicAuthException() {
		super(BE_MESSAGE);
	}

}

package cn.dev33.satoken.exception;

/**
 * 一个异常：代表 Same-Token 校验未通过
 * 
 * @author click33
 * @since 2022-10-24
 */
public class SameTokenInvalidException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130144L;
	
	/**
	 * 一个异常：代表 Same-Token 校验未通过
	 * @param message 异常描述 
	 */
	public SameTokenInvalidException(String message) {
		super(message);
	}

}

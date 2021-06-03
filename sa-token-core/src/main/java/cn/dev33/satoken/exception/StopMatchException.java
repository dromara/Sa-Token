package cn.dev33.satoken.exception;

/**
 * 一个异常：代表停止路由匹配 
 * 
 * @author kong
 */
public class StopMatchException extends SaTokenException {

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 6806129545290130143L;

	/**
	 * 构造 
	 */
	public StopMatchException() {
		super("stop match");
	}

}

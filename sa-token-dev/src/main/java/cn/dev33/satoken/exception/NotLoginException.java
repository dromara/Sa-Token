package cn.dev33.satoken.exception;

/**
 * 没有登陆抛出的异常
 */
public class NotLoginException extends RuntimeException {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 6806129545290130142L;

	/**
	 * 创建一个
	 */
	public NotLoginException() {
        super("当前账号未登录");
    }
	

}

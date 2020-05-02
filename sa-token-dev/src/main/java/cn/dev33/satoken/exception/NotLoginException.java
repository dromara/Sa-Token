package cn.dev33.satoken.exception;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 没有登陆抛出的异常
 */
public class NotLoginException extends RuntimeException {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 6806129545290130142L;
	

	/**
	 * login_key 
	 */
	private String login_key;
	/** 
	 * 获得login_key
	 * @return login_key
	 */
	public String getLoginKey() {
		return login_key;
	}
	

	/**
	 * 创建一个
	 */
	public NotLoginException() {
        this(StpUtil.stpLogic.login_key);
    }
	
	/**
	 *  创建一个
	 * @param login_key login_key
	 */
	public NotLoginException(String login_key) {
        super("当前会话未登录");	// 这里到底要不要拼接上login_key呢？纠结
        this.login_key = login_key;
    }

}

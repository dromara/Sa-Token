package cn.dev33.satoken.exception;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 没有登陆抛出的异常
 * @author kong
 *
 */
public class NotLoginException extends RuntimeException {
	
	/**
	 *
	 */
	private static final long serialVersionUID = 6806129545290130142L;
	

	/**
	 * login_key 
	 */
	private String loginKey;
	/** 
	 * 获得login_key
	 * @return login_key
	 */
	public String getLoginKey() {
		return loginKey;
	}
	

	/**
	 * 创建一个
	 */
	public NotLoginException() {
        this(StpUtil.stpLogic.loginKey);
    }
	
	/**
	 *  创建一个
	 * @param loginKey login_key
	 */
	public NotLoginException(String loginKey) {
		// 这里到底要不要拼接上login_key呢？纠结
        super("当前会话未登录");	
        this.loginKey = loginKey;
    }

}

package cn.dev33.satoken.exception;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 没有指定权限码，抛出的异常 
 */
public class NotPermissionException extends RuntimeException {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 6806129545290130142L;
	
	/**
	 * 权限码
	 */
	private Object code;
	/**
	 * @return 获得权限码 
	 */
	public Object getCode() {
		return code;
	}
	
	/**
	 * login_key 
	 */
	private String login_key;
	/** 
	 * 	获得login_key
	 * @return login_key
	 */
	public String getLoginKey() {
		return login_key;
	}
	

	public NotPermissionException(Object code) {
        this(code, StpUtil.stpLogic.login_key);
    }
	public NotPermissionException(Object code, String login_key) {
		super("无此权限：" + code);	// 这里到底要不要拼接上login_key呢？纠结
        this.code = code;
        this.login_key = login_key;
    }


}

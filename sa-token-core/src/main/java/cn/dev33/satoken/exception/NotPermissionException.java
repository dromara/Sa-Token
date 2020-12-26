package cn.dev33.satoken.exception;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 没有指定权限码，抛出的异常 
 * @author kong
 *
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
	 * loginKey 
	 */
	private String loginKey;
	/** 
	 * 	获得loginKey 
	 * @return loginKey 
	 */
	public String getLoginKey() {
		return loginKey;
	}
	

	public NotPermissionException(Object code) {
        this(code, StpUtil.stpLogic.loginKey);
    }
	public NotPermissionException(Object code, String loginKey) {
		// 这里到底要不要拼接上login_key呢？纠结
		super("无此权限：" + code);	
        this.code = code;
        this.loginKey = loginKey;
    }


}

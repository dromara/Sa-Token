package cn.dev33.satoken.exception;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 没有指定权限码，抛出的异常 
 * 
 * @author kong
 *
 */
public class NotPermissionException extends SaTokenException {

	/**
	 * 序列化版本号 
	 */
	private static final long serialVersionUID = 6806129545290130141L;

	/** 权限码 */
	private String code;

	/**
	 * @return 获得权限码
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 账号类型
	 */
	private String loginType;

	/**
	 * 获得账号类型
	 * 
	 * @return 账号类型
	 */
	public String getLoginType() {
		return loginType;
	}

	public NotPermissionException(String code) {
		this(code, StpUtil.stpLogic.loginType);
	}

	public NotPermissionException(String code, String loginType) {
		super("无此权限：" + code);
		this.code = code;
		this.loginType = loginType;
	}

}

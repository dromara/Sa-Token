package cn.dev33.satoken.exception;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 没有指定角色标识，抛出的异常 
 * 
 * @author kong
 *
 */
public class NotRoleException extends SaTokenException {

	/**
	 * 序列化版本号 
	 */
	private static final long serialVersionUID = 8243974276159004739L;

	/** 角色标识 */
	private String role;

	/**
	 * @return 获得角色标识
	 */
	public String getRole() {
		return role;
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

	public NotRoleException(String role) {
		this(role, StpUtil.stpLogic.loginType);
	}

	public NotRoleException(String role, String loginType) {
		super("无此角色：" + role);
		this.role = role;
		this.loginType = loginType;
	}

}

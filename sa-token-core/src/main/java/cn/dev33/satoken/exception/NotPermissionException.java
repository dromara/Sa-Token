package cn.dev33.satoken.exception;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 一个异常：代表会话未能通过权限认证校验
 * 
 * @author click33
 * @since <= 1.34.0
 */
public class NotPermissionException extends SaTokenException {

	/**
	 * 序列化版本号 
	 */
	private static final long serialVersionUID = 6806129545290130141L;

	/** 权限码 */
	private String permission;

	/**
	 * @return 获得具体缺少的权限码
	 */
	public String getPermission() {
		return permission;
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

	public NotPermissionException(String permission) {
		this(permission, StpUtil.stpLogic.loginType);
	}

	public NotPermissionException(String permission, String loginType) {
		super("无此权限：" + permission);
		this.permission = permission;
		this.loginType = loginType;
	}

	/**
	 * <h1> 警告：自 v1.30+ 版本起，获取异常权限码由 getCode() 更改为 getPermission()，请及时更换！ </h1> 
	 * @return 获得权限码
	 */
	@Deprecated
	public int getCode() {
		return super.getCode();
	}

}

package cn.dev33.satoken.stp;

/**
 * token信息Model: 用来描述一个token的常用参数
 * 
 * @author kong
 *
 */
public class SaTokenInfo {

	/** token名称 */
	public String tokenName;

	/** token值 */
	public String tokenValue;

	/** 此token是否已经登录 */
	public Boolean isLogin;

	/** 此token对应的LoginId，未登录时为null */
	public Object loginId;

	/** 账号类型 */
	public String loginType;

	/** token剩余有效期 (单位: 秒) */
	public long tokenTimeout;

	/** User-Session剩余有效时间 (单位: 秒) */
	public long sessionTimeout;

	/** Token-Session剩余有效时间 (单位: 秒) */
	public long tokenSessionTimeout;

	/** token剩余无操作有效时间 (单位: 秒) */
	public long tokenActivityTimeout;

	/** 登录设备标识 */
	public String loginDevice;

	/**
	 * @return token名称 
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * @param tokenName token名称 
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	/**
	 * @return token值 
	 */
	public String getTokenValue() {
		return tokenValue;
	}

	/**
	 * @param tokenValue token值 
	 */
	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	/**
	 * @return 此token是否已经登录 
	 */
	public Boolean getIsLogin() {
		return isLogin;
	}

	/**
	 * @param isLogin 此token是否已经登录 
	 */
	public void setIsLogin(Boolean isLogin) {
		this.isLogin = isLogin;
	}

	/**
	 * @return 此token对应的LoginId，未登录时为null 
	 */
	public Object getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId 此token对应的LoginId，未登录时为null 
	 */
	public void setLoginId(Object loginId) {
		this.loginId = loginId;
	}

	/**
	 * @return 账号类型
	 */
	public String getLoginType() {
		return loginType;
	}

	/**
	 * @param loginType 账号类型
	 */
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	/**
	 * @return token剩余有效期 (单位: 秒) 
	 */
	public long getTokenTimeout() {
		return tokenTimeout;
	}

	/**
	 * @param tokenTimeout token剩余有效期 (单位: 秒) 
	 */
	public void setTokenTimeout(long tokenTimeout) {
		this.tokenTimeout = tokenTimeout;
	}

	/**
	 * @return User-Session剩余有效时间 (单位: 秒) 
	 */
	public long getSessionTimeout() {
		return sessionTimeout;
	}

	/**
	 * @param sessionTimeout User-Session剩余有效时间 (单位: 秒) 
	 */
	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	/**
	 * @return Token-Session剩余有效时间 (单位: 秒) 
	 */
	public long getTokenSessionTimeout() {
		return tokenSessionTimeout;
	}

	/**
	 * @param tokenSessionTimeout Token-Session剩余有效时间 (单位: 秒) 
	 */
	public void setTokenSessionTimeout(long tokenSessionTimeout) {
		this.tokenSessionTimeout = tokenSessionTimeout;
	}

	/**
	 * @return token剩余无操作有效时间 (单位: 秒)
	 */
	public long getTokenActivityTimeout() {
		return tokenActivityTimeout;
	}

	/**
	 * @param tokenActivityTimeout token剩余无操作有效时间 (单位: 秒)
	 */
	public void setTokenActivityTimeout(long tokenActivityTimeout) {
		this.tokenActivityTimeout = tokenActivityTimeout;
	}

	/**
	 * @return 登录设备标识 
	 */
	public String getLoginDevice() {
		return loginDevice;
	}

	/**
	 * @param loginDevice 登录设备标识 
	 */
	public void setLoginDevice(String loginDevice) {
		this.loginDevice = loginDevice;
	}

	/**
	 * toString
	 */
	@Override
	public String toString() {
		return "SaTokenInfo [tokenName=" + tokenName + ", tokenValue=" + tokenValue + ", isLogin=" + isLogin
				+ ", loginId=" + loginId + ", loginType=" + loginType + ", tokenTimeout=" + tokenTimeout
				+ ", sessionTimeout=" + sessionTimeout + ", tokenSessionTimeout=" + tokenSessionTimeout
				+ ", tokenActivityTimeout=" + tokenActivityTimeout + ", loginDevice=" + loginDevice + "]";
	}

}

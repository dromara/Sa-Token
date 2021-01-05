package cn.dev33.satoken.stp;

/**
 * 用来描述一个token常用信息的类 
 * @author kong
 *
 */
public class SaTokenInfo {

	/** token名称 */
	public String tokenName;

	/** token值 */
	public String tokenValue;

	/** 当前是否已经登录 */
	public Boolean isLogin;

	/** 当前loginId，未登录时为null */
	public Object loginId;

	/** 当前loginKey */
	public String loginKey;

	/** token剩余有效期 (单位: 秒) */
	public long tokenTimeout;

	/** session剩余有效时间 (单位: 秒) */
	public long sessionTimeout;

	/** token专属session剩余有效时间 (单位: 秒) */
	public long tokenSessionTimeout;
	
	/**
	 * token剩余无操作有效时间
	 */
	public long tokenActivityTimeout;

	/** 当前登录设备 */
	public String loginDevice;

	
	
	/**
	 * @return tokenName
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * @param tokenName 要设置的 tokenName
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	/**
	 * @return tokenValue
	 */
	public String getTokenValue() {
		return tokenValue;
	}

	/**
	 * @param tokenValue 要设置的 tokenValue
	 */
	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	/**
	 * @return isLogin
	 */
	public Boolean getIsLogin() {
		return isLogin;
	}

	/**
	 * @param isLogin 要设置的 isLogin
	 */
	public void setIsLogin(Boolean isLogin) {
		this.isLogin = isLogin;
	}

	/**
	 * @return loginId
	 */
	public Object getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId 要设置的 loginId
	 */
	public void setLoginId(Object loginId) {
		this.loginId = loginId;
	}

	/**
	 * @return loginKey
	 */
	public String getLoginKey() {
		return loginKey;
	}

	/**
	 * @param loginKey 要设置的 loginKey
	 */
	public void setLoginKey(String loginKey) {
		this.loginKey = loginKey;
	}

	/**
	 * @return tokenTimeout
	 */
	public long getTokenTimeout() {
		return tokenTimeout;
	}

	/**
	 * @param tokenTimeout 要设置的 tokenTimeout
	 */
	public void setTokenTimeout(long tokenTimeout) {
		this.tokenTimeout = tokenTimeout;
	}

	/**
	 * @return sessionTimeout
	 */
	public long getSessionTimeout() {
		return sessionTimeout;
	}

	/**
	 * @param sessionTimeout 要设置的 sessionTimeout
	 */
	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	/**
	 * @return tokenSessionTimeout
	 */
	public long getTokenSessionTimeout() {
		return tokenSessionTimeout;
	}

	/**
	 * @param tokenSessionTimeout 要设置的 tokenSessionTimeout
	 */
	public void setTokenSessionTimeout(long tokenSessionTimeout) {
		this.tokenSessionTimeout = tokenSessionTimeout;
	}

	/**
	 * @return tokenActivityTimeout
	 */
	public long getTokenActivityTimeout() {
		return tokenActivityTimeout;
	}

	/**
	 * @param tokenActivityTimeout 要设置的 tokenActivityTimeout
	 */
	public void setTokenActivityTimeout(long tokenActivityTimeout) {
		this.tokenActivityTimeout = tokenActivityTimeout;
	}

	/**
	 * @return loginDevice
	 */
	public String getLoginDevice() {
		return loginDevice;
	}

	/**
	 * @param loginDevice 要设置的 loginDevice
	 */
	public void setLoginDevice(String loginDevice) {
		this.loginDevice = loginDevice;
	}

	
	
	
	@Override
	public String toString() {
		return "SaTokenInfo [tokenName=" + tokenName + ", tokenValue=" + tokenValue + ", isLogin=" + isLogin
				+ ", loginId=" + loginId + ", loginKey=" + loginKey + ", tokenTimeout=" + tokenTimeout
				+ ", sessionTimeout=" + sessionTimeout + ", tokenSessionTimeout=" + tokenSessionTimeout
				+ ", tokenActivityTimeout=" + tokenActivityTimeout + ", loginDevice=" + loginDevice + "]";
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
}

package cn.dev33.satoken.stp;

/**
 * Token 信息 Model: 用来描述一个 Token 的常见参数。
 *
 * <p>
 *     例如：<br>
 *     <pre>
 *     {
 *         "tokenName": "satoken",           // token名称
 *         "tokenValue": "e67b99f1-3d7a-4a8d-bb2f-e888a0805633",      // token值
 *         "isLogin": true,                  // 此token是否已经登录
 *         "loginId": "10001",               // 此token对应的LoginId，未登录时为null
 *         "loginType": "login",              // 账号类型标识
 *         "tokenTimeout": 2591977,          // token剩余有效期 (单位: 秒)
 *         "sessionTimeout": 2591977,        // Account-Session剩余有效时间 (单位: 秒)
 *         "tokenSessionTimeout": -2,        // Token-Session剩余有效时间 (单位: 秒) (-2表示系统中不存在这个缓存)
 *         "tokenActivityTimeout": -1,       // token剩余无操作有效时间 (单位: 秒)
 *         "loginDevice": "default-device"   // 登录设备类型
 *     }
 *     </pre>
 * </p>
 * 
 * @author click33
 * @since <= 1.34.0
 */
public class SaTokenInfo {

	/** token 名称 */
	public String tokenName;

	/** token 值 */
	public String tokenValue;

	/** 此 token 是否已经登录 */
	public Boolean isLogin;

	/** 此 token 对应的 LoginId，未登录时为 null */
	public Object loginId;

	/** 多账号体系下的账号类型 */
	public String loginType;

	/** token 剩余有效期（单位: 秒） */
	public long tokenTimeout;

	/** Account-Session 剩余有效时间（单位: 秒） */
	public long sessionTimeout;

	/** Token-Session 剩余有效时间（单位: 秒） */
	public long tokenSessionTimeout;

	/** token 剩余无操作有效时间（单位: 秒） */
	public long tokenActivityTimeout;

	/** 登录设备类型 */
	public String loginDevice;

	/** 自定义数据（暂无意义，留作扩展） */
	public String tag;
	


	/**
	 * @return token 名称
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * @param tokenName token 名称
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	/**
	 * @return token 值
	 */
	public String getTokenValue() {
		return tokenValue;
	}

	/**
	 * @param tokenValue token 值
	 */
	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	/**
	 * @return 此 token 是否已经登录
	 */
	public Boolean getIsLogin() {
		return isLogin;
	}

	/**
	 * @param isLogin 此 token 是否已经登录
	 */
	public void setIsLogin(Boolean isLogin) {
		this.isLogin = isLogin;
	}

	/**
	 * @return 此 token 对应的LoginId，未登录时为null
	 */
	public Object getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId 此 token 对应的LoginId，未登录时为null
	 */
	public void setLoginId(Object loginId) {
		this.loginId = loginId;
	}

	/**
	 * @return 多账号体系下的账号类型
	 */
	public String getLoginType() {
		return loginType;
	}

	/**
	 * @param loginType 多账号体系下的账号类型
	 */
	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	/**
	 * @return token 剩余有效期（单位: 秒）
	 */
	public long getTokenTimeout() {
		return tokenTimeout;
	}

	/**
	 * @param tokenTimeout token剩余有效期（单位: 秒）
	 */
	public void setTokenTimeout(long tokenTimeout) {
		this.tokenTimeout = tokenTimeout;
	}

	/**
	 * @return Account-Session 剩余有效时间（单位: 秒）
	 */
	public long getSessionTimeout() {
		return sessionTimeout;
	}

	/**
	 * @param sessionTimeout Account-Session剩余有效时间（单位: 秒）
	 */
	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	/**
	 * @return Token-Session剩余有效时间（单位: 秒）
	 */
	public long getTokenSessionTimeout() {
		return tokenSessionTimeout;
	}

	/**
	 * @param tokenSessionTimeout Token-Session剩余有效时间（单位: 秒）
	 */
	public void setTokenSessionTimeout(long tokenSessionTimeout) {
		this.tokenSessionTimeout = tokenSessionTimeout;
	}

	/**
	 * @return token 剩余无操作有效时间（单位: 秒）
	 */
	public long getTokenActivityTimeout() {
		return tokenActivityTimeout;
	}

	/**
	 * @param tokenActivityTimeout token 剩余无操作有效时间（单位: 秒）
	 */
	public void setTokenActivityTimeout(long tokenActivityTimeout) {
		this.tokenActivityTimeout = tokenActivityTimeout;
	}

	/**
	 * @return 登录设备类型
	 */
	public String getLoginDevice() {
		return loginDevice;
	}

	/**
	 * @param loginDevice 登录设备类型
	 */
	public void setLoginDevice(String loginDevice) {
		this.loginDevice = loginDevice;
	}

	/**
	 * @return 自定义数据（暂无意义，留作扩展）
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag 自定义数据（暂无意义，留作扩展）
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * toString
	 */
	@Override
	public String toString() {
		return "SaTokenInfo [tokenName=" + tokenName + ", tokenValue=" + tokenValue + ", isLogin=" + isLogin
				+ ", loginId=" + loginId + ", loginType=" + loginType + ", tokenTimeout=" + tokenTimeout
				+ ", sessionTimeout=" + sessionTimeout + ", tokenSessionTimeout=" + tokenSessionTimeout
				+ ", tokenActivityTimeout=" + tokenActivityTimeout + ", loginDevice=" + loginDevice + ", tag=" + tag
				+ "]";
	}

}

package cn.dev33.satoken.session;

import java.io.Serializable;

/**
 * Token 签名 Model 
 * 
 * 挂在到SaSession上的token签名
 * 
 * @author kong
 *
 */
public class TokenSign implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1406115065849845073L;

	/**
	 * token值
	 */
	private String value;

	/**
	 * 所在设备标识
	 */
	private String device;

	/** 构建一个 */
	public TokenSign() {
	}

	/**
	 * 构建一个
	 * 
	 * @param value  token值
	 * @param device 所在设备标识
	 */
	public TokenSign(String value, String device) {
		this.value = value;
		this.device = device;
	}

	/**
	 * @return token值 
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return token登录设备 
	 */
	public String getDevice() {
		return device;
	}

	@Override
	public String toString() {
		return "TokenSign [value=" + value + ", device=" + device + "]";
	}

}

package cn.dev33.satoken.stp;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * 调用 `StpUtil.setLogin()` 时的 [配置参数 Model ]
 * @author kong
 *
 */
public class SaLoginModel {

	
	/**
	 * 此次登录的客户端设备标识 
	 */
	public String device;
	
	/**
	 * 指定此次登录token的有效期, 单位:秒 （如未指定，自动取全局配置的timeout值）
	 */
	public Long timeout;

	/**
	 * 是否为临时Cookie（临时Cookie会在浏览器关闭时自动删除）
	 */
	public Boolean isTempCookie;

	
	/**
	 * @return device
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * @param device 要设置的 device
	 * @return 对象自身
	 */
	public SaLoginModel setDevice(String device) {
		this.device = device;
		return this;
	}

	/**
	 * @return timeout
	 */
	public Long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout 要设置的 timeout
	 * @return 对象自身
	 */
	public SaLoginModel setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * @return isTempCookie
	 */
	public Boolean getIsTempCookie() {
		return isTempCookie;
	}

	/**
	 * @param isTempCookie 要设置的 isTempCookie
	 * @return 对象自身
	 */
	public SaLoginModel setIsTempCookie(Boolean isTempCookie) {
		this.isTempCookie = isTempCookie;
		return this;
	}

	/**
	 * 构建对象，初始化默认值 
	 * @return 对象自身
	 */
	public SaLoginModel build() {
		return build(SaTokenManager.getConfig());
	}
	
	/**
	 * 构建对象，初始化默认值 
	 * @param config 配置对象 
	 * @return 对象自身
	 */
	public SaLoginModel build(SaTokenConfig config) {
		if(device == null) {
			device = SaTokenConsts.DEFAULT_LOGIN_DEVICE;
		}
		if(timeout == null) {
			timeout = config.getTimeout();
		}
		if(isTempCookie == null) {
			isTempCookie = false;
		}
		return this;
	}
	
	
	/**
	 * 静态方法获取一个 SaLoginModel 对象
	 * @return SaLoginModel 对象 
	 */
	public static SaLoginModel create() {
		return new SaLoginModel();
	}

	
}

package cn.dev33.satoken.stp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.util.SaTokenConsts;

import java.util.Map;

/**
 * 调用 `StpUtil.login()` 时的 [配置参数 Model ]
 * @author kong
 *
 */
public class SaLoginModel {

	/**
	 * 此次登录的客户端设备标识 
	 */
	public String device;
	
	/**
	 * 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
	 */
	public Boolean isLastingCookie;

	/**
	 * 指定此次登录token的有效期, 单位:秒 （如未指定，自动取全局配置的timeout值）
	 */
	public Long timeout;

	/**
	 * jwt扩展信息
	 */
	public Map<String, Object> expandInfoMap;

	
	/**
	 * @return 参考 {@link #device}
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * @param device 参考 {@link #device}
	 * @return 对象自身
	 */
	public SaLoginModel setDevice(String device) {
		this.device = device;
		return this;
	}

	/**
	 * @return 参考 {@link #isLastingCookie}
	 */
	public Boolean getIsLastingCookie() {
		return isLastingCookie;
	}

	/**
	 * @param isLastingCookie 参考 {@link #isLastingCookie}
	 * @return 对象自身
	 */
	public SaLoginModel setIsLastingCookie(Boolean isLastingCookie) {
		this.isLastingCookie = isLastingCookie;
		return this;
	}

	/**
	 * @return 参考 {@link #timeout}
	 */
	public Long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout 参考 {@link #timeout}
	 * @return 对象自身
	 */
	public SaLoginModel setTimeout(long timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * @return 参考 {@link #expandInfoMap}
	 */
	public Map<String, Object> getExpandInfoMap() {
		return expandInfoMap;
	}

	/**
	 * @param expandInfoMap 参考 {@link #expandInfoMap}
	 * @return 对象自身
	 */
	public SaLoginModel setExpandInfoMap(Map<String, Object> expandInfoMap) {
		this.expandInfoMap = expandInfoMap;
		return this;
	}

	/**
	 * @return Cookie时长
	 */
	public int getCookieTimeout() {
		if(isLastingCookie == false) {
			return -1;
		}
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			return Integer.MAX_VALUE;
		}
		return (int)(long)timeout;
	}

	/**
	 * @return 获取device参数，如果为null，则返回默认值
	 */
	public String getDeviceOrDefault() {
		if(device == null) {
			return SaTokenConsts.DEFAULT_LOGIN_DEVICE;
		}
		return device;
	}
	
	/**
	 * 构建对象，初始化默认值 
	 * @return 对象自身
	 */
	public SaLoginModel build() {
		return build(SaManager.getConfig());
	}
	
	/**
	 * 构建对象，初始化默认值 
	 * @param config 配置对象 
	 * @return 对象自身
	 */
	public SaLoginModel build(SaTokenConfig config) {
//		if(device == null) {
//			device = SaTokenConsts.DEFAULT_LOGIN_DEVICE;
//		}
		if(isLastingCookie == null) {
			isLastingCookie = true;
		}
		if(timeout == null) {
			timeout = config.getTimeout();
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

	/**
	 * toString
	 */
	@Override
	public String toString() {
		return "SaLoginModel [device=" + device + ", isLastingCookie=" + isLastingCookie + ", timeout=" + timeout + "]";
	}

	
	/**
	 * 更换为 getDeviceOrDefault() 
	 * @return / 
	 */
	@Deprecated
	public String getDeviceOrDefalut() {
		return getDeviceOrDefault();
	}
}

package cn.dev33.satoken.oauth2;

import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Interface;
import cn.dev33.satoken.oauth2.logic.SaOAuth2InterfaceDefaultImpl;

/**
 * sa-token oauth2 模块 总控类
 * 
 * @author kong
 *
 */
public class SaOAuth2Manager {

	/**
	 * OAuth2 配置 Bean 
	 */
	private static SaOAuth2Config config;
	public static SaOAuth2Config getConfig() {
		if (config == null) {
			// 初始化默认值
			synchronized (SaOAuth2Manager.class) {
				if (config == null) {
					setConfig(new SaOAuth2Config());
				}
			}
		}
		return config;
	}
	public static void setConfig(SaOAuth2Config config) {
		SaOAuth2Manager.config = config;
	}

	/**
	 * sa-token-oauth2 逻辑 Bean 
	 */
	private static SaOAuth2Interface saOAuth2Interface;
	public static SaOAuth2Interface getInterface() {
		if (saOAuth2Interface == null) {
			// 初始化默认值
			synchronized (SaOAuth2Manager.class) {
				if (saOAuth2Interface == null) {
					setInterface(new SaOAuth2InterfaceDefaultImpl());
				}
			}
		}
		return saOAuth2Interface;
	}
	public static void setInterface(SaOAuth2Interface interfaceObj) {
		SaOAuth2Manager.saOAuth2Interface = interfaceObj;
	}

	
}

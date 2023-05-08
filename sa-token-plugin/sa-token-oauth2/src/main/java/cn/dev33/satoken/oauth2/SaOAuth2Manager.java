package cn.dev33.satoken.oauth2;

import cn.dev33.satoken.oauth2.config.SaOAuth2Config;

/**
 * Sa-Token-OAuth2 模块 总控类
 * 
 * @author click33
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

}

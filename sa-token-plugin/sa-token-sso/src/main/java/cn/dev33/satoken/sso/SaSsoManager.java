package cn.dev33.satoken.sso;

import cn.dev33.satoken.config.SaSsoConfig;

/**
 * Sa-Token-SSO 模块 总控类
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaSsoManager {

	/**
	 * Sso 配置 Bean
	 */
	private volatile static SaSsoConfig config;
	public static SaSsoConfig getConfig() {
		if (config == null) {
			synchronized (SaSsoManager.class) {
				if (config == null) {
					setConfig(new SaSsoConfig());
				}
			}
		}
		return config;
	}
	public static void setConfig(SaSsoConfig config) {
		SaSsoManager.config = config;
	}

}

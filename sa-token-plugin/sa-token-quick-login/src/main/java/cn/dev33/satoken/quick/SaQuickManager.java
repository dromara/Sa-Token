package cn.dev33.satoken.quick;

import cn.dev33.satoken.quick.config.SaQuickConfig;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * SaQuickManager，持有 SaQuickConfig 配置对象全局引用
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaQuickManager {

	/**
	 * 配置文件 Bean 
	 */
	private static SaQuickConfig config;	
	public static void setConfig(SaQuickConfig config) {
		SaQuickManager.config = config;
		// 如果配置了 auto=true，则随机生成账号名密码
		if(config.getAuto()) {
			config.setName(SaFoxUtil.getRandomString(8));
			config.setPwd(SaFoxUtil.getRandomString(8));
		}
	}
	public static SaQuickConfig getConfig() {
		if (config == null) {
			synchronized (SaQuickManager.class) {
				if (config == null) {
					setConfig(new SaQuickConfig());
				}
			}
		}
		return config;
	}
	
}

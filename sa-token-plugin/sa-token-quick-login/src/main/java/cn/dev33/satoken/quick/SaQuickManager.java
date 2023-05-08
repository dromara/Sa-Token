package cn.dev33.satoken.quick;

import cn.dev33.satoken.quick.config.SaQuickConfig;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * SaQuickManager 
 * @author click33
 *
 */
public class SaQuickManager {

	/**
	 * 配置文件 Bean 
	 */
	private static SaQuickConfig config;	
	public static void setConfig(SaQuickConfig config) {
		SaQuickManager.config = config;
		// 如果配置了随机密码
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

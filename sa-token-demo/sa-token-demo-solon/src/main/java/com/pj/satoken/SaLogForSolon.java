package com.pj.satoken;

import org.noear.solon.core.util.LogUtil;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.log.SaLogForConsole;
import cn.dev33.satoken.util.StrFormatter;

/**
 * 将 Sa-Token log 信息转接到 Solon  
 * 
 * @author kong
 * @since 2022-11-2
 */
//@Component
public class SaLogForSolon extends SaLogForConsole implements SaLog {

	/**
	 * 打印日志到控制台
	 *
	 * @param level 日志等级
	 * @param str   字符串
	 * @param args  参数列表
	 */
	public void println(int level, String str, Object... args) {
		SaTokenConfig config = SaManager.getConfig();

		if (config.getIsLog() && level >= config.getLogLevelInt()) {
			switch (level) {
				case trace:
					LogUtil.global().trace(LOG_PREFIX + StrFormatter.format(str, args));
					break;
				case debug:
					LogUtil.global().debug(LOG_PREFIX + StrFormatter.format(str, args));
					break;
				case info:
					LogUtil.global().info(LOG_PREFIX + StrFormatter.format(str, args));
					break;
				case warn:
					LogUtil.global().warn(LOG_PREFIX + StrFormatter.format(str, args));
					break;
				case error:
				case fatal:
					LogUtil.global().error(LOG_PREFIX + StrFormatter.format(str, args));
					break;
			}
		}
	}
}

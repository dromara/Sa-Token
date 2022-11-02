package cn.dev33.satoken.log;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.util.StrFormatter;

/**
 * Sa-Token 日志实现类 [控制台打印] 
 * 
 * @author kong
 * @since 2022-11-1
 */
public class SaLogForConsole implements SaLog {

	/**
	 * 日志输出的前缀
	 */
	public static String LOG_PREFIX = "SaLog -->: ";
	
	/**
	 * 日志等级 
	 */
	public static final int trace = 1;
	public static final int debug = 2;
	public static final int info = 3;
	public static final int warn = 4;
	public static final int error = 5;
	public static final int fatal = 6;
	
	@Override
	public void trace(String str, Object... args) {
		println(trace, str, args);
	}

	@Override
	public void debug(String str, Object... args) {
		println(debug, str, args);
	}

	@Override
	public void info(String str, Object... args) {
		println(info, str, args);
	}

	@Override
	public void warn(String str, Object... args) {
		println(warn, str, args);
	}

	@Override
	public void error(String str, Object... args) {
		println(error, str, args);
	}

	@Override
	public void fatal(String str, Object... args) {
		println(fatal, str, args);
	}

	/**
	 * 打印日志到控制台 
	 * @param level 日志等级 
	 * @param str 字符串
	 * @param args 参数列表 
	 */
	public void println(int level, String str, Object... args) {
		SaTokenConfig config = SaManager.getConfig();
		if(config.getIsLog() && level >= config.getLogLevelInt()) {
			System.out.println(LOG_PREFIX + StrFormatter.format(str, args));
		}
	}

}

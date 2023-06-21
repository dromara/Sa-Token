/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.log;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.util.StrFormatter;

/**
 * Sa-Token 日志实现类 [ 控制台打印 ]
 * 
 * @author click33
 * @since 1.33.0
 */
public class SaLogForConsole implements SaLog {

	/**
	 * 日志等级 
	 */
	public static final int trace = 1;
	public static final int debug = 2;
	public static final int info = 3;
	public static final int warn = 4;
	public static final int error = 5;
	public static final int fatal = 6;

	/**
	 * 日志输出的前缀
	 */
	public static String LOG_PREFIX = "SaLog -->: ";
	public static String TRACE_PREFIX = "SA [TRACE]-->: ";
	public static String DEBUG_PREFIX = "SA [DEBUG]-->: ";
	public static String INFO_PREFIX  = "SA [INFO] -->: ";
	public static String WARN_PREFIX  = "SA [WARN] -->: ";
	public static String ERROR_PREFIX = "SA [ERROR]-->: ";
	public static String FATAL_PREFIX = "SA [FATAL]-->: ";

	/**
	 * 日志输出的颜色
	 */
	public static String TRACE_COLOR = "\033[39m";
	public static String DEBUG_COLOR = "\033[34m";
	public static String INFO_COLOR  = "\033[32m";
	public static String WARN_COLOR  = "\033[33m";
	public static String ERROR_COLOR = "\033[31m";
	public static String FATAL_COLOR = "\033[35m";

	public static String DEFAULT_COLOR = "\033[39m";

	@Override
	public void trace(String str, Object... args) {
		println(trace, TRACE_COLOR, TRACE_PREFIX, str, args);
	}

	@Override
	public void debug(String str, Object... args) {
		println(debug, DEBUG_COLOR, DEBUG_PREFIX, str, args);
	}

	@Override
	public void info(String str, Object... args) {
		println(info, INFO_COLOR, INFO_PREFIX, str, args);
	}

	@Override
	public void warn(String str, Object... args) {
		println(warn, WARN_COLOR, WARN_PREFIX, str, args);
	}

	@Override
	public void error(String str, Object... args) {
		println(error, ERROR_COLOR, ERROR_PREFIX, str, args);
	}

	@Override
	public void fatal(String str, Object... args) {
		println(fatal, FATAL_COLOR, FATAL_PREFIX, str, args);
	}

	/**
	 * 打印日志到控制台 
	 * @param level 日志等级
	 * @param color 颜色编码
	 * @param prefix 前缀
	 * @param str 字符串
	 * @param args 参数列表 
	 */
	public void println(int level, String color, String prefix, String str, Object... args) {
		SaTokenConfig config = SaManager.getConfig();
		if(config.getIsLog() && level >= config.getLogLevelInt()) {
			if(config.getIsColorLog() == Boolean.TRUE) {
				// 彩色日志
				System.out.println(color + prefix + StrFormatter.format(str, args) + DEFAULT_COLOR);
			} else {
				// 黑白日志
				System.out.println(prefix + StrFormatter.format(str, args));
			}
		}
	}

	/*
		// 三种写法速度对比
		// if( config.getIsColorLog() != null && config.getIsColorLog() )  10亿次，2058ms
		// if( config.getIsColorLog() == Boolean.TRUE ) 	10亿次，1050ms   最快
		// if( Objects.equals(config.getIsColorLog(), Boolean.TRUE) )  	10亿次，1543ms
	 */

	/*
		颜色参考：
			DEFAULT  	39
			BLACK  		30
			RED  		31
			GREEN  		32
			YELLOW  	33
			BLUE  		34
			MAGENTA  	35
			CYAN  		36
			WHITE  		37
			BRIGHT_BLACK  	90
			BRIGHT_RED  	91
			BRIGHT_GREEN  	92
			BRIGHT_YELLOW  	93
			BRIGHT_BLUE  	94
			BRIGHT_MAGENTA	95
			BRIGHT_CYAN  	96
			BRIGHT_WHITE  	97
	 */

}

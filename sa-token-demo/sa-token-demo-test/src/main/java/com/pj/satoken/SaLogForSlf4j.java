package com.pj.satoken;

import cn.dev33.satoken.log.SaLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将 Sa-Token log 信息转接到 Slf4j 
 * 
 * @author click33
 * @since 2022-11-2
 */
//@Component
public class SaLogForSlf4j implements SaLog {

	Logger log = LoggerFactory.getLogger(SaLogForSlf4j.class);
	
	@Override
	public void trace(String str, Object... args) {
		log.trace(str, args);
	}

	@Override
	public void debug(String str, Object... args) {
		log.debug(str, args);
	}

	@Override
	public void info(String str, Object... args) {
		log.info(str, args);
	}

	@Override
	public void warn(String str, Object... args) {
		log.warn(str, args);
	}

	@Override
	public void error(String str, Object... args) {
		log.error(str, args);
	}

	@Override
	public void fatal(String str, Object... args) {
		log.error(str, args);
	}

}

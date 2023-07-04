package com.pj.satoken;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.log.SaLogForConsole;
import cn.dev33.satoken.util.StrFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将 Sa-Token log 信息转接到 slf4j 接口
 *
 * @author noear 2022/11/14 created
 */
//@Component
public class SaLogForSlf4j extends SaLogForConsole implements SaLog {
    static final Logger log = LoggerFactory.getLogger(SaLogForSlf4j.class);

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
                    log.trace(LOG_PREFIX + StrFormatter.format(str, args));
                    break;
                case debug:
                    log.debug(LOG_PREFIX + StrFormatter.format(str, args));
                    break;
                case info:
                    log.info(LOG_PREFIX + StrFormatter.format(str, args));
                    break;
                case warn:
                    log.warn(LOG_PREFIX + StrFormatter.format(str, args));
                    break;
                case error:
                case fatal:
                    log.error(LOG_PREFIX + StrFormatter.format(str, args));
                    break;
            }
        }
    }
}

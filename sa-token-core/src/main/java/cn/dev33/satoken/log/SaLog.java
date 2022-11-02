package cn.dev33.satoken.log;

/**
 * Sa-Token 日志输出接口
 * 
 * @author kong
 * @since 2022-11-1
 */
public interface SaLog {

    /**
     * 输出 trace 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    public void trace(String str, Object ...args);

    /**
     * 输出 debug 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    public void debug(String str, Object ...args);

    /**
     * 输出 info 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    public void info(String str, Object ...args);

    /**
     * 输出 warn 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    public void warn(String str, Object ...args);

    /**
     * 输出 error 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    public void error(String str, Object ...args);

    /**
     * 输出 fatal 日志 
     * @param str 日志内容
     * @param args 参数列表
     */
    public void fatal(String str, Object ...args);
    
}

package cn.dev33.satoken.filter;

/**
 * Sa-Token 全局过滤器 - 异常处理策略封装，方便 lambda 表达式风格调用
 *
 * <p> 此方法的返回值将在 toString() 后返回给前端，如果你要返回 JSON 数据，需要在返回前自行序列化为 JSON 字符串 </p>
 *
 * @author click33
 * @since <= 1.34.0
 */
@FunctionalInterface
public interface SaFilterErrorStrategy {
	
	/**
	 * 执行方法 
	 * @param e 异常对象
	 * @return 输出对象，此返回值将在 toString() 后返回给前端，如果你要返回 JSON 数据，需要在返回前自行序列化为 JSON 字符串
	 */
	Object run(Throwable e);
	
}

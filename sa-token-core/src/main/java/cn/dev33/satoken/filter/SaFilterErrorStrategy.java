package cn.dev33.satoken.filter;

/**
 * Sa-Token全局过滤器-异常处理策略
 * @author kong
 *
 */
public interface SaFilterErrorStrategy {
	
	/**
	 * 执行方法 
	 * @param e 异常对象
	 * @return 输出对象(请提前序列化)
	 */
	public Object run(Throwable e);
	
}

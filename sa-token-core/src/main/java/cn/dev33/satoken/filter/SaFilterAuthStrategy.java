package cn.dev33.satoken.filter;

/**
 * Sa-Token全局过滤器-认证策略
 * @author kong
 *
 */
public interface SaFilterAuthStrategy {
	
	/**
	 * 执行方法 
	 * @param r 无含义参数，留作扩展 
	 */
	public void run(Object r);
	
}

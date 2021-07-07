package cn.dev33.satoken.context.model;

import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Request 包装类
 * @author kong
 *
 */
public interface SaRequest {
	
	/**
	 * 获取底层源对象 
	 * @return see note 
	 */
	public Object getSource();
	
	/**
	 * 在 [请求体] 里获取一个值 
	 * @param name 键 
	 * @return 值 
	 */
	public String getParameter(String name);

	/**
	 * 在 [请求体] 里获取一个值，值为空时返回默认值  
	 * @param name 键 
	 * @param defaultValue 值为空时的默认值  
	 * @return 值 
	 */
	public default String getParameter(String name, String defaultValue) {
		String value = getParameter(name);
		if(SaFoxUtil.isEmpty(value)) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * 在 [请求头] 里获取一个值 
	 * @param name 键 
	 * @return 值 
	 */
	public String getHeader(String name);

	/**
	 * 在 [Cookie作用域] 里获取一个值 
	 * @param name 键 
	 * @return 值 
	 */
	public String getCookieValue(String name);

	/**
	 * 返回当前请求path (不包括上下文名称) 
	 * @return see note
	 */
	public String getRequestPath();

	/**
	 * 返回当前请求的url，例：http://xxx.com/?id=127
	 * @return see note
	 */
	public String getUrl();
	
	/**
	 * 返回当前请求的类型 
	 * @return see note
	 */
	public String getMethod();
	
	/**
	 * 此请求是否为Ajax请求 
	 * @return see note 
	 */
	public default boolean isAjax() {
		return getHeader("X-Requested-With") != null;
	}
	
}

package cn.dev33.satoken.context.model;

/**
 * Request包装类
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
	 * 在 [Request作用域] 里写入一个值 
	 * @param name 键 
	 * @param value 值
	 */
	public void setAttribute(String name, Object value);
	
	/**
	 * 在 [Request作用域] 里获取一个值 
	 * @param name 键 
	 * @return 值 
	 */
	public Object getAttribute(String name);

	/**
	 * 在 [Request作用域] 里删除一个值 
	 * @param name 键 
	 */
	public void removeAttribute(String name);

	/**
	 * 在 [请求体] 里获取一个值 
	 * @param name 键 
	 * @return 值 
	 */
	public String getParameter(String name);

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
	 * 返回当前请求的URL 
	 * @return see note
	 */
	public String getRequestURI();
	
}

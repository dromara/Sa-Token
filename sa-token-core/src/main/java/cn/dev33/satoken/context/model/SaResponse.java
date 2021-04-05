package cn.dev33.satoken.context.model;

/**
 * Response 包装类 
 * @author kong
 *
 */
public interface SaResponse {

	/**
	 * 获取底层源对象 
	 * @return see note 
	 */
	public Object getSource();
	
	/**
	 * 删除指定Cookie 
	 * @param name Cookie名称
	 */
	public void deleteCookie(String name);
	
	/**
	 * 写入指定Cookie 
	 * @param name     Cookie名称
	 * @param value    Cookie值
	 * @param path     Cookie路径
	 * @param domain   Cookie的作用域
	 * @param timeout  过期时间 （秒）
	 */
	public void addCookie(String name, String value, String path, String domain, int timeout);
	
}

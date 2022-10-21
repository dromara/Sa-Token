package cn.dev33.satoken.context.model;

/**
 * Response 包装类 
 * @author kong
 *
 */
public interface SaResponse {

	/**
	 * 指定前端可以获取到哪些响应头时使用的参数名 
	 */
	public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	
	/**
	 * 获取底层源对象 
	 * @return see note 
	 */
	public Object getSource();
	
	/**
	 * 删除指定Cookie 
	 * @param name Cookie名称
	 */
	public default void deleteCookie(String name) {
		addCookie(name, null, null, null, 0);
	}

	/**
	 * 写入指定Cookie
	 * @param name     Cookie名称
	 * @param value    Cookie值
	 * @param path     Cookie路径
	 * @param domain   Cookie的作用域
	 * @param timeout  过期时间 （秒）
	 */
	public default void addCookie(String name, String value, String path, String domain, int timeout) {
		this.addCookie(new SaCookie(name, value).setPath(path).setDomain(domain).setMaxAge(timeout)); 
	}
	
	/**
	 * 写入指定Cookie
	 * @param cookie Cookie-Model
	 */
	public default void addCookie(SaCookie cookie) {
		this.addHeader(SaCookie.HEADER_NAME, cookie.toHeaderValue());
	}
	
	/**
	 * 设置响应状态码
	 * @param sc 响应状态码
	 * @return 对象自身
	 */
	public SaResponse setStatus(int sc);
	
	/**
	 * 在响应头里写入一个值 
	 * @param name 名字
	 * @param value 值 
	 * @return 对象自身 
	 */
	public SaResponse setHeader(String name, String value);

	/**
	 * 在响应头里添加一个值 
	 * @param name 名字
	 * @param value 值 
	 * @return 对象自身 
	 */
	public SaResponse addHeader(String name, String value);
	
	/**
	 * 在响应头写入 [Server] 服务器名称 
	 * @param value 服务器名称  
	 * @return 对象自身 
	 */
	public default SaResponse setServer(String value) {
		return this.setHeader("Server", value);
	}

	/**
	 * 重定向 
	 * @param url 重定向地址 
	 * @return 任意值 
	 */
	public Object redirect(String url);
	
}

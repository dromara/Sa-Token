package cn.dev33.satoken.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * sa-token 对cookie的相关操作 接口类
 * 
 * @author kong
 *
 */
public interface SaTokenCookie {

	/**
	 * 在request对象中获取指定Cookie 
	 * 
	 * @param request    request对象
	 * @param cookieName Cookie名称
	 * @return 查找到的Cookie对象
	 */
	public Cookie getCookie(HttpServletRequest request, String cookieName);

	/**
	 * 添加Cookie 
	 * 
	 * @param response response对象
	 * @param name     Cookie名称
	 * @param value    Cookie值
	 * @param path     Cookie路径
	 * @param domain   Cookie的作用域
	 * @param timeout  过期时间 （秒）
	 */
	public void addCookie(HttpServletResponse response, String name, String value, String path, String domain, int timeout);

	/**
	 * 删除Cookie 
	 * 
	 * @param request  request对象
	 * @param response response对象
	 * @param name     Cookie名称
	 */
	public void delCookie(HttpServletRequest request, HttpServletResponse response, String name);

	/**
	 * 修改Cookie的value值 
	 * 
	 * @param request  request对象
	 * @param response response对象
	 * @param name     Cookie名称
	 * @param value    Cookie值
	 */
	public void updateCookie(HttpServletRequest request, HttpServletResponse response, String name, String value);

}

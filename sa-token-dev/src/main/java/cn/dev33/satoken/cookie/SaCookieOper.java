package cn.dev33.satoken.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * sa-token 对cookie的相关操作 接口类 
 * @author kong
 *
 */
public interface SaCookieOper {

	/**
	 * 获取指定cookie .
	 * 
	 * @param request    .
	 * @param cookieName .
	 * @return .
	 */
	public Cookie getCookie(HttpServletRequest request, String cookieName);

	/**
	 * 添加cookie
	 * 
	 * @param response .
	 * @param name     .
	 * @param value    .
	 * @param path     .
	 * @param timeout  .
	 */
	public void addCookie(HttpServletResponse response, String name, String value, String path, int timeout);

	/**
	 * 删除cookie .
	 * 
	 * @param request  .
	 * @param response .
	 * @param name     .
	 */
	public void delCookie(HttpServletRequest request, HttpServletResponse response, String name);

	/**
	 * 修改cookie的value值
	 * 
	 * @param request  .
	 * @param response .
	 * @param name     .
	 * @param value    .
	 */
	public void updateCookie(HttpServletRequest request, HttpServletResponse response, String name, String value);

}

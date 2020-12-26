package cn.dev33.satoken.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Cookie操作工具类 
 * @author kong 
 */
public class SaTokenCookieUtil {

	/**
	 * 在request对象中获取指定Cookie 
	 * @param request request对象 
	 * @param cookieName Cookie名称 
	 * @return 查找到的Cookie对象
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null && cookieName.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return null;
	}

	/**
	 * 添加cookie 
	 * @param response response
	 * @param name     Cookie名称 
	 * @param value    Cookie值
	 * @param path     Cookie写入路径
	 * @param timeout  Cookie有效期 (秒) 
	 */
	public static void addCookie(HttpServletResponse response, String name, String value, String path, int timeout) {
		Cookie cookie = new Cookie(name, value);
		if (path == null) {
			path = "/";
		}
		cookie.setPath(path);
		cookie.setMaxAge(timeout);
		response.addCookie(cookie);
	}

	/**
	 * 删除Cookie 
	 * @param request  request对象
	 * @param response response对象
	 * @param name     Cookie名称 
	 */
	public static void delCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null && (name).equals(cookie.getName())) {
					addCookie(response, name, null, null, 0);
					return;
				}
			}
		}
	}

	/**
	 * 修改cookie的value值 
	 * @param request  request对象 
	 * @param response response对象 
	 * @param name     Cookie名称 
	 * @param value    Cookie值 
	 */
	public static void updateCookie(HttpServletRequest request, HttpServletResponse response, String name,
			String value) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null && (name).equals(cookie.getName())) {
					addCookie(response, name, value, cookie.getPath(), cookie.getMaxAge());
					return;
				}
			}
		}
	}

}
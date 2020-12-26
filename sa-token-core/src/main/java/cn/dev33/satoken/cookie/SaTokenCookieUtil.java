package cn.dev33.satoken.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cookie操作工具类 
 * 
 * @author kong
 *
 */
public class SaTokenCookieUtil {

	/**
	 * 获取指定cookie .
	 * 
	 * @param request    .
	 * @param cookieName .
	 * @return .
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
	 * 
	 * @param response .
	 * @param name     .
	 * @param value    .
	 * @param path     .
	 * @param timeout  .
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
	 * 删除cookie .
	 * 
	 * @param request  .
	 * @param response .
	 * @param name     .
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
	 * 
	 * @param request  .
	 * @param response .
	 * @param name     .
	 * @param value    .
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
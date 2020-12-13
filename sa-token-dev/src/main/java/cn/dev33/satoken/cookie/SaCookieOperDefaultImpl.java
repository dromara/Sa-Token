package cn.dev33.satoken.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.dev33.satoken.util.SaCookieUtil;

/**
 * sa-token 对cookie的相关操作 接口实现类 
 * @author kong
 *
 */
public class SaCookieOperDefaultImpl implements SaCookieOper {

	/**
	 * 获取指定cookie 
	 */
	public Cookie getCookie(HttpServletRequest request, String cookieName) {
		return SaCookieUtil.getCookie(request, cookieName);
	}

	/**
	 * 添加cookie 
	 */
	public void addCookie(HttpServletResponse response, String name, String value, String path, int timeout) {
		SaCookieUtil.addCookie(response, name, value, path, timeout);
	}

	/**
	 * 删除cookie 
	 */
	public void delCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		SaCookieUtil.delCookie(request, response, name);
	}

	/**
	 * 修改cookie的value值 
	 */
	public void updateCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {
		SaCookieUtil.updateCookie(request, response, name, value);
	}

}

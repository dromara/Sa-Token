package cn.dev33.satoken.spring;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * 
 * @author kong
 *
 */
public class SaPathMatcherHolder {
	
	private SaPathMatcherHolder() {
	}

	/**
	 * 路由匹配器
	 */
	public static PathMatcher pathMatcher;

	/**
	 * 获取路由匹配器
	 * @return 路由匹配器
	 */
	public static PathMatcher getPathMatcher() {
		if(pathMatcher == null) {
			pathMatcher = new AntPathMatcher();
		}
		return pathMatcher;
	}
	
	/**
	 * 写入路由匹配器
	 * @param pathMatcher 路由匹配器
	 */
	public static void setPathMatcher(PathMatcher pathMatcher) {
		SaPathMatcherHolder.pathMatcher = pathMatcher;
	}
	
}

package cn.dev33.satoken.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import cn.dev33.satoken.servlet.SaTokenServlet;

/**
 * sa-token 对Cookie的相关操作 接口实现类
 * 
 * @author kong
 *
 */
public class SaTokenServletSpringImpl implements SaTokenServlet {

	/**
	 * 获取当前请求的Request对象
	 */
	@Override
	public HttpServletRequest getRequest() {
		return SpringMVCUtil.getRequest();
	}

	/**
	 * 获取当前请求的Response对象
	 */
	@Override
	public HttpServletResponse getResponse() {
		return SpringMVCUtil.getResponse();
	}


	/**
	 * 路由匹配器
	 */
	private static PathMatcher pathMatcher;

	/**
	 * 获取路由匹配器
	 * @param pathMatcher 路由匹配器
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
		SaTokenServletSpringImpl.pathMatcher = pathMatcher;
	}
	
	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径 
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		return getPathMatcher().match(pattern, path);
	}

}

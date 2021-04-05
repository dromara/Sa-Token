package cn.dev33.satoken.spring;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.servlet.SaRequestForServlet;
import cn.dev33.satoken.context.model.servlet.SaResponseForServlet;

/**
 * sa-token 对Cookie的相关操作 接口实现类
 * 
 * @author kong
 *
 */
public class SaTokenContextForSpring implements SaTokenContext {

	/**
	 * 获取当前请求的Request对象
	 */
	@Override
	public SaRequest getRequest() {
		return new SaRequestForServlet(SpringMVCUtil.getRequest());
	}

	/**
	 * 获取当前请求的Response对象
	 */
	@Override
	public SaResponse getResponse() {
		return new SaResponseForServlet(SpringMVCUtil.getResponse());
	}

	/**
	 * 路由匹配器
	 */
	private static PathMatcher pathMatcher;

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
		SaTokenContextForSpring.pathMatcher = pathMatcher;
	}
	
	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径 
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		return getPathMatcher().match(pattern, path);
	}

}

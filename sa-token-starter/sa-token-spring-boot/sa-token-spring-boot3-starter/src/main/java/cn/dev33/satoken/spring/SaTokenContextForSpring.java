package cn.dev33.satoken.spring;

import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;

/**
 * Sa-Token 上下文处理器 [ SpringMVC版本实现 ] 
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
	 * 获取当前请求的 [存储器] 对象 
	 */
	@Override
	public SaStorage getStorage() {
		return new SaStorageForServlet(SpringMVCUtil.getRequest());
	}
	
	/**
	 * 校验指定路由匹配符是否可以匹配成功指定路径 
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		return SaPathMatcherHolder.getPathMatcher().match(pattern, path);
	}

	/**
	 * 此上下文是否有效 
	 */
	@Override
	public boolean isValid() {
		return SpringMVCUtil.isWeb();
	}

}

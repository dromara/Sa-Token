package cn.dev33.satoken.reactor.spring;

import cn.dev33.satoken.context.SaTokenContextForThreadLocal;
import cn.dev33.satoken.spring.SaPathMatcherHolder;

/**
 * Sa-Token 上下文处理器 [ Spring Reactor 版本实现 ] 
 * 
 * @author kong
 *
 */
public class SaTokenContextForSpringReactor extends SaTokenContextForThreadLocal {
	
	/**
	 * 重写路由匹配方法
	 */
	@Override
	public boolean matchPath(String pattern, String path) {
		return SaPathMatcherHolder.getPathMatcher().match(pattern, path);
	}
	
}

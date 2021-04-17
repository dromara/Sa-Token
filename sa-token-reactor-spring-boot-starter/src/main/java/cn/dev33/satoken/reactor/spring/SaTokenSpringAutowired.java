package cn.dev33.satoken.reactor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.action.SaTokenAction;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.SaTokenContextForThreadLocal;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.StpInterface;

/**
 * 利用spring的自动装配来加载开发者重写的Bean
 * 
 * @author kong
 *
 */
@Component
public class SaTokenSpringAutowired {

	/**
	 * 获取配置Bean
	 * 
	 * @return 配置对象
	 */
	@Bean
	@ConfigurationProperties(prefix = "spring.sa-token")
	public SaTokenConfig getSaTokenConfig() {
		return new SaTokenConfig();
	}

	/**
	 * 注入配置Bean
	 * 
	 * @param saTokenConfig 配置对象
	 */
	@Autowired
	public void setConfig(SaTokenConfig saTokenConfig) {
		SaTokenManager.setConfig(saTokenConfig);
	}

	/**
	 * 注入持久化Bean
	 * 
	 * @param saTokenDao SaTokenDao对象 
	 */
	@Autowired(required = false)
	public void setSaTokenDao(SaTokenDao saTokenDao) {
		SaTokenManager.setSaTokenDao(saTokenDao);
	}

	/**
	 * 注入权限认证Bean
	 * 
	 * @param stpInterface StpInterface对象 
	 */
	@Autowired(required = false)
	public void setStpInterface(StpInterface stpInterface) {
		SaTokenManager.setStpInterface(stpInterface);
	}

	/**
	 * 注入框架行为Bean
	 * 
	 * @param saTokenAction SaTokenAction对象 
	 */
	@Autowired(required = false)
	public void setSaTokenAction(SaTokenAction saTokenAction) {
		SaTokenManager.setSaTokenAction(saTokenAction);
	}

	/**
	 * 获取容器交互Bean (ThreadLocal版)
	 * 
	 * @return 容器交互Bean (ThreadLocal版)
	 */
	@Bean
	public SaTokenContext getSaTokenContext() {
		return new SaTokenContextForThreadLocal() {
			/**
			 * 重写路由匹配方法
			 */
			@Override
			public boolean matchPath(String pattern, String path) {
				return SaPathMatcherHolder.getPathMatcher().match(pattern, path);
			}
		};
	}

	/**
	 * 注入容器交互Bean
	 * 
	 * @param saTokenContext SaTokenContext对象 
	 */
	@Autowired
	public void setSaTokenContext(SaTokenContext saTokenContext) {
		SaTokenManager.setSaTokenContext(saTokenContext);
	}

	/**
	 * 注入监听器Bean
	 * 
	 * @param saTokenListener saTokenListener对象 
	 */
	@Autowired(required = false)
	public void setSaTokenListener(SaTokenListener saTokenListener) {
		SaTokenManager.setSaTokenListener(saTokenListener);
	}
	/**
	 * 利用自动匹配特性，获取SpringMVC框架内部使用的路由匹配器
	 * 
	 * @param pathMatcher 要设置的 pathMatcher
	 */
	@Autowired(required = false)
	public void setPathMatcher(PathMatcher pathMatcher) {
		SaPathMatcherHolder.setPathMatcher(pathMatcher);
	}


}

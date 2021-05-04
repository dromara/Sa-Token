package cn.dev33.satoken.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.action.SaTokenAction;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
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
		SaManager.setConfig(saTokenConfig);
	}

	/**
	 * 注入持久化Bean
	 * 
	 * @param saTokenDao SaTokenDao对象 
	 */
	@Autowired(required = false)
	public void setSaTokenDao(SaTokenDao saTokenDao) {
		SaManager.setSaTokenDao(saTokenDao);
	}

	/**
	 * 注入权限认证Bean
	 * 
	 * @param stpInterface StpInterface对象 
	 */
	@Autowired(required = false)
	public void setStpInterface(StpInterface stpInterface) {
		SaManager.setStpInterface(stpInterface);
	}

	/**
	 * 注入框架行为Bean
	 * 
	 * @param saTokenAction SaTokenAction对象 
	 */
	@Autowired(required = false)
	public void setSaTokenAction(SaTokenAction saTokenAction) {
		SaManager.setSaTokenAction(saTokenAction);
	}

	/**
	 * 获取容器交互Bean (Spring版)
	 * 
	 * @return 容器交互Bean (Spring版)
	 */
	@Bean
	public SaTokenContext getSaTokenContext() {
		return new SaTokenContextForSpring();
	}

	/**
	 * 注入容器交互Bean
	 * 
	 * @param saTokenContext SaTokenContext对象 
	 */
	@Autowired
	public void setSaTokenContext(SaTokenContext saTokenContext) {
		SaManager.setSaTokenContext(saTokenContext);
	}

	/**
	 * 注入侦听器Bean
	 * 
	 * @param saTokenListener saTokenListener对象 
	 */
	@Autowired(required = false)
	public void setSaTokenListener(SaTokenListener saTokenListener) {
		SaManager.setSaTokenListener(saTokenListener);
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

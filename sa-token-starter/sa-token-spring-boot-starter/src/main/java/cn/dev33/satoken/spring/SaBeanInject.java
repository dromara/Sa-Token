package cn.dev33.satoken.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.action.SaTokenAction;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.temp.SaTempInterface;

/**
 * 注入Sa-Token所需要的Bean 
 * 
 * @author kong
 *
 */
@Component
@Import({SaBeanRegister.class, SaHistoryVersionInject.class})
public class SaBeanInject {

	/**
	 * 注入配置Bean
	 * 
	 * @param saTokenConfig 配置对象
	 */
	@Autowired(required = false)
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
	 * 注入容器交互Bean
	 * 
	 * @param saTokenContext SaTokenContext对象 
	 */
	@Autowired(required = false)
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
	 * 注入临时令牌验证模块 Bean
	 * 
	 * @param saTemp saTemp对象 
	 */
	@Autowired(required = false)
	public void setSaTemp(SaTempInterface saTemp) {
		SaManager.setSaTemp(saTemp);
	}
	
	/**
	 * 利用自动注入特性，获取Spring框架内部使用的路由匹配器
	 * 
	 * @param pathMatcher 要设置的 pathMatcher
	 */
	@Autowired(required = false)
	@Qualifier("mvcPathMatcher")
	public void setPathMatcher(PathMatcher pathMatcher) {
		SaPathMatcherHolder.setPathMatcher(pathMatcher);
	}

}

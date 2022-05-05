package cn.dev33.satoken.reactor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.PathMatcher;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.basic.SaBasicTemplate;
import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.second.SaTokenSecondContextCreator;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.id.SaIdTemplate;
import cn.dev33.satoken.id.SaIdUtil;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempInterface;

/**
 * 利用spring的自动装配来加载开发者重写的Bean
 * 
 * @author kong
 *
 */
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
	 * 注入上下文Bean
	 * 
	 * @param saTokenContext SaTokenContext对象 
	 */
	@Autowired(required = false)
	public void setSaTokenContext(SaTokenContext saTokenContext) {
		SaManager.setSaTokenContext(saTokenContext);
	}

	/**
	 * 注入二级上下文Bean
	 * 
	 * @param saTokenSecondContextCreator 二级上下文创建器 
	 */
	@Autowired(required = false)
	public void setSaTokenContext(SaTokenSecondContextCreator saTokenSecondContextCreator) {
		SaManager.setSaTokenSecondContext(saTokenSecondContextCreator.create());
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
	 * 注入 Sa-Id-Token 模块 Bean
	 * 
	 * @param saIdTemplate saIdTemplate对象 
	 */
	@Autowired(required = false)
	public void setSaIdTemplate(SaIdTemplate saIdTemplate) {
		SaIdUtil.saIdTemplate = saIdTemplate;
	}

	/**
	 * 注入 Sa-Token Http Basic 认证模块 
	 * 
	 * @param saBasicTemplate saBasicTemplate对象 
	 */
	@Autowired(required = false)
	public void setSaBasicTemplate(SaBasicTemplate saBasicTemplate) {
		SaBasicUtil.saBasicTemplate = saBasicTemplate;
	}
	
	/**
	 * 注入自定义的 JSON 转换器 Bean 
	 * 
	 * @param saJsonTemplate JSON 转换器 
	 */
	@Autowired(required = false)
	public void setSaJsonTemplate(SaJsonTemplate saJsonTemplate) {
		SaManager.setSaJsonTemplate(saJsonTemplate);
	}

	/**
	 * 注入自定义的 参数签名 Bean 
	 * 
	 * @param saSignTemplate 参数签名 Bean 
	 */
	@Autowired(required = false)
	public void setSaSignTemplate(SaSignTemplate saSignTemplate) {
		SaManager.setSaSignTemplate(saSignTemplate);
	}
	
	/**
	 * 注入自定义的 StpLogic 
	 * @param stpLogic / 
	 */
	@Autowired(required = false)
	public void setStpLogic(StpLogic stpLogic) {
		StpUtil.setStpLogic(stpLogic);
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

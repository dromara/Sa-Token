package cn.dev33.satoken.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.strategy.SaStrategy;

/**
 * 注入 Sa-Token 所需要的 Bean
 *
 * @author click33
 * @since 1.27.0
 */
@Component
@Lazy(false)
public class SaBeanInject {

	/**
	 * 注入侦听器Bean 
	 * 
	 * @param saLog 侦听器Bean 
	 */
	@Autowired(required = false)
	public void setSaLog(SaLog saLog) {
		SaManager.setLog(saLog);
	}

	/**
	 * 注入持久化Bean
	 *
	 * @param saTokenDao 持久化Bean
	 */
	@Autowired(required = false)
	public void setSaTokenDao(SaTokenDao saTokenDao) {
		SaManager.setSaTokenDao(saTokenDao);
	}

	/**
	 * 注入权限认证Bean
	 *
	 * @param stpInterface 权限认证Bean
	 */
	@Autowired(required = false)
	public void setStpInterface(StpInterface stpInterface) {
		SaManager.setStpInterface(stpInterface);
	}

	/**
	 * 注入上下文Bean
	 *
	 * @param saTokenContext 上下文Bean
	 */
	@Autowired(required = false)
	public void setSaTokenContext(SaTokenContext saTokenContext) {
		SaManager.setSaTokenContext(saTokenContext);
	}

	/**
	 * 注入Sa-Token的 Json 转换器Bean 
	 * 
	 * @param saJsonTemplate Sa-Token的 Json 转换器Bean 
	 */
	@Autowired(required = false)
	public void setSaJsonTemplate(SaJsonTemplate saJsonTemplate) {
		SaManager.setSaJsonTemplate(saJsonTemplate);
	}

	/**
	 * 注入自定义的 SaStrategy 算法类
	 *
	 * @param saStrategy /
	 */
	@Autowired(required = false)
	public void setSaStrategy(SaStrategy saStrategy) {
		SaStrategy.me = saStrategy;
	}

}

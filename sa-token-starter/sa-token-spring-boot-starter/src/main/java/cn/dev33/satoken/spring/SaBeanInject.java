package cn.dev33.satoken.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.StpInterface;

/**
 * 注入 Sa-Token 所需要的Bean
 * 
 * @author click33
 *
 */
@Lazy(false)
@Component
public class SaBeanInject {

	/**
	 * 注入自定义的 SaTokenConfig
	 * 
	 * @param saTokenConfig 配置对象 
	 */
	@Autowired(required = false)
	public void setConfig(SaTokenConfig saTokenConfig) {
		SaManager.setConfig(saTokenConfig);
	}

	/**
	 * 注入自定义的 SaTokenDao 
	 * @param saTokenDao SaTokenDao
	 */
	@Autowired(required = false)
	public void setSaTokenDao(SaTokenDao saTokenDao) {
		SaManager.setSaTokenDao(saTokenDao);
	}

	/**
	 * 注入自定义的 StpInterface 
	 * @param stpInterface StpInterface
	 */
	@Autowired(required = false)
	public void setStpInterface(StpInterface stpInterface) {
		SaManager.setStpInterface(stpInterface);
	}

	/**
	 * 注入自定义的 SaTokenContext 
	 * @param saTokenContext SaTokenContext
	 */
	@Autowired(required = false)
	public void setSaTokenContext(SaTokenContext saTokenContext) {
		SaManager.setSaTokenContextNotPrimary(saTokenContext);
	}

	/**
	 * 注入自定义的 SaTokenListener
	 * @param saTokenListener saTokenListener
	 */
	@Autowired(required = false)
	public void setSaTokenListener(SaTokenListener saTokenListener) {
		SaManager.setSaTokenListener(saTokenListener);
	}

}

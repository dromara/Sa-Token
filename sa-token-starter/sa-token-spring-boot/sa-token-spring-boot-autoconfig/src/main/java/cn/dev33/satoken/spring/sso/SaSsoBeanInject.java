package cn.dev33.satoken.spring.sso;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoTemplate;
import cn.dev33.satoken.sso.SaSsoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

/**
 * 注入 Sa-Token-SSO 所需要的Bean 
 * 
 * @author kong
 *
 */
@ConditionalOnClass(SaSsoManager.class)
public class SaSsoBeanInject {

	/**
	 * 注入 Sa-Token-SSO 配置Bean 
	 * 
	 * @param saSsoConfig 配置对象 
	 */
	@Autowired(required = false)
	public void setSaOAuth2Config(SaSsoConfig saSsoConfig) {
		SaSsoManager.setConfig(saSsoConfig);
	}

	/**
	 * 注入 Sa-Token-SSO 单点登录模块 Bean
	 * 
	 * @param ssoTemplate saSsoTemplate对象 
	 */
	@Autowired(required = false)
	public void setSaSsoTemplate(SaSsoTemplate ssoTemplate) {
		SaSsoUtil.ssoTemplate = ssoTemplate;
		SaSsoProcessor.instance.ssoTemplate = ssoTemplate;
	}

}

package cn.dev33.satoken.spring.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoTemplate;
import cn.dev33.satoken.sso.SaSsoUtil;

/**
 * 注入 Sa-Token-SSO 所需要的 Bean
 * 
 * @author click33
 * @since <= 1.34.0
 */
@ConditionalOnClass(SaSsoManager.class)
public class SaSsoBeanInject {

	/**
	 * 注入 Sa-Token-SSO 配置类
	 * 
	 * @param saSsoConfig 配置对象 
	 */
	@Autowired(required = false)
	public void setSaOAuth2Config(SaSsoConfig saSsoConfig) {
		SaSsoManager.setConfig(saSsoConfig);
	}

	/**
	 * 注入 SSO 模板代码类
	 * 
	 * @param ssoTemplate SaSsoTemplate 对象
	 */
	@Autowired(required = false)
	public void setSaSsoTemplate(SaSsoTemplate ssoTemplate) {
		SaSsoUtil.ssoTemplate = ssoTemplate;
		SaSsoProcessor.instance.ssoTemplate = ssoTemplate;
	}

}

package cn.dev33.satoken.reactor.spring.sso;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoManager;

/**
 * 注册 Sa-Token-SSO 所需要的Bean 
 * @author kong
 *
 */
@ConditionalOnClass(SaSsoManager.class)
public class SaSsoBeanRegister {

	/**
	 * 获取 SSO 配置Bean 
	 * @return 配置对象 
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa-token.sso")
	public SaSsoConfig getSaSsoConfig() {
		return new SaSsoConfig();
	}
	
}

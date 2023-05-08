package cn.dev33.satoken.spring.oauth2;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;

/**
 * 注册 Sa-Token-OAuth2 所需要的Bean 
 * @author click33
 *
 */
@ConditionalOnClass(SaOAuth2Manager.class)
public class SaOAuth2BeanRegister {

	/**
	 * 获取OAuth2配置Bean 
	 * @return 配置对象 
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa-token.oauth2")
	public SaOAuth2Config getSaOAuth2Config() {
		return new SaOAuth2Config();
	}
	
}

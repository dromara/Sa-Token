package cn.dev33.satoken.spring.oauth2;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 注册 Sa-Token-OAuth2 所需要的Bean
 *
 * @author click33
 * @since <= 1.34.0
 */
@ConditionalOnClass(SaOAuth2Manager.class)
public class SaOAuth2BeanRegister {

	/**
	 * 获取 OAuth2 配置对象
	 *
	 * @return 配置对象 
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa-token.oauth2")
	public SaOAuth2Config getSaOAuth2Config() {
		return new SaOAuth2Config();
	}
	
}

package com.pj.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.oauth2.config.SaOAuth2Config;

/**
 * 注册Bean 
 * 
 * @author kong
 *
 */
@Component
public class SaOAuth2BeanRegister {

	/**
	 * 获取OAuth2配置Bean
	 * 
	 * @return 配置对象
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa-token.oauth2")
	public SaOAuth2Config getSaOAuth2Config() {
		return new SaOAuth2Config();
	}

}

package cn.dev33.satoken.spring;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.spring.json.SaJsonTemplateForJackson;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 注册Sa-Token所需要的Bean 
 * <p> Bean 的注册与注入应该分开在两个文件中，否则在某些场景下会造成循环依赖 
 * @author kong
 *
 */
public class SaBeanRegister {

	/**
	 * 获取配置Bean
	 * 
	 * @return 配置对象
	 */
	@Bean
	@ConfigurationProperties(prefix = "sa-token")
	public SaTokenConfig getSaTokenConfig() {
		return new SaTokenConfig();
	}

	/**
	 * 获取 json 转换器 Bean (Jackson版)
	 * 
	 * @return json 转换器 Bean (Jackson版)
	 */
	@Bean
	public SaJsonTemplate getSaJsonTemplateForJackson() {
		return new SaJsonTemplateForJackson();
	}
	
}

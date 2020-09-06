package cn.dev33.satoken.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.stp.StpInterface;

/**
 * 与SpringBoot集成, 保证此类被扫描，即可完成sa-token与SpringBoot的集成 
 * @author kong
 *
 */
@Component
public class SpringSaToken {

	
	/**
	 * 获取配置Bean 
	 * @return
	 */
	@Bean
	@ConfigurationProperties(prefix="spring.sa-token")
	public SaTokenConfig getSaTokenConfig() {
		return new SaTokenConfig();
	}
	
	/**
	 * 注入配置Bean 
	 * @param saTokenConfig .
	 */
	@Autowired
	public void setConfig(SaTokenConfig saTokenConfig){
		SaTokenManager.setConfig(saTokenConfig);
	}

	/**
	 * 注入持久化Bean 
	 * @param dao .
	 */
	@Autowired(required = false)
	public void setDao(SaTokenDao dao){
		SaTokenManager.setDao(dao);
	}

	/**
	 * 注入权限认证Bean 
	 * @param stp .
	 */
	@Autowired(required = false)
	public void setStp(StpInterface stp){
		SaTokenManager.setStp(stp);
	}
	
	
}

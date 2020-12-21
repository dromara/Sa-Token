package cn.dev33.satoken.autowired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.action.SaTokenAction;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.cookie.SaTokenCookie;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.servlet.SaTokenServlet;
import cn.dev33.satoken.spring.SaTokenServletSpringImpl;
import cn.dev33.satoken.stp.StpInterface;

/**
 * 利用spring的自动装配来加载开发者重写的Bean 
 * @author kong
 *
 */
@Component
public class SaTokenSpringAutowired {

	
	/**
	 * 获取配置Bean 
	 * @return .
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
	 * @param saTokenDao .
	 */
	@Autowired(required = false)
	public void setSaTokenDao(SaTokenDao saTokenDao){
		SaTokenManager.setSaTokenDao(saTokenDao);
	}

	/**
	 * 注入权限认证Bean 
	 * @param stpInterface .
	 */
	@Autowired(required = false)
	public void setStpInterface(StpInterface stpInterface){
		SaTokenManager.setStpInterface(stpInterface);
	}

	/**
	 * 注入Cookie操作Bean 
	 * @param saTokenCookie .
	 */
	@Autowired(required = false)
	public void setSaTokenCookie(SaTokenCookie saTokenCookie){
		SaTokenManager.setSaTokenCookie(saTokenCookie);
	}

	/**
	 * 注入框架行为Bean 
	 * @param saTokenAction .
	 */
	@Autowired(required = false)
	public void setSaTokenAction(SaTokenAction saTokenAction){
		SaTokenManager.setSaTokenAction(saTokenAction);
	}

	/**
	 * 获取Servlet操作Bean (Spring版) 
	 * @return Servlet操作Bean (Spring版) 
	 */
	@Bean
	public SaTokenServlet getSaTokenServlet() {
		return new SaTokenServletSpringImpl();
	}
	
	/**
	 * 注入Servlet操作Bean 
	 * @param saTokenServlet .
	 */
	@Autowired
	public void setSaTokenServlet(SaTokenServlet saTokenServlet){
		SaTokenManager.setSaTokenServlet(saTokenServlet);
	}
	
	
}

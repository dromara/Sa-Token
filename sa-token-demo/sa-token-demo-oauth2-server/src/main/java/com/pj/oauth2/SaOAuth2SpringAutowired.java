package com.pj.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;

/**
 * 利用Spring完成自动装配
 * 
 * @author kong
 *
 */
@Component
public class SaOAuth2SpringAutowired {

	/**
	 * 注入OAuth2配置Bean
	 * 
	 * @param saOAuth2Config 配置对象 
	 */
	@Autowired
	public void setSaOAuth2Config(SaOAuth2Config saOAuth2Config) {
		SaOAuth2Manager.setConfig(saOAuth2Config);
	}

	/**
	 * 注入OAuth2接口Bean 
	 * 
	 * @param saOAuth2Interface OAuth2接口Bean 
	 */
	@Autowired(required = false)
	public void setSaOAuth2Interface(SaOAuth2Template saOAuth2Interface) {
		SaOAuth2Util.saOAuth2Template = saOAuth2Interface;
	}
	
}

package com.pj.satoken;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;


/**
 * [Sa-Token 权限认证] 配置类 
 * @author click33
 *
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

	/**
	 * 注册 Sa-Token 拦截器打开注解鉴权功能  
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册 Sa-Token 拦截器打开注解鉴权功能 
		registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
	}
	
    /**
     * Sa-Token 整合 jwt 
     */
	@Bean
    public StpLogic getStpLogicJwt() {
    	return new StpLogicJwtForSimple();
    }
    
}

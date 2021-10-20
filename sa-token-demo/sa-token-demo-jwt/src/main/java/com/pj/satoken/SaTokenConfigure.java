package com.pj.satoken;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForTokenStyle;
import cn.dev33.satoken.stp.StpLogic;


/**
 * [Sa-Token 权限认证] 配置类 
 * @author kong
 *
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	
	/**
	 * 注册Sa-Token 的拦截器，打开注解式鉴权功能 
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册注解拦截器 
		registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");
	}
	
    /**
     * Sa-Token 整合 jwt 
     */
	@Bean
    public StpLogic getStpLogicJwt() {
    	return new StpLogicJwtForTokenStyle();
    }
    
}

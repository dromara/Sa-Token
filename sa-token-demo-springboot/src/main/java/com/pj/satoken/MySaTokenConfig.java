package com.pj.satoken;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;

/**
 * sa-token代码方式进行配置
 * @author kong
 *
 */
@Configuration
public class MySaTokenConfig implements WebMvcConfigurer {

	// 注册sa-token的拦截器，打开注解式鉴权功能 
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册注解拦截器 
		registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**");
	}
	
}

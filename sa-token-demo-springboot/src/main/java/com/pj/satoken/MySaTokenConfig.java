package com.pj.satoken;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.annotation.SaCheckInterceptor;

/**
 * sa-token代码方式进行配置
 */
@Configuration
public class MySaTokenConfig implements WebMvcConfigurer {

	// 获取配置Bean (以代码的方式配置sa-token)
//	@Primary
//	@Bean(name="MySaTokenConfig")
//	public SaTokenConfig getSaTokenConfig() {
//		SaTokenConfig config = new SaTokenConfig();
//		config.setTokenName("satoken");		// token名称（同时也是cookie名称）
//		config.setTimeout(30 * 24 * 60 * 60); 	// token有效期，单位s 默认30天
//		config.setIsShare(true);				// 在多人登录同一账号时，是否共享会话（为true时共用一个，为false时新登录挤掉旧登录）
//		config.setIsReadHead(true);		// 是否在cookie读取不到token时，继续从请求header里继续尝试读取 
//		config.setIsReadBody(true);		// 是否在cookie读取不到token时，继续从请求header里继续尝试读取 
//		config.setIsV(true);					// 是否在初始化配置时打印版本字符画
//		return config;
//	}
	
	// 注册sa-token的拦截器，打开注解式鉴权功能 
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SaCheckInterceptor()).addPathPatterns("/**");	// 全局拦截器
	}
	
	
}

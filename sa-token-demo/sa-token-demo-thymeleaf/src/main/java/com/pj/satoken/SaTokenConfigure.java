package com.pj.satoken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.thymeleaf.dialect.SaTokenDialect;


/**
 * [Sa-Token 权限认证] 配置类 
 * @author click33
 *
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	
	// Sa-Token 标签方言 (Thymeleaf版)
	@Bean
	public SaTokenDialect getSaTokenDialect() {
		return new SaTokenDialect();
	}

    // 为 Thymeleaf 注入全局变量，以便在页面中调用 Sa-Token 的方法 
    @Autowired
    private void configureThymeleafStaticVars(ThymeleafViewResolver viewResolver) {
    	viewResolver.addStaticVariable("stp", StpUtil.stpLogic);
    }
	
}

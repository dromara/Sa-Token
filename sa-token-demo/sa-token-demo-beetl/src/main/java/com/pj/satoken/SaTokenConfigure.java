package com.pj.satoken;

import cn.dev33.satoken.stp.StpUtil;
import com.ibeetl.starter.BeetlTemplateCustomize;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * [Sa-Token 权限认证] 配置类 
 * @author click33
 *
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

	// 为 Beetl 视图引擎注册自定义函数：
	// 	通过 stp.xxx() 调用 StpUtil.stpLogic 对象上所有 public 方法
	@Bean
	public BeetlTemplateCustomize beetlTemplateCustomize(){
		return groupTemplate -> groupTemplate.registerFunctionPackage("stp", StpUtil.stpLogic);
	}

}

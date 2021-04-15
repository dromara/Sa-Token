package com.pj.satoken;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pj.util.AjaxJson;

import cn.dev33.satoken.filter.SaFilterErrorStrategy;
import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouterUtil;
import cn.dev33.satoken.stp.StpUtil;

/**
 * [Sa-Token 权限认证] 配置类 
 * @author kong
 *
 */
@Configuration
public class SaTokenConfigure {

	/**
	 * 注册 [sa-token全局过滤器] 
	 */
	@Bean
	public SaReactorFilter getSaReactorFilter() {
		return new SaReactorFilter()
        		.addInclude("/**")
        		.addExclude("/favicon.ico");
	}
	
	/**
	 * 注册 [sa-token全局过滤器-认证策略] 
	 */
	@Bean
	public SaFilterAuthStrategy getSaFilterStrategy() {
		return r -> {
			System.out.println("---------- 进入sa-token全局过滤器 -----------");
			SaRouterUtil.match("/test/test333", () -> StpUtil.checkLogin());
//            SaRouterUtil.match("/user/**", () -> StpUtil.checkPermission("user"));
//            SaRouterUtil.match("/admin/**", () -> StpUtil.checkPermission("admin"));
//            SaRouterUtil.match("/goods/**", () -> StpUtil.checkPermission("goods"));
//            SaRouterUtil.match("/orders/**", () -> StpUtil.checkPermission("orders"));
//            SaRouterUtil.match("/notice/**", () -> StpUtil.checkPermission("notice"));
//            SaRouterUtil.match("/comment/**", () -> StpUtil.checkPermission("comment"));
		};
	}
	
	/**
	 * 注册 [sa-token全局过滤器-异常处理策略] 
	 */
	@Bean
	public SaFilterErrorStrategy getSaFilterErrorStrategy() {
		return e -> AjaxJson.getError(e.getMessage());
	}
	
	
	
}

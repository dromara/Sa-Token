package com.pj.satoken;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pj.util.AjaxJson;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;

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
        		// 指定 [拦截路由]
        		.addInclude("/**")
        		// 指定 [放行路由]
        		.addExclude("/favicon.ico")
        		// 指定[认证函数]: 每次请求执行 
        		.setAuth(r -> {
        			System.out.println("---------- sa全局认证");
                    // SaRouterUtil.match("/test/test", () -> StpUtil.checkLogin());
        		})
        		// 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数 
        		.setError(e -> {
        			System.out.println("---------- sa全局异常 ");
        			return AjaxJson.getError(e.getMessage());
        		})
        		;
    }
	
}

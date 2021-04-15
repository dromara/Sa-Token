package com.pj.satoken;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;


/**
 * [Sa-Token 权限认证] 配置类 
 * @author kong
 *
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

	/**
	 * 注册sa-token的拦截器，打开注解式鉴权功能 
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册注解拦截器 
		registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**").excludePathPatterns("");
	}
	
//	/**
//     * 注册 [sa-token全局过滤器] 
//     */
//    @Bean
//    public SaServletFilter getSaReactorFilter() {
//        return new SaServletFilter()
//        		// 指定 [拦截路由]
//        		.addInclude("/**")
//        		// 指定 [放行路由]
//        		.addExclude("/favicon.ico")
//        		// 指定[认证函数]: 每次请求执行 
//        		.setAuth(r -> {
//        			System.out.println("---------- sa全局认证");
//                    SaRouterUtil.match("/test/test", () -> StpUtil.checkLogin());
//        		})
//        		// 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数 
//        		.setError(e -> {
//        			System.out.println("---------- sa全局异常 ");
//        			return AjaxJson.getError(e.getMessage());
//        		})
//        		;
//    }
    
}

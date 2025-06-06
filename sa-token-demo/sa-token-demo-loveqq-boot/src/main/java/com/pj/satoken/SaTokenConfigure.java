package com.pj.satoken;

import cn.dev33.satoken.loveqq.boot.filter.SaRequestFilter;
import cn.dev33.satoken.util.SaResult;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Configuration;

/**
 * [Sa-Token 权限认证] 配置类 
 * @author click33
 *
 */
@Configuration
public class SaTokenConfigure {

	/**
     * 注册 [sa-token全局过滤器] 
     */
    @Bean
    public SaRequestFilter getSaReactorFilter() {
        return new SaRequestFilter()
        		// 指定 [拦截路由]
        		.addInclude("/**")
        		// 指定 [放行路由]
        		.addExclude("/favicon.ico")
        		// 指定[认证函数]: 每次请求执行 
        		.setAuth(r -> {
        			System.out.println("---------- sa全局认证");
                    // SaRouter.match("/test/test", () -> StpUtil.checkLogin());
        		})
        		// 指定[异常处理函数]：每次[认证函数]发生异常时执行此函数 
        		.setError(e -> {
        			System.out.println("---------- sa全局异常 ");
					e.printStackTrace();
        			return SaResult.error(e.getMessage());
        		})
        		;
    }
}

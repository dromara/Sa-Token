package cn.dev33.satoken.integrate.router;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 相关配置类
 * 
 * @author kong
 * @since: 2022-9-2
 */
@Configuration
@SuppressWarnings("deprecation")
public class SaTokenConfigure3 implements WebMvcConfigurer {
	
    // 
	@Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 的注解拦截器 
        registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/rt/getInfo_200");
        
        // 注册 Sa-Token 的路由拦截器 
        registry.addInterceptor(SaRouteInterceptor.newInstance((req, res, handler)->{
        	SaRouter.stop();
        })).addPathPatterns("/rt/getInfo_200");
        
        // 注册 Sa-Token 的路由拦截器 
        registry.addInterceptor(SaRouteInterceptor.newInstance((req, res, handler)->{
        })).addPathPatterns("/rt/getInfo_201");
        registry.addInterceptor(SaRouteInterceptor.newInstance((req, res, handler)->{
        	SaRouter.back(SaResult.code(201));
        })).addPathPatterns("/rt/getInfo_201");
        
        // 注册 Sa-Token 的路由拦截器 
        registry.addInterceptor(new SaRouteInterceptor()).addPathPatterns("/rt/getInfo_202");
    }
    
}


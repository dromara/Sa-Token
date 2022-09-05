package cn.dev33.satoken.integrate.router;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 相关配置类
 * 
 * @author kong
 * @since: 2022-9-2
 */
@Configuration
public class SaTokenConfigure2 implements WebMvcConfigurer {
	
    // 路由鉴权
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 路由鉴权
        registry.addInterceptor(new SaInterceptor(handle -> {
        	
        	// 匹配 getInfo ，返回code=201 
        	SaRouter.match("/**")
        		.match(SaHttpMethod.POST)
        		.matchMethod("POST")
        		.match(SaHolder.getRequest().getMethod().equals("POST"))
        		.match(r -> SaHolder.getRequest().isPath("/rt/getInfo"))
        		.match(r -> SaHolder.getRequest().isParam("name", "zhang"))
        		.back(SaResult.code(201));

        	// 匹配 getInfo2 ，返回code=202 
        	SaRouter.match("/rt/getInfo2")
        		.match(Arrays.asList("/rt/getInfo2", "/rt/*"))
        		.notMatch("/rt/getInfo3")
        		.notMatch(false)
        		.notMatch(r -> false)
        		.notMatch(SaHttpMethod.GET)
        		.notMatchMethod("PUT")
        		.notMatch(Arrays.asList("/rt/getInfo4", "/rt/getInfo5"))
        		.back(SaResult.code(202));

        	// 匹配 getInfo3 ，返回code=203 
        	SaRouter.match("/rt/getInfo3", "/rt/getInfo4", () -> SaRouter.back(SaResult.code(203)));
        	SaRouter.match("/rt/getInfo4", "/rt/getInfo5", r -> SaRouter.back(SaResult.code(204)));
        	SaRouter.match("/rt/getInfo5", () -> SaRouter.back(SaResult.code(205)));
        	SaRouter.match("/rt/getInfo6", r -> SaRouter.back(SaResult.code(206)));
        	
        	// 通往 Controller  
        	SaRouter.match(Arrays.asList("/rt/getInfo7")).stop();

        	// 通往 Controller  
        	SaRouter.match("/rt/getInfo8", () -> SaRouter.stop());
        	
        	SaRouter.matchMethod("POST").match("/rt/getInfo9").free(r -> SaRouter.back(SaResult.code(209)));
        	SaRouter.match(SaHttpMethod.POST).match("/rt/getInfo10").setHit(false).back();
        	
        	// 11
        	SaRouter.notMatch("/rt/getInfo11").reset().match("/rt/getInfo11").back(SaResult.code(211));
        	SaRouter.notMatch(SaHttpMethod.GET).match("/rt/getInfo12").back(SaResult.code(212));
        	SaRouter.notMatch(Arrays.asList("/rt/getInfo12", "/rt/getInfo14")).match("/rt/getInfo13").back(SaResult.code(213));
        	SaRouter.notMatchMethod("GET", "PUT").match("/rt/getInfo14").back(SaResult.code(214));
        	
//        	SaRouter.match(Arrays.asList("/rt/getInfo15", "/rt/getInfo16"))
        	if(SaRouter.isMatchCurrURI("/rt/getInfo15")) {
    			if(SaHolder.getRequest().getCookieValue("ddd") == null
    					&& SaHolder.getStorage().getSource() == SpringMVCUtil.getRequest()
    					&& SaHolder.getRequest().getSource() == SpringMVCUtil.getRequest()
    					&& SaHolder.getResponse().getSource() == SpringMVCUtil.getResponse()
    					) {
    				SaRouter.newMatch().free(r -> SaRouter.back(SaResult.code(215)));
    			}
        	}
        	
        	SaRouter.match("/rt/getInfo16", () -> {
        		Assertions.assertThrows(Exception.class, () -> SaHolder.getResponse().redirect(null));
        		SaHolder.getResponse().redirect("/rt/getInfo3");
        	});
        	
        })).addPathPatterns("/**");
    }
    
}


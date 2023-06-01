package cn.dev33.satoken.spring;

import cn.dev33.satoken.interceptor.SaInterceptor;
import jakarta.annotation.Resource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class SaTokenInterceptorRegister implements WebMvcConfigurer {

  @Resource
  private SaDynamicRouterExecutor saDynamicRouterExecutor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new SaInterceptor(r -> {
      saDynamicRouterExecutor.execute();
    }));
  }
}

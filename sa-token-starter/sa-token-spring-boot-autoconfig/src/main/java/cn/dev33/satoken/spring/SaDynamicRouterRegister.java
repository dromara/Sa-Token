package cn.dev33.satoken.spring;

import cn.dev33.satoken.config.SaRouterConfig;
import org.springframework.context.annotation.Bean;

/**
 * 当且仅当 sa-token.router.enable=true 时初始化动态路由执行器
 *
 * @author einsitang
 */
public class SaDynamicRouterRegister {

  @Bean
  public SaDynamicRouterExecutor dynamicRouterExecutor(SaRouterConfig routerConfig) {
    SaDynamicRouterExecutor executor = new SaDynamicRouterExecutor();
    executor.reset(routerConfig.getEnable(), routerConfig.getRules());
    return executor;
  }

}

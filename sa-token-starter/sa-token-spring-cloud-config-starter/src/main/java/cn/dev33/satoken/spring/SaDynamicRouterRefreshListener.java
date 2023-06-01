package cn.dev33.satoken.spring;

import cn.dev33.satoken.config.SaRouterConfig;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;

/**
 * 动态路由刷新监听器
 * <p>
 * 通过对 EnvironmentChangeEvent 事件进行监听，触发sa-token.router 则对动态路由执行器进行刷新/重置
 *
 * @author einsitang
 */
public class SaDynamicRouterRefreshListener {

  public SaDynamicRouterRefreshListener(SaDynamicRouterRefreshConfig saDynamicRouterRefreshInject,
      SaDynamicRouterExecutor saDynamicRouterExecutor) {
    this.saDynamicRouterRefreshInject = saDynamicRouterRefreshInject;
    this.saDynamicRouterExecutor = saDynamicRouterExecutor;
  }

  private final SaDynamicRouterRefreshConfig saDynamicRouterRefreshInject;

  private final SaDynamicRouterExecutor saDynamicRouterExecutor;

  @EventListener(EnvironmentChangeEvent.class)
  public void onRefresh(EnvironmentChangeEvent event) {
    for (String key : event.getKeys()) {
      if (key.startsWith("sa-token.router")) {

        SaRouterConfig config = saDynamicRouterRefreshInject.getSaRouterConfig();
        saDynamicRouterExecutor.reset(config.getEnable(), config.getRules());
        break;
      }
    }
  }

}

package cn.dev33.satoken.spring;

import cn.dev33.satoken.config.SaRouterConfig;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;


/**
 * 动态路由刷新配置
 * <p>
 * 使用 @RefreshScope 对 SaRouterConfig 进行动态配置，是的其成为Proxy类，方便后续使用注入
 *
 * @author einsitang
 */
@Component
@RefreshScope
public class SaDynamicRouterRefreshConfig {

  /**
   * SaRouterConfig
   * <p>
   * 被 @RefreshScope 包装后由原来的"单例注入"变成"代理单例注入"
   */
  private final SaRouterConfig saRouterConfig;

  public SaDynamicRouterRefreshConfig(SaRouterConfig saRouterConfig) {
    this.saRouterConfig = saRouterConfig;
  }

  public SaRouterConfig getSaRouterConfig() {
    return this.saRouterConfig;
  }

}

package cn.dev33.satoken.spring.annotation;

import cn.dev33.satoken.spring.SaDynamicRouterRefreshConfig;
import cn.dev33.satoken.spring.SaDynamicRouterRefreshListener;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 开启动态路由注解
 * <p>
 * 配合 配置服务 将可以实现动态路由刷新
 * <p>
 * 仅需要在SpringBoot/Cloud 项目启动时加入 @EnableRefreshDynamicRouter
 *
 * @author einsitang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SaDynamicRouterRefreshListener.class, SaDynamicRouterRefreshConfig.class})
public @interface EnableRefreshDynamicRouter {

}

package demo;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.handle.Filter;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.solon.integration.SaTokenPathFilter;
import cn.dev33.satoken.stp.StpUtil;

/**
 * @author noear 2022/3/30 created
 */
@Configuration
public class Config {

	@Bean
    public Filter saTokenFilter() {
        return new SaTokenPathFilter()
                // 指定 [拦截路由] 与 [放行路由]
                .addInclude("/**").addExclude("/favicon.ico")

                // 认证函数: 每次请求执行
                .setAuth(s -> {
                    SaRouter.match("/**", StpUtil::checkLogin);

                    // 根据路由划分模块，不同模块不同鉴权
                    SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
                    SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
                    SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
                    SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
                })

                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    System.out.println("---------- sa全局异常 ");
                    System.out.println(e.getMessage());
                    StpUtil.login(123);
                    return e.getMessage();
                });
    }
}

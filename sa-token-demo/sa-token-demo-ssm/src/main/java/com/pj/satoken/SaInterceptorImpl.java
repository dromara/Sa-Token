package com.pj.satoken;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.interceptor.SaInterceptor;

/**
 * 路由拦截鉴权测试
 * @author click33
 * @since 2024/4/15
 */
public class SaInterceptorImpl extends SaInterceptor {

    public SaInterceptorImpl() {
        super(hadnle->{
            System.out.println("-------------- SA 路由拦截鉴权，你访问的是：" + SaHolder.getRequest().getRequestPath());
            // System.out.println("你访问的是：" + SaHolder.getRequest().getRequestPath());
            // SaRouter.match("/test/test", r -> StpUtil.checkLogin());

            // 根据路由划分模块，不同模块不同鉴权
//            SaRouter.match("/user/**", r -> StpUtil.checkPermission("user"));
//            SaRouter.match("/admin/**", r -> StpUtil.checkPermission("admin"));
//            SaRouter.match("/goods/**", r -> StpUtil.checkPermission("goods"));
//            SaRouter.match("/orders/**", r -> StpUtil.checkPermission("orders"));
//            SaRouter.match("/notice/**", r -> StpUtil.checkPermission("notice"));
//            SaRouter.match("/comment/**", r -> StpUtil.checkPermission("comment"));

            // 更多写法参考：https://sa-token.cc/doc.html#/use/route-check

        });
    }

}
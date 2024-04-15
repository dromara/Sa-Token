package com.pj.satoken;

import cn.dev33.satoken.application.ApplicationInfo;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.spring.SaBeanInject;
import cn.dev33.satoken.spring.SaTokenContextForSpring;
import cn.dev33.satoken.spring.json.SaJsonTemplateForJackson;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 手动注入 Sa-Token 所需要的组件
 * @author click33
 * @since 2024/4/15
 */
public class SaTokenBeanInjection {

    public SaTokenBeanInjection(
            SaLog log,
            SaTokenConfig config,
            RedisConnectionFactory connectionFactory,
            String routePrefix
    ) {
        System.out.println("---------------- 手动注入 Sa-Token 所需要的组件 start ----------------");

        // 日志组件、配置信息
        SaBeanInject inject = new SaBeanInject(log, config);

        // 基于 Spring 的上下文处理器
        inject.setSaTokenContext(new SaTokenContextForSpring());

        // 基于 Jackson 的 json解析器
        inject.setSaJsonTemplate(new SaJsonTemplateForJackson());

        // 基于 Jackson 序列化的 Redis 持久化组件
        SaTokenDaoRedisJackson saTokenDaoRedisJackson = new SaTokenDaoRedisJackson();
        saTokenDaoRedisJackson.init(connectionFactory);
        inject.setSaTokenDao(saTokenDaoRedisJackson);

        // 权限和角色数据
        inject.setStpInterface(new StpInterfaceImpl());

        // 项目路由前缀，方便路由拦截鉴权的
        ApplicationInfo.routePrefix = routePrefix;
        // System.out.println(routePrefix);

        // 注入更多组件 ....
        // inject.setXxx

        System.out.println("---------------- 手动注入 Sa-Token 所需要的组件 end ----------------");
    }

}
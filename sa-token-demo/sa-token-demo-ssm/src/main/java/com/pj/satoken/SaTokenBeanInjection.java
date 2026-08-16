package com.pj.satoken;

import cn.dev33.satoken.application.ApplicationInfo;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate;
import cn.dev33.satoken.json.SaJsonTemplateForJackson;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.plugin.SaTokenPluginHolder;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;
import cn.dev33.satoken.spring.SaBeanInject;
import cn.dev33.satoken.spring.pathmatch.SaPatternsRequestConditionHolder;
import cn.dev33.satoken.strategy.SaStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 手动注入 Sa-Token 所需要的组件
 * @author click33
 * @since 2024/4/15
 */
public class SaTokenBeanInjection {

    public SaTokenBeanInjection(
            SaLog log,
            SaTokenConfig config,
            @Autowired(required = false) SaTokenPluginHolder pluginHolder,
            RedisConnectionFactory connectionFactory,
            String routePrefix
    ) {
        System.out.println("---------------- 手动注入 Sa-Token 所需要的组件 start ----------------");

        // 注册 Sa-Token 策略（非 SpringBoot 集成时需手动注册，与 SaTokenContextRegister 保持一致）
        SaStrategy.instance.routeMatcher = (pattern, path) -> SaPatternsRequestConditionHolder.match(pattern, path);
        SaStrategy.instance.createSaRequest = source -> new SaRequestForServlet((HttpServletRequest) source);
        SaStrategy.instance.createSaResponse = source -> new SaResponseForServlet((HttpServletResponse) source);
        SaStrategy.instance.createSaStorage = source -> new SaStorageForServlet((HttpServletRequest) source);

        // 日志组件、配置信息（上下文由 web.xml 中的 SaTokenContextFilter 初始化，默认使用 ThreadLocal 方案）
        SaBeanInject inject = new SaBeanInject(log, config, pluginHolder);

        // 基于 Jackson 的 json解析器
        inject.setSaJsonTemplate(new SaJsonTemplateForJackson());

        // 基于 Jackson 序列化的 Redis 持久化组件
        SaTokenDaoForRedisTemplate saTokenDaoForRedisTemplate = new SaTokenDaoForRedisTemplate();
        saTokenDaoForRedisTemplate.init(connectionFactory);
        inject.setSaTokenDao(saTokenDaoForRedisTemplate);

        // 权限和角色数据
        inject.setStpInterface(new StpInterfaceImpl());

        // 项目路由前缀，方便路由拦截鉴权的
        ApplicationInfo.routePrefix = routePrefix;

        // 注入更多组件 ....
        // inject.setXxx

        System.out.println("---------------- 手动注入 Sa-Token 所需要的组件 end ----------------");
    }

}

package com.pj.redisson;

import org.redisson.config.SingleServerConfig;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redisson 配置
 *
 * @author 疯狂的狮子Li
 */
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonConfig {

    @Autowired
    private RedissonProperties redissonProperties;

    /**
     * 自定义Redisson配置注入器 被RedissonAutoConfiguration调用执行
     * 具体参考 {@link org.redisson.spring.starter.RedissonAutoConfiguration}
     * <p/>
     * 使用自定义配置类手动注入配置数据
     * 也可根据redisson官网使用properties文件配置
     */
    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {
        return config -> {
            config.setThreads(redissonProperties.getThreads());
            config.setNettyThreads(redissonProperties.getNettyThreads());
            SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
            if (singleServerConfig != null) {
                // 使用单机模式
                config.useSingleServer()
                    .setTimeout(singleServerConfig.getTimeout())
                    .setClientName(singleServerConfig.getClientName())
                    .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                    .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                    .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                    .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
            }

        };
    }


}

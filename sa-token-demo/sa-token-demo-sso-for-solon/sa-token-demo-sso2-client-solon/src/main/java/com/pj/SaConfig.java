package com.pj;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoOfRedis;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import com.dtflys.forest.Forest;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2023/3/13 created
 */
@Configuration
public class SaConfig {

    /**
     * 配置 Sa-Token 单独使用的Redis连接 （此处需要和SSO-Server端连接同一个Redis）
     * */
    @Bean
    public SaTokenDao saTokenDaoInit(@Inject("${sa-token.dao.redis}") SaTokenDaoOfRedis saTokenDao) {
        return saTokenDao;
    }

    @Bean
    public void configSso(SaSsoClientConfig ssoClient) {
        // 配置Http请求处理器
        ssoClient.sendHttp = url -> {
            System.out.println("------ 发起请求：" + url);
            String resStr = Forest.get(url).executeAsString();
            System.out.println("------ 请求结果：" + resStr);
            return resStr;
        };
    }

}

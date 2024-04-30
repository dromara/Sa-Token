package com.pj.sso;

import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import com.dtflys.forest.Forest;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author noear 2023/3/13 created
 */
@Configuration
public class SsoConfig {
    // 配置SSO相关参数
    @Bean
    private void configSso(SaSsoClientConfig ssoClient) {
        // 配置Http请求处理器
        ssoClient.sendHttp = url -> {
            System.out.println("------ 发起请求：" + url);
            return Forest.get(url).executeAsString();
        };
    }
}

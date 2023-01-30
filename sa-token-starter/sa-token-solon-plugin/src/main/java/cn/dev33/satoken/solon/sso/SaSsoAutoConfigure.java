package cn.dev33.satoken.solon.sso;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoTemplate;
import cn.dev33.satoken.sso.SaSsoUtil;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AopContext;

/**
 * @author noear
 * @since 2.0
 */
@Configuration
public class SaSsoAutoConfigure {
    /**
     * 获取 SSO 配置Bean
     * */
    @Bean
    public SaSsoConfig getConfig(@Inject(value = "${sa-token.sso}",required = false) SaSsoConfig ssoConfig) {
        return ssoConfig;
    }

    /**
     * 注入 Sa-Token-SSO 配置Bean
     *
     * @param saSsoConfig 配置对象
     */
    @Bean
    public void setSaSsoConfig(@Inject(required = false) SaSsoConfig saSsoConfig) {
        SaSsoManager.setConfig(saSsoConfig);
    }

    /**
     * 注入 Sa-Token-SSO 单点登录模块 Bean
     *
     * @param ssoTemplate saSsoTemplate对象
     */
    @Bean
    public void setSaSsoTemplate(@Inject(required = false) SaSsoTemplate ssoTemplate) {
        SaSsoUtil.ssoTemplate = ssoTemplate;
        SaSsoProcessor.instance.ssoTemplate = ssoTemplate;
    }
}

package cn.dev33.satoken.solon.oauth2;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear
 * @since 2.0
 */
@Configuration
public class SaOAuth2AutoConfigure {
    /**
     * 获取 OAuth2配置Bean
     */
    @Bean
    public SaOAuth2Config getConfig(@Inject(value = "${sa-token.oauth2}",required = false) SaOAuth2Config oAuth2Config) {
        return oAuth2Config;
    }

    /**
     * 注入OAuth2配置Bean
     *
     * @param saOAuth2Config 配置对象
     */
    @Bean
    public void setSaOAuth2Config(@Inject(required = false) SaOAuth2Config saOAuth2Config) {
        SaOAuth2Manager.setConfig(saOAuth2Config);
    }

    /**
     * 注入代码模板Bean
     *
     * @param saOAuth2Template 代码模板Bean
     */
    @Bean
    public void setSaOAuth2Interface(@Inject(required = false) SaOAuth2Template saOAuth2Template) {
        SaOAuth2Util.saOAuth2Template = saOAuth2Template;
    }
}

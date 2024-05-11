//package com.pj.sso;
//
//import cn.dev33.satoken.sso.SaSsoManager;
//import cn.dev33.satoken.sso.config.SaSsoClientConfig;
//import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
//import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
//import org.noear.solon.annotation.Bean;
//import org.noear.solon.annotation.Condition;
//import org.noear.solon.annotation.Configuration;
//import org.noear.solon.annotation.Inject;
//
///**
// * solon 的 sso 适配，在 cn.dev33:sa-token-solon-plugin:1.34.0 里还没有。（临时加这个类）
// *
// * //todo: 如果使用 org.noear:sa-token-solon-plugin:xxx ，则需要删掉这个类
// *
// * @author noear
// * @since 2.0
// */
//@Condition(onClass = SaSsoManager.class)
//@Configuration
//public class SaSsoAutoConfigure {
//    /**
//     * 获取 SSO 配置Bean
//     * */
//    @Bean
//    public SaSsoClientConfig getConfig(@Inject(value = "${sa-token.sso-client}",required = false) SaSsoClientConfig ssoConfig) {
//        return ssoConfig;
//    }
//
//    /**
//     * 注入 Sa-Token-SSO 配置Bean
//     *
//     * @param saSsoConfig 配置对象
//     */
//    @Bean
//    public void setSaSsoConfig(@Inject(required = false) SaSsoClientConfig saSsoConfig) {
//        SaSsoManager.setClientConfig(saSsoConfig);
//    }
//
//    /**
//     * 注入 Sa-Token-SSO 单点登录模块 Bean
//     *
//     * @param ssoClientTemplate ssoClientTemplate对象
//     */
//    @Bean
//    public void setSaSsoClientTemplate(@Inject(required = false) SaSsoClientTemplate ssoClientTemplate) {
//        SaSsoClientProcessor.instance.ssoClientTemplate = ssoClientTemplate;
//    }
//}

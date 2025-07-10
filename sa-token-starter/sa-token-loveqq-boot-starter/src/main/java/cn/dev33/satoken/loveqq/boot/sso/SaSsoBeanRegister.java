/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.loveqq.boot.sso;

import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.ConfigurationProperties;
import com.kfyty.loveqq.framework.core.autoconfig.condition.annotation.ConditionalOnClass;
import com.kfyty.loveqq.framework.core.autoconfig.condition.annotation.ConditionalOnMissingBean;

/**
 * 注册 Sa-Token SSO 所需要的 Bean
 *
 * @author click33
 * @since 1.34.0
 */
@Component
@ConditionalOnClass("cn.dev33.satoken.sso.SaSsoManager")
public class SaSsoBeanRegister {
    /**
     * 获取 SSO Server 端 配置对象
     *
     * @return 配置对象
     */
    @Bean
    @ConfigurationProperties("sa-token.sso-server")
    public SaSsoServerConfig getSaSsoServerConfig() {
        return new SaSsoServerConfig();
    }

    /**
     * 获取 SSO Client 端 配置对象
     *
     * @return 配置对象
     */
    @Bean
    @ConfigurationProperties("sa-token.sso-client")
    public SaSsoClientConfig getSaSsoClientConfig() {
        return new SaSsoClientConfig();
    }

    /**
     * 获取 SSO Server 端 SaSsoServerTemplate
     *
     * @return /
     */
    @Bean
    @ConditionalOnMissingBean(SaSsoServerTemplate.class)
    public SaSsoServerTemplate getSaSsoServerTemplate() {
        return SaSsoServerProcessor.instance.ssoServerTemplate;
    }

    /**
     * 获取 SSO Client 端 SaSsoClientTemplate
     *
     * @return /
     */
    @Bean
    @ConditionalOnMissingBean(SaSsoClientTemplate.class)
    public SaSsoClientTemplate getSaSsoClientTemplate() {
        return SaSsoClientProcessor.instance.ssoClientTemplate;
    }
}

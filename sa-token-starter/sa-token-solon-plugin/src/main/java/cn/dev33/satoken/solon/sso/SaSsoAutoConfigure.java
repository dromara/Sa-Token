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
package cn.dev33.satoken.solon.sso;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.sso.SaSsoTemplate;
import cn.dev33.satoken.sso.SaSsoUtil;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear
 * @since 2.0
 */
@Condition(onClass = SaSsoManager.class)
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

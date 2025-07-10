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

import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.condition.annotation.ConditionalOnClass;

/**
 * 注入 Sa-Token SSO 所需要的 Bean
 *
 * @author click33
 * @since 1.34.0
 */
@Component
@ConditionalOnClass("cn.dev33.satoken.sso.SaSsoManager")
public class SaSsoBeanInject {
    /**
     * 注入 Sa-Token SSO Server 端 配置类
     *
     * @param serverConfig 配置对象
     */
    @Autowired(required = false)
    public void setSaSsoServerConfig(SaSsoServerConfig serverConfig) {
        SaSsoManager.setServerConfig(serverConfig);
    }

    /**
     * 注入 Sa-Token SSO Client 端 配置类
     *
     * @param clientConfig 配置对象
     */
    @Autowired(required = false)
    public void setSaSsoClientConfig(SaSsoClientConfig clientConfig) {
        SaSsoManager.setClientConfig(clientConfig);
    }

    /**
     * 注入 SSO 模板代码类 (Server 端)
     *
     * @param ssoServerTemplate /
     */
    @Autowired(required = false)
    public void setSaSsoServerTemplate(SaSsoServerTemplate ssoServerTemplate) {
        SaSsoServerProcessor.instance.ssoServerTemplate = ssoServerTemplate;
    }

    /**
     * 注入 SSO 模板代码类 (Client 端)
     *
     * @param ssoClientTemplate /
     */
    @Autowired(required = false)
    public void setSaSsoClientTemplate(SaSsoClientTemplate ssoClientTemplate) {
        SaSsoClientProcessor.instance.ssoClientTemplate = ssoClientTemplate;
    }
}

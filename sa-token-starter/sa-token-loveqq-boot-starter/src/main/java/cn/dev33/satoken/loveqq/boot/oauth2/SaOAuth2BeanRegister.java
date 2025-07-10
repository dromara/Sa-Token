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
package cn.dev33.satoken.loveqq.boot.oauth2;

import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Bean;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.ConfigurationProperties;
import com.kfyty.loveqq.framework.core.autoconfig.condition.annotation.ConditionalOnClass;

/**
 * 注册 Sa-Token-OAuth2 所需要的Bean
 *
 * @author click33
 * @since 1.34.0
 */
@Component
@ConditionalOnClass("cn.dev33.satoken.oauth2.SaOAuth2Manager")
public class SaOAuth2BeanRegister {
    /**
     * 获取 OAuth2 配置 Bean
     *
     * @return 配置对象
     */
    @Bean
    @ConfigurationProperties("sa-token.oauth2-server")
    public SaOAuth2ServerConfig getSaOAuth2Config() {
        return new SaOAuth2ServerConfig();
    }
}

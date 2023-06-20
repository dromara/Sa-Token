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
package cn.dev33.satoken.solon.oauth2;

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Template;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear
 * @since 2.0
 */
@Condition(onClass = SaOAuth2Manager.class)
@Configuration
public class SaOAuth2AutoConfigure {
    /**
     * 获取 OAuth2配置Bean
     */
    @Bean
    public SaOAuth2Config getConfig(@Inject(value = "${sa-token.oauth2}", required = false) SaOAuth2Config oAuth2Config) {
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

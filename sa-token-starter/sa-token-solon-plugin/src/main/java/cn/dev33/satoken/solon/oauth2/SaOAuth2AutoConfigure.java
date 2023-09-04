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
import org.noear.solon.core.AppContext;
import org.noear.solon.core.bean.InitializingBean;

/**
 * @author noear
 * @since 2.0
 */

@Condition(onClass = SaOAuth2Manager.class)
@Configuration
public class SaOAuth2AutoConfigure implements InitializingBean {
    @Inject
    private AppContext appContext;

    @Override
    public void afterInjection() throws Throwable {
        appContext.subBeansOfType(SaOAuth2Template.class, bean -> {
            SaOAuth2Util.saOAuth2Template = bean;
        });

        appContext.subBeansOfType(SaOAuth2Config.class, bean -> {
            SaOAuth2Manager.setConfig(bean);
        });
    }

    /**
     * 获取 OAuth2配置Bean
     */
    @Bean
    public SaOAuth2Config getConfig(@Inject(value = "${sa-token.oauth2}", required = false) SaOAuth2Config oAuth2Config) {
        return oAuth2Config;
    }
}
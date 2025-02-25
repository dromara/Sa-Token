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
package cn.dev33.satoken.solon;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.solon.json.SaJsonTemplateForSnack3;
import cn.dev33.satoken.solon.model.SaContextForSolon;
import cn.dev33.satoken.solon.oauth2.SaOAuth2AutoConfigure;
import cn.dev33.satoken.solon.sso.SaSsoBeanInject;
import cn.dev33.satoken.solon.sso.SaSsoBeanRegister;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.4
 */
public class SaSolonPlugin implements Plugin {

    @Override
    public void start(AppContext context) {
        // 注入上下文Bean
        SaManager.setSaTokenContext(new SaContextForSolon());

        // 注入JSON解析器Bean
        SaManager.setSaJsonTemplate(new SaJsonTemplateForSnack3());

        //sa-token
        context.beanMake(SaBeanRegister.class);
        context.beanMake(SaBeanInject.class);

        //sa-sso
        context.beanMake(SaSsoBeanRegister.class);
        context.beanMake(SaSsoBeanInject.class);

        //sa-oauth2
        context.beanMake(SaOAuth2AutoConfigure.class);
    }
}
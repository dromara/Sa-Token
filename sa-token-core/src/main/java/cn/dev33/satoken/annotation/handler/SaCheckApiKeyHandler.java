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
package cn.dev33.satoken.annotation.handler;

import cn.dev33.satoken.annotation.SaCheckApiKey;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.apikey.SaApiKeyUtil;
import cn.dev33.satoken.context.SaHolder;

import java.lang.reflect.Method;

/**
 * 注解 SaCheckApiKey 的处理器
 *
 * @author click33
 * @since 1.42.0
 */
public class SaCheckApiKeyHandler implements SaAnnotationHandlerInterface<SaCheckApiKey> {

    @Override
    public Class<SaCheckApiKey> getHandlerAnnotationClass() {
        return SaCheckApiKey.class;
    }

    @Override
    public void checkMethod(SaCheckApiKey at, Method method) {
        _checkMethod(at.scope(), at.mode());
    }

    public static void _checkMethod(String[] scope, SaMode mode) {
        String apiKey = SaApiKeyUtil.readApiKeyValue(SaHolder.getRequest());
        if(mode == SaMode.AND) {
            SaApiKeyUtil.checkApiKeyScope(apiKey, scope);
        } else {
            SaApiKeyUtil.checkApiKeyScopeOr(apiKey, scope);
        }

    }

}
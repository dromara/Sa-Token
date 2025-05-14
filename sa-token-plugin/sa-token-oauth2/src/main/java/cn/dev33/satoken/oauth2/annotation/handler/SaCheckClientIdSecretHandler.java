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
package cn.dev33.satoken.oauth2.annotation.handler;

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.oauth2.annotation.SaCheckClientIdSecret;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;

import java.lang.reflect.AnnotatedElement;

/**
 * 注解 SaCheckClientSecret 的处理器
 *
 * @author click33
 * @since 1.39.0
 */
public class SaCheckClientIdSecretHandler implements SaAnnotationHandlerInterface<SaCheckClientIdSecret> {

    @Override
    public Class<SaCheckClientIdSecret> getHandlerAnnotationClass() {
        return SaCheckClientIdSecret.class;
    }

    @Override
    public void checkMethod(SaCheckClientIdSecret at, AnnotatedElement element) {
        _checkMethod();
    }

    public static void _checkMethod() {
        SaOAuth2ServerProcessor.instance.checkCurrClientSecret();
    }

}
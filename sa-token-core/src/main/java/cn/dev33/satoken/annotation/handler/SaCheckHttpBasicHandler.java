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

import cn.dev33.satoken.annotation.SaCheckHttpBasic;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;

import java.lang.reflect.Method;

/**
 * 注解 SaCheckHttpBasic 的处理器
 *
 * @author click33
 * @since 2024/8/2
 */
public class SaCheckHttpBasicHandler implements SaAnnotationHandlerInterface<SaCheckHttpBasic> {

    @Override
    public Class<SaCheckHttpBasic> getHandlerAnnotationClass() {
        return SaCheckHttpBasic.class;
    }

    @Override
    public void checkMethod(SaCheckHttpBasic at, Method method) {
        _checkMethod(at.realm(), at.account());
    }

    public static void _checkMethod(String realm, String account) {
        SaHttpBasicUtil.check(realm, account);
    }

}
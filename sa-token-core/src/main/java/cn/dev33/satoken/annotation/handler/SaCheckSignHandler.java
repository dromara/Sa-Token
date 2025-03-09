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

import cn.dev33.satoken.annotation.SaCheckSign;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sign.SaSignUtil;

import java.lang.reflect.Method;

/**
 * 注解 SaCheckSign 的处理器
 *
 * @author click33
 * @since 1.41.0
 */
public class SaCheckSignHandler implements SaAnnotationHandlerInterface<SaCheckSign> {

    @Override
    public Class<SaCheckSign> getHandlerAnnotationClass() {
        return SaCheckSign.class;
    }

    @Override
    public void checkMethod(SaCheckSign at, Method method) {
        _checkMethod(at.verifyParams());
    }

    public static void _checkMethod(String[] params) {
        SaSignUtil.checkRequest(SaHolder.getRequest(), params);
    }

}
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

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.router.SaRouter;

import java.lang.reflect.Method;

/**
 * 注解 SaIgnore 的处理器
 *
 * @author click33
 * @since 2024/8/2
 */
public class SaIgnoreHandler implements SaAnnotationHandlerInterface<SaIgnore> {

    @Override
    public Class<SaIgnore> getHandlerAnnotationClass() {
        return SaIgnore.class;
    }

    @Override
    public void checkMethod(SaIgnore at, Method method) {
        _checkMethod();
    }

    public static void _checkMethod() {
        SaRouter.stop();
    }

}
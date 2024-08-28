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
package cn.dev33.satoken.aop;

import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * Sa-Token 注解方法拦截器 AOP环绕切入
 *
 * @author click33
 * @since 1.39.0
 */
public class SaAroundAnnotationMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // 注解鉴权
        try{
            Method method = invocation.getMethod();
            SaAnnotationStrategy.instance.checkMethodAnnotation.accept(method);
        } catch (StopMatchException ignored) {
        }
        // 执行原有防范
        return invocation.proceed();
    }

}
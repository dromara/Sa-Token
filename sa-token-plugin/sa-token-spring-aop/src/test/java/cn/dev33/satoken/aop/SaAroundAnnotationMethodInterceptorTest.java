/*
 * Copyright 2020-2099 sa-token.com
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

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

/**
 * {@link SaAroundAnnotationMethodInterceptor} 环绕拦截：鉴权通过、忽略、未登录抛异常
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaAroundAnnotationMethodInterceptorTest {

    private final SaAroundAnnotationMethodInterceptor interceptor = new SaAroundAnnotationMethodInterceptor();

    /** 没有鉴权注解的方法应该直接执行原方法 */
    @Test
    public void invoke_plainMethod_proceeds() throws Throwable {
        Demo target = new Demo();
        StubInvocation invocation = StubInvocation.of(target, "plain");
        Object result = interceptor.invoke(invocation);
        Assertions.assertEquals("plain", result);
        Assertions.assertEquals(1, invocation.proceedCount);
    }

    /** 未登录访问 @SaCheckLogin 方法时应该抛 NotLoginException，且原方法不能执行 */
    @Test
    public void invoke_checkLogin_notLogin_throws() {
        Demo target = new Demo();
        StubInvocation invocation = StubInvocation.of(target, "needLogin");
        SaTokenContextMockUtil.setMockContext(() -> {
            Assertions.assertThrows(NotLoginException.class, () -> interceptor.invoke(invocation));
        });
        Assertions.assertEquals(0, invocation.proceedCount);
    }

    /** 已登录访问 @SaCheckLogin 方法时应该放行并执行原方法 */
    @Test
    public void invoke_checkLogin_loggedIn_proceeds() throws Throwable {
        Demo target = new Demo();
        StubInvocation invocation = StubInvocation.of(target, "needLogin");
        Object result = SaTokenContextMockUtil.setMockContext(() -> {
            StpUtil.login(10001);
            try {
                return interceptor.invoke(invocation);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        Assertions.assertEquals("login", result);
        Assertions.assertEquals(1, invocation.proceedCount);
    }

    /** 标了 @SaIgnore 时应该吞掉 StopMatchException 并继续执行原方法 */
    @Test
    public void invoke_saIgnore_swallowsStopMatchAndProceeds() throws Throwable {
        Demo target = new Demo();
        StubInvocation invocation = StubInvocation.of(target, "ignored");
        Object result = interceptor.invoke(invocation);
        Assertions.assertEquals("ignored", result);
        Assertions.assertEquals(1, invocation.proceedCount);
    }

    public static class Demo {
        public String plain() {
            return "plain";
        }

        @SaCheckLogin
        public String needLogin() {
            return "login";
        }

        @SaIgnore
        @SaCheckLogin
        public String ignored() {
            return "ignored";
        }
    }

    /** 给拦截器喂一个最小 MethodInvocation */
    static class StubInvocation implements MethodInvocation {
        private final Object target;
        private final Method method;
        private int proceedCount;

        static StubInvocation of(Object target, String methodName) {
            try {
                return new StubInvocation(target, target.getClass().getMethod(methodName));
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }

        private StubInvocation(Object target, Method method) {
            this.target = target;
            this.method = method;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Object[] getArguments() {
            return new Object[0];
        }

        @Override
        public Object proceed() throws Throwable {
            proceedCount++;
            return method.invoke(target);
        }

        @Override
        public Object getThis() {
            return target;
        }

        @Override
        public AccessibleObject getStaticPart() {
            return method;
        }
    }

}

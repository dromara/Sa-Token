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
package cn.dev33.satoken.jfinal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.jfinal.testsupport.JfinalTestHelper;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import com.jfinal.aop.Invocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link SaAnnotationInterceptor} 注解鉴权拦截器测试
 */
@SaTokenTest
public class SaAnnotationInterceptorTest {

    /** 方法上没有鉴权注解时，应该直接放行 */
    @Test
    public void intercept_plainMethod_shouldInvoke() throws Exception {
        Invocation invocation = mock(Invocation.class);
        when(invocation.getMethod()).thenReturn(AnnoTarget.class.getMethod("plain"));

        new SaAnnotationInterceptor().intercept(invocation);

        verify(invocation).invoke();
    }

    /** 标了登录校验但还没登录时，应该拦住，不能往下 invoke */
    @Test
    public void intercept_checkLogin_whenNotLogin_shouldThrow() throws Exception {
        Method method = AnnoTarget.class.getMethod("needLogin");
        Invocation invocation = mock(Invocation.class);
        when(invocation.getMethod()).thenReturn(method);

        HttpServletRequest request = JfinalTestHelper.mockRequest("/secure");
        HttpServletResponse response = JfinalTestHelper.mockResponse();
        JfinalTestHelper.withHeldController(request, response, () -> {
            Assertions.assertThrows(NotLoginException.class,
                    () -> new SaAnnotationInterceptor().intercept(invocation));
        });
        verify(invocation, never()).invoke();
    }

    /** 已经登录时，登录校验应该通过并继续 invoke */
    @Test
    public void intercept_checkLogin_whenLogin_shouldInvoke() throws Exception {
        Method method = AnnoTarget.class.getMethod("needLogin");
        Invocation invocation = mock(Invocation.class);
        when(invocation.getMethod()).thenReturn(method);

        HttpServletRequest request = JfinalTestHelper.mockRequest("/secure");
        HttpServletResponse response = JfinalTestHelper.mockResponse();
        JfinalTestHelper.withHeldController(request, response, () -> {
            StpUtil.login(10001);
            new SaAnnotationInterceptor().intercept(invocation);
        });
        verify(invocation).invoke();
    }

    /** 给拦截器提供带注解的目标方法 */
    public static class AnnoTarget {
        public void plain() {
        }

        @SaCheckLogin
        public void needLogin() {
        }
    }
}

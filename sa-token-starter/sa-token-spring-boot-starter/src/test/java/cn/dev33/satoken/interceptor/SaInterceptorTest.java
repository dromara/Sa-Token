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
package cn.dev33.satoken.interceptor;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.SaParamFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link SaInterceptor} 构造、配置与 preHandle 分支测试
 */
public class SaInterceptorTest {

    static class DemoController {
        public String hello() {
            return "ok";
        }
    }

    /** 无参/有参构造和链式配置应该能正常赋值 */
    @Test
    public void constructorsAndFluentApi() {
        AtomicReference<Object> captured = new AtomicReference<>();
        SaParamFunction<Object> auth = captured::set;

        SaInterceptor interceptor1 = new SaInterceptor();
        SaInterceptor interceptor2 = new SaInterceptor(auth);

        interceptor1.setAuth(auth).setBeforeAuth(auth).isAnnotation(false);

        Assertions.assertFalse(interceptor1.isAnnotation);
        Assertions.assertSame(auth, interceptor1.auth);
        Assertions.assertSame(auth, interceptor2.auth);
    }

    /** handler 不是 HandlerMethod 时，关掉注解鉴权后应该直接走 auth 并返回 true */
    @Test
    public void preHandle_nonHandlerMethod_pass() throws Exception {
        SaInterceptor interceptor = new SaInterceptor(h -> {}).isAnnotation(false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean pass = interceptor.preHandle(request, response, new Object());

        Assertions.assertTrue(pass);
    }

    /** beforeAuth 和 auth 都应该被执行到 */
    @Test
    public void preHandle_runBeforeAuthAndAuth() throws Exception {
        AtomicReference<Object> before = new AtomicReference<>();
        AtomicReference<Object> auth = new AtomicReference<>();
        HandlerMethod handlerMethod = handlerMethod("hello");

        SaInterceptor interceptor = new SaInterceptor()
                .isAnnotation(false)
                .setBeforeAuth(before::set)
                .setAuth(auth::set);

        Assertions.assertTrue(interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handlerMethod));
        Assertions.assertSame(handlerMethod, before.get());
        Assertions.assertSame(handlerMethod, auth.get());
    }

    /** auth 抛 StopMatchException 时应该吞掉并返回 true */
    @Test
    public void preHandle_stopMatch_pass() throws Exception {
        SaInterceptor interceptor = new SaInterceptor(h -> {
            throw new StopMatchException();
        }).isAnnotation(false);

        boolean pass = interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handlerMethod("hello"));

        Assertions.assertTrue(pass);
    }

    /** auth 抛 BackResultException 时应该写回响应并返回 false */
    @Test
    public void preHandle_backResult_writeResponse() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        SaInterceptor interceptor = new SaInterceptor(h -> {
            throw new BackResultException("no");
        }).isAnnotation(false);

        boolean pass = interceptor.preHandle(new MockHttpServletRequest(), response, handlerMethod("hello"));

        Assertions.assertFalse(pass);
        Assertions.assertEquals("no", response.getContentAsString());
        Assertions.assertEquals("text/plain; charset=utf-8", response.getContentType());
    }

    /** auth 抛 BackResultException 时如果 response 已有 Content-Type，拦截器不应该覆盖 */
    @Test
    public void preHandle_backResult_keepExistingContentType() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setContentType("application/json");
        SaInterceptor interceptor = new SaInterceptor(h -> {
            throw new BackResultException("json");
        }).isAnnotation(false);

        interceptor.preHandle(new MockHttpServletRequest(), response, handlerMethod("hello"));

        Assertions.assertEquals("application/json", response.getContentType());
    }

    /** 开启注解鉴权且 handler 为 HandlerMethod 时应该执行注解检查并继续通过 */
    @Test
    public void preHandle_withAnnotationEnabled_pass() throws Exception {
        SaInterceptor interceptor = new SaInterceptor(h -> {});
        HandlerMethod handlerMethod = handlerMethod("hello");

        boolean pass = interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handlerMethod);

        Assertions.assertTrue(pass);
    }

    /** 开启注解鉴权但 handler 不是 HandlerMethod 时应该跳过注解检查 */
    @Test
    public void preHandle_withAnnotationEnabled_nonHandlerMethod_pass() throws Exception {
        SaInterceptor interceptor = new SaInterceptor(h -> {});

        boolean pass = interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), new Object());

        Assertions.assertTrue(pass);
    }

    private static HandlerMethod handlerMethod(String methodName) throws NoSuchMethodException {
        Method method = DemoController.class.getMethod(methodName);
        return new HandlerMethod(new DemoController(), method);
    }

}

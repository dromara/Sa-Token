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
package cn.dev33.satoken.jboot;

import cn.dev33.satoken.jboot.testsupport.JbootTestHelper;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import io.jboot.web.controller.JbootControllerContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * {@link SaTokenContextForJboot} 上下文读写测试
 */
@SaTokenTest
public class SaTokenContextForJbootTest {

    /** 每个用例结束后把 Controller 线程绑定清掉 */
    @AfterEach
    public void tearDown() {
        JbootControllerContext.release();
    }

    /** 没绑 Controller 时 isValid 应该是 false */
    @Test
    public void isValid_withoutController() {
        SaTokenContextForJboot context = new SaTokenContextForJboot();
        Assertions.assertFalse(context.isValid());
    }

    /** hold 住 Controller 之后 isValid 应该是 true */
    @Test
    public void isValid_withController() {
        HttpServletRequest request = JbootTestHelper.mockRequest("/ping");
        HttpServletResponse response = JbootTestHelper.mockResponse();
        JbootTestHelper.withHeldController(request, response, () -> {
            Assertions.assertTrue(new SaTokenContextForJboot().isValid());
        });
    }

    /** getRequest / getResponse / getStorage 应该包的是当前 Controller 里的 Servlet 对象 */
    @Test
    public void getRequestResponseStorage_fromHeldController() {
        HttpServletRequest request = JbootTestHelper.mockRequest("/user/1");
        HttpServletResponse response = JbootTestHelper.mockResponse();
        JbootTestHelper.withHeldController(request, response, () -> {
            SaTokenContextForJboot context = new SaTokenContextForJboot();
            Assertions.assertTrue(context.getRequest() instanceof SaRequestForServlet);
            Assertions.assertTrue(context.getResponse() instanceof SaResponseForServlet);
            Assertions.assertTrue(context.getStorage() instanceof SaStorageForServlet);
            Assertions.assertSame(request, context.getRequest().getSource());
            Assertions.assertSame(response, context.getResponse().getSource());
            Assertions.assertSame(request, context.getStorage().getSource());
            Assertions.assertEquals("/user/1", context.getRequest().getRequestPath());
        });
    }

    /** 构造器应该把路由匹配和 Servlet 包装策略挂到 SaStrategy 上 */
    @Test
    public void constructor_shouldInstallStrategy() {
        new SaTokenContextForJboot();
        Assertions.assertTrue(SaStrategy.instance.routeMatcher.apply("/user/{id}", "/user/9"));
        Assertions.assertFalse(SaStrategy.instance.routeMatcher.apply("/user/{id}", "/order/9"));

        HttpServletRequest request = JbootTestHelper.mockRequest("/x");
        HttpServletResponse response = JbootTestHelper.mockResponse();
        Assertions.assertTrue(SaStrategy.instance.createSaRequest.apply(request) instanceof SaRequestForServlet);
        Assertions.assertTrue(SaStrategy.instance.createSaResponse.apply(response) instanceof SaResponseForServlet);
        Assertions.assertTrue(SaStrategy.instance.createSaStorage.apply(request) instanceof SaStorageForServlet);
    }
}

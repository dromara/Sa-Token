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
package cn.dev33.satoken.spring;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.exception.NotWebContextException;
import cn.dev33.satoken.servlet.model.SaRequestForServlet;
import cn.dev33.satoken.servlet.model.SaResponseForServlet;
import cn.dev33.satoken.servlet.model.SaStorageForServlet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * {@link SaTokenContextForSpring} 基于 SpringMVCUtil 的上下文包装测试
 */
public class SaTokenContextForSpringTest {

    private final SaTokenContextForSpring context = new SaTokenContextForSpring();

    @AfterEach
    public void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    /** 非 Web 上下文时 isValid 应该返回 false，取 request 应该抛异常 */
    @Test
    public void withoutWebContext_invalid() {
        Assertions.assertFalse(context.isValid());
        Assertions.assertThrows(NotWebContextException.class, context::getRequest);
    }

    /** Web 上下文下应该能包装出 Servlet 版 Request/Response/Storage */
    @Test
    public void withWebContext_wrapServletModels() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/hello");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

        Assertions.assertTrue(context.isValid());

        SaRequest saRequest = context.getRequest();
        SaResponse saResponse = context.getResponse();
        SaStorage saStorage = context.getStorage();

        Assertions.assertInstanceOf(SaRequestForServlet.class, saRequest);
        Assertions.assertInstanceOf(SaResponseForServlet.class, saResponse);
        Assertions.assertInstanceOf(SaStorageForServlet.class, saStorage);
        Assertions.assertSame(request, saRequest.getSource());
        Assertions.assertSame(response, saResponse.getSource());
        Assertions.assertSame(request, saStorage.getSource());
    }

}

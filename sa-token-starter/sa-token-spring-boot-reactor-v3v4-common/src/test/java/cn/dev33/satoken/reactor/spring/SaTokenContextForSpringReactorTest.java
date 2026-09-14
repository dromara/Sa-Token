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
package cn.dev33.satoken.reactor.spring;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.exception.SaTokenContextException;
import cn.dev33.satoken.testsupport.ReactorTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.server.MockServerWebExchange;

/**
 * {@link SaTokenContextForSpringReactor} 基于 ThreadLocal 的上下文包装测试
 */
public class SaTokenContextForSpringReactorTest {

    private final SaTokenContextForSpringReactor context = new SaTokenContextForSpringReactor();

    /** 未设置上下文时 isValid 应该返回 false，取 request 应该抛异常 */
    @Test
    public void withoutContext_invalid() {
        Assertions.assertFalse(context.isValid());
        Assertions.assertThrows(SaTokenContextException.class, context::getRequest);
    }

    /** setContext 之后应该能包装出 Reactor 版 Request/Response/Storage */
    @Test
    public void withContext_wrapReactorModels() {
        ReactorTestHelper.ensureReactorStrategy();
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/hello");

        context.setContext(
                new cn.dev33.satoken.reactor.model.SaRequestForReactor(exchange.getRequest()),
                new cn.dev33.satoken.reactor.model.SaResponseForReactor(exchange.getResponse()),
                new cn.dev33.satoken.reactor.model.SaStorageForReactor(exchange));

        Assertions.assertTrue(context.isValid());
        SaRequest saRequest = context.getRequest();
        SaResponse saResponse = context.getResponse();
        SaStorage saStorage = context.getStorage();
        Assertions.assertInstanceOf(cn.dev33.satoken.reactor.model.SaRequestForReactor.class, saRequest);
        Assertions.assertInstanceOf(cn.dev33.satoken.reactor.model.SaResponseForReactor.class, saResponse);
        Assertions.assertInstanceOf(cn.dev33.satoken.reactor.model.SaStorageForReactor.class, saStorage);
        Assertions.assertSame(exchange.getRequest(), saRequest.getSource());
        Assertions.assertSame(exchange.getResponse(), saResponse.getSource());
        Assertions.assertSame(exchange, saStorage.getSource());

        context.clearContext();
        Assertions.assertFalse(context.isValid());
    }

}

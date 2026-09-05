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
package cn.dev33.satoken.reactor.context;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.testsupport.ReactorTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.server.MockServerWebExchange;

/**
 * {@link SaReactorSyncHolder} ThreadLocal 上下文同步工具测试
 */
@SaTokenTest
public class SaReactorSyncHolderTest {

    /** 初始化 Reactor 策略，保证用例跑在 Reactor 版请求模型上 */
    @BeforeEach
    public void setUp() {
        ReactorTestHelper.ensureReactorStrategy();
    }

    /** setContext 之后应该能通过 getExchange 取回 exchange，clearContext 之后应该失效 */
    @Test
    public void setContext_getAndClear() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/sync");

        SaReactorSyncHolder.setContext(exchange);
        Assertions.assertSame(exchange, SaReactorSyncHolder.getExchange());

        SaReactorSyncHolder.clearContext();
        Assertions.assertFalse(SaHolder.getContext().isValid());
    }

    /** setContext(exchange, fun) 应该在上下文中执行函数并返回结果，执行完自动清理 */
    @Test
    public void setContext_withFunction_autoClear() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/sync");

        String result = SaReactorSyncHolder.setContext(exchange,
                () -> "path=" + cn.dev33.satoken.context.SaHolder.getRequest().getRequestPath());

        Assertions.assertEquals("path=/sync", result);
        Assertions.assertFalse(SaHolder.getContext().isValid());
    }


    /** 默认构造函数应该能 new 出来 */
    @Test
    public void constructor_instantiable() {
        Assertions.assertNotNull(new SaReactorSyncHolder());
    }
}

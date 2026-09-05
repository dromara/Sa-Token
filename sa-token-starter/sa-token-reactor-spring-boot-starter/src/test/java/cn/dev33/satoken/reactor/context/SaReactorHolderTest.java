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

import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.testsupport.ReactorTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * {@link SaReactorHolder} Reactor Context 读写工具测试
 */
@SaTokenTest
public class SaReactorHolderTest {

    private MockServerWebExchange exchange;

    /** 初始化 Reactor 策略，保证用例跑在 Reactor 版请求模型上 */
    @BeforeEach
    public void setUp() {
        ReactorTestHelper.ensureReactorStrategy();
        exchange = ReactorTestHelper.newGetExchange("/holder");
    }

    /** setContext 写入的 exchange 和 chain 应该能通过对应 getter 读回 */
    @Test
    public void setContext_getExchangeAndGetChain() {
        WebFilterChain chain = e -> Mono.empty();

        Context ctx = SaReactorHolder.setContext(Context.empty(), exchange, chain);

        Assertions.assertSame(exchange, SaReactorHolder.getExchange(ctx));
        Assertions.assertSame(chain, SaReactorHolder.getChain(ctx));
    }

    /** getMonoExchange 应该能从订阅上下文中取回 exchange */
    @Test
    public void getMonoExchange() {
        WebFilterChain chain = e -> Mono.empty();

        org.springframework.web.server.ServerWebExchange seen = SaReactorHolder.getMonoExchange()
                .contextWrite(ctx -> SaReactorHolder.setContext(ctx, exchange, chain))
                .block();

        Assertions.assertSame(exchange, seen);
    }

    /** sync 应该在 Reactor Context 存在时恢复 ThreadLocal 上下文执行函数并返回结果 */
    @Test
    public void sync_runFunctionWithExchange() {
        WebFilterChain chain = e -> Mono.empty();

        String result = SaReactorHolder.sync(() -> "method=" + cn.dev33.satoken.context.SaHolder.getRequest().getMethod())
                .contextWrite(ctx -> SaReactorHolder.setContext(ctx, exchange, chain))
                .block();

        Assertions.assertEquals("method=GET", result);
    }


    /** 默认构造函数应该能 new 出来 */
    @Test
    public void constructor_instantiable() {
        Assertions.assertNotNull(new SaReactorHolder());
    }
}

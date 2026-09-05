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
package cn.dev33.satoken.reactor.filter;

import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.testsupport.ReactorTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link SaTokenContextFilterForReactor} 上下文写入测试
 */
@SaTokenTest
public class SaTokenContextFilterForReactorTest {

    /** 初始化 Reactor 策略，保证用例跑在 Reactor 版请求模型上 */
    @BeforeEach
    public void setUp() {
        ReactorTestHelper.ensureReactorStrategy();
    }

    /** filter 应该把 exchange 和 chain 写入 Reactor Context，供异步下游读取 */
    @Test
    public void filter_writeExchangeAndChainToReactorContext() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/any");
        AtomicReference<ServerWebExchange> seenExchange = new AtomicReference<>();
        AtomicReference<Object> seenChain = new AtomicReference<>();
        SaTokenContextFilterForReactor filter = new SaTokenContextFilterForReactor();
        WebFilterChain chain = e -> Mono.deferContextual(ctx -> {
            seenExchange.set(SaReactorHolder.getExchange(ctx));
            seenChain.set(SaReactorHolder.getChain(ctx));
            return Mono.empty();
        });

        filter.filter(exchange, chain).block();

        Assertions.assertSame(exchange, seenExchange.get());
        Assertions.assertSame(chain, seenChain.get());
    }

}

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

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.testsupport.ReactorTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link SaTokenCorsFilterForReactor} CORS 策略执行与异常分支测试
 */
@SaTokenTest
public class SaTokenCorsFilterForReactorTest {

    private SaCorsHandleFunction backupCorsHandle;

    /** 备份 CORS 策略字段，用例内替换为自定义实现 */
    @BeforeEach
    public void setUp() {
        ReactorTestHelper.ensureReactorStrategy();
        backupCorsHandle = SaStrategy.instance.corsHandle;
    }

    /** 恢复原 CORS 策略字段 */
    @AfterEach
    public void tearDown() {
        SaStrategy.instance.corsHandle = backupCorsHandle;
    }

    /** corsHandle 正常执行后应该继续走 FilterChain */
    @Test
    public void doFilter_corsPass_continueChain() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        AtomicBoolean corsCalled = new AtomicBoolean(false);
        SaStrategy.instance.corsHandle = (req, res, sto) -> corsCalled.set(true);
        SaTokenCorsFilterForReactor filter = new SaTokenCorsFilterForReactor();
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/cors");
        WebFilterChain chain = e -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertTrue(corsCalled.get());
        Assertions.assertTrue(chainCalled.get());
    }

    /** corsHandle 抛 StopMatchException 时应该吞掉并继续走链 */
    @Test
    public void doFilter_stopMatch_continueChain() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaStrategy.instance.corsHandle = (req, res, sto) -> {
            throw new StopMatchException();
        };
        SaTokenCorsFilterForReactor filter = new SaTokenCorsFilterForReactor();
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/cors");
        WebFilterChain chain = e -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertTrue(chainCalled.get());
    }

    /** corsHandle 抛 BackResultException 时应该写回响应并中断链条 */
    @Test
    public void doFilter_backResult_writeResponse() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaStrategy.instance.corsHandle = (req, res, sto) -> {
            throw new BackResultException("cors-block");
        };
        SaTokenCorsFilterForReactor filter = new SaTokenCorsFilterForReactor();
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/cors");
        WebFilterChain chain = e -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertEquals("cors-block", exchange.getResponse().getBodyAsString().block());
        Assertions.assertFalse(chainCalled.get());
    }

}

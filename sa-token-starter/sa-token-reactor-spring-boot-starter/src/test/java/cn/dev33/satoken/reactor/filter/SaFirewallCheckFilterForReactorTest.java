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
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.strategy.SaFirewallCheckFailHandleFunction;
import cn.dev33.satoken.fun.strategy.SaFirewallCheckFunction;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
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
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link SaFirewallCheckFilterForReactor} 防火墙校验与异常分支测试
 */
@SaTokenTest
public class SaFirewallCheckFilterForReactorTest {

    private SaFirewallCheckFunction backupCheck;
    private SaFirewallCheckFailHandleFunction backupFailHandle;

    /** 初始化 Reactor 策略并备份防火墙策略字段 */
    @BeforeEach
    public void setUp() {
        ReactorTestHelper.ensureReactorStrategy();
        backupCheck = SaFirewallStrategy.instance.check;
        backupFailHandle = SaFirewallStrategy.instance.checkFailHandle;
    }

    /** 恢复原防火墙策略字段 */
    @AfterEach
    public void tearDown() {
        SaFirewallStrategy.instance.check = backupCheck;
        SaFirewallStrategy.instance.checkFailHandle = backupFailHandle;
    }

    /** 防火墙校验通过时应该继续走 FilterChain */
    @Test
    public void doFilter_checkPass_continueChain() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaFirewallStrategy.instance.check = (req, res, extArg) -> {};
        SaFirewallCheckFilterForReactor filter = new SaFirewallCheckFilterForReactor();
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/safe");
        WebFilterChain chain = e -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertTrue(chainCalled.get());
    }

    /** check 抛 StopMatchException 时应该吞掉并继续走链 */
    @Test
    public void doFilter_stopMatch_continueChain() {
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaFirewallStrategy.instance.check = (req, res, extArg) -> {
            throw new StopMatchException();
        };
        SaFirewallCheckFilterForReactor filter = new SaFirewallCheckFilterForReactor();
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/safe");
        WebFilterChain chain = e -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertTrue(chainCalled.get());
    }

    /** check 抛 BackResultException 时应该写回响应并中断链条 */
    @Test
    public void doFilter_backResult_writeResponse() {
        SaFirewallStrategy.instance.check = (req, res, extArg) -> {
            throw new BackResultException("fw-block");
        };
        SaFirewallCheckFilterForReactor filter = new SaFirewallCheckFilterForReactor();
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/safe");

        filter.filter(exchange, e -> Mono.empty()).block();

        Assertions.assertEquals("fw-block", exchange.getResponse().getBodyAsString().block());
    }

    /** FirewallCheckException 且没有自定义 failHandle 时，应该直接把异常信息写回响应 */
    @Test
    public void doFilter_firewallCheckDefault_writeMessage() {
        SaFirewallStrategy.instance.checkFailHandle = null;
        SaFirewallStrategy.instance.check = (req, res, extArg) -> {
            throw new FirewallCheckException("bad path");
        };
        SaFirewallCheckFilterForReactor filter = new SaFirewallCheckFilterForReactor();
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/bad");

        filter.filter(exchange, e -> Mono.empty()).block();

        Assertions.assertEquals("bad path", exchange.getResponse().getBodyAsString().block());
    }

    /** FirewallCheckException 且配置了 failHandle 时，应该走自定义处理逻辑且不再走链 */
    @Test
    public void doFilter_firewallCheckCustom_useFailHandle() {
        AtomicReference<String> message = new AtomicReference<>();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaFirewallStrategy.instance.check = (req, res, extArg) -> {
            throw new FirewallCheckException("bad path");
        };
        SaFirewallStrategy.instance.checkFailHandle = (e, req, res, extArg) -> message.set("handled:" + e.getMessage());
        SaFirewallCheckFilterForReactor filter = new SaFirewallCheckFilterForReactor();
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/bad");
        WebFilterChain chain = e -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertEquals("handled:bad path", message.get());
        Assertions.assertFalse(chainCalled.get());
    }

}

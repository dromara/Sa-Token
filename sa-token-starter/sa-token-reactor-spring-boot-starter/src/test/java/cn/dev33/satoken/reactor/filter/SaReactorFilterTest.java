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
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.context.SaReactorHolder;
import cn.dev33.satoken.reactor.context.SaReactorSyncHolder;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.testsupport.ReactorTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link SaReactorFilter} 路由配置与 filter 异常分支测试
 *
 * <p> 注意 Reactor 版特有契约：filter 的 finally 会先清掉 ThreadLocal 上下文再放行链条，
 * 异步下游必须通过 Reactor Context（SaReactorHolder.sync）恢复请求上下文
 */
@SaTokenTest
public class SaReactorFilterTest {

    /** 初始化 Reactor 策略，保证用例跑在 Reactor 版请求模型上 */
    @BeforeEach
    public void setUp() {
        ReactorTestHelper.ensureReactorStrategy();
    }

    /** 链式配置 include/exclude 和钩子函数应该能正常读写 */
    @Test
    public void configure_includeExcludeAndHooks() {
        SaReactorFilter filter = new SaReactorFilter()
                .addInclude("/**")
                .setIncludeList(Arrays.asList("/api/**"))
                .addExclude("/favicon.ico")
                .setExcludeList(Arrays.asList("/health"))
                .setAuth(r -> {})
                .setBeforeAuth(r -> {})
                .setError(e -> "err:" + e.getMessage());

        Assertions.assertEquals("/api/**", filter.includeList.get(0));
        Assertions.assertEquals("/health", filter.excludeList.get(0));
        Assertions.assertEquals("err:msg", filter.error.run(new SaTokenException("msg")));
    }

    /** 默认 error 策略应该把异常再包一层 SaTokenException 抛出 */
    @Test
    public void defaultErrorStrategy_rethrow() {
        SaReactorFilter filter = new SaReactorFilter();
        Assertions.assertThrows(SaTokenException.class, () -> filter.error.run(new RuntimeException("x")));
    }

    /** 路由命中且 auth 通过时，应该继续走 FilterChain */
    @Test
    public void doFilter_authPass_continueChain() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/api/user");
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaReactorFilter filter = new SaReactorFilter()
                .addInclude("/**")
                .setAuth(r -> {});
        WebFilterChain chain = e -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertTrue(chainCalled.get());
    }

    /** auth 里抛 StopMatchException 时应该吞掉异常并继续走链 */
    @Test
    public void doFilter_stopMatch_continueChain() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/api/user");
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaReactorFilter filter = new SaReactorFilter()
                .addInclude("/**")
                .setAuth(r -> {
                    throw new StopMatchException();
                });
        WebFilterChain chain = e -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertTrue(chainCalled.get());
    }

    /** auth 里抛 BackResultException 时应该写回响应并中断链条 */
    @Test
    public void doFilter_backResult_writeResponse() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/api/user");
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaReactorFilter filter = new SaReactorFilter()
                .addInclude("/**")
                .setAuth(r -> {
                    throw new BackResultException("blocked");
                });
        WebFilterChain chain = e -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertFalse(chainCalled.get());
        Assertions.assertEquals("blocked", exchange.getResponse().getBodyAsString().block());
    }

    /** auth 里抛普通异常时应该走 error 策略写回响应 */
    @Test
    public void doFilter_authError_useErrorStrategy() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/api/user");
        SaReactorFilter filter = new SaReactorFilter()
                .addInclude("/**")
                .setAuth(r -> {
                    throw new RuntimeException("boom");
                })
                .setError(e -> "handled");

        filter.filter(exchange, e -> Mono.empty()).block();

        Assertions.assertEquals("handled", exchange.getResponse().getBodyAsString().block());
    }

    /** 未命中 include 路由时应该跳过 auth 并继续走链，但 beforeAuth 不受路由限制仍然执行 */
    @Test
    public void doFilter_pathNotIncluded_skipAuthButBeforeAuthRuns() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/public/info");
        AtomicBoolean authCalled = new AtomicBoolean(false);
        AtomicBoolean beforeAuthCalled = new AtomicBoolean(false);
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaReactorFilter filter = new SaReactorFilter()
                .addInclude("/api/**")
                .setAuth(r -> authCalled.set(true))
                .setBeforeAuth(r -> beforeAuthCalled.set(true));
        WebFilterChain chain = e -> {
            chainCalled.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertFalse(authCalled.get());
        Assertions.assertTrue(beforeAuthCalled.get());
        Assertions.assertTrue(chainCalled.get());
    }

    /** Reactor 特有契约：进入异步链条时 ThreadLocal 上下文已经被 filter 的 finally 清空 */
    @Test
    public void doFilter_threadLocalContextClearedBeforeChain() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/api/user");
        AtomicBoolean contextValidAtChain = new AtomicBoolean(true);
        SaReactorFilter filter = new SaReactorFilter()
                .addInclude("/**")
                .setAuth(r -> Assertions.assertTrue(SaHolder.getContext().isValid()));
        WebFilterChain chain = e -> {
            // auth 阶段上下文可用，进入链条时已被 finally 清理
            contextValidAtChain.set(SaHolder.getContext().isValid());
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        Assertions.assertFalse(contextValidAtChain.get());
        SaReactorSyncHolder.clearContext();
    }

    /** 与 SaTokenContextFilterForReactor 组合时，下游应该能通过 Reactor Context 恢复请求上下文 */
    @Test
    public void composedChain_downstreamSyncAccessContext() {
        MockServerWebExchange exchange = ReactorTestHelper.newGetExchange("/api/user");
        AtomicBoolean downstreamValid = new AtomicBoolean(false);
        SaReactorFilter authFilter = new SaReactorFilter()
                .addInclude("/**")
                .setAuth(r -> {});
        WebFilterChain chain = e -> SaReactorHolder.sync(() -> {
            // 下游异步代码通过 sync 恢复 ThreadLocal 上下文后，SaHolder 应该可用
            downstreamValid.set(SaHolder.getContext().isValid()
                    && "GET".equals(SaHolder.getRequest().getMethod()));
            return "ok";
        }).then(Mono.empty());

        new SaTokenContextFilterForReactor()
                .filter(exchange, e -> authFilter.filter(e, chain))
                .block();

        Assertions.assertTrue(downstreamValid.get());
    }

}

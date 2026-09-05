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
package cn.dev33.satoken.filter;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.testsupport.ServletTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link SaServletFilter} 路由配置与 doFilter 异常分支测试
 */
@SaTokenTest
public class SaServletFilterTest {

    @BeforeEach
    public void setUp() {
        ServletTestHelper.ensureServletStrategy();
    }

    /** 链式配置 include/exclude 和钩子函数应该能正常读写 */
    @Test
    public void configure_includeExcludeAndHooks() {
        SaServletFilter filter = new SaServletFilter()
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
        SaServletFilter filter = new SaServletFilter();
        Assertions.assertThrows(SaTokenException.class, () -> filter.error.run(new RuntimeException("x")));
    }

    /** 路由命中且 auth 通过时，应该继续走 FilterChain */
    @Test
    public void doFilter_authPass_continueChain() throws Exception {
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/api/user");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaServletFilter filter = new SaServletFilter()
                .addInclude("/**")
                .setAuth(r -> {});

        ServletTestHelper.withContext(request, response, () -> {
            try {
                filter.doFilter(request, response, (req, res) -> chainCalled.set(true));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Assertions.assertTrue(chainCalled.get());
    }

    /** auth 里抛 StopMatchException 时应该吞掉异常并继续走链 */
    @Test
    public void doFilter_stopMatch_continueChain() throws Exception {
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/api/user");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaServletFilter filter = new SaServletFilter()
                .addInclude("/**")
                .setAuth(r -> {
                    throw new StopMatchException();
                });

        ServletTestHelper.withContext(request, response, () -> {
            try {
                filter.doFilter(request, response, (req, res) -> chainCalled.set(true));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Assertions.assertTrue(chainCalled.get());
    }

    /** auth 里抛 BackResultException 时应该写回响应并中断链条 */
    @Test
    public void doFilter_backResult_writeResponse() throws Exception {
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/api/user");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaServletFilter filter = new SaServletFilter()
                .addInclude("/**")
                .setAuth(r -> {
                    throw new BackResultException("blocked");
                });

        ServletTestHelper.withContext(request, response, () -> {
            try {
                filter.doFilter(request, response, (req, res) -> chainCalled.set(true));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Assertions.assertFalse(chainCalled.get());
        Assertions.assertEquals("blocked", response.getContentAsString());
    }

    /** auth 里抛普通异常时应该走 error 策略写回响应 */
    @Test
    public void doFilter_authError_useErrorStrategy() throws Exception {
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/api/user");
        MockHttpServletResponse response = new MockHttpServletResponse();
        SaServletFilter filter = new SaServletFilter()
                .addInclude("/**")
                .setAuth(r -> {
                    throw new RuntimeException("boom");
                })
                .setError(e -> "handled");

        ServletTestHelper.withContext(request, response, () -> {
            try {
                filter.doFilter(request, response, new MockFilterChain());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Assertions.assertEquals("handled", response.getContentAsString());
    }

    /** 未命中 include 路由时应该跳过 auth 并继续走链 */
    @Test
    public void doFilter_pathNotIncluded_skipAuth() throws Exception {
        MockHttpServletRequest request = ServletTestHelper.newGetRequest("/public/info");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean authCalled = new AtomicBoolean(false);
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        SaServletFilter filter = new SaServletFilter()
                .addInclude("/api/**")
                .setAuth(r -> authCalled.set(true));

        ServletTestHelper.withContext(request, response, () -> {
            try {
                filter.doFilter(request, response, (req, res) -> chainCalled.set(true));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Assertions.assertFalse(authCalled.get());
        Assertions.assertTrue(chainCalled.get());
    }

    /** Filter 生命周期方法应该能正常调用 */
    @Test
    public void initAndDestroy_ok() throws Exception {
        SaServletFilter filter = new SaServletFilter();
        filter.init(null);
        filter.destroy();
    }

}

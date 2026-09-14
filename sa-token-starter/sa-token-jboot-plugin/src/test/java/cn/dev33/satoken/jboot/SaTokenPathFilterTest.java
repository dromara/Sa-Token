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

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.jboot.testsupport.JbootTestHelper;
import cn.dev33.satoken.test.SaTokenTest;
import com.jfinal.aop.Invocation;
import io.jboot.web.controller.JbootControllerContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link SaTokenPathFilter} 路由配置与钩子函数测试
 */
@SaTokenTest
public class SaTokenPathFilterTest {

    /** 链式配置 include/exclude 和钩子函数应该能正常读写 */
    @Test
    public void configure_includeExcludeAndHooks() {
        SaTokenPathFilter filter = new SaTokenPathFilter()
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

    /** addInclude / addExclude 应该是往列表里追加，不是覆盖 */
    @Test
    public void addIncludeAndExclude_shouldAppend() {
        SaTokenPathFilter filter = new SaTokenPathFilter()
                .addInclude("/a", "/b")
                .addExclude("/x", "/y");
        Assertions.assertEquals(Arrays.asList("/a", "/b"), filter.includeList);
        Assertions.assertEquals(Arrays.asList("/x", "/y"), filter.excludeList);
    }

    /** 默认 error 策略遇到异常应该再包一层 SaTokenException 抛出 */
    @Test
    public void defaultErrorStrategy_rethrow() {
        SaTokenPathFilter filter = new SaTokenPathFilter();
        Assertions.assertThrows(SaTokenException.class, () -> filter.error.run(new RuntimeException("x")));
    }

    /** 默认 auth / beforeAuth 应该是空操作，调了也不该炸 */
    @Test
    public void defaultAuthHooks_shouldBeNoop() {
        SaTokenPathFilter filter = new SaTokenPathFilter();
        filter.beforeAuth.run(null);
        filter.auth.run(null);
    }

    /** include 命中且 auth 通过时应该继续 invoke */
    @Test
    public void intercept_authPass_shouldInvoke() {
        Invocation invocation = mock(Invocation.class);
        HttpServletRequest request = JbootTestHelper.mockRequest("/api/user");
        HttpServletResponse response = JbootTestHelper.mockResponse();
        SaTokenPathFilter filter = new SaTokenPathFilter().addInclude("/api/**");
        JbootTestHelper.withHeldController(request, response, () -> filter.intercept(invocation));
        verify(invocation).invoke();
    }

    /** auth 抛 BackResultException 时应该写回文案，不再 invoke */
    @Test
    public void intercept_backResult_shouldRenderAndStop() {
        Invocation invocation = mock(Invocation.class);
        HttpServletRequest request = JbootTestHelper.mockRequest("/api/user");
        HttpServletResponse response = JbootTestHelper.mockResponse();
        SaTokenPathFilter filter = new SaTokenPathFilter()
                .addInclude("/**")
                .setAuth(r -> {
                    throw new BackResultException("blocked");
                });
        JbootTestHelper.withHeldController(request, response, () -> {
            when(invocation.getController()).thenReturn(JbootControllerContext.get());
            filter.intercept(invocation);
            verify(JbootControllerContext.get()).renderText("blocked");
        });
        verify(invocation, never()).invoke();
    }

    /** StopMatchException 应该继续 invoke */
    @Test
    public void intercept_stopMatch_shouldInvoke() {
        Invocation invocation = mock(Invocation.class);
        HttpServletRequest request = JbootTestHelper.mockRequest("/api/user");
        HttpServletResponse response = JbootTestHelper.mockResponse();
        SaTokenPathFilter filter = new SaTokenPathFilter()
                .addInclude("/**")
                .setAuth(r -> {
                    throw new StopMatchException();
                });
        JbootTestHelper.withHeldController(request, response, () -> filter.intercept(invocation));
        verify(invocation).invoke();
    }
}

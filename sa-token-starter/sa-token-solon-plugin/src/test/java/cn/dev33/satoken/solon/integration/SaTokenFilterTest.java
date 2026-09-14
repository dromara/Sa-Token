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
package cn.dev33.satoken.solon.integration;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.solon.testsupport.SolonTestHelper;
import cn.dev33.satoken.solon.testsupport.TestSolonContext;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.noear.solon.core.handle.Handler;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link SaTokenFilter} 路由配置与 doFilter 异常分支测试
 */
@SaTokenTest
public class SaTokenFilterTest {

	/** 每个用例开始前挂上 Solon 策略 */
	@BeforeEach
	public void setUp() {
		SolonTestHelper.ensureSolonStrategy();
	}

	/** 链式配置 include/exclude 和钩子函数应该能正常读写 */
	@Test
	public void configure_includeExcludeAndHooks() {
		SaTokenFilter filter = new SaTokenFilter()
				.addInclude("/**")
				.setIncludeList(Arrays.asList("/api/**"))
				.addExclude("/favicon.ico")
				.setExcludeList(Arrays.asList("/health"))
				.setAuth(r -> {})
				.setBeforeAuth(r -> {})
				.setError(e -> "err:" + e.getMessage());

		Assertions.assertEquals("/api/**", filter.getIncludeList().get(0));
		Assertions.assertEquals("/health", filter.getExcludeList().get(0));
		Assertions.assertEquals("err:msg", filter.error.run(new SaTokenException("msg")));
	}

	/** 默认 error 策略遇到 SaTokenException 应该原样抛出，其它异常要再包一层 */
	@Test
	public void defaultErrorStrategy_rethrow() {
		SaTokenFilter filter = new SaTokenFilter();
		SaTokenException saEx = new SaTokenException("sa");
		Assertions.assertSame(saEx, Assertions.assertThrows(SaTokenException.class, () -> filter.error.run(saEx)));
		Assertions.assertThrows(SaTokenException.class, () -> filter.error.run(new RuntimeException("x")));
	}

	/** 路由命中且 auth 通过时，应该继续走 FilterChain */
	@Test
	public void doFilter_authPass_continueChain() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenFilter filter = new SaTokenFilter().addInclude("/**").setAuth(r -> {});
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api/user");
		SolonTestHelper.setMainHandler(ctx, emptyHandler());

		SolonTestHelper.withSaContext(ctx, () -> filter.doFilter(ctx, c -> chainCalled.set(true)));

		Assertions.assertTrue(chainCalled.get());
	}

	/** auth 里抛 StopMatchException 时应该吞掉异常并继续走链 */
	@Test
	public void doFilter_stopMatch_continueChain() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenFilter filter = new SaTokenFilter()
				.addInclude("/**")
				.setAuth(r -> {
					throw new StopMatchException();
				});
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api/user");
		SolonTestHelper.setMainHandler(ctx, emptyHandler());

		SolonTestHelper.withSaContext(ctx, () -> filter.doFilter(ctx, c -> chainCalled.set(true)));

		Assertions.assertTrue(chainCalled.get());
	}

	/** auth 里抛 BackResultException 时应该写回响应并中断链条 */
	@Test
	public void doFilter_backResult_writeResponse() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenFilter filter = new SaTokenFilter()
				.addInclude("/**")
				.setAuth(r -> {
					throw new BackResultException("blocked");
				});
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api/user");
		SolonTestHelper.setMainHandler(ctx, emptyHandler());

		SolonTestHelper.withSaContext(ctx, () ->
				SolonTestHelper.runMayNpeOnRender(() -> filter.doFilter(ctx, c -> chainCalled.set(true))));

		Assertions.assertFalse(chainCalled.get());
	}

	/** auth 里抛 SaTokenException 时应该走 error 策略写回响应 */
	@Test
	public void doFilter_authError_useErrorStrategy() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenFilter filter = new SaTokenFilter()
				.addInclude("/**")
				.setAuth(r -> {
					throw new SaTokenException("boom");
				})
				.setError(e -> "handled");
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api/user");
		SolonTestHelper.setMainHandler(ctx, emptyHandler());

		SolonTestHelper.withSaContext(ctx, () ->
				SolonTestHelper.runMayNpeOnRender(() -> filter.doFilter(ctx, c -> chainCalled.set(true))));

		Assertions.assertFalse(chainCalled.get());
	}

	/** 未命中 include 路由时应该跳过 auth 并继续走链 */
	@Test
	public void doFilter_pathNotIncluded_skipAuth() throws Throwable {
		AtomicBoolean authCalled = new AtomicBoolean(false);
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenFilter filter = new SaTokenFilter()
				.addInclude("/api/**")
				.setAuth(r -> authCalled.set(true));
		TestSolonContext ctx = SolonTestHelper.newGetContext("/public/info");
		SolonTestHelper.setMainHandler(ctx, emptyHandler());

		SolonTestHelper.withSaContext(ctx, () -> filter.doFilter(ctx, c -> chainCalled.set(true)));

		Assertions.assertFalse(authCalled.get());
		Assertions.assertTrue(chainCalled.get());
	}

	/** 命中 exclude 时也应该跳过 auth */
	@Test
	public void doFilter_pathExcluded_skipAuth() throws Throwable {
		AtomicBoolean authCalled = new AtomicBoolean(false);
		SaTokenFilter filter = new SaTokenFilter()
				.addInclude("/**")
				.addExclude("/health")
				.setAuth(r -> authCalled.set(true));
		TestSolonContext ctx = SolonTestHelper.newGetContext("/health");
		SolonTestHelper.setMainHandler(ctx, emptyHandler());

		SolonTestHelper.withSaContext(ctx, () -> filter.doFilter(ctx, c -> {}));

		Assertions.assertFalse(authCalled.get());
	}

	/** beforeAuth 为 null 时不应该 NPE，关掉注解鉴权后应该直接走 auth */
	@Test
	public void doFilter_beforeAuthNull_andAnnotationOff() throws Throwable {
		AtomicReference<Object> authArg = new AtomicReference<>();
		SaTokenFilter filter = new SaTokenFilter().addInclude("/**").setAuth(authArg::set);
		filter.beforeAuth = null;
		filter.isAnnotation = false;
		Handler handler = emptyHandler();
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api");
		SolonTestHelper.setMainHandler(ctx, handler);

		SolonTestHelper.withSaContext(ctx, () -> filter.doFilter(ctx, c -> {}));

		Assertions.assertSame(handler, authArg.get());
	}

	/** 不 setAuth 时应该走默认空认证函数 */
	@Test
	public void doFilter_defaultAuth_empty() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenFilter filter = new SaTokenFilter().addInclude("/**");
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api");
		SolonTestHelper.setMainHandler(ctx, emptyHandler());

		SolonTestHelper.withSaContext(ctx, () -> filter.doFilter(ctx, c -> chainCalled.set(true)));

		Assertions.assertTrue(chainCalled.get());
	}

	private static Handler emptyHandler() {
		return c -> {};
	}

}

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

/**
 * {@link SaTokenInterceptor} 路由配置与 doIntercept 异常分支测试
 */
@SaTokenTest
public class SaTokenInterceptorTest {

	/** 每个用例开始前挂上 Solon 策略 */
	@BeforeEach
	public void setUp() {
		SolonTestHelper.ensureSolonStrategy();
	}

	/** 链式配置 include/exclude 和钩子函数应该能正常读写 */
	@Test
	public void configure_includeExcludeAndHooks() {
		SaTokenInterceptor interceptor = new SaTokenInterceptor()
				.addInclude("/**")
				.setIncludeList(Arrays.asList("/api/**"))
				.addExclude("/favicon.ico")
				.setExcludeList(Arrays.asList("/health"))
				.setAuth(r -> {})
				.setBeforeAuth(r -> {})
				.setError(e -> "err:" + e.getMessage());

		Assertions.assertEquals("/api/**", interceptor.getIncludeList().get(0));
		Assertions.assertEquals("/health", interceptor.getExcludeList().get(0));
		Assertions.assertEquals("err:msg", interceptor.error.run(new SaTokenException("msg")));
	}

	/** 默认 error 策略遇到 SaTokenException 应该原样抛出，其它异常要再包一层 */
	@Test
	public void defaultErrorStrategy_rethrow() {
		SaTokenInterceptor interceptor = new SaTokenInterceptor();
		SaTokenException saEx = new SaTokenException("sa");
		Assertions.assertSame(saEx, Assertions.assertThrows(SaTokenException.class, () -> interceptor.error.run(saEx)));
		Assertions.assertThrows(SaTokenException.class, () -> interceptor.error.run(new RuntimeException("x")));
	}

	/** 路由命中且 auth 通过时，应该继续走拦截器链 */
	@Test
	public void doIntercept_authPass_continueChain() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenInterceptor interceptor = new SaTokenInterceptor().addInclude("/**").setAuth(r -> {});
		interceptor.isAnnotation = false;
		Handler handler = emptyHandler();
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api/user");

		SolonTestHelper.withSaContext(ctx, () ->
				interceptor.doIntercept(ctx, handler, (c, h) -> chainCalled.set(true)));

		Assertions.assertTrue(chainCalled.get());
	}

	/** auth 里抛 StopMatchException 时应该吞掉异常并继续走链 */
	@Test
	public void doIntercept_stopMatch_continueChain() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenInterceptor interceptor = new SaTokenInterceptor()
				.addInclude("/**")
				.setAuth(r -> {
					throw new StopMatchException();
				});
		interceptor.isAnnotation = false;
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api/user");

		SolonTestHelper.withSaContext(ctx, () ->
				interceptor.doIntercept(ctx, emptyHandler(), (c, h) -> chainCalled.set(true)));

		Assertions.assertTrue(chainCalled.get());
	}

	/** auth 里抛 BackResultException 时应该写回响应并中断链条 */
	@Test
	public void doIntercept_backResult_writeResponse() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenInterceptor interceptor = new SaTokenInterceptor()
				.addInclude("/**")
				.setAuth(r -> {
					throw new BackResultException("blocked");
				});
		interceptor.isAnnotation = false;
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api/user");

		SolonTestHelper.withSaContext(ctx, () ->
				SolonTestHelper.runMayNpeOnRender(() ->
						interceptor.doIntercept(ctx, emptyHandler(), (c, h) -> chainCalled.set(true))));

		Assertions.assertFalse(chainCalled.get());
	}

	/** auth 里抛 SaTokenException 时应该走 error 策略写回响应 */
	@Test
	public void doIntercept_authError_useErrorStrategy() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenInterceptor interceptor = new SaTokenInterceptor()
				.addInclude("/**")
				.setAuth(r -> {
					throw new SaTokenException("boom");
				})
				.setError(e -> "handled");
		interceptor.isAnnotation = false;
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api/user");

		SolonTestHelper.withSaContext(ctx, () ->
				SolonTestHelper.runMayNpeOnRender(() ->
						interceptor.doIntercept(ctx, emptyHandler(), (c, h) -> chainCalled.set(true))));

		Assertions.assertFalse(chainCalled.get());
	}

	/** 命中 exclude 时也应该跳过 auth */
	@Test
	public void doIntercept_pathExcluded_skipAuth() throws Throwable {
		AtomicBoolean authCalled = new AtomicBoolean(false);
		SaTokenInterceptor interceptor = new SaTokenInterceptor()
				.addInclude("/**")
				.addExclude("/health")
				.setAuth(r -> authCalled.set(true));
		interceptor.isAnnotation = false;
		TestSolonContext ctx = SolonTestHelper.newGetContext("/health");

		SolonTestHelper.withSaContext(ctx, () ->
				interceptor.doIntercept(ctx, emptyHandler(), (c, h) -> {}));

		Assertions.assertFalse(authCalled.get());
	}

	/** 未命中 include 路由时应该跳过 auth 并继续走链 */
	@Test
	public void doIntercept_pathNotIncluded_skipAuth() throws Throwable {
		AtomicBoolean authCalled = new AtomicBoolean(false);
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenInterceptor interceptor = new SaTokenInterceptor()
				.addInclude("/api/**")
				.setAuth(r -> authCalled.set(true));
		interceptor.isAnnotation = false;
		TestSolonContext ctx = SolonTestHelper.newGetContext("/public/info");

		SolonTestHelper.withSaContext(ctx, () ->
				interceptor.doIntercept(ctx, emptyHandler(), (c, h) -> chainCalled.set(true)));

		Assertions.assertFalse(authCalled.get());
		Assertions.assertTrue(chainCalled.get());
	}

	/** beforeAuth 为 null 时不应该 NPE */
	@Test
	public void doIntercept_beforeAuthNull() throws Throwable {
		SaTokenInterceptor interceptor = new SaTokenInterceptor().addInclude("/**").setAuth(r -> {});
		interceptor.beforeAuth = null;
		interceptor.isAnnotation = false;
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api");

		SolonTestHelper.withSaContext(ctx, () ->
				interceptor.doIntercept(ctx, emptyHandler(), (c, h) -> {}));
	}

	/** 不 setAuth 时应该走默认空认证函数 */
	@Test
	public void doIntercept_defaultAuth_empty() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaTokenInterceptor interceptor = new SaTokenInterceptor().addInclude("/**");
		interceptor.isAnnotation = false;
		TestSolonContext ctx = SolonTestHelper.newGetContext("/api");

		SolonTestHelper.withSaContext(ctx, () ->
				interceptor.doIntercept(ctx, emptyHandler(), (c, h) -> chainCalled.set(true)));

		Assertions.assertTrue(chainCalled.get());
	}

	private static Handler emptyHandler() {
		return c -> {};
	}

}

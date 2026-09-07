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
package cn.dev33.satoken.loveqq.boot.filter;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerRequest;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerResponse;
import cn.dev33.satoken.test.SaTokenTest;
import com.kfyty.loveqq.framework.web.core.filter.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link SaRequestFilter} 路由配置与 doFilter 异常分支测试
 */
@SaTokenTest
public class SaRequestFilterTest {

	/** 每个用例开始前挂上 LoveQQ 策略 */
	@BeforeEach
	public void setUp() {
		LoveqqTestHelper.ensureLoveqqStrategy();
	}

	/** 链式配置 include/exclude 和钩子函数应该能正常读写 */
	@Test
	public void configure_includeExcludeAndHooks() {
		SaRequestFilter filter = new SaRequestFilter()
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

	/** 默认 error 策略遇到异常应该再包一层 SaTokenException 抛出 */
	@Test
	public void defaultErrorStrategy_rethrow() {
		SaRequestFilter filter = new SaRequestFilter();
		Assertions.assertThrows(SaTokenException.class, () -> filter.error.run(new RuntimeException("x")));
	}

	/** 路由命中且 auth 通过时，应该继续往里走 */
	@Test
	public void doFilter_authPass_continue() {
		AtomicBoolean beforeCalled = new AtomicBoolean(false);
		SaRequestFilter filter = new SaRequestFilter()
				.addInclude("/**")
				.setBeforeAuth(r -> beforeCalled.set(true))
				.setAuth(r -> {});
		TestServerRequest request = LoveqqTestHelper.newGetRequest("/api/user");
		TestServerResponse response = LoveqqTestHelper.newResponse();

		Filter.Continue result = filter.doFilter(request, response);

		Assertions.assertTrue(result._continue_());
		Assertions.assertTrue(beforeCalled.get());
	}

	/** auth 里抛 StopMatchException 时应该吞掉异常并继续 */
	@Test
	public void doFilter_stopMatch_continue() {
		SaRequestFilter filter = new SaRequestFilter()
				.addInclude("/**")
				.setAuth(r -> {
					throw new StopMatchException();
				});

		Filter.Continue result = filter.doFilter(LoveqqTestHelper.newGetRequest("/api/user"), LoveqqTestHelper.newResponse());

		Assertions.assertTrue(result._continue_());
	}

	/** auth 里抛 BackResultException 时应该写回响应并中断 */
	@Test
	public void doFilter_backResult_writeResponse() {
		SaRequestFilter filter = new SaRequestFilter()
				.addInclude("/**")
				.setAuth(r -> {
					throw new BackResultException("blocked");
				});
		TestServerResponse response = LoveqqTestHelper.newResponse();

		Filter.Continue result = filter.doFilter(LoveqqTestHelper.newGetRequest("/api/user"), response);

		Assertions.assertFalse(result._continue_());
		Assertions.assertEquals("blocked", response.bodyText());
	}

	/** auth 里抛异常时应该走 error 策略写回响应 */
	@Test
	public void doFilter_authError_useErrorStrategy() {
		SaRequestFilter filter = new SaRequestFilter()
				.addInclude("/**")
				.setAuth(r -> {
					throw new SaTokenException("boom");
				})
				.setError(e -> "handled");
		TestServerResponse response = LoveqqTestHelper.newResponse();

		Filter.Continue result = filter.doFilter(LoveqqTestHelper.newGetRequest("/api/user"), response);

		Assertions.assertFalse(result._continue_());
		Assertions.assertEquals("handled", response.bodyText());
	}

	/** 未命中 include 路由时应该跳过 auth */
	@Test
	public void doFilter_pathNotIncluded_skipAuth() {
		AtomicBoolean authCalled = new AtomicBoolean(false);
		SaRequestFilter filter = new SaRequestFilter()
				.addInclude("/api/**")
				.setAuth(r -> authCalled.set(true));

		filter.doFilter(LoveqqTestHelper.newGetRequest("/public/info"), LoveqqTestHelper.newResponse());

		Assertions.assertFalse(authCalled.get());
	}

	/** 命中 exclude 时也应该跳过 auth，但 beforeAuth 还是会跑 */
	@Test
	public void doFilter_pathExcluded_skipAuth() {
		AtomicBoolean authCalled = new AtomicBoolean(false);
		AtomicBoolean beforeCalled = new AtomicBoolean(false);
		SaRequestFilter filter = new SaRequestFilter()
				.addInclude("/**")
				.addExclude("/health")
				.setBeforeAuth(r -> beforeCalled.set(true))
				.setAuth(r -> authCalled.set(true));

		filter.doFilter(LoveqqTestHelper.newGetRequest("/health"), LoveqqTestHelper.newResponse());

		Assertions.assertTrue(beforeCalled.get());
		Assertions.assertFalse(authCalled.get());
	}

	/** 不 setAuth 时应该走默认空认证函数 */
	@Test
	public void doFilter_defaultAuth_empty() {
		SaRequestFilter filter = new SaRequestFilter().addInclude("/**");
		Filter.Continue result = filter.doFilter(LoveqqTestHelper.newGetRequest("/api"), LoveqqTestHelper.newResponse());
		Assertions.assertTrue(result._continue_());
	}

}

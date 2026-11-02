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
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.solon.testsupport.SolonTestHelper;
import cn.dev33.satoken.solon.testsupport.TestSolonContext;
import cn.dev33.satoken.solon.util.SaTokenContextSolonUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link SaTokenCorsFilterForSolon} CORS 策略执行与异常分支测试
 */
@SaTokenTest
public class SaTokenCorsFilterForSolonTest {

	private SaCorsHandleFunction backupCorsHandle;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	public void setUp() {
		SolonTestHelper.ensureSolonStrategy();
		backupCorsHandle = SaStrategy.instance.corsHandle;
	}

	/** 每个用例结束后把测试现场清掉 */
	@AfterEach
	public void tearDown() {
		SaStrategy.instance.corsHandle = backupCorsHandle;
	}

	/** corsHandle 正常执行后应该继续走 FilterChain */
	@Test
	public void doFilter_corsPass_continueChain() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaStrategy.instance.corsHandle = (req, res, sto) -> {};
		TestSolonContext ctx = SolonTestHelper.newGetContext("/cors");
		SaTokenCorsFilterForSolon corsFilter = new SaTokenCorsFilterForSolon();

		SaTokenContextSolonUtil.setContext(ctx, () -> {
			try {
				corsFilter.doFilter(ctx, c -> chainCalled.set(true));
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});

		Assertions.assertTrue(chainCalled.get());
	}

	/** corsHandle 抛 StopMatchException 时应该吞掉并继续走链 */
	@Test
	public void doFilter_stopMatch_continueChain() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaStrategy.instance.corsHandle = (req, res, sto) -> {
			throw new StopMatchException();
		};
		TestSolonContext ctx = SolonTestHelper.newGetContext("/cors");
		SaTokenCorsFilterForSolon corsFilter = new SaTokenCorsFilterForSolon();

		SaTokenContextSolonUtil.setContext(ctx, () -> {
			try {
				corsFilter.doFilter(ctx, c -> chainCalled.set(true));
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		});

		Assertions.assertTrue(chainCalled.get());
	}

	/** corsHandle 抛 BackResultException 时应该写回响应并中断链条 */
	@Test
	public void doFilter_backResult_writeResponse() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaStrategy.instance.corsHandle = (req, res, sto) -> {
			throw new BackResultException("cors-block");
		};
		TestSolonContext ctx = SolonTestHelper.newGetContext("/cors");
		SaTokenCorsFilterForSolon corsFilter = new SaTokenCorsFilterForSolon();

		SaTokenContextSolonUtil.setContext(ctx, () ->
				SolonTestHelper.runMayNpeOnRender(() -> corsFilter.doFilter(ctx, c -> chainCalled.set(true))));

		Assertions.assertFalse(chainCalled.get());
	}

}

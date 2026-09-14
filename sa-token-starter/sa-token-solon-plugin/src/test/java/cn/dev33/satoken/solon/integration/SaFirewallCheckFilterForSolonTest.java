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
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.strategy.SaFirewallCheckFailHandleFunction;
import cn.dev33.satoken.fun.strategy.SaFirewallCheckFunction;
import cn.dev33.satoken.solon.testsupport.SolonTestHelper;
import cn.dev33.satoken.solon.testsupport.TestSolonContext;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link SaFirewallCheckFilterForSolon} 防火墙校验与异常分支测试
 */
@SaTokenTest
public class SaFirewallCheckFilterForSolonTest {

	private SaFirewallCheckFunction backupCheck;
	private SaFirewallCheckFailHandleFunction backupFailHandle;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	public void setUp() {
		SolonTestHelper.ensureSolonStrategy();
		backupCheck = SaFirewallStrategy.instance.check;
		backupFailHandle = SaFirewallStrategy.instance.checkFailHandle;
	}

	/** 每个用例结束后把测试现场清掉 */
	@AfterEach
	public void tearDown() {
		SaFirewallStrategy.instance.check = backupCheck;
		SaFirewallStrategy.instance.checkFailHandle = backupFailHandle;
	}

	/** 防火墙校验通过时应该继续走 FilterChain */
	@Test
	public void doFilter_checkPass_continueChain() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaFirewallStrategy.instance.check = (req, res, extArg) -> {};
		SaFirewallCheckFilterForSolon filter = new SaFirewallCheckFilterForSolon();

		filter.doFilter(SolonTestHelper.newGetContext("/safe"), c -> chainCalled.set(true));

		Assertions.assertTrue(chainCalled.get());
	}

	/** check 抛 StopMatchException 时应该吞掉并继续走链 */
	@Test
	public void doFilter_stopMatch_continueChain() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaFirewallStrategy.instance.check = (req, res, extArg) -> {
			throw new StopMatchException();
		};
		SaFirewallCheckFilterForSolon filter = new SaFirewallCheckFilterForSolon();

		filter.doFilter(SolonTestHelper.newGetContext("/safe"), c -> chainCalled.set(true));

		Assertions.assertTrue(chainCalled.get());
	}

	/** check 抛 BackResultException 时应该写回响应并中断链条 */
	@Test
	public void doFilter_backResult_writeResponse() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaFirewallStrategy.instance.check = (req, res, extArg) -> {
			throw new BackResultException("fw-block");
		};
		SaFirewallCheckFilterForSolon filter = new SaFirewallCheckFilterForSolon();
		TestSolonContext ctx = SolonTestHelper.newGetContext("/safe");

		SolonTestHelper.runMayNpeOnRender(() -> filter.doFilter(ctx, c -> chainCalled.set(true)));

		Assertions.assertFalse(chainCalled.get());
	}

	/** FirewallCheckException 且没有自定义 failHandle 时，应该直接把异常信息写回响应 */
	@Test
	public void doFilter_firewallCheckDefault_writeMessage() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		SaFirewallStrategy.instance.checkFailHandle = null;
		SaFirewallStrategy.instance.check = (req, res, extArg) -> {
			throw new FirewallCheckException("bad path");
		};
		SaFirewallCheckFilterForSolon filter = new SaFirewallCheckFilterForSolon();
		TestSolonContext ctx = SolonTestHelper.newGetContext("/bad");

		SolonTestHelper.runMayNpeOnRender(() -> filter.doFilter(ctx, c -> chainCalled.set(true)));

		Assertions.assertFalse(chainCalled.get());
	}

	/** FirewallCheckException 且配置了 failHandle 时，应该走自定义处理逻辑 */
	@Test
	public void doFilter_firewallCheckCustom_useFailHandle() throws Throwable {
		AtomicBoolean chainCalled = new AtomicBoolean(false);
		AtomicReference<String> message = new AtomicReference<>();
		SaFirewallStrategy.instance.check = (req, res, extArg) -> {
			throw new FirewallCheckException("bad path");
		};
		SaFirewallStrategy.instance.checkFailHandle = (e, req, res, extArg) -> message.set("handled:" + e.getMessage());
		SaFirewallCheckFilterForSolon filter = new SaFirewallCheckFilterForSolon();

		filter.doFilter(SolonTestHelper.newGetContext("/bad"), c -> chainCalled.set(true));

		Assertions.assertEquals("handled:bad path", message.get());
		Assertions.assertFalse(chainCalled.get());
	}

}

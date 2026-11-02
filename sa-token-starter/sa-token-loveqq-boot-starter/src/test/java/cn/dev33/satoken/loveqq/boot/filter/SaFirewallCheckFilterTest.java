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
import cn.dev33.satoken.exception.FirewallCheckException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.strategy.SaFirewallCheckFailHandleFunction;
import cn.dev33.satoken.fun.strategy.SaFirewallCheckFunction;
import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerResponse;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import com.kfyty.loveqq.framework.web.core.filter.Filter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link SaFirewallCheckFilter} 防火墙校验与异常分支测试
 */
@SaTokenTest
public class SaFirewallCheckFilterTest {

	private SaFirewallCheckFunction backupCheck;
	private SaFirewallCheckFailHandleFunction backupFailHandle;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	public void setUp() {
		LoveqqTestHelper.ensureLoveqqStrategy();
		backupCheck = SaFirewallStrategy.instance.check;
		backupFailHandle = SaFirewallStrategy.instance.checkFailHandle;
	}

	/** 每个用例结束后把测试现场清掉 */
	@AfterEach
	public void tearDown() {
		SaFirewallStrategy.instance.check = backupCheck;
		SaFirewallStrategy.instance.checkFailHandle = backupFailHandle;
	}

	/** 防火墙校验通过时应该继续往里走 */
	@Test
	public void doFilter_checkPass_continue() {
		SaFirewallStrategy.instance.check = (req, res, extArg) -> {};
		SaFirewallCheckFilter filter = new SaFirewallCheckFilter();

		Filter.Continue result = filter.doFilter(LoveqqTestHelper.newGetRequest("/safe"), LoveqqTestHelper.newResponse());

		Assertions.assertTrue(result._continue_());
	}

	/** check 抛 StopMatchException 时应该吞掉并继续 */
	@Test
	public void doFilter_stopMatch_continue() {
		SaFirewallStrategy.instance.check = (req, res, extArg) -> {
			throw new StopMatchException();
		};
		SaFirewallCheckFilter filter = new SaFirewallCheckFilter();

		Filter.Continue result = filter.doFilter(LoveqqTestHelper.newGetRequest("/safe"), LoveqqTestHelper.newResponse());

		Assertions.assertTrue(result._continue_());
	}

	/** check 抛 BackResultException 时应该写回响应并中断 */
	@Test
	public void doFilter_backResult_writeResponse() {
		SaFirewallStrategy.instance.check = (req, res, extArg) -> {
			throw new BackResultException("fw-block");
		};
		TestServerResponse response = LoveqqTestHelper.newResponse();
		SaFirewallCheckFilter filter = new SaFirewallCheckFilter();

		Filter.Continue result = filter.doFilter(LoveqqTestHelper.newGetRequest("/safe"), response);

		Assertions.assertFalse(result._continue_());
		Assertions.assertEquals("fw-block", response.bodyText());
	}

	/** FirewallCheckException 且没有自定义 failHandle 时，应该直接把异常信息写回响应 */
	@Test
	public void doFilter_firewallCheckDefault_writeMessage() {
		SaFirewallStrategy.instance.checkFailHandle = null;
		SaFirewallStrategy.instance.check = (req, res, extArg) -> {
			throw new FirewallCheckException("bad path");
		};
		TestServerResponse response = LoveqqTestHelper.newResponse();
		SaFirewallCheckFilter filter = new SaFirewallCheckFilter();

		Filter.Continue result = filter.doFilter(LoveqqTestHelper.newGetRequest("/bad"), response);

		Assertions.assertFalse(result._continue_());
		Assertions.assertEquals("bad path", response.bodyText());
	}

	/** FirewallCheckException 且配置了 failHandle 时，应该走自定义处理逻辑 */
	@Test
	public void doFilter_firewallCheckCustom_useFailHandle() {
		AtomicReference<String> message = new AtomicReference<String>();
		SaFirewallStrategy.instance.check = (req, res, extArg) -> {
			throw new FirewallCheckException("bad path");
		};
		SaFirewallStrategy.instance.checkFailHandle = (e, req, res, extArg) -> message.set("handled:" + e.getMessage());
		SaFirewallCheckFilter filter = new SaFirewallCheckFilter();

		Filter.Continue result = filter.doFilter(LoveqqTestHelper.newGetRequest("/bad"), LoveqqTestHelper.newResponse());

		Assertions.assertEquals("handled:bad path", message.get());
		Assertions.assertFalse(result._continue_());
	}

}

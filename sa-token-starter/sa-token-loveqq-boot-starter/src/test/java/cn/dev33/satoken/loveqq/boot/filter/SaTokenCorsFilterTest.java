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
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.loveqq.boot.testsupport.LoveqqTestHelper;
import cn.dev33.satoken.loveqq.boot.testsupport.TestServerResponse;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import com.kfyty.loveqq.framework.web.core.filter.Filter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaTokenCorsFilter} CORS 策略执行与异常分支测试
 */
@SaTokenTest
public class SaTokenCorsFilterTest {

	private SaCorsHandleFunction backupCorsHandle;

	/** 每个用例开始前准备测试现场 */
	@BeforeEach
	public void setUp() {
		LoveqqTestHelper.ensureLoveqqStrategy();
		backupCorsHandle = SaStrategy.instance.corsHandle;
	}

	/** 每个用例结束后把测试现场清掉 */
	@AfterEach
	public void tearDown() {
		SaStrategy.instance.corsHandle = backupCorsHandle;
	}

	/** corsHandle 正常执行后应该继续往里走 */
	@Test
	public void doFilter_corsPass_continue() {
		SaStrategy.instance.corsHandle = (req, res, sto) -> {};
		SaTokenCorsFilter corsFilter = new SaTokenCorsFilter();

		Filter.Continue result = corsFilter.doFilter(LoveqqTestHelper.newGetRequest("/cors"), LoveqqTestHelper.newResponse());

		Assertions.assertTrue(result._continue_());
	}

	/** corsHandle 抛 StopMatchException 时应该吞掉并继续 */
	@Test
	public void doFilter_stopMatch_continue() {
		SaStrategy.instance.corsHandle = (req, res, sto) -> {
			throw new StopMatchException();
		};
		SaTokenCorsFilter corsFilter = new SaTokenCorsFilter();

		Filter.Continue result = corsFilter.doFilter(LoveqqTestHelper.newGetRequest("/cors"), LoveqqTestHelper.newResponse());

		Assertions.assertTrue(result._continue_());
	}

	/** corsHandle 抛 BackResultException 时应该写回响应并中断 */
	@Test
	public void doFilter_backResult_writeResponse() {
		SaStrategy.instance.corsHandle = (req, res, sto) -> {
			throw new BackResultException("cors-block");
		};
		TestServerResponse response = LoveqqTestHelper.newResponse();
		SaTokenCorsFilter corsFilter = new SaTokenCorsFilter();

		Filter.Continue result = corsFilter.doFilter(LoveqqTestHelper.newGetRequest("/cors"), response);

		Assertions.assertFalse(result._continue_());
		Assertions.assertEquals("cors-block", response.bodyText());
	}

}

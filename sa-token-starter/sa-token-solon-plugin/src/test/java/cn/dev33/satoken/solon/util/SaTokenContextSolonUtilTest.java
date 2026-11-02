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
package cn.dev33.satoken.solon.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.solon.testsupport.SolonTestHelper;
import cn.dev33.satoken.solon.testsupport.TestSolonContext;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link SaTokenContextSolonUtil} 上下文读写测试
 */
@SaTokenTest
public class SaTokenContextSolonUtilTest {

	/** 每个用例开始前挂上 Solon 策略 */
	@BeforeEach
	public void setUp() {
		SolonTestHelper.ensureSolonStrategy();
	}

	/** 无回调的 setContext 应该写入上下文，手动清理后失效 */
	@Test
	public void setContext_withoutCallback_manualClear() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/ctx");
		SaTokenContextSolonUtil.setContext(ctx);
		try {
			Assertions.assertNotNull(SaTokenContextSolonUtil.getModelBox());
			Assertions.assertSame(ctx, SaTokenContextSolonUtil.getContext());
			Assertions.assertEquals("/ctx", SaHolder.getRequest().getRequestPath());
		} finally {
			SaTokenContextSolonUtil.clearContext();
		}
		Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
	}

	/** Runnable 版 setContext 应该在执行后自动清理上下文 */
	@Test
	public void setContext_withRunnable_autoClear() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/ctx");
		SaTokenContextSolonUtil.setContext(ctx, () ->
				Assertions.assertEquals("/ctx", SaHolder.getRequest().getRequestPath()));
		Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
	}

	/** Runnable 版抛异常时也应该清理上下文 */
	@Test
	public void setContext_withRunnable_clearOnException() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/ctx");
		Assertions.assertThrows(RuntimeException.class, () ->
				SaTokenContextSolonUtil.setContext(ctx, () -> {
					throw new RuntimeException("boom");
				}));
		Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
	}

	/** 泛型版 setContext 应该返回函数结果并在结束后清理上下文 */
	@Test
	public void setContext_withGenericFunction_returnValue() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/ctx");
		String value = SaTokenContextSolonUtil.setContext(ctx, () -> "ok");
		Assertions.assertEquals("ok", value);
		Assertions.assertThrows(Exception.class, () -> SaHolder.getRequest().getRequestPath());
	}

	/** 应测尽测：测试 SaTokenContextSolonUtil 无参构造 */
	@Test
	public void constructor_shouldCreateInstance() {
		Assertions.assertNotNull(new SaTokenContextSolonUtil());
	}

}

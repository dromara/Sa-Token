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
package cn.dev33.satoken.solon.model;

import cn.dev33.satoken.solon.testsupport.SolonTestHelper;
import cn.dev33.satoken.solon.testsupport.TestSolonContext;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaResponseForSolon} 响应包装测试
 */
@SaTokenTest
public class SaResponseForSolonTest {

	/** 无参构造应该读 Context.current() */
	@Test
	public void noArgConstructor_useCurrentContext() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/");
		SolonTestHelper.withCurrent(ctx, () -> Assertions.assertSame(ctx, new SaResponseForSolon().getSource()));
	}

	/** 状态码、响应头与 addHeader 应该写入底层 Context */
	@Test
	public void setStatusAndHeaders() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/");
		SaResponseForSolon saResponse = new SaResponseForSolon(ctx);

		saResponse.setStatus(201).setHeader("A", "1").addHeader("B", "2");

		Assertions.assertSame(ctx, saResponse.getSource());
		Assertions.assertEquals(201, ctx.status());
		Assertions.assertEquals("1", ctx.headerOfResponse("A"));
		Assertions.assertEquals("2", ctx.headerOfResponse("B"));
	}

	/** redirect 应该交给底层 Context 并返回 null */
	@Test
	public void redirect_ok_returnNull() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/");
		SaResponseForSolon saResponse = new SaResponseForSolon(ctx);

		Assertions.assertNull(saResponse.redirect("/login"));
		Assertions.assertEquals(302, ctx.status());
		Assertions.assertEquals("/login", ctx.headerOfResponse("Location"));
	}

}

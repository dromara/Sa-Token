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
 * {@link SaContextForSolon} 旧版只读上下文测试
 */
@SaTokenTest
public class SaContextForSolonTest {

	/** 有当前 Context 时 isValid 应该是 true，getRequest/Response/Storage 都能拿到包装对象 */
	@Test
	public void isValid_whenCurrentContext_true() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/old");
		SolonTestHelper.withCurrent(ctx, () -> {
			SaContextForSolon context = new SaContextForSolon();
			Assertions.assertTrue(context.isValid());
			Assertions.assertSame(ctx, context.getRequest().getSource());
			Assertions.assertSame(ctx, context.getResponse().getSource());
			Assertions.assertSame(ctx, context.getStorage().getSource());
		});
	}

}

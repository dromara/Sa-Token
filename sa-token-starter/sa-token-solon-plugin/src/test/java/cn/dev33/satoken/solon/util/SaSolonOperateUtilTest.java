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

import cn.dev33.satoken.solon.testsupport.SolonTestHelper;
import cn.dev33.satoken.solon.testsupport.TestSolonContext;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaSolonOperateUtil} 写回结果测试
 */
@SaTokenTest
public class SaSolonOperateUtilTest {

	/** result 为 null 时不应该 render，但仍要标记已处理 */
	@Test
	public void writeResult_null_onlyMarkHandled() throws Throwable {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/");
		SaSolonOperateUtil.writeResult(ctx, null);
		Assertions.assertTrue(ctx.getHandled());
	}

	/** result 非空时会走 render；Solon 未启动时底层会 NPE，真渲染由 HTTP 集成测覆盖 */
	@Test
	public void writeResult_notNull_goRenderPath() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/");
		SolonTestHelper.runMayNpeOnRender(() -> SaSolonOperateUtil.writeResult(ctx, "blocked"));
	}

	/** 应测尽测：测试 SaSolonOperateUtil 无参构造 */
	@Test
	public void constructor_shouldCreateInstance() {
		Assertions.assertNotNull(new SaSolonOperateUtil());
	}

}

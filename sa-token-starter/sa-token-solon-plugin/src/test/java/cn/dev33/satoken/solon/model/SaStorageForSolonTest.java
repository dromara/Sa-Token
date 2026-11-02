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
 * {@link SaStorageForSolon} 请求作用域存储测试
 */
@SaTokenTest
public class SaStorageForSolonTest {

	/** 无参构造应该读 Context.current() */
	@Test
	public void noArgConstructor_useCurrentContext() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/");
		SolonTestHelper.withCurrent(ctx, () -> Assertions.assertSame(ctx, new SaStorageForSolon().getSource()));
	}

	/** set/get/delete 应该读写 Context attr */
	@Test
	public void setGetDelete_attributeOnContext() {
		TestSolonContext ctx = SolonTestHelper.newGetContext("/");
		SaStorageForSolon storage = new SaStorageForSolon(ctx);

		Assertions.assertSame(ctx, storage.getSource());
		storage.set("k", "v");
		Assertions.assertEquals("v", storage.get("k"));
		storage.delete("k");
		Assertions.assertNull(storage.get("k"));
	}

}

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
package cn.dev33.satoken.core.context;

import cn.dev33.satoken.context.SaTokenContextForReadOnly;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaResponseForMock;
import cn.dev33.satoken.context.mock.SaStorageForMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaTokenContextForReadOnly 上下文处理器测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenContextForReadOnlyTest {

	/** 只读上下文的写操作应无副作用，且不提供 ModelBox */
	@Test
	void readOnlyOperationsAreNoOps() {
		SaTokenContextForReadOnly context = new SaTokenContextForReadOnly() {
			@Override
			public boolean isValid() {
				return true;
			}
		};

		Assertions.assertDoesNotThrow(() -> context.setContext(
				new SaRequestForMock(), new SaResponseForMock(), new SaStorageForMock()));
		Assertions.assertDoesNotThrow(context::clearContext);
		Assertions.assertNull(context.getModelBox());
	}

}

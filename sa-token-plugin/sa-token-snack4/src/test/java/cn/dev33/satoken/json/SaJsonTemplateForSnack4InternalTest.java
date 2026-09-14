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
package cn.dev33.satoken.json;

import cn.dev33.satoken.exception.SaJsonConvertException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaJsonTemplateForSnack4} 包级静态方法内部测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaJsonTemplateForSnack4InternalTest {

	/** 异常链上存在无 message 的异常时应该跳过它并走通用包装 */
	@Test
	void wrapsExceptionWhenChainHasNullMessage() {
		RuntimeException source = new RuntimeException(new RuntimeException());
		SaJsonConvertException ex = SaJsonTemplateForSnack4.toSaJsonConvertException(source);
		Assertions.assertSame(source, ex.getCause());
	}

}

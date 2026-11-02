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
package cn.dev33.satoken.core.router;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.router.SaHttpMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaHttpMethod 转换
 *
 * @author click33
 * @since 1.46.0
 */
public class SaHttpMethodTest {

	/** toEnum 应忽略大小写并拒绝 null 与未知请求方式 */
	@Test
	void toEnum_convertsAndValidatesMethods() {
		Assertions.assertEquals(SaHttpMethod.GET, SaHttpMethod.toEnum("get"));
		Assertions.assertEquals(SaHttpMethod.ALL, SaHttpMethod.toEnum("ALL"));

		SaTokenException nullException = Assertions.assertThrows(SaTokenException.class,
				() -> SaHttpMethod.toEnum(null));
		Assertions.assertEquals(10321, nullException.getCode());
		SaTokenException invalidException = Assertions.assertThrows(SaTokenException.class,
				() -> SaHttpMethod.toEnum("INVALID"));
		Assertions.assertEquals(10321, invalidException.getCode());
	}

	/** toEnumArray 应按原始顺序转换每个请求方式 */
	@Test
	void toEnumArray_convertsMethods() {
		Assertions.assertArrayEquals(new SaHttpMethod[] {SaHttpMethod.POST, SaHttpMethod.DELETE},
				SaHttpMethod.toEnumArray("POST", "delete"));
	}

}

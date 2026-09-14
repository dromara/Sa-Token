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
package cn.dev33.satoken.core.http;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.http.SaHttpTemplateDefaultImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * SaHttpTemplateDefaultImpl 默认未实现行为测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaHttpTemplateDefaultImplTest {

	private final SaHttpTemplateDefaultImpl template = new SaHttpTemplateDefaultImpl();

	/** get 应抛出 NotImplException */
	@Test
	void get_throwsNotImpl() {
		NotImplException ex = Assertions.assertThrows(NotImplException.class,
				() -> template.get("http://example.com"));
		Assertions.assertEquals(SaErrorCode.CODE_10004, ex.getCode());
		Assertions.assertEquals(SaHttpTemplateDefaultImpl.ERROR_MESSAGE, ex.getMessage());
	}

	/** postByFormData 应抛出 NotImplException */
	@Test
	void postByFormData_throwsNotImpl() {
		NotImplException ex = Assertions.assertThrows(NotImplException.class,
				() -> template.postByFormData("http://example.com", Collections.emptyMap()));
		Assertions.assertEquals(SaErrorCode.CODE_10004, ex.getCode());
	}

}

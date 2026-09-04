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
package cn.dev33.satoken.core.json;

import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;

/**
 * json默认实现类测试 
 * 
 * @author click33
 * @since 2022-9-1
 */
public class SaJsonTemplateDefaultImplTest {

    /** 默认 JSON 实现未引入第三方库时应抛出 NotImplException */
    @Test
    public void testSaJsonTemplateDefaultImpl() {
		SaJsonTemplateDefaultImpl saJsonTemplate = new SaJsonTemplateDefaultImpl();
		assertNotImplException(() -> saJsonTemplate.jsonToMap("{}"));
		assertNotImplException(() -> saJsonTemplate.objectToJson(Collections.singletonMap("name", "zhangsan")));
		assertNotImplException(() -> saJsonTemplate.jsonToObject("{}"));
		assertNotImplException(() -> saJsonTemplate.jsonToObject("{}", Object.class));
    }

	/** 默认 JSON 实现的所有转换入口均应提示未注入具体实现 */
	private void assertNotImplException(Executable executable) {
		NotImplException ex = Assertions.assertThrows(NotImplException.class, executable);
		Assertions.assertEquals(SaJsonTemplateDefaultImpl.ERROR_MESSAGE, ex.getMessage());
		Assertions.assertEquals(SaErrorCode.CODE_10003, ex.getCode());
	}

}

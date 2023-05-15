/*
 * Copyright 2020-2099 sa-token.cc
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.util.SoMap;

/**
 * json默认实现类测试 
 * 
 * @author click33
 * @since 2022-9-1
 */
public class SaJsonTemplateDefaultImplTest {

    @Test
    public void testSaJsonTemplateDefaultImpl() {
    	SaJsonTemplateDefaultImpl saJsonTemplate = new SaJsonTemplateDefaultImpl();
    	// 组件未实现
    	Assertions.assertThrows(NotImplException.class, () -> {
    		saJsonTemplate.parseJsonToMap("{}");
    	});
    	// 组件未实现
    	Assertions.assertThrows(NotImplException.class, () -> {
    		saJsonTemplate.toJsonString(SoMap.getSoMap("name", "zhangsan"));
    	});
    }

}

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
package cn.dev33.satoken.core.config;

import cn.dev33.satoken.config.SaCookieConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SaCookieConfig getter/setter 与扩展属性测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaCookieConfigFullTest {

	/** getter/setter 与 extraAttrs 操作应正常工作 */
	@Test
	void gettersSettersAndExtraAttrs() {
		SaCookieConfig config = new SaCookieConfig();
		config.setDomain("example.com")
				.setPath("/app")
				.setSecure(true)
				.setHttpOnly(true)
				.setSameSite("Strict");

		Assertions.assertEquals("example.com", config.getDomain());
		Assertions.assertEquals("/app", config.getPath());
		Assertions.assertEquals(true, config.getSecure());
		Assertions.assertEquals(true, config.getHttpOnly());
		Assertions.assertEquals("Strict", config.getSameSite());

		Map<String, String> attrs = new LinkedHashMap<>();
		attrs.put("Partitioned", null);
		config.setExtraAttrs(attrs);
		Assertions.assertSame(attrs, config.getExtraAttrs());

		config.addExtraAttr("SameSite", "None");
		config.addExtraAttr("FlagOnly");
		Assertions.assertEquals("None", config.getExtraAttrs().get("SameSite"));
		Assertions.assertNull(config.getExtraAttrs().get("FlagOnly"));

		config.removeExtraAttr("FlagOnly");
		Assertions.assertFalse(config.getExtraAttrs().containsKey("FlagOnly"));

		Assertions.assertTrue(config.toString().contains("example.com"));
	}

	/** extraAttrs 为 null 时 addExtraAttr 应自动创建 Map */
	@Test
	void addExtraAttr_whenMapNull_createsMap() {
		SaCookieConfig config = new SaCookieConfig();
		config.setExtraAttrs(null);
		config.addExtraAttr("k", "v");
		Assertions.assertEquals("v", config.getExtraAttrs().get("k"));
	}

}

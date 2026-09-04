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

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.http.SaHttpTemplate;
import cn.dev33.satoken.http.SaHttpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

/**
 * SaHttpUtil HTTP 门面测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaHttpUtilTest {

	/** get / postByFormData 应委托给 SaManager 中的 SaHttpTemplate */
	@Test
	void delegatesToSaHttpTemplate() {
		SaManager.setSaHttpTemplate(new SaHttpTemplate() {
			@Override
			public String get(String url) {
				return "get:" + url;
			}

			@Override
			public String postByFormData(String url, Map<String, Object> params) {
				return "post:" + url + ":" + params.size();
			}
		});

		Assertions.assertEquals("get:http://test", SaHttpUtil.get("http://test"));
		Assertions.assertEquals("post:http://test:0",
				SaHttpUtil.postByFormData("http://test", Collections.emptyMap()));
	}

	/** 默认构造函数应可正常创建实例 */
	@Test
	void defaultConstructor() {
		Assertions.assertDoesNotThrow(SaHttpUtil::new);
	}

}

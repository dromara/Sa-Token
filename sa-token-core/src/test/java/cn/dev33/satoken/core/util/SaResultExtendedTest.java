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
package cn.dev33.satoken.core.util;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SaResult 剩余方法测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaResultExtendedTest {

	@BeforeEach
	void setUpJsonTemplate() {
		SaManager.setSaJsonTemplate(new SimpleSaJsonTemplate());
	}

	/** 链式 setter 应返回自身并正确设置各字段 */
	@Test
	void chainSetters() {
		SaResult result = new SaResult()
				.setCode(201)
				.setMsg("created")
				.setData("payload")
				.set("extra", 1);
		Assertions.assertSame(result, result.setCode(202));
		Assertions.assertEquals(202, result.getCode());
		Assertions.assertEquals("created", result.getMsg());
		Assertions.assertEquals("payload", result.getData());
		Assertions.assertEquals(1, result.get("extra"));
		Assertions.assertEquals(1, result.get("extra", Integer.class));
	}

	/** setMap 与 setJsonString 应正确填充字段 */
	@Test
	void setMapAndSetJsonString() {
		Map<String, Object> map = new HashMap<>();
		map.put("a", 1);
		map.put("b", "two");
		SaResult result = new SaResult().setMap(map);
		Assertions.assertEquals(1, result.get("a"));
		Assertions.assertEquals("two", result.get("b"));

		SaResult fromJson = new SaResult().setJsonString("{\"code\":200,\"msg\":\"ok\",\"data\":99}");
		Assertions.assertEquals(200, fromJson.getCode());
		Assertions.assertEquals("ok", fromJson.getMsg());
		Assertions.assertEquals(99, fromJson.getData());
	}

	/** removeDefaultFields/removeNonDefaultFields 应分别移除默认或非默认字段 */
	@Test
	void removeFields() {
		SaResult result = SaResult.get(200, "ok", "data").set("extra", "x");
		result.removeDefaultFields();
		Assertions.assertNull(result.getCode());
		Assertions.assertNull(result.getMsg());
		Assertions.assertNull(result.getData());
		Assertions.assertEquals("x", result.get("extra"));

		SaResult defaultsOnly = SaResult.get(200, "ok", "data");
		defaultsOnly.removeNonDefaultFields();
		Assertions.assertEquals(200, defaultsOnly.getCode());
		Assertions.assertEquals("ok", defaultsOnly.getMsg());
		Assertions.assertEquals("data", defaultsOnly.getData());
	}

	/** ok/data/notLogin/notPermission/empty 等静态工厂方法应返回预期状态码 */
	@Test
	void staticFactoryMethods() {
		Assertions.assertEquals("done", SaResult.ok("done").getMsg());
		Assertions.assertEquals("payload", SaResult.data("payload").getData());
		Assertions.assertEquals(SaResult.CODE_NOT_LOGIN, SaResult.notLogin().getCode());
		Assertions.assertEquals(SaResult.CODE_NOT_PERMISSION, SaResult.notPermission().getCode());
		Assertions.assertNull(SaResult.empty().getCode());
	}

	/** toString 在字段为 null 时应输出含 null 的 JSON 字符串 */
	@Test
	void toStringWithNullFields() {
		SaResult result = new SaResult().setCode(200);
		Assertions.assertEquals("{\"code\": 200, \"msg\": null, \"data\": null}", result.toString());
	}

	private static class SimpleSaJsonTemplate implements SaJsonTemplate {

		@Override
		public String objectToJson(Object obj) {
			return String.valueOf(obj);
		}

		@Override
		@SuppressWarnings("unchecked")
		public Object jsonToObject(String jsonStr) {
			Map<String, Object> map = new LinkedHashMap<>();
			String body = jsonStr.substring(1, jsonStr.length() - 1).trim();
			if (body.isEmpty()) {
				return map;
			}
			for (String part : body.split(",")) {
				String[] kv = part.split(":", 2);
				String key = kv[0].trim().replace("\"", "");
				String rawValue = kv[1].trim();
				if (rawValue.startsWith("\"")) {
					map.put(key, rawValue.substring(1, rawValue.length() - 1));
				} else {
					map.put(key, Integer.parseInt(rawValue));
				}
			}
			return map;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T jsonToObject(String jsonStr, Class<T> type) {
			return (T) jsonToObject(jsonStr);
		}

		@Override
		public Map<String, Object> jsonToMap(String jsonStr) {
			return (Map<String, Object>) jsonToObject(jsonStr);
		}

	}

}

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
package cn.dev33.satoken.core.serializer;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseBase64;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseISO_8859_1;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJson;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * serializer.impl 包剩余实现类测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaSerializerImplExtendedTest {

	@BeforeEach
	void setUpJsonTemplate() {
		SaManager.setSaJsonTemplate(new SimpleSaJsonTemplate());
	}

	/** SaSerializerTemplateForJdkUseBase64 应对 Map 完成 Base64 编码往返 */
	@Test
	void jdkUseBase64_roundTrip() {
		SaSerializerTemplateForJdkUseBase64 serializer = new SaSerializerTemplateForJdkUseBase64();
		Map<String, String> map = new HashMap<>();
		map.put("k", "v");
		String encoded = serializer.objectToString(map);
		@SuppressWarnings("unchecked")
		Map<String, String> restored = (Map<String, String>) serializer.stringToObject(encoded);
		Assertions.assertEquals("v", restored.get("k"));
	}

	/** SaSerializerTemplateForJdkUseISO_8859_1 应对对象完成字节往返序列化 */
	@Test
	void jdkUseIso8859_roundTrip() {
		SaSerializerTemplateForJdkUseISO_8859_1 serializer = new SaSerializerTemplateForJdkUseISO_8859_1();
		SimpleBean bean = new SimpleBean("iso", 1);
		byte[] bytes = serializer.objectToBytes(bean);
		SimpleBean restored = (SimpleBean) serializer.bytesToObject(bytes);
		Assertions.assertEquals("iso", restored.name);
	}

	/** SaSerializerTemplateForJson 应对 Map 完成 JSON 字符串往返序列化 */
	@Test
	void jsonSerializer_stringRoundTrip() {
		SaSerializerTemplateForJson serializer = new SaSerializerTemplateForJson();
		Map<String, Object> map = new HashMap<>();
		map.put("name", "json-user");
		map.put("age", 20);

		String json = serializer.objectToString(map);
		@SuppressWarnings("unchecked")
		Map<String, Object> restored = (Map<String, Object>) serializer.stringToObject(json);
		Assertions.assertEquals("json-user", restored.get("name"));

		Map<String, Object> typed = serializer.stringToObject(json, Map.class);
		Assertions.assertEquals("json-user", typed.get("name"));
	}

	/** SaSerializerTemplateForJson 的字节序列化方法应抛出 ApiDisabledException */
	@Test
	void jsonSerializer_bytesMethodsThrow() {
		SaSerializerTemplateForJson serializer = new SaSerializerTemplateForJson();
		Assertions.assertThrows(ApiDisabledException.class, () -> serializer.objectToBytes("x"));
		Assertions.assertThrows(ApiDisabledException.class, () -> serializer.bytesToObject(new byte[] {1}));
	}

	private static class SimpleBean implements Serializable {
		private static final long serialVersionUID = 1L;
		private final String name;
		private final int value;

		SimpleBean(String name, int value) {
			this.name = name;
			this.value = value;
		}
	}

	private static class SimpleSaJsonTemplate implements SaJsonTemplate {

		@Override
		public String objectToJson(Object obj) {
			if (obj instanceof Map) {
				Map<?, ?> map = (Map<?, ?>) obj;
				StringBuilder sb = new StringBuilder("{");
				boolean first = true;
				for (Map.Entry<?, ?> entry : map.entrySet()) {
					if (!first) {
						sb.append(',');
					}
					first = false;
					sb.append('"').append(entry.getKey()).append("\":");
					Object value = entry.getValue();
					if (value instanceof Number) {
						sb.append(value);
					} else {
						sb.append('"').append(value).append('"');
					}
				}
				sb.append('}');
				return sb.toString();
			}
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

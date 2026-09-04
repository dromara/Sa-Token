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
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJson;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SaSerializerTemplate 序列化测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaSerializerTemplateTest {

	@BeforeEach
	void setUpJsonTemplate() {
		SaManager.setSaJsonTemplate(new SimpleSaJsonTemplate());
	}

	/** SaSerializerTemplateForJson 应对 Map 完成字符串往返序列化 */
	@Test
	void saSerializerTemplateForJson_roundTrip() {
		SaSerializerTemplateForJson serializer = new SaSerializerTemplateForJson();
		Map<String, Object> source = new LinkedHashMap<>();
		source.put("name", "zhangsan");
		source.put("age", 18);

		String json = serializer.objectToString(source);
		@SuppressWarnings("unchecked")
		Map<String, Object> restored = (Map<String, Object>) serializer.stringToObject(json);

		Assertions.assertEquals("zhangsan", restored.get("name"));
		Assertions.assertEquals(18, restored.get("age"));

		Map<String, Object> typed = serializer.stringToObject(json, Map.class);
		Assertions.assertEquals("zhangsan", typed.get("name"));
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
					appendValue(sb, entry.getValue());
				}
				sb.append('}');
				return sb.toString();
			}
			return String.valueOf(obj);
		}

		private void appendValue(StringBuilder sb, Object value) {
			if (value instanceof Number) {
				sb.append(value);
			} else {
				sb.append('"').append(value).append('"');
			}
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

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
 * See the License for the specific language governing permissions.
 * limitations under the License.
 */
package cn.dev33.satoken.sso.support;

import cn.dev33.satoken.json.SaJsonTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 单测用的扁平 JSON 转换器：只处理一层对象，避免拉 Jackson。
 */
public class StubSaJsonTemplate implements SaJsonTemplate {

	/** 把扁平 Map / SaResult 拼成 JSON */
	@Override
	public String objectToJson(Object obj) {
		if (obj instanceof Map) {
			return mapToJson((Map<?, ?>) obj);
		}
		return String.valueOf(obj);
	}

	/** 不走类型反序列化，单测用不到 */
	@Override
	public Object jsonToObject(String jsonStr) {
		return jsonToMap(jsonStr);
	}

	/** 不走类型反序列化，单测用不到 */
	@Override
	public <T> T jsonToObject(String jsonStr, Class<T> type) {
		return null;
	}

	/** 解析扁平 {"k":v}，数字尽量收成 Integer/Long */
	@Override
	public Map<String, Object> jsonToMap(String jsonStr) {
		Map<String, Object> map = new LinkedHashMap<>();
		if (jsonStr == null) {
			return map;
		}
		String body = jsonStr.trim();
		if (body.length() >= 2 && body.startsWith("{") && body.endsWith("}")) {
			body = body.substring(1, body.length() - 1).trim();
		}
		if (body.isEmpty()) {
			return map;
		}
		int i = 0;
		while (i < body.length()) {
			int colon = body.indexOf(':', i);
			if (colon < 0) {
				break;
			}
			String key = unquote(body.substring(i, colon).trim());
			int valueStart = colon + 1;
			int comma = indexOfTopComma(body, valueStart);
			String raw = (comma < 0 ? body.substring(valueStart) : body.substring(valueStart, comma)).trim();
			map.put(key, parseValue(raw));
			if (comma < 0) {
				break;
			}
			i = comma + 1;
		}
		return map;
	}

	/** 职责：把扁平 map 写成 JSON 文本 */
	private static String mapToJson(Map<?, ?> map) {
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (Map.Entry<?, ?> e : map.entrySet()) {
			if (!first) {
				sb.append(',');
			}
			first = false;
			sb.append('"').append(e.getKey()).append('"').append(':');
			Object v = e.getValue();
			if (v == null) {
				sb.append("null");
			} else if (v instanceof Number || v instanceof Boolean) {
				sb.append(v);
			} else {
				sb.append('"').append(String.valueOf(v).replace("\"", "\\\"")).append('"');
			}
		}
		return sb.append('}').toString();
	}

	/** 职责：找当前层逗号，避开字符串里的逗号 */
	private static int indexOfTopComma(String body, int from) {
		boolean inStr = false;
		for (int i = from; i < body.length(); i++) {
			char c = body.charAt(i);
			if (c == '"' && (i == 0 || body.charAt(i - 1) != '\\')) {
				inStr = !inStr;
			} else if (c == ',' && !inStr) {
				return i;
			}
		}
		return -1;
	}

	/** 职责：去掉首尾引号 */
	private static String unquote(String s) {
		if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
			return s.substring(1, s.length() - 1);
		}
		return s;
	}

	/** 职责：把 JSON 字面量收成 Java 值 */
	private static Object parseValue(String raw) {
		if ("null".equals(raw)) {
			return null;
		}
		if ("true".equals(raw) || "false".equals(raw)) {
			return Boolean.valueOf(raw);
		}
		if (raw.startsWith("\"")) {
			return unquote(raw);
		}
		if (raw.contains(".")) {
			return Double.valueOf(raw);
		}
		try {
			long n = Long.parseLong(raw);
			if (n >= Integer.MIN_VALUE && n <= Integer.MAX_VALUE) {
				return (int) n;
			}
			return n;
		} catch (NumberFormatException e) {
			return raw;
		}
	}

}

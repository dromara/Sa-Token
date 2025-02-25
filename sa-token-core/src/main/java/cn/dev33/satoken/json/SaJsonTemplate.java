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
package cn.dev33.satoken.json;

import java.util.Map;

/**
 * JSON 转换器 
 * 
 * @author click33
 * @since 1.30.0
 */
public interface SaJsonTemplate {

	/**
	 * 序列化：对象 -> json 字符串
	 *
	 * @param obj /
	 * @return /
	 */
	String objectToJson(Object obj);

	/**
	 * 反序列化：json 字符串 → 对象
	 *
	 * @param jsonStr /
	 * @param type /
	 * @return /
	 * @param <T> /
	 */
	<T>T jsonToObject(String jsonStr, Class<T> type);

	/**
	 * 反序列化：json 字符串 → 对象 (自动判断类型)
	 *
	 * @param jsonStr /
	 * @return /
	 */
	default Object jsonToObject(String jsonStr) {
		return jsonToObject(jsonStr, Object.class);
	};

	/**
	 * 反序列化：json 字符串 → Map
	 *
	 * @param jsonStr /
	 * @return /
	 */
	default Map<String, Object> jsonToMap(String jsonStr) {
		return jsonToObject(jsonStr, Map.class);
	};
	
}

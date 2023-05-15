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
 * @since <= 1.34.0
 */
public interface SaJsonTemplate {

	/**
	 * 将任意对象序列化为 json 字符串
	 *
	 * @param obj 对象
	 * @return 转换后的 json 字符串
	 */
	String toJsonString(Object obj);

	/**
	 * 解析 json 字符串为 map 对象
	 * @param jsonStr json 字符串
	 * @return map 对象
	 */
	Map<String, Object> parseJsonToMap(String jsonStr);
	
}

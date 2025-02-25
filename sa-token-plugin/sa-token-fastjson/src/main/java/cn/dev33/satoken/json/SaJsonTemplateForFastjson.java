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

import com.alibaba.fastjson.JSON;

/**
 * JSON 转换器， Fastjson 版实现
 * 
 * @author click33
 * @since 1.34.0
 */
public class SaJsonTemplateForFastjson implements SaJsonTemplate {

	/**
	 * 序列化：对象 -> json 字符串
	 */
	@Override
	public String objectToJson(Object obj) {
		return JSON.toJSONString(obj);
	}

	/**
	 * 反序列化：json 字符串 → 对象
	 */
	@Override
	public<T> T jsonToObject(String jsonStr, Class<T> type) {
		return JSON.parseObject(jsonStr, type);
	}

}

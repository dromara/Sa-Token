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
package cn.dev33.satoken.spring.json;

import cn.dev33.satoken.error.SaSpringBootErrorCode;
import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.json.SaJsonTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 *  JSON 转换器， Jackson 版实现 
 * 
 * @author click33
 * @since 1.34.0
 */
public class SaJsonTemplateForJackson implements SaJsonTemplate {

	/**
	 * 底层 Mapper 对象 
	 */
	public ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 将任意对象转换为 json 字符串
	 */
	@Override
	public String toJsonString(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new SaJsonConvertException(e).setCode(SaSpringBootErrorCode.CODE_20103);
		}
	}
	
	/**
	 * 将 json 字符串解析为 Map
	 */
	@Override
	public Map<String, Object> parseJsonToMap(String jsonStr) {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = objectMapper.readValue(jsonStr, Map.class);
			return map;
		} catch (JsonProcessingException e) {
			throw new SaJsonConvertException(e).setCode(SaSpringBootErrorCode.CODE_20104);
		}
	}

}

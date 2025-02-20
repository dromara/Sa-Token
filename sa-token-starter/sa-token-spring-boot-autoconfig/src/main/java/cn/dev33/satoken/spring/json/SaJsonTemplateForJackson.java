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
import cn.dev33.satoken.util.SaFoxUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

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

	public SaJsonTemplateForJackson() {

		// 1、使 objectMapper 序列化时带上类型信息，以便该 json 字符串可以成功反序列化
		// 	  构建反序列化限制器，此处可以限制只允许指定类型或指定包下的类型才可以反序列化，此处指定所有类型都可以反序列化
		PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
				// 允许所有子类型反序列化（即反序列化时遇到的类）
				.allowIfSubType(Object.class)
				// 允许所有基类型反序列化（如 Object、自定义抽象类）
				.allowIfBaseType(Object.class)
				.build();
		// 	  启用全局默认类型（嵌入类型信息）
		objectMapper.activateDefaultTyping(
				ptv,
				// 对非 final 类嵌入类型信息
				ObjectMapper.DefaultTyping.NON_FINAL,
				// 类型信息以属性形式存在（"@class"）
				JsonTypeInfo.As.PROPERTY
				);

		// 2、使空 bean 在序列化时也能记录类型信息，而不是只序列化成 {}
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

	}

	/**
	 * 序列化：对象 -> json 字符串
	 */
	@Override
	public String objectToJson(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new SaJsonConvertException(e).setCode(SaSpringBootErrorCode.CODE_20103);
		}
	}

	/**
	 * 反序列化：json 字符串 → 对象
	 */
	@Override
	public Object jsonToObject(String jsonStr) {
		if(SaFoxUtil.isEmpty(jsonStr)) {
			return null;
		}
		try {
			Object value = objectMapper.readValue(jsonStr, Object.class);
			return value;
		} catch (JsonProcessingException e) {
			throw new SaJsonConvertException(e).setCode(SaSpringBootErrorCode.CODE_20106);
		}
	}

	/**
	 * 反序列化：json 字符串 → Map
	 */
	@Override
	public Map<String, Object> jsonToMap(String jsonStr) {
		if(SaFoxUtil.isEmpty(jsonStr)) {
			return null;
		}
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = objectMapper.readValue(jsonStr, Map.class);
			return map;
		} catch (JsonProcessingException e) {
			throw new SaJsonConvertException(e).setCode(SaSpringBootErrorCode.CODE_20104);
		}
	}

}

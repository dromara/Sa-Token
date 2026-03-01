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

import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.util.SaFoxUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

/**
 * JSON 转换器，Jackson 3 版实现
 *
 * @author click33
 * @since 1.45.0
 */
public class SaJsonTemplateForJackson3 implements SaJsonTemplate {

	/**
	 * 底层 Mapper 对象（带多态类型信息，用于 Session 等复杂对象序列化）
	 */
	public final JsonMapper objectMapper;

	/**
	 * 处理 Map 的 Mapper（无多态类型配置，用于简单 JSON 解析）
	 */
	public final JsonMapper mapObjectMapper;

	public SaJsonTemplateForJackson3() {
		// 1、构建反序列化限制器（PolymorphicTypeValidator），限制哪些类型可以被自动多态反序列化
		//    这里允许所有 Object 类型和其子类型参与多态反序列化，从而支持复杂对象（如 SaSession）的序列化、反序列化
		PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
				.allowIfSubType(Object.class)		// 允许 Object.class 的子类型被反序列化
				.allowIfBaseType(Object.class)		// 允许 Object.class 作为基类型（在启用多态时）
				.build();		// 构建 Validator 实例

		// 2、通过 JsonMapper 的 builder 模式创建 objectMapper（用于带多态类型信息的 JSON 处理，Jackson3 默认不可变需用构建器）
		this.objectMapper = JsonMapper.builder()
				// 启用默认多态类型处理，并以 "@class" 作为写入类型信息的属性名（即持久化时包含类型字段），仅对非 final 类型生效
				.activateDefaultTypingAsProperty(ptv, DefaultTyping.NON_FINAL, "@class")
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)		// 序列化时如果 bean 没有属性，则不抛出异常
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)		// 反序列化时如果有未知属性，也不抛出异常（兼容性更好）
				.build();		// 构建真正的 JsonMapper 实例

		// 3、创建 mapObjectMapper，用于简单类型（如 Map）的序列化和反序列化，和 objectMapper 独立，且不启用多态类型
		this.mapObjectMapper = JsonMapper.builder()
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)		// 序列化时如果 bean 没有属性，则不抛出异常
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)		// 反序列化时如果有未知属性，也不抛出异常，避免出错
				.build();		// 构建用于处理简单场景的 JsonMapper 实例
	}

	/**
	 * 序列化：对象 -> json 字符串
	 */
	@Override
	public String objectToJson(Object obj) {
		if (SaFoxUtil.isEmpty(obj)) {
			return null;
		}
		try {
			if (obj instanceof Map) {
				return mapObjectMapper.writeValueAsString(obj);
			}
			return objectMapper.writeValueAsString(obj);
		} catch (JacksonException e) {
			throw new SaJsonConvertException(e);
		}
	}

	/**
	 * 反序列化：json 字符串 → 对象
	 */
	@Override
	public <T> T jsonToObject(String jsonStr, Class<T> type) {
		if (SaFoxUtil.isEmpty(jsonStr)) {
			return null;
		}
		try {
			return objectMapper.readValue(jsonStr, type);
		} catch (JacksonException e) {
			throw new SaJsonConvertException(e);
		}
	}

	/**
	 * 将 json 字符串解析为 Map
	 */
	@Override
	public Map<String, Object> jsonToMap(String jsonStr) {
		if (SaFoxUtil.isEmpty(jsonStr)) {
			return null;
		}
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = mapObjectMapper.readValue(jsonStr, Map.class);
			return map;
		} catch (JacksonException e) {
			throw new SaJsonConvertException(e);
		}
	}

}

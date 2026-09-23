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
package cn.dev33.satoken.json;

import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.util.SaFoxUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

/**
 * JSON 转换器，Jackson 3 无类型信息版实现。
 *
 * <p>此实现不会向 JSON 写入 {@code @class} 等多态类型信息。需要还原业务对象时，
 * 请调用方通过 {@link #jsonToObject(String, Class)} 或 {@code SaSession.getModel(key, Class)}
 * 显式提供目标类型。</p>
 *
 * @author click33
 * @since 1.47.0
 */
public class SaJsonTemplateForJackson3Plain implements SaJsonTemplate {

	/** 底层 Mapper，不启用 DefaultTyping。 */
	public final JsonMapper objectMapper;

	public SaJsonTemplateForJackson3Plain() {
		this.objectMapper = createObjectMapper();
	}

	/**
	 * 创建无类型信息的 Jackson 3 Mapper。
	 *
	 * @return JsonMapper
	 */
	public static JsonMapper createObjectMapper() {
		return JsonMapper.builder()
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.build();
	}

	/** 序列化：对象 -> JSON 字符串。 */
	@Override
	public String objectToJson(Object obj) {
		if (SaFoxUtil.isEmpty(obj)) {
			return null;
		}
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JacksonException e) {
			throw new SaJsonConvertException(e);
		}
	}

	/** 反序列化：JSON 字符串 -> 指定类型对象。 */
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

	/** 将 JSON 字符串解析为 Map。 */
	@Override
	public Map<String, Object> jsonToMap(String jsonStr) {
		if (SaFoxUtil.isEmpty(jsonStr)) {
			return null;
		}
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = objectMapper.readValue(jsonStr, Map.class);
			return map;
		} catch (JacksonException e) {
			throw new SaJsonConvertException(e);
		}
	}

}

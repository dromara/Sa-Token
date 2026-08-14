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
import cn.dev33.satoken.strategy.SaJsonStrategy;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * JSON 转换器， Jackson 版实现
 * 
 * @author click33
 * @since 1.34.0
 */
public class SaJsonTemplateForJackson implements SaJsonTemplate {

	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String TIME_PATTERN = "HH:mm:ss";
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);

	/**
	 * 底层 Mapper 对象
	 */
	public ObjectMapper objectMapper = new ObjectMapper();

	public SaJsonTemplateForJackson() {

		// 1、使 objectMapper 序列化时带上类型信息，以便该 json 字符串可以成功反序列化
		//    构建反序列化限制器：仅允许 SaJsonStrategy 白名单内的类型
		PolymorphicTypeValidator ptv = buildAllowTypeValidator();
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

		// 3、配置 [ 忽略未知字段 ]
		this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 4、配置 [ 时间类型转换 ]
		JavaTimeModule timeModule = new JavaTimeModule();
		// 		LocalDateTime序列化与反序列化
		timeModule.addSerializer(new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
		timeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));
		// 		LocalDate序列化与反序列化
		timeModule.addSerializer(new LocalDateSerializer(DATE_FORMATTER));
		timeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));
		// 		LocalTime序列化与反序列化
		timeModule.addSerializer(new LocalTimeSerializer(TIME_FORMATTER));
		timeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(TIME_FORMATTER));
		//
		this.objectMapper.registerModule(timeModule);

	}

	/**
	 * 序列化：对象 -> json 字符串
	 */
	@Override
	public String objectToJson(Object obj) {
		if(SaFoxUtil.isEmpty(obj)) {
			return null;
		}
		try {
			if(obj instanceof Map) {
				return mapObjectMapper.writeValueAsString(obj);
			}
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new SaJsonConvertException(e);
		}
	}

	/**
	 * 反序列化：json 字符串 → 对象
	 */
	@Override
	public <T> T jsonToObject(String jsonStr, Class<T> type) {
		if(SaFoxUtil.isEmpty(jsonStr)) {
			return null;
		}
		try {
            return objectMapper.readValue(jsonStr, type);
		} catch (JsonProcessingException e) {
			throw toSaJsonConvertException(e);
		}
	}

	/*
	 * 由于构造方法中的如下代码：
	 * 		ObjectMapper.DefaultTyping.NON_FINAL,
	 * 导致 objectMapper 对所有非 final 类型的反序列化均要求提供 @class 信息。
	 *
	 * 例如：
	 * 		一个简单的字符串 {"name": "zhangsan"} 将无法反序列化为 Map 对象，因为这个字符串上没有提供 @class 信息。
	 *
	 * 尝试诸多方案，均未能解决此问题。
	 *
	 * 因此，以下代码将为 Map 的反序列化提供一个独立干净的 mapObjectMapper 对象，保证其不受构造方法中关于类型配置的影响。
	 *
	 */

	/**
	 * 处理 Map 的序列化与反序列化
	 */
	public ObjectMapper mapObjectMapper = new ObjectMapper();

	/**
	 * 将 json 字符串解析为 Map
	 */
	@Override
	public Map<String, Object> jsonToMap(String jsonStr) {
		if(SaFoxUtil.isEmpty(jsonStr)) {
			return null;
		}
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = mapObjectMapper.readValue(jsonStr, Map.class);
			return map;
		} catch (JsonProcessingException e) {
			throw new SaJsonConvertException(e);
		}
	}


	// ----------------------- 内部方法

	/**
	 * 根据 {@link SaJsonStrategy} 白名单构建 Jackson 多态反序列化校验器
	 *
	 * @return 仅允许白名单内类型参与多态反序列化的 {@link PolymorphicTypeValidator}
	 */
	public static PolymorphicTypeValidator buildAllowTypeValidator() {
		BasicPolymorphicTypeValidator.Builder builder = BasicPolymorphicTypeValidator.builder();
		for (Class<?> type : SaJsonStrategy.instance.getSaJsonAllowTypeList()) {
			builder.allowIfSubType(type);
		}
		return builder.build();
	}

	/**
	 * 将 Jackson 反序列化异常包装为 {@link SaJsonConvertException}；
	 * 若为多态类型白名单拦截，则明确提示无法反序列化的类型名。
	 */
	static SaJsonConvertException toSaJsonConvertException(JsonProcessingException e) {
		InvalidTypeIdException typeIdEx = findInvalidTypeIdException(e);
		if (typeIdEx != null && typeIdEx.getTypeId() != null) {
			return new SaJsonConvertException(
					"无法反序列化的类型：" + typeIdEx.getTypeId() + "，请先将其注册到 JSON 全局类型白名单",
					e);
		}
		return new SaJsonConvertException(e);
	}

	static InvalidTypeIdException findInvalidTypeIdException(Throwable e) {
		for (Throwable t = e; t != null; t = t.getCause()) {
			if (t instanceof InvalidTypeIdException) {
				return (InvalidTypeIdException) t;
			}
		}
		return null;
	}

}

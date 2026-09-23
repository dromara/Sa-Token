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
package cn.dev33.satoken.session;

import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.json.SaJsonTemplateForJackson3Plain;
import cn.dev33.satoken.util.SaFoxUtil;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Jackson 3 无类型信息定制版 SaSession，重写类型转换 API。
 *
 * @author click33
 * @since 1.47.0
 */
public class SaSessionForJackson3PlainCustomized extends SaSession {

	private static final long serialVersionUID = -6414954645763129604L;

	/** 用于将反序列化后 Map 形态的值显式转换为业务类型。 */
	private static final JsonMapper OBJECT_MAPPER = SaJsonTemplateForJackson3Plain.createObjectMapper();

	/** 构建一个 SaSession 对象。 */
	public SaSessionForJackson3PlainCustomized() {
		super();
	}

	/**
	 * 构建一个 SaSession 对象。
	 * @param id Session 的 id
	 */
	public SaSessionForJackson3PlainCustomized(String id) {
		super(id);
	}

	/**
	 * 取值（指定转换类型）。
	 *
	 * <p>Plain JSON 不保存业务对象的运行时类型；因此对象从持久化存储恢复后，
	 * 需要在这里以调用方提供的 {@code cs} 显式转换。</p>
	 *
	 * @param <T> 泛型
	 * @param key key
	 * @param cs 指定转换类型
	 * @return 值
	 */
	@Override
	public <T> T getModel(String key, Class<T> cs) {
		Object value = get(key);
		if (SaFoxUtil.isBasicType(cs)) {
			return SaFoxUtil.getValueByType(value, cs);
		}
		if (valueIsNull(value)) {
			return null;
		}
		if (cs.isInstance(value)) {
			return cs.cast(value);
		}
		try {
			return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(value), cs);
		} catch (JacksonException e) {
			throw new SaJsonConvertException(e);
		}
	}

}

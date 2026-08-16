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

import cn.dev33.satoken.json.SaJsonTemplateForForyJson;
import cn.dev33.satoken.util.SaFoxUtil;
import org.apache.fory.json.JsonObject;

/**
 * Fory JSON 定制版 SaSession，重写类型转换API
 *
 * @author click33
 * @since 1.46.0
 */
public class SaSessionForForyJsonCustomized extends SaSession {

	private static final long serialVersionUID = -7600983549653130681L;

	/**
	 * 构建一个 SaSession 对象
	 */
	public SaSessionForForyJsonCustomized() {
		super();
	}

	/**
	 * 构建一个 SaSession 对象
	 * @param id Session 的 id
	 */
	public SaSessionForForyJsonCustomized(String id) {
		super(id);
	}

	/**
	 * 取值 (指定转换类型)
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
		if (value instanceof JsonObject) {
			String json = SaJsonTemplateForForyJson.FORY_JSON.toJson(value);
			return SaJsonTemplateForForyJson.FORY_JSON.fromJson(json, cs);
		}
		return SaJsonTemplateForForyJson.FORY_JSON.fromJson(value.toString(), cs);
	}

}

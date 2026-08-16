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

import cn.dev33.satoken.util.SaFoxUtil;
import org.apache.fory.json.ForyJson;

/**
 * JSON 转换器， Apache Fory JSON 版实现
 *
 * @author click33
 * @since 1.46.0
 */
public class SaJsonTemplateForForyJson implements SaJsonTemplate {

	/**
	 * 全局复用，ForyJson 线程安全
	 */
	public static final ForyJson FORY_JSON = ForyJson.builder().build();

	/**
	 * 序列化：对象 -> json 字符串
	 */
	@Override
	public String objectToJson(Object obj) {
		if (SaFoxUtil.isEmpty(obj)) {
			return null;
		}
		return FORY_JSON.toJson(obj);
	}

	/**
	 * 反序列化：json 字符串 → 对象
	 */
	@Override
	public <T> T jsonToObject(String jsonStr, Class<T> type) {
		if (SaFoxUtil.isEmpty(jsonStr)) {
			return null;
		}
		return FORY_JSON.fromJson(jsonStr, type);
	}

}

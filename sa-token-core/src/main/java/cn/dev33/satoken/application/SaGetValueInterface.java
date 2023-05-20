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
package cn.dev33.satoken.application;

import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 对取值的一组方法封装
 * <p> 封装 SaStorage、SaSession、SaApplication 等存取值的一些固定方法，减少重复编码 </p>
 * 
 * @author click33
 * @since 1.31.0
 */
public interface SaGetValueInterface {

	// --------- 需要子类实现的方法 
	
	/**
	 * 取值 
	 * @param key key 
	 * @return 值 
	 */
	Object get(String key);
	
	
	// --------- 接口提供封装的方法 

	/**
	 * 取值 (指定默认值)
	 *
	 * @param <T> 默认值的类型
	 * @param key key 
	 * @param defaultValue 取不到值时返回的默认值 
	 * @return 值 
	 */
	default <T> T get(String key, T defaultValue) {
		return getValueByDefaultValue(get(key), defaultValue);
	}

	/**
	 * 取值 (转String类型) 
	 * @param key key 
	 * @return 值 
	 */
	default String getString(String key) {
		Object value = get(key);
		if(value == null) {
			return null;
		}
		return String.valueOf(value);
	}

	/**
	 * 取值 (转int类型) 
	 * @param key key 
	 * @return 值 
	 */
	default int getInt(String key) {
		return getValueByDefaultValue(get(key), 0);
	}

	/**
	 * 取值 (转long类型) 
	 * @param key key 
	 * @return 值 
	 */
	default long getLong(String key) {
		return getValueByDefaultValue(get(key), 0L);
	}

	/**
	 * 取值 (转double类型) 
	 * @param key key 
	 * @return 值 
	 */
	default double getDouble(String key) {
		return getValueByDefaultValue(get(key), 0.0);
	}

	/**
	 * 取值 (转float类型) 
	 * @param key key 
	 * @return 值 
	 */
	default float getFloat(String key) {
		return getValueByDefaultValue(get(key), 0.0f);
	}

	/**
	 * 取值 (指定转换类型)
	 * @param <T> 泛型
	 * @param key key 
	 * @param cs 指定转换类型 
	 * @return 值 
	 */
	default <T> T getModel(String key, Class<T> cs) {
		return SaFoxUtil.getValueByType(get(key), cs);
	}

	/**
	 * 取值 (指定转换类型, 并指定值为 null 时返回的默认值)
	 * @param <T> 泛型
	 * @param key key 
	 * @param cs 指定转换类型 
	 * @param defaultValue 值为Null时返回的默认值
	 * @return 值 
	 */
	@SuppressWarnings("unchecked")
	default <T> T getModel(String key, Class<T> cs, Object defaultValue) {
		Object value = get(key);
		if(valueIsNull(value)) {
			return (T)defaultValue;
		}
		return SaFoxUtil.getValueByType(value, cs);
	}

	/**
	 * 是否含有某个 key
	 * @param key 指定 key
	 * @return 是否含有
	 */
	default boolean has(String key) {
		return !valueIsNull(get(key));
	}

	
	// --------- 内部工具方法 

	/**
	 * 判断一个值是否为null 
	 * @param value 指定值 
	 * @return 此value是否为null 
	 */
	default boolean valueIsNull(Object value) {
		return value == null || value.equals("");
	}

	/**
	 * 根据默认值来获取值
	 * @param <T> 泛型
	 * @param value 值 
	 * @param defaultValue 默认值
	 * @return 转换后的值 
	 */
	@SuppressWarnings("unchecked")
	default <T> T getValueByDefaultValue(Object value, T defaultValue) {
		
		// 如果 obj 为 null，则直接返回默认值 
		if(valueIsNull(value)) {
			return defaultValue;
		}
		
		// 开始转换类型
		Class<T> cs = (Class<T>) defaultValue.getClass();
		return SaFoxUtil.getValueByType(value, cs);
	}
	
	
}

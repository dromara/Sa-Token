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
package cn.dev33.satoken.application;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		T model = getModel(key, cs);
		return valueIsNull(model) ? (T)defaultValue : model;
	}

	/**
	 * 取值 (指定 List 元素类型)
	 *
	 * @param key key
	 * @param elementClass List 元素类型
	 * @param <T> 元素泛型
	 * @return 转换后的 List；值为 null 时返回空 List
	 */
	default <T> List<T> getList(String key, Class<T> elementClass) {
		return convertToList(get(key), key, elementClass);
	}

	/**
	 * 取值 (指定 Set 元素类型)
	 *
	 * @param key key
	 * @param elementClass Set 元素类型
	 * @param <T> 元素泛型
	 * @return 转换后的 Set；值为 null 时返回空 Set
	 */
	default <T> Set<T> getSet(String key, Class<T> elementClass) {
		return convertToSet(get(key), key, elementClass);
	}

	/**
	 * 取值 (指定 Map 键值类型)
	 *
	 * @param key key
	 * @param keyClass Map 键类型
	 * @param valueClass Map 值类型
	 * @param <K> 键泛型
	 * @param <V> 值泛型
	 * @return 转换后的 Map；值为 null 时返回空 Map
	 */
	default <K, V> Map<K, V> getMap(String key, Class<K> keyClass, Class<V> valueClass) {
		return convertToMap(get(key), key, keyClass, valueClass);
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

	/**
	 * 将 value 转换为指定元素类型的 List
	 *
	 * @param value 原始值
	 * @param key key（仅用于异常提示）
	 * @param elementClass List 元素类型
	 * @param <T> 元素泛型
	 * @return 转换后的 List；值为 null 时返回空 List
	 */
	default <T> List<T> convertToList(Object value, String key, Class<T> elementClass) {
		if(valueIsNull(value)) {
			return new ArrayList<>();
		}
		Collection<?> rawCollection = resolveCollection(value, key);
		List<T> list = new ArrayList<>(rawCollection.size());
		for (Object item : rawCollection) {
			list.add(convertToElement(item, elementClass));
		}
		return list;
	}

	/**
	 * 将 value 转换为指定元素类型的 Set
	 *
	 * @param value 原始值
	 * @param key key（仅用于异常提示）
	 * @param elementClass Set 元素类型
	 * @param <T> 元素泛型
	 * @return 转换后的 Set；值为 null 时返回空 Set
	 */
	default <T> Set<T> convertToSet(Object value, String key, Class<T> elementClass) {
		if(valueIsNull(value)) {
			return new LinkedHashSet<>();
		}
		Collection<?> rawCollection = resolveCollection(value, key);
		Set<T> set = new LinkedHashSet<>(rawCollection.size());
		for (Object item : rawCollection) {
			set.add(convertToElement(item, elementClass));
		}
		return set;
	}

	/**
	 * 将 value 转换为指定键值类型的 Map
	 *
	 * @param value 原始值
	 * @param key key（仅用于异常提示）
	 * @param keyClass Map 键类型
	 * @param valueClass Map 值类型
	 * @param <K> 键泛型
	 * @param <V> 值泛型
	 * @return 转换后的 Map；值为 null 时返回空 Map
	 */
	default <K, V> Map<K, V> convertToMap(Object value, String key, Class<K> keyClass, Class<V> valueClass) {
		if(valueIsNull(value)) {
			return new LinkedHashMap<>();
		}
		Map<?, ?> rawMap = resolveMap(value, key);
		Map<K, V> map = new LinkedHashMap<>(rawMap.size());
		for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
			map.put(
				convertToElement(entry.getKey(), keyClass),
				convertToElement(entry.getValue(), valueClass)
			);
		}
		return map;
	}

	/**
	 * 解析 Map 集合
	 *
	 * @param value 原始值
	 * @param key key（仅用于异常提示）
	 * @return Map
	 */
	default Map<?, ?> resolveMap(Object value, String key) {
		if(value instanceof Map) {
			return (Map<?, ?>) value;
		}
		throw new SaTokenException("key [" + key + "] 的值不是 Map 类型");
	}

	/**
	 * 解析 List / Set 集合（JSON 持久化读回时 Set 可能变为 List）
	 *
	 * @param value 原始值
	 * @param key key（仅用于异常提示）
	 * @return 集合
	 */
	default Collection<?> resolveCollection(Object value, String key) {
		if(value instanceof List) {
			return (List<?>) value;
		}
		if(value instanceof Set) {
			return (Set<?>) value;
		}
		throw new SaTokenException("key [" + key + "] 的值不是 List 或 Set 类型");
	}

	/**
	 * 将单个元素转换为指定类型
	 *
	 * @param item 原始元素
	 * @param elementClass 目标类型
	 * @param <T> 元素泛型
	 * @return 转换后的元素
	 */
	default <T> T convertToElement(Object item, Class<T> elementClass) {
		if(item == null) {
			return null;
		}
		if(SaFoxUtil.isBasicType(elementClass)) {
			return SaFoxUtil.getValueByType(item, elementClass);
		}
		if(elementClass.isInstance(item)) {
			return elementClass.cast(item);
		}
		SaJsonTemplate jsonTemplate = SaManager.getSaJsonTemplate();
		String jsonString = jsonTemplate.objectToJson(item);
		return jsonTemplate.jsonToObject(jsonString, elementClass);
	}


}

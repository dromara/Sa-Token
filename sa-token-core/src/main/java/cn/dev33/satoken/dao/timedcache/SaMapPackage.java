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
package cn.dev33.satoken.dao.timedcache;

import java.util.Set;

/**
 * Map 包装类
 *
 * @author click33
 * @since 1.41.0
 */
public interface SaMapPackage<V> {

	/**
	 * 获取底层被包装的源对象
	 *
	 * @return /
	 */
	Object getSource();


	/**
	 * 读
	 *
	 * @param key /
	 * @return /
	 */
	V get(String key);

	/**
	 * 写
	 *
	 * @param key /
	 * @param value /
	 */
	void put(String key, V value);

	/**
	 * 写，仅在 key 不存在时生效
	 *
	 * <p> 默认实现退化为 get + put 两步操作，非原子，实现类建议覆写为原子操作。
	 *
	 * @param key /
	 * @param value /
	 * @return key 原本对应的旧值，不存在时返回 null
	 */
	default V putIfAbsent(String key, V value) {
		V oldValue = get(key);
		if(oldValue == null) {
			put(key, value);
		}
		return oldValue;
	}

	/**
	 * 删
	 * @param key /
	 */
	void remove(String key);

	/**
	 * 所有 key
	 */
	Set<String> keySet();

}

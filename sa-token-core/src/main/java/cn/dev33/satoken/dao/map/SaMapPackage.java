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
package cn.dev33.satoken.dao.map;

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
	 * 删
	 * @param key /
	 */
	void remove(String key);

	/**
	 * 所有 key
	 */
	Set<String> keySet();

}

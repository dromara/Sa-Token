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
package cn.dev33.satoken.dao;

import cn.dev33.satoken.dao.map.SaMapPackage;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Map 包装类 (Caffeine 版)
 *
 * @author click33
 * @since 1.41.0
 */
public class SaMapPackageForCaffeine<V> implements SaMapPackage<V> {

	public Cache<String, V> cache = Caffeine.newBuilder()
			.expireAfterWrite(Long.MAX_VALUE, TimeUnit.SECONDS)
			.maximumSize(Integer.MAX_VALUE)
			.build();

	@Override
	public Object getSource() {
		return cache;
	}

	/**
	 * 读
	 *
	 * @param key /
	 * @return /
	 */
	@Override
	public V get(String key) {
		return cache.getIfPresent(key);
	}

	/**
	 * 写
	 *
	 * @param key /
	 * @param value /
	 */
	@Override
	public void put(String key, V value) {
		cache.put(key, value);
	}

	/**
	 * 删
	 * @param key /
	 */
	@Override
	public void remove(String key) {
		cache.invalidate(key);
	}

	/**
	 * 所有 key
	 */
	@Override
	public Set<String> keySet() {
		return cache.asMap().keySet();
	}

}

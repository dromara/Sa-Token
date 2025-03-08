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
package cn.dev33.satoken.dao.auto;

/**
 * SaTokenDao 次级实现：String 读写跟随 Object 读写 (推荐内存型缓存实现 implements 此接口)
 *
 * @author click33
 * @since 1.41.0
 */
public interface SaTokenDaoByStringFollowObject extends SaTokenDaoBySessionFollowObject {

	// --------------------- String 读写 ---------------------

	@Override
	default String get(String key) {
		return (String) getObject(key);
	}

	@Override
	default void set(String key, String value, long timeout) {
		setObject(key, value, timeout);
	}

	@Override
	default void update(String key, String value) {
		updateObject(key, value);
	}

	@Override
	default void delete(String key) {
		deleteObject(key);
	}

	@Override
	default long getTimeout(String key) {
		return getObjectTimeout(key);
	}

	@Override
	default void updateTimeout(String key, long timeout) {
		updateObjectTimeout(key, timeout);
	}

}

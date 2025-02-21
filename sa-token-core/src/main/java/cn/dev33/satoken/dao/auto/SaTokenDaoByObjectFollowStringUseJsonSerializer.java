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

import cn.dev33.satoken.SaManager;

/**
 * SaTokenDao 次级实现，以 JSON 序列化方式实现 Object 读写相关操作
 *
 * @author click33
 * @since 1.41.0
 */
public interface SaTokenDaoByObjectFollowStringUseJsonSerializer extends SaTokenDaoBySessionFollowObject {

	// --------------------- Object 读写 ---------------------

	/**
	 * 获取 Object，如无返空
	 *
	 * @param key 键名称
	 * @return object
	 */
	@Override
	default Object getObject(String key) {
		String jsonString = get(key);
		return SaManager.getSaJsonTemplate().jsonToObject(jsonString);
	}

	/**
	 * 写入 Object，并设定存活时间 （单位: 秒）
	 *
	 * @param key     键名称
	 * @param object  值
	 * @param timeout 存活时间（值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储）
	 */
	@Override
	default void setObject(String key, Object object, long timeout) {
		String jsonString = SaManager.getSaJsonTemplate().objectToJson(object);
		set(key, jsonString, timeout);
	}

	/**
	 * 更新 Object （过期时间不变）
	 * @param key 键名称 
	 * @param object 值 
	 */
	@Override
	default void updateObject(String key, Object object) {
		String jsonString = SaManager.getSaJsonTemplate().objectToJson(object);
		update(key, jsonString);
	}

	/**
	 * 删除 Object
	 * @param key 键名称 
	 */
	@Override
	default void deleteObject(String key) {
		delete(key);
	}
	
	/**
	 * 获取 Object 的剩余存活时间 （单位: 秒）
	 * @param key 指定 key
	 * @return 这个 key 的剩余存活时间
	 */
	@Override
	default long getObjectTimeout(String key) {
		return getTimeout(key);
	}
	
	/**
	 * 修改 Object 的剩余存活时间（单位: 秒）
	 * @param key 指定 key
	 * @param timeout 剩余存活时间
	 */
	@Override
	default void updateObjectTimeout(String key, long timeout) {
		updateTimeout(key, timeout);
	}

}

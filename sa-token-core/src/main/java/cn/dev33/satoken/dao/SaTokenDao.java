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

import cn.dev33.satoken.session.SaSession;

import java.util.List;

/**
 * Sa-Token 持久层接口
 *
 * <p>
 *     此接口的不同实现类可将数据存储至不同位置，如：内存Map、Redis 等等。
 *     如果你要自定义数据存储策略，也需通过实现此接口来完成。
 * </p>
 *
 * @author click33
 * @since 1.10.0
 */
public interface SaTokenDao {

	/** 常量，表示一个 key 永不过期 （在一个 key 被标注为永远不过期时返回此值） */
	long NEVER_EXPIRE = -1;
	
	/** 常量，表示系统中不存在这个缓存（在对不存在的 key 获取剩余存活时间时返回此值） */
	long NOT_VALUE_EXPIRE = -2;

	
	// --------------------- 字符串读写 ---------------------
	
	/**
	 * 获取 value，如无返空
	 *
	 * @param key 键名称 
	 * @return value
	 */
	String get(String key);

	/**
	 * 写入 value，并设定存活时间（单位: 秒）
	 *
	 * @param key 键名称 
	 * @param value 值 
	 * @param timeout 数据有效期（值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储）
	 */
	void set(String key, String value, long timeout);

	/**
	 * 更新 value （过期时间不变）
	 * @param key 键名称 
	 * @param value 值 
	 */
	void update(String key, String value);

	/**
	 * 删除 value
	 * @param key 键名称 
	 */
	void delete(String key);
	
	/**
	 * 获取 value 的剩余存活时间（单位: 秒）
	 * @param key 指定 key
	 * @return 这个 key 的剩余存活时间
	 */
	long getTimeout(String key);
	
	/**
	 * 修改 value 的剩余存活时间（单位: 秒）
	 * @param key 指定 key
	 * @param timeout 过期时间（单位: 秒）
	 */
	void updateTimeout(String key, long timeout);

	
	// --------------------- 对象读写 ---------------------

	/**
	 * 获取 Object，如无返空
	 * @param key 键名称 
	 * @return object
	 */
	Object getObject(String key);

	/**
	 * 写入 Object，并设定存活时间 （单位: 秒）
	 * @param key 键名称 
	 * @param object 值 
	 * @param timeout 存活时间（值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储）
	 */
	void setObject(String key, Object object, long timeout);

	/**
	 * 更新 Object （过期时间不变）
	 * @param key 键名称 
	 * @param object 值 
	 */
	void updateObject(String key, Object object);

	/**
	 * 删除 Object
	 * @param key 键名称 
	 */
	void deleteObject(String key);
	
	/**
	 * 获取 Object 的剩余存活时间 （单位: 秒）
	 * @param key 指定 key
	 * @return 这个 key 的剩余存活时间
	 */
	long getObjectTimeout(String key);
	
	/**
	 * 修改 Object 的剩余存活时间（单位: 秒）
	 * @param key 指定 key
	 * @param timeout 剩余存活时间
	 */
	void updateObjectTimeout(String key, long timeout);

	
	// --------------------- SaSession 读写 （默认复用 Object 读写方法） ---------------------

	/**
	 * 获取 SaSession，如无返空
	 * @param sessionId sessionId
	 * @return SaSession
	 */
	default SaSession getSession(String sessionId) {
		return (SaSession)getObject(sessionId);
	}

	/**
	 * 写入 SaSession，并设定存活时间（单位: 秒）
	 * @param session 要保存的 SaSession 对象
	 * @param timeout 过期时间（单位: 秒）
	 */
	default void setSession(SaSession session, long timeout) {
		setObject(session.getId(), session, timeout);
	}

	/**
	 * 更新 SaSession
	 * @param session 要更新的 SaSession 对象
	 */
	default void updateSession(SaSession session) {
		updateObject(session.getId(), session);
	}
	
	/**
	 * 删除 SaSession
	 * @param sessionId sessionId
	 */
	default void deleteSession(String sessionId) {
		deleteObject(sessionId);
	}

	/**
	 * 获取 SaSession 剩余存活时间（单位: 秒）
	 * @param sessionId 指定 SaSession
	 * @return 这个 SaSession 的剩余存活时间
	 */
	default long getSessionTimeout(String sessionId) {
		return getObjectTimeout(sessionId);
	}
	
	/**
	 * 修改 SaSession 剩余存活时间（单位: 秒）
	 * @param sessionId 指定 SaSession
	 * @param timeout 剩余存活时间
	 */
	default void updateSessionTimeout(String sessionId, long timeout) {
		updateObjectTimeout(sessionId, timeout);
	}
	
	
	// --------------------- 会话管理 ---------------------

	/**
	 * 搜索数据 
	 * @param prefix 前缀 
	 * @param keyword 关键字 
	 * @param start 开始处索引
	 * @param size 获取数量  (-1代表从 start 处一直取到末尾)
	 * @param sortType 排序类型（true=正序，false=反序）
	 * 
	 * @return 查询到的数据集合 
	 */
	List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType);
	
	
}

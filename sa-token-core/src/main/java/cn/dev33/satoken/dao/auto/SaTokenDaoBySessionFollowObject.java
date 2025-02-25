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

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaStrategy;

/**
 * SaTokenDao 次级实现：SaSession 读写跟随 Object 读写
 *
 * @author click33
 * @since 1.41.0
 */
public interface SaTokenDaoBySessionFollowObject extends SaTokenDao {

	// --------------------- SaSession 读写 （默认复用 Object 读写方法） ---------------------

	/**
	 * 获取 SaSession，如无返空
	 * @param sessionId sessionId
	 * @return SaSession
	 */
	default SaSession getSession(String sessionId) {
		return getObject(sessionId, SaStrategy.instance.sessionClassType);
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

}

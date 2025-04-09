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
package cn.dev33.satoken.session.raw;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.strategy.SaStrategy;

/**
 * SaSession 读写工具类
 * 
 * @author click33
 * @since 1.42.0
 */
public class SaRawSessionUtil {

	private SaRawSessionUtil() {
	}

	/**
	 * 拼接Key: 在存储 SaSession 时应该使用的 key
	 *
	 * @param type 类型
	 * @param valueId 唯一标识
	 * @return sessionId
	 */
	public static String splicingSessionKey(String type, Object valueId) {
		return SaManager.getConfig().getTokenName() + ":raw-session:" + type + ":" + valueId;
	}

	/**
	 * 判断：指定 SaSession 是否存在
	 *
	 * @param type /
	 * @param valueId /
	 * @return 是否存在
	 */
	public static boolean isExists(String type, Object valueId) {
		return SaManager.getSaTokenDao().getSession(splicingSessionKey(type, valueId)) != null;
	}

	/**
	 * 获取指定 SaSession 对象, 如果此 SaSession 尚未在 Cache 创建，isCreate 参数代表是否则新建并返回
	 *
	 * @param type /
	 * @param valueId /
	 * @param isCreate  如果此 SaSession 尚未在 DB 创建，是否新建并返回
	 * @return SaSession 对象
	 */
	public static SaSession getSessionById(String type, Object valueId, boolean isCreate) {
		String sessionId = splicingSessionKey(type, valueId);
		SaSession session = SaManager.getSaTokenDao().getSession(sessionId);
		if (session == null && isCreate) {
			session = SaStrategy.instance.createSession.apply(sessionId);
			session.setType(type);
			// TODO 过期时间
			SaManager.getSaTokenDao().setSession(session, SaManager.getConfig().getTimeout());
		}
		return session;
	}

	/**
	 * 获取指定 SaSession, 如果此 SaSession 尚未在 DB 创建，则新建并返回
	 *
	 * @param type /
	 * @param valueId /
	 * @return SaSession 对象
	 */
	public static SaSession getSessionById(String type, Object valueId) {
		return getSessionById(type, valueId, true);
	}

	/**
	 * 删除指定 SaSession
	 *
	 * @param type /
	 * @param valueId /
	 */
	public static void deleteSessionById(String type, Object valueId) {
		SaManager.getSaTokenDao().deleteSession(splicingSessionKey(type, valueId));
	}

}

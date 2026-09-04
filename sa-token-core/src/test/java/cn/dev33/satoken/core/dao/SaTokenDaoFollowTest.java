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
package cn.dev33.satoken.core.dao;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.auto.SaTokenDaoByObjectFollowString;
import cn.dev33.satoken.dao.timedcache.SaMapPackageForConcurrentHashMap;
import cn.dev33.satoken.dao.timedcache.SaTimedCache;
import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJdkUseHex;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaFoxUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * SaTokenDaoByObjectFollowString / SaTokenDaoBySessionFollowObject 默认跟随实现测试
 */
@SaTokenTest
public class SaTokenDaoFollowTest {

	private final ObjectFollowStringDao dao = new ObjectFollowStringDao();

	@BeforeEach
	void setUpSerializer() {
		SaManager.setSaSerializerTemplate(new SaSerializerTemplateForJdkUseHex());
		SaManager.setSaTokenDao(dao);
	}

	/** Object 存储应跟随 String 接口同步 */
	@Test
	void objectFollowsString() {
		dao.setObject("obj-key", "hello", 60);
		String raw = dao.get("obj-key");
		Assertions.assertNotNull(raw);
		Assertions.assertNotEquals("hello", raw);
		Assertions.assertEquals("hello", dao.getObject("obj-key"));
		Assertions.assertEquals("hello", dao.getObject("obj-key", String.class));
		Assertions.assertTrue(dao.getTimeout("obj-key") > 0);
		Assertions.assertTrue(dao.getObjectTimeout("obj-key") > 0);

		dao.updateObject("obj-key", "world");
		Assertions.assertEquals("world", dao.getObject("obj-key"));
		Assertions.assertNotEquals("world", dao.get("obj-key"));

		dao.updateObjectTimeout("obj-key", 120);
		Assertions.assertTrue(dao.getObjectTimeout("obj-key") >= 60);

		dao.deleteObject("obj-key");
		Assertions.assertNull(dao.get("obj-key"));
		Assertions.assertNull(dao.getObject("obj-key"));
	}

	/** Session 存储应跟随 Object 接口同步 */
	@Test
	void sessionFollowsObject() {
		SaSession session = new SaSession("session-follow-1");
		session.set("role", "admin");

		dao.setSession(session, 60);
		SaSession loaded = dao.getSession("session-follow-1");
		Assertions.assertNotNull(loaded);
		Assertions.assertEquals("session-follow-1", loaded.getId());
		Assertions.assertEquals("admin", loaded.get("role"));
		Assertions.assertTrue(dao.getSessionTimeout("session-follow-1") > 0);

		loaded.set("role", "user");
		dao.updateSession(loaded);
		Assertions.assertEquals("user", dao.getSession("session-follow-1").get("role"));

		dao.updateSessionTimeout("session-follow-1", 90);
		Assertions.assertTrue(dao.getSessionTimeout("session-follow-1") >= 60);

		dao.deleteSession("session-follow-1");
		Assertions.assertNull(dao.getSession("session-follow-1"));
	}

	/**
	 * 以 String 为主存储、Object 跟随 String 的测试用 DAO
	 */
	private static class ObjectFollowStringDao implements SaTokenDaoByObjectFollowString {

		private final SaTimedCache timedCache = new SaTimedCache(
				new SaMapPackageForConcurrentHashMap<>(),
				new SaMapPackageForConcurrentHashMap<>()
		);

		@Override
		public String get(String key) {
			return (String) timedCache.getObject(key);
		}

		@Override
		public void set(String key, String value, long timeout) {
			timedCache.setObject(key, value, timeout);
		}

		@Override
		public void update(String key, String value) {
			timedCache.updateObject(key, value);
		}

		@Override
		public void delete(String key) {
			timedCache.deleteObject(key);
		}

		@Override
		public long getTimeout(String key) {
			return timedCache.getObjectTimeout(key);
		}

		@Override
		public void updateTimeout(String key, long timeout) {
			timedCache.updateObjectTimeout(key, timeout);
		}

		@Override
		public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
			return SaFoxUtil.searchList(timedCache.keySet(), prefix, keyword, start, size, sortType);
		}
	}

}

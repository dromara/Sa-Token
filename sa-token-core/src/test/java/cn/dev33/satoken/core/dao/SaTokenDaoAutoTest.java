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

import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.dao.auto.SaTokenDaoByStringFollowObject;
import cn.dev33.satoken.session.SaSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SaTokenDao 自动跟随接口默认实现测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaTokenDaoAutoTest {

	private final SaTokenDaoByStringFollowObject dao = new SaTokenDaoDefaultImpl();

	/** String 存储应跟随 Object 接口同步 */
	@Test
	void stringFollowsObject() {
		dao.setObject("key-str", "hello", 60);
		Assertions.assertEquals("hello", dao.get("key-str"));
		Assertions.assertEquals("hello", dao.getObject("key-str"));
		Assertions.assertTrue(dao.getTimeout("key-str") > 0);
		Assertions.assertTrue(dao.getObjectTimeout("key-str") > 0);

		dao.update("key-str", "world");
		Assertions.assertEquals("world", dao.getObject("key-str"));

		dao.deleteObject("key-str");
		Assertions.assertNull(dao.get("key-str"));
	}

	/** Session 存储应跟随 Object 接口同步 */
	@Test
	void sessionFollowsObject() {
		SaSession session = new SaSession("session-1001");
		session.set("name", "zhangsan");

		dao.setSession(session, 60);
		SaSession loaded = dao.getSession("session-1001");
		Assertions.assertNotNull(loaded);
		Assertions.assertEquals("session-1001", loaded.getId());
		Assertions.assertEquals("zhangsan", loaded.get("name"));
		Assertions.assertTrue(dao.getSessionTimeout("session-1001") > 0);

		loaded.set("name", "lisi");
		dao.updateSession(loaded);
		Assertions.assertEquals("lisi", dao.getSession("session-1001").get("name"));

		dao.deleteSession("session-1001");
		Assertions.assertNull(dao.getSession("session-1001"));
	}

}

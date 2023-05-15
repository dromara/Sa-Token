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
package cn.dev33.satoken.core.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.session.SaSession;

/**
 * SaTokenDao 持久层 测试 
 * 
 * @author click33
 * @since: 2022-2-9 15:39:38
 */
public class SaTokenDaoTest {

	SaTokenDao dao = new SaTokenDaoDefaultImpl();

	// 字符串存取 
    @Test
    public void get() {
    	dao.set("name", "zhangsan", 60);
    	Assertions.assertEquals(dao.get("name"), "zhangsan");
    	Assertions.assertTrue(dao.getTimeout("name") <= 60);
    	Assertions.assertEquals(dao.getTimeout("name2"), -2);
    	
    	dao.update("name", "lisi");
    	Assertions.assertEquals(dao.get("name"), "lisi");
    	
    	dao.updateTimeout("name", 100);
    	Assertions.assertTrue(dao.getTimeout("name") <= 100);
    	
    	dao.delete("name");
    	Assertions.assertEquals(dao.get("name"), null);
    }

	// 对象存取 
    @Test
    public void getObject() {
    	dao.setObject("name", "zhangsan", 60);
    	Assertions.assertEquals(dao.getObject("name"), "zhangsan");
    	Assertions.assertTrue(dao.getObjectTimeout("name") <= 60);
    	
    	dao.updateObject("name", "lisi");
    	Assertions.assertEquals(dao.getObject("name"), "lisi");
    	
    	dao.updateObjectTimeout("name", 100);
    	Assertions.assertTrue(dao.getObjectTimeout("name") <= 100);
    	
    	dao.deleteObject("name");
    	Assertions.assertEquals(dao.getObject("name"), null);
    }

	// SaSession 存取 
    @Test
    public void getSession() {
    	SaSession session = new SaSession("session-1001");
    	
    	dao.setSession(session, 60);
    	Assertions.assertEquals(dao.getSession("session-1001").getId(), session.getId());
    	Assertions.assertTrue(dao.getSessionTimeout("session-1001") <= 60);
    	
    	SaSession session2 = new SaSession("session-1001");
    	dao.updateSession(session2);
    	Assertions.assertEquals(dao.getSession("session-1001").getId(), session2.getId());
    	
    	dao.updateSessionTimeout("session-1001", 100);
    	Assertions.assertTrue(dao.getSessionTimeout("session-1001") <= 100);
    	
    	dao.deleteSession("session-1001");
    	Assertions.assertEquals(dao.getSession("session-1001"), null);
    }

    // 测试永久有效期的写值改值 
    @Test
    public void testUpdate() {

    	// ----------- 字符串 相关 
    	
    	// 永久有效 
    	dao.set("age", "20", -1);
    	Assertions.assertEquals(dao.get("age"), "20");
    	Assertions.assertEquals(dao.getTimeout("age"), SaTokenDao.NEVER_EXPIRE);
    	
    	// 修改值 
    	dao.update("age", "22");
    	Assertions.assertEquals(dao.get("age"), "22");
    	// 有效期应该不变，还是永久 
    	Assertions.assertEquals(dao.getTimeout("age"), SaTokenDao.NEVER_EXPIRE);
    	
    	
    	// ----------- Session 相关 
    	
    	// 永久有效 
    	SaSession session = new SaSession("session-1001");
    	dao.setSession(session, -1);
    	Assertions.assertEquals(dao.getSession("session-1001").getId(), session.getId());
    	Assertions.assertEquals(dao.getSessionTimeout("session-1001"), SaTokenDao.NEVER_EXPIRE);
    	
    	// 修改值 
    	dao.updateSession(session);
    	Assertions.assertEquals(dao.getSession("session-1001").getId(), session.getId());
    	// 有效期应该不变，还是永久 
    	Assertions.assertEquals(dao.getSessionTimeout("session-1001"), SaTokenDao.NEVER_EXPIRE);
    	
    	
    	// ----------- 无效update 
    	dao.update("mid", "zhang");
    	Assertions.assertNull(dao.get("mid"));
    }

    // timeout为0或者小于-2时，不写入
    @Test
    public void test0Timeout() {
    	
    	// ----------- 字符串 相关 
    	
    	// 字符串 0 和 <-2 
    	dao.set("avatar", "1.jpg", 0);
    	Assertions.assertNull(dao.get("avatar"));
    	
    	dao.set("avatar", "1.jpg", -9);
    	Assertions.assertNull(dao.get("avatar"));

    	// ----------- Session 相关 
    	
    	// Session 0 和 <-2 
    	SaSession session = new SaSession("session-1001");
    	dao.setSession(session, 0);
    	Assertions.assertNull(dao.getSession("session-1001"));

    	dao.setSession(session, -9);
    	Assertions.assertNull(dao.getSession("session-1001"));
    }

    // TO-DO 和时间相关的测试 
    
}

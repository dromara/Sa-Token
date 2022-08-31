package cn.dev33.satoken.core.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.session.SaSession;

/**
 * SaTokenDao 持久层 测试 
 * 
 * @author kong
 * @since: 2022-2-9 15:39:38
 */
public class SaTokenDaoTest {

	SaTokenDao dao = new SaTokenDaoDefaultImpl();

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

}

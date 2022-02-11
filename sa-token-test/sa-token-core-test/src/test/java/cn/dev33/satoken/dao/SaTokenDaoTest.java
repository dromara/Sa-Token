package cn.dev33.satoken.dao;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import cn.dev33.satoken.session.SaSession;

/**
 * SaTokenDao 持久层 测试 
 * 
 * @author kong
 * @date: 2022-2-9 15:39:38
 */
@RunWith(SpringRunner.class)
public class SaTokenDaoTest {

	SaTokenDao dao = new SaTokenDaoDefaultImpl();

    @Test
    public void get() {
    	dao.set("name", "zhangsan", 60);
    	Assert.assertEquals(dao.get("name"), "zhangsan");
    	Assert.assertTrue(dao.getTimeout("name") <= 60);
    	Assert.assertEquals(dao.getTimeout("name2"), -2);
    	
    	dao.update("name", "lisi");
    	Assert.assertEquals(dao.get("name"), "lisi");
    	
    	dao.updateTimeout("name", 100);
    	Assert.assertTrue(dao.getTimeout("name") <= 100);
    	
    	dao.delete("name");
    	Assert.assertEquals(dao.get("name"), null);
    }

    @Test
    public void getObject() {
    	dao.setObject("name", "zhangsan", 60);
    	Assert.assertEquals(dao.getObject("name"), "zhangsan");
    	Assert.assertTrue(dao.getObjectTimeout("name") <= 60);
    	
    	dao.updateObject("name", "lisi");
    	Assert.assertEquals(dao.getObject("name"), "lisi");
    	
    	dao.updateObjectTimeout("name", 100);
    	Assert.assertTrue(dao.getObjectTimeout("name") <= 100);
    	
    	dao.deleteObject("name");
    	Assert.assertEquals(dao.getObject("name"), null);
    }

    @Test
    public void getSession() {
    	SaSession session = new SaSession("session-1001");
    	
    	dao.setSession(session, 60);
    	Assert.assertEquals(dao.getSession("session-1001").getId(), session.getId());
    	Assert.assertTrue(dao.getSessionTimeout("session-1001") <= 60);
    	
    	SaSession session2 = new SaSession("session-1001");
    	dao.updateSession(session2);
    	Assert.assertEquals(dao.getSession("session-1001").getId(), session2.getId());
    	
    	dao.updateSessionTimeout("session-1001", 100);
    	Assert.assertTrue(dao.getSessionTimeout("session-1001") <= 100);
    	
    	dao.deleteSession("session-1001");
    	Assert.assertEquals(dao.getSession("session-1001"), null);
    }

}

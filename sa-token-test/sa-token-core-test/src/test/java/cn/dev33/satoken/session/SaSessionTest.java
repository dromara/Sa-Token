package cn.dev33.satoken.session;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SaSession 测试 
 * 
 * @author kong
 * @date: 2022-2-9 
 */
@RunWith(SpringRunner.class)
public class SaSessionTest {

    @Test
    public void test() {
    	SaSession session = new SaSession("session-1001");
    	Assert.assertEquals(session.getId(), "session-1001");
    	
    	// 基础取值 
    	session.set("name", "zhangsan");
    	session.set("age", 18);
    	Assert.assertEquals(session.get("name"), "zhangsan");
    	Assert.assertEquals((int)session.get("age", 20), 18);
    	Assert.assertEquals((int)session.get("age2", 20), 20);
    	Assert.assertEquals(session.getModel("age", Double.class).getClass(), Double.class);
    	
    	// 复杂取值 
    	class User {
    		String name;
    		int age;
			User(String name, int age) {
				this.name = name;
				this.age = age;
			}
    	}
    	User user = new User("zhangsan", 18);
    	session.set("user", user);
    	
    	User user2 = session.getModel("user", User.class);
    	Assert.assertNotNull(user2);
    	Assert.assertEquals(user2.name, "zhangsan");
    	Assert.assertEquals(user2.age, 18);
    	
    	// Token签名 
    	session.addTokenSign("xxxx-xxxx-xxxx-xxxx-1", "PC");
    	session.addTokenSign("xxxx-xxxx-xxxx-xxxx-2", "APP");

    	Assert.assertEquals(session.getTokenSignList().size(), 2);
    	Assert.assertEquals(session.getTokenSign("xxxx-xxxx-xxxx-xxxx-1").getDevice(), "PC");
    	Assert.assertEquals(session.getTokenSign("xxxx-xxxx-xxxx-xxxx-2").getDevice(), "APP");

    	session.removeTokenSign("xxxx-xxxx-xxxx-xxxx-1");
    	Assert.assertEquals(session.getTokenSignList().size(), 1);
    }

}

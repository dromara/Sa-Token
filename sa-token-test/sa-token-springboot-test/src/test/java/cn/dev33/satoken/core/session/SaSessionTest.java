package cn.dev33.satoken.core.session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.session.SaSession;

/**
 * SaSession 测试 
 * 
 * @author kong
 * @since: 2022-2-9 
 */
public class SaSessionTest {

    @Test
    public void test() {
    	SaSession session = new SaSession("session-1001");
    	Assertions.assertEquals(session.getId(), "session-1001");
    	
    	// 基础取值 
    	session.set("name", "zhangsan");
    	session.set("age", 18);
    	Assertions.assertEquals(session.get("name"), "zhangsan");
    	Assertions.assertEquals((int)session.get("age", 20), 18);
    	Assertions.assertEquals((int)session.get("age2", 20), 20);
    	Assertions.assertEquals(session.getModel("age", Double.class).getClass(), Double.class);
    	
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
    	Assertions.assertNotNull(user2);
    	Assertions.assertEquals(user2.name, "zhangsan");
    	Assertions.assertEquals(user2.age, 18);
    	
    	// Token签名 
    	session.addTokenSign("xxxx-xxxx-xxxx-xxxx-1", "PC");
    	session.addTokenSign("xxxx-xxxx-xxxx-xxxx-2", "APP");

    	Assertions.assertEquals(session.getTokenSignList().size(), 2);
    	Assertions.assertEquals(session.getTokenSign("xxxx-xxxx-xxxx-xxxx-1").getDevice(), "PC");
    	Assertions.assertEquals(session.getTokenSign("xxxx-xxxx-xxxx-xxxx-2").getDevice(), "APP");

    	session.removeTokenSign("xxxx-xxxx-xxxx-xxxx-1");
    	Assertions.assertEquals(session.getTokenSignList().size(), 1);
    }

}

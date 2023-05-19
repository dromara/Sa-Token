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
package cn.dev33.satoken.core.session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.TokenSign;

/**
 * SaSession 测试 
 * 
 * @author click33
 * @since 2022-2-9 
 */
public class SaSessionTest {

	// 基础属性 
    @Test
    public void testProp() {
    	SaSession session = new SaSession("session-1001");
    	Assertions.assertEquals(session.getId(), "session-1001");

    	// 属性读取 
    	session = new SaSession();
    	session.setId("session-1009");
    	Assertions.assertEquals(session.getId(), "session-1009");
    	
    	session.setCreateTime(1662241013902L);
    	Assertions.assertEquals(session.getCreateTime(), 1662241013902L);
    }
	
	// 基础存取值 
    @Test
    public void testSetGet() {
    	
    	// 基础取值 
    	SaSession session = new SaSession("session-1002");
    	session.set("name", "zhangsan");
    	session.set("age", 18);
    	Assertions.assertEquals(session.get("name"), "zhangsan");
    	Assertions.assertEquals((int)session.get("age", 20), 18);
    	Assertions.assertEquals((int)session.get("age2", 20), 20);
    	Assertions.assertEquals(session.getModel("age", Double.class).getClass(), Double.class);
    	
    	// 原本无值时才会写入 
    	session.setByNull("name", "lisi");
    	Assertions.assertEquals(session.get("name"), "zhangsan");
    	session.setByNull("name2", "lisi");
    	Assertions.assertEquals(session.get("name2"), "lisi");
    	
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
    }
    
    // 测试有效期
    @Test
    public void testSessionTimeout() {
    	// 修改剩余有效期 
    	SaSession session = new SaSession("session-1005");
    	SaManager.getSaTokenDao().setSession(session, 20000);
    	session.updateMaxTimeout(100);
    	Assertions.assertTrue(session.getTimeout() <= 100);
    	System.out.println(session.getTimeout());
    	// 仍然是 <=100 
    	session.updateMaxTimeout(1000);
    	Assertions.assertTrue(session.getTimeout() <= 100);
    	System.out.println(session.getTimeout());
    	// Min 修改 
    	session.updateMinTimeout(-1);
    	System.out.println(session.getTimeout());
    	Assertions.assertTrue(session.getTimeout() == -1);
    }
    
    // 测试token 签名 
    @Test
    public void testTokenSign() {
    	SaSession session = new SaSession("session-1002");
    	
    	// 添加 Token 签名 
    	session.addTokenSign(new TokenSign("xxxx-xxxx-xxxx-xxxx-1", "PC", null));
    	session.addTokenSign(new TokenSign("xxxx-xxxx-xxxx-xxxx-2", "APP", null));

    	// 查询 
    	Assertions.assertEquals(session.getTokenSignList().size(), 2);
    	Assertions.assertEquals(session.getTokenSign("xxxx-xxxx-xxxx-xxxx-1").getDevice(), "PC");
    	Assertions.assertEquals(session.getTokenSign("xxxx-xxxx-xxxx-xxxx-2").getDevice(), "APP");

    	// 删除一个 
    	session.removeTokenSign("xxxx-xxxx-xxxx-xxxx-1");
    	Assertions.assertEquals(session.getTokenSignList().size(), 1);

    	// 删除一个不存在的，则不影响 TokenSign 列表
    	session.removeTokenSign("xxxx-xxxx-xxxx-xxxx-999");
    	Assertions.assertEquals(session.getTokenSignList().size(), 1);
    	
    	// 重置整个签名列表 
    	List<TokenSign> list = Arrays.asList(
    			new TokenSign("xxxx-xxxx-xxxx-xxxx-1", "WEB", null),
    			new TokenSign("xxxx-xxxx-xxxx-xxxx-2", "phone", null),
    			new TokenSign("xxxx-xxxx-xxxx-xxxx-3", "ipad", null)
    			);
    	session.setTokenSignList(list);
    	Assertions.assertEquals(session.getTokenSignList().size(), 3);
    	Assertions.assertEquals(session.getTokenSign("xxxx-xxxx-xxxx-xxxx-1").getDevice(), "WEB");
    	Assertions.assertEquals(session.getTokenSign("xxxx-xxxx-xxxx-xxxx-2").getDevice(), "phone");
    	Assertions.assertEquals(session.getTokenSign("xxxx-xxxx-xxxx-xxxx-3").getDevice(), "ipad");
    }
    
    // 测试重置 DataMap
    @Test
    public void testDataMap() {
    	SaSession session = new SaSession("session-1003");
    	session.set("key1", "value1");
    	session.set("key2", "value2");
    	session.set("key3", "value3");
    	
    	// 所有数据 
    	Assertions.assertEquals(session.keys().size(), 3);
    	Assertions.assertEquals(session.getDataMap().size(), 3);
    	
    	// 重置所有数据 
    	Map<String, Object> dataMap = new ConcurrentHashMap<>();
    	dataMap.put("aaa", "111");
    	dataMap.put("bbb", "222");
    	session.refreshDataMap(dataMap);
    	Assertions.assertEquals(session.keys().size(), 2);
    	
    }
    
}

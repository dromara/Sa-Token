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
package cn.dev33.satoken.core.session;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SaSession 测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaSessionTest {

	/** 构造与 setId/setCreateTime 后 id 与 createTime 应正确读写 */
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
	
	/** set/get/getModel/setByNull 应正确存取基础值与复杂对象 */
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

	/** getList/getSet/getMap 应支持直接读取、懒初始化及跨类型转换，非集合类型应抛异常 */
	@Test
	public void testGetListSetMap() {

    	SaSession session = new SaSession("session-get-collection");

    	// getList：内存对象直接读取
    	List<String> nameList = new ArrayList<>(Arrays.asList("a", "b"));
    	session.set("nameList", nameList);
    	List<String> nameList2 = session.getList("nameList", String.class);
    	Assertions.assertEquals(nameList2.size(), 2);
    	Assertions.assertEquals(nameList2.get(0), "a");

    	// getList：lazy 初始化
    	List<String> emptyList = session.getList("emptyList", String.class, ArrayList::new);
    	Assertions.assertTrue(emptyList.isEmpty());
    	Assertions.assertTrue(session.has("emptyList"));

    	// getList：Set 存储、List 读取
    	Set<String> nameSet = new HashSet<>(Arrays.asList("x", "y"));
    	session.set("nameSetAsList", nameSet);
    	List<String> nameSetAsList = session.getList("nameSetAsList", String.class);
    	Assertions.assertEquals(nameSetAsList.size(), 2);
    	Assertions.assertTrue(nameSetAsList.contains("x"));

    	// getSet：List 存储、Set 读取
    	session.set("nameListAsSet", nameList);
    	Set<String> nameListAsSet = session.getSet("nameListAsSet", String.class);
    	Assertions.assertEquals(nameListAsSet.size(), 2);
    	Assertions.assertTrue(nameListAsSet.contains("b"));

    	// getSet：lazy 初始化
    	Set<String> emptySet = session.getSet("emptySet", String.class, LinkedHashSet::new);
    	Assertions.assertTrue(emptySet.isEmpty());
    	Assertions.assertTrue(session.has("emptySet"));

    	// getMap：内存 Map 读取
    	Map<String, Long> scoreMap = new LinkedHashMap<>();
    	scoreMap.put("k1", 100L);
    	scoreMap.put("k2", 200L);
    	session.set("scoreMap", scoreMap);
    	Map<String, Long> scoreMap2 = session.getMap("scoreMap", String.class, Long.class);
    	Assertions.assertEquals(scoreMap2.size(), 2);
    	Assertions.assertEquals(scoreMap2.get("k1"), 100L);

    	// getMap：lazy 初始化
    	Map<String, Long> emptyMap = session.getMap("emptyMap", String.class, Long.class, LinkedHashMap::new);
    	Assertions.assertTrue(emptyMap.isEmpty());
    	Assertions.assertTrue(session.has("emptyMap"));

    	// 非集合类型读取时抛异常
    	session.set("notCollection", "abc");
    	Assertions.assertThrows(SaTokenException.class, () -> session.getList("notCollection", String.class));
    	Assertions.assertThrows(SaTokenException.class, () -> session.getSet("notCollection", String.class));
    	Assertions.assertThrows(SaTokenException.class, () -> session.getMap("notCollection", String.class, Long.class));
    }
    
	/** updateMaxTimeout 应缩短剩余有效期，updateMinTimeout(-1) 应设为永久 */
	@Test
	public void testSessionTimeout() {
    	// 修改剩余有效期 
    	SaSession session = new SaSession("session-1005");
    	SaManager.getSaTokenDao().setSession(session, 20000);
    	session.updateMaxTimeout(100);
    	Assertions.assertTrue(session.timeout() <= 100);
    	// 仍然是 <=100
    	session.updateMaxTimeout(1000);
    	Assertions.assertTrue(session.timeout() <= 100);
    	// Min 修改
    	session.updateMinTimeout(-1);
    	Assertions.assertEquals(-1, session.timeout());
    }
    
	/** addTerminal/removeTerminal/setTerminalList 应正确管理终端列表及按 token 查询 */
	@Test
	public void testSaTerminalInfo() {
    	SaSession session = new SaSession("session-1002");
    	
    	// 添加 Token 签名 
    	session.addTerminal(new SaTerminalInfo(1, "xxxx-xxxx-xxxx-xxxx-1", "PC", null));
    	session.addTerminal(new SaTerminalInfo(2, "xxxx-xxxx-xxxx-xxxx-2", "APP", null));

    	// 查询 
    	Assertions.assertEquals(session.getTerminalList().size(), 2);
    	Assertions.assertEquals(session.getTerminal("xxxx-xxxx-xxxx-xxxx-1").getDeviceType(), "PC");
    	Assertions.assertEquals(session.getTerminal("xxxx-xxxx-xxxx-xxxx-2").getDeviceType(), "APP");

    	// 删除一个 
    	session.removeTerminal("xxxx-xxxx-xxxx-xxxx-1");
    	Assertions.assertEquals(session.getTerminalList().size(), 1);

    	// 删除一个不存在的，则不影响 SaTerminalInfo 列表
    	session.removeTerminal("xxxx-xxxx-xxxx-xxxx-999");
    	Assertions.assertEquals(session.getTerminalList().size(), 1);
    	
    	// 重置整个签名列表 
    	List<SaTerminalInfo> list = Arrays.asList(
    			new SaTerminalInfo(1, "xxxx-xxxx-xxxx-xxxx-1", "WEB", null),
    			new SaTerminalInfo(2, "xxxx-xxxx-xxxx-xxxx-2", "phone", null),
    			new SaTerminalInfo(3, "xxxx-xxxx-xxxx-xxxx-3", "ipad", null)
    			);
    	session.setTerminalList(list);
    	Assertions.assertEquals(session.getTerminalList().size(), 3);
    	Assertions.assertEquals(session.getTerminal("xxxx-xxxx-xxxx-xxxx-1").getDeviceType(), "WEB");
    	Assertions.assertEquals(session.getTerminal("xxxx-xxxx-xxxx-xxxx-2").getDeviceType(), "phone");
    	Assertions.assertEquals(session.getTerminal("xxxx-xxxx-xxxx-xxxx-3").getDeviceType(), "ipad");
    }
    
	/** refreshDataMap 应替换全部 Session 数据并更新 keys 计数 */
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

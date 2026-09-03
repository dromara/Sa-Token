package com.pj.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.json.SaJsonTemplateForFastjson2;
import cn.dev33.satoken.session.SaSession;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.pj.test.model.SysUser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SaGetValueInterface getList / getSet / getMap 测试（模拟 JSON 持久化读回后的中间类型）
 */
public class SaGetValueInterfaceTest {

	@BeforeAll
	public static void beforeClass() {
		SaManager.setSaJsonTemplate(new SaJsonTemplateForFastjson2());
	}

	@Test
	public void testGetListWithJSONObjectElements() {
		SaSession session = new SaSession("json-list");

		JSONArray arr = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("id", 10001);
		jo.put("name", "张三");
		jo.put("age", 18);
		arr.add(jo);
		session.set("users", arr);

		List<SysUser> users = session.getList("users", SysUser.class);
		assertEquals(1, users.size());
		assertEquals(10001L, users.get(0).getId());
		assertEquals("张三", users.get(0).getName());
		assertEquals(18, users.get(0).getAge());
	}

	@Test
	public void testGetSetWithJSONObjectElements() {
		SaSession session = new SaSession("json-set");

		JSONArray arr = new JSONArray();
		JSONObject jo1 = new JSONObject();
		jo1.put("id", 10001);
		jo1.put("name", "张三");
		jo1.put("age", 18);
		arr.add(jo1);
		JSONObject jo2 = new JSONObject();
		jo2.put("id", 10002);
		jo2.put("name", "李四");
		jo2.put("age", 20);
		arr.add(jo2);
		session.set("users", arr);

		Set<SysUser> users = session.getSet("users", SysUser.class);
		assertEquals(2, users.size());
	}

	@Test
	public void testGetMapWithJSONObjectValues() {
		SaSession session = new SaSession("json-map");

		JSONObject map = new JSONObject();
		JSONObject jo = new JSONObject();
		jo.put("id", 10001);
		jo.put("name", "张三");
		jo.put("age", 18);
		map.put("user1", jo);
		session.set("userMap", map);

		Map<String, SysUser> userMap = session.getMap("userMap", String.class, SysUser.class);
		assertEquals(1, userMap.size());
		assertEquals(10001L, userMap.get("user1").getId());
		assertEquals("张三", userMap.get("user1").getName());
	}

	@Test
	public void testGetMapWithBasicTypeValues() {
		SaSession session = new SaSession("json-map-basic");

		JSONObject map = new JSONObject();
		map.put("score", 100);
		session.set("scoreMap", map);

		Map<String, Long> scoreMap = session.getMap("scoreMap", String.class, Long.class);
		assertEquals(100L, scoreMap.get("score"));
	}

	@Test
	public void testGetListSetMapLazyInit() {
		SaSession session = new SaSession("json-lazy");

		List<String> list = session.getList("listKey", String.class, ArrayList::new);
		assertTrue(list.isEmpty());
		assertTrue(session.has("listKey"));

		Set<String> set = session.getSet("setKey", String.class, LinkedHashSet::new);
		assertTrue(set.isEmpty());
		assertTrue(session.has("setKey"));

		Map<String, Long> map = session.getMap("mapKey", String.class, Long.class, LinkedHashMap::new);
		assertTrue(map.isEmpty());
		assertTrue(session.has("mapKey"));
	}

}

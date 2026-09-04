package com.pj.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.json.SaJsonTemplateForFastjson2;
import cn.dev33.satoken.session.SaSession;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.AfterAll;
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
 * Fastjson2 JSONObject/JSONArray 反序列化后的 Session 取值测试
 */
public class SaGetValueInterfaceTest {

	private static SaJsonTemplate previousJsonTemplate;

	/** 配置 Fastjson2 JSON 转换器 */
	@BeforeAll
	public static void beforeClass() {
		previousJsonTemplate = SaManager.getSaJsonTemplate();
		SaManager.setSaJsonTemplate(new SaJsonTemplateForFastjson2());
	}

	/** 恢复测试前的全局 JSON 转换器 */
	@AfterAll
	public static void afterClass() {
		SaManager.setSaJsonTemplate(previousJsonTemplate);
	}

	/** 验证 JSONObject 数组可转换为用户列表 */
	@Test
	public void testGetListWithJSONObjectElements() {
		SaSession session = new SaSession("json-list");
		JSONArray array = new JSONArray();
		array.add(userJson(10001, "张三", 18));
		session.set("users", array);

		List<User> users = session.getList("users", User.class);
		assertEquals(1, users.size());
		assertEquals(10001L, users.get(0).getId());
		assertEquals("张三", users.get(0).getName());
		assertEquals(18, users.get(0).getAge());
	}

	/** 验证 JSONObject 数组可转换为用户集合 */
	@Test
	public void testGetSetWithJSONObjectElements() {
		SaSession session = new SaSession("json-set");
		JSONArray array = new JSONArray();
		array.add(userJson(10001, "张三", 18));
		array.add(userJson(10002, "李四", 20));
		session.set("users", array);

		Set<User> users = session.getSet("users", User.class);
		assertEquals(2, users.size());
	}

	/** 验证 JSONObject 可转换为用户 Map */
	@Test
	public void testGetMapWithJSONObjectValues() {
		SaSession session = new SaSession("json-map");
		JSONObject map = new JSONObject();
		map.put("user1", userJson(10001, "张三", 18));
		session.set("userMap", map);

		Map<String, User> userMap = session.getMap("userMap", String.class, User.class);
		assertEquals(1, userMap.size());
		assertEquals(10001L, userMap.get("user1").getId());
		assertEquals("张三", userMap.get("user1").getName());
	}

	/** 验证 JSONObject 可转换为基础类型 Map */
	@Test
	public void testGetMapWithBasicTypeValues() {
		SaSession session = new SaSession("json-map-basic");
		JSONObject map = new JSONObject();
		map.put("score", 100);
		session.set("scoreMap", map);

		Map<String, Long> scoreMap = session.getMap("scoreMap", String.class, Long.class);
		assertEquals(100L, scoreMap.get("score"));
	}

	/** 验证集合访问可按需初始化容器 */
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

	private JSONObject userJson(long id, String name, int age) {
		JSONObject user = new JSONObject();
		user.put("id", id);
		user.put("name", name);
		user.put("age", age);
		return user;
	}

	/** Fastjson2 JSONObject 转换用的最小测试模型 */
	public static class User {
		private long id;
		private String name;
		private int age;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}

}

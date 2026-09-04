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
package cn.dev33.satoken.core.util;

import cn.dev33.satoken.util.SaFoxUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SaFoxUtil 补充测试（覆盖 SaFoxUtilTest 未触及的方法）
 */
public class SaFoxUtilExtendedTest {

	/** getRandomNumber 返回值应在指定闭区间内 */
	@Test
	void getRandomNumber() {
		int value = SaFoxUtil.getRandomNumber(1, 3);
		Assertions.assertTrue(value >= 1 && value <= 3);
	}

	/** isEmptyArray / isEmptyList 及已废弃的 isEmpty 应正确判断空集合 */
	@Test
	void isEmptyArrayAndList() {
		Assertions.assertTrue(SaFoxUtil.isEmptyArray(null));
		Assertions.assertTrue(SaFoxUtil.isEmptyArray(new String[0]));
		Assertions.assertFalse(SaFoxUtil.isEmptyArray(new String[] {"a"}));
		@SuppressWarnings("deprecation")
		boolean deprecatedEmpty = SaFoxUtil.isEmpty(new String[0]);
		Assertions.assertTrue(deprecatedEmpty);
		Assertions.assertTrue(SaFoxUtil.isEmptyList(null));
		Assertions.assertTrue(SaFoxUtil.isEmptyList(SaFoxUtil.emptyList()));
		Assertions.assertFalse(SaFoxUtil.isEmptyList(Arrays.asList("a")));
	}

	/** notEquals 应在相等、不等和 null 场景返回正确结果 */
	@Test
	void notEquals() {
		Assertions.assertFalse(SaFoxUtil.notEquals("a", "a"));
		Assertions.assertTrue(SaFoxUtil.notEquals("a", "b"));
		Assertions.assertTrue(SaFoxUtil.notEquals(null, "a"));
	}

	/** formatDate 应把 Date 格式化为 yyyy-MM-dd 开头的字符串 */
	@Test
	void formatDateWithDate() {
		Date date = new Date(1644328600364L);
		String formatted = SaFoxUtil.formatDate(date);
		Assertions.assertTrue(formatted.startsWith("2022-02-08"));
	}

	/** formatAfterDate 应返回标准日期时间格式字符串 */
	@Test
	void formatAfterDate() {
		String formatted = SaFoxUtil.formatAfterDate(60_000L);
		Assertions.assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
	}

	/** mapToObject 应把 Map 转为 Bean；null 入参返回 null；Map 类型入参原样返回 */
	@Test
	void mapToObject() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", "zhangsan");
		map.put("age", 18);
		SimpleBean bean = SaFoxUtil.mapToObject(map, SimpleBean.class);
		Assertions.assertEquals("zhangsan", bean.name);
		Assertions.assertEquals(18, bean.age);
		Assertions.assertNull(SaFoxUtil.mapToObject(null, SimpleBean.class));
		@SuppressWarnings("unchecked")
		Map<String, Object> sameMap = SaFoxUtil.mapToObject(map, Map.class);
		Assertions.assertSame(map, sameMap);
	}

	/** toArray 应把 List 转为数组 */
	@Test
	void toArray() {
		List<String> list = SaFoxUtil.toList("a", "b");
		String[] array = SaFoxUtil.toArray(list);
		Assertions.assertArrayEquals(new String[] {"a", "b"}, array);
	}

	/** translateLogLevel 系列方法应正确映射日志级别字符串与整数 */
	@Test
	void translateLogLevel() {
		Assertions.assertEquals(3, SaFoxUtil.translateLogLevelToInt("info"));
		Assertions.assertEquals(1, SaFoxUtil.translateLogLevelToInt("unknown"));
		Assertions.assertEquals("debug", SaFoxUtil.translateLogLevelToString(2));
		Assertions.assertEquals("trace", SaFoxUtil.translateLogLevelToString(-1));
	}

	/** isCanColorLog 应返回非 null 布尔值 */
	@Test
	void isCanColorLog() {
		Assertions.assertNotNull(SaFoxUtil.isCanColorLog());
	}

	/** 列表包含、交集判断及按另一列表移除元素的工具方法 */
	@Test
	void listContainAndRemove() {
		List<String> list1 = Arrays.asList("a", "b", "c");
		List<String> list2 = Arrays.asList("b", "c");
		Assertions.assertTrue(SaFoxUtil.list1ContainList2AllElement(list1, list2));
		Assertions.assertFalse(SaFoxUtil.list1ContainList2AllElement(list1, Arrays.asList("d")));
		Assertions.assertTrue(SaFoxUtil.list1ContainList2AllElement(list1, null));
		Assertions.assertTrue(SaFoxUtil.list1ContainList2AnyElement(list1, Arrays.asList("x", "b")));
		Assertions.assertFalse(SaFoxUtil.list1ContainList2AnyElement(list1, Arrays.asList("x", "y")));
		List<String> removed = SaFoxUtil.list1RemoveByList2(list1, Arrays.asList("b"));
		Assertions.assertEquals(Arrays.asList("a", "c"), removed);
		Assertions.assertEquals(list1, SaFoxUtil.list1RemoveByList2(list1, null));
		Assertions.assertNull(SaFoxUtil.list1RemoveByList2(null, list2));
	}

	/** valueToString 应把 null 转为空串，其他值转为字符串 */
	@Test
	void valueToString() {
		Assertions.assertEquals("", SaFoxUtil.valueToString(null));
		Assertions.assertEquals("123", SaFoxUtil.valueToString(123));
	}

	/** isUrl 应识别 ftp/file 等协议，并拒绝非法或未知协议 */
	@Test
	void isUrlExtended() {
		Assertions.assertTrue(SaFoxUtil.isUrl("ftp://example.com/resource"));
		Assertions.assertTrue(SaFoxUtil.isUrl("file:///tmp/test.txt"));
		Assertions.assertFalse(SaFoxUtil.isUrl("http://"));
		Assertions.assertFalse(SaFoxUtil.isUrl("custom://example.com"));
		Assertions.assertFalse(SaFoxUtil.isUrl(null));
	}

	/** printSaToken 应能正常执行不抛异常 */
	@Test
	void printSaToken() {
		Assertions.assertDoesNotThrow(SaFoxUtil::printSaToken);
	}

	public static class SimpleBean {
		public String name;
		public int age;
	}

}

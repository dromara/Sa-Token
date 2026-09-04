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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SaFoxUtil 剩余覆盖率补充测试
 */
public class SaFoxUtilMoreTest {

	/** searchList 前缀筛选应只返回匹配前缀的元素 */
	@Test
	void searchList_withPrefixFilter() {
		List<String> data = Arrays.asList(
				"satoken:login:token:aaa",
				"satoken:login:token:bbb",
				"satoken:login:session:ccc");
		List<String> filtered = SaFoxUtil.searchList(data, "satoken:login:token:", "", 0, 10, true);
		Assertions.assertEquals(2, filtered.size());
		filtered.forEach(item -> Assertions.assertTrue(item.startsWith("satoken:login:token:")));
	}

	/** searchList 在 start 为负数时应视为 0 */
	@Test
	void searchList_negativeStart_treatedAsZero() {
		List<String> data = Arrays.asList("a", "b", "c");
		List<String> result = SaFoxUtil.searchList(data,  -1, 2, true);
		Assertions.assertEquals(2, result.size());
		Assertions.assertEquals("a", result.get(0));
	}

	/** joinParam 在 query 以 & 结尾时应正确拼接参数 */
	@Test
	void joinParam_whenQueryEndsWithAmpersand() {
		Assertions.assertEquals("https://sa-token.com?name=zhang&id=1",
				SaFoxUtil.joinParam("https://sa-token.com?name=zhang&", "id=1"));
		Assertions.assertEquals("https://sa-token.com?name=zhang&&id=1",
				SaFoxUtil.joinParam("https://sa-token.com?name=zhang&", "&id=1"));
	}

	/** joinSharpParam 在 hash 以 & 结尾时应正确拼接参数 */
	@Test
	void joinSharpParam_whenHashEndsWithAmpersand() {
		Assertions.assertEquals("https://sa-token.com#name=zhang&id=1",
				SaFoxUtil.joinSharpParam("https://sa-token.com#name=zhang&", "id=1"));
		Assertions.assertEquals("https://sa-token.com#name=zhang&&id=1",
				SaFoxUtil.joinSharpParam("https://sa-token.com#name=zhang&", "&id=1"));
	}

	/** vagueMatch 应支持前导通配符 * 匹配 */
	@Test
	void vagueMatch_leadingWildcard() {
		Assertions.assertTrue(SaFoxUtil.vagueMatch("*login", "user/login"));
		Assertions.assertTrue(SaFoxUtil.vagueMatch("user/*", "user/login/extra"));
		Assertions.assertFalse(SaFoxUtil.vagueMatch("*login", "user/logout"));
	}

	/** getValueByType 应正确转换 char/Character 类型 */
	@Test
	void getValueByType_characterConversion() {
		Assertions.assertEquals('x', SaFoxUtil.getValueByType("xyz", char.class));
		Assertions.assertEquals('x', SaFoxUtil.getValueByType("xyz", Character.class));
	}

	/** getValueByType 对未知类型应原样返回对象 */
	@Test
	void getValueByType_unknownType_returnsOriginal() {
		Object source = new Object();
		Assertions.assertSame(source, SaFoxUtil.getValueByType(source, Object.class));
	}

	/** mapToObject 在目标类无法实例化时应抛出 RuntimeException */
	@Test
	void mapToObject_invalidClass_throwsRuntimeException() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", "zhangsan");
		Assertions.assertThrows(RuntimeException.class,
				() -> SaFoxUtil.mapToObject(map, Runnable.class));
	}

	/** list1ContainList2AllElement 在 list1 为空时应返回 false */
	@Test
	void list1ContainList2AllElement_whenList1Empty() {
		Assertions.assertFalse(SaFoxUtil.list1ContainList2AllElement(Arrays.asList(), Arrays.asList("a")));
	}

	/** list1ContainList2AnyElement 在 list1 为空时应返回 false */
	@Test
	void list1ContainList2AnyElement_whenList1Empty() {
		Assertions.assertFalse(SaFoxUtil.list1ContainList2AnyElement(Arrays.asList(), Arrays.asList("a")));
	}

	/** list1RemoveByList2 在 list2 为空时应原样返回 list1 */
	@Test
	void list1RemoveByList2_whenList2Empty() {
		List<String> list = Arrays.asList("a", "b");
		Assertions.assertEquals(list, SaFoxUtil.list1RemoveByList2(list, Arrays.asList()));
	}

	/** searchList 关键字筛选应只返回包含关键字的元素 */
	@Test
	void searchList_withKeywordFilter() {
		List<String> data = Arrays.asList(
				"satoken:login:token:aaa",
				"satoken:login:token:bbb",
				"satoken:login:session:ccc");
		List<String> filtered = SaFoxUtil.searchList(data, "satoken:login:token:", "bbb", 0, 10, true);
		Assertions.assertEquals(1, filtered.size());
		Assertions.assertEquals("satoken:login:token:bbb", filtered.get(0));
	}

	/** getRandomNumber 返回值应在指定闭区间内 */
	@Test
	void getRandomNumber_inRange() {
		int value = SaFoxUtil.getRandomNumber(5, 10);
		Assertions.assertTrue(value >= 5 && value <= 10);
	}

	/** isWrapperType 与 isBasicType 应正确区分包装类型与基本类型 */
	@Test
	void isWrapperType_and_isBasicType() {
		Assertions.assertTrue(SaFoxUtil.isWrapperType(Integer.class));
		Assertions.assertFalse(SaFoxUtil.isWrapperType(String.class));
		Assertions.assertTrue(SaFoxUtil.isBasicType(String.class));
		Assertions.assertTrue(SaFoxUtil.isBasicType(int.class));
	}

	/** convertListToString 与 toList 应正确互转 */
	@Test
	void convertListToString_and_toList() {
		Assertions.assertEquals("a,b,c", SaFoxUtil.convertListToString(Arrays.asList("a", "b", "c")));
		Assertions.assertEquals(Arrays.asList("x", "y"), SaFoxUtil.toList("x", "y"));
	}

	/** equals 与 isEmpty 系列辅助方法应返回预期结果 */
	@Test
	void equalsAndIsEmptyHelpers() {
		Assertions.assertTrue(SaFoxUtil.equals(null, null));
		Assertions.assertTrue(SaFoxUtil.isEmpty(new String[] {}));
		Assertions.assertFalse(SaFoxUtil.isNotEmpty(""));
	}

	/** isUrl 应覆盖 ftp/file 及各类非法 URL 边界场景 */
	@Test
	void isUrl_edgeCases() {
		Assertions.assertTrue(SaFoxUtil.isUrl("https://sa-token.com"));
		Assertions.assertTrue(SaFoxUtil.isUrl("file:///tmp/test.txt"));
		Assertions.assertFalse(SaFoxUtil.isUrl("https://"));
		Assertions.assertFalse(SaFoxUtil.isUrl("https://2001:db8::1/path"));
		Assertions.assertFalse(SaFoxUtil.isUrl("https://sa-token.com,"));
		Assertions.assertFalse(SaFoxUtil.isUrl("custom://host"));
	}

	/** encodeUrl 与 decoderUrl 应可往返还原字符串 */
	@Test
	void encodeUrl_and_decoderUrl_roundTrip() {
		String encoded = SaFoxUtil.encodeUrl("hello world");
		Assertions.assertEquals("hello world", SaFoxUtil.decoderUrl(encoded));
	}

	/** hasNonPrintableASCII 在 null 入参时应返回 false */
	@Test
	void hasNonPrintableASCII_null_returnsFalse() {
		Assertions.assertFalse(SaFoxUtil.hasNonPrintableASCII(null));
		Assertions.assertTrue(SaFoxUtil.hasNonPrintableASCII("a\u0007b"));
	}

	/** isCanColorLog 应返回非 null 布尔值 */
	@Test
	void isCanColorLog_returnsBoolean() {
		Assertions.assertNotNull(SaFoxUtil.isCanColorLog());
	}

	/** isUrl 应识别 ftp 协议并拒绝缺少主机或非法格式 */
	@Test
	void isUrl_ftpAndInvalidHosts() {
		Assertions.assertTrue(SaFoxUtil.isUrl("ftp://files.example.com/resource"));
		Assertions.assertFalse(SaFoxUtil.isUrl("ftp://"));
		Assertions.assertFalse(SaFoxUtil.isUrl("not-a-url"));
	}
}

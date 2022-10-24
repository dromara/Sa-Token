package cn.dev33.satoken.core.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.util.SaFoxUtil;

/**
 * SaFoxUtil 工具类测试 
 * 
 * @author kong
 * @since: 2022-2-8 22:14:25
 */
public class SaFoxUtilTest {

    @Test
    public void getRandomString() {
    	String randomString = SaFoxUtil.getRandomString(8);
    	Assertions.assertEquals(randomString.length(), 8);
    }

    @Test
    public void isEmpty() {
    	Assertions.assertTrue(SaFoxUtil.isEmpty(""));
    	Assertions.assertTrue(SaFoxUtil.isEmpty(null));
    	Assertions.assertFalse(SaFoxUtil.isEmpty("abc"));
    	
    	Assertions.assertTrue(SaFoxUtil.isNotEmpty("abc"));
    	Assertions.assertFalse(SaFoxUtil.isNotEmpty(null));
    	Assertions.assertFalse(SaFoxUtil.isNotEmpty(""));
    }

    @Test
    public void equals() {
    	Assertions.assertTrue(SaFoxUtil.equals(null, null));
    	Assertions.assertTrue(SaFoxUtil.equals("a", "a"));
    	Assertions.assertFalse(SaFoxUtil.equals("1", 1));
    	Assertions.assertFalse(SaFoxUtil.equals("1", null));
    	Assertions.assertFalse(SaFoxUtil.equals(null, "1"));
    }

    @Test
    public void getMarking28() {
    	Assertions.assertNotEquals(SaFoxUtil.getMarking28(), SaFoxUtil.getMarking28());
    }

    @Test
    public void formatDate() {
	Instant instant = Instant.ofEpochMilli(1644328600364L);
	ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("Asia/Shanghai"));
	String formatDate = SaFoxUtil.formatDate(zonedDateTime);
	Assertions.assertEquals(formatDate, "2022-02-08 21:56:40");
    }

    @Test
    public void searchList() {
    	// 原始数据 
    	List<String> dataList = Arrays.asList("token1", "token2", "token3", "token4", "token5", "aaa1");
    	
    	// 分页 
    	List<String> list1 = SaFoxUtil.searchList(dataList, 1, 2, true);
    	Assertions.assertEquals(list1.size(), 2);
    	Assertions.assertEquals(list1.get(0), "token2");
    	Assertions.assertEquals(list1.get(1), "token3");
    	
    	// 前缀筛选 
    	List<String> list2 = SaFoxUtil.searchList(dataList, "token", "", 0, 10, true);
    	Assertions.assertEquals(list2.size(), 5);

    	// 关键字筛选 
    	List<String> list3 = SaFoxUtil.searchList(dataList, "", "1", 0, 10, true);
    	Assertions.assertEquals(list3.size(), 2);

    	// 综合筛选 
    	List<String> list4 = SaFoxUtil.searchList(dataList, "token", "1", 0, 10, true);
    	Assertions.assertEquals(list4.size(), 1);

    	// 关键字为null时，效果和 "" 等同 
    	List<String> list4_2 = SaFoxUtil.searchList(dataList, null, null, 0, 10, true);
    	List<String> list4_3 = SaFoxUtil.searchList(dataList, "", "", 0, 10, true);
    	Assertions.assertEquals(list4_2.get(0), list4_3.get(0));
    	
    	
    	// 不做分页  
    	List<String> list5 = SaFoxUtil.searchList(dataList, "", "", 0, -1, true);
    	Assertions.assertEquals(list5.size(), dataList.size());
    	
    	// 反序排列 list6的第一个元素 == dataList最后一个元素 
    	List<String> list6 = SaFoxUtil.searchList(dataList, "", "", 0, -1, false);
    	Assertions.assertEquals(list6.get(0), dataList.get(dataList.size() - 1));
    }

    @Test
    public void vagueMatch() {
    	Assertions.assertTrue(SaFoxUtil.vagueMatch("hello*", "hello"));
    	Assertions.assertTrue(SaFoxUtil.vagueMatch("hello*", "hello world"));
    	Assertions.assertFalse(SaFoxUtil.vagueMatch("hello*", "he"));
    	Assertions.assertTrue(SaFoxUtil.vagueMatch("hello*", "hello*"));
    	Assertions.assertTrue(SaFoxUtil.vagueMatch(null, null));
    	Assertions.assertFalse(SaFoxUtil.vagueMatch(null, "hello"));
    	Assertions.assertFalse(SaFoxUtil.vagueMatch("hello*", null));
    }

    @Test
    public void isWrapperType() {
    	Assertions.assertTrue(SaFoxUtil.isWrapperType(Integer.class));
    	Assertions.assertTrue(SaFoxUtil.isWrapperType(Short.class));
    	Assertions.assertTrue(SaFoxUtil.isWrapperType(Long.class));
    	Assertions.assertTrue(SaFoxUtil.isWrapperType(Byte.class));
    	Assertions.assertTrue(SaFoxUtil.isWrapperType(Float.class));
    	Assertions.assertTrue(SaFoxUtil.isWrapperType(Double.class));
    	Assertions.assertTrue(SaFoxUtil.isWrapperType(Boolean.class));
    	Assertions.assertTrue(SaFoxUtil.isWrapperType(Character.class));
    	
    	Assertions.assertFalse(SaFoxUtil.isWrapperType(int.class));
    	Assertions.assertFalse(SaFoxUtil.isWrapperType(long.class));
    	Assertions.assertFalse(SaFoxUtil.isWrapperType(Object.class));
	}

    @Test
    public void isBasicType() {
    	Assertions.assertTrue(SaFoxUtil.isBasicType(int.class));
    	Assertions.assertTrue(SaFoxUtil.isBasicType(Integer.class));
    	Assertions.assertTrue(SaFoxUtil.isBasicType(long.class));
    	Assertions.assertTrue(SaFoxUtil.isBasicType(Long.class));
    	Assertions.assertTrue(SaFoxUtil.isBasicType(String.class));
    	
    	Assertions.assertFalse(SaFoxUtil.isBasicType(List.class));
    	Assertions.assertFalse(SaFoxUtil.isBasicType(Map.class));
	}
	
    @Test
    public void getValueByType() {
    	// 基础类型，转换 
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", int.class), 1);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", long.class), 1L);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", Long.class), 1L);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", String.class), "1");
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", short.class), (short)1);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", Short.class), (short)1);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", byte.class), (byte)1);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", Byte.class), (byte)1);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", float.class), 1f);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", Float.class), 1f);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", double.class), 1.0);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", Double.class), 1.0);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", boolean.class), false);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", Boolean.class), false);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", char.class), '1');
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", Character.class), '1');
    	Assertions.assertEquals(SaFoxUtil.getValueByType(1, String.class), "1");

    	// 复杂类型，还原 
    	Object obj = new ArrayList<>();
    	Assertions.assertEquals(SaFoxUtil.getValueByType(obj, List.class).getClass(), ArrayList.class);
    }

    @Test
    public void joinParam() {
    	// 参数为空时，返回原url
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.cc", null), "https://sa-token.cc");
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.cc", ""), "https://sa-token.cc");
    	// url为空时，视为空字符串 
    	Assertions.assertEquals(SaFoxUtil.joinParam(null, "id=1"), "?id=1");
    	Assertions.assertEquals(SaFoxUtil.joinParam("", "id=1"), "?id=1");
    	
    	// 各种情况的测试 
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.cc", "id=1"), "https://sa-token.cc?id=1");
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.cc?", "id=1"), "https://sa-token.cc?id=1");
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.cc?name=zhang", "id=1"), "https://sa-token.cc?name=zhang&id=1");
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.cc?name=zhang&", "id=1"), "https://sa-token.cc?name=zhang&id=1");
    	
    	// 重载方法测试 
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.cc?name=zhang&", "id", 1), "https://sa-token.cc?name=zhang&id=1");
    	// url或key为null时，不拼接 
    	Assertions.assertEquals(SaFoxUtil.joinParam(null, "id", 1), null);
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.cc", null, 1), "https://sa-token.cc");
    	// value为null时，会拼接出一个null字符串 
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.cc", "id", null), "https://sa-token.cc?id=null");
    }

    @Test
    public void joinSharpParam() {
    	// 参数为空时，返回原url
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.cc", null), "https://sa-token.cc");
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.cc", ""), "https://sa-token.cc");
    	// url为空时，视为空字符串 
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam(null, "id=1"), "#id=1");
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("", "id=1"), "#id=1");
    	
    	// 各种情况的测试 
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.cc", "id=1"), "https://sa-token.cc#id=1");
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.cc#", "id=1"), "https://sa-token.cc#id=1");
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.cc#name=zhang", "id=1"), "https://sa-token.cc#name=zhang&id=1");
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.cc#name=zhang&", "id=1"), "https://sa-token.cc#name=zhang&id=1");

    	// 重载方法测试 
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.cc#name=zhang&", "id", 1), "https://sa-token.cc#name=zhang&id=1");
    	// url或key为null时，不拼接 
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam(null, "id", 1), null);
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.cc", null, 1), "https://sa-token.cc");
    	// value为null时，会拼接出一个null字符串 
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.cc", "id", null), "https://sa-token.cc#id=null");
    }

    @Test
    public void spliceTwoUrl() {
    	// 其中一个为null时，直接返回另一个
    	Assertions.assertEquals(SaFoxUtil.spliceTwoUrl("https://sa-sso-server.com/sso/auth", null), "https://sa-sso-server.com/sso/auth");
    	Assertions.assertEquals(SaFoxUtil.spliceTwoUrl(null, "https://sa-sso-server.com/sso/auth"), "https://sa-sso-server.com/sso/auth");
    	
    	// 正常情况，拼接
    	Assertions.assertEquals(SaFoxUtil.spliceTwoUrl("https://sa-sso-server.com", "/sso/auth"), "https://sa-sso-server.com/sso/auth");
    	
    	// url2以http开头时，直接返回url2 
    	Assertions.assertEquals(SaFoxUtil.spliceTwoUrl("https://sa-sso-server2.com", "https://sa-sso-server.com/sso/auth2"), "https://sa-sso-server.com/sso/auth2");
    }
    
    @Test
    public void arrayJoin() {
    	Assertions.assertEquals(SaFoxUtil.arrayJoin(new String[] {"a", "b", "c"}), "a,b,c");
    	Assertions.assertEquals(SaFoxUtil.arrayJoin(new String[] {}), "");
    	Assertions.assertEquals(SaFoxUtil.arrayJoin(null), "");
    }

    @Test
    public void isUrl() {
    	Assertions.assertTrue(SaFoxUtil.isUrl("https://sa-token.cc"));
    	Assertions.assertTrue(SaFoxUtil.isUrl("https://www.baidu.com/"));

    	Assertions.assertFalse(SaFoxUtil.isUrl(null));
    	Assertions.assertFalse(SaFoxUtil.isUrl(""));
    	Assertions.assertFalse(SaFoxUtil.isUrl("htt://www.baidu.com/"));
    	Assertions.assertFalse(SaFoxUtil.isUrl("https:www.baidu.com/"));
    	Assertions.assertFalse(SaFoxUtil.isUrl("httpswwwbaiducom/"));
    	Assertions.assertFalse(SaFoxUtil.isUrl("https://www.baidu.com/,"));
    }

    @Test
    public void encodeUrl() {
    	Assertions.assertEquals(SaFoxUtil.encodeUrl("https://sa-token.cc"), "https%3A%2F%2Fsa-token.cc");
    	Assertions.assertEquals(SaFoxUtil.decoderUrl("https%3A%2F%2Fsa-token.cc"), "https://sa-token.cc");
    }
    
    @Test
    public void convertStringToList() {
    	List<String> list = SaFoxUtil.convertStringToList("a,b,,c");
    	Assertions.assertEquals(list.size(), 3);
    	Assertions.assertEquals(list.get(0), "a");
    	Assertions.assertEquals(list.get(1), "b");
    	Assertions.assertEquals(list.get(2), "c");

    	List<String> list2 = SaFoxUtil.convertStringToList("a,");
    	Assertions.assertEquals(list2.size(), 1);

    	List<String> list3 = SaFoxUtil.convertStringToList(",");
    	Assertions.assertEquals(list3.size(), 0);

    	List<String> list4 = SaFoxUtil.convertStringToList("");
    	Assertions.assertEquals(list4.size(), 0);

    	List<String> list5 = SaFoxUtil.convertStringToList(null);
    	Assertions.assertEquals(list5.size(), 0);
    }

    @Test
    public void convertListToString() {
    	// 正常
    	List<String> list = Arrays.asList("a", "b", "c");
    	Assertions.assertEquals(SaFoxUtil.convertListToString(list), "a,b,c");

    	// 空数组 
    	List<String> list2 = Arrays.asList();
    	Assertions.assertEquals(SaFoxUtil.convertListToString(list2), "");
    	
    	// 空 
    	List<String> list3 = null;
    	Assertions.assertEquals(SaFoxUtil.convertListToString(list3), "");
    }

    @Test
    public void convertStringToArray() {
    	String[] array = SaFoxUtil.convertStringToArray("a,b,c");
    	Assertions.assertEquals(array.length, 3);
    	Assertions.assertEquals(array[0], "a");
    	Assertions.assertEquals(array[1], "b");
    	Assertions.assertEquals(array[2], "c");

    	String[] array2 = SaFoxUtil.convertStringToArray("a,");
    	Assertions.assertEquals(array2.length, 1);

    	String[] array3 = SaFoxUtil.convertStringToArray(",");
    	Assertions.assertEquals(array3.length, 0);

    	String[] array4 = SaFoxUtil.convertStringToArray("");
    	Assertions.assertEquals(array4.length, 0);

    	String[] array5 = SaFoxUtil.convertStringToArray(null);
    	Assertions.assertEquals(array5.length, 0);
    }

    @Test
    public void convertArrayToString() {
    	// 正常 
    	String[] array = new String[] {"a", "b", "c"};
    	Assertions.assertEquals(SaFoxUtil.convertArrayToString(array), "a,b,c");

    	// null
    	String[] array2 = null;
    	Assertions.assertEquals(SaFoxUtil.convertArrayToString(array2), "");
    	
    	// 空数组 
    	String[] array3 = new String[] {};
    	Assertions.assertEquals(SaFoxUtil.convertArrayToString(array3), "");
    }

    @Test
    public void emptyList() {
    	List<String> list = SaFoxUtil.emptyList();
    	Assertions.assertEquals(list.size(), 0);
    }

    @Test
    public void toList() {
    	List<String> list = SaFoxUtil.toList("a","b", "c");
    	Assertions.assertEquals(list.size(), 3);
    	Assertions.assertEquals(list.get(0), "a");
    	Assertions.assertEquals(list.get(1), "b");
    	Assertions.assertEquals(list.get(2), "c");
    }

}

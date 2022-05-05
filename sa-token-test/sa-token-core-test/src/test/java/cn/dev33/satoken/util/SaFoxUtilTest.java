package cn.dev33.satoken.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    	Assertions.assertFalse(SaFoxUtil.isEmpty("abc"));
    	Assertions.assertTrue(SaFoxUtil.isEmpty(""));
    	Assertions.assertTrue(SaFoxUtil.isEmpty(null));
    	
    	Assertions.assertTrue(SaFoxUtil.isNotEmpty("abc"));
    }

    @Test
    public void getMarking28() {
    	Assertions.assertNotEquals(SaFoxUtil.getMarking28(), SaFoxUtil.getMarking28());
    }

    @Test
    public void formatDate() {
    	String formatDate = SaFoxUtil.formatDate(new Date(1644328600364L));
    	Assertions.assertEquals(formatDate, "2022-02-08 21:56:40");
    }

    @Test
    public void searchList() {
    	// 原始数据 
    	List<String> dataList = Arrays.asList("token1", "token2", "token3", "token4", "token5", "aaa1");
    	
    	// 分页 
    	List<String> list1 = SaFoxUtil.searchList(dataList, 1, 2);
    	Assertions.assertEquals(list1.size(), 2);
    	Assertions.assertEquals(list1.get(0), "token2");
    	Assertions.assertEquals(list1.get(1), "token3");
    	
    	// 前缀筛选 
    	List<String> list2 = SaFoxUtil.searchList(dataList, "token", "", 0, 10);
    	Assertions.assertEquals(list2.size(), 5);

    	// 关键字筛选 
    	List<String> list3 = SaFoxUtil.searchList(dataList, "", "1", 0, 10);
    	Assertions.assertEquals(list3.size(), 2);

    	// 综合筛选 
    	List<String> list4 = SaFoxUtil.searchList(dataList, "token", "1", 0, 10);
    	Assertions.assertEquals(list4.size(), 1);
    }

    @Test
    public void vagueMatch() {
    	Assertions.assertTrue(SaFoxUtil.vagueMatch("hello*", "hello"));
    	Assertions.assertTrue(SaFoxUtil.vagueMatch("hello*", "hello world"));
    	Assertions.assertFalse(SaFoxUtil.vagueMatch("hello*", "he"));
    	Assertions.assertTrue(SaFoxUtil.vagueMatch("hello*", "hello*"));
    }

    @Test
    public void getValueByType() {
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", Integer.class).getClass(), Integer.class);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", Long.class).getClass(), Long.class);
    	Assertions.assertEquals(SaFoxUtil.getValueByType("1", Double.class).getClass(), Double.class);
    }

    @Test
    public void joinParam() {
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.dev33.cn", "id=1"), "https://sa-token.dev33.cn?id=1");
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.dev33.cn?", "id=1"), "https://sa-token.dev33.cn?id=1");
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.dev33.cn?name=zhang", "id=1"), "https://sa-token.dev33.cn?name=zhang&id=1");
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.dev33.cn?name=zhang&", "id=1"), "https://sa-token.dev33.cn?name=zhang&id=1");
    	
    	Assertions.assertEquals(SaFoxUtil.joinParam("https://sa-token.dev33.cn?name=zhang&", "id", 1), "https://sa-token.dev33.cn?name=zhang&id=1");
    }

    @Test
    public void joinSharpParam() {
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.dev33.cn", "id=1"), "https://sa-token.dev33.cn#id=1");
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.dev33.cn#", "id=1"), "https://sa-token.dev33.cn#id=1");
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.dev33.cn#name=zhang", "id=1"), "https://sa-token.dev33.cn#name=zhang&id=1");
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.dev33.cn#name=zhang&", "id=1"), "https://sa-token.dev33.cn#name=zhang&id=1");
    	
    	Assertions.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.dev33.cn#name=zhang&", "id", 1), "https://sa-token.dev33.cn#name=zhang&id=1");
    }

    @Test
    public void arrayJoin() {
    	Assertions.assertEquals(SaFoxUtil.arrayJoin(new String[] {"a", "b", "c"}), "a,b,c");
    	Assertions.assertEquals(SaFoxUtil.arrayJoin(new String[] {}), "");
    }

    @Test
    public void isUrl() {
    	Assertions.assertTrue(SaFoxUtil.isUrl("https://sa-token.dev33.cn"));
    	Assertions.assertTrue(SaFoxUtil.isUrl("https://www.baidu.com/"));
    	
    	Assertions.assertFalse(SaFoxUtil.isUrl("htt://www.baidu.com/"));
    	Assertions.assertFalse(SaFoxUtil.isUrl("https:www.baidu.com/"));
    	Assertions.assertFalse(SaFoxUtil.isUrl("httpswwwbaiducom/"));
    	Assertions.assertFalse(SaFoxUtil.isUrl("https://www.baidu.com/,"));
    }

    @Test
    public void encodeUrl() {
    	Assertions.assertEquals(SaFoxUtil.encodeUrl("https://sa-token.dev33.cn"), "https%3A%2F%2Fsa-token.dev33.cn");
    	Assertions.assertEquals(SaFoxUtil.decoderUrl("https%3A%2F%2Fsa-token.dev33.cn"), "https://sa-token.dev33.cn");
    }

    @Test
    public void convertStringToList() {
    	List<String> list = SaFoxUtil.convertStringToList("a,b,c");
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
    	List<String> list = Arrays.asList("a", "b", "c");
    	Assertions.assertEquals(SaFoxUtil.convertListToString(list), "a,b,c");

    	List<String> list2 = Arrays.asList();
    	Assertions.assertEquals(SaFoxUtil.convertListToString(list2), "");
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
    	String[] array = new String[] {"a", "b", "c"};
    	Assertions.assertEquals(SaFoxUtil.convertArrayToString(array), "a,b,c");

    	String[] array2 = new String[] {};
    	Assertions.assertEquals(SaFoxUtil.convertArrayToString(array2), "");
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

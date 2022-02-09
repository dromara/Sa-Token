package cn.dev33.satoken.util;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * SaFoxUtil 工具类测试 
 * 
 * @author kong
 * @date: 2022-2-8 22:14:25
 */
@RunWith(SpringRunner.class)
public class SaFoxUtilTest {

    @Test
    public void getRandomString() {
    	String randomString = SaFoxUtil.getRandomString(8);
    	Assert.assertEquals(randomString.length(), 8);
    }

    @Test
    public void isEmpty() {
    	Assert.assertFalse(SaFoxUtil.isEmpty("abc"));
    	Assert.assertTrue(SaFoxUtil.isEmpty(""));
    	Assert.assertTrue(SaFoxUtil.isEmpty(null));
    	
    	Assert.assertTrue(SaFoxUtil.isNotEmpty("abc"));
    }

    @Test
    public void getMarking28() {
    	Assert.assertNotEquals(SaFoxUtil.getMarking28(), SaFoxUtil.getMarking28());
    }

    @Test
    public void formatDate() {
    	String formatDate = SaFoxUtil.formatDate(new Date(1644328600364L));
    	Assert.assertEquals(formatDate, "2022-02-08 21:56:40");
    }

    @Test
    public void searchList() {
    	// 原始数据 
    	List<String> dataList = Arrays.asList("token1", "token2", "token3", "token4", "token5", "aaa1");
    	
    	// 分页 
    	List<String> list1 = SaFoxUtil.searchList(dataList, 1, 2);
    	Assert.assertEquals(list1.size(), 2);
    	Assert.assertEquals(list1.get(0), "token2");
    	Assert.assertEquals(list1.get(1), "token3");
    	
    	// 前缀筛选 
    	List<String> list2 = SaFoxUtil.searchList(dataList, "token", "", 0, 10);
    	Assert.assertEquals(list2.size(), 5);

    	// 关键字筛选 
    	List<String> list3 = SaFoxUtil.searchList(dataList, "", "1", 0, 10);
    	Assert.assertEquals(list3.size(), 2);

    	// 综合筛选 
    	List<String> list4 = SaFoxUtil.searchList(dataList, "token", "1", 0, 10);
    	Assert.assertEquals(list4.size(), 1);
    }

    @Test
    public void vagueMatch() {
    	Assert.assertTrue(SaFoxUtil.vagueMatch("hello*", "hello"));
    	Assert.assertTrue(SaFoxUtil.vagueMatch("hello*", "hello world"));
    	Assert.assertFalse(SaFoxUtil.vagueMatch("hello*", "he"));
    	Assert.assertTrue(SaFoxUtil.vagueMatch("hello*", "hello*"));
    }

    @Test
    public void getValueByType() {
    	Assert.assertEquals(SaFoxUtil.getValueByType("1", Integer.class).getClass(), Integer.class);
    	Assert.assertEquals(SaFoxUtil.getValueByType("1", Long.class).getClass(), Long.class);
    	Assert.assertEquals(SaFoxUtil.getValueByType("1", Double.class).getClass(), Double.class);
    }

    @Test
    public void joinParam() {
    	Assert.assertEquals(SaFoxUtil.joinParam("https://sa-token.dev33.cn", "id=1"), "https://sa-token.dev33.cn?id=1");
    	Assert.assertEquals(SaFoxUtil.joinParam("https://sa-token.dev33.cn?", "id=1"), "https://sa-token.dev33.cn?id=1");
    	Assert.assertEquals(SaFoxUtil.joinParam("https://sa-token.dev33.cn?name=zhang", "id=1"), "https://sa-token.dev33.cn?name=zhang&id=1");
    	Assert.assertEquals(SaFoxUtil.joinParam("https://sa-token.dev33.cn?name=zhang&", "id=1"), "https://sa-token.dev33.cn?name=zhang&id=1");
    	
    	Assert.assertEquals(SaFoxUtil.joinParam("https://sa-token.dev33.cn?name=zhang&", "id", 1), "https://sa-token.dev33.cn?name=zhang&id=1");
    }

    @Test
    public void joinSharpParam() {
    	Assert.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.dev33.cn", "id=1"), "https://sa-token.dev33.cn#id=1");
    	Assert.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.dev33.cn#", "id=1"), "https://sa-token.dev33.cn#id=1");
    	Assert.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.dev33.cn#name=zhang", "id=1"), "https://sa-token.dev33.cn#name=zhang&id=1");
    	Assert.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.dev33.cn#name=zhang&", "id=1"), "https://sa-token.dev33.cn#name=zhang&id=1");
    	
    	Assert.assertEquals(SaFoxUtil.joinSharpParam("https://sa-token.dev33.cn#name=zhang&", "id", 1), "https://sa-token.dev33.cn#name=zhang&id=1");
    }

    @Test
    public void arrayJoin() {
    	Assert.assertEquals(SaFoxUtil.arrayJoin(new String[] {"a", "b", "c"}), "a,b,c");
    	Assert.assertEquals(SaFoxUtil.arrayJoin(new String[] {}), "");
    }

    @Test
    public void isUrl() {
    	Assert.assertTrue(SaFoxUtil.isUrl("https://sa-token.dev33.cn"));
    	Assert.assertTrue(SaFoxUtil.isUrl("https://www.baidu.com/"));
    	
    	Assert.assertFalse(SaFoxUtil.isUrl("htt://www.baidu.com/"));
    	Assert.assertFalse(SaFoxUtil.isUrl("https:www.baidu.com/"));
    	Assert.assertFalse(SaFoxUtil.isUrl("httpswwwbaiducom/"));
    	Assert.assertFalse(SaFoxUtil.isUrl("https://www.baidu.com/,"));
    }

    @Test
    public void encodeUrl() {
    	Assert.assertEquals(SaFoxUtil.encodeUrl("https://sa-token.dev33.cn"), "https%3A%2F%2Fsa-token.dev33.cn");
    	Assert.assertEquals(SaFoxUtil.decoderUrl("https%3A%2F%2Fsa-token.dev33.cn"), "https://sa-token.dev33.cn");
    }

    @Test
    public void convertStringToList() {
    	List<String> list = SaFoxUtil.convertStringToList("a,b,c");
    	Assert.assertEquals(list.size(), 3);
    	Assert.assertEquals(list.get(0), "a");
    	Assert.assertEquals(list.get(1), "b");
    	Assert.assertEquals(list.get(2), "c");

    	List<String> list2 = SaFoxUtil.convertStringToList("a,");
    	Assert.assertEquals(list2.size(), 1);

    	List<String> list3 = SaFoxUtil.convertStringToList(",");
    	Assert.assertEquals(list3.size(), 0);

    	List<String> list4 = SaFoxUtil.convertStringToList("");
    	Assert.assertEquals(list4.size(), 0);

    	List<String> list5 = SaFoxUtil.convertStringToList(null);
    	Assert.assertEquals(list5.size(), 0);
    }

    @Test
    public void convertListToString() {
    	List<String> list = Arrays.asList("a", "b", "c");
    	Assert.assertEquals(SaFoxUtil.convertListToString(list), "a,b,c");

    	List<String> list2 = Arrays.asList();
    	Assert.assertEquals(SaFoxUtil.convertListToString(list2), "");
    }

    @Test
    public void convertStringToArray() {
    	String[] array = SaFoxUtil.convertStringToArray("a,b,c");
    	Assert.assertEquals(array.length, 3);
    	Assert.assertEquals(array[0], "a");
    	Assert.assertEquals(array[1], "b");
    	Assert.assertEquals(array[2], "c");

    	String[] array2 = SaFoxUtil.convertStringToArray("a,");
    	Assert.assertEquals(array2.length, 1);

    	String[] array3 = SaFoxUtil.convertStringToArray(",");
    	Assert.assertEquals(array3.length, 0);

    	String[] array4 = SaFoxUtil.convertStringToArray("");
    	Assert.assertEquals(array4.length, 0);

    	String[] array5 = SaFoxUtil.convertStringToArray(null);
    	Assert.assertEquals(array5.length, 0);
    }

    @Test
    public void convertArrayToString() {
    	String[] array = new String[] {"a", "b", "c"};
    	Assert.assertEquals(SaFoxUtil.convertArrayToString(array), "a,b,c");

    	String[] array2 = new String[] {};
    	Assert.assertEquals(SaFoxUtil.convertArrayToString(array2), "");
    }

    @Test
    public void emptyList() {
    	List<String> list = SaFoxUtil.emptyList();
    	Assert.assertEquals(list.size(), 0);
    }

    @Test
    public void toList() {
    	List<String> list = SaFoxUtil.toList("a","b", "c");
    	Assert.assertEquals(list.size(), 3);
    	Assert.assertEquals(list.get(0), "a");
    	Assert.assertEquals(list.get(1), "b");
    	Assert.assertEquals(list.get(2), "c");
    }

}

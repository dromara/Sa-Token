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
package cn.dev33.satoken.util;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;

import java.io.Console;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Sa-Token 内部工具类
 *
 * @author click33
 * @since 1.18.0
 */
public class SaFoxUtil {

	private SaFoxUtil() {
	}

	/**
	 * 打印 Sa-Token 版本字符画
	 */
	public static void printSaToken() {
		String str = ""
				+ "____ ____    ___ ____ _  _ ____ _  _ \r\n"
				+ "[__  |__| __  |  |  | |_/  |___ |\\ | \r\n"
				+ "___] |  |     |  |__| | \\_ |___ | \\| "
//				+ SaTokenConsts.VERSION_NO
//				+ "sa-token："
//				+ "\r\n" + "DevDoc：" + SaTokenConsts.DEV_DOC_URL // + "\r\n";
				+ "\r\n" + SaTokenConsts.DEV_DOC_URL // + "\r\n";
				+ " (" + SaTokenConsts.VERSION_NO + ")"
//				+ "\r\n" + "GitHub：" + SaTokenConsts.GITHUB_URL // + "\r\n";
				;
		System.out.println(str);
	}

	/**
	 * 生成指定长度的随机字符串
	 *
	 * @param length 字符串的长度
	 * @return 一个随机字符串
	 */
	public static String getRandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int number = ThreadLocalRandom.current().nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 生成指定区间的 int 值
	 *
	 * @param min 最小值（包括）
	 * @param max 最大值（包括）
	 * @return /
	 */
	public static int getRandomNumber(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	/**
	 * 指定元素是否为null或者空字符串
	 * @param str 指定元素
	 * @return 是否为null或者空字符串
	 */
	public static boolean isEmpty(Object str) {
		return str == null || "".equals(str);
	}

	/**
	 * 指定元素是否不为 (null或者空字符串)
	 * @param str 指定元素
	 * @return 是否为null或者空字符串
	 */
	public static boolean isNotEmpty(Object str) {
		return ! isEmpty(str);
	}

	/**
	 * 指定数组是否为null或者空数组
	 * <h3> 该方法已过时，建议使用 isEmptyArray 方法 </h3>
	 * @param <T> /
	 * @param array /
	 * @return /
	 */
	@Deprecated
	public static <T> boolean isEmpty(T[] array) {
		return isEmptyArray(array);
	}

	/**
	 * 指定数组是否为null或者空数组
	 * @param <T> /
	 * @param array /
	 * @return /
	 */
	public static <T> boolean isEmptyArray(T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * 指定集合是否为null或者空数组
	 * @param list /
	 * @return /
	 */
	public static boolean isEmptyList(List<?> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * 比较两个对象是否相等
	 * @param a 第一个对象
	 * @param b 第二个对象
	 * @return 两个对象是否相等
	 */
	public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
	
	/**
	 * 比较两个对象是否不相等 
	 * @param a 第一个对象 
	 * @param b 第二个对象 
	 * @return 两个对象是否不相等 
	 */
	public static boolean notEquals(Object a, Object b) {
        return !equals(a, b);
    }
		/**
	 * 以当前时间戳和随机int数字拼接一个随机字符串
	 *
	 * @return 随机字符串
	 */
	public static String getMarking28() {
		return System.currentTimeMillis() + "" + ThreadLocalRandom.current().nextInt(Integer.MAX_VALUE);
	}

	/**
	 * 将日期格式化 （yyyy-MM-dd HH:mm:ss）
	 * @param date 日期
	 * @return 格式化后的时间
	 */
	public static String formatDate(Date date){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	/**
	 * 将日期格式化 （yyyy-MM-dd HH:mm:ss）
	 * @param zonedDateTime 日期
	 * @return 格式化后的时间
	 */
	public static String formatDate(ZonedDateTime zonedDateTime) {
		return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	/**
	 * 指定毫秒后的时间（格式化 ：yyyy-MM-dd HH:mm:ss）
	 * @param ms 指定毫秒后
	 * @return 格式化后的时间
	 */
	public static String formatAfterDate(long ms) {
		Instant instant = Instant.ofEpochMilli(System.currentTimeMillis() + ms);
		ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		return formatDate(zonedDateTime);
	}

	/**
	 * 从集合里查询数据
	 *
	 * @param dataList 数据集合
	 * @param prefix   前缀
	 * @param keyword  关键字
	 * @param start    起始位置 (-1代表查询所有)
	 * @param size     获取条数
	 * @param sortType     排序类型（true=正序，false=反序）
	 *
	 * @return 符合条件的新数据集合
	 */
	public static List<String> searchList(Collection<String> dataList, String prefix, String keyword, int start, int size, boolean sortType) {
		if (prefix == null) {
			prefix = "";
		}
		if (keyword == null) {
			keyword = "";
		}
		// 挑选出所有符合条件的
		List<String> list = new ArrayList<>();
		for (String key : dataList) {
			if (key.startsWith(prefix) && key.contains(keyword)) {
				list.add(key);
			}
		}
		// 取指定段数据
		return searchList(list, start, size, sortType);
	}

	/**
	 * 从集合里查询数据
	 *
	 * @param list  数据集合
	 * @param start 起始位置
	 * @param size  获取条数 (-1代表从start处一直取到末尾)
	 * @param sortType     排序类型（true=正序，false=反序）
	 *
	 * @return 符合条件的新数据集合
	 */
	public static List<String> searchList(List<String> list, int start, int size, boolean sortType) {
		// 如果是反序的话
		if( ! sortType) {
			Collections.reverse(list);
		}
		// start 至少为0
		if (start < 0) {
			start = 0;
		}
		// size为-1时，代表一直取到末尾，否则取到 start + size
		int end;
		if(size == -1) {
			end = list.size();
		} else {
			end = start + size;
		}
		// 取出的数据放到新集合中
		List<String> list2 = new ArrayList<>();
		for (int i = start; i < end; i++) {
			// 如果已经取到list的末尾，则直接退出
			if (i >= list.size()) {
				return list2;
			}
			list2.add(list.get(i));
		}
		return list2;
	}

	/**
	 * 字符串模糊匹配
	 * <p>example:
	 * <p> user* user-add   --  true
	 * <p> user* art-add    --  false
	 * @param patt 表达式
	 * @param str 待匹配的字符串
	 * @return 是否可以匹配
	 */
	public static boolean vagueMatch(String patt, String str) {
		// 两者均为 null 时，直接返回 true
		if(patt == null && str == null) {
			return true;
		}
		// 两者其一为 null 时，直接返回 false
		if(patt == null || str == null) {
			return false;
		}
		// 如果表达式不带有*号，则只需简单equals即可 (这样可以使速度提升200倍左右)
		if( ! patt.contains("*")) {
			return patt.equals(str);
		}
		// 深入匹配
		return vagueMatchMethod(patt, str);
	}

	/**
	 * 字符串模糊匹配
	 *
	 * @param pattern /
	 * @param str    /
	 * @return /
	 */
	private static boolean vagueMatchMethod( String pattern, String str) {
		int m = str.length();
		int n = pattern.length();
		boolean[][] dp = new boolean[m + 1][n + 1];
		dp[0][0] = true;
		for (int i = 1; i <= n; ++i) {
			if (pattern.charAt(i - 1) == '*') {
				dp[0][i] = true;
			} else {
				break;
			}
		}
		for (int i = 1; i <= m; ++i) {
			for (int j = 1; j <= n; ++j) {
				if (pattern.charAt(j - 1) == '*') {
					dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
				} else if (str.charAt(i - 1) == pattern.charAt(j - 1)) {
					dp[i][j] = dp[i - 1][j - 1];
				}
			}
		}
		return dp[m][n];
	}

	/**
	 * 判断类型是否为8大包装类型
	 * @param cs /
	 * @return /
	 */
	public static boolean isWrapperType(Class<?> cs) {
		return cs == Integer.class || cs == Short.class ||  cs == Long.class ||  cs == Byte.class
			|| cs == Float.class || cs == Double.class ||  cs == Boolean.class ||  cs == Character.class;
	}

	/**
	 * 判断类型是否为基础类型：8大基本数据类型、8大包装类、String
	 * @param cs /
	 * @return /
	 */
	public static boolean isBasicType(Class<?> cs) {
		return cs.isPrimitive() || isWrapperType(cs) || cs == String.class;
	}

	/**
	 * 将指定值转化为指定类型
	 * @param <T> 泛型
	 * @param obj 值
	 * @param cs 类型
	 * @return 转换后的值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getValueByType(Object obj, Class<T> cs) {
		// 如果 obj 为 null 或者本来就是 cs 类型
		if(obj == null || obj.getClass().equals(cs)) {
			return (T)obj;
		}
		// 开始转换
		String obj2 = String.valueOf(obj);
		Object obj3;
		if (cs.equals(String.class)) {
			obj3 = obj2;
		} else if (cs.equals(int.class) || cs.equals(Integer.class)) {
			obj3 = Integer.valueOf(obj2);
		} else if (cs.equals(long.class) || cs.equals(Long.class)) {
			obj3 = Long.valueOf(obj2);
		} else if (cs.equals(short.class) || cs.equals(Short.class)) {
			obj3 = Short.valueOf(obj2);
		} else if (cs.equals(byte.class) || cs.equals(Byte.class)) {
			obj3 = Byte.valueOf(obj2);
		} else if (cs.equals(float.class) || cs.equals(Float.class)) {
			obj3 = Float.valueOf(obj2);
		} else if (cs.equals(double.class) || cs.equals(Double.class)) {
			obj3 = Double.valueOf(obj2);
		} else if (cs.equals(boolean.class) || cs.equals(Boolean.class)) {
			obj3 = Boolean.valueOf(obj2);
		} else if (cs.equals(char.class) || cs.equals(Character.class)) {
			obj3 = obj2.charAt(0);
		} else {
			obj3 = obj;
		}
		return (T)obj3;
	}

	/**
	 * 将 Map 转化为 Object
	 * @param map /
	 * @param clazz /
	 * @return /
	 * @param <T> /
	 */
	public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
		if(map == null) {
			return null;
		}
		if(clazz == Map.class) {
			return (T) map;
		}
		try {
			T obj = clazz.getDeclaredConstructor().newInstance();
			for (Field field : clazz.getDeclaredFields()) {
				String fieldName = field.getName();
				if (map.containsKey(fieldName)) {
					field.setAccessible(true);
					field.set(obj, map.get(fieldName));
				}
			}
			return obj;
		} catch (Exception e) {
			throw new RuntimeException("转换失败: " + e.getMessage(), e);
		}
	}


	/**
	 * 在url上拼接上kv参数并返回
	 * @param url url
	 * @param paramStr 参数, 例如 id=1001
	 * @return 拼接后的url字符串
	 */
	public static String joinParam(String url, String paramStr) {
		// 如果参数为空, 直接返回
		if(paramStr == null || paramStr.length() == 0) {
			return url;
		}
		if(url == null) {
			url = "";
		}
		int index = url.lastIndexOf('?');
		// ? 不存在
		if(index == -1) {
			return url + '?' + paramStr;
		}
		// ? 是最后一位
		if(index == url.length() - 1) {
			return url + paramStr;
		}
		// ? 是其中一位
		if(index < url.length() - 1) {
			String separatorChar = "&";
			// 如果最后一位是 不是&, 且 paramStr 第一位不是 &, 就赠送一个 &
			if(url.lastIndexOf(separatorChar) != url.length() - 1 && paramStr.indexOf(separatorChar) != 0) {
				return url + separatorChar + paramStr;
			} else {
				return url + paramStr;
			}
		}
		// 正常情况下, 代码不可能执行到此
		return url;
	}

	/**
	 * 在url上拼接上kv参数并返回
	 * @param url url
	 * @param key 参数名称
	 * @param value 参数值
	 * @return 拼接后的url字符串
	 */
	public static String joinParam(String url, String key, Object value) {
		// 如果url或者key为空, 直接返回
		if(isEmpty(url) || isEmpty(key)) {
			return url;
		}
		return joinParam(url, key + "=" + value);
	}

	/**
	 * 在url上拼接锚参数
	 * @param url url
	 * @param paramStr 参数, 例如 id=1001
	 * @return 拼接后的url字符串
	 */
	public static String joinSharpParam(String url, String paramStr) {
		// 如果参数为空, 直接返回
		if(paramStr == null || paramStr.length() == 0) {
			return url;
		}
		if(url == null) {
			url = "";
		}
		int index = url.lastIndexOf('#');
		// # 不存在
		if(index == -1) {
			return url + '#' + paramStr;
		}
		// # 是最后一位
		if(index == url.length() - 1) {
			return url + paramStr;
		}
		// # 是其中一位
		if(index < url.length() - 1) {
			String separatorChar = "&";
			// 如果最后一位是 不是&, 且 paramStr 第一位不是 &, 就赠送一个 &
			if(url.lastIndexOf(separatorChar) != url.length() - 1 && paramStr.indexOf(separatorChar) != 0) {
				return url + separatorChar + paramStr;
			} else {
				return url + paramStr;
			}
		}
		// 正常情况下, 代码不可能执行到此
		return url;
	}

	/**
	 * 在url上拼接锚参数
	 * @param url url
	 * @param key 参数名称
	 * @param value 参数值
	 * @return 拼接后的url字符串
	 */
	public static String joinSharpParam(String url, String key, Object value) {
		// 如果url或者key为空, 直接返回
		if(isEmpty(url) || isEmpty(key)) {
			return url;
		}
		return joinSharpParam(url, key + "=" + value);
	}

	/**
	 * 拼接两个url
	 * <p> 例如：url1=http://domain.cn，url2=/sso/auth，则返回：http://domain.cn/sso/auth </p>
	 *
	 * @param url1 第一个url
	 * @param url2 第二个url
	 * @return 拼接完成的url
	 */
	public static String spliceTwoUrl(String url1, String url2) {
		// q1、任意一个为空，则直接返回另一个
		if(url1 == null) {
			return url2;
		}
		if(url2 == null) {
			return url1;
		}

		// q2、如果 url2 以 http 开头，将其视为一个完整地址
		if(url2.startsWith("http")) {
			return url2;
		}

		// q3、将两个地址拼接在一起
		return url1 + url2;
	}

	/**
	 * 将数组的所有元素使用逗号拼接在一起
	 * @param arr 数组
	 * @return 字符串，例: a,b,c
	 */
	public static String arrayJoin(String[] arr) {
		if(arr == null) {
			return "";
		}
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			str.append(arr[i]);
			if(i != arr.length - 1) {
				str.append(",");
			}
		}
		return str.toString();
	}

	/**
	 * 验证URL的正则表达式
	 */
	public static String URL_REGEX = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

	/**
	 * 使用正则表达式判断一个字符串是否为URL
	 * @param str 字符串
	 * @return 拼接后的url字符串
	 */
	public static boolean isUrl(String str) {
		if(isEmpty(str)) {
			return false;
		}
        return str.toLowerCase().matches(URL_REGEX);
	}

	/**
	 * URL编码
	 * @param url see note
	 * @return see note
	 */
	public static String encodeUrl(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new SaTokenException(e).setCode(SaErrorCode.CODE_12103);
		}
	}

	/**
	 * URL解码
	 * @param url see note
	 * @return see note
	 */
	public static String decoderUrl(String url) {
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new SaTokenException(e).setCode(SaErrorCode.CODE_12104);
		}
	}

	/**
	 * 将指定字符串按照逗号分隔符转化为字符串集合
	 * @param str 字符串
	 * @return 分割后的字符串集合
	 */
	public static List<String> convertStringToList(String str) {
		List<String> list = new ArrayList<>();
		if(isEmpty(str)) {
			return list;
		}
		String[] arr = str.split(",");
		for (String s : arr) {
			s = s.trim();
			if(!isEmpty(s)) {
				list.add(s);
			}
		}
		return list;
	}

	/**
	 * 将指定集合按照逗号连接成一个字符串
	 * @param list 集合
	 * @return 字符串
	 */
	public static String convertListToString(List<?> list) {
		if(list == null || list.isEmpty()) {
			return "";
		}
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			str.append(list.get(i));
			if(i != list.size() - 1) {
				str.append(",");
			}
		}
		return str.toString();
	}

    /**
     * String 转 Array，按照逗号切割
     * @param str 字符串
     * @return 数组
     */
    public static String[] convertStringToArray(String str) {
    	List<String> list = convertStringToList(str);
    	return list.toArray(new String[0]);
    }

    /**
     * Array 转 String，按照逗号连接
     * @param arr 数组
     * @return 字符串
     */
    public static String convertArrayToString(String[] arr) {
    	if(arr == null || arr.length == 0) {
    		return "";
    	}
    	return String.join(",", arr);
    }

    /**
     * 返回一个空集合
     * @param <T> 集合类型
     * @return 空集合
     */
    public static <T>List<T> emptyList() {
    	return new ArrayList<>();
    }

    /**
     * String数组转集合
     * @param str String数组
     * @return 集合
     */
    public static List<String> toList(String... str) {
		return new ArrayList<>(Arrays.asList(str));
    }

	/**
	 * String 集合转数组
	 * @param list 集合
	 * @return 数组
	 */
	public static String[] toArray(List<String> list) {
		return list.toArray(new String[0]);
	}

    public static List<String> logLevelList = Arrays.asList("", "trace", "debug", "info", "warn", "error", "fatal");

    /**
     * 将日志等级从 String 格式转化为 int 格式
     * @param level /
     * @return /
     */
    public static int translateLogLevelToInt(String level) {
    	int levelInt = logLevelList.indexOf(level);
    	if(levelInt <= 0 || levelInt >= logLevelList.size()) {
    		levelInt = 1;
    	}
    	return levelInt;
    }

    /**
     * 将日志等级从 String 格式转化为 int 格式
     * @param level /
     * @return /
     */
    public static String translateLogLevelToString(int level) {
    	if(level <= 0 || level >= logLevelList.size()) {
    		level = 1;
    	}
    	return logLevelList.get(level);
    }

	/**
	 * 判断当前系统是否可以打印彩色日志，判断准确率并非100%，但基本可以满足大部分场景
	 * @return /
	 */
	@SuppressWarnings("all")
	public static boolean isCanColorLog() {

		// 获取当前环境相关信息
		Console console = System.console();
		String term = System.getenv().get("TERM");

		// 两者均为 null，一般是在 eclipse、idea 等 IDE 环境下运行的，此时可以打印彩色日志
		if(console == null && term == null) {
			return true;
		}

		// 两者均不为 null，一般是在 linux 环境下控制台运行的，此时可以打印彩色日志
		if(console != null && term != null) {
			return true;
		}

		// console 有值，term 为 null，一般是在 windows 的 cmd 控制台运行的，此时不可以打印彩色日志
		if(console != null && term == null) {
			return false;
		}

		// console 为 null，term 有值，一般是在 linux 的 nohup 命令运行的，此时不可以打印彩色日志
		// 此时也有可能是在 windows 的 git bash 环境下运行的，此时可以打印彩色日志，但此场景无法和上述场景区分，所以统一不打印彩色日志
		if(console == null && term != null) {
			return false;
		}

		// 正常情况下，代码不会走到这里，但是方法又必须要有返回值，所以随便返回一个
		return false;
	}

	/**
	 * list1 是否完全包含 list2 中所有元素
	 * @param list1 集合1
	 * @param list2 集合2
	 * @return /
	 */
	public static boolean list1ContainList2AllElement(List<String> list1, List<String> list2){
		if(list2 == null || list2.isEmpty()) {
			return true;
		}
		if(list1 == null || list1.isEmpty()) {
			return false;
		}
		for (String str : list2) {
			if(!list1.contains(str)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * list1 是否包含 list2 中任意一个元素
	 * @param list1 集合1
	 * @param list2 集合2
	 * @return /
	 */
	public static boolean list1ContainList2AnyElement(List<String> list1, List<String> list2){
		if(list1 == null || list1.isEmpty() || list2 == null || list2.isEmpty()) {
			return false;
		}
		for (String str : list2) {
			if(list1.contains(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 从 list1 中剔除 list2 所包含的元素 （克隆副本操作，不影响 list1）
	 * @param list1 集合1
	 * @param list2 集合2
	 * @return /
	 */
	public static List<String> list1RemoveByList2(List<String> list1, List<String> list2){
		if(list1 == null) {
			return null;
		}
		if(list1.isEmpty() || list2 == null || list2.isEmpty()) {
			return new ArrayList<>(list1);
		}
		List<String> listX = new ArrayList<>(list1);
		for (String str : list2) {
			listX.remove(str);
		}
		return listX;
	}

	/**
	 * 检查字符串是否包含非可打印 ASCII 字符
	 * @param str /
	 * @return /
	 */
	public static boolean hasNonPrintableASCII(String str) {
		if (str == null) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			// ASCII 范围检查：0-31 或 127
			if ((c <= 31) || (c == 127)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 将 value 转化为 String，如果 value 为 null，则返回空字符串
	 * @param value /
	 * @return /
	 */
	public static String valueToString(Object value) {
		if (value == null) {
			return "";
		}
		return value.toString();
	}


}

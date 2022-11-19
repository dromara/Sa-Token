package cn.dev33.satoken.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;

/**
 * Sa-Token 内部工具类
 *
 * @author kong
 *
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
		return isEmpty(str) == false;
	}

	/**
	 * 指定数组是否为null或者空数组
	 * @param <T> /
	 * @param array /
	 * @return /
	 */
	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
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
		List<String> list = new ArrayList<String>();
		Iterator<String> keys = dataList.iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			if (key.startsWith(prefix) && key.indexOf(keyword) > -1) {
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
		if(sortType == false) {
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
		List<String> list2 = new ArrayList<String>();
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
		if(patt.indexOf("*") == -1) {
			return patt.equals(str);
		}
		// 正则匹配
		return Pattern.matches(patt.replaceAll("\\*", ".*"), str);
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
		Object obj3 = null;
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
			obj3 = (T)obj;
		}
		return (T)obj3;
	}

	/**
	 * 在url上拼接上kv参数并返回
	 * @param url url
	 * @param parameStr 参数, 例如 id=1001
	 * @return 拼接后的url字符串
	 */
	public static String joinParam(String url, String parameStr) {
		// 如果参数为空, 直接返回
		if(parameStr == null || parameStr.length() == 0) {
			return url;
		}
		if(url == null) {
			url = "";
		}
		int index = url.lastIndexOf('?');
		// ? 不存在
		if(index == -1) {
			return url + '?' + parameStr;
		}
		// ? 是最后一位
		if(index == url.length() - 1) {
			return url + parameStr;
		}
		// ? 是其中一位
		if(index > -1 && index < url.length() - 1) {
			String separatorChar = "&";
			// 如果最后一位是 不是&, 且 parameStr 第一位不是 &, 就增送一个 &
			if(url.lastIndexOf(separatorChar) != url.length() - 1 && parameStr.indexOf(separatorChar) != 0) {
				return url + separatorChar + parameStr;
			} else {
				return url + parameStr;
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
	 * @param parameStr 参数, 例如 id=1001
	 * @return 拼接后的url字符串
	 */
	public static String joinSharpParam(String url, String parameStr) {
		// 如果参数为空, 直接返回
		if(parameStr == null || parameStr.length() == 0) {
			return url;
		}
		if(url == null) {
			url = "";
		}
		int index = url.lastIndexOf('#');
		// ? 不存在
		if(index == -1) {
			return url + '#' + parameStr;
		}
		// ? 是最后一位
		if(index == url.length() - 1) {
			return url + parameStr;
		}
		// ? 是其中一位
		if(index > -1 && index < url.length() - 1) {
			String separatorChar = "&";
			// 如果最后一位是 不是&, 且 parameStr 第一位不是 &, 就增送一个 &
			if(url.lastIndexOf(separatorChar) != url.length() - 1 && parameStr.indexOf(separatorChar) != 0) {
				return url + separatorChar + parameStr;
			} else {
				return url + parameStr;
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
		String str = "";
		for (int i = 0; i < arr.length; i++) {
			str += arr[i];
			if(i != arr.length - 1) {
				str += ",";
			}
		}
		return str;
	}

	/**
	 * 验证URL的正则表达式
	 */
	public static final String URL_REGEX = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

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
		List<String> list = new ArrayList<String>();
		if(isEmpty(str)) {
			return list;
		}
		String[] arr = str.split(",");
		for (String s : arr) {
			s = s.trim();
			if(isEmpty(s) == false) {
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
		if(list == null || list.size() == 0) {
			return "";
		}
		String str = "";
		for (int i = 0; i < list.size(); i++) {
			str += list.get(i);
			if(i != list.size() - 1) {
				str += ",";
			}
		}
		return str;
	}

    /**
     * String 转 Array，按照逗号切割
     * @param str 字符串
     * @return 数组
     */
    public static String[] convertStringToArray(String str) {
    	List<String> list = convertStringToList(str);
    	return list.toArray(new String[list.size()]);
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
     * @param strs String数组
     * @return 集合
     */
    public static List<String> toList(String... strs) {
    	List<String> list = new ArrayList<>();
    	for (String str : strs) {
    		list.add(str);
		}
    	return list;
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

}

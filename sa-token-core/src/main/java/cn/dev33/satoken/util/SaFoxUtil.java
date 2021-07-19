package cn.dev33.satoken.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.dev33.satoken.exception.SaTokenException;

/**
 * Sa-Token 内部工具类 
 * 
 * @author kong
 *
 */
public class SaFoxUtil {

	/**
	 * 打印 Sa-Token 版本字符画
	 */
	public static void printSaToken() {
		String str = "____ ____    ___ ____ _  _ ____ _  _ \r\n" + "[__  |__| __  |  |  | |_/  |___ |\\ | \r\n"
				+ "___] |  |     |  |__| | \\_ |___ | \\| " 
//				+ "sa-token：" 
				+ "\r\n" + "DevDoc：" + SaTokenConsts.DEV_DOC_URL // + "\r\n";
				+ " (" + SaTokenConsts.VERSION_NO + ")" 
				+ "\r\n" + "GitHub：" + SaTokenConsts.GITHUB_URL // + "\r\n";
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
		Random random = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(62);
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
	 * 以当前时间戳和随机int数字拼接一个随机字符串
	 * 
	 * @return 随机字符串
	 */
	public static String getMarking28() {
		return System.currentTimeMillis() + "" + new Random().nextInt(Integer.MAX_VALUE);
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
	 * 从集合里查询数据
	 * 
	 * @param dataList 数据集合
	 * @param prefix   前缀
	 * @param keyword  关键字
	 * @param start    起始位置 (-1代表查询所有)
	 * @param size     获取条数
	 * @return 符合条件的新数据集合
	 */
	public static List<String> searchList(Collection<String> dataList, String prefix, String keyword, int start,
			int size) {
		// 挑选出所有符合条件的
		List<String> list = new ArrayList<>();
		String finalKeyword = keyword == null ? "" : keyword;
		String finalPrefix = prefix == null ? "" : prefix;
		dataList.stream()
				.filter(key -> key.startsWith(finalPrefix) && key.contains(finalKeyword))
				.forEach(list::add);
		// 取指定段数据
		return searchList(list, start, size);
	}

	/**
	 * 从集合里查询数据
	 * 
	 * @param list  数据集合
	 * @param start 起始位置 (-1代表查询所有)
	 * @param size  获取条数
	 * @return 符合条件的新数据集合
	 */
	public static List<String> searchList(List<String> list, int start, int size) {
		// 取指定段数据
		if (start < 0) {
			return list;
		}
		int end = start + size;
		List<String> list2 = new ArrayList<String>();
		for (int i = start; i < end; i++) {
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
		// 如果表达式不带有*号，则只需简单equals即可 (速度提升200倍) 
		if(!patt.contains("*")) {
			return patt.equals(str);
		}
		return Pattern.matches(patt.replaceAll("\\*", ".*"), str);
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
		} else {
			obj3 = obj;
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
		if(index < url.length() - 1) {
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
		// 如果参数为空, 直接返回 
		if(isEmpty(url) || isEmpty(key) || isEmpty(value)) {
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
		if(index < url.length() - 1) {
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
		// 如果参数为空, 直接返回 
		if(isEmpty(url) || isEmpty(key) || isEmpty(value)) {
			return url;
		}
		return joinSharpParam(url, key + "=" + value);
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
		return String.join(",", arr);
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
		if(str == null) {
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
			throw new SaTokenException(e);
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
			throw new SaTokenException(e);
		}
	}
	
	/**
	 * 将指定字符串按照逗号分隔符转化为字符串集合 
	 * @param str 字符串
	 * @return 分割后的字符串集合 
	 */
	public static List<String> convertStringToList(String str) {
		return convertStringToList(str, ",");
	}

	/**
	 * 将指定字符串按照逗号分隔符转化为字符串集合
	 * @param str 字符串
	 * @param delimiter 分隔符
	 * @return 分割后的字符串集合
	 */
	public static List<String> convertStringToList(String str, String delimiter) {
		List<String> list = new ArrayList<>();
		if(isEmpty(str)) {
			return list;
		}
		Arrays.stream(str.split(delimiter)).filter(s -> !isEmpty(s.trim()))
				.forEach(list::add);
		return list;
	}

	/**
	 * 将指定集合按照逗号连接成一个字符串 
	 * @param list 集合 
	 * @return 字符串 
	 */
	public static String convertListToString(List<?> list) {
		return convertListToString(list, ",");
	}

	/**
	 * 将指定集合按照逗号连接成一个字符串
	 * @param list 集合
	 * @param delimiter 分隔符
	 * @return 字符串，如果list为空则返回""
	 */
	public static String convertListToString(List<?> list, String delimiter) {
		if(list == null || list.size() == 0) {
			return "";
		}
		return list.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
	}
	
}

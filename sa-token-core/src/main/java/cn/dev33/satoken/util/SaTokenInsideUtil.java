package cn.dev33.satoken.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * sa-token 内部代码工具类
 * 
 * @author kong
 *
 */
public class SaTokenInsideUtil {

	/**
	 * 打印 sa-token 版本字符画
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
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * 指定字符串是否为null或者空字符串
	 * @param str 指定字符串
	 * @return 是否为null或者空字符串
	 */
	public static boolean isEmpty(String str) {
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
	 * 将日期格式化
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

}

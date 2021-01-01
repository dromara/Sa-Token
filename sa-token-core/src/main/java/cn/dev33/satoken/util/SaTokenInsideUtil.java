package cn.dev33.satoken.util;

import java.util.Random;

/**
 * sa-token 工具类 
 * @author kong
 *
 */
public class SaTokenInsideUtil {

	
	/**
	 * 打印 sa-token 版本字符画 
	 */
	public static void printSaToken() {
		String str = 
				"____ ____    ___ ____ _  _ ____ _  _ \r\n" + 
				"[__  |__| __  |  |  | |_/  |___ |\\ | \r\n" + 
				"___] |  |     |  |__| | \\_ |___ | \\| \r\n" + 
				"sa-token：" + SaTokenConsts.VERSION_NO + "         \r\n" +
				"GitHub：" + SaTokenConsts.GITHUB_URL; // + "\r\n";
		System.out.println(str);
	}

	
	/**
	 * 生成指定长度的随机字符串 
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
	 * 以当前时间戳和随机int数字拼接一个随机字符串 
	 * @return 随机字符串 
	 */
	public static String getMarking28() {
		return System.currentTimeMillis() + "" + new Random().nextInt(Integer.MAX_VALUE);
	}
			
	
}

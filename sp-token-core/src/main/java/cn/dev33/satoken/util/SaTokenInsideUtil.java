package cn.dev33.satoken.util;

import java.util.Random;

/**
 * sa-token 工具类 
 * @author kong
 *
 */
public class SaTokenInsideUtil {

	
	/**
	 * sa-token 版本号 
	 */
	public static final String VERSION_NO = "v1.6.0";

	/**
	 * sa-token 开源地址 
	 */
	public static final String GITHUB_URL = "https://github.com/click33/sa-token";
	
	/**
	 * 打印 sa-token
	 */
	public static void printSaToken() {
		String str = 
				"____ ____    ___ ____ _  _ ____ _  _ \r\n" + 
				"[__  |__| __  |  |  | |_/  |___ |\\ | \r\n" + 
				"___] |  |     |  |__| | \\_ |___ | \\| \r\n" + 
				"sa-token：" + VERSION_NO + "         \r\n" +
				"GitHub：" + GITHUB_URL; // + "\r\n";
		System.out.println(str);
	}

	/**
	 * 如果token为本次请求新创建的，则以此字符串为key存储在当前request中  JUST_CREATED_SAVE_KEY
	 */
	public static final String JUST_CREATED_SAVE_KEY = "JUST_CREATED_SAVE_KEY_"; 	
	
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
	
	
}

package cn.dev33.satoken.util;

/**
 * sa-token 工具类 
 * @author kong
 *
 */
public class SaTokenInsideUtil {

	
	/**
	 * sa-token 版本号 
	 */
	public static final String VERSION_NO = "v1.5.1";

	/**
	 * sa-token 开源地址 
	 */
	public static final String GITHUB_URL= "https://github.com/click33/sa-token";
	
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
	
	
}

package cn.dev33.satoken;

/**
 * sa-token 工具类 
 */
public class SaTokenUtil {

	
	// sa-token 版本号 
	public static final String version = "v1.0.3";

	// sa-token 开源地址 
	public static final String github_url = "https://github.com/click33/sa-token";
	
	// 打印 sa-token
	public static void printSaToken() {
		String str = 
				"____ ____    ___ ____ _  _ ____ _  _ \r\n" + 
				"[__  |__| __  |  |  | |_/  |___ |\\ | \r\n" + 
				"___] |  |     |  |__| | \\_ |___ | \\| \r\n" + 
				"sa-token：" + version + "         \r\n" +
				"GitHub：" + github_url + "\r\n";
		System.out.println(str);
	}

	// 如果token为本次请求新创建的，则以此字符串为key存储在当前request中  JUST_CREATED_SAVE_KEY
	public static final String JUST_CREATED_SAVE_KEY= "JUST_CREATED_SAVE_KEY_"; 	
	
	
}

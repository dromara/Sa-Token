package cn.dev33.satoken.util;

/**
 * 定义sa-token的所有常量 
 * @author kong
 *
 */
public class SaTokenConsts {

	/**
	 * sa-token 版本号 
	 */
	public static final String VERSION_NO = "v1.8.0";

	/**
	 * sa-token 开源地址 
	 */
	public static final String GITHUB_URL = "https://github.com/click33/sa-token";
	
	/**
	 * 如果token为本次请求新创建的，则以此字符串为key存储在当前request中  JUST_CREATED_SAVE_KEY
	 */
	public static final String JUST_CREATED_SAVE_KEY = "JUST_CREATED_SAVE_KEY_"; 	

	/**
	 * 如果本次请求已经验证过[无操作过期], 则以此值存储在当前request中  TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY
	 */
	public static final String TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY = "TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY_"; 	

	/**
	 * 在登录时，默认使用的设备名称 
	 */
	public static final String DEFAULT_LOGIN_DEVICE = "default-device"; 
	
//	/**
//	 * 在用一个字符串存储多个token时，所使用的分隔符 
//	 */
//	public static final String MULTIPLE_TOKEN_SEPARATOR = ","; 	

	
	
	
	
	
}

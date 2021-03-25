package cn.dev33.satoken.util;

/**
 * sa-token常量类 
 * @author kong
 *
 */
public class SaTokenConsts {

	
	// =================== sa-token版本信息 ===================  
	
	/**
	 * sa-token 版本号 
	 */
	public static final String VERSION_NO = "v1.15.2";

	/**
	 * sa-token 开源地址 
	 */
	public static final String GITHUB_URL = "https://github.com/click33/sa-token";

	
	// =================== 常量key标记 ===================  
	
	/**
	 * 常量key标记: 如果token为本次请求新创建的，则以此字符串为key存储在当前request中 
	 */
	public static final String JUST_CREATED_SAVE_KEY = "JUST_CREATED_SAVE_KEY_"; 	

	/**
	 * 常量key标记: 如果本次请求已经验证过[无操作过期], 则以此值存储在当前request中 
	 */
	public static final String TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY = "TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY_"; 	

	/**
	 * 常量key标记: 在登录时，默认使用的设备名称 
	 */
	public static final String DEFAULT_LOGIN_DEVICE = "default-device"; 
	
	/**
	 * 常量key标记: 在进行临时身份切换时使用的key 
	 */
	public static final String SWITCH_TO_SAVE_KEY = "SWITCH_TO_SAVE_KEY_"; 


	// =================== token-style 相关 ===================  
	
	/**
	 * token风格: uuid 
	 */
	public static final String TOKEN_STYLE_UUID = "uuid"; 
	
	/**
	 * token风格: 简单uuid (不带下划线) 
	 */
	public static final String TOKEN_STYLE_SIMPLE_UUID = "simple-uuid"; 
	
	/**
	 * token风格: 32位随机字符串 
	 */
	public static final String TOKEN_STYLE_RANDOM_32 = "random-32"; 
	
	/**
	 * token风格: 64位随机字符串 
	 */
	public static final String TOKEN_STYLE_RANDOM_64 = "random-64"; 
	
	/**
	 * token风格: 128位随机字符串 
	 */
	public static final String TOKEN_STYLE_RANDOM_128 = "random-128"; 
	
	/**
	 * token风格: tik风格 (2_14_16) 
	 */
	public static final String TOKEN_STYLE_TIK = "tik"; 

	
	// =================== 其它 ===================  

	/**
	 * 连接token前缀和token值的字符 
	 */
	public static final String TOKEN_CONNECTOR_CHAT  = " "; 
	
}

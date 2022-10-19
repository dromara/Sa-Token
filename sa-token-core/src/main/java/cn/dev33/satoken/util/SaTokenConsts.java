package cn.dev33.satoken.util;

/**
 * Sa-Token常量类 
 * @author kong
 *
 */
public class SaTokenConsts {

	private SaTokenConsts() {
	}
	
	// =================== sa-token版本信息 ===================  
	
	/**
	 * Sa-Token 当前版本号 
	 */
	public static final String VERSION_NO = "v1.31.1.temp";

	/**
	 * Sa-Token 开源地址 Gitee 
	 */
	public static final String GITEE_URL = "https://gitee.com/dromara/sa-token";

	/**
	 * Sa-Token 开源地址 GitHub  
	 */
	public static final String GITHUB_URL = "https://github.com/dromara/sa-token";

	/**
	 * Sa-Token 开发文档地址 
	 */
	public static final String DEV_DOC_URL = "http://sa-token.dev33.cn";
	
	// =================== 常量key标记 ===================  
	
	/**
	 * 常量key标记: 如果token为本次请求新创建的，则以此字符串为key存储在当前request中 
	 */
	public static final String JUST_CREATED = "JUST_CREATED_"; 	

	/**
	 * 常量key标记: 如果token为本次请求新创建的，则以此字符串为key存储在当前request中（不拼接前缀，纯Token）
	 */
	public static final String JUST_CREATED_NOT_PREFIX = "JUST_CREATED_NOT_PREFIX_"; 	

	/**
	 * 常量key标记: 如果本次请求已经验证过[无操作过期], 则以此值存储在当前request中 
	 */
	public static final String TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY = "TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY_"; 	

	/**
	 * 常量key标记: 在登录时，默认使用的设备类型 
	 */
	public static final String DEFAULT_LOGIN_DEVICE = "default-device"; 

	/**
	 * 常量key标记: 在封禁账号时，默认封禁的服务类型 
	 */
	public static final String DEFAULT_DISABLE_SERVICE = "login"; 

	/**
	 * 常量key标记: 在封禁账号时，默认封禁的等级 
	 */
	public static final int DEFAULT_DISABLE_LEVEL = 1; 

	/**
	 * 常量key标记: 在封禁账号时，可使用的最小封禁级别 
	 */
	public static final int MIN_DISABLE_LEVEL = 1; 

	/**
	 * 常量key标记: 账号封禁级别，表示未被封禁 
	 */
	public static final int NOT_DISABLE_LEVEL = -2; 
	
	/**
	 * 常量key标记: 在进行临时身份切换时使用的key 
	 */
	public static final String SWITCH_TO_SAVE_KEY = "SWITCH_TO_SAVE_KEY_"; 

	/**
	 * 常量key标记: 在进行Token二级验证时使用的key 
	 */
	public static final String SAFE_AUTH_SAVE_KEY = "SAFE_AUTH_SAVE_KEY_"; 


	// =================== token-style 相关 ===================  
	
	/**
	 * Token风格: uuid 
	 */
	public static final String TOKEN_STYLE_UUID = "uuid"; 
	
	/**
	 * Token风格: 简单uuid (不带下划线) 
	 */
	public static final String TOKEN_STYLE_SIMPLE_UUID = "simple-uuid"; 
	
	/**
	 * Token风格: 32位随机字符串 
	 */
	public static final String TOKEN_STYLE_RANDOM_32 = "random-32"; 
	
	/**
	 * Token风格: 64位随机字符串 
	 */
	public static final String TOKEN_STYLE_RANDOM_64 = "random-64"; 
	
	/**
	 * Token风格: 128位随机字符串 
	 */
	public static final String TOKEN_STYLE_RANDOM_128 = "random-128"; 
	
	/**
	 * Token风格: tik风格 (2_14_16) 
	 */
	public static final String TOKEN_STYLE_TIK = "tik"; 

	
	// =================== 其它 ===================  

	/**
	 * 连接Token前缀和Token值的字符 
	 */
	public static final String TOKEN_CONNECTOR_CHAT  = " "; 
	
	/**
	 * 切面、拦截器、过滤器等各种组件的注册优先级顺序
	 */
	public static final int ASSEMBLY_ORDER = -100;

	
	// =================== 废弃 ===================  

	/**
	 * 请更换为 JUST_CREATED  
	 */
	@Deprecated
	public static final String JUST_CREATED_SAVE_KEY = JUST_CREATED; 	
	
}

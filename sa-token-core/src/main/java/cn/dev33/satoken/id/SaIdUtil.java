package cn.dev33.satoken.id;

/**
 * <h1> 本类设计已过时，未来版本可能移除此类，请及时更换为 SaSameUtil ，使用方式保持不变 </h1>
 * 
 * Sa-Token-Id 身份凭证模块-工具类 
 * @author kong
 *
 */
@Deprecated
public class SaIdUtil {
	
	private SaIdUtil(){}

	/**
	 * 在 Request 上储存 Id-Token 时建议使用的key 
	 */
	public static final String ID_TOKEN = SaIdTemplate.ID_TOKEN;

	/**
	 * 底层 SaIdTemplate 对象 
	 */
	public static SaIdTemplate saIdTemplate = new SaIdTemplate();
	
	// -------------------- 获取 & 校验 
	
	/**
	 * 获取当前Id-Token, 如果不存在，则立即创建并返回 
	 * @return 当前token
	 */
	public static String getToken() {
		return saIdTemplate.getToken();
	}

	/**
	 * 判断一个Id-Token是否有效 
	 * @param token 要验证的token
	 * @return 这个token是否有效 
	 */
	public static boolean isValid(String token) {
		return saIdTemplate.isValid(token);
	}

	/**
	 * 校验一个Id-Token是否有效 (如果无效则抛出异常) 
	 * @param token 要验证的token
	 */
	public static void checkToken(String token) {
		saIdTemplate.checkToken(token);
	}

	/**
	 * 校验当前Request提供的Id-Token是否有效 (如果无效则抛出异常) 
	 */
	public static void checkCurrentRequestToken() {
		saIdTemplate.checkCurrentRequestToken();
	}
	
	/**
	 * 刷新一次Id-Token (注意集群环境中不要多个服务重复调用)
	 * @return 新Token 
	 */
	public static String refreshToken() {
		return saIdTemplate.refreshToken();
	}

	
	// -------------------- 获取Token 
	
	/**
	 * 获取Id-Token，不做任何处理 
	 * @return token 
	 */
	public static String getTokenNh() {
		return saIdTemplate.getTokenNh();
	}
	
	/**
	 * 获取Past-Id-Token，不做任何处理 
	 * @return token 
	 */
	public static String getPastTokenNh() {
		return saIdTemplate.getPastTokenNh();
	}

}

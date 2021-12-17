package cn.dev33.satoken.basic;

/**
 * Sa-Token Http Basic 认证 Util  
 * @author kong
 *
 */
public class SaBasicUtil {

	/**
	 * 底层 SaBasicTemplate 对象 
	 */
	public static SaBasicTemplate saBasicTemplate = new SaBasicTemplate();

	/**
	 * 获取浏览器提交的 Basic 参数 （裁剪掉前缀并解码）
	 * @return 值
	 */
	public static String getAuthorizationValue() {
		return saBasicTemplate.getAuthorizationValue();
	}
	
	/**
	 * 对当前会话进行 Basic 校验（使用全局配置的账号密码），校验不通过则抛出异常  
	 */
	public static void check() {
		saBasicTemplate.check();
	}

	/**
	 * 对当前会话进行 Basic 校验（手动设置账号密码），校验不通过则抛出异常  
	 * @param account 账号（格式为 user:password）
	 */
	public static void check(String account) {
		saBasicTemplate.check(account);
	}

	/**
	 * 对当前会话进行 Basic 校验（手动设置 Realm 和 账号密码），校验不通过则抛出异常 
	 * @param realm 领域 
	 * @param account 账号（格式为 user:password）
	 */
	public static void check(String realm, String account) {
		try {
                	saBasicTemplate.check(realm, account);
		} catch (NotBasicAuthException e) {
			SaRouter.back();
		}
		
	}

}

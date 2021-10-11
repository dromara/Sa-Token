package cn.dev33.satoken.temp;

import cn.dev33.satoken.SaManager;

/**
 * Sa-Token 临时验证令牌模块 
 * @author kong
 *
 */
public class SaTempUtil {

	/**
	 * 根据value创建一个token 
	 * @param value 指定值
	 * @param timeout 有效期，单位：秒 
	 * @return 生成的token
	 */
	public static String createToken(Object value, long timeout) {
		return SaManager.getSaTemp().createToken(value, timeout);
	}
	
	/**
	 *  解析token获取value 
	 * @param token 指定token 
	 * @return  See Note 
	 */
	public static Object parseToken(String token) {
		return SaManager.getSaTemp().parseToken(token);
	}

	/**
	 * 解析token获取value，并转换为指定类型 
	 * @param token 指定token 
	 * @param cs 指定类型 
	 * @param <T> 默认值的类型 
	 * @return  See Note 
	 */
	public static<T> T parseToken(String token, Class<T> cs) {
		return SaManager.getSaTemp().parseToken(token, cs);
	}
	
	/**
	 * 获取指定 token 的剩余有效期，单位：秒 
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param token see note 
	 * @return see note 
	 */
	public static long getTimeout(String token) {
		return SaManager.getSaTemp().getTimeout(token);
	}

	/**
	 * 删除一个 token 
	 * @param token 指定token 
	 */
	public static void deleteToken(String token) {
		SaManager.getSaTemp().deleteToken(token);
	}
	
}

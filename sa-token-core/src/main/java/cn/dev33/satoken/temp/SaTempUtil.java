package cn.dev33.satoken.temp;

import cn.dev33.satoken.SaManager;

/**
 * Sa-Token 临时验证令牌模块 
 * @author kong
 *
 */
public class SaTempUtil {

	private SaTempUtil() {
	}

	/**
	 * 为 指定值 创建一个临时 Token 
	 * @param value 指定值
	 * @param timeout 有效期，单位：秒，-1代表永久有效
	 * @return 生成的token
	 */
	public static String createToken(Object value, long timeout) {
		return SaManager.getSaTemp().createToken(value, timeout);
	}

	/**
	 * 为 指定服务 指定值 创建一个 Token 
	 * @param service 服务标识
	 * @param value 指定值
	 * @param timeout 有效期，单位：秒，-1代表永久有效
	 * @return 生成的token
	 */
	public static String createToken(String service, Object value, long timeout) {
		return SaManager.getSaTemp().createToken(service, value, timeout);
	}

	/**
	 * 解析 Token 获取 value 
	 * @param token 指定 Token 
	 * @return  / 
	 */
	public static Object parseToken(String token) {
		return SaManager.getSaTemp().parseToken(token);
	}

	/**
	 * 解析 Token 获取 value 
	 * @param service 服务标识
	 * @param token 指定 Token 
	 * @return /
	 */
	public static Object parseToken(String service, String token) {
		return SaManager.getSaTemp().parseToken(service, token);
	}

	/**
	 * 解析 Token 获取 value，并转换为指定类型 
	 * @param token 指定 Token 
	 * @param cs 指定类型 
	 * @param <T> 默认值的类型 
	 * @return /
	 */
	public static<T> T parseToken(String token, Class<T> cs) {
		return SaManager.getSaTemp().parseToken(token, cs);
	}

	/**
	 * 解析 Token 获取 value，并转换为指定类型
	 * @param service 服务标识
	 * @param token 指定 Token 
	 * @param cs 指定类型 
	 * @param <T> 默认值的类型 
	 * @return /
	 */
	public static<T> T parseToken(String service, String token, Class<T> cs) {
		return SaManager.getSaTemp().parseToken(service, token, cs);
	}
	
	/**
	 * 获取指定 Token 的剩余有效期，单位：秒 
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param token 指定 Token
	 * @return /
	 */
	public static long getTimeout(String token) {
		return SaManager.getSaTemp().getTimeout(token);
	}

	/**
	 * 获取指定服务指定 Token 的剩余有效期，单位：秒 
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param service 服务标识
	 * @param token 指定 Token
	 * @return / 
	 */
	public static long getTimeout(String service, String token) {
		return SaManager.getSaTemp().getTimeout(service, token);
	}

	/**
	 * 删除一个 Token
	 * @param token 指定 Token 
	 */
	public static void deleteToken(String token) {
		SaManager.getSaTemp().deleteToken(token);
	}
	
	/**
	 * 删除一个 Token
	 * @param service 服务标识
	 * @param token 指定 Token 
	 */
	public static void deleteToken(String service, String token) {
		SaManager.getSaTemp().deleteToken(service, token);
	}
	
}

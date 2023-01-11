package cn.dev33.satoken.temp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Sa-Token 临时令牌验证模块接口 
 * @author kong
 *
 */
public interface SaTempInterface {

	/**
	 * 为 指定值 创建一个临时 Token 
	 * @param value 指定值
	 * @param timeout 有效期，单位：秒，-1代表永久有效
	 * @return 生成的token
	 */
	public default String createToken(Object value, long timeout) {
		return createToken(SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE, value, timeout);
	}

	/**
	 * 为 指定服务 指定值 创建一个 Token 
	 * @param service 服务标识
	 * @param value 指定值
	 * @param timeout 有效期，单位：秒，-1代表永久有效
	 * @return 生成的token
	 */
	public default String createToken(String service, Object value, long timeout) {
		
		// 生成 token 
		String token = SaStrategy.me.createToken.apply(null, null);
		
		// 持久化映射关系 
		String key = splicingKeyTempToken(service, token);
		SaManager.getSaTokenDao().setObject(key, value, timeout);
		
		// 返回 
		return token;
	}
	
	/**
	 * 解析 Token 获取 value 
	 * @param token 指定 Token 
	 * @return  / 
	 */
	public default Object parseToken(String token) {
		return parseToken(SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE, token);
	}

	/**
	 * 解析 Token 获取 value 
	 * @param service 服务标识
	 * @param token 指定 Token 
	 * @return /
	 */
	public default Object parseToken(String service, String token) {
		String key = splicingKeyTempToken(service, token);
		return SaManager.getSaTokenDao().getObject(key);
	}

	/**
	 * 解析 Token 获取 value，并转换为指定类型 
	 * @param token 指定 Token 
	 * @param cs 指定类型 
	 * @param <T> 默认值的类型 
	 * @return /
	 */
	public default<T> T parseToken(String token, Class<T> cs) {
		return parseToken(SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE, token, cs);
	}

	/**
	 * 解析 Token 获取 value，并转换为指定类型
	 * @param service 服务标识
	 * @param token 指定 Token 
	 * @param cs 指定类型 
	 * @param <T> 默认值的类型 
	 * @return /
	 */
	public default<T> T parseToken(String service, String token, Class<T> cs) {
		return SaFoxUtil.getValueByType(parseToken(service, token), cs);
	}
	
	/**
	 * 获取指定 Token 的剩余有效期，单位：秒 
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param token 指定 Token
	 * @return /
	 */
	public default long getTimeout(String token) {
		return getTimeout(SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE, token);
	}

	/**
	 * 获取指定服务指定 Token 的剩余有效期，单位：秒 
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param service 服务标识
	 * @param token 指定 Token
	 * @return / 
	 */
	public default long getTimeout(String service, String token) {
		String key = splicingKeyTempToken(service, token);
		return SaManager.getSaTokenDao().getObjectTimeout(key);
	}

	/**
	 * 删除一个 Token
	 * @param token 指定 Token 
	 */
	public default void deleteToken(String token) {
		deleteToken(SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE, token);
	}
	
	/**
	 * 删除一个 Token
	 * @param service 服务标识
	 * @param token 指定 Token 
	 */
	public default void deleteToken(String service, String token) {
		String key = splicingKeyTempToken(service, token);
		SaManager.getSaTokenDao().deleteObject(key);
	}
	
	/**  
	 * 获取映射关系的持久化key 
	 * @param service 服务标识
	 * @param token token值
	 * @return key
	 */
	public default String splicingKeyTempToken(String service, String token) {
		return SaManager.getConfig().getTokenName() + ":temp-token:" + service + ":" + token;
	}

	/**
	 * @return jwt秘钥 (只有集成 sa-token-temp-jwt 模块时此参数才会生效)  
	 */
	public default String getJwtSecretKey() {
		return null;
	}
	
}

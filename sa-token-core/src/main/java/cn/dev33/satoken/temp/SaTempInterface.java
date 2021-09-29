package cn.dev33.satoken.temp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token 临时令牌验证模块接口 
 * @author kong
 *
 */
public interface SaTempInterface {

	/**
	 * 根据value创建一个token 
	 * @param value 指定值
	 * @param timeout 有效期，单位：秒 
	 * @return 生成的token
	 */
	public default String createToken(Object value, long timeout) {
		
		// 生成 token 
		String token = SaStrategy.me.createToken.apply(null, null);
		
		// 持久化映射关系 
		String key = splicingKeyTempToken(token);
		SaManager.getSaTokenDao().setObject(key, value, timeout);
		
		// 返回 
		return token;
	}
	
	/**
	 *  解析token获取value 
	 * @param token 指定token 
	 * @return  See Note 
	 */
	public default Object parseToken(String token) {
		String key = splicingKeyTempToken(token);
		return SaManager.getSaTokenDao().getObject(key);
	}

	/**
	 * 解析token获取value，并转换为指定类型 
	 * @param token 指定token 
	 * @param cs 指定类型 
	 * @param <T> 默认值的类型 
	 * @return  See Note 
	 */
	public default<T> T parseToken(String token, Class<T> cs) {
		return SaFoxUtil.getValueByType(parseToken(token), cs);
	}
	
	/**
	 * 获取指定 token 的剩余有效期，单位：秒 
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param token see note 
	 * @return see note 
	 */
	public default long getTimeout(String token) {
		String key = splicingKeyTempToken(token);
		return SaManager.getSaTokenDao().getObjectTimeout(key);
	}

	/**
	 * 删除一个token
	 * @param token 指定token 
	 */
	public default void deleteToken(String token) {
		String key = splicingKeyTempToken(token);
		SaManager.getSaTokenDao().deleteObject(key);
	}
	
	/**  
	 * 获取映射关系的持久化key 
	 * @param token token值
	 * @return key
	 */
	public default String splicingKeyTempToken(String token) {
		return SaManager.getConfig().getTokenName() + ":temp-token:" + token;
	}

	/**
	 * @return jwt秘钥 (只有集成 sa-token-temp-jwt 模块时此参数才会生效)  
	 */
	public default String getJwtSecretkey() {
		return null;
	}
	
}

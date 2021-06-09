package cn.dev33.satoken.temp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token 临时验证模块接口 
 * @author kong
 *
 */
public interface SaTempInterface {

	/**
	 * 根据value创建一个token 
	 * @param value 指定值
	 * @param time 有效期，单位：秒 
	 * @return 生成的token
	 */
	public default String createToken(Object value, long timeout) {
		
		// 生成 token 
		String token = SaManager.getSaTokenAction().createToken(null, null);
		
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
	 * @return  See Note 
	 */
	public default<T> T parseToken(String token, Class<T> cs) {
		return SaFoxUtil.getValueByType(parseToken(token), cs);
	}
	
	/**
	 * 返回指定token的剩余有效期，单位：秒 
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param token see note 
	 * @return see note 
	 */
	public default long getTimeout(String token) {
		String key = splicingKeyTempToken(token);
		return SaManager.getSaTokenDao().getObjectTimeout(key);
	}
	
	/**  
	 * 获取映射关系的持久化key 
	 * @param token token值
	 * @return key
	 */
	public default String splicingKeyTempToken(String token) {
		return SaManager.getConfig().getTokenName() + ":temp-token:" + token;
	}

}

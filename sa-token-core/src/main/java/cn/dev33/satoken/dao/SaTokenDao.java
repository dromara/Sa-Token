package cn.dev33.satoken.dao;

import cn.dev33.satoken.session.SaSession;

/**
 * sa-token持久层的接口 
 * @author kong
 *
 */
public interface SaTokenDao {

	
	/** 常量，表示一个key永不过期 (在一个key被标注为永远不过期时返回此值) */ 
	public static final Long NEVER_EXPIRE = -1L;
	
	/** 常量，表示系统中不存在这个缓存 (在对不存在的key获取剩余存活时间时返回此值) */ 
	public static final Long NOT_VALUE_EXPIRE = -2L;
	
	
	
	/**
	 * 根据key获取value ，如果没有，则返回空 
	 * @param key 键名称 
	 * @return value
	 */
	public String getValue(String key);

	/**
	 * 写入指定key-value键值对，并设定过期时间 (单位：秒)
	 * @param key 键名称 
	 * @param value 值 
	 * @param timeout 过期时间，单位：s 
	 */
	public void setValue(String key, String value, long timeout);

	/**
	 * 修改指定key-value键值对 (过期时间取原来的值)
	 * @param key 键名称 
	 * @param value 值 
	 */
	public void updateValue(String key, String value);

	/**
	 * 删除一个指定的key 
	 * @param key 键名称 
	 */
	public void deleteKey(String key);
	
	/**
	 * 获取指定key的剩余存活时间 (单位: 秒)
	 * @param key 指定key 
	 * @return 这个key的剩余存活时间 
	 */
	public long getTimeout(String key);
	

	/**
	 * 根据指定key的session，如果没有，则返回空 
	 * @param sessionId 键名称 
	 * @return SaSession
	 */
	public SaSession getSession(String sessionId);

	/**
	 * 将指定session持久化 
	 * @param session 要保存的session对象
	 * @param timeout 过期时间，单位: s
	 */
	public void saveSession(SaSession session, long timeout);

	/**
	 * 更新指定session
	 * @param session 要更新的session对象
	 */
	public void updateSession(SaSession session);
	
	/**
	 * 删除一个指定的session 
	 * @param sessionId sessionId
	 */
	public void deleteSession(String sessionId);

	/**
	 * 获取指定SaSession的剩余存活时间 (单位: 秒)
	 * @param sessionId 指定SaSession 
	 * @return 这个SaSession的剩余存活时间 
	 */
	public long getSessionTimeout(String sessionId);
	
	
	
	
	
}

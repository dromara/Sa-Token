package com.pj.sso.util;

import cn.hutool.cache.impl.TimedCache;

/**
 * 简易缓存（基于 Hutool TimedCache，demo 用，生产可换 Redis）
 *
 * @author click33
 */
public class CacheUtil {

	private static final TimedCache<String, Object> cache = cn.hutool.cache.CacheUtil.newTimedCache(1000);

	/**
	 * 写入缓存
	 * @param key 键
	 * @param value 值
	 * @param timeout 有效期（毫秒）
	 */
	public static void set(String key, Object value, long timeout) {
		cache.put(key, value, timeout);
	}

	/**
	 * 读取缓存，不存在或已过期返回 null
	 * @param key 键
	 * @return 值
	 */
	public static Object get(String key) {
		return cache.get(key, false);
	}

	/**
	 * 删除缓存
	 * @param key 键
	 */
	public static void delete(String key) {
		cache.remove(key);
	}

	/**
	 * 仅当 key 不存在（或已过期）时写入，成功返回 true
	 * @param key 键
	 * @param value 值
	 * @param timeout 有效期（毫秒）
	 * @return 是否写入成功
	 */
	public static boolean setIfAbsent(String key, Object value, long timeout) {
		if (get(key) != null) {
			return false;
		}
		set(key, value, timeout);
		return true;
	}

}

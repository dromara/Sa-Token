/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.dao;


import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.auto.SaTokenDaoByStringFollowObject;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import cn.hutool.cache.impl.TimedCache;

import java.util.Iterator;
import java.util.List;

/**
 * Sa-Token 持久层接口（基于 Hutool-TimedCache，系统重启后数据丢失）
 *
 * @author click33
 * @since 1.38.0
 */
public class SaTokenDaoForHutoolTimedCache implements SaTokenDaoByStringFollowObject {

	//
	/**
	 * 底层缓存对象：
	 * 参数填1000，代表默认ttl为1000毫秒，实际上此参数意义不大，因为后续每个值都会单独设置自己的ttl值
	 */
	public TimedCache<String, Object> timedCache = CacheUtil.newTimedCache(1000);


	// ------------------------ Object 读写操作

	@Override
	public Object getObject(String key) {
		// 第二个参数代表：是否刷新最后访问时间
		// 设置为false，因为我们不需要刷新最后访问时间，只需要取值即可
		return timedCache.get(key, false);
	}

	@Override
	public void setObject(String key, Object object, long timeout) {
		if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
			return;
		}
		// 如果为永不过期
		// 		在 sa-token 中，-1 代表永不过期
		// 		在 hutool-TimedCache 中，0 代表永不过期
		// 		为了适应 hutool-TimedCache 规范，这里将 -1 转换为 0
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			timedCache.put(key, object, 0);
			return;
		}
		// 正常情况
		timedCache.put(key, object, timeout * 1000);
	}

	@Override
	public void updateObject(String key, Object object) {
		long expire = getObjectTimeout(key);
		// -2 = 无此键
		if(expire == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		this.setObject(key, object, expire);
	}

	@Override
	public void deleteObject(String key) {
		timedCache.remove(key);
	}

	@Override
	public long getObjectTimeout(String key) {
		return getKeyTimeout(key);
	}

	@Override
	public void updateObjectTimeout(String key, long timeout) {
		// $$待优化：对一个不存在的key进行修改timeout操作时，可能会造成一些意外数据，待进一步测试
		this.setObject(key, this.getObject(key), timeout);
	}


	// ------------------------ Session 读写操作
	// 使用接口默认实现


	// --------- 会话管理

	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
		return SaFoxUtil.searchList(timedCache.keySet(), prefix, keyword, start, size, sortType);
	}



	// --------- 过期时间相关操作

	/**
	 * 获取指定 key 的剩余存活时间 （单位：秒）
	 * @param key 指定 key
	 * @return 这个 key 的剩余存活时间，返回-1=永不过期，返回-2=无此键
	 */
	long getKeyTimeout(String key) {
		final Iterator<CacheObj<String, Object>> values = timedCache.cacheObjIterator();
		CacheObj<String, Object> co;
		while (values.hasNext()) {
			co = values.next();
			if(co.getKey().equals(key)) {
				long ttl = co.getTtl();
				// 在 Hutool-TimedCache 中，ttl=0 (或<0) 代表永不过期，统一返回 Sa-Token 可以理解的 -1
				if(ttl <= 0) {
					return NEVER_EXPIRE;
				}
				// 不为 0，那就计算一下剩余有效期
				// 单位：毫秒
				long timeout = ttl - (System.currentTimeMillis() - co.getLastAccess());
				if(timeout < 0) {
					timeout = 0;
				}
				// 转秒返回
				return timeout / 1000;
			}
		}
		// 代码至此，说明缓存中没有这个值
		return NOT_VALUE_EXPIRE;
	}

	// --------- 定时清理过期数据

	/**
	 * 组件被安装时，开始刷新数据线程
	 */
	@Override
	public void init() {
		// 定时清理间隔
		int dataRefreshPeriod = SaManager.getConfig().getDataRefreshPeriod();
		// 配置为<=0代表不启用定时清理
		if(dataRefreshPeriod <= 0) {
			return;
		}
		// 启用定时清理（转毫秒）
		timedCache.schedulePrune(dataRefreshPeriod * 1000L);
	}

	/**
	 * 组件被卸载时，结束定时任务，不再定时清理过期数据
	 */
	@Override
	public void destroy() {
		timedCache.cancelPruneSchedule();
	}

}

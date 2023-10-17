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
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sa-Token 持久层接口，默认实现类（基于内存 Map，系统重启后数据丢失）
 *
 * @author click33
 * @since 1.10.0
 */
public class SaTokenDaoDefaultImpl implements SaTokenDao {

	/**
	 * 存储数据的集合
	 */
	public Map<String, Object> dataMap = new ConcurrentHashMap<>();

	/**
	 * 存储数据过期时间的集合（单位: 毫秒）, 记录所有 key 的到期时间 （注意存储的是到期时间，不是剩余存活时间）
	 */
	public Map<String, Long> expireMap = new ConcurrentHashMap<>();

	/**
	 * 构造函数
	 */
	public SaTokenDaoDefaultImpl() {
		initRefreshThread();
	}
	
	
	// ------------------------ String 读写操作 
	
	@Override
	public String get(String key) {
		clearKeyByTimeout(key);
		return (String)dataMap.get(key);
	}

	@Override
	public void set(String key, String value, long timeout) {
		if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
			return;
		}
		dataMap.put(key, value);
		expireMap.put(key, (timeout == SaTokenDao.NEVER_EXPIRE) ? (SaTokenDao.NEVER_EXPIRE) : (System.currentTimeMillis() + timeout * 1000));
	}

	@Override
	public void update(String key, String value) {
		if(getKeyTimeout(key) == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		dataMap.put(key, value);
	}

	@Override
	public void delete(String key) {
		dataMap.remove(key);
		expireMap.remove(key);
	}
	
	@Override
	public long getTimeout(String key) {
		return getKeyTimeout(key);
	}

	@Override
	public void updateTimeout(String key, long timeout) {
		expireMap.put(key, (timeout == SaTokenDao.NEVER_EXPIRE) ? (SaTokenDao.NEVER_EXPIRE) : (System.currentTimeMillis() + timeout * 1000));
	}

	
	// ------------------------ Object 读写操作 
	
	@Override
	public Object getObject(String key) {
		clearKeyByTimeout(key);
		return dataMap.get(key);
	}

	@Override
	public void setObject(String key, Object object, long timeout) {
		if(timeout == 0 || timeout <= SaTokenDao.NOT_VALUE_EXPIRE)  {
			return;
		}
		dataMap.put(key, object);
		expireMap.put(key, (timeout == SaTokenDao.NEVER_EXPIRE) ? (SaTokenDao.NEVER_EXPIRE) : (System.currentTimeMillis() + timeout * 1000));
	}

	@Override
	public void updateObject(String key, Object object) {
		if(getKeyTimeout(key) == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		dataMap.put(key, object);
	}

	@Override
	public void deleteObject(String key) {
		dataMap.remove(key);
		expireMap.remove(key);
	}

	@Override
	public long getObjectTimeout(String key) {
		return getKeyTimeout(key);
	}

	@Override
	public void updateObjectTimeout(String key, long timeout) {
		expireMap.put(key, (timeout == SaTokenDao.NEVER_EXPIRE) ? (SaTokenDao.NEVER_EXPIRE) : (System.currentTimeMillis() + timeout * 1000));
	}
	
	
	// ------------------------ Session 读写操作 
	// 使用接口默认实现 


	// --------- 会话管理

	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
		return SaFoxUtil.searchList(expireMap.keySet(), prefix, keyword, start, size, sortType);
	}


	// ------------------------ 以下是一个定时缓存的简单实现，采用：惰性检查 + 异步循环扫描

	// --------- 过期时间相关操作

	/**
	 * 如果指定的 key 已经过期，则立即清除它
	 * @param key 指定 key
	 */
	void clearKeyByTimeout(String key) {
		Long expirationTime = expireMap.get(key);
		// 清除条件：
		// 		1、数据存在。
		// 		2、不是 [ 永不过期 ]。
		// 		3、已经超过过期时间。
		if(expirationTime != null && expirationTime != SaTokenDao.NEVER_EXPIRE && expirationTime < System.currentTimeMillis()) {
			dataMap.remove(key);
			expireMap.remove(key);
		}
	}

	/**
	 * 获取指定 key 的剩余存活时间 （单位：秒）
	 * @param key 指定 key
	 * @return 这个 key 的剩余存活时间
	 */
	long getKeyTimeout(String key) {
		// 由于数据过期检测属于惰性扫描，很可能此时这个 key 已经是过期状态了，所以这里需要先检查一下
		clearKeyByTimeout(key);

		// 获取这个 key 的过期时间
		Long expire = expireMap.get(key);

		// 如果 expire 数据不存在，说明框架没有存储这个 key，此时返回 NOT_VALUE_EXPIRE
		if(expire == null) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}

		// 如果 expire 被标注为永不过期，则返回 NEVER_EXPIRE
		if(expire == SaTokenDao.NEVER_EXPIRE) {
			return SaTokenDao.NEVER_EXPIRE;
		}

		// ---- 代码至此，说明这个 key 是有过期时间的，且未过期，那么：

		// 计算剩余时间并返回 （过期时间戳 - 当前时间戳） / 1000 转秒
		long timeout = (expire - System.currentTimeMillis()) / 1000;

		// 小于零时，视为不存在 
		if(timeout < 0) {
			dataMap.remove(key);
			expireMap.remove(key);
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}
		return timeout;
	}

	// --------- 定时清理过期数据
	
	/**
	 * 执行数据清理的线程引用
	 */
	public Thread refreshThread;
	
	/**
	 * 是否继续执行数据清理的线程标记
	 */
	public volatile boolean refreshFlag;

	/**
	 * 清理所有已经过期的 key
	 */
	public void refreshDataMap() {
		for (String s : expireMap.keySet()) {
			clearKeyByTimeout(s);
		}
	}
	
	/**
	 * 初始化定时任务，定时清理过期数据
	 */
	public void initRefreshThread() {

		// 如果开发者配置了 <=0 的值，则不启动定时清理
		if(SaManager.getConfig().getDataRefreshPeriod() <= 0) {
			return;
		}

		// 启动定时刷新
		this.refreshFlag = true;
		this.refreshThread = new Thread(() -> {
			for (;;) {
				try {
					try {
						// 如果已经被标记为结束
						if( ! refreshFlag) {
							return;
						}
						// 执行清理
						refreshDataMap(); 
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 休眠N秒 
					int dataRefreshPeriod = SaManager.getConfig().getDataRefreshPeriod();
					if(dataRefreshPeriod <= 0) {
						dataRefreshPeriod = 1;
					}
					Thread.sleep(dataRefreshPeriod * 1000L);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		this.refreshThread.start();
	}

	/**
	 * 结束定时任务，不再定时清理过期数据
	 */
	@Override
	public void onChange() {
		this.refreshFlag = false;
	}
}

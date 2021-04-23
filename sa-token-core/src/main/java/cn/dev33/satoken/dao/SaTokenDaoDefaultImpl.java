package cn.dev33.satoken.dao;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * sa-token持久层默认的实现类 , 基于内存Map 
 * @author kong
 *
 */
public class SaTokenDaoDefaultImpl implements SaTokenDao {
	

	/**
	 * 数据集合 
	 */
	public Map<String, Object> dataMap = new ConcurrentHashMap<String, Object>();

	/**
	 * 过期时间集合 (单位: 毫秒) , 记录所有key的到期时间 [注意不是剩余存活时间] 
	 */
	public Map<String, Long> expireMap = new ConcurrentHashMap<String, Long>();
	
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
		expireMap.put(key, System.currentTimeMillis() + timeout * 1000);
	}

	
	// ------------------------ Object 读写操作 
	
	@Override
	public Object getObject(String key) {
		clearKeyByTimeout(key);
		return dataMap.get(key);
	}

	@Override
	public void setObject(String key, Object object, long timeout) {
		dataMap.put(key, object);
		expireMap.put(key, (timeout == SaTokenDao.NEVER_EXPIRE) ? (SaTokenDao.NEVER_EXPIRE) : (System.currentTimeMillis() + timeout * 1000));
	}

	@Override
	public void updateObject(String key, Object object) {
		if(getKeyTimeout(key) == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		// 无动作 
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
		expireMap.put(key, System.currentTimeMillis() + timeout * 1000);
	}
	
	
	// ------------------------ Session 读写操作 
	// 使用接口默认实现 
	

	// ------------------------ 过期时间相关操作 

	/**
	 * 如果指定key已经过期，则立即清除它 
	 * @param key 指定key 
	 */
	void clearKeyByTimeout(String key) {
		Long expirationTime = expireMap.get(key);
		// 清除条件：如果不为空 && 不是[永不过期] && 已经超过过期时间 
		if(expirationTime != null && expirationTime != SaTokenDao.NEVER_EXPIRE && expirationTime < System.currentTimeMillis()) {
			dataMap.remove(key);
			expireMap.remove(key);
		}
	}

	/**
	 * 获取指定key的剩余存活时间 (单位：秒)
	 */
	long getKeyTimeout(String key) {
		// 先检查是否已经过期
		clearKeyByTimeout(key);
		// 获取过期时间 
		Long expire = expireMap.get(key);
		// 如果根本没有这个值 
		if(expire == null) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}
		// 如果被标注为永不过期 
		if(expire == SaTokenDao.NEVER_EXPIRE) {
			return SaTokenDao.NEVER_EXPIRE;
		}
		// ---- 计算剩余时间并返回 
		long timeout = (expire - System.currentTimeMillis()) / 1000;
		// 小于零时，视为不存在 
		if(timeout < 0) {
			dataMap.remove(key);
			expireMap.remove(key);
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}
		return timeout;
	}
	
	
	// --------------------- 定时清理过期数据  
	
	/**
	 * 执行数据清理的线程
	 */
	public Thread refreshThread;
	
	/**
	 * 是否继续执行数据清理的线程标记
	 */
	public boolean refreshFlag;
	

	/**
	 * 清理所有已经过期的key 
	 */
	public void refreshDataMap() {
		Iterator<String> keys = expireMap.keySet().iterator();
		while (keys.hasNext()) {
			clearKeyByTimeout(keys.next());
		}
	}
	
	/**
	 * 初始化定时任务 
	 */
	public void initRefreshThread() {

		// 如果配置了<=0的值，则不启动定时清理
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
						if(refreshFlag == false) {
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
					Thread.sleep(dataRefreshPeriod * 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		refreshThread.start();
	}
	
	/**
	 * 结束定时任务
	 */
	public void endRefreshThread() {
		this.refreshFlag = false;
	}


	

	// --------------------- 会话管理 
	
	@Override
	public List<String> searchData(String prefix, String keyword, int start, int size) {
		return SaFoxUtil.searchList(expireMap.keySet(), prefix, keyword, start, size);
	}


	


	
	
	
}

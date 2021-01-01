package cn.dev33.satoken.dao;


import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.util.SaTaskUtil;
import cn.dev33.satoken.util.SaTaskUtil.FunctionRunClass;

/**
 * sa-token持久层默认的实现类 , 基于内存Map 
 * @author kong
 *
 */
public class SaTokenDaoDefaultImpl implements SaTokenDao {
	

	/**
	 * 所有数据集合 
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
		initRefreshTimer();
	}
	
	
	// ------------------------ String 读写操作 
	
	@Override
	public String getValue(String key) {
		clearKeyByTimeout(key);
		return (String)dataMap.get(key);
	}

	@Override
	public void setValue(String key, String value, long timeout) {
		dataMap.put(key, value);
		expireMap.put(key, (timeout == SaTokenDao.NEVER_EXPIRE) ? (SaTokenDao.NEVER_EXPIRE) : (System.currentTimeMillis() + timeout * 1000));
	}

	@Override
	public void updateValue(String key, String value) {
		if(getKeyTimeout(key) == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		dataMap.put(key, value);
	}

	@Override
	public void deleteKey(String key) {
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
	
	
	// ------------------------ Session 读写操作 
	
	@Override
	public SaSession getSession(String sessionId) {
		clearKeyByTimeout(sessionId);
		return (SaSession)dataMap.get(sessionId);
	}

	@Override
	public void saveSession(SaSession session, long timeout) {
		dataMap.put(session.getId(), session);
		expireMap.put(session.getId(), (timeout == SaTokenDao.NEVER_EXPIRE) ? (SaTokenDao.NEVER_EXPIRE) : (System.currentTimeMillis() + timeout * 1000));
	}

	@Override
	public void updateSession(SaSession session) {
		if(getKeyTimeout(session.getId()) == SaTokenDao.NOT_VALUE_EXPIRE) {
			return;
		}
		// 无动作 
	}

	@Override
	public void deleteSession(String sessionId) {
		dataMap.remove(sessionId);
		expireMap.remove(sessionId);
	}
	
	@Override
	public long getSessionTimeout(String sessionId) {
		return getKeyTimeout(sessionId);
	}
	
	@Override
	public void updateSessionTimeout(String sessionId, long timeout) {
		expireMap.put(sessionId, System.currentTimeMillis() + timeout * 1000);
	}
	

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
	 * 定时任务对象 
	 */
	public Timer refreshTimer;

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
	public void initRefreshTimer() {
		// 如果已经被初始化过了, 则停止它
		if(this.refreshTimer != null) {
			this.endRefreshTimer();
		}
		
		// 开始新的定时任务
		if(SaTokenManager.getConfig().getDataRefreshPeriod() < 0) {
			return;
		}
		int period = SaTokenManager.getConfig().getDataRefreshPeriod() * 1000;
		this.refreshTimer = SaTaskUtil.setInterval(new FunctionRunClass() {
			@Override
			public void run() {
				refreshDataMap(); 
			}
		}, period, period);
	}
	
	/**
	 * 结束定时任务
	 */
	public void endRefreshTimer() {
		this.refreshTimer.cancel();
	}


	


	
	
	
}

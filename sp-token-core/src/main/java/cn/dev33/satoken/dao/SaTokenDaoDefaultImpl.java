package cn.dev33.satoken.dao;

import java.util.HashMap;
import java.util.Map;

import cn.dev33.satoken.session.SaSession;

/**
 * sa-token持久层默认的实现类 , 基于内存Map 
 * @author kong
 *
 */
public class SaTokenDaoDefaultImpl implements SaTokenDao {

	/**
	 * 所有数据集合 
	 */
	Map<String, Object> dataMap = new HashMap<String, Object>();

	
	@Override
	public String getValue(String key) {
		return (String)dataMap.get(key);
	}

	@Override
	public void setValue(String key, String value, long timeout) {
		dataMap.put(key, value);
	}

	@Override
	public void updateValue(String key, String value) {
		this.setValue(key, value, 0);
	}

	@Override
	public void delKey(String key) {
		dataMap.remove(key);
	}

	
	@Override
	public SaSession getSaSession(String sessionId) {
		return (SaSession)dataMap.get(sessionId);
	}

	@Override
	public void saveSaSession(SaSession session, long timeout) {
		dataMap.put(session.getId(), session);
	}

	@Override
	public void updateSaSession(SaSession session) {
		// 无动作 
	}

	@Override
	public void deleteSaSession(String sessionId) {
		dataMap.remove(sessionId);
	}
	
	

	
	
	
	
	
}

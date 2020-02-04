package cn.dev33.satoken.session;

import cn.dev33.satoken.SaTokenManager;

/**
 * sa-session工具类  
 * @author kong
 *
 */
public class SaSessionUtil {

	// 添加上指定前缀，防止恶意伪造session  
	public static String session_key = "custom";
	public static String getSessionKey(String sessionId) {
		return SaTokenManager.getConfig().getTokenName() + ":" + session_key + ":session:" + sessionId;
	}
	
	/** 指定key的session是否存在  */
	public boolean isExists(String sessionId) {
		return SaTokenManager.getDao().getSaSession(getSessionKey(sessionId)) != null;
	}
	
	/** 获取指定key的session, 如果没有，is_create=是否新建并返回  */
	public static SaSession getSessionById(String sessionId, boolean is_create) {
		SaSession session = SaTokenManager.getDao().getSaSession(getSessionKey(sessionId));
		if(session == null && is_create) {
			session = new SaSession(getSessionKey(sessionId));
			SaTokenManager.getDao().saveSaSession(session, SaTokenManager.getConfig().getTimeout());
		}
		return session;
	}
	/** 获取指定key的session, 如果没有则新建并返回  */
	public static SaSession getSessionById(String sessionId) {
		return getSessionById(sessionId, true);
	}

	/** 删除指定key的session  */
	public static void delSessionById(String sessionId) {
		SaTokenManager.getDao().delSaSession(getSessionKey(sessionId));
	}
	
	
	
}

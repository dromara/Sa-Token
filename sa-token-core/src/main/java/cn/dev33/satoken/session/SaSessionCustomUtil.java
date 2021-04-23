package cn.dev33.satoken.session;

import cn.dev33.satoken.SaManager;

/**
 * 自定义Session工具类
 * 
 * @author kong
 *
 */
public class SaSessionCustomUtil {

	/**
	 * 添加上指定前缀，防止恶意伪造session
	 */
	public static String sessionKey = "custom";

	/**
	 * 组织一下自定义Session的id
	 * 
	 * @param sessionId 会话id
	 * @return sessionId
	 */
	public static String splicingSessionKey(String sessionId) {
		return SaManager.getConfig().getTokenName() + ":" + sessionKey + ":session:" + sessionId;
	}

	/**
	 * 验证指定key的Session是否存在
	 * 
	 * @param sessionId session的id
	 * @return 是否存在
	 */
	public static boolean isExists(String sessionId) {
		return SaManager.getSaTokenDao().getSession(splicingSessionKey(sessionId)) != null;
	}

	/**
	 * 获取指定key的Session
	 * 
	 * @param sessionId key
	 * @param isCreate  如果此Session尚未在DB创建，是否新建并返回
	 * @return SaSession
	 */
	public static SaSession getSessionById(String sessionId, boolean isCreate) {
		SaSession session = SaManager.getSaTokenDao().getSession(splicingSessionKey(sessionId));
		if (session == null && isCreate) {
			session = SaManager.getSaTokenAction().createSession(sessionId);
			SaManager.getSaTokenDao().setSession(session, SaManager.getConfig().getTimeout());
		}
		return session;
	}

	/**
	 * 获取指定key的Session, 如果此Session尚未在DB创建，则新建并返回
	 * 
	 * @param sessionId key
	 * @return session对象
	 */
	public static SaSession getSessionById(String sessionId) {
		return getSessionById(sessionId, true);
	}

	/**
	 * 删除指定key的session
	 * 
	 * @param sessionId 指定key
	 */
	public static void deleteSessionById(String sessionId) {
		SaManager.getSaTokenDao().deleteSession(splicingSessionKey(sessionId));
	}

}

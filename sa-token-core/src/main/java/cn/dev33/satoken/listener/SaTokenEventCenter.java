package cn.dev33.satoken.listener;

import java.util.ArrayList;
import java.util.List;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.SaLoginModel;

/**
 * Sa-Token 事件中心 事件发布器
 * 
 * @author kong
 * @since: 2022-8-19
 */
public class SaTokenEventCenter {

	// --------- 注册侦听器 
	
	private static List<SaTokenListener> listenerList = new ArrayList<>();
	
	static {
		// 默认添加控制台日志侦听器 
		listenerList.add(new SaTokenListenerForConsolePrint());
	}

	/**
	 * 获取已注册的所有侦听器
	 * @return / 
	 */
	public static List<SaTokenListener> getListenerList() {
		return listenerList;
	}

	/**
	 * 重置侦听器集合
	 * @param listenerList / 
	 */
	public static void setListenerList(List<SaTokenListener> listenerList) {
		if(listenerList == null) {
			throw new SaTokenException("重置的侦听器集合不可以为空");
		}
		SaTokenEventCenter.listenerList = listenerList;
	}

	/**
	 * 注册一个侦听器 
	 * @param listener / 
	 */
	public static void registerListener(SaTokenListener listener) {
		if(listener == null) {
			throw new SaTokenException("注册的侦听器不可以为空");
		}
		listenerList.add(listener);
	}

	/**
	 * 注册一组侦听器 
	 * @param listenerList / 
	 */
	public static void registerListenerList(List<SaTokenListener> listenerList) {
		if(listenerList == null) {
			throw new SaTokenException("注册的侦听器不可以为空");
		}
		for (SaTokenListener listener : listenerList) {
			if(listener == null) {
				throw new SaTokenException("注册的侦听器不可以为空");
			}
		}
		SaTokenEventCenter.listenerList.addAll(listenerList);
	}

	/**
	 * 移除一个侦听器 
	 * @param listener / 
	 */
	public static void removeListener(SaTokenListener listener) {
		listenerList.remove(listener);
	}

	/**
	 * 移除指定类型的所有侦听器 
	 * @param cls / 
	 */
	public static void removeListener(Class<? extends SaTokenListener> cls) {
		ArrayList<SaTokenListener> listenerListCopy = new ArrayList<>(listenerList);
		for (SaTokenListener listener : listenerListCopy) {
			if(cls.isAssignableFrom(listener.getClass())) {
				listenerList.remove(listener);
			}
		}
	}

	/**
	 * 清空所有已注册的侦听器 
	 */
	public static void clearListener() {
		listenerList.clear();
	}

	/**
	 * 判断是否已经注册了指定侦听器 
	 * @param listener / 
	 * @return / 
	 */
	public static boolean hasListener(SaTokenListener listener) {
		return listenerList.contains(listener);
	}

	/**
	 * 判断是否已经注册了指定类型的侦听器 
	 * @param cls / 
	 * @return / 
	 */
	public static boolean hasListener(Class<? extends SaTokenListener> cls) {
		for (SaTokenListener listener : listenerList) {
			if(cls.isAssignableFrom(listener.getClass())) {
				return true;
			}
		}
		return false;
	}
	
	
	// --------- 事件发布 
	
	/**
	 * 每次登录时触发 
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param tokenValue 本次登录产生的 token 值 
	 * @param loginModel 登录参数
	 */
	public static void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
		for (SaTokenListener listener : listenerList) {
			listener.doLogin(loginType, loginId, tokenValue, loginModel);
		}
	}
			
	/**
	 * 每次注销时触发 
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param tokenValue token值
	 */
	public static void doLogout(String loginType, Object loginId, String tokenValue) {
		for (SaTokenListener listener : listenerList) {
			listener.doLogout(loginType, loginId, tokenValue);
		}
	}
	
	/**
	 * 每次被踢下线时触发 
	 * @param loginType 账号类别 
	 * @param loginId 账号id 
	 * @param tokenValue token值 
	 */
	public static void doKickout(String loginType, Object loginId, String tokenValue) {
		for (SaTokenListener listener : listenerList) {
			listener.doKickout(loginType, loginId, tokenValue);
		}
	}

	/**
	 * 每次被顶下线时触发
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param tokenValue token值
	 */
	public static void doReplaced(String loginType, Object loginId, String tokenValue) {
		for (SaTokenListener listener : listenerList) {
			listener.doReplaced(loginType, loginId, tokenValue);
		}
	}

	/**
	 * 每次被封禁时触发
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param service 指定服务 
	 * @param disableTime 封禁时长，单位: 秒
	 */
	public static void doDisable(String loginType, Object loginId, String service, long disableTime) {
		for (SaTokenListener listener : listenerList) {
			listener.doDisable(loginType, loginId, service, disableTime);
		}
	}
	
	/**
	 * 每次被解封时触发
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param service 指定服务 
	 */
	public static void doUntieDisable(String loginType, Object loginId, String service) {
		for (SaTokenListener listener : listenerList) {
			listener.doUntieDisable(loginType, loginId, service);
		}
	}
	
	/**
	 * 每次创建Session时触发
	 * @param id SessionId
	 */
	public static void doCreateSession(String id) {
		for (SaTokenListener listener : listenerList) {
			listener.doCreateSession(id);
		}
	}
	
	/**
	 * 每次注销Session时触发
	 * @param id SessionId
	 */
	public static void doLogoutSession(String id) {
		for (SaTokenListener listener : listenerList) {
			listener.doLogoutSession(id);
		}
	}

	/**
	 * 每次Token续期时触发
	 * 
	 * @param tokenValue token 值 
	 * @param loginId 账号id 
	 * @param timeout 续期时间 
	 */
	public static void doRenewTimeout(String tokenValue,  Object loginId, long timeout) {
		for (SaTokenListener listener : listenerList) {
			listener.doRenewTimeout(tokenValue, loginId, timeout);
		}
	}

}

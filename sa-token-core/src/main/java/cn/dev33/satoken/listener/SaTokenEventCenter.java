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
package cn.dev33.satoken.listener;

import java.util.ArrayList;
import java.util.List;

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.SaLoginParameter;
import cn.dev33.satoken.stp.StpLogic;

/**
 * Sa-Token 事件中心 事件发布器
 *
 * <p> 提供侦听器注册、事件发布能力 </p>
 * 
 * @author click33
 * @since 1.31.0
 */
public class SaTokenEventCenter {

	// --------- 注册侦听器 
	
	private static List<SaTokenListener> listenerList = new ArrayList<>();
	
	static {
		// 默认添加控制台日志侦听器 
		listenerList.add(new SaTokenListenerForLog());
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
			throw new SaTokenException("重置的侦听器集合不可以为空").setCode(SaErrorCode.CODE_10031);
		}
		SaTokenEventCenter.listenerList = listenerList;
	}

	/**
	 * 注册一个侦听器 
	 * @param listener / 
	 */
	public static void registerListener(SaTokenListener listener) {
		if(listener == null) {
			throw new SaTokenException("注册的侦听器不可以为空").setCode(SaErrorCode.CODE_10032);
		}
		listenerList.add(listener);
	}

	/**
	 * 注册一组侦听器 
	 * @param listenerList / 
	 */
	public static void registerListenerList(List<SaTokenListener> listenerList) {
		if(listenerList == null) {
			throw new SaTokenException("注册的侦听器集合不可以为空").setCode(SaErrorCode.CODE_10031);
		}
		for (SaTokenListener listener : listenerList) {
			if(listener == null) {
				throw new SaTokenException("注册的侦听器不可以为空").setCode(SaErrorCode.CODE_10032);
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
	 * 事件发布：xx 账号登录
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param tokenValue 本次登录产生的 token 值 
	 * @param loginParameter 登录参数
	 */
	public static void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
		for (SaTokenListener listener : listenerList) {
			listener.doLogin(loginType, loginId, tokenValue, loginParameter);
		}
	}
			
	/**
	 * 事件发布：xx 账号注销
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
	 * 事件发布：xx 账号被踢下线
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
	 * 事件发布：xx 账号被顶下线
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
	 * 事件发布：xx 账号被封禁
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param service 指定服务 
	 * @param level 封禁等级 
	 * @param disableTime 封禁时长，单位: 秒
	 */
	public static void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
		for (SaTokenListener listener : listenerList) {
			listener.doDisable(loginType, loginId, service, level, disableTime);
		}
	}
	
	/**
	 * 事件发布：xx 账号被解封
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
	 * 事件发布：xx 账号完成二级认证
	 * @param loginType 账号类别
	 * @param tokenValue token值
	 * @param service 指定服务 
	 * @param safeTime 认证时间，单位：秒 
	 */
	public static void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
		for (SaTokenListener listener : listenerList) {
			listener.doOpenSafe(loginType, tokenValue, service, safeTime);
		}
	}

	/**
	 * 事件发布：xx 账号关闭二级认证
	 * @param loginType 账号类别
	 * @param service 指定服务 
	 * @param tokenValue token值
	 */
	public static void doCloseSafe(String loginType, String tokenValue, String service) {
		for (SaTokenListener listener : listenerList) {
			listener.doCloseSafe(loginType, tokenValue, service);
		}
	}

	/**
	 * 事件发布：创建了一个新的 SaSession
	 * @param id SessionId
	 */
	public static void doCreateSession(String id) {
		for (SaTokenListener listener : listenerList) {
			listener.doCreateSession(id);
		}
	}
	
	/**
	 * 事件发布：一个 SaSession 注销了
	 * @param id SessionId
	 */
	public static void doLogoutSession(String id) {
		for (SaTokenListener listener : listenerList) {
			listener.doLogoutSession(id);
		}
	}

	/**
	 * 事件发布：指定 Token 续期成功
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

	/**
	 * 事件发布：有新的全局组件载入到框架中
	 * @param compName 组件名称
	 * @param compObj 组件对象
	 */
	public static void doRegisterComponent(String compName, Object compObj) {
		for (SaTokenListener listener : listenerList) {
			listener.doRegisterComponent(compName, compObj);
		}
	}

	/**
	 * 事件发布：有新的注解处理器载入到框架中
	 * @param handler 注解处理器
	 */
	public static void doRegisterAnnotationHandler(SaAnnotationHandlerInterface<?> handler) {
		for (SaTokenListener listener : listenerList) {
			listener.doRegisterAnnotationHandler(handler);
		}
	}

	/**
	 * 事件发布：有新的 StpLogic 载入到框架中
	 * @param stpLogic / 
	 */
	public static void doSetStpLogic(StpLogic stpLogic) {
		for (SaTokenListener listener : listenerList) {
			listener.doSetStpLogic(stpLogic);
		}
	}

	/**
	 * 事件发布：有新的全局配置载入到框架中
	 * @param config / 
	 */
	public static void doSetConfig(SaTokenConfig config) {
		for (SaTokenListener listener : listenerList) {
			listener.doSetConfig(config);
		}
	}

}

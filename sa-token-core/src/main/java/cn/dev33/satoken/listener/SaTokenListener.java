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

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpLogic;

/**
 * Sa-Token 侦听器
 *
 * <p> 你可以通过实现此接口在用户登录、退出等关键性操作时进行一些AOP切面操作 </p>
 *
 * @author click33
 * @since <= 1.34.0
 */
public interface SaTokenListener {

	/**
	 * 每次登录时触发 
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param tokenValue 本次登录产生的 token 值 
	 * @param loginModel 登录参数
	 */
	void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel);
			
	/**
	 * 每次注销时触发 
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param tokenValue token值
	 */
	void doLogout(String loginType, Object loginId, String tokenValue);
	
	/**
	 * 每次被踢下线时触发 
	 * @param loginType 账号类别 
	 * @param loginId 账号id 
	 * @param tokenValue token值 
	 */
	void doKickout(String loginType, Object loginId, String tokenValue);

	/**
	 * 每次被顶下线时触发
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param tokenValue token值
	 */
	void doReplaced(String loginType, Object loginId, String tokenValue);

	/**
	 * 每次被封禁时触发
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param service 指定服务 
	 * @param level 封禁等级 
	 * @param disableTime 封禁时长，单位: 秒
	 */
	void doDisable(String loginType, Object loginId, String service, int level, long disableTime);
	
	/**
	 * 每次被解封时触发
	 * @param loginType 账号类别
	 * @param loginId 账号id
	 * @param service 指定服务 
	 */
	void doUntieDisable(String loginType, Object loginId, String service);

	/**
	 * 每次打开二级认证时触发
	 * @param loginType 账号类别
	 * @param tokenValue token值
	 * @param service 指定服务 
	 * @param safeTime 认证时间，单位：秒 
	 */
	void doOpenSafe(String loginType, String tokenValue, String service, long safeTime);

	/**
	 * 每次关闭二级认证时触发
	 * @param loginType 账号类别
	 * @param tokenValue token值
	 * @param service 指定服务 
	 */
	void doCloseSafe(String loginType, String tokenValue, String service);

	/**
	 * 每次创建 SaSession 时触发
	 * @param id SessionId
	 */
	void doCreateSession(String id);
	
	/**
	 * 每次注销 SaSession 时触发
	 * @param id SessionId
	 */
	void doLogoutSession(String id);

	/**
	 * 每次 Token 续期时触发（注意：是 timeout 续期，而不是 activity-timeout 续期）
	 * 
	 * @param tokenValue token 值 
	 * @param loginId 账号id 
	 * @param timeout 续期时间 
	 */
	void doRenewTimeout(String tokenValue,  Object loginId, long timeout);

	/**
	 * 全局组件载入 
	 * @param compName 组件名称
	 * @param compObj 组件对象
	 */
	default void doRegisterComponent(String compName, Object compObj) {}

	/**
	 * StpLogic 对象替换 
	 * @param stpLogic / 
	 */
	default void doSetStpLogic(StpLogic stpLogic) {}

	/**
	 * 载入全局配置 
	 * @param config / 
	 */
	default void doSetConfig(SaTokenConfig config) {}

}

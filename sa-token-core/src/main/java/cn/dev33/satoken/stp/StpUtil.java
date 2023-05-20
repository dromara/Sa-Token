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
package cn.dev33.satoken.stp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.session.SaSession;

import java.util.List;

/**
 * Sa-Token 权限认证工具类
 *
 * @author click33
 * @since <= 1.34.0
 */
public class StpUtil {
	
	private StpUtil() {}
	
	/**
	 * 多账号体系下的类型标识
	 */
	public static final String TYPE = "login";
	
	/**
	 * 底层使用的 StpLogic 对象
	 */
	public static StpLogic stpLogic = SaManager.getStpLogic(TYPE);

	/**
	 * 获取当前 StpLogic 的账号类型
	 *
	 * @return /
	 */
	public static String getLoginType(){
		return stpLogic.getLoginType();
	}

	/**
	 * 安全的重置 StpLogic 对象
	 *
	 * <br> 1、更改此账户的 StpLogic 对象 
	 * <br> 2、put 到全局 StpLogic 集合中 
	 * <br> 3、发送日志 
	 * 
	 * @param newStpLogic / 
	 */
	public static void setStpLogic(StpLogic newStpLogic) {
		// 1、重置此账户的 StpLogic 对象
		stpLogic = newStpLogic;
		
		// 2、添加到全局 StpLogic 集合中
		//    以便可以通过 SaManager.getStpLogic(type) 的方式来全局获取到这个 StpLogic
		SaManager.putStpLogic(newStpLogic);
		
		// 3、$$ 发布事件：更新了 stpLogic 对象
		SaTokenEventCenter.doSetStpLogic(stpLogic);
	}

	/**
	 * 获取 StpLogic 对象
	 *
	 * @return / 
	 */
	public static StpLogic getStpLogic() {
		return stpLogic;
	}
	
	
	// ------------------- 获取 token 相关 -------------------

	/**
	 * 返回 token 名称，此名称在以下地方体现：Cookie 保存 token 时的名称、提交 token 时参数的名称、存储 token 时的 key 前缀
	 *
	 * @return /
	 */
	public static String getTokenName() {
 		return stpLogic.getTokenName();
 	}

	/**
	 * 在当前会话写入指定 token 值
	 *
	 * @param tokenValue token 值
	 */
	public static void setTokenValue(String tokenValue){
		stpLogic.setTokenValue(tokenValue);
	}

	/**
	 * 在当前会话写入指定 token 值
	 *
	 * @param tokenValue token 值
	 * @param cookieTimeout Cookie存活时间(秒)
	 */
	public static void setTokenValue(String tokenValue, int cookieTimeout){
		stpLogic.setTokenValue(tokenValue, cookieTimeout);
	}

	/**
	 * 在当前会话写入指定 token 值
	 *
	 * @param tokenValue token 值
	 * @param loginModel 登录参数
	 */
	public static void setTokenValue(String tokenValue, SaLoginModel loginModel){
		stpLogic.setTokenValue(tokenValue, loginModel);
	}

	/**
	 * 获取当前请求的 token 值
	 *
	 * @return 当前tokenValue
	 */
	public static String getTokenValue() {
		return stpLogic.getTokenValue();
	}

	/**
	 * 获取当前请求的 token 值 （不裁剪前缀）
	 *
	 * @return / 
	 */
	public static String getTokenValueNotCut(){
		return stpLogic.getTokenValueNotCut();
	}

	/**
	 * 获取当前会话的 token 参数信息
	 *
	 * @return token 参数信息
	 */
	public static SaTokenInfo getTokenInfo() {
		return stpLogic.getTokenInfo();
	}

	
	// ------------------- 登录相关操作 -------------------

	// --- 登录 

	/**
	 * 会话登录
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 */
	public static void login(Object id) {
		stpLogic.login(id);
	}

	/**
	 * 会话登录，并指定登录设备类型
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param device 设备类型
	 */
	public static void login(Object id, String device) {
		stpLogic.login(id, device);
	}

	/**
	 * 会话登录，并指定是否 [记住我]
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param isLastingCookie 是否为持久Cookie，值为 true 时记住我，值为 false 时关闭浏览器需要重新登录
	 */
	public static void login(Object id, boolean isLastingCookie) {
		stpLogic.login(id, isLastingCookie);
	}

	/**
	 * 会话登录，并指定此次登录 token 的有效期, 单位:秒
	 *
	 * @param id      账号id，建议的类型：（long | int | String）
	 * @param timeout 此次登录 token 的有效期, 单位:秒
	 */
	public static void login(Object id, long timeout) {
		stpLogic.login(id, timeout);
	}

	/**
	 * 会话登录，并指定所有登录参数 Model
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param loginModel 此次登录的参数Model
	 */
	public static void login(Object id, SaLoginModel loginModel) {
		stpLogic.login(id, loginModel);
	}

	/**
	 * 创建指定账号 id 的登录会话数据
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @return 返回会话令牌
	 */
	public static String createLoginSession(Object id) {
		return stpLogic.createLoginSession(id);
	}

	/**
	 * 创建指定账号 id 的登录会话数据
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param loginModel 此次登录的参数Model 
	 * @return 返回会话令牌
	 */
	public static String createLoginSession(Object id, SaLoginModel loginModel) {
		return stpLogic.createLoginSession(id, loginModel);
	}
	
	// --- 注销 

	/**
	 * 在当前客户端会话注销
	 */
	public static void logout() {
		stpLogic.logout();
	}

	/**
	 * 会话注销，根据账号id 
	 *
	 * @param loginId 账号id
	 */
	public static void logout(Object loginId) {
		stpLogic.logout(loginId);
	}

	/**
	 * 会话注销，根据账号id 和 设备类型
	 *
	 * @param loginId 账号id 
	 * @param device 设备类型 (填 null 代表注销该账号的所有设备类型)
	 */
	public static void logout(Object loginId, String device) {
		stpLogic.logout(loginId, device);
	}

	/**
	 * 会话注销，根据指定 Token 
	 *
	 * @param tokenValue 指定 token
	 */
	public static void logoutByTokenValue(String tokenValue) {
		stpLogic.logoutByTokenValue(tokenValue);
	}

	/**
	 * 踢人下线，根据账号id 
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-5 </p>
	 *
	 * @param loginId 账号id 
	 */
	public static void kickout(Object loginId) {
		stpLogic.kickout(loginId);
	}

	/**
	 * 踢人下线，根据账号id 和 设备类型
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-5 </p>
	 *
	 * @param loginId 账号id
	 * @param device 设备类型 (填 null 代表踢出该账号的所有设备类型)
	 */
	public static void kickout(Object loginId, String device) {
		stpLogic.kickout(loginId, device);
	}

	/**
	 * 踢人下线，根据指定 token
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-5 </p>
	 *
	 * @param tokenValue 指定 token
	 */
	public static void kickoutByTokenValue(String tokenValue) {
		stpLogic.kickoutByTokenValue(tokenValue);
	}

	/**
	 * 顶人下线，根据账号id 和 设备类型
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-4 </p>
	 *
	 * @param loginId 账号id
	 * @param device 设备类型 （填 null 代表顶替该账号的所有设备类型）
	 */
	public static void replaced(Object loginId, String device) {
		stpLogic.replaced(loginId, device);
	}

	// 会话查询

	/**
	 * 判断当前会话是否已经登录
	 *
	 * @return 已登录返回 true，未登录返回 false
	 */
	public static boolean isLogin() {
		return stpLogic.isLogin();
	}

	/**
	 * 检验当前会话是否已经登录，如未登录，则抛出异常
	 */
 	public static void checkLogin() {
 		stpLogic.checkLogin();
 	}

	/**
	 * 获取当前会话账号id，如果未登录，则抛出异常
	 *
	 * @return 账号id
	 */
	public static Object getLoginId() {
		return stpLogic.getLoginId();
	}

	/**
	 * 获取当前会话账号id, 如果未登录，则返回默认值
	 *
	 * @param <T> 返回类型 
	 * @param defaultValue 默认值
	 * @return 登录id
	 */
	public static <T> T getLoginId(T defaultValue) {
		return stpLogic.getLoginId(defaultValue);
	}

	/**
	 * 获取当前会话账号id, 如果未登录，则返回null
	 *
	 * @return 账号id
	 */
	public static Object getLoginIdDefaultNull() {
		return stpLogic.getLoginIdDefaultNull();
 	}

	/**
	 * 获取当前会话账号id, 并转换为 String 类型
	 *
	 * @return 账号id
	 */
	public static String getLoginIdAsString() {
		return stpLogic.getLoginIdAsString();
	}

	/**
	 * 获取当前会话账号id, 并转换为 int 类型
	 *
	 * @return 账号id
	 */
	public static int getLoginIdAsInt() {
		return stpLogic.getLoginIdAsInt();
	}

	/**
	 * 获取当前会话账号id, 并转换为 long 类型
	 *
	 * @return 账号id
	 */
	public static long getLoginIdAsLong() {
		return stpLogic.getLoginIdAsLong();
	}

	/**
	 * 获取指定 token 对应的账号id，如果未登录，则返回 null
	 *
	 * @param tokenValue token
	 * @return 账号id
	 */
 	public static Object getLoginIdByToken(String tokenValue) {
 		return stpLogic.getLoginIdByToken(tokenValue);
 	}

	/**
	 * 获取当前 Token 的扩展信息（此函数只在jwt模式下生效）
	 *
	 * @param key 键值 
	 * @return 对应的扩展数据
	 */
	public static Object getExtra(String key) {
		return stpLogic.getExtra(key);
	}

	/**
	 * 获取指定 Token 的扩展信息（此函数只在jwt模式下生效）
	 *
	 * @param tokenValue 指定的 Token 值
	 * @param key 键值
	 * @return 对应的扩展数据
	 */
	public static Object getExtra(String tokenValue, String key) {
		return stpLogic.getExtra(tokenValue, key);
	}
 	
 	
	// ------------------- Account-Session 相关 -------------------

	/**
	 * 获取指定账号 id 的 Account-Session, 如果该 SaSession 尚未创建，isCreate=是否新建并返回
	 *
	 * @param loginId 账号id
	 * @param isCreate 是否新建
	 * @return SaSession 对象
	 */
	public static SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
		return stpLogic.getSessionByLoginId(loginId, isCreate);
	}

	/**
	 * 获取指定 key 的 SaSession, 如果该 SaSession 尚未创建，则返回 null
	 *
	 * @param sessionId SessionId
	 * @return Session对象
	 */
	public static SaSession getSessionBySessionId(String sessionId) {
		return stpLogic.getSessionBySessionId(sessionId);
	}

	/**
	 * 获取指定账号 id 的 Account-Session，如果该 SaSession 尚未创建，则新建并返回
	 *
	 * @param loginId 账号id
	 * @return SaSession 对象
	 */
	public static SaSession getSessionByLoginId(Object loginId) {
		return stpLogic.getSessionByLoginId(loginId);
	}

	/**
	 * 获取当前已登录账号的 Account-Session, 如果该 SaSession 尚未创建，isCreate=是否新建并返回
	 *
	 * @param isCreate 是否新建 
	 * @return Session对象
	 */
	public static SaSession getSession(boolean isCreate) {
		return stpLogic.getSession(isCreate);
	}

	/**
	 * 获取当前已登录账号的 Account-Session，如果该 SaSession 尚未创建，则新建并返回
	 *
	 * @return Session对象
	 */
	public static SaSession getSession() {
		return stpLogic.getSession();
	}

	
	// ------------------- Token-Session 相关 -------------------  

	/**
	 * 获取指定 token 的 Token-Session，如果该 SaSession 尚未创建，则新建并返回
	 *
	 * @param tokenValue Token值
	 * @return Session对象
	 */
	public static SaSession getTokenSessionByToken(String tokenValue) {
		return stpLogic.getTokenSessionByToken(tokenValue);
	}

	/**
	 * 获取当前 token 的 Token-Session，如果该 SaSession 尚未创建，则新建并返回
	 *
	 * @return Session对象
	 */
	public static SaSession getTokenSession() {
		return stpLogic.getTokenSession();
	}

	/**
	 * 获取当前匿名 Token-Session （可在未登录情况下使用的Token-Session）
	 *
	 * @return Token-Session 对象
	 */
	public static SaSession getAnonTokenSession() {
		return stpLogic.getAnonTokenSession();
	}
	

	// ------------------- Activity-Timeout token 最低活跃度 验证相关 -------------------

	/**
	 * 检查当前 token 是否已被冻结，如果是则抛出异常
	 */
 	public static void checkActivityTimeout() {
 		stpLogic.checkActivityTimeout();
 	}

	/**
	 * 续签当前 token：(将 [最后操作时间] 更新为当前时间戳)
	 * <h2>
	 * 		请注意: 即使 token 已被冻结 也可续签成功，
	 * 		如果此场景下需要提示续签失败，可在此之前调用 checkActivityTimeout() 强制检查是否冻结即可
	 * </h2>
	 */
 	public static void updateLastActivityToNow() {
 		stpLogic.updateLastActivityToNow();
 	}
 	

	// ------------------- 过期时间相关 -------------------  

	/**
	 * 获取当前会话 token 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @return token剩余有效时间
	 */
 	public static long getTokenTimeout() {
 		return stpLogic.getTokenTimeout();
 	}

	/**
	 * 获取指定 token 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @param token 指定token
	 * @return token剩余有效时间
	 */
	public static long getTokenTimeout(String token) {
		return stpLogic.getTokenTimeout(token);
	}

	/**
	 * 获取当前登录账号的 Account-Session 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @return token剩余有效时间
	 */
 	public static long getSessionTimeout() {
 		return stpLogic.getSessionTimeout();
 	}

	/**
	 * 获取当前 token 的 Token-Session 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @return token剩余有效时间
	 */
 	public static long getTokenSessionTimeout() {
 		return stpLogic.getTokenSessionTimeout();
 	}

	/**
	 * 获取当前 token 剩余活跃有效期：当前 token 距离被冻结还剩多少时间（单位: 秒，返回 -1 代表永不冻结，-2 代表没有这个值或 token 已被冻结了）
	 *
	 * @return /
	 */
 	public static long getTokenActivityTimeout() {
 		return stpLogic.getTokenActivityTimeout();
 	}

	/**
	 * 对当前 token 的 timeout 值进行续期
	 *
	 * @param timeout 要修改成为的有效时间 (单位: 秒)
	 */
 	public static void renewTimeout(long timeout) {
 		stpLogic.renewTimeout(timeout);
 	}

	/**
	 * 对指定 token 的 timeout 值进行续期
	 *
	 * @param tokenValue 指定 token
	 * @param timeout 要修改成为的有效时间 (单位: 秒，填 -1 代表要续为永久有效)
	 */
 	public static void renewTimeout(String tokenValue, long timeout) {
 		stpLogic.renewTimeout(tokenValue, timeout);
 	}
 	
 	
	// ------------------- 角色认证操作 -------------------

	/**
	 * 获取：当前账号的角色集合
	 *
	 * @return /
	 */
	public static List<String> getRoleList() {
		return stpLogic.getRoleList();
	}

	/**
	 * 获取：指定账号的角色集合
	 *
	 * @param loginId 指定账号id 
	 * @return /
	 */
	public static List<String> getRoleList(Object loginId) {
		return stpLogic.getRoleList(loginId);
	}

	/**
	 * 判断：当前账号是否拥有指定角色, 返回 true 或 false
	 *
	 * @param role 角色
	 * @return /
	 */
 	public static boolean hasRole(String role) {
 		return stpLogic.hasRole(role);
 	}

	/**
	 * 判断：指定账号是否含有指定角色标识, 返回 true 或 false
	 *
	 * @param loginId 账号id
	 * @param role 角色标识
	 * @return 是否含有指定角色标识
	 */
 	public static boolean hasRole(Object loginId, String role) {
 		return stpLogic.hasRole(loginId, role);
 	}

	/**
	 * 判断：当前账号是否含有指定角色标识 [ 指定多个，必须全部验证通过 ]
	 *
	 * @param roleArray 角色标识数组
	 * @return true或false
	 */
 	public static boolean hasRoleAnd(String... roleArray){
 		return stpLogic.hasRoleAnd(roleArray);
 	}

	/**
	 * 判断：当前账号是否含有指定角色标识 [ 指定多个，只要其一验证通过即可 ]
	 *
	 * @param roleArray 角色标识数组
	 * @return true或false
	 */
 	public static boolean hasRoleOr(String... roleArray){
 		return stpLogic.hasRoleOr(roleArray);
 	}

	/**
	 * 校验：当前账号是否含有指定角色标识, 如果验证未通过，则抛出异常: NotRoleException
	 *
	 * @param role 角色标识
	 */
 	public static void checkRole(String role) {
 		stpLogic.checkRole(role);
 	}

	/**
	 * 校验：当前账号是否含有指定角色标识 [ 指定多个，必须全部验证通过 ]
	 *
	 * @param roleArray 角色标识数组
	 */
 	public static void checkRoleAnd(String... roleArray){
 		stpLogic.checkRoleAnd(roleArray);
 	}

	/**
	 * 校验：当前账号是否含有指定角色标识 [ 指定多个，只要其一验证通过即可 ]
	 *
	 * @param roleArray 角色标识数组
	 */
 	public static void checkRoleOr(String... roleArray){
 		stpLogic.checkRoleOr(roleArray);
 	}

	
	// ------------------- 权限认证操作 -------------------

	/**
	 * 获取：当前账号的权限码集合
	 *
	 * @return / 
	 */
	public static List<String> getPermissionList() {
		return stpLogic.getPermissionList();
	}

	/**
	 * 获取：指定账号的权限码集合
	 *
	 * @param loginId 指定账号id
	 * @return / 
	 */
	public static List<String> getPermissionList(Object loginId) {
		return stpLogic.getPermissionList(loginId);
	}

	/**
	 * 判断：当前账号是否含有指定权限, 返回 true 或 false
	 *
	 * @param permission 权限码
	 * @return 是否含有指定权限
	 */
	public static boolean hasPermission(String permission) {
		return stpLogic.hasPermission(permission);
	}

	/**
	 * 判断：指定账号 id 是否含有指定权限, 返回 true 或 false
	 *
	 * @param loginId 账号 id
	 * @param permission 权限码
	 * @return 是否含有指定权限
	 */
	public static boolean hasPermission(Object loginId, String permission) {
		return stpLogic.hasPermission(loginId, permission);
	}

	/**
	 * 判断：当前账号是否含有指定权限 [ 指定多个，必须全部具有 ]
	 *
	 * @param permissionArray 权限码数组
	 * @return true 或 false
	 */
 	public static boolean hasPermissionAnd(String... permissionArray){
 		return stpLogic.hasPermissionAnd(permissionArray);
 	}

	/**
	 * 判断：当前账号是否含有指定权限 [ 指定多个，只要其一验证通过即可 ]
	 *
	 * @param permissionArray 权限码数组
	 * @return true 或 false
	 */
 	public static boolean hasPermissionOr(String... permissionArray){
 		return stpLogic.hasPermissionOr(permissionArray);
 	}

	/**
	 * 校验：当前账号是否含有指定权限, 如果验证未通过，则抛出异常: NotPermissionException
	 *
	 * @param permission 权限码
	 */
	public static void checkPermission(String permission) {
		stpLogic.checkPermission(permission);
	}

	/**
	 * 校验：当前账号是否含有指定权限 [ 指定多个，必须全部验证通过 ]
	 *
	 * @param permissionArray 权限码数组
	 */
	public static void checkPermissionAnd(String... permissionArray) {
		stpLogic.checkPermissionAnd(permissionArray);
	}

	/**
	 * 校验：当前账号是否含有指定权限 [ 指定多个，只要其一验证通过即可 ]
	 *
	 * @param permissionArray 权限码数组
	 */
	public static void checkPermissionOr(String... permissionArray) {
		stpLogic.checkPermissionOr(permissionArray);
	}


	// ------------------- id 反查 token 相关操作 -------------------

	/**
	 * 获取指定账号 id 的 token
	 * <p>
	 * 		在配置为允许并发登录时，此方法只会返回队列的最后一个 token，
	 * 		如果你需要返回此账号 id 的所有 token，请调用 getTokenValueListByLoginId
	 * </p>
	 *
	 * @param loginId 账号id
	 * @return token值
	 */
	public static String getTokenValueByLoginId(Object loginId) {
		return stpLogic.getTokenValueByLoginId(loginId);
	}

	/**
	 * 获取指定账号 id 指定设备类型端的 token
	 * <p>
	 * 		在配置为允许并发登录时，此方法只会返回队列的最后一个 token，
	 * 		如果你需要返回此账号 id 的所有 token，请调用 getTokenValueListByLoginId
	 * </p>
	 *
	 * @param loginId 账号id
	 * @param device 设备类型，填 null 代表不限设备类型
	 * @return token值
	 */
	public static String getTokenValueByLoginId(Object loginId, String device) {
		return stpLogic.getTokenValueByLoginId(loginId, device);
	}

	/**
	 * 获取指定账号 id 的 token 集合
	 *
	 * @param loginId 账号id
	 * @return 此 loginId 的所有相关 token
	 */
	public static List<String> getTokenValueListByLoginId(Object loginId) {
		return stpLogic.getTokenValueListByLoginId(loginId);
	}

	/**
	 * 获取指定账号 id 指定设备类型端的 token 集合
	 *
	 * @param loginId 账号id
	 * @param device 设备类型，填 null 代表不限设备类型
	 * @return 此 loginId 的所有登录 token
	 */
	public static List<String> getTokenValueListByLoginId(Object loginId, String device) {
		return stpLogic.getTokenValueListByLoginId(loginId, device);
	}

	/**
	 * 返回当前会话的登录设备类型
	 *
	 * @return 当前令牌的登录设备类型
	 */
	public static String getLoginDevice() {
		return stpLogic.getLoginDevice(); 
	}

	
	// ------------------- 会话管理 -------------------  

	/**
	 * 根据条件查询缓存中所有的 token
	 *
	 * @param keyword 关键字
	 * @param start 开始处索引
	 * @param size 获取数量 (-1代表一直获取到末尾)
	 * @param sortType 排序类型（true=正序，false=反序）
	 *
	 * @return token集合
	 */
	public static List<String> searchTokenValue(String keyword, int start, int size, boolean sortType) {
		return stpLogic.searchTokenValue(keyword, start, size, sortType);
	}

	/**
	 * 根据条件查询缓存中所有的 SessionId
	 *
	 * @param keyword 关键字
	 * @param start 开始处索引
	 * @param size 获取数量  (-1代表一直获取到末尾)
	 * @param sortType 排序类型（true=正序，false=反序）
	 *
	 * @return sessionId集合
	 */
	public static List<String> searchSessionId(String keyword, int start, int size, boolean sortType) {
		return stpLogic.searchSessionId(keyword, start, size, sortType);
	}

	/**
	 * 根据条件查询缓存中所有的 Token-Session-Id
	 *
	 * @param keyword 关键字
	 * @param start 开始处索引
	 * @param size 获取数量 (-1代表一直获取到末尾)
	 * @param sortType 排序类型（true=正序，false=反序）
	 *
	 * @return sessionId集合
	 */
	public static List<String> searchTokenSessionId(String keyword, int start, int size, boolean sortType) {
		return stpLogic.searchTokenSessionId(keyword, start, size, sortType);
	}

	
	// ------------------- 账号封禁 -------------------  

	/**
	 * 封禁：指定账号
	 * <p> 此方法不会直接将此账号id踢下线，如需封禁后立即掉线，请追加调用 StpUtil.logout(id)
	 *
	 * @param loginId 指定账号id 
	 * @param time 封禁时间, 单位: 秒 （-1=永久封禁）
	 */
	public static void disable(Object loginId, long time) {
		stpLogic.disable(loginId, time);
	}

	/**
	 * 判断：指定账号是否已被封禁 (true=已被封禁, false=未被封禁) 
	 *
	 * @param loginId 账号id
	 * @return / 
	 */
	public static boolean isDisable(Object loginId) {
		return stpLogic.isDisable(loginId);
	}

	/**
	 * 校验：指定账号是否已被封禁，如果被封禁则抛出异常
	 *
	 * @param loginId 账号id
	 */
	public static void checkDisable(Object loginId) {
		stpLogic.checkDisable(loginId);
	}

	/**
	 * 获取：指定账号剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
	 *
	 * @param loginId 账号id
	 * @return / 
	 */
	public static long getDisableTime(Object loginId) {
		return stpLogic.getDisableTime(loginId);
	}

	/**
	 * 解封：指定账号
	 *
	 * @param loginId 账号id
	 */
	public static void untieDisable(Object loginId) {
		stpLogic.untieDisable(loginId);
	}

	
	// ------------------- 分类封禁 -------------------  

	/**
	 * 封禁：指定账号的指定服务 
	 * <p> 此方法不会直接将此账号id踢下线，如需封禁后立即掉线，请追加调用 StpUtil.logout(id)
	 *
	 * @param loginId 指定账号id
	 * @param service 指定服务 
	 * @param time 封禁时间, 单位: 秒 （-1=永久封禁）
	 */
	public static void disable(Object loginId, String service, long time) {
		stpLogic.disable(loginId, service, time);
	}

	/**
	 * 判断：指定账号的指定服务 是否已被封禁（true=已被封禁, false=未被封禁）
	 *
	 * @param loginId 账号id
	 * @param service 指定服务 
	 * @return / 
	 */
	public static boolean isDisable(Object loginId, String service) {
		return stpLogic.isDisable(loginId, service);
	}

	/**
	 * 校验：指定账号 指定服务 是否已被封禁，如果被封禁则抛出异常
	 *
	 * @param loginId 账号id
	 * @param services 指定服务，可以指定多个 
	 */
	public static void checkDisable(Object loginId, String... services) {
		stpLogic.checkDisable(loginId, services);
	}

	/**
	 * 获取：指定账号 指定服务 剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
	 *
	 * @param loginId 账号id
	 * @param service 指定服务 
	 * @return see note 
	 */
	public static long getDisableTime(Object loginId, String service) {
		return stpLogic.getDisableTime(loginId, service);
	}

	/**
	 * 解封：指定账号、指定服务
	 *
	 * @param loginId 账号id
	 * @param services 指定服务，可以指定多个 
	 */
	public static void untieDisable(Object loginId, String... services) {
		stpLogic.untieDisable(loginId, services);
	}


	// ------------------- 阶梯封禁 -------------------  

	/**
	 * 封禁：指定账号，并指定封禁等级
	 *
	 * @param loginId 指定账号id 
	 * @param level 指定封禁等级 
	 * @param time 封禁时间, 单位: 秒 （-1=永久封禁）
	 */
	public static void disableLevel(Object loginId, int level, long time) {
		stpLogic.disableLevel(loginId, level, time);
	}

	/**
	 * 封禁：指定账号的指定服务，并指定封禁等级
	 *
	 * @param loginId 指定账号id 
	 * @param service 指定封禁服务 
	 * @param level 指定封禁等级 
	 * @param time 封禁时间, 单位: 秒 （-1=永久封禁）
	 */
	public static void disableLevel(Object loginId, String service, int level, long time) {
		stpLogic.disableLevel(loginId, service, level, time);
	}

	/**
	 * 判断：指定账号是否已被封禁到指定等级
	 *
	 * @param loginId 指定账号id 
	 * @param level 指定封禁等级 
	 * @return / 
	 */
	public static boolean isDisableLevel(Object loginId, int level) {
		return stpLogic.isDisableLevel(loginId, level);
	}

	/**
	 * 判断：指定账号的指定服务，是否已被封禁到指定等级 
	 *
	 * @param loginId 指定账号id 
	 * @param service 指定封禁服务 
	 * @param level 指定封禁等级 
	 * @return / 
	 */
	public static boolean isDisableLevel(Object loginId, String service, int level) {
		return stpLogic.isDisableLevel(loginId, service, level);
	}

	/**
	 * 校验：指定账号是否已被封禁到指定等级（如果已经达到，则抛出异常）
	 *
	 * @param loginId 指定账号id 
	 * @param level 封禁等级 （只有 封禁等级 ≥ 此值 才会抛出异常）
	 */
	public static void checkDisableLevel(Object loginId, int level) {
		stpLogic.checkDisableLevel(loginId, level);
	}

	/**
	 * 校验：指定账号的指定服务，是否已被封禁到指定等级（如果已经达到，则抛出异常）
	 *
	 * @param loginId 指定账号id 
	 * @param service 指定封禁服务 
	 * @param level 封禁等级 （只有 封禁等级 ≥ 此值 才会抛出异常）
	 */
	public static void checkDisableLevel(Object loginId, String service, int level) {
		stpLogic.checkDisableLevel(loginId, service, level);
	}

	/**
	 * 获取：指定账号被封禁的等级，如果未被封禁则返回-2 
	 *
	 * @param loginId 指定账号id 
	 * @return / 
	 */
	public static int getDisableLevel(Object loginId) {
		return stpLogic.getDisableLevel(loginId);
	}

	/**
	 * 获取：指定账号的 指定服务 被封禁的等级，如果未被封禁则返回-2 
	 *
	 * @param loginId 指定账号id 
	 * @param service 指定封禁服务 
	 * @return / 
	 */
	public static int getDisableLevel(Object loginId, String service) {
		return stpLogic.getDisableLevel(loginId, service);
	}
	
	
	// ------------------- 临时身份切换 -------------------

	/**
	 * 临时切换身份为指定账号id
	 *
	 * @param loginId 指定loginId 
	 */
	public static void switchTo(Object loginId) {
		stpLogic.switchTo(loginId);
	}

	/**
	 * 结束临时切换身份
	 */
	public static void endSwitch() {
		stpLogic.endSwitch();
	}

	/**
	 * 判断当前请求是否正处于 [ 身份临时切换 ] 中
	 *
	 * @return /
	 */
	public static boolean isSwitch() {
		return stpLogic.isSwitch();
	}

	/**
	 * 在一个 lambda 代码段里，临时切换身份为指定账号id，lambda 结束后自动恢复
	 *
	 * @param loginId 指定账号id 
	 * @param function 要执行的方法 
	 */
	public static void switchTo(Object loginId, SaFunction function) {
		stpLogic.switchTo(loginId, function);
	}
	

	// ------------------- 二级认证 -------------------  

	/**
	 * 在当前会话 开启二级认证
	 *
	 * @param safeTime 维持时间 (单位: 秒) 
	 */
	public static void openSafe(long safeTime) {
		stpLogic.openSafe(safeTime);
	}

	/**
	 * 在当前会话 开启二级认证
	 *
	 * @param service 业务标识  
	 * @param safeTime 维持时间 (单位: 秒) 
	 */
	public static void openSafe(String service, long safeTime) {
		stpLogic.openSafe(service, safeTime);
	}

	/**
	 * 判断：当前会话是否处于二级认证时间内
	 *
	 * @return true=二级认证已通过, false=尚未进行二级认证或认证已超时 
	 */
	public static boolean isSafe() {
		return stpLogic.isSafe();
	}

	/**
	 * 判断：当前会话 是否处于指定业务的二级认证时间内
	 *
	 * @param service 业务标识  
	 * @return true=二级认证已通过, false=尚未进行二级认证或认证已超时 
	 */
	public static boolean isSafe(String service) {
		return stpLogic.isSafe(service);
	}

	/**
	 * 判断：指定 token 是否处于二级认证时间内
	 *
	 * @param tokenValue Token 值  
	 * @param service 业务标识  
	 * @return true=二级认证已通过, false=尚未进行二级认证或认证已超时 
	 */
	public static boolean isSafe(String tokenValue, String service) {
		return stpLogic.isSafe(tokenValue, service);
	}

	/**
	 * 校验：当前会话是否已通过二级认证，如未通过则抛出异常
	 */
	public static void checkSafe() {
		stpLogic.checkSafe();
	}

	/**
	 * 校验：检查当前会话是否已通过指定业务的二级认证，如未通过则抛出异常
	 *
	 * @param service 业务标识  
	 */
	public static void checkSafe(String service) {
		stpLogic.checkSafe(service);
	}

	/**
	 * 获取：当前会话的二级认证剩余有效时间（单位: 秒, 返回-2代表尚未通过二级认证）
	 *
	 * @return 剩余有效时间
	 */
	public static long getSafeTime() {
		return stpLogic.getSafeTime();
	}

	/**
	 * 获取：当前会话的二级认证剩余有效时间（单位: 秒, 返回-2代表尚未通过二级认证）
	 *
	 * @param service 业务标识  
	 * @return 剩余有效时间
	 */
	public static long getSafeTime(String service) {
		return stpLogic.getSafeTime(service);
	}

	/**
	 * 在当前会话 结束二级认证 
	 */
	public static void closeSafe() {
		stpLogic.closeSafe();
	}

	/**
	 * 在当前会话 结束指定业务标识的二级认证
	 *
	 * @param service 业务标识  
	 */
	public static void closeSafe(String service) {
		stpLogic.closeSafe(service);
	}

}

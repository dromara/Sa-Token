package com.pj.satoken.at;

import java.util.List;

import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;

/**
 * Sa-Token 权限认证工具类 
 * @author kong 
 */
public class StpUserUtil {
	
	/**
	 * 账号类型标识 
	 */
	public static final String TYPE = "user";
	
	/**
	 * 底层的 StpLogic 对象  
	 */
	public static StpLogic stpLogic = new StpLogic(TYPE); 

	/**
	 * 获取当前 StpLogic 的账号类型
	 * @return See Note 
	 */
	public static String getLoginType(){
		return stpLogic.getLoginType();
	}

	
	// =================== 获取token 相关 ===================

	/**
	 * 返回token名称 
	 * @return 此StpLogic的token名称
	 */
	public static String getTokenName() {
 		return stpLogic.getTokenName();
 	}

 	/**
 	 * 在当前会话写入当前TokenValue 
 	 * @param tokenValue token值 
 	 * @param cookieTimeout Cookie存活时间(秒)
 	 */
	public static void setTokenValue(String tokenValue, int cookieTimeout){
		stpLogic.setTokenValue(tokenValue, cookieTimeout);
	}
 	
	/**
	 * 获取当前TokenValue
	 * @return 当前tokenValue
	 */
	public static String getTokenValue() {
		return stpLogic.getTokenValue();
	}

	/**
	 * 获取当前会话的Token信息 
	 * @return token信息 
	 */
	public static SaTokenInfo getTokenInfo() {
		return stpLogic.getTokenInfo();
	}

	
	// =================== 登录相关操作 ===================

	// --- 登录 
	
	/**
	 * 会话登录 
	 * @param id 账号id，建议的类型：（long | int | String）
	 */
	public static void login(Object id) {
		stpLogic.login(id);
	}

	/**
	 * 会话登录，并指定登录设备 
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param device 设备标识 
	 */
	public static void login(Object id, String device) {
		stpLogic.login(id, device);
	}

	/**
	 * 会话登录，并指定是否 [记住我] 
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param isLastingCookie 是否为持久Cookie 
	 */
	public static void login(Object id, boolean isLastingCookie) {
		stpLogic.login(id, isLastingCookie);
	}

	/**
	 * 会话登录，并指定所有登录参数Model 
	 * @param id 登录id，建议的类型：（long | int | String）
	 * @param loginModel 此次登录的参数Model 
	 */
	public static void login(Object id, SaLoginModel loginModel) {
		stpLogic.login(id, loginModel);
	}

	// --- 注销 
	
	/** 
	 * 会话注销 
	 */
	public static void logout() {
		stpLogic.logout();
	}

	/**
	 * 会话注销，根据账号id 
	 * @param loginId 账号id 
	 */
	public static void logout(Object loginId) {
		stpLogic.logout(loginId);
	}

	/**
	 * 会话注销，根据账号id 和 设备标识 
	 * 
	 * @param loginId 账号id 
	 * @param device 设备标识 (填null代表所有注销设备) 
	 */
	public static void logout(Object loginId, String device) {
		stpLogic.logout(loginId, device);
	}
	
	/**
	 * 注销会话，根据指定 Token 
	 * 
	 * @param tokenValue 指定token
	 */
	public static void logoutByTokenValue(String tokenValue) {
		stpLogic.logoutByTokenValue(tokenValue);
	}
	
	/**
	 * 踢人下线，根据账号id 
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-5 </p>
	 * 
	 * @param loginId 账号id 
	 */
	public static void kickout(Object loginId) {
		stpLogic.kickout(loginId);
	}
	
	/**
	 * 踢人下线，根据账号id 和 设备标识 
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-5 </p>
	 * 
	 * @param loginId 账号id 
	 * @param device 设备标识 (填null代表踢出所有设备) 
	 */
	public static void kickout(Object loginId, String device) {
		stpLogic.kickout(loginId, device);
	}

	/**
	 * 踢人下线，根据指定 Token 
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-5 </p>
	 * 
	 * @param tokenValue 指定token
	 */
	public static void kickoutByTokenValue(String tokenValue) {
		stpLogic.kickoutByTokenValue(tokenValue);
	}
	
	/**
	 * 顶人下线，根据账号id 和 设备标识 
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-4 </p>
	 * 
	 * @param loginId 账号id 
	 * @param device 设备标识 (填null代表顶替所有设备) 
	 */
	public static void replaced(Object loginId, String device) {
		stpLogic.replaced(loginId, device);
	}
	
	
	// 查询相关

	/** 
 	 * 当前会话是否已经登录 
 	 * @return 是否已登录 
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
 	 * 获取当前会话账号id, 如果未登录，则抛出异常 
 	 * @return 账号id
 	 */
	public static Object getLoginId() {
		return stpLogic.getLoginId();
	}

	/** 
	 * 获取当前会话账号id, 如果未登录，则返回默认值 
	 * @param <T> 返回类型 
	 * @param defaultValue 默认值
	 * @return 登录id 
	 */
	public static <T> T getLoginId(T defaultValue) {
		return stpLogic.getLoginId(defaultValue);
	}

	/** 
	 * 获取当前会话账号id, 如果未登录，则返回null 
	 * @return 账号id 
	 */
	public static Object getLoginIdDefaultNull() {
		return stpLogic.getLoginIdDefaultNull();
 	}

	/** 
	 * 获取当前会话账号id, 并转换为String类型
	 * @return 账号id 
	 */
	public static String getLoginIdAsString() {
		return stpLogic.getLoginIdAsString();
	}

	/** 
	 * 获取当前会话账号id, 并转换为int类型
	 * @return 账号id 
	 */
	public static int getLoginIdAsInt() {
		return stpLogic.getLoginIdAsInt();
	}

	/**
	 * 获取当前会话账号id, 并转换为long类型 
	 * @return 账号id 
	 */
	public static long getLoginIdAsLong() {
		return stpLogic.getLoginIdAsLong();
	}

	/** 
 	 * 获取指定Token对应的账号id，如果未登录，则返回 null 
 	 * @param tokenValue token
 	 * @return 账号id
 	 */
 	public static Object getLoginIdByToken(String tokenValue) {
 		return stpLogic.getLoginIdByToken(tokenValue);
 	}
	
 	
	// =================== session相关 ===================

 	/** 
	 * 获取指定账号id的Session, 如果Session尚未创建，isCreate=是否新建并返回
	 * @param loginId 账号id
	 * @param isCreate 是否新建
	 * @return Session对象
	 */
	public static SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
		return stpLogic.getSessionByLoginId(loginId, isCreate);
	}

	/** 
	 * 获取指定key的Session, 如果Session尚未创建，则返回null
	 * @param sessionId SessionId
	 * @return Session对象 
	 */
	public static SaSession getSessionBySessionId(String sessionId) {
		return stpLogic.getSessionBySessionId(sessionId);
	}

	/** 
	 * 获取指定账号id的Session，如果Session尚未创建，则新建并返回 
	 * @param loginId 账号id 
	 * @return Session对象 
	 */
	public static SaSession getSessionByLoginId(Object loginId) {
		return stpLogic.getSessionByLoginId(loginId);
	}

	/** 
	 * 获取当前会话的Session, 如果Session尚未创建，isCreate=是否新建并返回 
	 * @param isCreate 是否新建 
	 * @return Session对象 
	 */
	public static SaSession getSession(boolean isCreate) {
		return stpLogic.getSession(isCreate);
	}

	/** 
	 * 获取当前会话的Session，如果Session尚未创建，则新建并返回 
	 * @return Session对象 
	 */
	public static SaSession getSession() {
		return stpLogic.getSession();
	}

	
	// =================== token专属session ===================  
	
	/** 
	 * 获取指定Token-Session，如果Session尚未创建，则新建并返回 
	 * @param tokenValue Token值
	 * @return Session对象  
	 */
	public static SaSession getTokenSessionByToken(String tokenValue) {
		return stpLogic.getTokenSessionByToken(tokenValue);
	}
	
	/** 
	 * 获取当前Token-Session，如果Session尚未创建，则新建并返回
	 * @return Session对象 
	 */
	public static SaSession getTokenSession() {
		return stpLogic.getTokenSession();
	}


	// =================== [临时过期] 验证相关 ===================  

	/**
 	 * 检查当前token 是否已经[临时过期]，如果已经过期则抛出异常  
 	 */
 	public static void checkActivityTimeout() {
 		stpLogic.checkActivityTimeout();
 	}

 	/**
 	 * 续签当前token：(将 [最后操作时间] 更新为当前时间戳) 
 	 * <h1>请注意: 即时token已经 [临时过期] 也可续签成功，
 	 * 如果此场景下需要提示续签失败，可在此之前调用 checkActivityTimeout() 强制检查是否过期即可 </h1>
 	 */
 	public static void updateLastActivityToNow() {
 		stpLogic.updateLastActivityToNow();
 	}
 	

	// =================== 过期时间相关 ===================  

 	/**
 	 * 获取当前登录者的token剩余有效时间 (单位: 秒)
 	 * @return token剩余有效时间
 	 */
 	public static long getTokenTimeout() {
 		return stpLogic.getTokenTimeout();
 	}
 	
 	/**
 	 * 获取当前登录者的Session剩余有效时间 (单位: 秒)
 	 * @return token剩余有效时间
 	 */
 	public static long getSessionTimeout() {
 		return stpLogic.getSessionTimeout();
 	}

 	/**
 	 * 获取当前token的专属Session剩余有效时间 (单位: 秒) 
 	 * @return token剩余有效时间
 	 */
 	public static long getTokenSessionTimeout() {
 		return stpLogic.getTokenSessionTimeout();
 	}
 	
 	/**
 	 * 获取当前token[临时过期]剩余有效时间 (单位: 秒)
 	 * @return token[临时过期]剩余有效时间
 	 */
 	public static long getTokenActivityTimeout() {
 		return stpLogic.getTokenActivityTimeout();
 	}
 	

 	
	// =================== 角色验证操作 ===================  

 	/** 
 	 * 指定账号id是否含有角色标识, 返回true或false 
 	 * @param loginId 账号id
 	 * @param role 角色标识
 	 * @return 是否含有指定角色标识
 	 */
 	public static boolean hasRole(Object loginId, String role) {
 		return stpLogic.hasRole(loginId, role);
 	}
 	
 	/** 
 	 * 当前账号是否含有指定角色标识, 返回true或false 
 	 * @param role 角色标识
 	 * @return 是否含有指定角色标识
 	 */
 	public static boolean hasRole(String role) {
 		return stpLogic.hasRole(role);
 	}
	
 	/** 
 	 * 当前账号是否含有指定角色标识, 如果验证未通过，则抛出异常: NotRoleException 
 	 * @param role 角色标识
 	 */
 	public static void checkRole(String role) {
 		stpLogic.checkRole(role);
 	}

 	/** 
 	 * 当前账号是否含有指定角色标识 [指定多个，必须全部验证通过] 
 	 * @param roleArray 角色标识数组
 	 */
 	public static void checkRoleAnd(String... roleArray){
 		stpLogic.checkRoleAnd(roleArray);
 	}

 	/** 
 	 * 当前账号是否含有指定角色标识 [指定多个，只要其一验证通过即可] 
 	 * @param roleArray 角色标识数组
 	 */
 	public static void checkRoleOr(String... roleArray){
 		stpLogic.checkRoleOr(roleArray);
 	}
 	
	
	// =================== 权限验证操作 ===================

 	/** 
 	 * 指定账号id是否含有指定权限, 返回true或false 
 	 * @param loginId 账号id
 	 * @param permission 权限码
 	 * @return 是否含有指定权限
 	 */
	public static boolean hasPermission(Object loginId, String permission) {
		return stpLogic.hasPermission(loginId, permission);
	}

 	/** 
 	 * 当前账号是否含有指定权限, 返回true或false 
 	 * @param permission 权限码
 	 * @return 是否含有指定权限
 	 */
	public static boolean hasPermission(String permission) {
		return stpLogic.hasPermission(permission);
	}

 	/** 
 	 * 当前账号是否含有指定权限, 如果验证未通过，则抛出异常: NotPermissionException 
 	 * @param permission 权限码
 	 */
	public static void checkPermission(String permission) {
		stpLogic.checkPermission(permission);
	}

 	/** 
 	 * 当前账号是否含有指定权限 [指定多个，必须全部验证通过] 
 	 * @param permissionArray 权限码数组
 	 */
	public static void checkPermissionAnd(String... permissionArray) {
		stpLogic.checkPermissionAnd(permissionArray);
	}

 	/** 
 	 * 当前账号是否含有指定权限 [指定多个，只要其一验证通过即可] 
 	 * @param permissionArray 权限码数组
 	 */
	public static void checkPermissionOr(String... permissionArray) {
		stpLogic.checkPermissionOr(permissionArray);
	}


	// =================== id 反查token 相关操作 ===================  
	
	/** 
	 * 获取指定账号id的tokenValue 
	 * <p> 在配置为允许并发登录时，此方法只会返回队列的最后一个token，
	 * 如果你需要返回此账号id的所有token，请调用 getTokenValueListByLoginId 
	 * @param loginId 账号id
	 * @return token值 
	 */
	public static String getTokenValueByLoginId(Object loginId) {
		return stpLogic.getTokenValueByLoginId(loginId);
	}

	/** 
	 * 获取指定账号id指定设备端的tokenValue 
	 * <p> 在配置为允许并发登录时，此方法只会返回队列的最后一个token，
	 * 如果你需要返回此账号id的所有token，请调用 getTokenValueListByLoginId 
	 * @param loginId 账号id
	 * @param device 设备标识 
	 * @return token值 
	 */
	public static String getTokenValueByLoginId(Object loginId, String device) {
		return stpLogic.getTokenValueByLoginId(loginId, device);
	}
	
	/** 
	 * 获取指定账号id的tokenValue集合 
	 * @param loginId 账号id 
	 * @return 此loginId的所有相关token 
 	 */
	public static List<String> getTokenValueListByLoginId(Object loginId) {
		return stpLogic.getTokenValueListByLoginId(loginId);
	}

	/** 
	 * 获取指定账号id指定设备端的tokenValue 集合 
	 * @param loginId 账号id 
	 * @param device 设备标识 
	 * @return 此loginId的所有相关token 
 	 */
	public static List<String> getTokenValueListByLoginId(Object loginId, String device) {
		return stpLogic.getTokenValueListByLoginId(loginId, device);
	}
	
	/**
	 * 返回当前会话的登录设备 
	 * @return 当前令牌的登录设备 
	 */
	public static String getLoginDevice() {
		return stpLogic.getLoginDevice(); 
	}

	
	// =================== 会话管理 ===================  

	/**
	 * 根据条件查询Token 
	 * @param keyword 关键字 
	 * @param start 开始处索引 (-1代表查询所有) 
	 * @param size 获取数量 
	 * @return token集合 
	 */
	public static List<String> searchTokenValue(String keyword, int start, int size) {
		return stpLogic.searchTokenValue(keyword, start, size);
	}
	
	/**
	 * 根据条件查询SessionId 
	 * @param keyword 关键字 
	 * @param start 开始处索引 (-1代表查询所有) 
	 * @param size 获取数量 
	 * @return sessionId集合 
	 */
	public static List<String> searchSessionId(String keyword, int start, int size) {
		return stpLogic.searchSessionId(keyword, start, size);
	}

	/**
	 * 根据条件查询Token专属Session的Id 
	 * @param keyword 关键字 
	 * @param start 开始处索引 (-1代表查询所有) 
	 * @param size 获取数量 
	 * @return sessionId集合 
	 */
	public static List<String> searchTokenSessionId(String keyword, int start, int size) {
		return stpLogic.searchTokenSessionId(keyword, start, size);
	}

	
	// ------------------- 账号封禁 -------------------  

	/**
	 * 封禁指定账号
	 * <p> 此方法不会直接将此账号id踢下线，而是在对方再次登录时抛出`DisableLoginException`异常 
	 * @param loginId 指定账号id 
	 * @param disableTime 封禁时间, 单位: 秒 （-1=永久封禁）
	 */
	public static void disable(Object loginId, long disableTime) {
		stpLogic.disable(loginId, disableTime);
	}
	
	/**
	 * 指定账号是否已被封禁 (true=已被封禁, false=未被封禁) 
	 * @param loginId 账号id
	 * @return see note
	 */
	public static boolean isDisable(Object loginId) {
		return stpLogic.isDisable(loginId);
	}
	
	/**
	 * 获取指定账号剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
	 * @param loginId 账号id
	 * @return see note 
	 */
	public static long getDisableTime(Object loginId) {
		return stpLogic.getDisableTime(loginId);
	}

	/**
	 * 解封指定账号
	 * @param loginId 账号id 
	 */
	public static void untieDisable(Object loginId) {
		stpLogic.untieDisable(loginId);
	}
	
	
	// =================== 身份切换 ===================  

	/**
	 * 临时切换身份为指定账号id 
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
	 * 当前是否正处于[身份临时切换]中 
	 * @return 是否正处于[身份临时切换]中 
	 */
	public static boolean isSwitch() {
		return stpLogic.isSwitch();
	}

	/**
	 * 在一个代码段里方法内，临时切换身份为指定账号id
	 * @param loginId 指定账号id 
	 * @param function 要执行的方法 
	 */
	public static void switchTo(Object loginId, SaFunction function) {
		stpLogic.switchTo(loginId, function);
	}
	

	// ------------------- 二级认证 -------------------  
	
	/**
	 * 在当前会话 开启二级认证 
	 * @param safeTime 维持时间 (单位: 秒) 
	 */
	public static void openSafe(long safeTime) {
		stpLogic.openSafe(safeTime);
	}

	/**
	 * 当前会话 是否处于二级认证时间内 
	 * @return true=二级认证已通过, false=尚未进行二级认证或认证已超时 
	 */
	public static boolean isSafe() {
		return stpLogic.isSafe();
	}

	/**
	 * 检查当前会话是否已通过二级认证，如未通过则抛出异常 
	 */
	public static void checkSafe() {
		stpLogic.checkSafe();
	}
	
	/**
	 * 获取当前会话的二级认证剩余有效时间 (单位: 秒, 返回-2代表尚未通过二级认证)
	 * @return 剩余有效时间
	 */
	public static long getSafeTime() {
		return stpLogic.getSafeTime();
	}

	/**
	 * 在当前会话 结束二级认证 
	 */
	public static void closeSafe() {
		stpLogic.closeSafe();
	}


	// =================== 历史API，兼容旧版本 ===================  

	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 StpUtil.getLoginType() ，使用方式保持不变 </h1>
	 * 获取当前StpLogin的loginKey 
	 * @return 当前StpLogin的loginKey
	 */
	@Deprecated
	public static String getLoginKey(){
		return stpLogic.getLoginType();
	}

	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 StpUtil.login() ，使用方式保持不变 </h1>
	 * 在当前会话上登录id 
	 * @param loginId 登录id，建议的类型：（long | int | String）
	 */
	@Deprecated
	public static void setLoginId(Object loginId) {
		stpLogic.login(loginId);
	}

	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 StpUtil.login() ，使用方式保持不变 </h1>
	 * 在当前会话上登录id, 并指定登录设备 
	 * @param loginId 登录id，建议的类型：（long | int | String）
	 * @param device 设备标识 
	 */
	@Deprecated
	public static void setLoginId(Object loginId, String device) {
		stpLogic.login(loginId, device);
	}

	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 StpUtil.login() ，使用方式保持不变 </h1>
	 * 在当前会话上登录id, 并指定登录设备 
	 * @param loginId 登录id，建议的类型：（long | int | String）
	 * @param isLastingCookie 是否为持久Cookie 
	 */
	@Deprecated
	public static void setLoginId(Object loginId, boolean isLastingCookie) {
		stpLogic.login(loginId, isLastingCookie);
	}
	
	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 StpUtil.login() ，使用方式保持不变 </h1>
	 * 在当前会话上登录id, 并指定所有登录参数Model 
	 * @param loginId 登录id，建议的类型：（long | int | String）
	 * @param loginModel 此次登录的参数Model 
	 */
	@Deprecated
	public static void setLoginId(Object loginId, SaLoginModel loginModel) {
		stpLogic.login(loginId, loginModel);
	}
	
}

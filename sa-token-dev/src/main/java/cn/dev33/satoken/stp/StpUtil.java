package cn.dev33.satoken.stp;

import java.util.Map;

import org.springframework.stereotype.Service;

import cn.dev33.satoken.session.SaSession;

/**
 * 一个默认的实现 
 */
@Service
public class StpUtil {

	// 底层的 StpLogic 对象  
	public static StpLogic stpLogic = new StpLogic("login"); 
	
	
	// =================== 获取token 相关 ===================


	/** 获取当前tokenValue */
	public static String getTokenValue() {
		return stpLogic.getTokenValue();
	}

	/** 获取指定id的tokenValue */
	public static String getTokenValueByLoginId(Object login_id) {
		return stpLogic.getTokenValueByLoginId(login_id);
	}

	/** 获取当前会话的token信息：tokenName与tokenValue */
	public static Map<String, String> getTokenInfo() {
		return stpLogic.getTokenInfo();
	}

	// =================== 登录相关操作 ===================

	/** 在当前会话上设置登录id，建议的类型：（long | int | String） */
	public static void setLoginId(Object login_id) {
		stpLogic.setLoginId(login_id);
	}

	/** 当前会话注销登录 */
	public static void logout() {
		stpLogic.logout();
	}

	/** 指定login_id的会话注销登录（踢人下线） */
	public static void logoutByLoginId(Object login_id) {
		stpLogic.logoutByLoginId(login_id);
	}

	// 查询相关

	/** 获取当前会话是否已经登录 */
	public static boolean isLogin() {
		return stpLogic.isLogin();
	}

	/** 获取当前会话登录id, 如果未登录，则抛出异常 */
	public static Object getLoginId() {
		return stpLogic.getLoginId();
	}

	/** 获取当前会话登录id, 如果未登录，则返回默认值 */
	public static <T> T getLoginId(T default_value) {
		return stpLogic.getLoginId(default_value);
	}
	
	/** 获取当前会话登录id, 如果未登录，则返回null  */
	public static Object getLoginId_defaultNull() {
		return stpLogic.getLoginId_defaultNull();
 	}

	/** 获取当前会话登录id, 并转换为String */
	public static String getLoginId_asString() {
		return stpLogic.getLoginId_asString();
	}

	/** 获取当前会话登录id, 并转换为int */
	public static int getLoginId_asInt() {
		return stpLogic.getLoginId_asInt();
	}

	/** 获取当前会话登录id, 并转换为long */
	public static long getLoginId_asLong() {
		return stpLogic.getLoginId_asLong();
	}

	// =================== session相关 ===================

	/** 获取指定login_id的session */
	public static SaSession getSessionByLoginId(Object login_id) {
		return stpLogic.getSessionByLoginId(login_id);
	}

	/** 获取当前会话的session */
	public static SaSession getSession() {
		return stpLogic.getSession();
	}

	// =================== 权限验证操作 ===================

	/** 指定login_id是否含有指定权限 */
	public static boolean hasPermission(Object login_id, Object pcode) {
		return stpLogic.hasPermission(login_id, pcode);
	}

	/** 当前会话是否含有指定权限 */
	public static boolean hasPermission(Object pcode) {
		return stpLogic.hasPermission(pcode);
	}

	/** 当前账号是否含有指定权限 ， 没有就抛出异常 */
	public static void checkPermission(Object pcode) {
		stpLogic.checkPermission(pcode);
	}

	/** 当前账号是否含有指定权限 ， 【指定多个，必须全都有】 */
	public static void checkPermissionAnd(Object... pcodeArray) {
		stpLogic.checkPermissionAnd(pcodeArray);
	}

	/** 当前账号是否含有指定权限 ， 【指定多个，有一个就可以了】 */
	public static void checkPermissionOr(Object... pcodeArray) {
		stpLogic.checkPermissionOr(pcodeArray);
	}


}

package cn.dev33.satoken.stp;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.util.SaTokenInsideUtil;

/**
 * sa-token 权限验证，逻辑实现类 
 * <p>
 * (stp = sa-token-permission 的缩写 )
 * @author kong
 */
public class StpLogic {

	/**
	 * 持久化的key前缀，多账号认证体系时以此值区分，比如：login、user、admin 
	 */
	public String loginKey = "";		
	
	/**
	 * 初始化StpLogic, 并制定loginKey
	 * @param loginKey 账号标识 
	 */
	public StpLogic(String loginKey) {
		this.loginKey = loginKey;
	}

	
	// =================== 获取token 相关 ===================  
	
	/**
	 * 返回token名称 
	 * @return 此StpLogic的token名称
	 */
	public String getTokenName() {
 		return getKeyTokenName();
 	}

	/**
	 * 随机生成一个tokenValue
	 * @param loginId loginId
	 * @return 生成的tokenValue 
	 */
 	public String randomTokenValue(Object loginId) {
		return SaTokenManager.getSaTokenAction().createToken(loginId, loginKey);
	}
	
	/**
	 *  获取当前tokenValue
	 * @return 当前tokenValue
	 */
	public String getTokenValue(){
		// 0. 获取相应对象 
		HttpServletRequest request = SaTokenManager.getSaTokenServlet().getRequest();
		SaTokenConfig config = getConfig();
		String keyTokenName = getTokenName();
		String tokenValue = null;
		
		// 1. 尝试从request里读取 
		if(request.getAttribute(SaTokenInsideUtil.JUST_CREATED_SAVE_KEY) != null) {
			tokenValue = String.valueOf(request.getAttribute(SaTokenInsideUtil.JUST_CREATED_SAVE_KEY));
		}
		// 2. 尝试从请求体里面读取 
		if(tokenValue == null && config.getIsReadBody() == true){
			tokenValue = request.getParameter(keyTokenName);
		}
		// 3. 尝试从header力读取 
		if(tokenValue == null && config.getIsReadHead() == true){
			tokenValue = request.getHeader(keyTokenName);
		}
		// 4. 尝试从cookie里读取 
		if(tokenValue == null && config.getIsReadCookie() == true){
			Cookie cookie = SaTokenManager.getSaTokenCookie().getCookie(request, keyTokenName);
			if(cookie != null){
				tokenValue = cookie.getValue();
			}
		}
		
		// 5. 返回 
		return tokenValue;
	}
	
	/** 
	 * 获取指定loginId的tokenValue
	 * @param loginId 账号id
	 * @return token值 
	 */
	public String getTokenValueByLoginId(Object loginId) {
		return SaTokenManager.getSaTokenDao().getValue(getKeyLoginId(loginId)); 
	}
	
	/**
	 * 获取当前StpLogin的loginKey
	 * @return 当前StpLogin的loginKey
	 */
	public String getLoginKey(){
		return loginKey;
	}
	
	/**
	 * 获取当前会话的token信息 
	 * @return token信息 
	 */
	public SaTokenInfo getTokenInfo() {
		SaTokenInfo info = new SaTokenInfo();
		info.tokenName = getTokenName();
		info.tokenValue = getTokenValue();
		info.isLogin = isLogin();
		info.loginId = getLoginIdDefaultNull();
		info.loginKey = getLoginKey();
		info.tokenTimeout = getTokenTimeout();
		info.sessionTimeout = getSessionTimeout();
		info.tokenSessionTimeout = getTokenSessionTimeout();
		info.tokenActivityTimeout = getTokenActivityTimeout();
		return info;
	}
	
	
	// =================== 登录相关操作 ===================  

	/**
	 * 在当前会话上登录id 
	 * @param loginId 登录id，建议的类型：（long | int | String）
	 */
	public void setLoginId(Object loginId) {
		
		// 1、获取相应对象  
		HttpServletRequest request = SaTokenManager.getSaTokenServlet().getRequest();
		SaTokenConfig config = getConfig();
		SaTokenDao dao = SaTokenManager.getSaTokenDao();
		
		// 2、获取tokenValue
		String tokenValue = getTokenValueByLoginId(loginId);	// 获取旧tokenValue
		if(tokenValue == null){			// 为null则创建一个新的
			tokenValue = randomTokenValue(loginId);
		} else {
			// 不为null, 并且配置不共享会话，则：将原来的会话标记为[被顶替] 
			if(config.getIsShare() == false){
				dao.updateValue(getKeyTokenValue(tokenValue), NotLoginException.BE_REPLACED);
				clearLastActivity(tokenValue); 	// 同时清理掉[最后操作时间] 
				tokenValue = randomTokenValue(loginId);	// 再重新生成一个token 
			}
		}

		// 3、持久化 
		dao.setValue(getKeyTokenValue(tokenValue), String.valueOf(loginId), config.getTimeout());	// token -> uid 
		dao.setValue(getKeyLoginId(loginId), tokenValue, config.getTimeout());							// uid -> token 
		request.setAttribute(SaTokenInsideUtil.JUST_CREATED_SAVE_KEY, tokenValue);								// 保存到本次request里  
		setLastActivityToNow(tokenValue);  // 写入 [最后操作时间]
		if(config.getIsReadCookie() == true){
			SaTokenManager.getSaTokenCookie().addCookie(SaTokenManager.getSaTokenServlet().getResponse(), getTokenName(), tokenValue, "/", (int)config.getTimeout());		// cookie注入 
		}
	}

	/** 
	 * 当前会话注销登录
	 */
	public void logout() {
		// 如果连token都没有，那么无需执行任何操作 
		String tokenValue = getTokenValue();
 		if(tokenValue == null) {
 			return;
 		}
 		// 如果打开了cookie模式，第一步，先把cookie清除掉 
 		if(getConfig().getIsReadCookie() == true){
			SaTokenManager.getSaTokenCookie().delCookie(SaTokenManager.getSaTokenServlet().getRequest(), SaTokenManager.getSaTokenServlet().getResponse(), getTokenName()); 	
		}
 		// 尝试从db中获取loginId值 
 		String loginId = SaTokenManager.getSaTokenDao().getValue(getKeyTokenValue(tokenValue));
 		// 如果根本查不到loginId，那么也无需执行任何操作 
 		if(loginId == null) {
 			return;
 		}
 		// 如果已过期或被顶替或被挤下线，那么只删除此token即可 
 		if(loginId.equals(NotLoginException.TOKEN_TIMEOUT) || loginId.equals(NotLoginException.BE_REPLACED) || loginId.equals(NotLoginException.KICK_OUT)) {
 			return;
 		}
		// 至此，已经是一个正常的loginId，开始三清 
 		logoutByLoginId(loginId);
	}

	/**
	 * 指定loginId的会话注销登录（正常注销下线）
	 * @param loginId 账号id 
	 */
	public void logoutByLoginId(Object loginId) {
		
		// 获取相应tokenValue
		String tokenValue = getTokenValueByLoginId(loginId);
		if(tokenValue == null) {
			return;
		}
		
		// 清除相关数据 
		SaTokenManager.getSaTokenDao().deleteKey(getKeyTokenValue(tokenValue));	// 清除token-id键值对  
		SaTokenManager.getSaTokenDao().deleteKey(getKeyLoginId(loginId));		// 清除id-token键值对  
		SaTokenManager.getSaTokenDao().deleteSession(getKeySession(loginId));		// 清除其session 
		clearLastActivity(tokenValue); 	// 同时清理掉 [最后操作时间] 
	}

	/**
	 * 指定loginId的会话注销登录（踢人下线）
	 * @param loginId 账号id 
	 */
	public void kickoutByLoginId(Object loginId) {
		
		// 获取相应tokenValue
		String tokenValue = getTokenValueByLoginId(loginId);
		if(tokenValue == null) {
			return;
		}
		
		// 清除相关数据 
		SaTokenManager.getSaTokenDao().updateValue(getKeyTokenValue(tokenValue), NotLoginException.KICK_OUT);	// 标记：已被踢下线 
		SaTokenManager.getSaTokenDao().deleteKey(getKeyLoginId(loginId));		// 清除id-token键值对  
		SaTokenManager.getSaTokenDao().deleteSession(getKeySession(loginId));		// 清除其session 
		clearLastActivity(tokenValue); 	 // 同时清理掉 [最后操作时间] 
	}
	
	// 查询相关 
	
 	/** 
 	 * 获取当前会话是否已经登录 
 	 * @return 是否已登录 
 	 */
 	public boolean isLogin() {
 		// 判断条件：不为null，并且不在异常项集合里 
 		return getLoginIdDefaultNull() != null;
 	}
 	
 	/** 
 	 * 检验当前会话是否已经登录，如未登录，则抛出异常 
 	 */
 	public void checkLogin() {
 		getLoginId();
 	}
 	
 	/** 
 	 * 获取当前会话账号id, 如果未登录，则抛出异常 
 	 * @return 账号id
 	 */
 	public Object getLoginId() {
 		// 如果获取不到token，则抛出：无token
 		String tokenValue = getTokenValue();
 		if(tokenValue == null) {
 			throw NotLoginException.newInstance(loginKey, NotLoginException.NOT_TOKEN);
 		}
 		// 查找此token对应loginId, 则抛出：无效token 
 		String loginId = SaTokenManager.getSaTokenDao().getValue(getKeyTokenValue(tokenValue));
 		if(loginId == null) {
 			throw NotLoginException.newInstance(loginKey, NotLoginException.INVALID_TOKEN);
 		}
 		// 如果是已经过期，则抛出已经过期 
 		if(loginId.equals(NotLoginException.TOKEN_TIMEOUT)) {
 			throw NotLoginException.newInstance(loginKey, NotLoginException.TOKEN_TIMEOUT);
 		}
 		// 如果是已经被顶替下去了, 则抛出：已被顶下线 
 		if(loginId.equals(NotLoginException.BE_REPLACED)) {
 			throw NotLoginException.newInstance(loginKey, NotLoginException.BE_REPLACED);
 		}
 		// 如果是已经被踢下线了, 则抛出：已被踢下线
 		if(loginId.equals(NotLoginException.KICK_OUT)) {
 			throw NotLoginException.newInstance(loginKey, NotLoginException.KICK_OUT);
 		}
 		// 检查是否已经 [临时过期]，同时更新[最后操作时间] 
 		checkActivityTimeout(tokenValue);
 		updateLastActivityToNow(tokenValue);
 		// 至此，返回loginId
 		return loginId;
 	}
	
	/** 
	 * 获取当前会话登录id, 如果未登录，则返回默认值 
	 * @param <T> 返回类型 
	 * @param defaultValue 默认值
	 * @return 登录id 
	 */
 	@SuppressWarnings("unchecked")
	public <T>T getLoginId(T defaultValue) {
		Object loginId = getLoginIdDefaultNull();
		// 如果loginId为null，则返回默认值 
		if(loginId == null) {
			return defaultValue;
		}
		// 开始尝试类型转换，只尝试三种类型：int、long、String 
		if(defaultValue instanceof Integer) {
			return (T)Integer.valueOf(loginId.toString());
		}
		if(defaultValue instanceof Long) {
			return (T)Long.valueOf(loginId.toString());
		}
		if(defaultValue instanceof String) {
			return (T)loginId.toString();
		}
		return (T)loginId;
 	}
 	
	/** 
	 * 获取当前会话登录id, 如果未登录，则返回null 
	 * @return 账号id 
	 */
	public Object getLoginIdDefaultNull() {
		// 如果连token都是空的，则直接返回 
		String tokenValue = getTokenValue();
 		if(tokenValue == null) {
 			return null;
 		}
 		// loginId为null或者在异常项里面，均视为未登录
 		Object loginId = SaTokenManager.getSaTokenDao().getValue(getKeyTokenValue(tokenValue));
 		if(loginId == null || NotLoginException.ABNORMAL_LIST.contains(loginId)) {
 			return null;
 		}
 		// 如果已经[临时过期] 
 		if(getTokenActivityTimeoutByToken(tokenValue) == SaTokenDao.NOT_VALUE_EXPIRE) {
 			return null;
 		}
 		// 执行到此，证明loginId已经是个正常的账号id了 
 		return loginId;
 	}

	/** 
	 * 获取当前会话登录id, 并转换为String
	 * @return 账号id 
	 */
 	public String getLoginIdAsString() {
 		return String.valueOf(getLoginId());
 	}

	/** 
	 * 获取当前会话登录id, 并转换为int
	 * @return 账号id 
	 */
 	public int getLoginIdAsInt() {
 		// Object loginId = getLoginId();
// 		if(loginId instanceof Integer) {
// 			return (Integer)loginId;
// 		}
 		return Integer.valueOf(String.valueOf(getLoginId()));
 	}

	/**
	 * 获取当前会话登录id, 并转换为long
	 * @return 账号id 
	 */
 	public long getLoginIdAsLong() {
// 		Object loginId = getLoginId();
// 		if(loginId instanceof Long) {
// 			return (Long)loginId;
// 		}
 		return Long.valueOf(String.valueOf(getLoginId()));
 	}
 	
 	/** 
 	 * 获取指定token对应的登录id，如果未登录，则返回 null 
 	 * @param tokenValue token
 	 * @return 登录id
 	 */
 	public Object getLoginIdByToken(String tokenValue) {
 		if(tokenValue != null) {
 			Object loginId = SaTokenManager.getSaTokenDao().getValue(getKeyTokenValue(tokenValue));
 			if(loginId != null) {
 				return loginId;
 			}
 		}
 		return null;
 	}
 	
 	
	// =================== session相关 ===================  

	/** 
	 * 获取指定key的session, 如果session尚未创建，isCreate=是否新建并返回
	 * @param sessionId sessionId
	 * @param isCreate 是否新建
	 * @return session对象 
	 */
	public SaSession getSessionBySessionId(String sessionId, boolean isCreate) {
		SaSession session = SaTokenManager.getSaTokenDao().getSession(sessionId);
		if(session == null && isCreate) {
			session = new SaSession(sessionId);
			SaTokenManager.getSaTokenDao().saveSession(session, getConfig().getTimeout());
		}
		return session;
	}

	/** 
	 * 获取指定loginId的session, 如果session尚未创建，isCreate=是否新建并返回
	 * @param loginId 账号id
	 * @param isCreate 是否新建
	 * @return SaSession
	 */
	public SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
		return getSessionBySessionId(getKeySession(loginId), isCreate);
	}

	/** 
	 * 获取指定loginId的session，如果session尚未创建，则新建并返回 
	 * @param loginId 账号id 
	 * @return session会话 
	 */
	public SaSession getSessionByLoginId(Object loginId) {
		return getSessionByLoginId(loginId, true);
	}

	/** 
	 * 获取当前会话的session, 如果session尚未创建，isCreate=是否新建并返回 
	 * @param isCreate 是否新建 
	 * @return 当前会话的session 
	 */
	public SaSession getSession(boolean isCreate) {
		return getSessionByLoginId(getLoginId(), isCreate);
	}
	
	/** 
	 * 获取当前会话的session，如果session尚未创建，则新建并返回 
	 * @return 当前会话的session 
	 */
	public SaSession getSession() {
		return getSession(true);
	}

	// ---------- token专属session
	
	/** 
	 * 获取指定token的专属session，如果session尚未创建，则新建并返回 
	 * @param tokenValue token值
	 * @return session会话 
	 */
	public SaSession getTokenSessionByToken(String tokenValue) {
		return getSessionBySessionId(getKeyTokenSession(tokenValue), true);
	}
	
	/** 
	 * 获取当前token的专属-session，如果session尚未创建，则新建并返回 
	 * <p> 只有当前会话属于登录状态才可调用 
	 * @return session会话 
	 */
	public SaSession getTokenSession() {
		if(getConfig().getTokenSessionCheckLogin()) {
			checkLogin();
		}
		return getTokenSessionByToken(getTokenValue());
	}

 	
	// =================== [临时过期] 验证相关 ===================  

 	/**
 	 * 写入指定token的 [最后操作时间] 为当前时间戳 
 	 * @param tokenValue 指定token 
 	 */
 	protected void setLastActivityToNow(String tokenValue) {
 		// 如果token == null 或者 设置了[永不过期], 则立即返回 
 		if(tokenValue == null || getConfig().getActivityTimeout() == SaTokenDao.NEVER_EXPIRE) {
 			return;
 		}
 		// 将[最后操作时间]标记为当前时间戳 
 		SaTokenManager.getSaTokenDao().setValue(getKeyLastActivityTime(tokenValue), String.valueOf(System.currentTimeMillis()), getConfig().getTimeout());
 	}
 	
 	/**
 	 * 清除指定token的 [最后操作时间] 
 	 * @param tokenValue 指定token 
 	 */
 	protected void clearLastActivity(String tokenValue) {
 		// 如果token == null 或者 设置了[永不过期], 则立即返回 
 		if(tokenValue == null || getConfig().getActivityTimeout() == SaTokenDao.NEVER_EXPIRE) {
 			return;
 		}
 		// 删除[最后操作时间]
 		SaTokenManager.getSaTokenDao().deleteKey(getKeyLastActivityTime(tokenValue));
 		// 清除标记 
 		SaTokenManager.getSaTokenServlet().getRequest().removeAttribute(SaTokenInsideUtil.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY);
 	}
 	
 	/**
 	 * 检查指定token 是否已经[临时过期]，如果已经过期则抛出异常  
 	 * @param tokenValue 指定token
 	 */
 	public void checkActivityTimeout(String tokenValue) {
 		// 如果token == null 或者 设置了[永不过期], 则立即返回 
 		if(tokenValue == null || getConfig().getActivityTimeout() == SaTokenDao.NEVER_EXPIRE) {
 			return;
 		}
 		// 如果本次请求已经有了[检查标记], 则立即返回 
 		HttpServletRequest request = SaTokenManager.getSaTokenServlet().getRequest();
 		if(request.getAttribute(SaTokenInsideUtil.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY) != null) {
 			return;
 		}
 		// ------------ 验证是否已经 [临时过期] 
 		// 获取 [临时剩余时间]
 		long timeout = getTokenActivityTimeoutByToken(tokenValue);
 		// -1 代表此token已经被设置永不过期，无须继续验证 
 		if(timeout == SaTokenDao.NEVER_EXPIRE) {
 			return;
 		}
 		// -2 代表已过期，抛出异常 
 		if(timeout == SaTokenDao.NOT_VALUE_EXPIRE) {
 			throw NotLoginException.newInstance(loginKey, NotLoginException.TOKEN_TIMEOUT);
 		}
 		// --- 至此，验证已通过 

 		// 打上[检查标记]，标记一下当前请求已经通过验证，避免一次请求多次验证，造成不必要的性能消耗 
 		request.setAttribute(SaTokenInsideUtil.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY, true);
 	}

 	/**
 	 * 检查当前token 是否已经[临时过期]，如果已经过期则抛出异常  
 	 */
 	public void checkActivityTimeout() {
 		checkActivityTimeout(getTokenValue());
 	}
 	
 	/**
 	 * 续签指定token：(将 [最后操作时间] 更新为当前时间戳) 
 	 * @param tokenValue 指定token 
 	 */
 	public void updateLastActivityToNow(String tokenValue) {
 		// 如果token == null 或者 设置了[永不过期], 则立即返回 
 		if(tokenValue == null || getConfig().getActivityTimeout() == SaTokenDao.NEVER_EXPIRE) {
 			return;
 		}
 		SaTokenManager.getSaTokenDao().updateValue(getKeyLastActivityTime(tokenValue), String.valueOf(System.currentTimeMillis()));
 	}

 	/**
 	 * 续签当前token：(将 [最后操作时间] 更新为当前时间戳) 
 	 * <h1>请注意: 即时token已经 [临时过期] 也可续签成功，
 	 * 如果此场景下需要提示续签失败，可在此之前调用 checkActivityTimeout() 强制检查是否过期即可 </h1>
 	 */
 	public void updateLastActivityToNow() {
 		updateLastActivityToNow(getTokenValue());
 	}
 	
 	

	// =================== 过期时间相关 ===================  

 	/**
 	 * 获取当前登录者的token剩余有效时间 (单位: 秒)
 	 * @return token剩余有效时间
 	 */
 	public long getTokenTimeout() {
 		return SaTokenManager.getSaTokenDao().getTimeout(getKeyTokenValue(getTokenValue()));
 	}
 	
 	/**
 	 * 获取指定loginId的token剩余有效时间 (单位: 秒) 
 	 * @param loginId 指定loginId 
 	 * @return token剩余有效时间 
 	 */
 	public long getTokenTimeoutByLoginId(Object loginId) {
 		return SaTokenManager.getSaTokenDao().getTimeout(getKeyTokenValue(getTokenValueByLoginId(loginId)));
 	}

 	/**
 	 * 获取当前登录者的Session剩余有效时间 (单位: 秒)
 	 * @return token剩余有效时间
 	 */
 	public long getSessionTimeout() {
 		return getSessionTimeoutByLoginId(getLoginIdDefaultNull());
 	}
 	
 	/**
 	 * 获取指定loginId的Session剩余有效时间 (单位: 秒) 
 	 * @param loginId 指定loginId 
 	 * @return token剩余有效时间 
 	 */
 	public long getSessionTimeoutByLoginId(Object loginId) {
 		return SaTokenManager.getSaTokenDao().getSessionTimeout(getKeySession(loginId));
 	}

 	/**
 	 * 获取当前token的专属Session剩余有效时间 (单位: 秒) 
 	 * @return token剩余有效时间
 	 */
 	public long getTokenSessionTimeout() {
 		return getTokenSessionTimeoutByTokenValue(getTokenValue());
 	}
 	
 	/**
 	 * 获取指定token的专属Session剩余有效时间 (单位: 秒) 
 	 * @param tokenValue 指定token 
 	 * @return token剩余有效时间 
 	 */
 	public long getTokenSessionTimeoutByTokenValue(String tokenValue) {
 		return SaTokenManager.getSaTokenDao().getSessionTimeout(getKeyTokenSession(tokenValue));
 	}

 	
 	/**
 	 * 获取当前token[临时过期]剩余有效时间 (单位: 秒)
 	 * @return token[临时过期]剩余有效时间
 	 */
 	public long getTokenActivityTimeout() {
 		return getTokenActivityTimeoutByToken(getTokenValue());
 	}
 	
 	/**
 	 * 获取指定token[临时过期]剩余有效时间 (单位: 秒)
 	 * @param tokenValue 指定token 
 	 * @return token[临时过期]剩余有效时间
 	 */
 	public long getTokenActivityTimeoutByToken(String tokenValue) {
 		// 如果token为null , 则返回 -2
 		if(tokenValue == null) {
 			return SaTokenDao.NOT_VALUE_EXPIRE;
 		}
 		// 如果设置了永不过期, 则返回 -1 
 		if(getConfig().getActivityTimeout() == SaTokenDao.NEVER_EXPIRE) {
 			return SaTokenDao.NEVER_EXPIRE;
 		}
 		// ------ 开始查询 
 		// 获取相关数据 
 		String keyLastActivityTime = getKeyLastActivityTime(tokenValue);
 		String lastActivityTimeString = SaTokenManager.getSaTokenDao().getValue(keyLastActivityTime);
 		// 查不到，返回-2 
 		if(lastActivityTimeString == null) {
 			return SaTokenDao.NOT_VALUE_EXPIRE;
 		}
 		// 计算相差时间
 		long lastActivityTime = Long.valueOf(lastActivityTimeString);
 		long apartSecond = (System.currentTimeMillis() - lastActivityTime) / 1000;
 		long timeout = getConfig().getActivityTimeout() - apartSecond;
 		// 如果 < 0， 代表已经过期 ，返回-2 
 		if(timeout < 0) {
 			return SaTokenDao.NOT_VALUE_EXPIRE;
 		}
 		return timeout;
 	}
 	
 	

	// =================== 权限验证操作 ===================  

 	/** 
 	 * 指定账号id是否含有指定权限 
 	 * @param loginId 账号id
 	 * @param permissionCode 权限码
 	 * @return 是否含有指定权限
 	 */
 	public boolean hasPermission(Object loginId, Object permissionCode) {
 		List<Object> pcodeList = SaTokenManager.getStpInterface().getPermissionCodeList(loginId, loginKey);
		return !(pcodeList == null || pcodeList.contains(permissionCode) == false);
 	}
 	
 	/** 
 	 * 当前账号id是否含有指定权限 
 	 * @param permissionCode 权限码
 	 * @return 是否含有指定权限
 	 */
 	public boolean hasPermission(Object permissionCode) {
 		return hasPermission(getLoginId(), permissionCode);
 	}
	
 	/** 
 	 * 当前账号是否含有指定权限， 没有就抛出异常
 	 * @param permissionCode 权限码
 	 */
 	public void checkPermission(Object permissionCode) {
 		if(hasPermission(permissionCode) == false) {
			throw new NotPermissionException(permissionCode, this.loginKey);
		}
 	}

 	/** 
 	 * 当前账号是否含有指定权限, [指定多个，必须全都有]
 	 * @param permissionCodeArray 权限码数组
 	 */
 	public void checkPermissionAnd(Object... permissionCodeArray){
 		Object loginId = getLoginId();
 		List<Object> pcodeList = SaTokenManager.getStpInterface().getPermissionCodeList(loginId, loginKey);
 		for (Object pcode : permissionCodeArray) {
 			if(pcodeList.contains(pcode) == false) {
 				throw new NotPermissionException(pcode, this.loginKey);	// 没有权限抛出异常 
 			}
 		}
 	}

 	/** 
 	 * 当前账号是否含有指定权限, [指定多个，有一个就可以通过]
 	 * @param permissionCodeArray 权限码数组
 	 */
 	public void checkPermissionOr(Object... permissionCodeArray){
 		Object loginId = getLoginId();
 		List<Object> pcodeList = SaTokenManager.getStpInterface().getPermissionCodeList(loginId, loginKey);
 		for (Object pcode : permissionCodeArray) {
 			if(pcodeList.contains(pcode) == true) {
 				return;		// 有的话提前退出
 			}
 		}
		if(permissionCodeArray.length > 0) {
	 		throw new NotPermissionException(permissionCodeArray[0], this.loginKey);	// 没有权限抛出异常 
		}
 	}
 	
	
	// =================== 返回相应key ===================  

	/**
	 *  获取key：客户端 tokenName 
	 * @return key
	 */
	public String getKeyTokenName() {
 		return getConfig().getTokenName();
 	}
	/**  
	 * 获取key： tokenValue 持久化
	 * @param tokenValue token值
	 * @return key
	 */
	public String getKeyTokenValue(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginKey + ":token:" + tokenValue;
	}
	/** 
	 * 获取key： id 持久化
	 * @param loginId 账号id
	 * @return key
	 */
	public String getKeyLoginId(Object loginId) {
		return getConfig().getTokenName() + ":" + loginKey + ":id:" + loginId;
	}
	/** 
	 * 获取key： session 持久化  
	 * @param loginId 账号id
	 * @return key
	 */
	public String getKeySession(Object loginId) {
		return getConfig().getTokenName() + ":" + loginKey + ":session:" + loginId;
	}
	/**  
	 * 获取key： tokenValue的专属session 
	 * @param tokenValue token值
	 * @return key
	 */
	public String getKeyTokenSession(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginKey + ":token-session:" + tokenValue;
	}
	/** 
	 * 获取key： 指定token的最后操作时间 持久化 
	 * @param tokenValue token值
	 * @return key
	 */
	public String getKeyLastActivityTime(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginKey + ":last-activity:" + tokenValue;
	}
 	
	/**
	 * 返回配置对象
	 */
	public SaTokenConfig getConfig() {
		// 为什么再代理一层? 为某些极端业务场景下[需要不同StpLogic不同配置]提供便利 
		return SaTokenManager.getConfig();
	}
	
	
}

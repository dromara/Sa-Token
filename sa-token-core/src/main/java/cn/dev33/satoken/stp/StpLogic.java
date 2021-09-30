package cn.dev33.satoken.stp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.DisableLoginException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.TokenSign;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Sa-Token 权限认证，逻辑实现类 
 * @author kong
 */
public class StpLogic {

	/**
	 * 账号类型，多账号体系时以此值区分，比如：login、user、admin 
	 */
	public String loginType;
	
	/**
	 * 初始化StpLogic, 并指定账号类型 
	 * @param loginType 账号体系标识 
	 */
	public StpLogic(String loginType) {
		this.loginType = loginType;
		// 在 SaTokenManager 中记录下此 StpLogic，以便根据 LoginType 进行查找此对象 
		SaManager.putStpLogic(this);
	}

	/**
	 * 获取当前 StpLogic 的账号类型
	 * @return See Note 
	 */
	public String getLoginType(){
		return loginType;
	}

	/**
	 * 设置当前账号类型
	 * @param loginType loginType
	 * @return 对象自身
	 */
	public StpLogic setLoginType(String loginType){
		this.loginType = loginType;
		SaManager.putStpLogic(this);
		return this;
	}
	
	
	// ------------------- 获取token 相关 -------------------  
	
	/**
	 * 返回token名称 
	 * @return 此StpLogic的token名称
	 */
	public String getTokenName() {
 		return splicingKeyTokenName();
 	}

	/**
	 * 创建一个TokenValue
	 * @param loginId loginId
	 * @return 生成的tokenValue 
	 */
 	public String createTokenValue(Object loginId) {
 		return SaStrategy.me.createToken.apply(loginId, loginType);
	}
 	
 	/**
 	 * 在当前会话写入当前TokenValue 
 	 * @param tokenValue token值 
 	 * @param cookieTimeout Cookie存活时间(秒)
 	 */
	public void setTokenValue(String tokenValue, int cookieTimeout){
		SaTokenConfig config = getConfig();
		
		// 1. 将token保存到[存储器]里  
		SaStorage storage = SaHolder.getStorage();
		
		// 如果打开了token前缀模式，则拼接上前缀一起写入 
		String tokenPrefix = config.getTokenPrefix();
		if(SaFoxUtil.isEmpty(tokenPrefix) == false) {
			storage.set(splicingKeyJustCreatedSave(), tokenPrefix + SaTokenConsts.TOKEN_CONNECTOR_CHAT + tokenValue);	
		} else {
			storage.set(splicingKeyJustCreatedSave(), tokenValue);	
		}
		
		// 2. 将token保存到[Cookie]里 
		if (config.getIsReadCookie()) {
			SaResponse response = SaHolder.getResponse();
			response.addCookie(getTokenName(), tokenValue, "/", 
					config.getCookieDomain(), cookieTimeout, config.getIsCookieHttpOnly(), config.getIsCookieSecure());
		}
	}
 	
	/**
	 * 获取当前TokenValue
	 * @return 当前tokenValue
	 */
	public String getTokenValue(){
		// 0. 获取相应对象 
		SaStorage storage = SaHolder.getStorage();
		SaRequest request = SaHolder.getRequest();
		SaTokenConfig config = getConfig();
		String keyTokenName = getTokenName();
		String tokenValue = null;
		
		// 1. 尝试从Storage里读取 
		if(storage.get(splicingKeyJustCreatedSave()) != null) {
			tokenValue = String.valueOf(storage.get(splicingKeyJustCreatedSave()));
		}
		// 2. 尝试从请求体里面读取 
		if(tokenValue == null && config.getIsReadBody()){
			tokenValue = request.getParam(keyTokenName);
		}
		// 3. 尝试从header里读取 
		if(tokenValue == null && config.getIsReadHead()){
			tokenValue = request.getHeader(keyTokenName);
		}
		// 4. 尝试从cookie里读取 
		if(tokenValue == null && config.getIsReadCookie()){
			tokenValue = request.getCookieValue(keyTokenName);
		}
		
		// 5. 如果打开了前缀模式 
		String tokenPrefix = getConfig().getTokenPrefix();
		if(SaFoxUtil.isEmpty(tokenPrefix) == false) {
			// 如果token并没有按照指定的前缀开头，则视为未提供token 
			if(SaFoxUtil.isEmpty(tokenValue) || tokenValue.startsWith(tokenPrefix + SaTokenConsts.TOKEN_CONNECTOR_CHAT) == false) {
				tokenValue = null;
			} else {
				// 则裁剪掉前缀 
				tokenValue = tokenValue.substring(tokenPrefix.length() + SaTokenConsts.TOKEN_CONNECTOR_CHAT.length());
			}
		}
		
		// 6. 返回 
		return tokenValue;
	}
	
	/**
	 * 获取当前会话的Token信息 
	 * @return token信息 
	 */
	public SaTokenInfo getTokenInfo() {
		SaTokenInfo info = new SaTokenInfo();
		info.tokenName = getTokenName();
		info.tokenValue = getTokenValue();
		info.isLogin = isLogin();
		info.loginId = getLoginIdDefaultNull();
		info.loginType = getLoginType();
		info.tokenTimeout = getTokenTimeout();
		info.sessionTimeout = getSessionTimeout();
		info.tokenSessionTimeout = getTokenSessionTimeout();
		info.tokenActivityTimeout = getTokenActivityTimeout();
		info.loginDevice = getLoginDevice();
		return info;
	}
	
	
	// ------------------- 登录相关操作 -------------------  

	// --- 登录 
	
	/**
	 * 会话登录 
	 * @param id 账号id，建议的类型：（long | int | String）
	 */
	public void login(Object id) {
		login(id, new SaLoginModel());
	}

	/**
	 * 会话登录，并指定登录设备 
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param device 设备标识 
	 */
	public void login(Object id, String device) {
		login(id, new SaLoginModel().setDevice(device));
	}

	/**
	 * 会话登录，并指定是否 [记住我] 
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param isLastingCookie 是否为持久Cookie 
	 */
	public void login(Object id, boolean isLastingCookie) {
		login(id, new SaLoginModel().setIsLastingCookie(isLastingCookie));
	}
	
	/**
	 * 会话登录，并指定所有登录参数Model 
	 * @param id 登录id，建议的类型：（long | int | String）
	 * @param loginModel 此次登录的参数Model 
	 */
	public void login(Object id, SaLoginModel loginModel) {
		
		SaTokenException.throwByNull(id, "账号id不能为空");
		
		// ------ 0、前置检查：如果此账号已被封禁. 
		if(isDisable(id)) {
			throw new DisableLoginException(loginType, id, getDisableTime(id));
		}
		
		// ------ 1、初始化 loginModel 
		SaTokenConfig config = getConfig();
		loginModel.build(config);
		
		// ------ 2、生成一个token  
		String tokenValue = null;
		// --- 如果允许并发登录 
		if(config.getIsConcurrent()) {
			// 如果配置为共享token, 则尝试从Session签名记录里取出token 
			if(config.getIsShare()) {
				tokenValue = getTokenValueByLoginId(id, loginModel.getDevice());
			}
		} else {
			// --- 如果不允许并发登录，则将这个账号的历史登录标记为：被顶下线 
			replaced(id, loginModel.getDevice());
		}
		// 如果至此，仍未成功创建tokenValue, 则开始生成一个 
		if(tokenValue == null) {
			tokenValue = createTokenValue(id);
		}
		
		// ------ 3. 获取 User-Session , 续期 
		SaSession session = getSessionByLoginId(id, true);
		session.updateMinTimeout(loginModel.getTimeout());
		
		// 在 User-Session 上记录token签名 
		session.addTokenSign(tokenValue, loginModel.getDevice());
		
		// ------ 4. 持久化其它数据 
		// token -> id 映射关系  
		saveTokenToIdMapping(tokenValue, id, loginModel.getTimeout());
		
		// 在当前会话写入tokenValue 
		setTokenValue(tokenValue, loginModel.getCookieTimeout());

		// 写入 [token-last-activity] 
		setLastActivityToNow(tokenValue); 
		
		// $$ 通知监听器，账号xxx 登录成功 
		SaManager.getSaTokenListener().doLogin(loginType, id, loginModel);
	}

	// --- 注销 
	
	/** 
	 * 会话注销 
	 */
	public void logout() {
		// 如果连token都没有，那么无需执行任何操作 
		String tokenValue = getTokenValue();
 		if(SaFoxUtil.isEmpty(tokenValue)) {
 			return;
 		}
 		// 如果打开了Cookie模式，第一步，先把cookie清除掉 
 		if(getConfig().getIsReadCookie()){
 			SaHolder.getResponse().deleteCookie(getTokenName());
		}
 		logoutByTokenValue(tokenValue);
	}

	/**
	 * 会话注销，根据账号id 
	 * 
	 * @param loginId 账号id 
	 */
	public void logout(Object loginId) {
		logout(loginId, null);
	}
	
	/**
	 * 会话注销，根据账号id 和 设备标识 
	 * 
	 * @param loginId 账号id 
	 * @param device 设备标识 (填null代表所有注销设备) 
	 */
	public void logout(Object loginId, String device) {
		clearTokenCommonMethod(loginId, device, tokenValue -> {
	 		// 删除Token-Id映射 & 清除Token-Session 
			deleteTokenToIdMapping(tokenValue);
			deleteTokenSession(tokenValue);
			SaManager.getSaTokenListener().doLogout(loginType, loginId, tokenValue);
		}, true);
	}
	
	/**
	 * 会话注销，根据指定 Token 
	 * 
	 * @param tokenValue 指定token
	 */
	public void logoutByTokenValue(String tokenValue) {
		// 1. 清理 token-last-activity
		clearLastActivity(tokenValue); 	
		
		// 2. 注销 Token-Session 
		deleteTokenSession(tokenValue);

		// if. 无效 loginId 立即返回 
 		String loginId = getLoginIdNotHandle(tokenValue);
 	 	if(isValidLoginId(loginId) == false) {
 	 		if(loginId != null) {
 	 			deleteTokenToIdMapping(tokenValue);
 	 		}
 			return;
 		}
 		// 3. 清理token-id索引 
 	 	deleteTokenToIdMapping(tokenValue);
 	 	
 	 	// $$ 通知监听器，某某Token注销下线了 
		SaManager.getSaTokenListener().doLogout(loginType, loginId, tokenValue);

		// 4. 清理User-Session上的token签名 & 尝试注销User-Session 
 	 	SaSession session = getSessionByLoginId(loginId, false);
 	 	if(session != null) {
 	 	 	session.removeTokenSign(tokenValue); 
 			session.logoutByTokenSignCountToZero();
 	 	}
	}
	
	/**
	 * 踢人下线，根据账号id 
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-5 </p>
	 * 
	 * @param loginId 账号id 
	 */
	public void kickout(Object loginId) {
		kickout(loginId, null);
	}
	
	/**
	 * 踢人下线，根据账号id 和 设备标识 
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-5 </p>
	 * 
	 * @param loginId 账号id 
	 * @param device 设备标识 (填null代表踢出所有设备) 
	 */
	public void kickout(Object loginId, String device) {
		clearTokenCommonMethod(loginId, device, tokenValue -> {
			// 将此 token 标记为已被踢下线  
			updateTokenToIdMapping(tokenValue, NotLoginException.KICK_OUT);
	 		SaManager.getSaTokenListener().doKickout(loginType, loginId, tokenValue);
		}, true);
	}

	/**
	 * 踢人下线，根据指定 Token 
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-5 </p>
	 * 
	 * @param tokenValue 指定token
	 */
	public void kickoutByTokenValue(String tokenValue) {
		// 1. 清理 token-last-activity
		clearLastActivity(tokenValue); 	
		
		// 2. 不注销 Token-Session 

 		// if. 无效 loginId 立即返回 
 		String loginId = getLoginIdNotHandle(tokenValue);
 	 	if(isValidLoginId(loginId) == false) {
 			return;
 		}
 	 	
 		// 3. 给token打上标记：被踢下线 
 	 	updateTokenToIdMapping(tokenValue, NotLoginException.KICK_OUT);
		
 	 	// $$. 否则通知监听器，某某Token被踢下线了 
		SaManager.getSaTokenListener().doKickout(loginType, loginId, tokenValue);

		// 4. 清理User-Session上的token签名 & 尝试注销User-Session 
 	 	SaSession session = getSessionByLoginId(loginId, false);
 	 	if(session != null) {
 	 	 	session.removeTokenSign(tokenValue); 
 			session.logoutByTokenSignCountToZero();
 	 	}
	}
	
	/**
	 * 顶人下线，根据账号id 和 设备标识 
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-4 </p>
	 * 
	 * @param loginId 账号id 
	 * @param device 设备标识 (填null代表顶替所有设备) 
	 */
	public void replaced(Object loginId, String device) {
		clearTokenCommonMethod(loginId, device, tokenValue -> {
			// 将此 token 标记为已被顶替 
			updateTokenToIdMapping(tokenValue, NotLoginException.BE_REPLACED);
	 		SaManager.getSaTokenListener().doReplaced(loginType, loginId, tokenValue);
		}, false);
	}
	
	/**
	 * 封装 注销、踢人、顶人 三个动作的相同代码（无API含义方法）
	 * @param loginId 账号id 
	 * @param device 设备标识 
	 * @param appendFun 追加操作 
	 * @param isLogoutSession 是否注销 User-Session 
	 */
	private void clearTokenCommonMethod(Object loginId, String device, Consumer<String> appendFun, boolean isLogoutSession) {
		// 1. 如果此账号尚未登录，则不执行任何操作 
		SaSession session = getSessionByLoginId(loginId, false);
		if(session == null) {
			return;
		}
		// 2. 循环token签名列表，开始删除相关信息 
		for (TokenSign tokenSign : session.getTokenSignList()) {
			if(device == null || tokenSign.getDevice().equals(device)) {
				// -------- 共有操作 
				// s1. 获取token 
				String tokenValue = tokenSign.getValue();
				// s2. 清理掉[token-last-activity] 
				clearLastActivity(tokenValue); 	
		 		// s3. 从token签名列表移除 
		 		session.removeTokenSign(tokenValue); 
				// -------- 追加操作 
		 		appendFun.accept(tokenValue);
			}
		}
 	 	// 3. 尝试注销session 
		if(isLogoutSession) {
			session.logoutByTokenSignCountToZero();
		}
	}
	
	// ---- 会话查询 
	
 	/** 
 	 * 当前会话是否已经登录 
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
		// 如果正在[临时身份切换], 则返回临时身份 
		if(isSwitch()) {
			return getSwitchLoginId();
		}
 		// 如果获取不到token，则抛出: 无token
 		String tokenValue = getTokenValue();
 		if(tokenValue == null) {
 			throw NotLoginException.newInstance(loginType, NotLoginException.NOT_TOKEN);
 		}
 		// 查找此token对应loginId, 如果找不到则抛出：无效token 
 		String loginId = getLoginIdNotHandle(tokenValue);
 		if(loginId == null) {
 			throw NotLoginException.newInstance(loginType, NotLoginException.INVALID_TOKEN);
 		}
 		// 如果是已经过期，则抛出已经过期 
 		if(loginId.equals(NotLoginException.TOKEN_TIMEOUT)) {
 			throw NotLoginException.newInstance(loginType, NotLoginException.TOKEN_TIMEOUT);
 		}
 		// 如果是已经被顶替下去了, 则抛出：已被顶下线 
 		if(loginId.equals(NotLoginException.BE_REPLACED)) {
 			throw NotLoginException.newInstance(loginType, NotLoginException.BE_REPLACED);
 		}
 		// 如果是已经被踢下线了, 则抛出：已被踢下线 
 		if(loginId.equals(NotLoginException.KICK_OUT)) {
 			throw NotLoginException.newInstance(loginType, NotLoginException.KICK_OUT);
 		}
 		// 检查是否已经 [临时过期]
	 	checkActivityTimeout(tokenValue);
 		// 如果配置了自动续签, 则: 更新[最后操作时间] 
 		if(getConfig().getAutoRenew()) {
 	 		updateLastActivityToNow(tokenValue);
 		}
 		// 至此，返回loginId 
 		return loginId;
 	}
	
 	/** 
	 * 获取当前会话账号id, 如果未登录，则返回默认值 
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
	 * 获取当前会话账号id, 如果未登录，则返回null 
	 * @return 账号id 
	 */
	public Object getLoginIdDefaultNull() {
		// 如果正在[临时身份切换]
		if(isSwitch()) {
			return getSwitchLoginId();
		}
		// 如果连token都是空的，则直接返回 
		String tokenValue = getTokenValue();
 		if(tokenValue == null) {
 			return null;
 		}
 		// loginId为null或者在异常项里面，均视为未登录, 返回null 
 		Object loginId = getLoginIdNotHandle(tokenValue);
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
	 * 获取当前会话账号id, 并转换为String类型
	 * @return 账号id 
	 */
 	public String getLoginIdAsString() {
 		return String.valueOf(getLoginId());
 	}

 	/** 
	 * 获取当前会话账号id, 并转换为int类型
	 * @return 账号id 
	 */
 	public int getLoginIdAsInt() {
 		return Integer.parseInt(String.valueOf(getLoginId()));
 	}

 	/**
	 * 获取当前会话账号id, 并转换为long类型 
	 * @return 账号id 
	 */
 	public long getLoginIdAsLong() {
 		return Long.parseLong(String.valueOf(getLoginId()));
 	}
 	
 	/** 
 	 * 获取指定Token对应的账号id，如果未登录，则返回 null 
 	 * @param tokenValue token
 	 * @return 账号id
 	 */
 	public Object getLoginIdByToken(String tokenValue) {
 		if(tokenValue == null) {
 	 		return null;
 		}
 		return getLoginIdNotHandle(tokenValue);
 	}
 	
 	 /**
 	  * 获取指定Token对应的账号id (不做任何特殊处理) 
 	  * @param tokenValue token值 
 	  * @return 账号id
 	  */
 	public String getLoginIdNotHandle(String tokenValue) {
 		return SaManager.getSaTokenDao().get(splicingKeyTokenValue(tokenValue));
 	}
 	
	// ---- 其它操作 
 	/**
	 * 判断一个 loginId 是否是有效的 
	 * @param loginId 账号id 
	 * @return / 
	 */
	public boolean isValidLoginId(Object loginId) {
		return loginId != null && !NotLoginException.ABNORMAL_LIST.contains(loginId.toString());
	}
	/**
	 * 删除 Token-Id 映射 
	 * @param tokenValue token值 
	 */
	public void deleteTokenToIdMapping(String tokenValue) {
		SaManager.getSaTokenDao().delete(splicingKeyTokenValue(tokenValue));
	}
	/**
	 * 更改 Token 指向的 账号Id 值 
	 * @param tokenValue token值 
	 * @param loginId 新的账号Id值
	 */
	public void updateTokenToIdMapping(String tokenValue, Object loginId) {
		SaTokenException.throwBy(SaFoxUtil.isEmpty(loginId), "LoginId 不能为空");
		SaManager.getSaTokenDao().update(splicingKeyTokenValue(tokenValue), loginId.toString());
	}
	/**
	 * 存储 Token-Id 映射 
	 * @param tokenValue token值 
	 * @param loginId 账号id 
	 * @param timeout 会话有效期 (单位: 秒) 
	 */
	public void saveTokenToIdMapping(String tokenValue, Object loginId, long timeout) {
		SaManager.getSaTokenDao().set(splicingKeyTokenValue(tokenValue), String.valueOf(loginId), timeout);
	}
	
	
	
	// ------------------- User-Session 相关 -------------------  

	/** 
	 * 获取指定key的Session, 如果Session尚未创建，isCreate=是否新建并返回
	 * @param sessionId SessionId
	 * @param isCreate 是否新建
	 * @return Session对象 
	 */
	public SaSession getSessionBySessionId(String sessionId, boolean isCreate) {
		SaSession session = SaManager.getSaTokenDao().getSession(sessionId);
		if(session == null && isCreate) {
			session = SaStrategy.me.createSession.apply(sessionId);
			SaManager.getSaTokenDao().setSession(session, getConfig().getTimeout());
		}
		return session;
	}

	/** 
	 * 获取指定key的Session, 如果Session尚未创建，则返回null
	 * @param sessionId SessionId
	 * @return Session对象 
	 */
	public SaSession getSessionBySessionId(String sessionId) {
		return getSessionBySessionId(sessionId, false);
	}

	/** 
	 * 获取指定账号id的User-Session, 如果Session尚未创建，isCreate=是否新建并返回
	 * @param loginId 账号id
	 * @param isCreate 是否新建
	 * @return Session对象
	 */
	public SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
		return getSessionBySessionId(splicingKeySession(loginId), isCreate);
	}

	/** 
	 * 获取指定账号id的User-Session，如果Session尚未创建，则新建并返回 
	 * @param loginId 账号id 
	 * @return Session对象 
	 */
	public SaSession getSessionByLoginId(Object loginId) {
		return getSessionBySessionId(splicingKeySession(loginId), true);
	}

	/** 
	 * 获取当前User-Session, 如果Session尚未创建，isCreate=是否新建并返回 
	 * @param isCreate 是否新建 
	 * @return Session对象 
	 */
	public SaSession getSession(boolean isCreate) {
		return getSessionByLoginId(getLoginId(), isCreate);
	}
	
	/** 
	 * 获取当前User-Session，如果Session尚未创建，则新建并返回 
	 * @return Session对象 
	 */
	public SaSession getSession() {
		return getSession(true);
	}

	
	// ------------------- Token-Session 相关 -------------------  

	/** 
	 * 获取指定Token-Session，如果Session尚未创建，isCreate代表是否新建并返回
	 * @param tokenValue token值
	 * @param isCreate 是否新建 
	 * @return session对象
	 */
	public SaSession getTokenSessionByToken(String tokenValue, boolean isCreate) {
		return getSessionBySessionId(splicingKeyTokenSession(tokenValue), isCreate);
	}
	
	/** 
	 * 获取指定Token-Session，如果Session尚未创建，则新建并返回 
	 * @param tokenValue Token值
	 * @return Session对象  
	 */
	public SaSession getTokenSessionByToken(String tokenValue) {
		return getSessionBySessionId(splicingKeyTokenSession(tokenValue), true);
	}

	/** 
	 * 获取当前Token-Session，如果Session尚未创建，isCreate代表是否新建并返回 
	 * @param isCreate 是否新建 
	 * @return Session对象  
	 */
	public SaSession getTokenSession(boolean isCreate) {
		// 如果配置了需要校验登录状态，则验证一下
		if(getConfig().getTokenSessionCheckLogin()) {
			checkLogin();
		} else {
			// 如果配置忽略token登录校验，则必须保证token不为null (token为null的时候随机创建一个) 
			String tokenValue = getTokenValue();
			if(tokenValue == null || Objects.equals(tokenValue, "")) {
				// 随机一个token送给Ta 
				tokenValue = createTokenValue(null);
				// 写入 [最后操作时间]
				setLastActivityToNow(tokenValue);  
				// 在当前会话写入这个tokenValue 
				int cookieTimeout = (int)(getConfig().getTimeout() == SaTokenDao.NEVER_EXPIRE ? Integer.MAX_VALUE : getConfig().getTimeout());
				setTokenValue(tokenValue, cookieTimeout);
			}
		}
		// 返回这个token对应的Token-Session 
		return getSessionBySessionId(splicingKeyTokenSession(getTokenValue()), isCreate);
	}
	
	/** 
	 * 获取当前Token-Session，如果Session尚未创建，则新建并返回
	 * @return Session对象 
	 */
	public SaSession getTokenSession() {
		return getTokenSession(true);
	}
	
	/**
	 * 删除Token-Session 
	 * @param tokenValue token值 
	 */
	public void deleteTokenSession(String tokenValue) {
		SaManager.getSaTokenDao().delete(splicingKeyTokenSession(tokenValue));
	}
 	
	// ------------------- [临时过期] 验证相关 -------------------  

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
 		SaManager.getSaTokenDao().set(splicingKeyLastActivityTime(tokenValue), String.valueOf(System.currentTimeMillis()), getConfig().getTimeout());
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
 		SaManager.getSaTokenDao().delete(splicingKeyLastActivityTime(tokenValue));
 		// 清除标记 
 		SaHolder.getStorage().delete(SaTokenConsts.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY);
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
 		SaStorage storage = SaHolder.getStorage();
 		if(storage.get(SaTokenConsts.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY) != null) {
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
 			throw NotLoginException.newInstance(loginType, NotLoginException.TOKEN_TIMEOUT);
 		}
 		// --- 至此，验证已通过 

 		// 打上[检查标记]，标记一下当前请求已经通过验证，避免一次请求多次验证，造成不必要的性能消耗 
 		storage.set(SaTokenConsts.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY, true);
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
 		SaManager.getSaTokenDao().update(splicingKeyLastActivityTime(tokenValue), String.valueOf(System.currentTimeMillis()));
 	}

 	/**
 	 * 续签当前token：(将 [最后操作时间] 更新为当前时间戳) 
 	 * <h1>请注意: 即时token已经 [临时过期] 也可续签成功，
 	 * 如果此场景下需要提示续签失败，可在此之前调用 checkActivityTimeout() 强制检查是否过期即可 </h1>
 	 */
 	public void updateLastActivityToNow() {
 		updateLastActivityToNow(getTokenValue());
 	}
 	
 	
	// ------------------- 过期时间相关 -------------------  

 	/**
 	 * 获取当前登录者的 token 剩余有效时间 (单位: 秒)
 	 * @return token剩余有效时间
 	 */
 	public long getTokenTimeout() {
 		return SaManager.getSaTokenDao().getTimeout(splicingKeyTokenValue(getTokenValue()));
 	}
 	
 	/**
 	 * 获取指定 loginId 的 token 剩余有效时间 (单位: 秒) 
 	 * @param loginId 指定loginId 
 	 * @return token剩余有效时间 
 	 */
 	public long getTokenTimeoutByLoginId(Object loginId) {
 		return SaManager.getSaTokenDao().getTimeout(splicingKeyTokenValue(getTokenValueByLoginId(loginId)));
 	}

 	/**
 	 * 获取当前登录者的 User-Session 剩余有效时间 (单位: 秒)
 	 * @return token剩余有效时间
 	 */
 	public long getSessionTimeout() {
 		return getSessionTimeoutByLoginId(getLoginIdDefaultNull());
 	}
 	
 	/**
 	 * 获取指定 loginId 的 User-Session 剩余有效时间 (单位: 秒) 
 	 * @param loginId 指定loginId 
 	 * @return token剩余有效时间 
 	 */
 	public long getSessionTimeoutByLoginId(Object loginId) {
 		return SaManager.getSaTokenDao().getSessionTimeout(splicingKeySession(loginId));
 	}

 	/**
 	 * 获取当前 Token-Session 剩余有效时间 (单位: 秒) 
 	 * @return token剩余有效时间
 	 */
 	public long getTokenSessionTimeout() {
 		return getTokenSessionTimeoutByTokenValue(getTokenValue());
 	}
 	
 	/**
 	 * 获取指定 Token-Session 剩余有效时间 (单位: 秒) 
 	 * @param tokenValue 指定token 
 	 * @return token剩余有效时间 
 	 */
 	public long getTokenSessionTimeoutByTokenValue(String tokenValue) {
 		return SaManager.getSaTokenDao().getSessionTimeout(splicingKeyTokenSession(tokenValue));
 	}

 	/**
 	 * 获取当前 token [临时过期] 剩余有效时间 (单位: 秒)
 	 * @return token [临时过期] 剩余有效时间
 	 */
 	public long getTokenActivityTimeout() {
 		return getTokenActivityTimeoutByToken(getTokenValue());
 	}
 	
 	/**
 	 * 获取指定 token [临时过期] 剩余有效时间 (单位: 秒)
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
 		String keyLastActivityTime = splicingKeyLastActivityTime(tokenValue);
 		String lastActivityTimeString = SaManager.getSaTokenDao().get(keyLastActivityTime);
 		// 查不到，返回-2 
 		if(lastActivityTimeString == null) {
 			return SaTokenDao.NOT_VALUE_EXPIRE;
 		}
 		// 计算相差时间
 		long lastActivityTime = Long.parseLong(lastActivityTimeString);
 		long apartSecond = (System.currentTimeMillis() - lastActivityTime) / 1000;
 		long timeout = getConfig().getActivityTimeout() - apartSecond;
 		// 如果 < 0， 代表已经过期 ，返回-2 
 		if(timeout < 0) {
 			return SaTokenDao.NOT_VALUE_EXPIRE;
 		}
 		return timeout;
 	}

 	
	// ------------------- 角色验证操作 -------------------  

 	/** 
 	 * 指定账号id是否含有角色标识, 返回true或false 
 	 * @param loginId 账号id
 	 * @param role 角色标识
 	 * @return 是否含有指定角色标识
 	 */
 	public boolean hasRole(Object loginId, String role) {
 		List<String> roleList = SaManager.getStpInterface().getRoleList(loginId, loginType);
 		return SaStrategy.me.hasElement.apply(roleList, role);
 	}
 	
 	/** 
 	 * 当前账号是否含有指定角色标识, 返回true或false 
 	 * @param role 角色标识
 	 * @return 是否含有指定角色标识
 	 */
 	public boolean hasRole(String role) {
 		return hasRole(getLoginId(), role);
 	}
	
 	/** 
 	 * 当前账号是否含有指定角色标识, 如果验证未通过，则抛出异常: NotRoleException 
 	 * @param role 角色标识
 	 */
 	public void checkRole(String role) {
 		if(!hasRole(role)) {
			throw new NotRoleException(role, this.loginType);
		}
 	}

 	/** 
 	 * 当前账号是否含有指定角色标识 [指定多个，必须全部验证通过] 
 	 * @param roleArray 角色标识数组
 	 */
 	public void checkRoleAnd(String... roleArray){
 		Object loginId = getLoginId();
 		List<String> roleList = SaManager.getStpInterface().getRoleList(loginId, loginType);
 		for (String role : roleArray) {
 			if(!SaStrategy.me.hasElement.apply(roleList, role)) {
 				throw new NotRoleException(role, this.loginType);
 			}
 		}
 	}

 	/** 
 	 * 当前账号是否含有指定角色标识 [指定多个，只要其一验证通过即可] 
 	 * @param roleArray 角色标识数组
 	 */
 	public void checkRoleOr(String... roleArray){
 		Object loginId = getLoginId();
 		List<String> roleList = SaManager.getStpInterface().getRoleList(loginId, loginType);
 		for (String role : roleArray) {
 			if(SaStrategy.me.hasElement.apply(roleList, role)) {
 				// 有的话提前退出
 				return;		
 			}
 		}
		if(roleArray.length > 0) {
	 		throw new NotRoleException(roleArray[0], this.loginType);
		}
 	}
 	
 	
	// ------------------- 权限验证操作 -------------------  

 	/** 
 	 * 指定账号id是否含有指定权限, 返回true或false 
 	 * @param loginId 账号id
 	 * @param permission 权限码
 	 * @return 是否含有指定权限
 	 */
 	public boolean hasPermission(Object loginId, String permission) {
 		List<String> permissionList = SaManager.getStpInterface().getPermissionList(loginId, loginType);
 		return SaStrategy.me.hasElement.apply(permissionList, permission);
 	}
 	
 	/** 
 	 * 当前账号是否含有指定权限, 返回true或false 
 	 * @param permission 权限码
 	 * @return 是否含有指定权限
 	 */
 	public boolean hasPermission(String permission) {
 		return hasPermission(getLoginId(), permission);
 	}
	
 	/** 
 	 * 当前账号是否含有指定权限, 如果验证未通过，则抛出异常: NotPermissionException 
 	 * @param permission 权限码
 	 */
 	public void checkPermission(String permission) {
 		if(hasPermission(permission) == false) {
			throw new NotPermissionException(permission, this.loginType);
		}
 	}

 	/** 
 	 * 当前账号是否含有指定权限 [指定多个，必须全部验证通过] 
 	 * @param permissionArray 权限码数组
 	 */
 	public void checkPermissionAnd(String... permissionArray){
 		Object loginId = getLoginId();
 		List<String> permissionList = SaManager.getStpInterface().getPermissionList(loginId, loginType);
 		for (String permission : permissionArray) {
 			if(!SaStrategy.me.hasElement.apply(permissionList, permission)) {
 				throw new NotPermissionException(permission, this.loginType);	
 			}
 		}
 	}

 	/** 
 	 * 当前账号是否含有指定权限 [指定多个，只要其一验证通过即可] 
 	 * @param permissionArray 权限码数组
 	 */
 	public void checkPermissionOr(String... permissionArray){
 		Object loginId = getLoginId();
 		List<String> permissionList = SaManager.getStpInterface().getPermissionList(loginId, loginType);
 		for (String permission : permissionArray) {
 			if(SaStrategy.me.hasElement.apply(permissionList, permission)) {
 				// 有的话提前退出
 				return;		
 			}
 		}
		if(permissionArray.length > 0) {
	 		throw new NotPermissionException(permissionArray[0], this.loginType);
		}
 	}


 	
	
	// ------------------- id 反查token 相关操作 -------------------  
	
	/** 
	 * 获取指定账号id的tokenValue 
	 * <p> 在配置为允许并发登录时，此方法只会返回队列的最后一个token，
	 * 如果你需要返回此账号id的所有token，请调用 getTokenValueListByLoginId 
	 * @param loginId 账号id
	 * @return token值 
	 */
	public String getTokenValueByLoginId(Object loginId) {
		return getTokenValueByLoginId(loginId, SaTokenConsts.DEFAULT_LOGIN_DEVICE);
	}

	/** 
	 * 获取指定账号id指定设备端的tokenValue 
	 * <p> 在配置为允许并发登录时，此方法只会返回队列的最后一个token，
	 * 如果你需要返回此账号id的所有token，请调用 getTokenValueListByLoginId 
	 * @param loginId 账号id
	 * @param device 设备标识 
	 * @return token值 
	 */
	public String getTokenValueByLoginId(Object loginId, String device) {
		List<String> tokenValueList = getTokenValueListByLoginId(loginId, device);
		return tokenValueList.size() == 0 ? null : tokenValueList.get(tokenValueList.size() - 1);
	}
	
 	/** 
	 * 获取指定账号id的tokenValue集合 
	 * @param loginId 账号id 
	 * @return 此loginId的所有相关token 
 	 */
	public List<String> getTokenValueListByLoginId(Object loginId) {
		return getTokenValueListByLoginId(loginId, SaTokenConsts.DEFAULT_LOGIN_DEVICE);
	}

 	/** 
	 * 获取指定账号id指定设备端的tokenValue 集合 
	 * @param loginId 账号id 
	 * @param device 设备标识 
	 * @return 此loginId的所有相关token 
 	 */
	public List<String> getTokenValueListByLoginId(Object loginId, String device) {
		// 如果session为null的话直接返回空集合  
		SaSession session = getSessionByLoginId(loginId, false);
		if(session == null) {
			return Collections.emptyList();
		}
		// 遍历解析 
		List<TokenSign> tokenSignList = session.getTokenSignList();
		List<String> tokenValueList = new ArrayList<>();
		for (TokenSign tokenSign : tokenSignList) {
			if(tokenSign.getDevice().equals(device)) {
				tokenValueList.add(tokenSign.getValue());
			}
		}
		return tokenValueList;
	}
	
	/**
	 * 返回当前会话的登录设备 
	 * @return 当前令牌的登录设备 
	 */
	public String getLoginDevice() {
		// 如果没有token，直接返回 null 
		String tokenValue = getTokenValue();
		if(tokenValue == null) {
			return null;
		}
		// 如果还未登录，直接返回 null 
		if(!isLogin()) {
			return null;
		}
		// 如果session为null的话直接返回 null 
		SaSession session = getSessionByLoginId(getLoginIdDefaultNull(), false);
		if(session == null) {
			return null;
		}
		// 遍历解析 
		List<TokenSign> tokenSignList = session.getTokenSignList();
		for (TokenSign tokenSign : tokenSignList) {
			if(tokenSign.getValue().equals(tokenValue)) {
				return tokenSign.getDevice();
			}
		}
		return null;
	}
	

	// ------------------- 会话管理 -------------------  

	/**
	 * 根据条件查询Token 
	 * @param keyword 关键字 
	 * @param start 开始处索引 (-1代表查询所有) 
	 * @param size 获取数量 
	 * @return token集合 
	 */
	public List<String> searchTokenValue(String keyword, int start, int size) {
		return SaManager.getSaTokenDao().searchData(splicingKeyTokenValue(""), keyword, start, size);
	}
	
	/**
	 * 根据条件查询SessionId 
	 * @param keyword 关键字 
	 * @param start 开始处索引 (-1代表查询所有) 
	 * @param size 获取数量 
	 * @return sessionId集合 
	 */
	public List<String> searchSessionId(String keyword, int start, int size) {
		return SaManager.getSaTokenDao().searchData(splicingKeySession(""), keyword, start, size);
	}

	/**
	 * 根据条件查询Token专属Session的Id 
	 * @param keyword 关键字 
	 * @param start 开始处索引 (-1代表查询所有) 
	 * @param size 获取数量 
	 * @return sessionId集合 
	 */
	public List<String> searchTokenSessionId(String keyword, int start, int size) {
		return SaManager.getSaTokenDao().searchData(splicingKeyTokenSession(""), keyword, start, size);
	}
	

	// ------------------- 其它方法 -------------------  

	/**
	 * 根据注解(@SaCheckLogin)鉴权
	 * @param at 注解对象 
	 */
	public void checkByAnnotation(SaCheckLogin at) {
		this.checkLogin();
	}

	/**
	 * 根据注解(@SaCheckRole)鉴权
	 * @param at 注解对象 
	 */
	public void checkByAnnotation(SaCheckRole at) {
		String[] roleArray = at.value();
		if(at.mode() == SaMode.AND) {
			this.checkRoleAnd(roleArray);	
		} else {
			this.checkRoleOr(roleArray);	
		}
	}
	
	/**
	 * 根据注解(@SaCheckPermission)鉴权
	 * @param at 注解对象 
	 */
	public void checkByAnnotation(SaCheckPermission at) {
		String[] permissionArray = at.value();
		if(at.mode() == SaMode.AND) {
			this.checkPermissionAnd(permissionArray);	
		} else {
			this.checkPermissionOr(permissionArray);	
		}
	}

	/**
	 * 根据注解(@SaCheckSafe)鉴权
	 * @param at 注解对象 
	 */
	public void checkByAnnotation(SaCheckSafe at) {
		this.checkSafe();
	}

	
	// ------------------- 账号封禁 -------------------  

	/**
	 * 封禁指定账号
	 * <p> 此方法不会直接将此账号id踢下线，而是在对方再次登录时抛出`DisableLoginException`异常 
	 * @param loginId 指定账号id 
	 * @param disableTime 封禁时间, 单位: 秒 （-1=永久封禁）
	 */
	public void disable(Object loginId, long disableTime) {
		// 标注为已被封禁 
		SaManager.getSaTokenDao().set(splicingKeyDisable(loginId), DisableLoginException.BE_VALUE, disableTime);
 		
 		// $$ 通知监听器 
 		SaManager.getSaTokenListener().doDisable(loginType, loginId, disableTime);
	}
	
	/**
	 * 指定账号是否已被封禁 (true=已被封禁, false=未被封禁) 
	 * @param loginId 账号id
	 * @return see note
	 */
	public boolean isDisable(Object loginId) {
		return SaManager.getSaTokenDao().get(splicingKeyDisable(loginId)) != null;
	}

	/**
	 * 获取指定账号剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
	 * @param loginId 账号id
	 * @return see note 
	 */
	public long getDisableTime(Object loginId) {
		return SaManager.getSaTokenDao().getTimeout(splicingKeyDisable(loginId));
	}
	
	/**
	 * 解封指定账号
	 * @param loginId 账号id
	 */
	public void untieDisable(Object loginId) {
		SaManager.getSaTokenDao().delete(splicingKeyDisable(loginId));
		
 		// $$ 通知监听器 
 		SaManager.getSaTokenListener().doUntieDisable(loginType, loginId);
	}
	
	
	// ------------------- 身份切换 -------------------  

	/**
	 * 临时切换身份为指定账号id 
	 * @param loginId 指定loginId 
	 */
	public void switchTo(Object loginId) {
		SaHolder.getStorage().set(splicingKeySwitch(), loginId);
	}
	
	/**
	 * 结束临时切换身份
	 */
	public void endSwitch() {
		SaHolder.getStorage().delete(splicingKeySwitch());
	}

	/**
	 * 当前是否正处于[身份临时切换]中 
	 * @return 是否正处于[身份临时切换]中 
	 */
	public boolean isSwitch() {
		return SaHolder.getStorage().get(splicingKeySwitch()) != null;
	}
	
	/**
	 * 返回[身份临时切换]的loginId 
	 * @return 返回[身份临时切换]的loginId 
	 */
	public Object getSwitchLoginId() {
		return SaHolder.getStorage().get(splicingKeySwitch());
	}
	
	/**
	 * 在一个代码段里方法内，临时切换身份为指定账号id
	 * @param loginId 指定账号id 
	 * @param function 要执行的方法 
	 */
	public void switchTo(Object loginId, SaFunction function) {
		try {
			switchTo(loginId);
			function.run();
		} catch (Exception e) {
			throw e;
		} finally {
			endSwitch();
		}
	}


	// ------------------- 二级认证 -------------------  
	
	/**
	 * 在当前会话 开启二级认证 
	 * @param safeTime 维持时间 (单位: 秒) 
	 */
	public void openSafe(long safeTime) {
		long eff = System.currentTimeMillis() + safeTime * 1000;
		getTokenSession().set(SaTokenConsts.SAFE_AUTH_SAVE_KEY, eff);
	}

	/**
	 * 当前会话 是否处于二级认证时间内 
	 * @return true=二级认证已通过, false=尚未进行二级认证或认证已超时 
	 */
	public boolean isSafe() {
		long eff = getTokenSession().get(SaTokenConsts.SAFE_AUTH_SAVE_KEY, 0L);
		if(eff == 0 || eff < System.currentTimeMillis()) {
			return false;
		}
		return true;
	}

	/**
	 * 检查当前会话是否已通过二级认证，如未通过则抛出异常 
	 */
	public void checkSafe() {
		if (isSafe() == false) {
			throw new NotSafeException();
		}
	}
	
	/**
	 * 获取当前会话的二级认证剩余有效时间 (单位: 秒, 返回-2代表尚未通过二级认证)
	 * @return 剩余有效时间
	 */
	public long getSafeTime() {
		long eff = getTokenSession().get(SaTokenConsts.SAFE_AUTH_SAVE_KEY, 0L);
		if(eff == 0 || eff < System.currentTimeMillis()) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}
		return (eff - System.currentTimeMillis()) / 1000;
	}

	/**
	 * 在当前会话 结束二级认证 
	 */
	public void closeSafe() {
		getTokenSession().delete(SaTokenConsts.SAFE_AUTH_SAVE_KEY);
	}

	
	// ------------------- 返回相应key -------------------  

	/**
	 * 拼接key：客户端 tokenName 
	 * @return key
	 */
	public String splicingKeyTokenName() {
 		return getConfig().getTokenName();
 	}
	
	/**  
	 * 拼接key： tokenValue 持久化 token-id
	 * @param tokenValue token值
	 * @return key
	 */
	public String splicingKeyTokenValue(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginType + ":token:" + tokenValue;
	}
	
	/** 
	 * 拼接key： Session 持久化  
	 * @param loginId 账号id
	 * @return key
	 */
	public String splicingKeySession(Object loginId) {
		return getConfig().getTokenName() + ":" + loginType + ":session:" + loginId;
	}
	
	/**  
	 * 拼接key： tokenValue的专属session 
	 * @param tokenValue token值
	 * @return key
	 */
	public String splicingKeyTokenSession(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginType + ":token-session:" + tokenValue;
	}
	
	/** 
	 * 拼接key： 指定token的最后操作时间 持久化 
	 * @param tokenValue token值
	 * @return key
	 */
	public String splicingKeyLastActivityTime(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginType + ":last-activity:" + tokenValue;
	}

	/**
	 * 在进行身份切换时，使用的存储key 
	 * @return key
	 */
	public String splicingKeySwitch() {
		return SaTokenConsts.SWITCH_TO_SAVE_KEY + loginType;
	}

	/**
	 * 如果token为本次请求新创建的，则以此字符串为key存储在当前request中 
	 * @return key
	 */
	public String splicingKeyJustCreatedSave() {
		return SaTokenConsts.JUST_CREATED_SAVE_KEY + loginType;
	}

	/**  
	 * 拼接key： 账号封禁
	 * @param loginId 账号id
	 * @return key 
	 */
	public String splicingKeyDisable(Object loginId) {
		return getConfig().getTokenName() + ":" + loginType + ":disable:" + loginId;
	}

	
	// ------------------- Bean对象代理 -------------------  
	
	/**
	 * 返回配置对象 
	 * @return 配置对象 
	 */
	public SaTokenConfig getConfig() {
		// 为什么再次代理一层? 为某些极端业务场景下[需要不同StpLogic不同配置]提供便利 
		return SaManager.getConfig();
	}
	
	

	// ------------------- 历史API，兼容旧版本 -------------------  

	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 StpUtil.kickout() ，使用方式保持不变 </h1>
	 * 
	 * 会话注销，根据账号id （踢人下线）
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-2
	 * @param loginId 账号id 
	 */
	public void logoutByLoginId(Object loginId) {
		this.kickout(loginId);
	}
	
	/**
	 * <h1> 本函数设计已过时，未来版本可能移除此函数，请及时更换为 StpUtil.kickout() ，使用方式保持不变 </h1>
	 * 
	 * 会话注销，根据账号id and 设备标识 （踢人下线）
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-2 </p>
	 * @param loginId 账号id 
	 * @param device 设备标识 (填null代表所有注销设备) 
	 */
	public void logoutByLoginId(Object loginId, String device) {
		this.kickout(loginId, device);
	}
	
}

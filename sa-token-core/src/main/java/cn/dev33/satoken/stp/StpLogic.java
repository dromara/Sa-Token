package cn.dev33.satoken.stp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
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
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.TokenSign;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;

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
	public String loginKey;
	
	/**
	 * 初始化StpLogic, 并指定LoginKey
	 * @param loginKey 账号体系标识 
	 */
	public StpLogic(String loginKey) {
		this.loginKey = loginKey;
		// 在 SaTokenManager 中记录下此 StpLogic，以便根据 LoginKey 进行查找此对象 
		SaManager.putStpLogic(this);
	}

	/**
	 * 获取当前StpLogin的LoginKey
	 * @return 当前StpLogin的loginKey
	 */
	public String getLoginKey(){
		return loginKey;
	}

	/**
	 * 写入当前StpLogin的LoginKey
	 * @param loginKey loginKey
	 * @return 对象自身
	 */
	public StpLogic setLoginKey(String loginKey){
		this.loginKey = loginKey;
		return this;
	}
	
	
	// =================== 获取token 相关 ===================  
	
	/**
	 * 返回token名称 
	 * @return 此StpLogic的token名称
	 */
	public String getTokenName() {
 		return splicingKeyTokenName();
 	}

	/**
	 * 创建一个tokenValue
	 * @param loginId loginId
	 * @return 生成的tokenValue 
	 */
 	public String createTokenValue(Object loginId) {
		return SaManager.getSaTokenAction().createToken(loginId, loginKey);
	}
 	
 	/**
 	 * 在当前会话写入当前tokenValue 
 	 * @param tokenValue token值 
 	 * @param cookieTimeout Cookie存活时间(秒)
 	 */
	public void setTokenValue(String tokenValue, int cookieTimeout){
		SaTokenConfig config = getConfig();
		// 将token保存到[存储器]里  
		SaStorage storage = SaHolder.getStorage();
		// 判断是否配置了token前缀 
		String tokenPrefix = config.getTokenPrefix();
		if(SaFoxUtil.isEmpty(tokenPrefix)) {
			storage.set(splicingKeyJustCreatedSave(), tokenValue);	
		} else {
			// 如果配置了token前缀，则拼接上前缀一起写入 
			storage.set(splicingKeyJustCreatedSave(), tokenPrefix + SaTokenConsts.TOKEN_CONNECTOR_CHAT + tokenValue);	
		}
		
		// 注入Cookie 
		if(config.getIsReadCookie()){
			SaResponse response = SaHolder.getResponse();
			response.addCookie(getTokenName(), tokenValue, "/", config.getCookieDomain(), cookieTimeout);
		}
	}
 	
	/**
	 * 获取当前tokenValue
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
			tokenValue = request.getParameter(keyTokenName);
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
		if(!SaFoxUtil.isEmpty(tokenPrefix) && !SaFoxUtil.isEmpty(tokenValue)) {
			// 如果token以指定的前缀开头, 则裁剪掉它, 否则视为未提供token 
			if(tokenValue.startsWith(tokenPrefix + SaTokenConsts.TOKEN_CONNECTOR_CHAT)) {
				tokenValue = tokenValue.substring(tokenPrefix.length() + SaTokenConsts.TOKEN_CONNECTOR_CHAT.length());
			} else {
				tokenValue = null;
			}
		}
		
		// 6. 返回 
		return tokenValue;
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
		info.loginDevice = getLoginDevice();
		return info;
	}
	
	
	// =================== 登录相关操作 ===================  

	// 登录与注销 
	
	/**
	 * 在当前会话上登录id 
	 * @param loginId 登录id，建议的类型：（long | int | String）
	 */
	public void setLoginId(Object loginId) {
		setLoginId(loginId, new SaLoginModel());
	}

	/**
	 * 在当前会话上登录id, 并指定登录设备 
	 * @param loginId 登录id，建议的类型：（long | int | String）
	 * @param device 设备标识 
	 */
	public void setLoginId(Object loginId, String device) {
		setLoginId(loginId, new SaLoginModel().setDevice(device));
	}

	/**
	 * 在当前会话上登录id, 并指定登录设备 
	 * @param loginId 登录id，建议的类型：（long | int | String）
	 * @param isLastingCookie 是否为持久Cookie 
	 */
	public void setLoginId(Object loginId, boolean isLastingCookie) {
		setLoginId(loginId, new SaLoginModel().setIsLastingCookie(isLastingCookie));
	}
	
	/**
	 * 在当前会话上登录id, 并指定所有登录参数Model 
	 * @param loginId 登录id，建议的类型：（long | int | String）
	 * @param loginModel 此次登录的参数Model 
	 */
	public void setLoginId(Object loginId, SaLoginModel loginModel) {
		
		// ------ 0、检查此账号是否已被封禁 
		if(isDisable(loginId)) {
			throw new DisableLoginException(loginKey, loginId, getDisableTime(loginId));
		}
		
		// ------ 1、获取相应对象  
		SaTokenConfig config = getConfig();
		SaTokenDao dao = SaManager.getSaTokenDao();
		loginModel.build(config);
		
		// ------ 2、生成一个token 
		String tokenValue = null;
		// --- 如果允许并发登录 
		if(config.getAllowConcurrentLogin()) {
			// 如果配置为共享token, 则尝试从Session签名记录里取出token 
			if(config.getIsShare()) {
				tokenValue = getTokenValueByLoginId(loginId, loginModel.getDevice());
			}
		} else {
			// --- 如果不允许并发登录 
			// 如果此时[user-session]不为null，说明此账号在其他地正在登录，现在需要先把其它地的同设备token标记为被顶下线 
			SaSession session = getSessionByLoginId(loginId, false);
			if(session != null) {
				List<TokenSign> tokenSignList = session.getTokenSignList();
				for (TokenSign tokenSign : tokenSignList) {
					if(tokenSign.getDevice().equals(loginModel.getDevice())) {
						// 1. 将此token 标记为已顶替 
						dao.update(splicingKeyTokenValue(tokenSign.getValue()), NotLoginException.BE_REPLACED);	
						// 2. 清理掉[token-最后操作时间] 
						clearLastActivity(tokenSign.getValue()); 			
						// 3. 清理user-session上的token签名记录 
						session.removeTokenSign(tokenSign.getValue()); 		
				 		// $$ 通知监听器 
				 		SaManager.getSaTokenListener().doReplaced(loginKey, loginId, tokenSign.getValue(), tokenSign.getDevice());
					}
				}
			}
		}
		// 如果至此，仍未成功创建tokenValue, 则开始生成一个 
		if(tokenValue == null) {
			tokenValue = createTokenValue(loginId);
		}
		
		// ------ 3. 获取[User-Session] (如果还没有创建session, 则新建, 如果已经创建，则续期) 
		SaSession session = getSessionByLoginId(loginId, false);
		if(session == null) {
			session = getSessionByLoginId(loginId);
		} else {
			session.updateMinTimeout(loginModel.getTimeout());
		}
		// 在session上记录token签名 
		session.addTokenSign(new TokenSign(tokenValue, loginModel.getDevice()));
		
		// ------ 4. 持久化其它数据 
		// token -> uid 
		dao.set(splicingKeyTokenValue(tokenValue), String.valueOf(loginId), loginModel.getTimeout());
		
		// 写入 [最后操作时间]
		setLastActivityToNow(tokenValue); 
		
		// 在当前会话写入当前tokenValue 
		setTokenValue(tokenValue, loginModel.getCookieTimeout());
		
		// $$ 通知监听器
		SaManager.getSaTokenListener().doLogin(loginKey, loginId, loginModel);
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
 		if(getConfig().getIsReadCookie()){
 			SaHolder.getResponse().deleteCookie(getTokenName());
		}
 		logoutByTokenValue(tokenValue);
	}

	/**
	 * 指定token的会话注销登录 
	 * @param tokenValue 指定token
	 */
	public void logoutByTokenValue(String tokenValue) {
		// 1. 清理掉[token-最后操作时间] 
		clearLastActivity(tokenValue); 	

		// 2. 清理Token-Session 
		SaManager.getSaTokenDao().delete(splicingKeyTokenSession(tokenValue));
		
 		// 3. 尝试清除token-id键值对 (先从db中获取loginId值，如果根本查不到loginId，那么无需继续操作 )
 		String loginId = getLoginIdNotHandle(tokenValue);
 	 	if(loginId == null || NotLoginException.ABNORMAL_LIST.contains(loginId)) {
 			return;
 		}
 		SaManager.getSaTokenDao().delete(splicingKeyTokenValue(tokenValue));	
 		
 		// $$ 通知监听器 
 		SaManager.getSaTokenListener().doLogout(loginKey, loginId, tokenValue);

		// 4. 尝试清理User-Session上的token签名 (如果为null或已被标记为异常, 那么无需继续执行 )
 	 	SaSession session = getSessionByLoginId(loginId, false);
 	 	if(session == null) {
 	 		return;
 	 	}
 	 	session.removeTokenSign(tokenValue); 
 	 	
 	 	// 5. 尝试注销User-Session
		session.logoutByTokenSignCountToZero();
	}
	
	/**
	 * 指定loginId的会话注销登录（踢人下线）
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-2
	 * @param loginId 账号id 
	 */
	public void logoutByLoginId(Object loginId) {
		logoutByLoginId(loginId, null);
	}
	
	/**
	 * 指定loginId指定设备的会话注销登录（踢人下线）
	 * <p> 当对方再次访问系统时，会抛出NotLoginException异常，场景值=-2
	 * @param loginId 账号id 
	 * @param device 设备标识 (填null代表所有注销设备) 
	 */
	public void logoutByLoginId(Object loginId, String device) {
		// 1. 先获取这个账号的[user-session], 如果为null，则不执行任何操作 
		SaSession session = getSessionByLoginId(loginId, false);
		if(session == null) {
			return;
		}
		
		// 2. 循环token签名列表，开始删除相关信息 
		List<TokenSign> tokenSignList = session.getTokenSignList();
		for (TokenSign tokenSign : tokenSignList) {
			if(device == null || tokenSign.getDevice().equals(device)) {
				// 1. 获取token 
				String tokenValue = tokenSign.getValue();
				// 2. 清理掉[token-最后操作时间] 
				clearLastActivity(tokenValue); 	
		 		// 3. 标记：已被踢下线 
				SaManager.getSaTokenDao().update(splicingKeyTokenValue(tokenValue), NotLoginException.KICK_OUT);
		 		// 4. 清理账号session上的token签名 
		 		session.removeTokenSign(tokenValue); 
		 		// $$ 通知监听器 
		 		SaManager.getSaTokenListener().doLogoutByLoginId(loginKey, loginId, tokenValue, tokenSign.getDevice());
			}
		}
 	 	// 3. 尝试注销session
		session.logoutByTokenSignCountToZero();
	}

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
 		SaManager.getSaTokenListener().doDisable(loginKey, loginId, disableTime);
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
 		SaManager.getSaTokenListener().doUntieDisable(loginKey, loginId);
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
		// 如果正在[临时身份切换], 则返回临时身份 
		if(isSwitch()) {
			return getSwitchLoginId();
		}
 		// 如果获取不到token，则抛出: 无token
 		String tokenValue = getTokenValue();
 		if(tokenValue == null) {
 			throw NotLoginException.newInstance(loginKey, NotLoginException.NOT_TOKEN);
 		}
 		// 查找此token对应loginId, 如果找不到则抛出：无效token 
 		String loginId = getLoginIdNotHandle(tokenValue);
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
 		return Integer.parseInt(String.valueOf(getLoginId()));
 	}

 	/**
	 * 获取当前会话登录id, 并转换为long
	 * @return 账号id 
	 */
 	public long getLoginIdAsLong() {
 		return Long.parseLong(String.valueOf(getLoginId()));
 	}
 	
 	/** 
 	 * 获取指定token对应的登录id，如果未登录，则返回 null 
 	 * @param tokenValue token
 	 * @return 登录id
 	 */
 	public Object getLoginIdByToken(String tokenValue) {
 		if(tokenValue == null) {
 	 		return null;
 		}
 		return getLoginIdNotHandle(tokenValue);
 	}
 	
 	 /**
 	  * 获取指定token对应的登录id (不做任何特殊处理) 
 	  * @param tokenValue token值 
 	  * @return loginId
 	  */
 	public String getLoginIdNotHandle(String tokenValue) {
 		return SaManager.getSaTokenDao().get(splicingKeyTokenValue(tokenValue));
 	}
 	 
 	
	// =================== session相关 ===================  

	/** 
	 * 获取指定key的session, 如果session尚未创建，isCreate=是否新建并返回
	 * @param sessionId sessionId
	 * @param isCreate 是否新建
	 * @return session对象 
	 */
	public SaSession getSessionBySessionId(String sessionId, boolean isCreate) {
		SaSession session = SaManager.getSaTokenDao().getSession(sessionId);
		if(session == null && isCreate) {
			session = SaManager.getSaTokenAction().createSession(sessionId);
			SaManager.getSaTokenDao().setSession(session, getConfig().getTimeout());
		}
		return session;
	}

	/** 
	 * 获取指定key的session, 如果session尚未创建，则返回null
	 * @param sessionId sessionId
	 * @return session对象 
	 */
	public SaSession getSessionBySessionId(String sessionId) {
		return getSessionBySessionId(sessionId, false);
	}

	/** 
	 * 获取指定loginId的session, 如果session尚未创建，isCreate=是否新建并返回
	 * @param loginId 账号id
	 * @param isCreate 是否新建
	 * @return SaSession
	 */
	public SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
		return getSessionBySessionId(splicingKeySession(loginId), isCreate);
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

	
	// =================== token专属session ===================  

	/** 
	 * 获取指定token的专属session，如果session尚未创建，isCreate代表是否新建并返回
	 * @param tokenValue token值
	 * @param isCreate 是否新建 
	 * @return session会话 
	 */
	public SaSession getTokenSessionByToken(String tokenValue, boolean isCreate) {
		return getSessionBySessionId(splicingKeyTokenSession(tokenValue), isCreate);
	}
	
	/** 
	 * 获取指定token的专属session，如果session尚未创建，则新建并返回 
	 * @param tokenValue token值
	 * @return session会话 
	 */
	public SaSession getTokenSessionByToken(String tokenValue) {
		return getSessionBySessionId(splicingKeyTokenSession(tokenValue), true);
	}

	/** 
	 * 获取当前token的专属-session，如果session尚未创建，isCreate代表是否新建并返回 
	 * @param isCreate 是否新建 
	 * @return session会话 
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
		// 返回这个token对应的专属session 
		return getSessionBySessionId(splicingKeyTokenSession(getTokenValue()), isCreate);
	}
	
	/** 
	 * 获取当前token的专属-session，如果session尚未创建，则新建并返回
	 * @return session会话 
	 */
	public SaSession getTokenSession() {
		return getTokenSession(true);
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
 		SaHolder.getStorage().delete((SaTokenConsts.TOKEN_ACTIVITY_TIMEOUT_CHECKED_KEY));
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
 			throw NotLoginException.newInstance(loginKey, NotLoginException.TOKEN_TIMEOUT);
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
 	
 	
	// =================== 过期时间相关 ===================  

 	/**
 	 * 获取当前登录者的token剩余有效时间 (单位: 秒)
 	 * @return token剩余有效时间
 	 */
 	public long getTokenTimeout() {
 		return SaManager.getSaTokenDao().getTimeout(splicingKeyTokenValue(getTokenValue()));
 	}
 	
 	/**
 	 * 获取指定loginId的token剩余有效时间 (单位: 秒) 
 	 * @param loginId 指定loginId 
 	 * @return token剩余有效时间 
 	 */
 	public long getTokenTimeoutByLoginId(Object loginId) {
 		return SaManager.getSaTokenDao().getTimeout(splicingKeyTokenValue(getTokenValueByLoginId(loginId)));
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
 		return SaManager.getSaTokenDao().getSessionTimeout(splicingKeySession(loginId));
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
 		return SaManager.getSaTokenDao().getSessionTimeout(splicingKeyTokenSession(tokenValue));
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

 	
	// =================== 角色验证操作 ===================  

 	/** 
 	 * 指定账号id是否含有角色标识, 返回true或false 
 	 * @param loginId 账号id
 	 * @param role 角色标识
 	 * @return 是否含有指定角色标识
 	 */
 	public boolean hasRole(Object loginId, String role) {
 		List<String> roleList = SaManager.getStpInterface().getRoleList(loginId, loginKey);
 		return SaManager.getSaTokenAction().hasElement(roleList, role);
//		return !(roleList == null || roleList.contains(role) == false);
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
			throw new NotRoleException(role, this.loginKey);
		}
 	}

 	/** 
 	 * 当前账号是否含有指定角色标识 [指定多个，必须全部验证通过] 
 	 * @param roleArray 角色标识数组
 	 */
 	public void checkRoleAnd(String... roleArray){
 		Object loginId = getLoginId();
 		List<String> roleList = SaManager.getStpInterface().getRoleList(loginId, loginKey);
 		for (String role : roleArray) {
 			if(!SaManager.getSaTokenAction().hasElement(roleList, role)) {
 				throw new NotRoleException(role, this.loginKey);
 			}
 		}
 	}

 	/** 
 	 * 当前账号是否含有指定角色标识 [指定多个，只要其一验证通过即可] 
 	 * @param roleArray 角色标识数组
 	 */
 	public void checkRoleOr(String... roleArray){
 		Object loginId = getLoginId();
 		List<String> roleList = SaManager.getStpInterface().getRoleList(loginId, loginKey);
 		for (String role : roleArray) {
 			if(SaManager.getSaTokenAction().hasElement(roleList, role)) {
 				// 有的话提前退出
 				return;		
 			}
 		}
		if(roleArray.length > 0) {
	 		throw new NotRoleException(roleArray[0], this.loginKey);
		}
 	}
 	
 	
	// =================== 权限验证操作 ===================  

 	/** 
 	 * 指定账号id是否含有指定权限, 返回true或false 
 	 * @param loginId 账号id
 	 * @param permission 权限码
 	 * @return 是否含有指定权限
 	 */
 	public boolean hasPermission(Object loginId, String permission) {
 		List<String> permissionList = SaManager.getStpInterface().getPermissionList(loginId, loginKey);
 		return SaManager.getSaTokenAction().hasElement(permissionList, permission);
//		return !(permissionList == null || permissionList.contains(permission) == false);
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
			throw new NotPermissionException(permission, this.loginKey);
		}
 	}

 	/** 
 	 * 当前账号是否含有指定权限 [指定多个，必须全部验证通过] 
 	 * @param permissionArray 权限码数组
 	 */
 	public void checkPermissionAnd(String... permissionArray){
 		Object loginId = getLoginId();
 		List<String> permissionList = SaManager.getStpInterface().getPermissionList(loginId, loginKey);
 		for (String permission : permissionArray) {
 			if(!SaManager.getSaTokenAction().hasElement(permissionList, permission)) {
 				throw new NotPermissionException(permission, this.loginKey);	
 			}
 		}
 	}

 	/** 
 	 * 当前账号是否含有指定权限 [指定多个，只要其一验证通过即可] 
 	 * @param permissionArray 权限码数组
 	 */
 	public void checkPermissionOr(String... permissionArray){
 		Object loginId = getLoginId();
 		List<String> permissionList = SaManager.getStpInterface().getPermissionList(loginId, loginKey);
 		for (String permission : permissionArray) {
 			if(SaManager.getSaTokenAction().hasElement(permissionList, permission)) {
 				// 有的话提前退出
 				return;		
 			}
 		}
		if(permissionArray.length > 0) {
	 		throw new NotPermissionException(permissionArray[0], this.loginKey);
		}
 	}

	
	// =================== id 反查token 相关操作 ===================  
	
	/** 
	 * 获取指定loginId的tokenValue 
	 * <p> 在配置为允许并发登录时，此方法只会返回队列的最后一个token，
	 * 如果你需要返回此账号id的所有token，请调用 getTokenValueListByLoginId 
	 * @param loginId 账号id
	 * @return token值 
	 */
	public String getTokenValueByLoginId(Object loginId) {
		return getTokenValueByLoginId(loginId, SaTokenConsts.DEFAULT_LOGIN_DEVICE);
	}

	/** 
	 * 获取指定loginId指定设备端的tokenValue 
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
	 * 获取指定loginId的tokenValue集合 
	 * @param loginId 账号id 
	 * @return 此loginId的所有相关token 
 	 */
	public List<String> getTokenValueListByLoginId(Object loginId) {
		return getTokenValueListByLoginId(loginId, SaTokenConsts.DEFAULT_LOGIN_DEVICE);
	}

 	/** 
	 * 获取指定loginId指定设备端的tokenValue 集合 
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
	 * 返回当前token的登录设备 
	 * @return 当前令牌的登录设备 
	 */
	public String getLoginDevice() {
		// 如果没有token，直接返回 
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
	

	// =================== 会话管理 ===================  

	/**
	 * 根据条件查询token 
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
	 * 根据条件查询token专属Session的Id 
	 * @param keyword 关键字 
	 * @param start 开始处索引 (-1代表查询所有) 
	 * @param size 获取数量 
	 * @return sessionId集合 
	 */
	public List<String> searchTokenSessionId(String keyword, int start, int size) {
		return SaManager.getSaTokenDao().searchData(splicingKeyTokenSession(""), keyword, start, size);
	}
	

	// =================== 返回相应key ===================  

	/**
	 *  获取key：客户端 tokenName 
	 * @return key
	 */
	public String splicingKeyTokenName() {
 		return getConfig().getTokenName();
 	}
	
	/**  
	 * 获取key： tokenValue 持久化 token-id
	 * @param tokenValue token值
	 * @return key
	 */
	public String splicingKeyTokenValue(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginKey + ":token:" + tokenValue;
	}
	
	/** 
	 * 获取key： session 持久化  
	 * @param loginId 账号id
	 * @return key
	 */
	public String splicingKeySession(Object loginId) {
		return getConfig().getTokenName() + ":" + loginKey + ":session:" + loginId;
	}
	
	/**  
	 * 获取key： tokenValue的专属session 
	 * @param tokenValue token值
	 * @return key
	 */
	public String splicingKeyTokenSession(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginKey + ":token-session:" + tokenValue;
	}
	
	/** 
	 * 获取key： 指定token的最后操作时间 持久化 
	 * @param tokenValue token值
	 * @return key
	 */
	public String splicingKeyLastActivityTime(String tokenValue) {
		return getConfig().getTokenName() + ":" + loginKey + ":last-activity:" + tokenValue;
	}

	/**
	 * 在进行身份切换时，使用的存储key 
	 * @return key
	 */
	public String splicingKeySwitch() {
		return SaTokenConsts.SWITCH_TO_SAVE_KEY + loginKey;
	}

	/**
	 * 如果token为本次请求新创建的，则以此字符串为key存储在当前request中 
	 * @return key
	 */
	public String splicingKeyJustCreatedSave() {
		return SaTokenConsts.JUST_CREATED_SAVE_KEY + loginKey;
	}

	/**  
	 * 拼接key： 账号封禁
	 * @param loginId 账号id
	 * @return key 
	 */
	public String splicingKeyDisable(Object loginId) {
		return getConfig().getTokenName() + ":" + loginKey + ":disable:" + loginId;
	}
	
	// =================== Bean对象代理 ===================  
	
	/**
	 * 返回配置对象 
	 * @return 配置对象 
	 */
	public SaTokenConfig getConfig() {
		// 为什么再次代理一层? 为某些极端业务场景下[需要不同StpLogic不同配置]提供便利 
		return SaManager.getConfig();
	}
	

	// =================== 其它方法 ===================  

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

	
	// =================== 身份切换 ===================  

	/**
	 * 临时切换身份为指定loginId 
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
	 * 在一个代码段里方法内，临时切换身份为指定loginId
	 * @param loginId 指定loginId 
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
	
	
}

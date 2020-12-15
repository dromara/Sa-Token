package cn.dev33.satoken.stp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * sa-token 权限验证，逻辑 实现类 
 * <p>
 * (stp = sa-token-permission 的缩写 )
 * @author kong
 */
public class StpLogic {

	/**
	 * 持久化的key前缀，多账号体系时以此值区分，比如：login、user、admin
	 */
	public String loginKey = "";		
	
	/**
	 * 初始化StpLogic, 并制定loginKey
	 * @param loginKey .
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
		return SaTokenManager.getSta().createToken(loginId, loginKey);
	}
	
	/**
	 *  获取当前tokenValue
	 * @return 当前tokenValue
	 */
	public String getTokenValue(){
		// 0、获取相应对象 
		HttpServletRequest request = SaTokenManager.getSta().getCurrRequest();
		SaTokenConfig config = SaTokenManager.getConfig();
		String keyTokenName = getTokenName();
		
		// 1、尝试从request里读取 
		if(request.getAttribute(SaTokenInsideUtil.JUST_CREATED_SAVE_KEY) != null) {
			return String.valueOf(request.getAttribute(SaTokenInsideUtil.JUST_CREATED_SAVE_KEY));
		}
		// 2、尝试从请求体里面读取 
		if(config.getIsReadBody() == true){
			String tokenValue = request.getParameter(keyTokenName);
			if(tokenValue != null) {
				return tokenValue;
			}
		}
		// 3、尝试从header力读取 
		if(config.getIsReadHead() == true){
			String tokenValue = request.getHeader(keyTokenName);
			if(tokenValue != null) {
				return tokenValue;
			}
		}
		// 4、尝试从cookie里读取 
		if(config.getIsReadCookie() == true){
			Cookie cookie = SaTokenManager.getSaCookieOper().getCookie(request, keyTokenName);
			if(cookie != null){
				String tokenValue = cookie.getValue();
				if(tokenValue != null) {
					return tokenValue;
				}
			}
		}
		// 5、都读取不到，那算了吧还是  
		return null;
	}
	
	/** 
	 * 获取指定id的tokenValue
	 * @param loginId .
	 * @return .
	 */
	public String getTokenValueByLoginId(Object loginId) {
		return SaTokenManager.getDao().getValue(getKeyLoginId(loginId)); 
	}
	
	/**
	 * 获取当前会话的token信息：tokenName与tokenValue
	 * @return 一个Map对象 
	 */
	public Map<String, String> getTokenInfo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("tokenName", getTokenName());
		map.put("tokenValue", getTokenValue());
		return map;
	}
	
	
	// =================== 登录相关操作 ===================  

	/**
	 * 在当前会话上登录id 
	 * @param loginId 登录id ，建议的类型：（long | int | String）
	 */
	public void setLoginId(Object loginId) {
		
		// 1、获取相应对象  
		HttpServletRequest request = SaTokenManager.getSta().getCurrRequest();
		SaTokenConfig config = SaTokenManager.getConfig();
		SaTokenDao dao = SaTokenManager.getDao();
		
		// 2、获取tokenValue
		String tokenValue = getTokenValueByLoginId(loginId);	// 获取旧tokenValue
		if(tokenValue == null){			// 为null则创建一个新的
			tokenValue = randomTokenValue(loginId);
		} else {
			// 不为null, 并且配置不共享，则：将原来的标记为[被顶替] 
			if(config.getIsShare() == false){
//				dao.delKey(getKeyTokenValue(tokenValue)); 
				dao.updateValue(getKeyTokenValue(tokenValue), NotLoginException.BE_REPLACED);
				tokenValue = randomTokenValue(loginId);
			}
		}

		// 3、持久化 
		dao.setValue(getKeyTokenValue(tokenValue), String.valueOf(loginId), config.getTimeout());	// token -> uid 
		dao.setValue(getKeyLoginId(loginId), tokenValue, config.getTimeout());							// uid -> token 
		request.setAttribute(SaTokenInsideUtil.JUST_CREATED_SAVE_KEY, tokenValue);								// 保存到本次request里  
		if(config.getIsReadCookie() == true){
			SaTokenManager.getSaCookieOper().addCookie(SaTokenManager.getSta().getResponse(), getTokenName(), tokenValue, "/", (int)config.getTimeout());		// cookie注入 
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
 		if(SaTokenManager.getConfig().getIsReadCookie() == true){
			SaTokenManager.getSaCookieOper().delCookie(SaTokenManager.getSta().getCurrRequest(), SaTokenManager.getSta().getResponse(), getTokenName()); 	
		}
 		// 尝试从db中获取loginId值 
 		String loginId = SaTokenManager.getDao().getValue(getKeyTokenValue(tokenValue));
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
	 * 指定loginId的会话注销登录（清退下线）
	 * @param loginId 账号id 
	 */
	public void logoutByLoginId(Object loginId) {
		
		// 获取相应tokenValue
		String tokenValue = getTokenValueByLoginId(loginId);
		if(tokenValue == null) {
			return;
		}
		
		// 清除相关数据 
		SaTokenManager.getDao().delKey(getKeyTokenValue(tokenValue));	// 清除token-id键值对  
		SaTokenManager.getDao().delKey(getKeyLoginId(loginId));		// 清除id-token键值对  
		SaTokenManager.getDao().deleteSaSession(getKeySession(loginId));		// 清除其session 
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
		SaTokenManager.getDao().updateValue(getKeyTokenValue(tokenValue), NotLoginException.KICK_OUT);	// 标记：已被踢下线 
		SaTokenManager.getDao().delKey(getKeyLoginId(loginId));		// 清除id-token键值对  
		SaTokenManager.getDao().deleteSaSession(getKeySession(loginId));		// 清除其session 
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
 	 * 获取当前会话登录id, 如果未登录，则抛出异常
 	 * @return .
 	 */
 	public Object getLoginId() {
 		// 如果获取不到token，则抛出：无token
 		String tokenValue = getTokenValue();
 		if(tokenValue == null) {
 			throw NotLoginException.newInstance(loginKey, NotLoginException.NOT_TOKEN);
 		}
 		// 查找此token对应loginId, 则抛出：无效token 
 		String loginId = SaTokenManager.getDao().getValue(getKeyTokenValue(tokenValue));
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
 		// 至此，返回loginId
 		return loginId;
 	}
	
	/** 
	 * 获取当前会话登录id, 如果未登录，则返回默认值
	 * @param defaultValue .
	 * @return .
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
	 * @return .
	 */
	public Object getLoginIdDefaultNull() {
		// 如果连token都是空的，则直接返回 
		String tokenValue = getTokenValue();
 		if(tokenValue == null) {
 			return null;
 		}
 		// loginId为null或者在异常项里面，均视为未登录
 		Object loginId = SaTokenManager.getDao().getValue(getKeyTokenValue(tokenValue));
 		if(loginId == null || NotLoginException.ABNORMAL_LIST.contains(loginId)) {
 			return null;
 		}
 		// 执行到此，证明loginId已经是个正常的账号id了 
 		return loginId;
 	}

	/** 
	 * 获取当前会话登录id, 并转换为String
	 * @return
	 */
 	public String getLoginIdAsString() {
 		return String.valueOf(getLoginId());
 	}

	/** 
	 * 获取当前会话登录id, 并转换为int
	 * @return .
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
	 * @return .
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
 	 * @return .
 	 */
 	public Object getLoginIdByToken(String tokenValue) {
 		if(tokenValue != null) {
 			Object loginId = SaTokenManager.getDao().getValue(getKeyTokenValue(tokenValue));
 			if(loginId != null) {
 				return loginId;
 			}
 		}
 		return null;
 	}
 	
 	
	// =================== session相关 ===================  

	/** 
	 * 获取指定key的session, 如果没有，isCreate=是否新建并返回
	 * @param sessionId .
	 * @param isCreate .
	 * @return .
	 */
	protected SaSession getSessionBySessionId(String sessionId, boolean isCreate) {
		SaSession session = SaTokenManager.getDao().getSaSession(sessionId);
		if(session == null && isCreate) {
			session = new SaSession(sessionId);
			SaTokenManager.getDao().saveSaSession(session, SaTokenManager.getConfig().getTimeout());
		}
		return session;
	}

	/** 
	 * 获取指定loginId的session, 如果没有，isCreate=是否新建并返回
	 * @param loginId 登录id
	 * @param isCreate 是否新建
	 * @return SaSession
	 */
	public SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
		return getSessionBySessionId(getKeySession(loginId), isCreate);
	}

	/** 
	 * 获取指定loginId的session
	 * @param loginId .
	 * @return .
	 */
	public SaSession getSessionByLoginId(Object loginId) {
		return getSessionByLoginId(loginId, true);
	}
	
	/** 
	 * 获取当前会话的session
	 * @return
	 */
	public SaSession getSession() {
		return getSessionByLoginId(getLoginId());
	}
 	
	

	// =================== 权限验证操作 ===================  

 	/** 
 	 * 指定loginId是否含有指定权限
 	 * @param loginId .
 	 * @param pcode .
 	 * @return .
 	 */
 	public boolean hasPermission(Object loginId, Object pcode) {
 		List<Object> pcodeList = SaTokenManager.getStp().getPermissionCodeList(loginId, loginKey);
		return !(pcodeList == null || pcodeList.contains(pcode) == false);
 	}
 	
 	/** 
 	 * 当前会话是否含有指定权限
 	 * @param pcode .
 	 * @return .
 	 */
 	public boolean hasPermission(Object pcode) {
 		return hasPermission(getLoginId(), pcode);
 	}
	
 	/** 
 	 * 当前账号是否含有指定权限 ， 没有就抛出异常
 	 * @param pcode .
 	 */
 	public void checkPermission(Object pcode) {
 		if(hasPermission(pcode) == false) {
			throw new NotPermissionException(pcode, this.loginKey);
		}
 	}

 	/** 
 	 * 当前账号是否含有指定权限 ， 【指定多个，必须全都有】
 	 * @param pcodeArray .
 	 */
 	public void checkPermissionAnd(Object... pcodeArray){
 		Object loginId = getLoginId();
 		List<Object> pcodeList = SaTokenManager.getStp().getPermissionCodeList(loginId, loginKey);
 		for (Object pcode : pcodeArray) {
 			if(pcodeList.contains(pcode) == false) {
 				throw new NotPermissionException(pcode, this.loginKey);	// 没有权限抛出异常 
 			}
 		}
 	}

 	/** 
 	 * 当前账号是否含有指定权限 ， 【指定多个，有一个就可以了】
 	 * @param pcodeArray .
 	 */
 	public void checkPermissionOr(Object... pcodeArray){
 		Object loginId = getLoginId();
 		List<Object> pcodeList = SaTokenManager.getStp().getPermissionCodeList(loginId, loginKey);
 		for (Object pcode : pcodeArray) {
 			if(pcodeList.contains(pcode) == true) {
 				return;		// 有的话提前退出
 			}
 		}
		if(pcodeArray.length > 0) {
	 		throw new NotPermissionException(pcodeArray[0], this.loginKey);	// 没有权限抛出异常 
		}
 	}
 	
	
	// =================== 返回相应key ===================  

	/**
	 *  获取key：客户端 tokenName 
	 * @return
	 */
	public String getKeyTokenName() {
 		return SaTokenManager.getConfig().getTokenName();
 	}
	/**  
	 * 获取key： tokenValue 持久化
	 * @param tokenValue .
	 * @return
	 */
	public String getKeyTokenValue(String tokenValue) {
		return SaTokenManager.getConfig().getTokenName() + ":" + loginKey + ":token:" + tokenValue;
	}
	/** 
	 * 获取key： id 持久化
	 * @param loginId .
	 * @return
	 */
	public String getKeyLoginId(Object loginId) {
		return SaTokenManager.getConfig().getTokenName() + ":" + loginKey + ":id:" + loginId;
	}
	/** 
	 * 获取key： session 持久化  
	 * @param loginId .
	 * @return .
	 */
	public String getKeySession(Object loginId) {
		return SaTokenManager.getConfig().getTokenName() + ":" + loginKey + ":session:" + loginId;
	}
 	
	
}

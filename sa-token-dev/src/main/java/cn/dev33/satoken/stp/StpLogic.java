package cn.dev33.satoken.stp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.SaTokenUtil;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.util.SaCookieUtil;
import cn.dev33.satoken.util.SpringMVCUtil;

/**
 * sa-token 权限验证，逻辑 实现类 
 * <p>
 * (stp = sa-token-permission 的缩写 )
 *
 */
public class StpLogic {

	
	public String login_key = "";		// 持久化的key前缀，多账号体系时以此值区分，比如：login、user、admin
	
	public StpLogic(String login_key) {
		this.login_key = login_key;
	}

	// =================== 获取token 相关 ===================  
	

	/**
	 * 随机生成一个tokenValue
	 * @return 生成的tokenValue 
	 */
 	public String randomTokenValue() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 *  获取当前tokenValue
	 * @return 当前tokenValue
	 */
	public String getTokenValue(){
		// 0、获取相应对象 
		HttpServletRequest request = SpringMVCUtil.getRequest();
		SaTokenConfig config = SaTokenManager.getConfig();
		String key_tokenName = getKey_tokenName();
		
		// 1、尝试从request里读取 
		if(request.getAttribute(SaTokenUtil.JUST_CREATED_SAVE_KEY) != null) {
			return String.valueOf(request.getAttribute(SaTokenUtil.JUST_CREATED_SAVE_KEY));
		}
		
		// 2、尝试从cookie里读取 
		Cookie cookie = SaCookieUtil.getCookie(request, key_tokenName);
		if(cookie != null){
			String tokenValue = cookie.getValue();
			if(tokenValue != null) {
				return tokenValue;
			}
		}

		// 3、尝试从header力读取 
		if(config.getIsReadHead() == true){
			String tokenValue = request.getHeader(key_tokenName);
			if(tokenValue != null) {
				return tokenValue;
			}
		}
		
		// 4、尝试从请求体里面读取 
		if(config.getIsReadBody() == true){
			String tokenValue = request.getParameter(key_tokenName);
			if(tokenValue != null) {
				return tokenValue;
			}
		}
		
		// 5、都读取不到，那算了吧还是  
		return null;
	}
	
	/** 
	 * 获取指定id的tokenValue
	 * @param login_id 
	 * @return
	 */
	public String getTokenValueByLoginId(Object login_id) {
		return SaTokenManager.getDao().getValue(getKey_LoginId(login_id)); 
	}
	
	/**
	 * 获取当前会话的token信息：tokenName与tokenValue
	 * @return 一个Map对象 
	 */
	public Map<String, String> getTokenInfo() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("tokenName", getKey_tokenName());
		map.put("tokenValue", getTokenValue());
		return map;
	}
	
	
	// =================== 登录相关操作 ===================  

	/**
	 * 在当前会话上登录id 
	 * @param login_id 登录id ，建议的类型：（long | int | String）
	 */
	public void setLoginId(Object login_id) {
		
		// 1、获取相应对象  
		HttpServletRequest request = SpringMVCUtil.getRequest();
		SaTokenConfig config = SaTokenManager.getConfig();
		SaTokenDao dao = SaTokenManager.getDao();
		
		// 2、获取tokenValue
		String tokenValue = getTokenValueByLoginId(login_id);	// 获取旧tokenValue
		if(tokenValue == null){			// 为null则创建一个新的
			tokenValue = randomTokenValue();
		} else {
			// 不为null, 并且配置不共享，则删掉原来，并且创建新的 
			if(config.getIsShare() == false){
				dao.delKey(getKey_TokenValue(tokenValue)); 
				tokenValue = randomTokenValue();
			}
		}

		// 3、持久化 
		dao.setValue(getKey_TokenValue(tokenValue), String.valueOf(login_id), config.getTimeout());	// token -> uid 
		dao.setValue(getKey_LoginId(login_id), tokenValue, config.getTimeout());							// uid -> token 
		request.setAttribute(SaTokenUtil.JUST_CREATED_SAVE_KEY, tokenValue);								// 保存到本次request里  
		SaCookieUtil.addCookie(SpringMVCUtil.getResponse(), getKey_tokenName(), tokenValue, "/", (int)config.getTimeout());		// cookie注入 
	}

	/** 
	 * 当前会话注销登录
	 */
	public void logout() {
		Object login_id = getLoginId_defaultNull();
		if(login_id != null) {
			logoutByLoginId(login_id);
			SaCookieUtil.delCookie(SpringMVCUtil.getRequest(), SpringMVCUtil.getResponse(), getKey_tokenName()); 	// 清除cookie 
		}
	}

	/**
	 * 指定login_id的会话注销登录（踢人下线）
	 * @param login_id 账号id 
	 */
	public void logoutByLoginId(Object login_id) {
		
		// 获取相应tokenValue
		String tokenValue = getTokenValueByLoginId(login_id);
		if(tokenValue == null) {
			return;
		}
		
		// 清除相关数据 
		SaTokenManager.getDao().delKey(getKey_TokenValue(tokenValue));	// 清除token-id键值对  
		SaTokenManager.getDao().delKey(getKey_LoginId(login_id));		// 清除id-token键值对  
		SaTokenManager.getDao().delKey(getKey_session(login_id));		// 清除其session 
		// SaCookieUtil.delCookie(SpringMVCUtil.getRequest(), SpringMVCUtil.getResponse(), getKey_tokenName()); 	// 清除cookie 
	}
	
	// 查询相关 
	
 	/** 
 	 * 获取当前会话是否已经登录
 	 * @return 是否已登录 
 	 */
 	public boolean isLogin() {
 		return getLoginId_defaultNull() != null;
 	}
 	
 	/** 
 	 * 检验当前会话是否已经登录，如未登录，则抛出异常
 	 */
 	public void checkLogin() {
 		getLoginId();
 	}
 	
 	/** 
 	 * 获取当前会话登录id, 如果未登录，则抛出异常
 	 * @return 
 	 */
 	public Object getLoginId() {
 		Object login_id = getLoginId_defaultNull();
 		if(login_id == null) {
 			throw new NotLoginException(this.login_key);
 		}
 		return login_id;
 	}
	
	/** 
	 * 获取当前会话登录id, 如果未登录，则返回默认值
	 * @param default_value
	 * @return
	 */
 	@SuppressWarnings("unchecked")
	public <T>T getLoginId(T default_value) {
		Object login_id = getLoginId_defaultNull();
		if(login_id == null) {
			return default_value;
		}
		if(default_value instanceof Integer) {
			return (T)Integer.valueOf(login_id.toString());
		}
		if(default_value instanceof Long) {
			return (T)Long.valueOf(login_id.toString());
		}
		if(default_value instanceof String) {
			return (T)login_id.toString();
		}
		return (T)login_id;
 	}
 	
	/** 
	 * 获取当前会话登录id, 如果未登录，则返回null
	 * @return
	 */
	public Object getLoginId_defaultNull() {
 		String tokenValue = getTokenValue();
 		if(tokenValue != null) {
 			Object login_id = SaTokenManager.getDao().getValue(getKey_TokenValue(tokenValue));
 			if(login_id != null) {
 				return login_id;
 			}
 		}
 		return null;
 	}

	/** 
	 * 获取当前会话登录id, 并转换为String
	 * @return
	 */
 	public String getLoginId_asString() {
 		return String.valueOf(getLoginId());
 	}

	/** 
	 * 获取当前会话登录id, 并转换为int
	 * @return
	 */
 	public int getLoginId_asInt() {
 		// Object login_id = getLoginId();
// 		if(login_id instanceof Integer) {
// 			return (Integer)login_id;
// 		}
 		return Integer.valueOf(String.valueOf(getLoginId()));
 	}

	/**
	 * 获取当前会话登录id, 并转换为long
	 * @return
	 */
 	public long getLoginId_asLong() {
// 		Object login_id = getLoginId();
// 		if(login_id instanceof Long) {
// 			return (Long)login_id;
// 		}
 		return Long.valueOf(String.valueOf(getLoginId()));
 	}
 	
 	/** 
 	 * 获取指定token对应的登录id，如果未登录，则返回 null 
 	 * @return 
 	 */
 	public Object getLoginIdByToken(String tokenValue) {
 		if(tokenValue != null) {
 			Object login_id = SaTokenManager.getDao().getValue(getKey_TokenValue(tokenValue));
 			if(login_id != null) {
 				return login_id;
 			}
 		}
 		return null;
 	}
 	
 	
	// =================== session相关 ===================  

	/** 
	 * 获取指定key的session, 如果没有，is_create=是否新建并返回
	 * @param sessionId
	 * @param is_create
	 * @return
	 */
	protected SaSession getSessionBySessionId(String sessionId, boolean is_create) {
		SaSession session = SaTokenManager.getDao().getSaSession(sessionId);
		if(session == null && is_create) {
			session = new SaSession(sessionId);
			SaTokenManager.getDao().saveSaSession(session, SaTokenManager.getConfig().getTimeout());
		}
		return session;
	}

	/** 
	 * 获取指定login_id的session
	 * @param login_id
	 * @return
	 */
	public SaSession getSessionByLoginId(Object login_id) {
		return getSessionBySessionId(getKey_session(login_id), false);
	}

	/** 
	 * 获取当前会话的session
	 * @return
	 */
	public SaSession getSession() {
		return getSessionBySessionId(getKey_session(getLoginId()), true);
	}
 	
	

	// =================== 权限验证操作 ===================  

 	/** 
 	 * 指定login_id是否含有指定权限
 	 * @param login_id
 	 * @param pcode
 	 * @return
 	 */
 	public boolean hasPermission(Object login_id, Object pcode) {
 		List<Object> pcodeList = SaTokenManager.getStp().getPermissionCodeList(login_id, login_key);
		return !(pcodeList == null || pcodeList.contains(pcode) == false);
 	}
 	
 	/** 
 	 * 当前会话是否含有指定权限
 	 * @param pcode
 	 * @return
 	 */
 	public boolean hasPermission(Object pcode) {
 		return hasPermission(getLoginId(), pcode);
 	}
	
 	/** 
 	 * 当前账号是否含有指定权限 ， 没有就抛出异常
 	 * @param pcode
 	 */
 	public void checkPermission(Object pcode) {
 		if(hasPermission(pcode) == false) {
			throw new NotPermissionException(pcode, this.login_key);
		}
 	}

 	/** 
 	 * 当前账号是否含有指定权限 ， 【指定多个，必须全都有】
 	 * @param pcodeArray
 	 */
 	public void checkPermissionAnd(Object... pcodeArray){
 		Object login_id = getLoginId();
 		List<Object> pcodeList = SaTokenManager.getStp().getPermissionCodeList(login_id, login_key);
 		for (Object pcode : pcodeArray) {
 			if(pcodeList.contains(pcode) == false) {
 				throw new NotPermissionException(pcode, this.login_key);	// 没有权限抛出异常 
 			}
 		}
 	}

 	/** 
 	 * 当前账号是否含有指定权限 ， 【指定多个，有一个就可以了】
 	 * @param pcodeArray
 	 */
 	public void checkPermissionOr(Object... pcodeArray){
 		Object login_id = getLoginId();
 		List<Object> pcodeList = SaTokenManager.getStp().getPermissionCodeList(login_id, login_key);
 		for (Object pcode : pcodeArray) {
 			if(pcodeList.contains(pcode) == true) {
 				return;		// 有的话提前退出
 			}
 		}
		if(pcodeArray.length > 0) {
	 		throw new NotPermissionException(pcodeArray[0], this.login_key);	// 没有权限抛出异常 
		}
 	}
 	
	
	// =================== 返回相应key ===================  

	/**
	 *  获取key：客户端 tokenName 
	 * @return
	 */
	public String getKey_tokenName() {
 		return SaTokenManager.getConfig().getTokenName();
 	}
	/**  
	 * 获取key： tokenValue 持久化
	 * @param tokenValue
	 * @return
	 */
	public String getKey_TokenValue(String tokenValue) {
		return SaTokenManager.getConfig().getTokenName() + ":" + login_key + ":token:" + tokenValue;
	}
	/** 
	 * 获取key： id 持久化
	 * @param login_id
	 * @return
	 */
	public String getKey_LoginId(Object login_id) {
		return SaTokenManager.getConfig().getTokenName() + ":" + login_key + ":id:" + login_id;
	}
	/** 
	 * 获取key： session 持久化  
	 * @param login_id
	 * @return
	 */
	public String getKey_session(Object login_id) {
		return SaTokenManager.getConfig().getTokenName() + ":" + login_key + ":session:" + login_id;
	}
 	
	
}

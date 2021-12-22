package cn.dev33.satoken.jwt;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

import java.util.Map;

/**
 * Sa-Token 整合 jwt -- stateless 无状态 
 * @author kong
 *
 */
public class StpLogicJwtForStateless extends StpLogic {

	/**
	 * Sa-Token 整合 jwt -- stateless 无状态 
	 */
	public StpLogicJwtForStateless() {
		super(StpUtil.TYPE);
	}

	/**
	 * Sa-Token 整合 jwt -- stateless 无状态 
	 * @param loginType 账号体系标识 
	 */
	public StpLogicJwtForStateless(String loginType) {
		super(loginType);
	}

	/**
	 * 获取jwt秘钥 
	 * @return / 
	 */
	public String jwtSecretKey() {
		return getConfig().getJwtSecretKey();
	}
	
	// 
	// ------ 重写方法 
	// 

	// ------------------- 获取token 相关 -------------------  
	
	/**
	 * 创建一个TokenValue 
	 */
	@Override
 	public String createTokenValue(Object loginId, String device, long timeout) {
 		return SaJwtUtil.createToken(loginType, loginId, device, timeout, jwtSecretKey());
	}

	@Override
	public String createTokenValue(Object loginId, String device, Map<String, Object> expandInfoMap, long timeout) {
		return SaJwtUtil.createToken(loginType, loginId, device, expandInfoMap, timeout, jwtSecretKey());
	}

	/**
	 * 获取当前会话的Token信息 
	 * @return token信息 
	 */
	@Override
	public SaTokenInfo getTokenInfo() {
		SaTokenInfo info = new SaTokenInfo();
		info.tokenName = getTokenName();
		info.tokenValue = getTokenValue();
		info.isLogin = isLogin();
		info.loginId = getLoginIdDefaultNull();
		info.loginType = getLoginType();
		info.tokenTimeout = getTokenTimeout();
		info.sessionTimeout = SaTokenDao.NOT_VALUE_EXPIRE;
		info.tokenSessionTimeout = SaTokenDao.NOT_VALUE_EXPIRE;
		info.tokenActivityTimeout = SaTokenDao.NOT_VALUE_EXPIRE;
		info.loginDevice = getLoginDevice();
		return info;
	}
	
	// ------------------- 登录相关操作 -------------------  

	/**
	 * 会话登录，并指定所有登录参数Model 
	 */
	@Override
	public void login(Object id, SaLoginModel loginModel) {

		SaTokenException.throwByNull(id, "账号id不能为空");
		
		// ------ 1、初始化 loginModel 
		loginModel.build(getConfig());
		
		// ------ 2、生成一个token  
		String tokenValue = createTokenValue(id, loginModel.getDeviceOrDefault(), loginModel.getExpandInfoMap(), loginModel.getTimeout());
		
		// 3、在当前会话写入tokenValue 
		setTokenValue(tokenValue, loginModel.getCookieTimeout());

		// $$ 通知监听器，账号xxx 登录成功 
		SaManager.getSaTokenListener().doLogin(loginType, id, loginModel);
	}

	/**
	 * 获取指定Token对应的账号id (不做任何特殊处理) 
	 */
	@Override
	public String getLoginIdNotHandle(String tokenValue) {
		// 先验证 loginType，如果不符，则视为无效token，返回null 
		String loginType = SaJwtUtil.getPayloadsNotCheck(tokenValue, jwtSecretKey()).getStr(SaJwtUtil.LOGIN_TYPE);
		if(getLoginType().equals(loginType) == false) {
			return null;
		}
		// 获取 loginId 
		try {
			Object loginId = SaJwtUtil.getLoginId(tokenValue, jwtSecretKey());
			return String.valueOf(loginId);
		} catch (NotLoginException e) {
			return null;
		}
	}

	@Override
	public Object getExpandInfoNotHandle(String tokenValue) {
		// 先验证 loginType，如果不符，则视为无效token，返回null
		String loginType = SaJwtUtil.getPayloadsNotCheck(tokenValue, jwtSecretKey()).getStr(SaJwtUtil.LOGIN_TYPE);
		if(getLoginType().equals(loginType) == false) {
			return null;
		}
		// 获取 expandInfoMap
		try {
			return SaJwtUtil.getExpandInfo(tokenValue, jwtSecretKey());
		} catch (NotLoginException e) {
			return null;
		}
	}

	/**
	 * 会话注销 
	 */
	@Override
	public void logout() {
		// ... 

 		// 从当前 [storage存储器] 里删除 
 		SaHolder.getStorage().delete(splicingKeyJustCreatedSave());
 		
 		// 如果打开了Cookie模式，则把cookie清除掉 
 		if(getConfig().getIsReadCookie()){
 			SaHolder.getResponse().deleteCookie(getTokenName());
		}
	}
	
 	
 	// ------------------- 过期时间相关 -------------------  

 	/**
 	 * 获取当前登录者的 token 剩余有效时间 (单位: 秒)
 	 */
	@Override
 	public long getTokenTimeout() {
 		return SaJwtUtil.getTimeout(getTokenValue(), jwtSecretKey());
 	}
 	
 	
 	// ------------------- id 反查 token 相关操作 -------------------  

	/**
	 * 返回当前会话的登录设备 
	 * @return 当前令牌的登录设备 
	 */
	@Override
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
		// 获取
		return SaJwtUtil.getPayloadsNotCheck(tokenValue, jwtSecretKey()).getStr(SaJwtUtil.DEVICE); 
	}

	
	// ------------------- Bean对象代理 -------------------  
	
	/**
	 * [禁用] 返回持久化对象 
	 */
	@Override
	public SaTokenDao getSaTokenDao() {
		throw new ApiDisabledException();
	}
	
	
}

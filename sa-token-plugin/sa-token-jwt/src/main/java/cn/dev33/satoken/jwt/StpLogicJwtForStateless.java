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
package cn.dev33.satoken.jwt;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.jwt.error.SaJwtErrorCode;
import cn.dev33.satoken.jwt.exception.SaJwtException;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.Map;

/**
 * Sa-Token 整合 jwt -- Stateless 无状态模式
 * 
 * @author click33
 * @since 1.30.0
 */
public class StpLogicJwtForStateless extends StpLogic {

	/**
	 * Sa-Token 整合 jwt -- Stateless 无状态 
	 */
	public StpLogicJwtForStateless() {
		super(StpUtil.TYPE);
	}

	/**
	 * Sa-Token 整合 jwt -- Stateless 无状态 
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
		String keyt = getConfigOrGlobal().getJwtSecretKey();
		SaJwtException.throwByNull(keyt, "请配置jwt秘钥", SaJwtErrorCode.CODE_30205);
		return keyt;
	}
	
	// 
	// ------ 重写方法 
	// 

	// ------------------- 获取token 相关 -------------------  
	
	/**
	 * 创建一个TokenValue 
	 */
	@Override
	public String createTokenValue(Object loginId, String deviceType, long timeout, Map<String, Object> extraData) {
		return SaJwtUtil.createToken(loginType, loginId, deviceType, timeout, extraData, jwtSecretKey());
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
		info.tokenActiveTimeout = SaTokenDao.NOT_VALUE_EXPIRE;
		info.loginDeviceType = getLoginDeviceType();
		return info;
	}
	
	// ------------------- 登录相关操作 -------------------  

	/**
	 * 创建指定账号id的登录会话
	 * @param id 登录id，建议的类型：（long | int | String）
	 * @param loginParameter 此次登录的参数Model 
	 * @return 返回会话令牌 
	 */
	@Override
	public String createLoginSession(Object id, SaLoginParameter loginParameter) {

		// 1、先检查一下，传入的参数是否有效
		checkLoginArgs(id, loginParameter);

		// 3、生成一个token
		String tokenValue = createTokenValue(id, loginParameter.getDeviceType(), loginParameter.getTimeout(), loginParameter.getExtraData());
		
		// 4、$$ 发布事件：账号xxx 登录成功
		SaTokenEventCenter.doLogin(loginType, id, tokenValue, loginParameter);

		// 5、返回
		return tokenValue;
	}

	/**
	 * 获取指定Token对应的账号id (不做任何特殊处理) 
	 */
	@Override
	public String getLoginIdNotHandle(String tokenValue) {
		try {
			Object loginId = SaJwtUtil.getLoginId(tokenValue, loginType, jwtSecretKey());
			return String.valueOf(loginId);
		} catch (SaJwtException e) {
			// CODE == 30204 时，代表token已过期，此时返回-3，以便外层更精确的显示异常信息
			if(e.getCode() == SaJwtErrorCode.CODE_30204) {
				return NotLoginException.TOKEN_TIMEOUT;
			}
			return null;
		}
	}

	/**
	 * 会话注销 
	 */
	@Override
	public void logout() {
		// 如果连token都没有，那么无需执行任何操作 
		String tokenValue = getTokenValue();
 		if(SaFoxUtil.isEmpty(tokenValue)) {
 			return;
 		}

 		// 从当前 [storage存储器] 里删除 
 		SaHolder.getStorage().delete(splicingKeyJustCreatedSave());
 		
 		// 如果打开了Cookie模式，则把cookie清除掉 
 		if(getConfigOrGlobal().getIsReadCookie()){
 			SaHolder.getResponse().deleteCookie(getTokenName());
		}
	}

	/**
	 * 获取当前 Token 的扩展信息 
	 */
	@Override
	public Object getExtra(String key) {
		return getExtra(getTokenValue(), key);
	}

	/**
	 * 获取指定 Token 的扩展信息 
	 */
	@Override
	public Object getExtra(String tokenValue, String key) {
		return SaJwtUtil.getPayloads(tokenValue, loginType, jwtSecretKey()).get(key);
	}

 	
 	// ------------------- 过期时间相关 -------------------  

 	/**
 	 * 获取指定 token 剩余有效时间 (单位: 秒)
 	 */
	@Override
 	public long getTokenTimeout(String tokenValue) {
 		return SaJwtUtil.getTimeout(getTokenValue(), loginType, jwtSecretKey());
 	}
 	
 	
 	// ------------------- id 反查 token 相关操作 -------------------  

	/**
	 * 返回当前会话的登录设备类型 
	 * @return 当前令牌的登录设备类型
	 */
	@Override
	public String getLoginDeviceType() {
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
		return SaJwtUtil.getPayloadsNotCheck(tokenValue, loginType, jwtSecretKey()).getStr(SaJwtUtil.DEVICE_TYPE);
	}

	
	// ------------------- Bean对象代理 -------------------  
	
	/**
	 * [禁用] 返回持久化对象 
	 */
	@Override
	public SaTokenDao getSaTokenDao() {
		throw new ApiDisabledException();
	}

	/**
	 * 重写返回：支持 extra 扩展参数
	 */
	@Override
	public boolean isSupportExtra() {
		return true;
	}

}

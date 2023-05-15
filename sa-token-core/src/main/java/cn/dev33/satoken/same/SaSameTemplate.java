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
package cn.dev33.satoken.same;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.SameTokenInvalidException;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa Same-Token 同源系统身份认证模块 - 模板方法类 
 * 
 * <p> 解决同源系统互相调用时的身份认证校验， 例如：微服务网关请求转发鉴权、微服务RPC调用鉴权 
 * 
 * @author click33
 * @since 2022-10-24
 */
public class SaSameTemplate {

	/**
	 * 提交 Same-Token 时，建议使用的参数名称 
	 */
	 public static final String SAME_TOKEN = "SA-SAME-TOKEN";
	
	// -------------------- 获取 & 校验 
	
	/**
	 * 获取当前 Same-Token, 如果不存在，则立即创建并返回 
	 * @return / 
	 */
	public String getToken() {
		String currentToken = getTokenNh(); 
		if(SaFoxUtil.isEmpty(currentToken)) {
			// 注意这里的自刷新不能做到高并发可用 
			currentToken = refreshToken();
		}
		return currentToken;
	}

	/**
	 * 判断一个 Same-Token 是否有效 
	 * @param token / 
	 * @return /
	 */
	public boolean isValid(String token) {
		// 1、 如果传入的 token 为空，则立即返回 false 
		if(SaFoxUtil.isEmpty(token)) {
			return false;
		}
		
		// 2、 验证当前 Same-Token 及 Past-Same-Token 
		return token.equals(getToken()) || token.equals(getPastTokenNh());
	}

	/**
	 * 校验一个 Same-Token 是否有效 (如果无效则抛出异常) 
	 * @param token / 
	 */
	public void checkToken(String token) {
		if(isValid(token) == false) {
			token = (token == null ? "" : token);
			throw new SameTokenInvalidException("无效Same-Token：" + token).setCode(SaErrorCode.CODE_10301);
		}
	}

	/**
	 * 校验当前 Request 上下文提供的 Same-Token 是否有效 (如果无效则抛出异常) 
	 */
	public void checkCurrentRequestToken() {
		checkToken(SaHolder.getRequest().getHeader(SAME_TOKEN));
	}
	
	/**
	 * 刷新一次 Same-Token (注意集群环境中不要多个服务重复调用) 
	 * @return 刷新后产生的新 Same-Token 
	 */
	public String refreshToken() {
		
		// 1. 先将当前 Same-Token 写入到 Past-Same-Token 中 
		String sameToken = getTokenNh(); 
		if(SaFoxUtil.isEmpty(sameToken) == false) {
			savePastToken(sameToken, getTokenTimeout());
		}
		
		// 2. 再刷新当前 Same-Token
		String newSameToken = createToken();
		saveToken(newSameToken);
		
		// 3. 返回新的 Same-Token
		return newSameToken;
	}

	
	// ------------------------------ 保存Token 
	
	/**
	 * 保存 Same-Token
	 * @param token / 
	 */
	public void saveToken(String token) {
		if(SaFoxUtil.isEmpty(token)) {
			return;
		}
		SaManager.getSaTokenDao().set(splicingTokenSaveKey(), token, SaManager.getConfig().getSameTokenTimeout());
	}
	
	/**
	 * 保存 Past-Same-Token
	 * @param token token
	 * @param timeout 有效期（单位：秒）
	 */
	public void savePastToken(String token, long timeout){
		if(SaFoxUtil.isEmpty(token)) {
			return;
		}
		SaManager.getSaTokenDao().set(splicingPastTokenSaveKey(), token, timeout);
	}
	
	
	// -------------------- 获取Token 
	
	/**
	 * 获取 Same-Token，不做任何处理 
	 * @return / 
	 */
	public String getTokenNh() {
		return SaManager.getSaTokenDao().get(splicingTokenSaveKey());
	}
	
	/**
	 * 获取 Past-Same-Token，不做任何处理 
	 * @return / 
	 */
	public String getPastTokenNh() {
		return SaManager.getSaTokenDao().get(splicingPastTokenSaveKey());
	}

	/**
	 * 获取 Same-Token 的剩余有效期 (单位：秒) 
	 * @return / 
	 */
	public long getTokenTimeout() {
		return SaManager.getSaTokenDao().getTimeout(splicingTokenSaveKey());
	}
	

	// -------------------- 创建Token 
	
	/**
	 * 创建一个 Same-Token 
	 * @return Token 
	 */
	public String createToken() {
		return SaFoxUtil.getRandomString(64);
	}


	// -------------------- 拼接key 

	/** 
	 * 拼接key：Same-Token 的存储 key
	 * @return key
	 */
	public String splicingTokenSaveKey() {
		return SaManager.getConfig().getTokenName() + ":var:same-token";
	}

	/** 
	 * 拼接key：次级 Same-Token 的存储 key
	 * @return key
	 */
	public String splicingPastTokenSaveKey() {
		return SaManager.getConfig().getTokenName() + ":var:past-same-token";
	}

}

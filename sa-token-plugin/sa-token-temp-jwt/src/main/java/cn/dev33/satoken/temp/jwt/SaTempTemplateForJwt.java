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
package cn.dev33.satoken.temp.jwt;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.temp.SaTempTemplate;
import cn.dev33.satoken.temp.jwt.error.SaTempJwtErrorCode;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.List;

/**
 * Sa-Token 临时令牌验证模块接口 JWT实现类，提供以 JWT 为逻辑内核的临时 token 验证功能
 *
 * @author click33
 * @since 1.20.0
 */
public class SaTempTemplateForJwt extends SaTempTemplate {
	
	/**
	 * 根据value创建一个token 
	 */
	@Override
	public String createToken(Object value, long timeout, boolean isRecordIndex) {
		return SaJwtUtil.createToken(value, timeout, getJwtSecretKey());
	}
	
	/**
	 *  解析token获取value 
	 */
	@Override
	public Object parseToken(String token) {
		return SaJwtUtil.getValue(token, getJwtSecretKey());
	}
	
	/**
	 * 返回指定token的剩余有效期，单位：秒 
	 */
	@Override
	public long getTimeout(String token) {
		return SaJwtUtil.getTimeout(token, getJwtSecretKey());
	}

	/**
	 * 删除一个token
	 */
	@Override
	public void deleteToken(String token) {
		throw new ApiDisabledException("jwt cannot delete token").setCode(SaTempJwtErrorCode.CODE_30302);
	}

	/**
	 * 获取指定 value 的 temp-token 列表记录
	 * @param value /
	 * @return /
	 */
	public List<String> getTempTokenList(Object value) {
		throw new ApiDisabledException("jwt cannot get token list").setCode(SaTempJwtErrorCode.CODE_30304);
	}

	/**
	 * 获取jwt秘钥 
	 * @return jwt秘钥 
	 */
	@Override
	public String getJwtSecretKey() {
		String jwtSecretKey = SaManager.getConfig().getJwtSecretKey();
		if(SaFoxUtil.isEmpty(jwtSecretKey)) {
			throw new SaTokenException("请配置：jwtSecretKey").setCode(SaTempJwtErrorCode.CODE_30301);
		}
		return jwtSecretKey;
	}
	
}

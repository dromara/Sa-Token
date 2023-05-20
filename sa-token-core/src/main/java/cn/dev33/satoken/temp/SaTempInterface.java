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
package cn.dev33.satoken.temp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Sa-Token 临时 token 验证模块 - 接口
 *
 * <p>
 *     有效期很短的一种token，一般用于一次性接口防盗用、短时间资源访问等业务场景
 * </p>
 *
 * @author click33
 * @since 1.20.0
 */
public interface SaTempInterface {

	// -------- 创建

	/**
	 * 为指定 value 创建一个临时 Token
	 * @param value 指定值
	 * @param timeout 有效时间，单位：秒，-1 代表永久有效
	 * @return 生成的 token
	 */
	default String createToken(Object value, long timeout) {
		return createToken(SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE, value, timeout);
	}

	/**
	 * 为指定 业务标识、指定 value 创建一个 Token
	 * @param service 业务标识
	 * @param value 指定值
	 * @param timeout 有效期，单位：秒，-1 代表永久有效
	 * @return 生成的token
	 */
	default String createToken(String service, Object value, long timeout) {
		
		// 生成 token 
		String token = SaStrategy.me.createToken.apply(null, null);
		
		// 持久化映射关系 
		String key = splicingKeyTempToken(service, token);
		SaManager.getSaTokenDao().setObject(key, value, timeout);
		
		// 返回 
		return token;
	}

	// -------- 解析

	/**
	 * 解析 Token 获取 value 
	 * @param token 指定 Token 
	 * @return  / 
	 */
	default Object parseToken(String token) {
		return parseToken(SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE, token);
	}

	/**
	 * 解析 Token 获取 value 
	 * @param service 业务标识
	 * @param token 指定 Token
	 * @return /
	 */
	default Object parseToken(String service, String token) {
		String key = splicingKeyTempToken(service, token);
		return SaManager.getSaTokenDao().getObject(key);
	}

	/**
	 * 解析 Token 获取 value，并转换为指定类型 
	 * @param token 指定 Token 
	 * @param cs 指定类型 
	 * @param <T> 默认值的类型 
	 * @return /
	 */
	default<T> T parseToken(String token, Class<T> cs) {
		return parseToken(SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE, token, cs);
	}

	/**
	 * 解析 Token 获取 value，并转换为指定类型
	 * @param service 业务标识
	 * @param token 指定 Token 
	 * @param cs 指定类型 
	 * @param <T> 默认值的类型 
	 * @return /
	 */
	default<T> T parseToken(String service, String token, Class<T> cs) {
		return SaFoxUtil.getValueByType(parseToken(service, token), cs);
	}
	
	/**
	 * 获取指定 Token 的剩余有效期，单位：秒 
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param token 指定 Token
	 * @return /
	 */
	default long getTimeout(String token) {
		return getTimeout(SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE, token);
	}

	/**
	 * 获取指定 业务标识、指定 Token 的剩余有效期，单位：秒
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param service 业务标识
	 * @param token 指定 Token
	 * @return / 
	 */
	default long getTimeout(String service, String token) {
		String key = splicingKeyTempToken(service, token);
		return SaManager.getSaTokenDao().getObjectTimeout(key);
	}

	// -------- 删除

	/**
	 * 删除一个 Token
	 * @param token 指定 Token 
	 */
	default void deleteToken(String token) {
		deleteToken(SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE, token);
	}
	
	/**
	 * 删除一个 Token
	 * @param service 业务标识
	 * @param token 指定 Token 
	 */
	default void deleteToken(String service, String token) {
		String key = splicingKeyTempToken(service, token);
		SaManager.getSaTokenDao().deleteObject(key);
	}

	// -------- 其它

	/**  
	 * 获取：在存储临时 token 数据时，应该使用的 key
	 * @param service 业务标识
	 * @param token token值
	 * @return key
	 */
	default String splicingKeyTempToken(String service, String token) {
		return SaManager.getConfig().getTokenName() + ":temp-token:" + service + ":" + token;
	}

	/**
	 * @return jwt秘钥 (只有集成 sa-token-temp-jwt 模块时此参数才会生效)  
	 */
	default String getJwtSecretKey() {
		return null;
	}
	
}

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

/**
 * Sa-Token 临时 token 验证模块 - 工具类
 *
 * <p>
 *     有效期很短的一种token，一般用于一次性接口防盗用、短时间资源访问等业务场景
 * </p>
 *
 * @author click33
 * @since 1.20.0
 */
public class SaTempUtil {

	private SaTempUtil() {
	}

	// -------- 创建

	/**
	 * 为指定 value 创建一个临时 Token
	 * @param value 指定值
	 * @param timeout 有效期，单位：秒，-1 代表永久有效
	 * @return 生成的token
	 */
	public static String createToken(Object value, long timeout) {
		return SaManager.getSaTemp().createToken(value, timeout);
	}

	/**
	 * 为指定 业务标识、指定 value 创建一个 Token
	 * @param service 业务标识
	 * @param value 指定值
	 * @param timeout 有效期，单位：秒，-1 代表永久有效
	 * @return 生成的token
	 */
	public static String createToken(String service, Object value, long timeout) {
		return SaManager.getSaTemp().createToken(service, value, timeout);
	}

	// -------- 解析

	/**
	 * 解析 Token 获取 value 
	 * @param token 指定 Token 
	 * @return  / 
	 */
	public static Object parseToken(String token) {
		return SaManager.getSaTemp().parseToken(token);
	}

	/**
	 * 解析 Token 获取 value 
	 * @param service 业务标识
	 * @param token 指定 Token 
	 * @return /
	 */
	public static Object parseToken(String service, String token) {
		return SaManager.getSaTemp().parseToken(service, token);
	}

	/**
	 * 解析 Token 获取 value，并转换为指定类型 
	 * @param token 指定 Token 
	 * @param cs 指定类型 
	 * @param <T> 默认值的类型 
	 * @return /
	 */
	public static<T> T parseToken(String token, Class<T> cs) {
		return SaManager.getSaTemp().parseToken(token, cs);
	}

	/**
	 * 解析 Token 获取 value，并转换为指定类型
	 * @param service 业务标识
	 * @param token 指定 Token 
	 * @param cs 指定类型 
	 * @param <T> 默认值的类型 
	 * @return /
	 */
	public static<T> T parseToken(String service, String token, Class<T> cs) {
		return SaManager.getSaTemp().parseToken(service, token, cs);
	}
	
	/**
	 * 获取指定 Token 的剩余有效期，单位：秒 
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param token 指定 Token
	 * @return /
	 */
	public static long getTimeout(String token) {
		return SaManager.getSaTemp().getTimeout(token);
	}

	/**
	 * 获取指定 业务标识、指定 Token 的剩余有效期，单位：秒
	 * <p> 返回值 -1 代表永久，-2 代表token无效 
	 * @param service 业务标识
	 * @param token 指定 Token
	 * @return / 
	 */
	public static long getTimeout(String service, String token) {
		return SaManager.getSaTemp().getTimeout(service, token);
	}

	// -------- 删除

	/**
	 * 删除一个 Token
	 * @param token 指定 Token 
	 */
	public static void deleteToken(String token) {
		SaManager.getSaTemp().deleteToken(token);
	}
	
	/**
	 * 删除一个 Token
	 * @param service 业务标识
	 * @param token 指定 Token 
	 */
	public static void deleteToken(String service, String token) {
		SaManager.getSaTemp().deleteToken(service, token);
	}
	
}

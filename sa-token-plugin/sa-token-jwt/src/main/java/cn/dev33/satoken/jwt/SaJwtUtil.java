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

import java.util.Map;

import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;

/**
 * jwt 操作工具类封装
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaJwtUtil {
	
	/**
	 * 底层 saJwtTemplate 对象 
	 */
	public static SaJwtTemplate saJwtTemplate = new SaJwtTemplate();

	/**
	 * 获取底层 saJwtTemplate 对象 
	 * @return /
	 */
	public static SaJwtTemplate getSaJwtTemplate() {
		return saJwtTemplate;
	}

	/**
	 * 设置底层 saJwtTemplate 对象 
	 * @param saJwtTemplate / 
	 */
	public static void setSaJwtTemplate(SaJwtTemplate saJwtTemplate) {
		SaJwtUtil.saJwtTemplate = saJwtTemplate;
	}
	
	// 常量
	

	/**
	 * key：账号类型  
	 */
	public static final String LOGIN_TYPE = SaJwtTemplate.LOGIN_TYPE; 
	
	/**
	 * key：账号id  
	 */
	public static final String LOGIN_ID = SaJwtTemplate.LOGIN_ID; 
	
	/**
	 * key：登录设备类型
	 */
	public static final String DEVICE = SaJwtTemplate.DEVICE; 
	
	/**
	 * key：有效截止期 (时间戳) 
	 */
	public static final String EFF = SaJwtTemplate.EFF; 

	/**
	 * key：乱数 （ 混入随机字符串，防止每次生成的 token 都是一样的 ）
	 */
	public static final String RN_STR = SaJwtTemplate.RN_STR; 

	/** 
	 * 当有效期被设为此值时，代表永不过期 
	 */ 
	public static final long NEVER_EXPIRE = SaJwtTemplate.NEVER_EXPIRE; 

	/** 
	 * 表示一个值不存在 
	 */ 
	public static final long NOT_VALUE_EXPIRE = SaJwtTemplate.NOT_VALUE_EXPIRE; 
	
	// ------ 创建

	/**
	 * 创建 jwt （简单方式）
     * @param loginType 登录类型 
	 * @param loginId 账号id 
	 * @param extraData 扩展数据
     * @param keyt 秘钥
	 * @return jwt-token 
	 */
    public static String createToken(String loginType, Object loginId, Map<String, Object> extraData, String keyt) {
    	return saJwtTemplate.createToken(loginType, loginId, extraData, keyt);
    }

	/**
	 * 创建 jwt （全参数方式）
	 * @param loginType 账号类型
	 * @param loginId 账号id
	 * @param device 设备类型
	 * @param timeout token有效期 (单位 秒)
	 * @param extraData 扩展数据
	 * @param keyt 秘钥
	 * @return jwt-token
	 */
	public static String createToken(String loginType, Object loginId, String device,
									 long timeout, Map<String, Object> extraData, String keyt) {
		return saJwtTemplate.createToken(loginType, loginId, device, timeout, extraData, keyt);
	}

	/**
	 * 为 JWT 对象和 keyt 秘钥，生成 token 字符串 
	 * @param jwt JWT构建对象
	 * @param keyt 秘钥 
	 * @return 根据 JWT 对象和 keyt 秘钥，生成的 token 字符串
	 */
	public static String generateToken (JWT jwt, String keyt) {
		return saJwtTemplate.generateToken(jwt, keyt);
	}
	
	// ------ 解析 

    /**
     * jwt 解析 
     * @param token Jwt-Token值 
     * @param loginType 登录类型 
     * @param keyt 秘钥
     * @param isCheckTimeout 是否校验 timeout 字段
     * @return 解析后的jwt 对象 
     */
    public static JWT parseToken(String token, String loginType, String keyt, boolean isCheckTimeout) {
		return saJwtTemplate.parseToken(token, loginType, keyt, isCheckTimeout);
    }

    /**
     * 获取 jwt 数据载荷 （校验 sign、loginType、timeout） 
     * @param token token值
     * @param loginType 登录类型 
     * @param keyt 秘钥 
     * @return 载荷 
     */
    public static JSONObject getPayloads(String token, String loginType, String keyt) {
    	return saJwtTemplate.getPayloads(token, loginType, keyt);
    }

    /**
     * 获取 jwt 数据载荷 （校验 sign、loginType，不校验 timeout） 
     * @param token token值
     * @param loginType 登录类型 
     * @param keyt 秘钥 
     * @return 载荷 
     */
    public static JSONObject getPayloadsNotCheck(String token, String loginType, String keyt) {
    	return saJwtTemplate.getPayloadsNotCheck(token, loginType, keyt);
    }
    
    /**
     * 获取 jwt 代表的账号id 
     * @param token Token值 
     * @param loginType 登录类型 
     * @param keyt 秘钥
     * @return 值 
     */
    public static Object getLoginId(String token, String loginType, String keyt) {
    	return saJwtTemplate.getLoginId(token, loginType, keyt);
    }

    /**
     * 获取 jwt 代表的账号id (未登录时返回null)
     * @param token Token值 
     * @param loginType 登录类型 
     * @param keyt 秘钥
     * @return 值 
     */
    public static Object getLoginIdOrNull(String token, String loginType, String keyt) {
    	return saJwtTemplate.getLoginIdOrNull(token, loginType, keyt);
    }

    /**
     * 获取 jwt 剩余有效期 
     * @param token JwtToken值 
     * @param loginType 登录类型 
     * @param keyt 秘钥
     * @return 值 
     */
    public static long getTimeout(String token, String loginType, String keyt) {
    	return saJwtTemplate.getTimeout(token, loginType, keyt);
    }

}

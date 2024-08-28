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

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.error.SaJwtErrorCode;
import cn.dev33.satoken.jwt.exception.SaJwtException;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTException;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;

import java.util.Map;
import java.util.Objects;

/**
 * jwt 操作模板方法封装
 *
 * @author click33
 * @since 1.31.0
 */
public class SaJwtTemplate {
	
	/**
	 * key：账号类型  
	 */
	public static final String LOGIN_TYPE = "loginType"; 
	
	/**
	 * key：账号id  
	 */
	public static final String LOGIN_ID = "loginId"; 
	
	/**
	 * key：登录设备类型
	 */
	public static final String DEVICE = "device"; 
	
	/**
	 * key：有效截止期 (时间戳) 
	 */
	public static final String EFF = "eff";

	/**
	 * key：乱数 （ 混入随机字符串，防止每次生成的 token 都是一样的 ）
	 */
	public static final String RN_STR = "rnStr";

	/** 
	 * 当有效期被设为此值时，代表永不过期 
	 */ 
	public static final long NEVER_EXPIRE = SaTokenDao.NEVER_EXPIRE;

	/** 
	 * 表示一个值不存在 
	 */ 
	public static final long NOT_VALUE_EXPIRE = SaTokenDao.NOT_VALUE_EXPIRE;
	
	// ------ 创建

	/**
	 * 创建 jwt （简单方式）
	 *
     * @param loginType 登录类型 
	 * @param loginId 账号id 
	 * @param extraData 扩展数据
     * @param keyt 秘钥
	 * @return jwt-token 
	 */
    public String createToken(String loginType, Object loginId, Map<String, Object> extraData, String keyt) {
    	
    	// 构建
    	JWT jwt = JWT.create()
				.setPayload(LOGIN_TYPE, loginType)
			    .setPayload(LOGIN_ID, loginId)
				// 塞入一个随机字符串，防止同账号下每次生成的 token 都一样的
			    .setPayload(RN_STR, SaFoxUtil.getRandomString(32))
				.addPayloads(extraData)
			    ;
    	
    	// 返回 
		return generateToken(jwt, keyt);
    }

	/**
	 * 创建 jwt （全参数方式）
	 *
	 * @param loginType 账号类型
	 * @param loginId 账号id
	 * @param device 设备类型
	 * @param timeout token有效期 (单位 秒)
	 * @param extraData 扩展数据
	 * @param keyt 秘钥
	 * @return jwt-token
	 */
	public String createToken(String loginType, Object loginId, String device,
									 long timeout, Map<String, Object> extraData, String keyt) {

		// 计算 eff 有效期：
		// 		如果 timeout 指定为 -1，那么 eff 也为 -1，代表永不过期
		// 		如果 timeout 指定为一个具体的值，那么 eff 为 13 位时间戳，代表此 token 到期的时间
		long effTime = timeout;
		if(timeout != NEVER_EXPIRE) {
			effTime = timeout * 1000 + System.currentTimeMillis();
		}

		// 创建  
		JWT jwt = JWT.create()
				.setPayload(LOGIN_TYPE, loginType)
				.setPayload(LOGIN_ID, loginId)
				.setPayload(DEVICE, device)
				.setPayload(EFF, effTime)
				// 塞入一个随机字符串，防止同账号同一毫秒下每次生成的 token 都一样的
			    .setPayload(RN_STR, SaFoxUtil.getRandomString(32))
				.addPayloads(extraData);

		// 返回
		return generateToken(jwt, keyt);
	}

	/**
	 * 为 JWT 对象和 keyt 秘钥，生成 token 字符串
	 *
	 * @param jwt JWT构建对象
	 * @param keyt 秘钥 
	 * @return 根据 JWT 对象和 keyt 秘钥，生成的 token 字符串
	 */
	public String generateToken (JWT jwt, String keyt) {
		return jwt.setSigner(createSigner(keyt)).sign();
	}

	/**
	 * 返回 jwt 使用的签名算法
	 *
	 * @param keyt 秘钥
	 * @return /
	 */
	public JWTSigner createSigner (String keyt) {
		return JWTSignerUtil.hs256(keyt.getBytes());
	}

	// ------ 解析 

    /**
     * jwt 解析
	 *
     * @param token Jwt-Token值 
     * @param loginType 登录类型 
     * @param keyt 秘钥
     * @param isCheckTimeout 是否校验 timeout 字段
     * @return 解析后的jwt 对象 
     */
    public JWT parseToken(String token, String loginType, String keyt, boolean isCheckTimeout) {

    	// 秘钥不可以为空
    	if(SaFoxUtil.isEmpty(keyt)) {
    		throw new SaJwtException("请配置 jwt 秘钥");
    	}

    	// 如果token为null 
    	if(token == null) {
    		throw new SaJwtException("jwt 字符串不可为空");
    	}
    	
    	// 解析 
    	JWT jwt;
    	try {
    		jwt = JWT.of(token);
		} catch (JWTException | JSONException e) {
    		throw new SaJwtException("jwt 解析失败：" + token, e).setCode(SaJwtErrorCode.CODE_30201);
		}
    	JSONObject payloads = jwt.getPayloads();
    	
    	// 校验 Token 签名
		boolean verify = jwt.setSigner(createSigner(keyt)).verify();
    	if( ! verify) {
    		throw new SaJwtException("jwt 签名无效：" + token).setCode(SaJwtErrorCode.CODE_30202);
    	}

    	// 校验 loginType 
    	if( ! Objects.equals(loginType, payloads.getStr(LOGIN_TYPE))) {
    		throw new SaJwtException("jwt loginType 无效：" + token).setCode(SaJwtErrorCode.CODE_30203);
    	}
    	
    	// 校验 Token 有效期
    	if(isCheckTimeout) {
    		Long effTime = payloads.getLong(EFF, 0L);
        	if(effTime != NEVER_EXPIRE) {
        		if(effTime == null || effTime < System.currentTimeMillis()) {
        			throw new SaJwtException("jwt 已过期：" + token).setCode(SaJwtErrorCode.CODE_30204);
        		}
        	}
    	}
    	
        // 返回 
        return jwt;
    }

    /**
     * 获取 jwt 数据载荷 （校验 sign、loginType、timeout） 
     * @param token token值
     * @param loginType 登录类型 
     * @param keyt 秘钥 
     * @return 载荷 
     */
    public JSONObject getPayloads(String token, String loginType, String keyt) {
    	return parseToken(token, loginType, keyt, true).getPayloads();
    }

    /**
     * 获取 jwt 数据载荷 （校验 sign、loginType，不校验 timeout） 
     * @param token token值
     * @param loginType 登录类型 
     * @param keyt 秘钥 
     * @return 载荷 
     */
    public JSONObject getPayloadsNotCheck(String token, String loginType, String keyt) {
    	return parseToken(token, loginType, keyt, false).getPayloads();
    }
    
    /**
     * 获取 jwt 代表的账号id 
     * @param token Token值 
     * @param loginType 登录类型 
     * @param keyt 秘钥
     * @return 值 
     */
    public Object getLoginId(String token, String loginType, String keyt) {
    	return getPayloads(token, loginType, keyt).get(LOGIN_ID);
    }

    /**
     * 获取 jwt 代表的账号id (未登录时返回null)
     * @param token Token值 
     * @param loginType 登录类型 
     * @param keyt 秘钥
     * @return 值 
     */
    public Object getLoginIdOrNull(String token, String loginType, String keyt) {
    	try {
    		return getPayloads(token, loginType, keyt).get(LOGIN_ID);
		} catch (SaJwtException e) {
			return null;
		}
    }

    /**
     * 获取 jwt 剩余有效期 
     * @param token JwtToken值 
     * @param loginType 登录类型 
     * @param keyt 秘钥
     * @return 值 
     */
    public long getTimeout(String token, String loginType, String keyt) {
    	
    	// 如果token为null 
    	if(token == null) {
    		return NOT_VALUE_EXPIRE;
    	}
    	
    	// 取出数据 
    	JWT jwt;
    	try {
    		jwt = JWT.of(token);
		} catch (JWTException e) {
			// 解析失败 
			return NOT_VALUE_EXPIRE;
		}
    	JSONObject payloads = jwt.getPayloads();
    	
    	// 如果签名无效 
    	boolean verify = jwt.setSigner(createSigner(keyt)).verify();
    	if( ! verify) {
    		return NOT_VALUE_EXPIRE;
    	}

    	// 如果 loginType  无效 
    	if( ! Objects.equals(loginType, payloads.getStr(LOGIN_TYPE))) {
    		return NOT_VALUE_EXPIRE;
    	}
    	
    	// 如果被设置为：永不过期 
    	Long effTime = payloads.get(EFF, Long.class);
    	if(effTime == NEVER_EXPIRE) {
    		return NEVER_EXPIRE;
    	}
    	// 如果已经超时 
    	if(effTime == null || effTime < System.currentTimeMillis()) {
    		return NOT_VALUE_EXPIRE;
    	}
    	
        // 计算timeout (转化为以秒为单位的有效时间)
        return (effTime - System.currentTimeMillis()) / 1000;
    }



	// -------------- 其它方法

	/**
	 * 创建 jwt （Map 参数方式）
	 *
	 * @param map 扩展数据
	 * @param keyt 秘钥
	 * @return jwt-token
	 */
	public String createToken(Map<String, Object> map, String keyt) {
		// 创建
		JWT jwt = JWT.create().addPayloads(map);

		// 返回
		return generateToken(jwt, keyt);
	}


}

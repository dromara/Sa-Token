package cn.dev33.satoken.jwt;

import java.util.Map;
import java.util.Objects;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.jwt.exception.SaJwtException;
import cn.dev33.satoken.jwt.exception.SaJwtExceptionCode;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTException;

/**
 * jwt 操作工具类封装 
 * @author kong
 *
 */
public class SaJwtUtil {
	
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
     * @param loginType 登录类型 
	 * @param loginId 账号id 
	 * @param extraData 扩展数据
     * @param keyt 秘钥
	 * @return jwt-token 
	 */
    public static String createToken(String loginType, Object loginId, Map<String, Object> extraData, String keyt) {
    	
    	// 构建
    	String token = JWT.create()
				.setPayload(LOGIN_TYPE, loginType)
			    .setPayload(LOGIN_ID, loginId)
			    .setPayload(RN_STR, SaFoxUtil.getRandomString(32))
				.addPayloads(extraData)
			    .setKey(keyt.getBytes())
			    .sign();
    	
    	// 返回 
    	return token;
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

		// 计算有效期
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
			    .setPayload(RN_STR, SaFoxUtil.getRandomString(32))
				.addPayloads(extraData);

		// 返回
		return jwt.setKey(keyt.getBytes()).sign();
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

    	// 秘钥不可以为空
    	if(keyt == null) {
    		throw new SaJwtException("请配置 jwt 秘钥");
    	}

    	// 如果token为null 
    	if(token == null) {
    		throw new SaJwtException("jwt 字符串不可为空");
    	}
    	
    	// 解析 
    	JWT jwt = null;
    	try {
    		jwt = JWT.of(token);
		} catch (JWTException e) {
    		throw new SaJwtException("jwt 解析失败：" + token, e).setCode(SaJwtExceptionCode.CODE_40101);
		}
    	JSONObject payloads = jwt.getPayloads();
    	
    	// 校验 Token 签名 
    	boolean verify = jwt.setKey(keyt.getBytes()).verify();
    	if(verify == false) {
    		throw new SaJwtException("jwt 签名无效：" + token).setCode(SaJwtExceptionCode.CODE_40102);
    	};

    	// 校验 loginType 
    	if(Objects.equals(loginType, payloads.getStr(LOGIN_TYPE)) == false) {
    		throw new SaJwtException("jwt loginType 无效：" + token).setCode(SaJwtExceptionCode.CODE_40103);
    	}
    	
    	// 校验 Token 有效期
    	if(isCheckTimeout) {
    		Long effTime = payloads.getLong(EFF, 0L);
        	if(effTime != NEVER_EXPIRE) {
        		if(effTime == null || effTime < System.currentTimeMillis()) {
        			throw new SaJwtException("jwt 已过期：" + token).setCode(SaJwtExceptionCode.CODE_40104);
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
    public static JSONObject getPayloads(String token, String loginType, String keyt) {
    	return parseToken(token, loginType, keyt, true).getPayloads();
    }

    /**
     * 获取 jwt 数据载荷 （校验 sign、loginType，不校验 timeout） 
     * @param token token值
     * @param loginType 登录类型 
     * @param keyt 秘钥 
     * @return 载荷 
     */
    public static JSONObject getPayloadsNotCheck(String token, String loginType, String keyt) {
    	return parseToken(token, loginType, keyt, false).getPayloads();
    }
    
    /**
     * 获取 jwt 代表的账号id 
     * @param token Token值 
     * @param loginType 登录类型 
     * @param keyt 秘钥
     * @return 值 
     */
    public static Object getLoginId(String token, String loginType, String keyt) {
    	return getPayloads(token, loginType, keyt).get(LOGIN_ID);
    }

    /**
     * 获取 jwt 代表的账号id (未登录时返回null)
     * @param token Token值 
     * @param loginType 登录类型 
     * @param keyt 秘钥
     * @return 值 
     */
    public static Object getLoginIdOrNull(String token, String loginType, String keyt) {
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
    public static long getTimeout(String token, String loginType, String keyt) {
    	
    	// 如果token为null 
    	if(token == null) {
    		return NOT_VALUE_EXPIRE;
    	}
    	
    	// 取出数据 
    	JWT jwt = null;
    	try {
    		jwt = JWT.of(token);
		} catch (JWTException e) {
			// 解析失败 
			return NOT_VALUE_EXPIRE;
		}
    	JSONObject payloads = jwt.getPayloads();
    	
    	// 如果签名无效 
    	boolean verify = jwt.setKey(keyt.getBytes()).verify();
    	if(verify == false) {
    		return NOT_VALUE_EXPIRE;
    	};

    	// 如果 loginType  无效 
    	if(Objects.equals(loginType, payloads.getStr(LOGIN_TYPE)) == false) {
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

}

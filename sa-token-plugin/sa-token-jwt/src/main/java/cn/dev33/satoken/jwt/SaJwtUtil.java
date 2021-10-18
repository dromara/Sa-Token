package cn.dev33.satoken.jwt;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTException;

/**
 * jwt操作工具类封装 
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
	 * key：登录设备 
	 */
	public static final String DEVICE = "device"; 
	
	/**
	 * key：有效截止期 (时间戳) 
	 */
	public static final String EFF = "eff"; 

	/** 
	 * 当有效期被设为此值时，代表永不过期 
	 */ 
	public static final long NEVER_EXPIRE = SaTokenDao.NEVER_EXPIRE;
	
	
	/**
	 * 创建 jwt （简单方式）
	 * @param loginId 账号id 
     * @param keyt 秘钥
	 * @return jwt-token 
	 */
    public static String createToken(Object loginId, String keyt) {
    	
    	// 秘钥不可以为空 
    	SaTokenException.throwByNull(keyt, "请配置jwt秘钥");
    	
    	// 构建
    	String token = JWT.create()
			    .setPayload(LOGIN_ID, loginId)
			    // 混入随机字符 
			    .setPayload("rn", SaFoxUtil.getRandomString(32))
			    .setKey(keyt.getBytes())
			    .sign();
    	
    	// 返回 
    	return token;
    }

	/**
	 * 创建 jwt （全参数方式） 
	 * @param loginType 账号类型 
	 * @param loginId 账号id 
	 * @param device 设备标识
	 * @param timeout token有效期 (单位 秒)
     * @param keyt 秘钥
	 * @return jwt-token 
	 */
    public static String createToken(String loginType, Object loginId, String device, long timeout, String keyt) {

    	// 秘钥不可以为空 
    	SaTokenException.throwByNull(keyt, "请配置jwt秘钥");
    	
    	// 计算有效期 
    	long effTime = timeout;
    	if(timeout != NEVER_EXPIRE) {
    		effTime = timeout * 1000 + System.currentTimeMillis();
    	}
    	
    	// 构建
    	String token = JWT.create()
			    .setPayload(LOGIN_TYPE, loginType)
			    .setPayload(LOGIN_ID, loginId)
			    .setPayload(DEVICE, device)
			    .setPayload(EFF, effTime)
			    .setKey(keyt.getBytes())
			    .sign();
    	
    	// 返回 
    	return token;
    }

    /**
     * jwt 解析（校验签名和密码） 
     * @param token Jwt-Token值 
     * @param keyt 秘钥
     * @return 解析后的jwt 对象 
     */
    public static JWT parseToken(String token, String keyt) {

    	// 如果token为null 
    	if(token == null) {
    		throw NotLoginException.newInstance(null, NotLoginException.NOT_TOKEN);
    	}
    	
    	// 解析 
    	JWT jwt = null;
    	try {
    		jwt = JWT.of(token);
		} catch (JWTException e) {
			// 解析失败 
			throw NotLoginException.newInstance(null, NotLoginException.INVALID_TOKEN, token);
		}
    	JSONObject payloads = jwt.getPayloads();
    	
    	// 校验 Token 签名 
    	boolean verify = jwt.setKey(keyt.getBytes()).verify();
    	if(verify == false) {
    		throw NotLoginException.newInstance(payloads.getStr(LOGIN_TYPE), NotLoginException.INVALID_TOKEN, token);
    	};
    	
    	// 校验 Token 有效期
    	Long effTime = payloads.getLong(EFF, 0L);
    	if(effTime != NEVER_EXPIRE) {
    		if(effTime == null || effTime < System.currentTimeMillis()) {
    			throw NotLoginException.newInstance(payloads.getStr(LOGIN_TYPE), NotLoginException.TOKEN_TIMEOUT, token);
    		}
    	}
    	
        // 返回 
        return jwt;
    }

    /**
     * 获取 jwt 数据载荷 （校验签名和密码） 
     * @param token token值
     * @param keyt 秘钥 
     * @return 载荷 
     */
    public static JSONObject getPayloads(String token, String keyt) {
    	return parseToken(token, keyt).getPayloads();
    }

    /**
     * 获取 jwt 数据载荷 （不校验签名和密码） 
     * @param token token值
     * @param keyt 秘钥 
     * @return 载荷 
     */
    public static JSONObject getPayloadsNotCheck(String token, String keyt) {
    	try {
    		JWT jwt = JWT.of(token);
        	JSONObject payloads = jwt.getPayloads();
        	return payloads;
		} catch (JWTException e) {
			return new JSONObject();
		}
    }
    
    /**
     * 获取 jwt 代表的账号id 
     * @param token Token值 
     * @param keyt 秘钥
     * @return 值 
     */
    public static Object getLoginId(String token, String keyt) {
    	return getPayloads(token, keyt).get(LOGIN_ID);
    }

    /**
     * 获取 jwt 代表的账号id (未登录时返回null)
     * @param token Token值 
     * @param keyt 秘钥
     * @return 值 
     */
    public static Object getLoginIdOrNull(String token, String keyt) {
    	try {
    		return getPayloads(token, keyt).get(LOGIN_ID);
		} catch (NotLoginException e) {
			return null;
		}
    }

    /**
     * 获取 jwt 剩余有效期 
     * @param token JwtToken值 
     * @param keyt 秘钥
     * @return 值 
     */
    public static long getTimeout(String token, String keyt) {
    	
    	// 如果token为null 
    	if(token == null) {
    		return SaTokenDao.NOT_VALUE_EXPIRE;
    	}
    	
    	// 取出数据 
    	JWT jwt = null;
    	try {
    		jwt = JWT.of(token);
		} catch (JWTException e) {
			// 解析失败 
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}
    	JSONObject payloads = jwt.getPayloads();
    	
    	// 如果签名无效 
    	boolean verify = jwt.setKey(keyt.getBytes()).verify();
    	if(verify == false) {
    		return SaTokenDao.NOT_VALUE_EXPIRE;
    	};
    	
    	// 如果被设置为：永不过期 
    	Long effTime = payloads.get(EFF, Long.class);
    	if(effTime == NEVER_EXPIRE) {
    		return NEVER_EXPIRE;
    	}
    	// 如果已经超时 
    	if(effTime == null || effTime < System.currentTimeMillis()) {
    		return SaTokenDao.NOT_VALUE_EXPIRE;
    	}
    	
        // 计算timeout (转化为以秒为单位的有效时间)
        return (effTime - System.currentTimeMillis()) / 1000;
    }
    
    
}

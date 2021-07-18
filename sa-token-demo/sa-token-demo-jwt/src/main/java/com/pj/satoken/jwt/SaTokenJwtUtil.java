package com.pj.satoken.jwt;

import java.util.Date;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class SaTokenJwtUtil {

	/**
	 * 秘钥 (随便手打几个字母就好了) 
	 */
	public static final String BASE64_SECURITY = "79e7c69681b8270162386e6daa53d1dd"; 

	/**
	 * token有效期 (单位: 秒)
	 */
	public static final long TIMEOUT = 60 * 60 * 2; 
	
	
	public static final String LOGIN_ID_KEY = "loginId"; 
	
	
	/**
	 * 根据userId生成token 
	 * @param loginId 账号id 
	 * @param base64Security 秘钥
	 * @return jwt-token 
	 */
    public static String createToken(Object loginId) {
    	// 判断，不可使用默认秘钥
//    	if(BASE64_SECURITY.equals("79e7c69681b8270162386e6daa53d1dd")) {
//    		throw new SaTokenException("请更换秘钥");
//    	}
    	// 在这里你可以使用官方提供的claim方法构建载荷，也可以使用setPayload自定义载荷，但是两者不可一起使用
        JwtBuilder builder = Jwts.builder()
        		.setHeaderParam("type", "JWT")
        		.claim(LOGIN_ID_KEY, loginId)
        		.setIssuedAt(new Date())	// 签发日期
        		.setExpiration(new Date(System.currentTimeMillis() + 1000 * TIMEOUT))  // 有效截止日期 
                .signWith(SignatureAlgorithm.HS256, BASE64_SECURITY.getBytes()); // 加密算法 
        //生成JWT
        return builder.compact();
    }

    /**
     * 从一个jwt里面解析出Claims
     * @param tokenValue token值 
     * @param base64Security 秘钥
     * @return Claims对象 
     */
    public static Claims getClaims(String tokenValue) {
//    	System.out.println(tokenValue);
        Claims claims = Jwts.parser()
                .setSigningKey(BASE64_SECURITY.getBytes())
                .parseClaimsJws(tokenValue).getBody();
        return claims;
    }

    /**
     * 从一个jwt里面解析loginId
     * @param tokenValue token值 
     * @param base64Security 秘钥
     * @return loginId 
     */
    public static String getLoginId(String tokenValue) {
    	try {
        	Object loginId = getClaims(tokenValue).get(LOGIN_ID_KEY);
        	if(loginId == null) {
        		return null;
        	}
        	return String.valueOf(loginId);
        } catch (ExpiredJwtException e) {
//        	throw NotLoginException.newInstance(StpUtil.TYPE, NotLoginException.TOKEN_TIMEOUT);
        	return NotLoginException.TOKEN_TIMEOUT;
		} catch (MalformedJwtException e) {
        	throw NotLoginException.newInstance(StpUtil.stpLogic.loginType, NotLoginException.INVALID_TOKEN);
		} catch (Exception e) {
        	throw new SaTokenException(e);
        }
    }
    
	
    
    static {
    	
    	// 判断秘钥
    	if(BASE64_SECURITY.equals("79e7c69681b8270162386e6daa53d1dd")) {
    		String warn = "-------------------------------------\n";
    		warn += "请更换JWT秘钥，不要使用示例默认秘钥\n";
    		warn += "-------------------------------------";
    		System.err.println(warn);
    	}

    	// 提前调用一下方法，促使其属性初始化 
    	StpUtil.getLoginType();	
    	
    	// 修改默认实现 
    	StpUtil.stpLogic = new StpLogic("login") {
    		
    		// 重写 (随机生成一个tokenValue)
    		@Override
    		public String createTokenValue(Object loginId) {
    			return SaTokenJwtUtil.createToken(loginId);
    		}
    		
    		// 重写 (在当前会话上登录id )
    		@Override
    		public void login(Object loginId, SaLoginModel loginModel) {
    			// ------ 1、获取相应对象  
    			SaStorage storage = SaManager.getSaTokenContext().getStorage();
    			SaTokenConfig config = getConfig();
    			// ------ 2、生成一个token 
    			String tokenValue = createTokenValue(loginId);
    			storage.set(splicingKeyJustCreatedSave(), tokenValue);	// 将token保存到本次request里  
    			if(config.getIsReadCookie() == true){	// cookie注入 
    				SaManager.getSaTokenContext().getResponse().addCookie(getTokenName(), tokenValue, "/", config.getCookieDomain(), (int)config.getTimeout());		
    			}
    		}
    		
    		// 重写 (获取指定token对应的登录id)
    		@Override
    		public String getLoginIdNotHandle(String tokenValue) {
    	 		try {
    	 			return SaTokenJwtUtil.getLoginId(tokenValue);
				} catch (Exception e) {
					return null;
				}
    		}
    		
    		// 重写 (当前会话注销登录)
    		@Override
    		public void logout() {
    			// 如果连token都没有，那么无需执行任何操作 
    			String tokenValue = getTokenValue();
    	 		if(tokenValue == null) {
    	 			return;
    	 		}
    	 		// 如果打开了cookie模式，把cookie清除掉 
    	 		if(getConfig().getIsReadCookie() == true){
    				SaManager.getSaTokenContext().getResponse().deleteCookie(getTokenName()); 	
    			}
    		}
    		
    		// 重写 (获取指定key的session)
    		@Override
    		public SaSession getSessionBySessionId(String sessionId, boolean isCreate) {
    			throw new SaTokenException("jwt has not session");
    		}
    		
    		// 重写 (获取当前登录者的token剩余有效时间 (单位: 秒))
    		@Override
    		public long getTokenTimeout() {
				// 如果没有token 
    			String tokenValue = getTokenValue();
    			if(tokenValue == null) {
    				return SaTokenDao.NOT_VALUE_EXPIRE;
    			}
    			// 开始取值 
    			Claims claims = null;
    			try {
    				claims = SaTokenJwtUtil.getClaims(tokenValue);
				} catch (Exception e) {
					return SaTokenDao.NOT_VALUE_EXPIRE;
				}
    			if(claims == null) {
    				return SaTokenDao.NOT_VALUE_EXPIRE;
    			}
    			Date expiration = claims.getExpiration();
    			if(expiration == null) {
    				return SaTokenDao.NOT_VALUE_EXPIRE;
    			}
    			return (expiration.getTime() - System.currentTimeMillis()) / 1000;
    		}
    		
    		// 重写 (返回当前token的登录设备) 
    		@Override
    		public String getLoginDevice() {
    			return SaTokenConsts.DEFAULT_LOGIN_DEVICE;
    		}
    		
    		// 重写 (获取当前会话的token信息)
    		@Override
    		public SaTokenInfo getTokenInfo() {
    			SaTokenInfo info = new SaTokenInfo();
    			info.tokenName = getTokenName();
    			info.tokenValue = getTokenValue();
    			info.isLogin = isLogin();
    			info.loginId = getLoginIdDefaultNull();
    			info.loginType = getLoginType();
    			info.tokenTimeout = getTokenTimeout();
//    			info.sessionTimeout = getSessionTimeout();
//    			info.tokenSessionTimeout = getTokenSessionTimeout();
//    			info.tokenActivityTimeout = getTokenActivityTimeout();
    			info.loginDevice = getLoginDevice();
    			return info;
    		}
    		
    		
    	};
    	
    }
	
    
    
    
    
    
    
    
    
    
    
    
	
}

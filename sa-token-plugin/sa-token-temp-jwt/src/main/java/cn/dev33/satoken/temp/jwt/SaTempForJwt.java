package cn.dev33.satoken.temp.jwt;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.temp.SaTempInterface;
import cn.dev33.satoken.temp.jwt.error.SaTempJwtErrorCode;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token 临时令牌验证模块接口 JWT实现类 
 * @author kong
 *
 */
public class SaTempForJwt implements SaTempInterface {
	
	/**
	 * 根据value创建一个token 
	 */
	@Override
	public String createToken(String service, Object value, long timeout) {
		String token = SaJwtUtil.createToken(service, value, timeout, getJwtSecretKey());
		return token;
	}
	
	/**
	 *  解析token获取value 
	 */
	@Override
	public Object parseToken(String service, String token) {
		Object value = SaJwtUtil.getValue(service, token, getJwtSecretKey());
		return value;
	}
	
	/**
	 * 返回指定token的剩余有效期，单位：秒 
	 */
	@Override
	public long getTimeout(String service, String token) {
		long timeout = SaJwtUtil.getTimeout(service, token, getJwtSecretKey());
		return timeout;
	}

	/**
	 * 删除一个token
	 */
	@Override
	public void deleteToken(String service, String token) {
		throw new ApiDisabledException("jwt cannot delete token").setCode(SaTempJwtErrorCode.CODE_30302);
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

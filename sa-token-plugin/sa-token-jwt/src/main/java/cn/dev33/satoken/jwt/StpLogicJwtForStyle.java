package cn.dev33.satoken.jwt;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

/**
 * Sa-Token 整合 jwt -- Style模式 
 * @author kong
 *
 */
public class StpLogicJwtForStyle extends StpLogic {

	/**
	 * Sa-Token 整合 jwt -- Style模式 
	 */
	public StpLogicJwtForStyle() {
		super(StpUtil.TYPE);
	}

	/**
	 * Sa-Token 整合 jwt -- Style模式 
	 * @param loginType 账号体系标识 
	 */
	public StpLogicJwtForStyle(String loginType) {
		super(loginType);
	}

	/**
	 * 获取jwt秘钥 
	 * @return / 
	 */
	public String jwtSecretKey() {
		return getConfig().getJwtSecretKey();
	}
	
	// ------ 重写方法 
	
	/**
	 * 创建一个TokenValue
	 * @param loginId loginId
	 * @return 生成的tokenValue 
	 */
	@Override
 	public String createTokenValue(Object loginId, String device, long timeout) {
 		return SaJwtUtil.createToken(loginId, jwtSecretKey());
	}
 	
}

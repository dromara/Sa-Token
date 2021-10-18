package cn.dev33.satoken.jwt;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

/**
 * Sa-Token 整合 jwt -- Token风格 
 * @author kong
 *
 */
public class StpLogicJwtForTokenStyle extends StpLogic {

	/**
	 * 初始化StpLogic, 并指定账号类型 
	 * @param loginType 账号体系标识 
	 */
	public StpLogicJwtForTokenStyle() {
		super(StpUtil.TYPE);
	}

	/**
	 * 初始化StpLogic, 并指定账号类型 
	 * @param loginType 账号体系标识 
	 */
	public StpLogicJwtForTokenStyle(String loginType) {
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
 	public String createTokenValue(Object loginId) {
 		return SaJwtUtil.createToken(loginId, jwtSecretKey());
	}
 	
}

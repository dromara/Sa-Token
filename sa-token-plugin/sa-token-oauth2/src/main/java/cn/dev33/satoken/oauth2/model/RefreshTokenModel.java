package cn.dev33.satoken.oauth2.model;

import java.io.Serializable;

/**
 * Model: Refresh-Token 
 * @author kong
 *
 */
public class RefreshTokenModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;
 
	/**
	 * Refresh-Token 值
	 */
	public String refreshToken;

	/**
	 * Refresh-Token 到期时间 
	 */
	public long expiresTime;
	
	/**
	 * 应用id 
	 */
	public String clientId;
	
	/**
	 * 授权范围
	 */
	public String scope;

	/**
	 * 对应账号id 
	 */
	public Object loginId;

	/**
	 * 对应账号id 
	 */
	public String openid;

	@Override
	public String toString() {
		return "RefreshTokenModel [refreshToken=" + refreshToken + ", expiresTime=" + expiresTime
				+ ", clientId=" + clientId + ", scope=" + scope + ", loginId=" + loginId + ", openid=" + openid + "]";
	}

	/**
	 * 获取：此 Refresh-Token 的剩余有效期（秒）
	 * @return see note 
	 */
	public long getExpiresIn() {
		long s = (expiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}
	
}

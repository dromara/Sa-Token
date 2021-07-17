package cn.dev33.satoken.oauth2.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Model: Access-Token
 * @author kong
 *
 */
public class AccessTokenModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * Access-Token 值
	 */
	public String accessToken;
	
	/**
	 * Refresh-Token 值
	 */
	public String refreshToken;
	
	/**
	 * Access-Token 到期时间 
	 */
	public long expiresTime;

	/**
	 * Refresh-Token 到期时间   
	 */
	public long refreshExpiresTime;

	/**
	 * 应用id 
	 */
	public String clientId;

	/**
	 * 账号id 
	 */
	public Object loginId;
	
	/**
	 * 开放账号id 
	 */
	public String openid;

	/**
	 * 授权范围
	 */
	public String scope;  

	public AccessTokenModel() {}
	/**
	 * 构建一个 
	 * @param accessToken accessToken
	 * @param clientId 应用id 
	 * @param scope 请求授权范围 
	 * @param loginId 对应的账号id 
	 */
	public AccessTokenModel(String accessToken, String clientId, Object loginId, String scope) {
		super();
		this.accessToken = accessToken;
		this.clientId = clientId;
		this.loginId = loginId;
		this.scope = scope;
	}
	
	@Override
	public String toString() {
		return "AccessTokenModel [accessToken=" + accessToken + ", refreshToken=" + refreshToken
				+ ", accessTokenTimeout=" + expiresTime + ", refreshTokenTimeout=" + refreshExpiresTime
				+ ", clientId=" + clientId + ", scope=" + scope + ", openid=" + openid + "]";
	}


	/**
	 * 获取：此 Access-Token 的剩余有效期（秒）
	 * @return see note 
	 */
	public long getExpiresIn() {
		long s = (expiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}

	/**
	 * 获取：此 Refresh-Token 的剩余有效期（秒）
	 * @return see note 
	 */
	public long getRefreshExpiresIn() {
		long s = (refreshExpiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}
	
	/**
	 * 将所有属性转换为下划线形式的Map 
	 * @return
	 */
	public Map<String, Object> toLineMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("access_token", accessToken);
		map.put("refresh_token", refreshToken);
		map.put("expires_in", getExpiresIn());
		map.put("refresh_expires_in", getRefreshExpiresIn());
		map.put("client_id", clientId);
		map.put("scope", scope);
		map.put("openid", openid);
		return map;
	}
	
}

package cn.dev33.satoken.oauth2.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Model: client_token
 * @author kong
 *
 */
public class ClientTokenModel {

	/**
	 * client_token 值
	 */
	public String clientToken;
	
	/**
	 * client_token 到期时间 
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

	public ClientTokenModel() {}
	
	/**
	 * 构建一个 
	 * @param accessToken accessToken
	 * @param clientId 应用id 
	 * @param scope 请求授权范围 
	 * @param loginId 对应的账号id 
	 */
	public ClientTokenModel(String accessToken, String clientId, String scope) {
		super();
		this.clientToken = accessToken;
		this.clientId = clientId;
		this.scope = scope;
	}
	
	@Override
	public String toString() {
		return "ClientTokenModel [clientToken=" + clientToken + ", expiresTime=" + expiresTime + ", clientId="
				+ clientId + ", scope=" + scope + "]";
	}
	
	/**
	 * 获取：此 Client-Token 的剩余有效期（秒）
	 * @return see note 
	 */
	public long getExpiresIn() {
		long s = (expiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}
	
	/**
	 * 将所有属性转换为下划线形式的Map 
	 * @return
	 */
	public Map<String, Object> toLineMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("client_token", clientToken);
		map.put("expires_in", getExpiresIn());
		map.put("client_id", clientId);
		map.put("scope", scope);	
		return map;
	}
	
	
}

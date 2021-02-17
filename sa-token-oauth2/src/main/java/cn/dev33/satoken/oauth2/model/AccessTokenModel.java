package cn.dev33.satoken.oauth2.model;

/**
 * Model: access_token
 * @author kong
 *
 */
public class AccessTokenModel {

	/**
	 * access_token 值
	 */
	private String accessToken;
	
	/**
	 * refresh_token 值
	 */
	private String refreshToken;
	
	/**
	 * access_token 剩余有效时间 (秒) 
	 */
	private long expiresIn;

	/**
	 * refresh_token 剩余有效期 (秒)  
	 */
	private long refreshExpiresIn;

	/**
	 * 此 access_token令牌 是由哪个code码创建 
	 */
	private String code;
	
	/**
	 * 应用id 
	 */
	private String clientId;
	
	/**
	 * 授权范围
	 */
	private String scope;

	/**
	 * 开放账号id 
	 */
	private String openid;

	/**
	 * 其他自定义数据 
	 */
	private Object tag;

	
	/**
	 * @return accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken 要设置的 accessToken
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return refreshToken
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * @param refreshToken 要设置的 refreshToken
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * @return expiresIn
	 */
	public long getExpiresIn() {
		return expiresIn;
	}

	/**
	 * @param expiresIn 要设置的 expiresIn
	 */
	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}

	/**
	 * @return refreshExpiresIn
	 */
	public long getRefreshExpiresIn() {
		return refreshExpiresIn;
	}

	/**
	 * @param refreshExpiresIn 要设置的 refreshExpiresIn
	 */
	public void setRefreshExpiresIn(long refreshExpiresIn) {
		this.refreshExpiresIn = refreshExpiresIn;
	}

	/**
	 * @return code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code 要设置的 code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId 要设置的 clientId
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param scope 要设置的 scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @return openid
	 */
	public String getOpenid() {
		return openid;
	}

	/**
	 * @param openid 要设置的 openid
	 */
	public void setOpenid(String openid) {
		this.openid = openid;
	}

	/**
	 * @return tag
	 */
	public Object getTag() {
		return tag;
	}

	/**
	 * @param tag 要设置的 tag
	 */
	public void setTag(Object tag) {
		this.tag = tag;
	}

	
	
	
	
}

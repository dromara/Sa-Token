package cn.dev33.satoken.oauth2.config;

/**
 * sa-token oauth2 配置类 Model
 * @author kong
 *
 */
public class SaOAuth2Config {

	/**
	 * 授权码默认保存的时间(单位秒) 默认五分钟 
	 */
	private long codeTimeout = 60 * 5;

	/**
	 * access_token默认保存的时间(单位秒) 默认两个小时 
	 */
	private long accessTokenTimeout = 60 * 60 * 2;

	/**
	 * refresh_token默认保存的时间(单位秒) 默认30 天 
	 */
	private long refreshTokenTimeout = 60 * 60 * 24 * 30;

	
	/**
	 * @return codeTimeout
	 */
	public long getCodeTimeout() {
		return codeTimeout;
	}

	/**
	 * @param codeTimeout 要设置的 codeTimeout
	 * @return 对象自身
	 */
	public SaOAuth2Config setCodeTimeout(long codeTimeout) {
		this.codeTimeout = codeTimeout;
		return this;
	}

	/**
	 * @return accessTokenTimeout
	 */
	public long getAccessTokenTimeout() {
		return accessTokenTimeout;
	}

	/**
	 * @param accessTokenTimeout 要设置的 accessTokenTimeout
	 * @return 对象自身
	 */
	public SaOAuth2Config setAccessTokenTimeout(long accessTokenTimeout) {
		this.accessTokenTimeout = accessTokenTimeout;
		return this;
	}

	/**
	 * @return refreshTokenTimeout
	 */
	public long getRefreshTokenTimeout() {
		return refreshTokenTimeout;
	}

	/**
	 * @param refreshTokenTimeout 要设置的 refreshTokenTimeout
	 * @return 对象自身
	 */
	public SaOAuth2Config setRefreshTokenTimeout(long refreshTokenTimeout) {
		this.refreshTokenTimeout = refreshTokenTimeout;
		return this;
	}

	
	
	
	
	
}

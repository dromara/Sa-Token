package cn.dev33.satoken.config;

/**
 * sa-token 总配置类  
 * @author kong
 *
 */
public class SaTokenConfig {

	private String tokenName = "satoken";		// token名称 (同时也是cookie名称)
	private long timeout = 30 * 24 * 60 * 60;	// token有效期，单位s 默认30天
	private long activityTimeout = -1;			// token临时有效期 (指定时间内无操作就视为token过期) 单位: 秒, 默认-1 代表不限制 (例如可以设置为1800代表30分钟内无操作就过期) 
	private Boolean isShare = true;			// 在多人登录同一账号时，是否共享会话 (为true时共用一个，为false时新登录挤掉旧登录)
	private Boolean isReadBody = true;			// 是否尝试从请求体里读取token
	private Boolean isReadHead = true;			// 是否尝试从header里读取token
	private Boolean isReadCookie = true;		// 是否尝试从cookie里读取token
	private String tokenStyle = "uuid";				// token风格 
	
	private Boolean isV = true;	 // 是否在初始化配置时打印版本字符画

	
	
	
	/**
	 * @return tokenName
	 */
	public String getTokenName() {
		return tokenName;
	}

	/**
	 * @param tokenName 要设置的 tokenName
	 */
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	/**
	 * @return timeout
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout 要设置的 timeout
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * @return activityTimeout
	 */
	public long getActivityTimeout() {
		return activityTimeout;
	}

	/**
	 * @param activityTimeout 要设置的 activityTimeout
	 */
	public void setActivityTimeout(long activityTimeout) {
		this.activityTimeout = activityTimeout;
	}
	
	/**
	 * @return isShare
	 */
	public Boolean getIsShare() {
		return isShare;
	}

	/**
	 * @param isShare 要设置的 isShare
	 */
	public void setIsShare(Boolean isShare) {
		this.isShare = isShare;
	}

	/**
	 * @return isReadCookie
	 */
	public Boolean getIsReadCookie() {
		return isReadCookie;
	}

	/**
	 * @param isReadCookie 要设置的 isReadCookie
	 */
	public void setIsReadCookie(Boolean isReadCookie) {
		this.isReadCookie = isReadCookie;
	}
	
	/**
	 * @return isReadHead
	 */
	public Boolean getIsReadHead() {
		return isReadHead;
	}

	/**
	 * @param isReadHead 要设置的 isReadHead
	 */
	public void setIsReadHead(Boolean isReadHead) {
		this.isReadHead = isReadHead;
	}

	/**
	 * @return isReadBody
	 */
	public Boolean getIsReadBody() {
		return isReadBody;
	}

	/**
	 * @param isReadBody 要设置的 isReadBody
	 */
	public void setIsReadBody(Boolean isReadBody) {
		this.isReadBody = isReadBody;
	}
	
	/**
	 * @return tokenStyle
	 */
	public String getTokenStyle() {
		return tokenStyle;
	}

	/**
	 * @param tokenStyle 要设置的 tokenStyle
	 */
	public void setTokenStyle(String tokenStyle) {
		this.tokenStyle = tokenStyle;
	}
	
	/**
	 * @return isV
	 */
	public Boolean getIsV() {
		return isV;
	}

	/**
	 * @param isV 要设置的 isV
	 */
	public void setIsV(Boolean isV) {
		this.isV = isV;
	}

	
	
	
	@Override
	public String toString() {
		return "SaTokenConfig [tokenName=" + tokenName + ", timeout=" + timeout + ", activityTimeout=" + activityTimeout
				+ ", isShare=" + isShare + ", isReadBody=" + isReadBody + ", isReadHead=" + isReadHead
				+ ", isReadCookie=" + isReadCookie + ", tokenStyle=" + tokenStyle + ", isV=" + isV + "]";
	}

	
	
	

	
	
	

	
	
	
	
	
	
	
	
}

package cn.dev33.satoken.config;

/**
 * sa-token 总配置类  
 */
public class SaTokenConfig {

	private String tokenName = "satoken";		// token名称（同时也是cookie名称）
	private long timeout = 30 * 24 * 60 * 60;		// token有效期，单位s 默认30天，-1为永不过期   
	private Boolean isShare = true;			// 在多人登录同一账号时，是否共享会话（为true时共用一个，为false时新登录挤掉旧登录）
	private Boolean isReadHead = true;		// 是否在cookie读取不到token时，继续从请求header里继续尝试读取 
	private Boolean isReadBody = true;		// 是否在header读取不到token时，继续从请求题参数里继续尝试读取 
	
	private Boolean isV = true;	 // 是否在初始化配置时打印版本字符画
	
	
	public String getTokenName() {
		return tokenName;
	}
	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}
	public long getTimeout() {
		return timeout;
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	public Boolean getIsShare() {
		return isShare;
	}
	public void setIsShare(Boolean isShare) {
		this.isShare = isShare;
	}
	public Boolean getIsReadHead() {
		return isReadHead;
	}
	public void setIsReadHead(Boolean isReadHead) {
		this.isReadHead = isReadHead;
	}
	public Boolean getIsReadBody() {
		return isReadBody;
	}
	public void setIsReadBody(Boolean isReadBody) {
		this.isReadBody = isReadBody;
	}
	public Boolean getIsV() {
		return isV;
	}
	public void setIsV(Boolean isV) {
		this.isV = isV;
	}
	
	
	@Override
	public String toString() {
		return "SaTokenConfig [tokenName=" + tokenName + ", timeout=" + timeout + ", isShare=" + isShare
				+ ", isReadHead=" + isReadHead + ", isReadBody=" + isReadBody + ", isV=" + isV + "]";
	}
	
	
	
	
	
}

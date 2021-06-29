package cn.dev33.satoken.config;

import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token-SSO 单点登录模块 配置Model
 * @author kong
 *
 */
public class SaSsoConfig {

	/**
	 * Ticket有效期 (单位: 秒)
	 */
	public long ticketTimeout = 60 * 5;
	
	/**
	 * 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) 
	 */
	public String allowUrl = "*";
	
	/**
	 * 调用秘钥 
	 */
	public String secretkey;
	
	/**
	 * SSO-Server端 单点登录地址
	 */
	public String authUrl;

	/**
	 * SSO-Server端 Ticket校验地址 [模式三专用配置]
	 */
	public String checkTicketUrl;

	/**
	 * SSO-Server端 单点注销地址 [模式三专用配置]
	 */
	public String sloUrl;



	/**
	 * @return Ticket有效期 (单位: 秒) 
	 */
	public long getTicketTimeout() {
		return ticketTimeout;
	}

	/**
	 * @param ticketTimeout Ticket有效期 (单位: 秒) 
	 * @return 对象自身
	 */
	public SaSsoConfig setTicketTimeout(long ticketTimeout) {
		this.ticketTimeout = ticketTimeout;
		return this;
	}

	/**
	 * @return 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) 
	 */
	public String getAllowUrl() {
		return allowUrl;
	}

	/**
	 * @param allowUrl 所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) 
	 * @return 对象自身
	 */
	public SaSsoConfig setAllowUrl(String allowUrl) {
		this.allowUrl = allowUrl;
		return this;
	}

	/**
	 * @return 调用秘钥
	 */
	public String getSecretkey() {
		return secretkey;
	}

	/**
	 * @param secretkey 调用秘钥 
	 * @return 对象自身
	 */
	public SaSsoConfig setSecretkey(String secretkey) {
		this.secretkey = secretkey;
		return this;
	}

	/**
	 * @return SSO-Server端 单点登录地址
	 */
	public String getAuthUrl() {
		return authUrl;
	}

	/**
	 * @param authUrl SSO-Server端 单点登录地址
	 */
	public void setAuthUrl(String authUrl) {
		this.authUrl = authUrl;
	}

	/**
	 * @return SSO-Server端Ticket校验地址 
	 */
	public String getCheckTicketUrl() {
		return checkTicketUrl;
	}

	/**
	 * @param checkTicketUrl SSO-Server端Ticket校验地址
	 */
	public void setCheckTicketUrl(String checkTicketUrl) {
		this.checkTicketUrl = checkTicketUrl;
	}

	/**
	 * @return SSO-Server端单点注销地址
	 */
	public String getSloUrl() {
		return sloUrl;
	}

	/**
	 * @param sloUrl SSO-Server端单点注销地址
	 */
	public void setSloUrl(String sloUrl) {
		this.sloUrl = sloUrl;
	}

	@Override
	public String toString() {
		return "SaSsoConfig [ticketTimeout=" + ticketTimeout + ", allowUrl=" + allowUrl + ", secretkey=" + secretkey
				+ ", authUrl=" + authUrl + ", checkTicketUrl=" + checkTicketUrl + ", sloUrl=" + sloUrl + "]";
	}
	

	/**
	 * 以数组形式写入允许的授权回调地址 
	 * @param url 所有集合 
	 * @return 对象自身
	 */
	public SaSsoConfig setAllow(String ...url) {
		this.allowUrl = SaFoxUtil.arrayJoin(url);
		return this;
	}
	
}

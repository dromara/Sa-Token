package cn.dev33.satoken.oauth2.model;

import java.io.Serializable;

/**
 * Client应用信息 Model 
 * @author kong
 *
 */
public class SaClientModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * 应用id 
	 */
	public String clientId;
	
	/**
	 * 应用秘钥 
	 */
	public String clientSecret;

	/**
	 * 应用签约的所有权限, 多个用逗号隔开 
	 */
	public String contractScope;
	
	/**
	 * 应用允许授权的所有URL, 多个用逗号隔开 
	 */
	public String allowUrl;
	
	/** 此 Client 是否打开模式：授权码（Authorization Code） */
	public Boolean isCode = false;

	/** 此 Client 是否打开模式：隐藏式（Implicit） */
	public Boolean isImplicit = false;

	/** 此 Client 是否打开模式：密码式（Password） */
	public Boolean isPassword = false;

	/** 此 Client 是否打开模式：凭证式（Client Credentials） */
	public Boolean isClient = false;

	/** 是否自动判断开放的授权模式，此值为true时单独设置（isCode、isImplicit、isPassword、isClient）不再有效，而是跟随全局设置 */
	public Boolean isAutoMode = false;

	public SaClientModel() {
		
	}
	public SaClientModel(String clientId, String clientSecret, String contractScope, String allowUrl) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.contractScope = contractScope;
		this.allowUrl = allowUrl;
	}

	/**
	 * @return 应用id
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId 应用id 
	 * @return 对象自身 
	 */
	public SaClientModel setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	/**
	 * @return 应用秘钥 
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * @param clientSecret 应用秘钥 
	 * @return 对象自身 
	 */
	public SaClientModel setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
		return this;
	}

	/**
	 * @return 应用签约的所有权限, 多个用逗号隔开
	 */
	public String getContractScope() {
		return contractScope;
	}

	/**
	 * @param contractScope 应用签约的所有权限, 多个用逗号隔开
	 * @return 对象自身 
	 */
	public SaClientModel setContractScope(String contractScope) {
		this.contractScope = contractScope;
		return this;
	}

	/**
	 * @return 应用允许授权的所有URL, 多个用逗号隔开
	 */
	public String getAllowUrl() {
		return allowUrl;
	}

	/**
	 * @param allowUrl 应用允许授权的所有URL, 多个用逗号隔开
	 * @return 对象自身 
	 */
	public SaClientModel setAllowUrl(String allowUrl) {
		this.allowUrl = allowUrl;
		return this;
	}

	/**
	 * @return 此 Client 是否打开模式：授权码（Authorization Code）
	 */
	public Boolean getIsCode() {
		return isCode;
	}
	
	/**
	 * @param isCode 此 Client 是否打开模式：授权码（Authorization Code）
	 * @return 对象自身 
	 */
	public SaClientModel setIsCode(Boolean isCode) {
		this.isCode = isCode;
		return this;
	}
	
	/**
	 * @return 此 Client 是否打开模式：隐藏式（Implicit）
	 */
	public Boolean getIsImplicit() {
		return isImplicit;
	}
	
	/**
	 * @param isImplicit 此 Client 是否打开模式：隐藏式（Implicit）
	 * @return 对象自身 
	 */
	public SaClientModel setIsImplicit(Boolean isImplicit) {
		this.isImplicit = isImplicit;
		return this;
	}
	
	/**
	 * @return 此 Client 是否打开模式：密码式（Password）
	 */
	public Boolean getIsPassword() {
		return isPassword;
	}
	
	/**
	 * @param isPassword 此 Client 是否打开模式：密码式（Password）
	 * @return 对象自身 
	 */
	public SaClientModel setIsPassword(Boolean isPassword) {
		this.isPassword = isPassword;
		return this;
	}
	
	/**
	 * @return 此 Client 是否打开模式：凭证式（Client Credentials）
	 * @return 对象自身 
	 */
	public Boolean getIsClient() {
		return isClient;
	}
	
	/**
	 * @param isClient 此 Client 是否打开模式：凭证式（Client Credentials）
	 * @return 对象自身 
	 */
	public SaClientModel setIsClient(Boolean isClient) {
		this.isClient = isClient;
		return this;
	}

	/**
	 * @return 是否自动判断开放的授权模式
	 */
	public Boolean getIsAutoMode() {
		return isAutoMode;
	}
	
	/**
	 * @param isAutoMode 是否自动判断开放的授权模式
	 * @return 对象自身 
	 */
	public SaClientModel setIsAutoMode(Boolean isAutoMode) {
		this.isAutoMode = isAutoMode;
		return this;
	}
	
	// 
	@Override
	public String toString() {
		return "SaClientModel [clientId=" + clientId + ", clientSecret=" + clientSecret + ", contractScope="
				+ contractScope + ", allowUrl=" + allowUrl + ", isCode=" + isCode + ", isImplicit=" + isImplicit
				+ ", isPassword=" + isPassword + ", isClient=" + isClient + ", isAutoMode=" + isAutoMode + "]";
	}
	
}

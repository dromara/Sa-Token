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

	/**
	 * 应用允许授权的所有URL, 多个用逗号隔开
	 */
	public String allowType;

	public SaClientModel() {
		
	}
	public SaClientModel(String clientId, String clientSecret, String contractScope, String allowUrl,String allowType) {
		super();
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.contractScope = contractScope;
		this.allowUrl = allowUrl;
		this.allowType = allowType;
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
	 * @return 应用允许的授权模式, 多个用逗号隔开
	 */
	public String getAllowType() {
		return allowType;
	}

	/**
	 * @param allowType 应用允许的授权模式, 多个用逗号隔开
	 * @return 对象自身
	 */
	public SaClientModel setAllowType(String allowType) {
		this.allowType = allowType;
		return this;
	}
	
	@Override
	public String toString() {
		return "SaClientModel [clientId=" + clientId + ", clientSecret=" + clientSecret + ", contractScope="
				+ contractScope + ", allowUrl=" + allowUrl + "]";
	}
	
}

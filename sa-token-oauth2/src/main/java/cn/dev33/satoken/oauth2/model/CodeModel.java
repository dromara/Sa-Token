package cn.dev33.satoken.oauth2.model;

/**
 * Model: [授权码 - 数据 对应关系] 
 * @author kong
 *
 */
public class CodeModel {

	/**
	 * 授权码 
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
	 * 对应账号id 
	 */
	private Object loginId;

	/**
	 * 用户是否已经确认了这个授权 
	 */
	private Boolean isConfirm;

	/**
	 * 确认授权后重定向的地址 
	 */
	private String redirectUri;

	/**
	 * 拒绝授权后重定向的地址 
	 */
	private String rejectUri;


	/**
	 * 其他自定义数据 
	 */
	private Object tag;
	

	/**
	 * 构建一个 
	 */
	public CodeModel() {
		
	}
	/**
	 * 构建一个 
	 * @param code 授权码 
	 * @param clientId 应用id 
	 * @param scope 请求授权范围 
	 * @param loginId 对应的账号id 
	 */
	public CodeModel(String code, String clientId, String scope, Object loginId) {
		super();
		this.code = code;
		this.clientId = clientId;
		this.scope = scope;
		this.loginId = loginId;
		this.isConfirm = false;
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
	 * @return loginId
	 */
	public Object getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId 要设置的 loginId
	 */
	public void setLoginId(Object loginId) {
		this.loginId = loginId;
	}

	/**
	 * @return isConfirm
	 */
	public Boolean getIsConfirm() {
		return isConfirm;
	}

	/**
	 * @param isConfirm 要设置的 isConfirm
	 */
	public void setIsConfirm(Boolean isConfirm) {
		this.isConfirm = isConfirm;
	}

	/**
	 * @return redirectUri
	 */
	public String getRedirectUri() {
		return redirectUri;
	}
	
	/**
	 * @param redirectUri 要设置的 redirectUri
	 */
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	/**
	 * @return rejectUri
	 */
	public String getRejectUri() {
		return rejectUri;
	}
	/**
	 * @param rejectUri 要设置的 rejectUri
	 */
	public void setRejectUri(String rejectUri) {
		this.rejectUri = rejectUri;
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

package cn.dev33.satoken.oauth2.model;

import java.io.Serializable;

import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 请求授权参数的Model
 * @author kong
 *
 */
public class RequestAuthModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * 应用id 
	 */
	public String clientId;
	 
	/**
	 * 授权范围
	 */
	public String scope;
	
	/**
	 * 对应的账号id 
	 */
	public Object loginId;
	
	/**
	 * 待重定向URL
	 */
	public String redirectUri; 
	
	/**
	 * 授权类型, 非必填 
	 */
	public String responseType;

	/**
	 * 状态标识, 可为null 
	 */
	public String state;

	
	/**
	 * @return clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId 要设置的 clientId
	 * @return 对象自身
	 */
	public RequestAuthModel setClientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	/**
	 * @return scope
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param scope 要设置的 scope
	 * @return 对象自身
	 */
	public RequestAuthModel setScope(String scope) {
		this.scope = scope;
		return this;
	}

	/**
	 * @return loginId
	 */
	public Object getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId 要设置的 loginId
	 * @return 对象自身
	 */
	public RequestAuthModel setLoginId(Object loginId) {
		this.loginId = loginId;
		return this;
	}

	/**
	 * @return redirectUri
	 */
	public String getRedirectUri() {
		return redirectUri;
	}

	/**
	 * @param redirectUri 要设置的 redirectUri
	 * @return 对象自身
	 */
	public RequestAuthModel setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
		return this;
	}

	/**
	 * @return responseType
	 */
	public String getResponseType() {
		return responseType;
	}

	/**
	 * @param responseType 要设置的 responseType
	 * @return 对象自身
	 */
	public RequestAuthModel setResponseType(String responseType) {
		this.responseType = responseType;
		return this;
	}
	
	/**
	 * @return state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state 要设置的 state
	 * @return 对象自身
	 */
	public RequestAuthModel setState(String state) {
		this.state = state;
		return this;
	}
	
	/**
	 * 检查此Model参数是否有效  
	 * @return 对象自身
	 */
	public RequestAuthModel checkModel() {
		if(SaFoxUtil.isEmpty(clientId)) {
			throw new SaOAuth2Exception("client_id 不可为空").setCode(SaOAuth2ErrorCode.CODE_30101);
		}
		if(SaFoxUtil.isEmpty(scope)) {
			throw new SaOAuth2Exception("scope 不可为空").setCode(SaOAuth2ErrorCode.CODE_30102);
		}
		if(SaFoxUtil.isEmpty(redirectUri)) {
			throw new SaOAuth2Exception("redirect_uri 不可为空").setCode(SaOAuth2ErrorCode.CODE_30103);
		}
		if(SaFoxUtil.isEmpty(String.valueOf(loginId))) {
			throw new SaOAuth2Exception("LoginId 不可为空").setCode(SaOAuth2ErrorCode.CODE_30104);
		}
		return this;
	}

	
}

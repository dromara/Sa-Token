package cn.dev33.satoken.oauth2.config;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.util.SaResult;

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
	 * client_token默认保存的时间(单位秒) 默认两个小时 
	 */
	private long clientTokenTimeout = 60 * 60 * 2;

	
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

	/**
	 * @return clientTokenTimeout
	 */
	public long getClientTokenTimeout() {
		return clientTokenTimeout;
	}

	/**
	 * @param clientTokenTimeout 要设置的 clientTokenTimeout
	 * @return 对象自身
	 */
	public SaOAuth2Config setClientTokenTimeout(long clientTokenTimeout) {
		this.clientTokenTimeout = clientTokenTimeout;
		return this;
	}

	
	

	// -------------------- SaOAuth2Handle 所有回调函数 -------------------- 
	

	/**
	 * OAuth-Server端：未登录时返回的View 
	 */
	public Supplier<Object> notLoginView = () -> "当前会话在OAuth-Server认证中心尚未登录";

	/**
	 * OAuth-Server端：重定向URL无效时返回的View  
	 */
	public BiFunction<String, String, Object> invalidUrlView = (clientId, url) -> "无效重定向URL：" + url;
	
	/**
	 * OAuth-Server端：Client请求的Scope暂未签约时返回的View 
	 */
	public BiFunction<String, String, Object> invalidScopeView = (clientId, scope) -> "请求的Scope暂未签约";
	
	/**
	 * OAuth-Server端：确认授权时返回的View 
	 */
	public BiFunction<String, String, Object> confirmView = (clientId, scope) -> "本次操作需要用户授权";

	/**
	 * OAuth-Server端：登录函数 
	 */
	public BiFunction<String, String, Object> doLoginHandle = (name, pwd) -> SaResult.error();

	/**
	 * SSO-Client端：发送Http请求的处理函数 
	 */
	public Function<String, Object> sendHttp = url -> {throw new SaTokenException("请配置Http处理器");};


	/**
	 * @param notLoginView OAuth-Server端：未登录时返回的View 
	 * @return 对象自身
	 */
	public SaOAuth2Config setNotLoginView(Supplier<Object> notLoginView) {
		this.notLoginView = notLoginView;
		return this;
	}

	/**
	 * @param invalidScopeView OAuth-Server端：重定向URL无效时返回的View 
	 * @return 对象自身
	 */
	public SaOAuth2Config setInvalidUrlView(BiFunction<String, String, Object> invalidUrlView) {
		this.invalidUrlView = invalidUrlView;
		return this;
	}

	/**
	 * @param invalidScopeView OAuth-Server端：Client请求的Scope暂未签约时返回的View 
	 * @return 对象自身
	 */
	public SaOAuth2Config setInvalidScopeView(BiFunction<String, String, Object> invalidScopeView) {
		this.invalidScopeView = invalidScopeView;
		return this;
	}

	/**
	 * @param confirmView OAuth-Server端：确认授权时返回的View 
	 * @return 对象自身
	 */
	public SaOAuth2Config setConfirmView(BiFunction<String, String, Object> confirmView) {
		this.confirmView = confirmView;
		return this;
	}
	
	/**
	 * @param doLoginHandle OAuth-Server端：登录函数 
	 * @return 对象自身
	 */
	public SaOAuth2Config setDoLoginHandle(BiFunction<String, String, Object> doLoginHandle) {
		this.doLoginHandle = doLoginHandle;
		return this;
	}

	/**
	 * @param sendHttp 发送Http请求的处理函数 
	 * @return 对象自身 
	 */
	public SaOAuth2Config setSendHttp(Function<String, Object> sendHttp) {
		this.sendHttp = sendHttp;
		return this;
	}

	
	
	
	
	@Override
	public String toString() {
		return "SaOAuth2Config [codeTimeout=" + codeTimeout + ", accessTokenTimeout=" + accessTokenTimeout
				+ ", refreshTokenTimeout=" + refreshTokenTimeout + "]";
	}

}

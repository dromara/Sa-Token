package cn.dev33.satoken.oauth2.logic;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.model.CodeModel;
import cn.dev33.satoken.oauth2.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.model.RequestAuthModel;
import cn.dev33.satoken.oauth2.model.SaClientModel;

/**
 * Sa-Token-OAuth2 模块 工具类 
 * @author kong
 * 
 */
public class SaOAuth2Util {

	/**
	 * 模板代码对象 
	 */
	public static SaOAuth2Template saOAuth2Template = new SaOAuth2Template();

	
	// ------------------- 资源校验API  
	
	/**
	 * 根据id获取Client信息, 如果Client为空，则抛出异常 
	 * @param clientId 应用id
	 * @return ClientModel 
	 */
	public static SaClientModel checkClientModel(String clientId) {
		return saOAuth2Template.checkClientModel(clientId);
	}
	
	/**
	 * 获取 Access-Token，如果AccessToken为空则抛出异常 
	 * @param accessToken . 
	 * @return .
	 */
	public static AccessTokenModel checkAccessToken(String accessToken) {
		return saOAuth2Template.checkAccessToken(accessToken);
	}
	
	/**
	 * 获取 Client-Token，如果ClientToken为空则抛出异常 
	 * @param clientToken . 
	 * @return .
	 */
	public static ClientTokenModel checkClientToken(String clientToken) {
		return saOAuth2Template.checkClientToken(clientToken);
	}

	/**
	 * 获取 Access-Token 所代表的LoginId 
	 * @param accessToken Access-Token 
	 * @return LoginId 
	 */
	public static Object getLoginIdByAccessToken(String accessToken) {
		return saOAuth2Template.getLoginIdByAccessToken(accessToken);
	}
	
	/**
	 * 校验：指定 Access-Token 是否具有指定 Scope  
	 * @param accessToken Access-Token
	 * @param scopes 需要校验的权限列表 
	 */
	public static void checkScope(String accessToken, String... scopes) {
		saOAuth2Template.checkScope(accessToken, scopes);
	}
	
	// ------------------- generate 构建数据 
	
	/**
	 * 构建Model：请求Model  
	 * @param req SaRequest对象 
	 * @param loginId 账号id 
	 * @return RequestAuthModel对象 
	 */
	public static RequestAuthModel generateRequestAuth(SaRequest req, Object loginId) {
		return saOAuth2Template.generateRequestAuth(req, loginId);
	}
	
	/**
	 * 构建Model：Code授权码 
	 * @param ra 请求参数Model 
	 * @return 授权码Model
	 */
	public static CodeModel generateCode(RequestAuthModel ra) {
		return saOAuth2Template.generateCode(ra);
	}
	
	/**
	 * 构建Model：Access-Token  
	 * @param code 授权码Model
	 * @return AccessToken Model
	 */
	public static AccessTokenModel generateAccessToken(String code) {
		return saOAuth2Template.generateAccessToken(code);
	}

	/**
	 * 刷新Model：根据 Refresh-Token 生成一个新的 Access-Token 
	 * @param refreshToken Refresh-Token值 
	 * @return 新的 Access-Token 
	 */
	public static AccessTokenModel refreshAccessToken(String refreshToken) {
		return saOAuth2Template.refreshAccessToken(refreshToken);
	}

	/**
	 * 构建Model：Access-Token (根据RequestAuthModel构建，用于隐藏式 and 密码式) 
	 * @param ra 请求参数Model 
	 * @param isCreateRt 是否生成对应的Refresh-Token 
	 * @return Access-Token Model 
	 */
	public static AccessTokenModel generateAccessToken(RequestAuthModel ra, boolean isCreateRt) {
		return saOAuth2Template.generateAccessToken(ra, isCreateRt);
	}
	
	/**
	 * 构建Model：Client-Token 
	 * @param clientId 应用id 
	 * @param scope 授权范围 
	 * @return Client-Token Model 
	 */
	public static ClientTokenModel generateClientToken(String clientId, String scope) {
		return saOAuth2Template.generateClientToken(clientId, scope);
	}
	
	/**
	 * 构建URL：下放Code URL (Authorization Code 授权码)
	 * @param redirectUri 下放地址 
	 * @param code code参数
	 * @param state state参数 
	 * @return 构建完毕的URL 
	 */
	public static String buildRedirectUri(String redirectUri, String code, String state) {
		return saOAuth2Template.buildRedirectUri(redirectUri, code, state);
	}

	/**
	 * 构建URL：下放Access-Token URL （implicit 隐藏式）
	 * @param redirectUri 下放地址 
	 * @param token token
	 * @param state state参数 
	 * @return 构建完毕的URL 
	 */
	public static String buildImplicitRedirectUri(String redirectUri, String token, String state) {
		return saOAuth2Template.buildImplicitRedirectUri(redirectUri, token, state);
	}
	
	/**
	 * 回收 Access-Token 
	 * @param accessToken Access-Token值 
	 */
	public static void revokeAccessToken(String accessToken) {
		saOAuth2Template.revokeAccessToken(accessToken);
	}
	
	// ------------------- 数据校验 
	
	/**
	 * 判断：指定 loginId 是否对一个 Client 授权给了指定 Scope 
	 * @param loginId 账号id 
	 * @param clientId 应用id 
	 * @param scope 权限 
	 * @return 是否已经授权
	 */
	public static boolean isGrant(Object loginId, String clientId, String scope) {
		return saOAuth2Template.isGrant(loginId, clientId, scope);
	}
	
	/**
	 * 校验：该Client是否签约了指定的Scope 
	 * @param clientId 应用id
	 * @param scope 权限(多个用逗号隔开) 
	 */
	public static void checkContract(String clientId, String scope) {
		saOAuth2Template.checkContract(clientId, scope);
	}
	
	/**
	 * 校验：该Client使用指定url作为回调地址，是否合法 
	 * @param clientId 应用id 
	 * @param url 指定url
	 */
	public static void checkRightUrl(String clientId, String url) {
		saOAuth2Template.checkRightUrl(clientId, url);
	}
	
	/**
	 * 校验：clientId 与 clientSecret 是否正确
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @return SaClientModel对象 
	 */
	public static SaClientModel checkClientSecret(String clientId, String clientSecret) {
		return saOAuth2Template.checkClientSecret(clientId, clientSecret);
	}
	
	/**
	 * 校验：使用 code 获取 token 时提供的参数校验 
	 * @param code 授权码
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @param redirectUri 重定向地址 
	 * @return CodeModel对象 
	 */
	public static CodeModel checkGainTokenParam(String code, String clientId, String clientSecret, String redirectUri) {
		return saOAuth2Template.checkGainTokenParam(code, clientId, clientSecret, redirectUri);
	}

	/**
	 * 校验：使用 Refresh-Token 刷新 Access-Token 时提供的参数校验 
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @param refreshToken Refresh-Token
	 * @return CodeModel对象 
	 */
	public static RefreshTokenModel checkRefreshTokenParam(String clientId, String clientSecret, String refreshToken) {
		return saOAuth2Template.checkRefreshTokenParam(clientId, clientSecret, refreshToken);
	}
	
	/**
	 * 校验：Access-Token、clientId、clientSecret 三者是否匹配成功 
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @param accessToken Access-Token 
	 * @return SaClientModel对象 
	 */
	public static AccessTokenModel checkAccessTokenParam(String clientId, String clientSecret, String accessToken) {
		return saOAuth2Template.checkAccessTokenParam(clientId, clientSecret, accessToken);
	}
	
	// ------------------- save 数据 
	
	/**
	 * 持久化：用户授权记录 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @param scope 权限列表(多个逗号隔开) 
	 */
	public static void saveGrantScope(String clientId, Object loginId, String scope) {
		saOAuth2Template.saveGrantScope(clientId, loginId, scope);
	}
	
	
	// ------------------- get 数据
	
	/**
	 * 获取：Code Model  
	 * @param code .
	 * @return .
	 */
	public static CodeModel getCode(String code) {
		return saOAuth2Template.getCode(code);
	}

	/**
	 * 获取：Access-Token Model 
	 * @param accessToken . 
	 * @return .
	 */
	public static AccessTokenModel getAccessToken(String accessToken) {
		return saOAuth2Template.getAccessToken(accessToken);
	}
	
	/**
	 * 获取：Refresh-Token Model 
	 * @param refreshToken . 
	 * @return . 
	 */
	public static RefreshTokenModel getRefreshToken(String refreshToken) {
		return saOAuth2Template.getRefreshToken(refreshToken);
	}
	
	/**
	 * 获取：Client-Token Model 
	 * @param clientToken . 
	 * @return .
	 */
	public static ClientTokenModel getClientToken(String clientToken) {
		return saOAuth2Template.getClientToken(clientToken);
	}
	
	/**
	 * 获取：用户授权记录 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return 权限 
	 */
	public static String getGrantScope(String clientId, Object loginId) {
		return saOAuth2Template.getGrantScope(clientId, loginId);
	}
	
	

}

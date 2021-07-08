package cn.dev33.satoken.oauth2.logic;

import java.util.List;

import cn.dev33.satoken.oauth2.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.model.CodeModel;
import cn.dev33.satoken.oauth2.model.RequestAuthModel;

/**
 * sa-token-oauth2 模块 静态类接口转发, 方便调用 
 * @author kong
 *
 */
public class SaOAuth2Util {

	public static SaOAuth2Template saOAuth2Template = new SaOAuth2Template();
	
	
	// ------------------- 获取数据 
	
	/**
	 * 返回此平台所有权限集合 
	 * @return 此平台所有权限名称集合 
	 */
	public static List<String> getAppScopeList() {
		return saOAuth2Template.getAppScopeList();
	}

	/**
	 * 返回指定Client签约的所有Scope名称集合 
	 * @param clientId 应用id 
	 * @return Scope集合 
	 */
	public static List<String> getClientScopeList(String clientId) {
		return saOAuth2Template.getClientScopeList(clientId);
	}

	/**
	 * 获取指定 LoginId 对指定 Client 已经授权过的所有 Scope 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return Scope集合 
	 */
	public static List<String> getGrantScopeList(Object loginId, String clientId) {
		return saOAuth2Template.getGrantScopeList(loginId, clientId);
	}

	
	// ------------------- 数据校验 
	
	/**
	 * 指定 loginId 是否对一个 Client 授权给了指定 Scope 
	 * @param clientId 应用id 
	 * @param scope 权限 
	 * @param loginId 账号id 
	 * @return 是否已经授权
	 */
	public static boolean isGrant(Object loginId, String clientId, String scope) {
		return saOAuth2Template.isGrant(loginId, clientId, scope);
	}
	
	/**
	 * 校验code、clientId、clientSecret 三者是否正确 
	 * @param code 授权码
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @return CodeModel对象 
	 */
	public static CodeModel checkCodeIdSecret(String code, String clientId, String clientSecret) {
		return saOAuth2Template.checkCodeIdSecret(code, clientId, clientSecret);
	}
	
	/**
	 * [default] 校验access_token、clientId、clientSecret 三者是否正确 
	 * @param accessToken access_token
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @return AccessTokenModel对象 
	 */
	public static AccessTokenModel checkTokenIdSecret(String accessToken, String clientId, String clientSecret) {
		return saOAuth2Template.checkTokenIdSecret(accessToken, clientId, clientSecret);
	}
	
	

	// ------------------- 逻辑相关 
	
	/**
	 * 根据参数生成一个授权码并返回 
	 * @param authModel 请求授权参数Model 
	 * @return 授权码Model
	 */
	public static CodeModel generateCode(RequestAuthModel authModel) {
		return saOAuth2Template.generateCode(authModel);
	}
	
	/**
	 * 根据授权码获得授权码Model 
	 * @param code 授权码 
	 * @return 授权码Model
	 */
	public static CodeModel getCode(String code) {
		return saOAuth2Template.getCode(code);
	}

	/**
	 * 手动更改授权码对象信息
	 * @param code 授权码 
	 * @param codeModel 授权码Model 
	 */
	public static void updateCode(String code, CodeModel codeModel) {
		saOAuth2Template.updateCode(code, codeModel);
	}

	/**
	 * 确认授权一个code 
	 * @param code 授权码 
	 */
	public static void confirmCode(String code) {
		saOAuth2Template.confirmCode(code);
	}

	/**
	 * [default] 删除一个授权码 
	 * @param code 授权码 
	 */
	public static void deleteCode(String code) {
		saOAuth2Template.deleteCode(code);
	}
	
	/**
	 * [default] 根据授权码Model生成一个access_token
	 * @param codeModel 授权码Model
	 * @return AccessTokenModel
	 */
	public static AccessTokenModel generateAccessToken(CodeModel codeModel) {
		return saOAuth2Template.generateAccessToken(codeModel);
	}

	/**
	 * [default] 根据 access_token 获得其Model详细信息 
	 * @param accessToken access_token 
	 * @return AccessTokenModel (授权码Model) 
	 */
	public static AccessTokenModel getAccessToken(String accessToken) {
		return saOAuth2Template.getAccessToken(accessToken);
	}

	/**
	 * 根据 refresh_token 生成一个新的 access_token 
	 * @param refreshToken refresh_token
	 * @return 新的 access_token 
	 */
	public static AccessTokenModel refreshAccessToken(String refreshToken) {
		return saOAuth2Template.refreshAccessToken(refreshToken);
	}

	/**
	 * [default] 根据 refresh_token 获得其Model详细信息 (授权码Model) 
	 * @param refreshToken refresh_token 
	 * @return RefreshToken (授权码Model) 
	 */
	public static CodeModel getRefreshToken(String refreshToken) {
		return saOAuth2Template.getRefreshToken(refreshToken);
	}

	/**
	 * [default] 获取 access_token 的有效期 
	 * @param accessToken access_token 
	 * @return 有效期 
	 */
	public static long getAccessTokenExpiresIn(String accessToken) {
		return saOAuth2Template.getAccessTokenExpiresIn(accessToken);
	}

	/**
	 * [default] 获取 refresh_token 的有效期 
	 * @param refreshToken refresh_token 
	 * @return 有效期 
	 */
	public static long getRefreshTokenExpiresIn(String refreshToken) {
		return saOAuth2Template.getRefreshTokenExpiresIn(refreshToken);
	}

	/**
	 * [default] 获取 access_token 所代表的LoginId 
	 * @param accessToken access_token 
	 * @return LoginId 
	 */
	public static Object getLoginIdByAccessToken(String accessToken) {
		return saOAuth2Template.getLoginIdByAccessToken(accessToken);
	}
	
	
	
	
	
	
	
}

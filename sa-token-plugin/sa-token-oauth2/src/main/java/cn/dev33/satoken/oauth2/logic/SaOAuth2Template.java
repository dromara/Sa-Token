package cn.dev33.satoken.oauth2.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Consts.Param;
import cn.dev33.satoken.oauth2.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.model.CodeModel;
import cn.dev33.satoken.oauth2.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.model.RequestAuthModel;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * sa-token-oauth2 模块 逻辑接口 
 * @author kong
 *
 */
public class SaOAuth2Template {

	// ------------------- 获取数据 
	/**
	 * 返回此平台所有权限集合 
	 * @return 此平台所有权限名称集合 
	 */
	public List<String> getAppScopeList() {
		return Arrays.asList("userinfo");
	}

	/**
	 * 返回指定Client签约的所有Scope名称集合 
	 * @param clientId 应用id 
	 * @return Scope集合 
	 */
	public List<String> getClientScopeList(String clientId) {
		// 默认返回此APP的所有权限 
		return getAppScopeList();
	}

	/**
	 * [转Redis]获取指定 LoginId 对指定 Client 已经授权过的所有 Scope 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return Scope集合 
	 */
	public List<String> getGrantScopeList(Object loginId, String clientId) {
		// 默认返回空集合  
		return Arrays.asList(); 
	}

	/**
	 * 返回指定Client允许的回调域名, 多个用逗号隔开, *代表不限制 
	 * @param clientId 应用id 
	 * @return domain集合 
	 */
	public String getClientDomain(String clientId) {
		return "*";
	}

	/**
	 * 返回指定ClientId的ClientSecret 
	 * @param clientId 应用id 
	 * @return 此应用的秘钥 
	 */
	public String getClientSecret(String clientId) {
		return null;
	}

	/**
	 * 根据ClientId和LoginId返回openid 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return 此账号在此Client下的openid 
	 */
	public String getOpenid(String clientId, Object loginId) {
		return null;
	}

	/**
	 * [可取消]根据ClientId和openid返回LoginId 
	 * @param clientId 应用id 
	 * @param openid openid
	 * @return LoginId 
	 */
	public Object getLoginId(String clientId, String openid) {
		return null;
	}

	// ------------------- conver 数据转换  
	/**
	 * [OK] 将 CodeModel 转换为 AccessTokenModel 
	 * @param cm CodeModel对象
	 * @return AccessToken对象 
	 */
	public AccessTokenModel converCodeToAccessToken(CodeModel cm) {
		AccessTokenModel at = new AccessTokenModel();
		at.accessToken = randomAccessToken(cm.clientId, cm.loginId, cm.scope); 
		at.refreshToken = randomRefreshToken(cm.clientId, cm.loginId, cm.scope);
		at.clientId = cm.clientId;
		at.loginId = cm.loginId;
		at.scope = cm.scope;
		at.openid = getOpenid(cm.clientId, cm.loginId);
		at.expiresTime = System.currentTimeMillis() + (SaOAuth2Manager.getConfig().getAccessTokenTimeout() * 1000);
		// at.refreshExpiresTime = System.currentTimeMillis() + (SaOAuth2Manager.getConfig().getRefreshTokenTimeout() * 1000);
		return at;
	}
	/**
	 * [OK] 将 AccessToken 转换为 RefreshTokenModel 
	 * @param at AccessToken对象
	 * @return RefreshToken对象 
	 */
	public RefreshTokenModel converAccessTokenToRefreshToken(AccessTokenModel at) {
		RefreshTokenModel rt = new RefreshTokenModel();
		rt.refreshToken = at.refreshToken;
		rt.clientId = at.clientId;
		rt.loginId = at.loginId;
		rt.scope = at.scope;
		rt.openid = at.openid;
		rt.expiresTime = System.currentTimeMillis() + (SaOAuth2Manager.getConfig().getRefreshTokenTimeout() * 1000);
		return rt;
	}
	/**
	 * [OK] 将 RefreshTokenModel 转换为 AccessTokenModel 
	 * @param codeModel CodeModel对象
	 * @return RefreshToken对象 
	 */
	public AccessTokenModel converRefreshTokenToAccessToken(RefreshTokenModel rt) {
		AccessTokenModel at = new AccessTokenModel();
		at.accessToken = randomAccessToken(rt.clientId, rt.loginId, rt.scope);
		at.refreshToken = rt.refreshToken;
		at.clientId = rt.clientId;
		at.loginId = rt.loginId;
		at.scope = rt.scope;
		at.openid = rt.openid;
		at.expiresTime = System.currentTimeMillis() + (SaOAuth2Manager.getConfig().getAccessTokenTimeout() * 1000);
		at.refreshExpiresTime = rt.expiresTime;
		return at;
	}
	/**
	 * 将指定字符串按照逗号分隔符转化为字符串集合 
	 * @param str 字符串
	 * @return 分割后的字符串集合 
	 */
	public List<String> convertStringToList(String str) {
		String[] arr = str.split(",");
		List<String> list = new ArrayList<String>();
		for (String s : arr) {
			if(SaFoxUtil.isEmpty(s) == false) {
				list.add(s);
			}
		}
		return list;
	}
	
	// ------------------- generate 构建数据 
	/**
	 * 构建：请求Model  
	 * @param req SaRequest对象 
	 * @param loginId 账号id 
	 * @return RequestAuthModel对象 
	 */
	public RequestAuthModel generateRequestAuth(SaRequest req, Object loginId) {
		RequestAuthModel ra = new RequestAuthModel();
		ra.clientId = req.getParamNotNull(Param.client_id);
		ra.responseType = req.getParamNotNull(Param.response_type);
		ra.redirectUri = req.getParamNotNull(Param.redirect_uri);
		ra.state = req.getParam(Param.state);
		ra.scope = req.getParam(Param.scope, "");
		ra.loginId = loginId;
		return ra;
	}
	/**
	 * 构建：授权码Model 
	 * @param ra 请求授权参数Model 
	 * @return 授权码Model
	 */
	public CodeModel generateCode(RequestAuthModel ra) {
		
		// 删除旧Code 
		String oldCode = getCodeValue(ra.clientId, ra.loginId);
		if(oldCode != null) {
			deleteCode(oldCode);
			// deleteCodeIndex(ra.clientId, ra.loginId); // 此处无需删除，因为下边的set会直接覆盖掉 
		}

		// 生成新Code 
		String code = randomCode(ra.clientId, ra.loginId, ra.scope);
		CodeModel codeModel = new CodeModel(code, ra.clientId, ra.scope, ra.loginId, ra.redirectUri);
		
		// 保存新Code 
		saveCode(codeModel);
		saveCodeIndex(codeModel);
		
		// 返回 
		return codeModel;
	}
	/**
	 * 构建：AccessToken Model 
	 * @param codeModel 授权码Model
	 * @return AccessTokenModel
	 */
	public AccessTokenModel generateAccessToken(String code) {

		// 先校验 
		CodeModel cm = getCode(code);
		SaOAuth2Exception.throwBy(cm == null, "无效code");
		
		// 生成token 
		AccessTokenModel at = converCodeToAccessToken(cm); 
		RefreshTokenModel rt = converAccessTokenToRefreshToken(at);
		at.refreshExpiresTime = rt.expiresTime;

		// 保存Token 
		saveAccessToken(at);
		saveAccessTokenIndex(at);
		saveRefreshToken(rt);
		saveRefreshTokenIndex(rt);
		
		// 删除此Code 
		deleteCode(code);
		deleteCodeIndex(cm.clientId, cm.loginId);
		
		// 返回 
		return at;
	}
	/**
	 * 刷新：根据 RefreshToken 生成一个 AccessToken 
	 * @param refreshToken refresh_token
	 * @return 新的 access_token 
	 */
	public AccessTokenModel refreshAccessToken(String refreshToken) {
		
		// 获取RefreshToken信息 
		RefreshTokenModel rt = getRefreshToken(refreshToken);
		SaOAuth2Exception.throwBy(rt == null, "无效refresh_token: " + refreshToken); 
		
		// 删除旧AccessToken 
		String atValue = getAccessTokenValue(rt.clientId, rt.loginId);
		if(atValue != null) {
			deleteAccessToken(atValue);
			deleteAccessTokenIndex(rt.clientId, rt.loginId);
		}
		
		// 生成新AccessToken 
		AccessTokenModel at = converRefreshTokenToAccessToken(rt);
		
		// 保存新AccessToken 
		saveAccessToken(at);
		saveAccessTokenIndex(at); 
		
		// 返回新AccessToken  
		return at;
	}
	/**
	 * 构建：AccessToken Model (根据RequestAuthModel) 用于隐藏式 
	 * @param ra 请求授权参数Model 
	 * @return 授权码Model 
	 */
	public AccessTokenModel generateAccessToken(RequestAuthModel ra) {
		
		// 删除旧AccessToken 
		String oldAccessToken = getAccessTokenValue(ra.clientId, ra.loginId);
		if(oldAccessToken != null) {
			deleteAccessToken(oldAccessToken);
		}

		// 生成新AccessToken 
		String atValue = randomAccessToken(ra.clientId, ra.loginId, ra.scope);
		AccessTokenModel at = new AccessTokenModel(atValue, ra.clientId, ra.loginId, ra.scope);
		at.openid = getOpenid(ra.clientId, ra.loginId);
		at.expiresTime = System.currentTimeMillis() + (SaOAuth2Manager.getConfig().getAccessTokenTimeout() * 1000);

		// 保存新Token 
		saveAccessToken(at);
		saveAccessTokenIndex(at);
		
		// 返回新Token 
		return at;
	}
	
	/**
	 * 构建：ClientToken Model 
	 * @param ra 请求授权参数Model 
	 * @return ClientToken-Model 
	 */
	public ClientTokenModel generateClientToken(String clientId, String scope) {
		// 1、删掉 Past-Token 
		String ptValue = getPastTokenValue(clientId);
		if(ptValue != null) {
			deleteClientToken(ptValue);
		}
		
		// 2、将Client-Token 标记 Past-Token 
		String ctValue = getClientTokenValue(clientId);
		if(ctValue != null) {
			ClientTokenModel ct = getClientToken(ctValue);
			if(ct != null) {
				savePastClientTokenIndex(ct); 	
			}
		}
		
		// 3、生成新Token 
		ClientTokenModel ct = new ClientTokenModel(randomClientToken(clientId, scope), clientId, scope);
		ct.expiresTime = System.currentTimeMillis() + (SaOAuth2Manager.getConfig().getClientTokenTimeout() * 1000);
		
		// 3、保存新Token 
		saveClientToken(ct);
		saveClientTokenIndex(ct); 
		
		// 4、返回 
		return ct;
	}
	
	// ------------------- 其它方法 
	/**
	 * 获取 access_token 所代表的LoginId 
	 * @param accessToken access_token 
	 * @return LoginId 
	 */
	public Object getLoginIdByAccessToken(String accessToken) {
		AccessTokenModel tokenModel = SaOAuth2Util.getAccessToken(accessToken);
		if(tokenModel == null) {
			throw new SaTokenException("无效access_token");
		}
		return getLoginId(tokenModel.clientId, tokenModel.openid);
	}
	/**
	 * [OK] 构建URL：下放授权码URL 
	 * @param redirectUri 下放地址 
	 * @param code code参数
	 * @param state state参数 
	 * @return 构建完毕的URL 
	 */
	public String buildRedirectUri(String redirectUri, String code, String state) {
		String url = SaFoxUtil.joinParam(redirectUri, Param.code, code);
		if(SaFoxUtil.isEmpty(state) == false) {
			url = SaFoxUtil.joinParam(url, Param.state, state); 
		}
		return url;
	}
	/**
	 * [OK] 构建URL：下放Token URL 
	 * @param redirectUri 下放地址 
	 * @param token token
	 * @param state state参数 
	 * @return 构建完毕的URL 
	 */
	public String buildRedirectUri2(String redirectUri, String token, String state) {
		String url = SaFoxUtil.joinSharpParam(redirectUri, Param.token, token);
		if(SaFoxUtil.isEmpty(state) == false) {
			url = SaFoxUtil.joinParam(url, Param.state, state); 
		}
		return url;
	}

	// ------------------- 数据校验 
	/**
	 * [OK] 判断：该Client是否签约了指定的Scope 
	 * @param clientId 应用id
	 * @param scope 权限 
	 */
	public boolean isContract(String clientId, String scope) {
		if(SaFoxUtil.isEmpty(scope)) {
			return true;
		}
		List<String> clientScopeList = getClientScopeList(clientId);
		List<String> scopelist = Arrays.asList(scope.split(","));
		return clientScopeList.containsAll(scopelist);
	}
	/**
	 * [OK] 指定 loginId 是否对一个 Client 授权给了指定 Scope 
	 * @param loginId 账号id 
	 * @param clientId 应用id 
	 * @param scope 权限 
	 * @return 是否已经授权
	 */
	public boolean isGrant(Object loginId, String clientId, String scope) {
		List<String> grantScopeList = getGrantScopeList(loginId, clientId);
		List<String> scopeList = convertStringToList(scope);
		return scopeList.size() == 0 || grantScopeList.containsAll(scopeList);
	}
	/**
	 * [OK] 指定Client使用指定url作为回调地址，是否合法 
	 * @param clientId 应用id 
	 * @param url 指定url
	 * @return 是否合法 
	 */
	public boolean isRightUrl(String clientId, String url) {
		
		// 1、是否是一个有效的url
		if(SaFoxUtil.isUrl(url) == false) {
			return false;
		}
		
		// 2、截取掉?后面的部分 
		int qIndex = url.indexOf("?");
		if(qIndex != -1) {
			url = url.substring(0, qIndex);
		}
		
		// 3、是否在[允许地址列表]之中 
		String domain = getClientDomain(clientId);
		if(SaFoxUtil.isEmpty(domain)) {
			return false;
		}
		List<String> authUrlList = Arrays.asList(domain.replaceAll(" ", "").split(",")); 
		if(SaManager.getSaTokenAction().hasElement(authUrlList, url) == false) {
			return false;
		}
		
		// 验证通过 
		return true;
	}
	/**
	 * [OK 方法名改一下]校验code、clientId、clientSecret 三者是否正确 
	 * @param code 授权码
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @param redirectUri 秘钥 
	 * @return CodeModel对象 
	 */
	public CodeModel checkCodeIdSecret(String code, String clientId, String clientSecret, String redirectUri) {
		
		// 校验：Code是否存在 
		CodeModel codeModel = getCode(code);
		if(codeModel == null) {
			throw new SaOAuth2Exception("无效code");
		}

		// 校验：ClientId是否一致 
		if(codeModel.getClientId().equals(clientId) == false){
			throw new SaOAuth2Exception("无效client_id");
		}
		
		// 校验：Secret是否正确 
		String dbClientSecret = getClientSecret(clientId);
		if(dbClientSecret == null || dbClientSecret.equals(clientSecret) == false){
			throw new SaOAuth2Exception("无效client_secret");
		}
		
		// 如果提供了redirectUri，则校验其是否与请求Code时提供的一致 
		if(SaFoxUtil.isEmpty(redirectUri) == false) {
			if(redirectUri.equals(codeModel.redirectUri) == false) {
				throw new SaOAuth2Exception("无效redirect_uri"); 
			}
		}
		
		// 返回CodeMdoel
		return codeModel;
	}
	/**
	 * 校验access_token、clientId、clientSecret 三者是否正确 
	 * @param accessToken access_token
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @return AccessTokenModel对象 
	 */
	public AccessTokenModel checkTokenIdSecret(String accessToken, String clientId, String clientSecret) {
		
		// 获取授权码信息 
		AccessTokenModel tokenModel = getAccessToken(accessToken);
		
		// 验证code、client_id、client_secret
		if(tokenModel == null) {
			throw new SaTokenException("无效access_token");
		}
		if(tokenModel.clientId.equals(clientId) == false){
			throw new SaTokenException("无效client_id");
		}
		String dbClientSecret = getClientSecret(clientId);
		if(dbClientSecret == null || dbClientSecret.equals(clientSecret)){
			throw new SaTokenException("无效client_secret");
		}
		
		// 返回AccessTokenModel
		return tokenModel;
	}

	// ------------------- save 数据 
	/**
	 * 持久化：Code-Model 
	 * @param c . 
	 */
	public void saveCode(CodeModel c) {
		SaManager.getSaTokenDao().setObject(splicingKeySaveCode(c.code), c, SaOAuth2Manager.getConfig().getCodeTimeout());
	}
	/**
	 * 持久化：Code-索引 
	 * @param c . 
	 */
	public void saveCodeIndex(CodeModel c) {
		SaManager.getSaTokenDao().set(splicingKeyCodeIndex(c.clientId, c.loginId), c.code, SaOAuth2Manager.getConfig().getCodeTimeout());
	}
	/**
	 * 持久化：AccessToken-Model 
	 * @param at . 
	 */
	public void saveAccessToken(AccessTokenModel at) {
		SaManager.getSaTokenDao().setObject(splicingKeySaveAccessToken(at.accessToken), at, at.getExpiresIn());
	}
	/**
	 * 持久化：AccessToken-索引 
	 * @param at . 
	 */
	public void saveAccessTokenIndex(AccessTokenModel at) {
		SaManager.getSaTokenDao().set(splicingKeyAccessTokenIndex(at.clientId, at.loginId), at.accessToken, at.getExpiresIn());
	}
	/**
	 * 持久化：RefreshToken-Model 
	 * @param rt . 
	 */
	public void saveRefreshToken(RefreshTokenModel rt) {
		SaManager.getSaTokenDao().setObject(splicingKeySaveRefreshToken(rt.refreshToken), rt, rt.getExpiresIn());
	}
	/**
	 * 持久化：RefreshToken-索引 
	 * @param rt . 
	 */
	public void saveRefreshTokenIndex(RefreshTokenModel rt) {
		SaManager.getSaTokenDao().set(splicingKeyRefreshTokenIndex(rt.clientId, rt.loginId), rt.refreshToken, rt.getExpiresIn());
	}
	/**
	 * 持久化：ClientToken-Model 
	 * @param at . 
	 */
	public void saveClientToken(ClientTokenModel ct) {
		SaManager.getSaTokenDao().setObject(splicingKeySaveClientToken(ct.clientToken), ct, ct.getExpiresIn());
	}
	/**
	 * 持久化：ClientToken-索引 
	 * @param at . 
	 */
	public void saveClientTokenIndex(ClientTokenModel ct) {
		SaManager.getSaTokenDao().set(splicingKeyClientTokenIndex(ct.clientId), ct.clientToken, ct.getExpiresIn());
	}
	/**
	 * 持久化：Past-ClientToken-索引  
	 * @param at . 
	 */
	public void savePastClientTokenIndex(ClientTokenModel ct) {
		SaManager.getSaTokenDao().set(splicingKeyPastTokenIndex(ct.clientId), ct.clientToken, ct.getExpiresIn());
	}
	
	// ------------------- get 数据 [OK] 
	/**
	 * 获取：授权码 
	 * @param code .
	 * @return .
	 */
	public CodeModel getCode(String code) {
		return (CodeModel)SaManager.getSaTokenDao().getObject(splicingKeySaveCode(code));
	}
	/**
	 * 获取：授权码-Value 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return .
	 */
	public String getCodeValue(String clientId, Object loginId) {
		return SaManager.getSaTokenDao().get(splicingKeyCodeIndex(clientId, loginId)); 
	}
	/**
	 * 获取：AccessToken 
	 * @param accessToken . 
	 * @return .
	 */
	public AccessTokenModel getAccessToken(String accessToken) {
		return (AccessTokenModel)SaManager.getSaTokenDao().getObject(splicingKeySaveAccessToken(accessToken));
	}
	/**
	 * 获取：AccessToken-Value 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return .
	 */
	public String getAccessTokenValue(String clientId, Object loginId) {
		return SaManager.getSaTokenDao().get(splicingKeyAccessTokenIndex(clientId, loginId)); 
	}
	/**
	 * 获取：RefreshToken 
	 * @param refreshToken . 
	 * @return . 
	 */
	public RefreshTokenModel getRefreshToken(String refreshToken) {
		return (RefreshTokenModel)SaManager.getSaTokenDao().getObject(splicingKeySaveRefreshToken(refreshToken));
	}
	/**
	 * 获取：RefreshToken-Value 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return .
	 */
	public String getRefreshTokenValue(String clientId, Object loginId) {
		return SaManager.getSaTokenDao().get(splicingKeyRefreshTokenIndex(clientId, loginId)); 
	}
	
	/**
	 * 获取：ClientTokenModel 
	 * @param clientToken . 
	 * @return .
	 */
	public ClientTokenModel getClientToken(String clientToken) {
		return (ClientTokenModel)SaManager.getSaTokenDao().getObject(splicingKeySaveClientToken(clientToken));
	}
	/**
	 * 获取：ClientTokenModel-Value 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return .
	 */
	public String getClientTokenValue(String clientId) {
		return SaManager.getSaTokenDao().get(splicingKeyClientTokenIndex(clientId)); 
	}
	/**
	 * 获取：ClientTokenModel-Value 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return .
	 */
	public String getPastTokenValue(String clientId) {
		return SaManager.getSaTokenDao().get(splicingKeyPastTokenIndex(clientId)); 
	}
	
	
	// ------------------- delete 数据 [OK]
	/**
	 * 删除：授权码
	 * @param code 授权码 
	 */
	public void deleteCode(String code) {
		SaManager.getSaTokenDao().deleteObject(splicingKeySaveCode(code));
	}
	/**
	 * 删除：授权码索引 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 */
	public void deleteCodeIndex(String clientId, Object loginId) {
		SaManager.getSaTokenDao().delete(splicingKeyCodeIndex(clientId, loginId)); 
	}
	/**
	 * 删除：AccessToken
	 * @param accessToken .
	 */
	public void deleteAccessToken(String accessToken) {
		SaManager.getSaTokenDao().deleteObject(splicingKeySaveAccessToken(accessToken));
	}
	/**
	 * 删除：AccessToken索引 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 */
	public void deleteAccessTokenIndex(String clientId, Object loginId) {
		SaManager.getSaTokenDao().delete(splicingKeyAccessTokenIndex(clientId, loginId)); 
	}
	/**
	 * 删除：RefreshAccess
	 * @param refreshAccess .
	 */
	public void deleteRefreshAccess(String refreshAccess) {
		SaManager.getSaTokenDao().deleteObject(splicingKeySaveRefreshToken(refreshAccess));
	}
	/**
	 * 删除：RefreshAccess索引 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 */
	public void deleteRefreshAccessIndex(String clientId, Object loginId) {
		SaManager.getSaTokenDao().delete(splicingKeyRefreshTokenIndex(clientId, loginId)); 
	}
	/**
	 * 删除：ClientToken
	 * @param clientToken .
	 */
	public void deleteClientToken(String clientToken) {
		SaManager.getSaTokenDao().deleteObject(splicingKeySaveClientToken(clientToken));
	}
	/**
	 * 删除：ClientToken索引 
	 * @param clientId 应用id 
	 */
	public void deleteClientTokenIndex(String clientId) {
		SaManager.getSaTokenDao().delete(splicingKeyClientTokenIndex(clientId)); 
	}
	/**
	 * 删除：Past-Token索引 
	 * @param clientId 应用id 
	 */
	public void deletePastTokenIndex(String clientId) {
		SaManager.getSaTokenDao().delete(splicingKeyPastTokenIndex(clientId)); 
	}
	
	
	// ------------------- Random数据 [OK] 
	/**
	 * 生成授权码 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @param scope 权限
	 * @return 授权码 
	 */
	public String randomCode(String clientId, Object loginId, String scope) {
		return SaFoxUtil.getRandomString(60); 
	}
	/**
	 * 生成AccessToken 
	 * @param codeModel CodeModel对象
	 * @return AccessToken 
	 */
	public String randomAccessToken(String clientId, Object loginId, String scope) {
		return SaFoxUtil.getRandomString(60);
	}
	/**
	 * 生成RefreshToken 
	 * @param codeModel CodeModel对象
	 * @return RefreshToken 
	 */
	public String randomRefreshToken(String clientId, Object loginId, String scope) {
		return SaFoxUtil.getRandomString(60);
	}
	/**
	 * 生成ClientToken  
	 * @return RefreshToken 
	 */
	public String randomClientToken(String clientId, Object loginId) {
		return SaFoxUtil.getRandomString(60);
	}
	
	// ------------------- 拼接key [OK] 
	/**  
	 * 拼接key：授权码持久化 
	 * @param code 授权码
	 * @return key
	 */
	public String splicingKeySaveCode(String code) {
		return SaManager.getConfig().getTokenName() + ":oauth2:code:" + code;
	}
	/**  
	 * 拼接key：授权码索引 
	 * @param clientId 应用id
	 * @param loginId 账号id
	 * @return key
	 */
	public String splicingKeyCodeIndex(String clientId, Object loginId) {
		return SaManager.getConfig().getTokenName() + ":oauth2:code-index:" + clientId + ":" + loginId;
	}
	/**  
	 * 拼接key：Access-Token持久化 
	 * @param accessToken accessToken
	 * @return key
	 */
	public String splicingKeySaveAccessToken(String accessToken) {
		return SaManager.getConfig().getTokenName() + ":oauth2:access-token:" + accessToken;
	}
	/**  
	 * 拼接key：Access-Token索引 
	 * @param clientId 应用id
	 * @param loginId 账号id
	 * @return key
	 */
	public String splicingKeyAccessTokenIndex(String clientId, Object loginId) {
		return SaManager.getConfig().getTokenName() + ":oauth2:access-token-index:" + clientId + ":" + loginId;
	}
	/**  
	 * 拼接key：RefreshToken持久化 
	 * @param refreshToken refreshToken
	 * @return key
	 */
	public String splicingKeySaveRefreshToken(String refreshToken) {
		return SaManager.getConfig().getTokenName() + ":oauth2:refresh-token:" + refreshToken;
	}
	/**  
	 * 拼接key：RefreshToken索引 
	 * @param clientId 应用id
	 * @param loginId 账号id
	 * @return key
	 */
	public String splicingKeyRefreshTokenIndex(String clientId, Object loginId) {
		return SaManager.getConfig().getTokenName() + ":oauth2:refresh-token-index:" + clientId + ":" + loginId;
	}
	/**  
	 * 拼接key：Client-Token持久化 
	 * @param clientToken accessToken
	 * @return key
	 */
	public String splicingKeySaveClientToken(String clientToken) {
		return SaManager.getConfig().getTokenName() + ":oauth2:client-token:" + clientToken;
	}
	/**  
	 * 拼接key：ClientToken 索引 
	 * @param clientToken accessToken
	 * @return key
	 */
	public String splicingKeyClientTokenIndex(String clientId) {
		return SaManager.getConfig().getTokenName() + ":oauth2:client-token-indedx:" + clientId;
	}
	/**  
	 * 拼接key：Past-ClientToken 索引 
	 * @param clientId clientId 
	 * @return key 
	 */
	public String splicingKeyPastTokenIndex(String clientId) {
		return SaManager.getConfig().getTokenName() + ":oauth2:past-token-indedx:" + clientId;
	}
	
}

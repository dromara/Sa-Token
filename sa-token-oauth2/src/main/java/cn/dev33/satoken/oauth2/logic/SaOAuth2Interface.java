package cn.dev33.satoken.oauth2.logic;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.model.CodeModel;
import cn.dev33.satoken.oauth2.model.RequestAuthModel;
import cn.dev33.satoken.oauth2.util.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.util.SaOAuth2InsideUtil;
import cn.dev33.satoken.util.SaTokenInsideUtil;

/**
 * sa-token-oauth2 模块 逻辑接口 
 * @author kong
 *
 */
public interface SaOAuth2Interface {

	
	// ------------------- 获取数据 
	
	/**
	 * [default] 返回此平台所有权限集合 
	 * @return 此平台所有权限名称集合 
	 */
	public default List<String> getAppScopeList() {
		return Arrays.asList("userinfo");
	}

	/**
	 * [default] 返回指定Client签约的所有Scope名称集合 
	 * @param clientId 应用id 
	 * @return Scope集合 
	 */
	public default List<String> getClientScopeList(String clientId) {
		// 默认返回此APP的所有权限 
		return getAppScopeList();
	}

	/**
	 * [default] 获取指定 LoginId 对指定 Client 已经授权过的所有 Scope 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return Scope集合 
	 */
	public default List<String> getGrantScopeList(Object loginId, String clientId) {
		// 默认返回空集合  
		return Arrays.asList(); 
	}

	/**
	 * [default] 返回指定Client允许的回调域名, 多个用逗号隔开, *代表不限制 
	 * @param clientId 应用id 
	 * @return domain集合 
	 */
	public default String getClientDomain(String clientId) {
		return "*";
	}

	/**
	 * [default] 返回指定ClientId的ClientSecret 
	 * @param clientId 应用id 
	 * @return 此应用的秘钥 
	 */
	public default String getClientSecret(String clientId) {
		return null;
	}

	/**
	 * [default] 根据ClientId和LoginId返回openid 
	 * @param clientId 应用id 
	 * @param loginId 账号id 
	 * @return 此账号在此Client下的openid 
	 */
	public default String getOpenid(String clientId, Object loginId) {
		return null;
	}

	/**
	 * [default] 根据ClientId和openid返回LoginId 
	 * @param clientId 应用id 
	 * @param openid openid
	 * @return LoginId 
	 */
	public default Object getLoginId(String clientId, String openid) {
		return null;
	}

	
	// ------------------- 数据校验 
	
	/**
	 * [default] 检查一个 Client 是否签约了指定的Scope 
	 * @param clientId 应用id
	 * @param scope 权限 
	 */
	public default void checkContract(String clientId, String scope) {
		List<String> clientScopeList = getClientScopeList(clientId);
		List<String> scopelist = Arrays.asList(scope.split(","));
		if(clientScopeList.containsAll(scopelist) == false) {
			throw new SaTokenException("请求授权范围超出或无效");
		}
	}

	/**
	 * [default] 指定 loginId 是否对一个 Client 授权给了指定 Scope 
	 * @param loginId 账号id 
	 * @param clientId 应用id 
	 * @param scope 权限 
	 * @return 是否已经授权
	 */
	public default boolean isGrant(Object loginId, String clientId, String scope) {
		List<String> grantScopeList = getGrantScopeList(loginId, clientId);
		List<String> scopeList = convertStringToList(scope);
		return grantScopeList.containsAll(scopeList);
	}
	
	/**
	 * [default] 指定Client使用指定url作为回调地址，是否合法 
	 * @param clientId 应用id 
	 * @param url 指定url
	 */
	public default void checkRightUrl(String clientId, String url) {
		// 首先检测url格式 
		if(SaOAuth2InsideUtil.isUrl(url) == false) {
			throw new SaTokenException("url格式错误");
		}
		// ---- 检测
		
		// 获取此应用允许的域名列表
		String domain = getClientDomain(clientId);
		// 如果是null或者空字符串, 则代表任何域名都无法通过检查 
		if(domain == null || "".equals(domain)) {
			throw new SaTokenException("重定向地址无效");
		}
		// 如果是*符号，代表允许任何域名 
		if(SaOAuth2Consts.UNLIMITED_DOMAIN.equals(domain)) {
			return;
		}
		
		// 获取域名进行比对 
		try {
			String host = new URL(url).getHost();
			List<String> domainList = convertStringToList(domain);
			if(domainList.contains(host) == false) {
				throw new SaTokenException("重定向地址不在列表中");
			}
		} catch (MalformedURLException e) {
			throw new SaTokenException("url格式错误", e);
		}
	}

	/**
	 * [default] 校验code、clientId、clientSecret 三者是否正确 
	 * @param code 授权码
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @return CodeModel对象 
	 */
	public default CodeModel checkCodeIdSecret(String code, String clientId, String clientSecret) {
		
		// 获取授权码信息 
		CodeModel codeModel = getCode(code);
		
		// 验证code、client_id、client_secret
		if(codeModel == null) {
			throw new SaTokenException("无效code");
		}

		if(codeModel.getClientId().equals(clientId) == false){
			throw new SaTokenException("无效client_id");
		}
		String dbClientSecret = getClientSecret(clientId);
		System.out.println(dbClientSecret);
		System.out.println(clientSecret);
		if(dbClientSecret == null || dbClientSecret.equals(clientSecret) == false){
			throw new SaTokenException("无效client_secret");
		}
		
		// 返回CodeMdoel
		return codeModel;
	}

	/**
	 * [default] 校验access_token、clientId、clientSecret 三者是否正确 
	 * @param accessToken access_token
	 * @param clientId 应用id 
	 * @param clientSecret 秘钥 
	 * @return AccessTokenModel对象 
	 */
	public default AccessTokenModel checkTokenIdSecret(String accessToken, String clientId, String clientSecret) {
		
		// 获取授权码信息 
		AccessTokenModel tokenModel = getAccessToken(accessToken);
		
		// 验证code、client_id、client_secret
		if(tokenModel == null) {
			throw new SaTokenException("无效access_token");
		}
		if(tokenModel.getClientId().equals(clientId) == false){
			throw new SaTokenException("无效client_id");
		}
		String dbClientSecret = getClientSecret(clientId);
		if(dbClientSecret == null || dbClientSecret.equals(clientSecret)){
			throw new SaTokenException("无效client_secret");
		}
		
		// 返回AccessTokenModel
		return tokenModel;
	}
	
	
	// ------------------- 逻辑相关 
	
	// ---- 授权码 
	/**
	 * [default] 根据参数生成一个授权码并返回 
	 * @param authModel 请求授权参数Model 
	 * @return 授权码Model
	 */
	public default CodeModel generateCode(RequestAuthModel authModel) {
		
		// 获取参数 
		String clientId = authModel.getClientId();
		String scope = authModel.getScope();
		Object loginId = authModel.getLoginId();
		String redirectUri = authModel.getRedirectUri();
		String state = authModel.getState();

		// ------ 参数校验 
		// 此Client是否签约了此Scope 
		checkContract(clientId, scope);

		// 校验重定向域名的格式是否合法 
		checkRightUrl(clientId, redirectUri);
		
		// ------ 开始生成code码  
		String code = createCode(clientId, scope, loginId);
		CodeModel codeModel = new CodeModel(code, clientId, scope, loginId);
		
		// 拼接授权后重定向的域名 (拼接code和state参数)
		String url = splicingParame(redirectUri, "code=" + code);
		if(state != null) {
			url = splicingParame(url, "state=" + state);
		}
		codeModel.setRedirectUri(url);
		
		// 拒绝授权时重定向的地址 
		codeModel.setRejectUri(splicingParame(redirectUri, "handle=reject"));
		
		// 计算此Scope是否已经授权过了 
		codeModel.setIsConfirm(isGrant(loginId, clientId, scope));
		
		// ------ 开始保存 
		
		// 将此授权码保存到DB 
		long codeTimeout = SaOAuth2Manager.getConfig().getCodeTimeout();
		SaTokenManager.getSaTokenDao().setObject(getKeyCodeModel(code), codeModel, codeTimeout);
		
		// 如果此[Client&账号]已经有code正在存储，则先删除它
		String key = getKeyClientLoginId(loginId, clientId);
		SaTokenManager.getSaTokenDao().delete(key);
		
		// 将此[Client&账号]的最新授权码保存到DB中
		// 以便于完成授权码覆盖操作: 保证每次只有最新的授权码有效 
		SaTokenManager.getSaTokenDao().set(key, code, codeTimeout);
		
		// 返回 
		return codeModel;
	}
	
	/**
	 * [default] 根据授权码获得授权码Model 
	 * @param code 授权码 
	 * @return 授权码Model
	 */
	public default CodeModel getCode(String code) {
		return (CodeModel)SaTokenManager.getSaTokenDao().getObject(getKeyCodeModel(code));
	}

	/**
	 * [default] 手动更改授权码对象信息
	 * @param code 授权码 
	 * @param codeModel 授权码Model 
	 */
	public default void updateCode(String code, CodeModel codeModel) {
		SaTokenManager.getSaTokenDao().updateObject(getKeyCodeModel(code), codeModel);
	}

	/**
	 * [default] 确认授权一个code 
	 * @param code 授权码 
	 */
	public default void confirmCode(String code) {
		// 获取codeModel
		CodeModel codeModel = getCode(code);
		// 如果该code码已经确认 
		if(codeModel.getIsConfirm() == true) {
			return;
		}
		// 进行确认
		codeModel.setIsConfirm(true);
		updateCode(code, codeModel);
	}

	/**
	 * [default] 删除一个授权码 
	 * @param code 授权码 
	 */
	public default void deleteCode(String code) {
		SaTokenManager.getSaTokenDao().deleteObject(getKeyCodeModel(code));
	}

	
	// ------------------- access_token 和 refresh_token 相关 
	
	/**
	 * [default] 根据授权码Model生成一个access_token
	 * @param codeModel 授权码Model
	 * @return AccessTokenModel
	 */
	public default AccessTokenModel generateAccessToken(CodeModel codeModel) {

		// 先校验 
		if(codeModel == null) {
			throw new SaTokenException("无效code");
		}
		if(codeModel.getIsConfirm() == false) {
			throw new SaTokenException("该code尚未授权");
		}
		
		// 获取 TokenModel 并保存 
		AccessTokenModel tokenModel = converCodeToAccessToken(codeModel);
		SaTokenManager.getSaTokenDao().setObject(getKeyAccessToken(tokenModel.getAccessToken()), tokenModel, SaOAuth2Manager.getConfig().getAccessTokenTimeout());
		
		// 将此 CodeModel 当做 refresh_token 保存下来 
		SaTokenManager.getSaTokenDao().setObject(getKeyRefreshToken(tokenModel.getRefreshToken()), codeModel, SaOAuth2Manager.getConfig().getRefreshTokenTimeout());
		
		// 返回 
		return tokenModel;
	}

	/**
	 * [default] 根据 access_token 获得其Model详细信息 
	 * @param accessToken access_token 
	 * @return AccessTokenModel (授权码Model) 
	 */
	public default AccessTokenModel getAccessToken(String accessToken) {
		return (AccessTokenModel)SaTokenManager.getSaTokenDao().getObject(getKeyAccessToken(accessToken));
	}

	/**
	 * [default] 根据 refresh_token 生成一个新的 access_token 
	 * @param refreshToken refresh_token
	 * @return 新的 access_token 
	 */
	public default AccessTokenModel refreshAccessToken(String refreshToken) {
		// 获取Model信息 
		CodeModel codeModel = getRefreshToken(refreshToken);
		if(codeModel == null) {
			throw new SaTokenException("无效refresh_token");
		}
		// 获取新的 AccessToken 并保存 
		AccessTokenModel tokenModel = converCodeToAccessToken(codeModel);
		tokenModel.setRefreshToken(refreshToken);
		SaTokenManager.getSaTokenDao().setObject(getKeyAccessToken(tokenModel.getAccessToken()), tokenModel, SaOAuth2Manager.getConfig().getAccessTokenTimeout());
		
		// 返回 
		return tokenModel;
	}

	/**
	 * [default] 根据 refresh_token 获得其Model详细信息 (授权码Model) 
	 * @param refreshToken refresh_token 
	 * @return RefreshToken (授权码Model) 
	 */
	public default CodeModel getRefreshToken(String refreshToken) {
		return (CodeModel)SaTokenManager.getSaTokenDao().getObject(getKeyRefreshToken(refreshToken));
	}

	/**
	 * [default] 获取 access_token 的有效期 
	 * @param accessToken access_token 
	 * @return 有效期 
	 */
	public default long getAccessTokenExpiresIn(String accessToken) {
		return SaTokenManager.getSaTokenDao().getObjectTimeout(getKeyAccessToken(accessToken));
	}

	/**
	 * [default] 获取 refresh_token 的有效期 
	 * @param refreshToken refresh_token 
	 * @return 有效期 
	 */
	public default long getRefreshTokenExpiresIn(String refreshToken) {
		return SaTokenManager.getSaTokenDao().getObjectTimeout(getKeyRefreshToken(refreshToken));
	}
	
	/**
	 * [default] 获取 access_token 所代表的LoginId 
	 * @param accessToken access_token 
	 * @return LoginId 
	 */
	public default Object getLoginIdByAccessToken(String accessToken) {
		AccessTokenModel tokenModel = SaOAuth2Util.getAccessToken(accessToken);
		if(tokenModel == null) {
			throw new SaTokenException("无效access_token");
		}
		return getLoginId(tokenModel.getClientId(), tokenModel.getOpenid());
	}
	
	
	// ------------------- 自定义策略相关

	/**
	 * [default] 将指定字符串按照逗号分隔符转化为字符串集合 
	 * @param str 字符串
	 * @return 分割后的字符串集合 
	 */
	public default List<String> convertStringToList(String str) {
		return Arrays.asList(str.split(","));
	}
	
	/**
	 * [default] 生成授权码 
	 * @param clientId 应用id 
	 * @param scope 权限
	 * @param loginId 账号id 
	 * @return 授权码 
	 */
	public default String createCode(String clientId, String scope, Object loginId) {
		return SaTokenInsideUtil.getRandomString(60).toLowerCase();
	}
	
	/**
	 * [default] 生成AccessToken 
	 * @param codeModel CodeModel对象
	 * @return AccessToken 
	 */
	public default String createAccessToken(CodeModel codeModel) {
		return SaTokenInsideUtil.getRandomString(60).toLowerCase();
	}
	
	/**
	 * [default] 生成RefreshToken 
	 * @param codeModel CodeModel对象
	 * @return RefreshToken 
	 */
	public default String createRefreshToken(CodeModel codeModel) {
		return SaTokenInsideUtil.getRandomString(60).toLowerCase();
	}

	/**
	 * [default] 在url上拼接上kv参数并返回 
	 * @param url url
	 * @param parameStr 参数, 例如 id=1001
	 * @return 拼接后的url字符串 
	 */
	public default String splicingParame(String url, String parameStr) {
		// 如果参数为空, 直接返回 
		if(parameStr == null || parameStr.length() == 0) {
			return url;
		}
		int index = url.indexOf('?');
		// ? 不存在
		if(index == -1) {
			return url + '?' + parameStr;
		}
		// ? 是最后一位
		if(index == url.length() - 1) {
			return url + parameStr;
		}
		// ? 是其中一位
		if(index > -1 && index < url.length() - 1) {
			String separatorChar = "&";
			// 如果最后一位是 不是&, 且 arg_str 第一位不是 &, 就增送一个 &
			if(url.lastIndexOf(separatorChar) != url.length() - 1 && parameStr.indexOf(separatorChar) != 0) {
				return url + separatorChar + parameStr;
			} else {
				return url + parameStr;
			}
		}
		// 正常情况下, 代码不可能执行到此 
		return url;
	}

	/**
	 * [default] 将 CodeModel 转换为 AccessTokenModel 
	 * @param codeModel CodeModel对象
	 * @return AccessToken对象 
	 */
	public default AccessTokenModel converCodeToAccessToken(CodeModel codeModel) {
		if(codeModel == null) {
			throw new SaTokenException("无效code");
		}
		AccessTokenModel tokenModel = new AccessTokenModel();
		tokenModel.setAccessToken(createAccessToken(codeModel));
		tokenModel.setRefreshToken(createRefreshToken(codeModel));
		tokenModel.setCode(codeModel.getCode());
		tokenModel.setClientId(codeModel.getClientId());
		tokenModel.setScope(codeModel.getScope());
		tokenModel.setOpenid(getOpenid(codeModel.getClientId(), codeModel.getLoginId()));
		tokenModel.setTag(codeModel.getTag());
		return tokenModel;
	}

	
	// ------------------- 返回相应key 

	/**  
	 * 获取key：授权码持久化使用的key 
	 * @param code 授权码
	 * @return key
	 */
	public default String getKeyCodeModel(String code) {
		return SaTokenManager.getConfig().getTokenName() + ":oauth2:code:" + code;
	}
	
	/**  
	 * 获取key：[Client&账号]最新授权码记录, 持久化使用的key 
	 * @param loginId 账号id
	 * @param clientId 应用id
	 * @return key
	 */
	public default String getKeyClientLoginId(Object loginId, String clientId) {
		return SaTokenManager.getConfig().getTokenName() + ":oauth2:newest-code:" + clientId + ":" + loginId;
	}

	/**  
	 * 获取key：refreshToken持久化使用的key 
	 * @param refreshToken refreshToken
	 * @return key
	 */
	public default String getKeyRefreshToken(String refreshToken) {
		return SaTokenManager.getConfig().getTokenName() + ":oauth2:refresh-token:" + refreshToken;
	}

	/**  
	 * 获取key：accessToken持久化使用的key 
	 * @param accessToken accessToken
	 * @return key
	 */
	public default String getKeyAccessToken(String accessToken) {
		return SaTokenManager.getConfig().getTokenName() + ":oauth2:access-token:" + accessToken;
	}
	
	
}

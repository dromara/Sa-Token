/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.oauth2.template;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts.Param;
import cn.dev33.satoken.oauth2.dao.SaOAuth2Dao;
import cn.dev33.satoken.oauth2.data.model.*;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.HashSet;
import java.util.List;

/**
 * Sa-Token-OAuth2 模块 代码实现
 *
 * @author click33
 * @since 1.23.0
 */
public class SaOAuth2Template {

	// ------------------- 数据加载

	/**
	 * 根据id获取Client信息
	 * @param clientId 应用id
	 * @return ClientModel
	 */
	public SaClientModel getClientModel(String clientId) {
		return SaOAuth2Manager.getDataLoader().getClientModel(clientId);
	}

	/**
	 * 根据ClientId 和 LoginId 获取openid
	 * @param clientId 应用id
	 * @param loginId 账号id
	 * @return 此账号在此Client下的openid
	 */
	public String getOpenid(String clientId, Object loginId) {
		return SaOAuth2Manager.getDataLoader().getOpenid(clientId, loginId);
	}

	// ------------------- 资源校验API
	/**
	 * 根据id获取Client信息, 如果Client为空，则抛出异常
	 * @param clientId 应用id
	 * @return ClientModel
	 */
	public SaClientModel checkClientModel(String clientId) {
		SaClientModel clientModel = getClientModel(clientId);
		if(clientModel == null) {
			throw new SaOAuth2Exception("无效client_id: " + clientId).setCode(SaOAuth2ErrorCode.CODE_30105);
		}
		return clientModel;
	}
	/**
	 * 获取 Access-Token，如果AccessToken为空则抛出异常
	 * @param accessToken .
	 * @return .
	 */
	public AccessTokenModel checkAccessToken(String accessToken) {
		AccessTokenModel at = SaOAuth2Manager.getDao().getAccessToken(accessToken);
		SaOAuth2Exception.throwBy(at == null, "无效access_token：" + accessToken, SaOAuth2ErrorCode.CODE_30106);
		return at;
	}
	/**
	 * 获取 Client-Token，如果ClientToken为空则抛出异常
	 * @param clientToken .
	 * @return .
	 */
	public ClientTokenModel checkClientToken(String clientToken) {
		ClientTokenModel ct = SaOAuth2Manager.getDao().getClientToken(clientToken);
		SaOAuth2Exception.throwBy(ct == null, "无效：client_token" + clientToken, SaOAuth2ErrorCode.CODE_30107);
		return ct;
	}
	/**
	 * 获取 Access-Token 所代表的LoginId
	 * @param accessToken Access-Token
	 * @return LoginId
	 */
	public Object getLoginIdByAccessToken(String accessToken) {
		return checkAccessToken(accessToken).loginId;
	}
	/**
	 * 校验：指定 Access-Token 是否具有指定 Scope
	 * @param accessToken Access-Token
	 * @param scopes 需要校验的权限列表
	 */
	public void checkScope(String accessToken, String... scopes) {
		if(scopes == null || scopes.length == 0) {
			return;
		}
		AccessTokenModel at = checkAccessToken(accessToken);
		List<String> scopeList = at.scopes;
		for (String scope : scopes) {
			SaOAuth2Exception.throwBy( ! scopeList.contains(scope), "该 Access-Token 不具备 Scope：" + scope, SaOAuth2ErrorCode.CODE_30108);
		}
	}
	/**
	 * 校验：指定 Client-Token 是否具有指定 Scope
	 * @param clientToken Client-Token
	 * @param scopes 需要校验的权限列表
	 */
	public void checkClientTokenScope(String clientToken, String... scopes) {
		if(scopes == null || scopes.length == 0) {
			return;
		}
		ClientTokenModel ct = checkClientToken(clientToken);
		List<String> scopeList = ct.scopes;
		for (String scope : scopes) {
			SaOAuth2Exception.throwBy( ! scopeList.contains(scope), "该 Client-Token 不具备 Scope：" + scope, SaOAuth2ErrorCode.CODE_30109);
		}
	}

	// ------------------- generate 构建数据
	/**
	 * 构建Model：请求Model
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
		// 数据解析
		String scope = req.getParam(Param.scope, "");
		ra.scopes = SaOAuth2Manager.getDataConverter().convertScopeStringToList(scope);
		ra.loginId = loginId;
		return ra;
	}
	/**
	 * 构建Model：Code授权码
	 * @param ra 请求参数Model
	 * @return 授权码Model
	 */
	public CodeModel generateCode(RequestAuthModel ra) {

		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 删除旧Code
		String oldCodeValue = SaOAuth2Manager.getDao().getCodeValue(ra.clientId, ra.loginId);
		dao.deleteCode(oldCodeValue);

		// 生成新Code
		String codeValue = randomCode(ra.clientId, ra.loginId, ra.scopes);
		CodeModel cm = new CodeModel(codeValue, ra.clientId, ra.scopes, ra.loginId, ra.redirectUri);

		// 保存新Code
		dao.saveCode(cm);
		dao.saveCodeIndex(cm);

		// 返回
		return cm;
	}
	/**
	 * 构建Model：Access-Token
	 * @param code 授权码Model
	 * @return AccessToken Model
	 */
	public AccessTokenModel generateAccessToken(String code) {

		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 1、先校验
		CodeModel cm = dao.getCode(code);
		SaOAuth2Exception.throwBy(cm == null, "无效code", SaOAuth2ErrorCode.CODE_30110);

		// 2、删除旧Token
		dao.deleteAccessToken(dao.getAccessTokenValue(cm.clientId, cm.loginId));
		dao.deleteRefreshToken(dao.getRefreshTokenValue(cm.clientId, cm.loginId));

		// 3、生成token
		AccessTokenModel at = convertCodeToAccessToken(cm);
		RefreshTokenModel rt = convertAccessTokenToRefreshToken(at);
		at.refreshToken = rt.refreshToken;
		at.refreshExpiresTime = rt.expiresTime;

		// 4、保存token
		dao.saveAccessToken(at);
		dao.saveAccessTokenIndex(at);
		dao.saveRefreshToken(rt);
		dao.saveRefreshTokenIndex(rt);

		// 5、删除此Code
		dao.deleteCode(code);
		dao.deleteCodeIndex(cm.clientId, cm.loginId);

		// 6、返回 Access-Token
		return at;
	}
	/**
	 * 刷新Model：根据 Refresh-Token 生成一个新的 Access-Token
	 * @param refreshToken Refresh-Token值
	 * @return 新的 Access-Token
	 */
	public AccessTokenModel refreshAccessToken(String refreshToken) {

		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 获取 Refresh-Token 信息
		RefreshTokenModel rt = dao.getRefreshToken(refreshToken);
		SaOAuth2Exception.throwBy(rt == null, "无效refresh_token: " + refreshToken, SaOAuth2ErrorCode.CODE_30111);
		
		// 如果配置了[每次刷新产生新的Refresh-Token]
		if(checkClientModel(rt.clientId).getIsNewRefresh()) {
			// 删除旧 Refresh-Token
			dao.deleteRefreshToken(rt.refreshToken);

			// 创建并保持新的 Refresh-Token
			rt = convertRefreshTokenToRefreshToken(rt);
			dao.saveRefreshToken(rt);
			dao.saveRefreshTokenIndex(rt);
		}

		// 删除旧 Access-Token
		dao.deleteAccessToken(dao.getAccessTokenValue(rt.clientId, rt.loginId));

		// 生成新 Access-Token
		AccessTokenModel at = convertRefreshTokenToAccessToken(rt);

		// 保存新 Access-Token
		dao.saveAccessToken(at);
		dao.saveAccessTokenIndex(at);

		// 返回新 Access-Token
		return at;
	}
	/**
	 * 构建Model：Access-Token (根据RequestAuthModel构建，用于隐藏式 and 密码式)
	 * @param ra 请求参数Model
	 * @param isCreateRt 是否生成对应的Refresh-Token
	 * @return Access-Token Model
	 */
	public AccessTokenModel generateAccessToken(RequestAuthModel ra, boolean isCreateRt) {

		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 1、删除 旧Token
		dao.deleteAccessToken(dao.getAccessTokenValue(ra.clientId, ra.loginId));
		if(isCreateRt) {
			dao.deleteRefreshToken(dao.getRefreshTokenValue(ra.clientId, ra.loginId));
		}

		// 2、生成 新Access-Token
		String newAtValue = randomAccessToken(ra.clientId, ra.loginId, ra.scopes);
		AccessTokenModel at = new AccessTokenModel(newAtValue, ra.clientId, ra.loginId, ra.scopes);
		at.openid = getOpenid(ra.clientId, ra.loginId);
		at.expiresTime = System.currentTimeMillis() + (checkClientModel(ra.clientId).getAccessTokenTimeout() * 1000);

		// 3、生成&保存 Refresh-Token
		if(isCreateRt) {
			RefreshTokenModel rt = convertAccessTokenToRefreshToken(at);
			dao.saveRefreshToken(rt);
			dao.saveRefreshTokenIndex(rt);
		}

		// 5、保存 新Access-Token
		dao.saveAccessToken(at);
		dao.saveAccessTokenIndex(at);

		// 6、返回 新Access-Token
		return at;
	}
	/**
	 * 构建Model：Client-Token
	 * @param clientId 应用id
	 * @param scopes 授权范围
	 * @return Client-Token Model
	 */
	public ClientTokenModel generateClientToken(String clientId, List<String> scopes) {

		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 1、删掉旧 Past-Token
		dao.deleteClientToken(dao.getPastTokenValue(clientId));

		// 2、将旧Client-Token 标记为新 Past-Token
		ClientTokenModel oldCt = dao.getClientToken(dao.getClientTokenValue(clientId));
		dao.savePastTokenIndex(oldCt);

		// 2.5、如果配置了 PastClientToken 的 ttl ，则需要更新一下 
		SaClientModel cm = checkClientModel(clientId);
		if(oldCt != null && cm.getPastClientTokenTimeout() != -1) {
			oldCt.expiresTime = System.currentTimeMillis() + (cm.getPastClientTokenTimeout() * 1000);
			dao.saveClientToken(oldCt);
		}

		// 3、生成新Client-Token
		ClientTokenModel ct = new ClientTokenModel(randomClientToken(clientId, scopes), clientId, scopes);
		ct.expiresTime = System.currentTimeMillis() + (cm.getClientTokenTimeout() * 1000);

		// 3、保存新Client-Token 
		dao.saveClientToken(ct);
		dao.saveClientTokenIndex(ct);

		// 4、返回
		return ct;
	}
	/**
	 * 构建URL：下放Code URL (Authorization Code 授权码)
	 * @param redirectUri 下放地址
	 * @param code code参数
	 * @param state state参数
	 * @return 构建完毕的URL
	 */
	public String buildRedirectUri(String redirectUri, String code, String state) {
		String url = SaFoxUtil.joinParam(redirectUri, Param.code, code);
		if( ! SaFoxUtil.isEmpty(state)) {
			url = SaFoxUtil.joinParam(url, Param.state, state);
		}
		return url;
	}
	/**
	 * 构建URL：下放Access-Token URL （implicit 隐藏式）
	 * @param redirectUri 下放地址
	 * @param token token
	 * @param state state参数
	 * @return 构建完毕的URL
	 */
	public String buildImplicitRedirectUri(String redirectUri, String token, String state) {
		String url = SaFoxUtil.joinSharpParam(redirectUri, Param.token, token);
		if( ! SaFoxUtil.isEmpty(state)) {
			url = SaFoxUtil.joinSharpParam(url, Param.state, state);
		}
		return url;
	}
	/**
	 * 回收 Access-Token
	 * @param accessToken Access-Token值
	 */
	public void revokeAccessToken(String accessToken) {

		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 如果查不到任何东西, 直接返回
		AccessTokenModel at = dao.getAccessToken(accessToken);
		if(at == null) {
			return;
		}

		// 删除 Access-Token
		dao.deleteAccessToken(accessToken);
		dao.deleteAccessTokenIndex(at.clientId, at.loginId);

		// 删除对应的 Refresh-Token
		String refreshToken = dao.getRefreshTokenValue(at.clientId, at.loginId);
		dao.deleteRefreshToken(refreshToken);
		dao.deleteRefreshTokenIndex(at.clientId, at.loginId);
	}

	// ------------------- check 数据校验
	/**
	 * 判断：指定 loginId 是否对一个 Client 授权给了指定 Scope
	 * @param loginId 账号id
	 * @param clientId 应用id
	 * @param scopes 权限
	 * @return 是否已经授权
	 */
	public boolean isGrant(Object loginId, String clientId, List<String> scopes) {
		SaOAuth2Dao dao = SaOAuth2Manager.getDao();
		List<String> grantScopeList = dao.getGrantScope(clientId, loginId);
        return scopes.isEmpty() || new HashSet<>(grantScopeList).containsAll(scopes);
	}
	/**
	 * 校验：该Client是否签约了指定的Scope
	 * @param clientId 应用id
	 * @param scopes 权限(多个用逗号隔开)
	 */
	public void checkContract(String clientId, List<String> scopes) {
		List<String> clientScopeList = checkClientModel(clientId).contractScopes;
        if( ! new HashSet<>(clientScopeList).containsAll(scopes)) {
			throw new SaOAuth2Exception("请求的Scope暂未签约").setCode(SaOAuth2ErrorCode.CODE_30112);
		}
	}
	/**
	 * 校验：该Client使用指定url作为回调地址，是否合法
	 * @param clientId 应用id
	 * @param url 指定url
	 */
	public void checkRightUrl(String clientId, String url) {
		// 1、是否是一个有效的url
		if( ! SaFoxUtil.isUrl(url)) {
			throw new SaOAuth2Exception("无效redirect_url：" + url).setCode(SaOAuth2ErrorCode.CODE_30113);
		}

		// 2、截取掉?后面的部分
		int qIndex = url.indexOf("?");
		if(qIndex != -1) {
			url = url.substring(0, qIndex);
		}

		// 3、不允许出现@字符
		if(url.contains("@")) {
			//  为什么不允许出现 @ 字符呢，因为这有可能导致 redirect_url 参数绕过 AllowUrl 列表的校验
			//
			//  举个例子 SaClientModel 配置：
			//       allow-url=http://sa-oauth-client.com*
			//
			//  开发者原意是为了允许 sa-oauth-client.com 下的所有地址都可以下放 code
			//
			//  但是如果攻击者精心构建一个url：
			// 	     http://sa-oauth-server.com:8000/oauth2/authorize?response_type=code&client_id=1001&redirect_uri=http://sa-oauth-client.com@sa-token.cc
			//
			//  那么这个url就会绕过 allow-url 的校验，code 被下发到了第三方服务器地址：
			//       http://sa-token.cc/?code=i8vDfbpqBViMe01QoLY1kHROJWYvv9plBtvTZ6kk77KK0e0U4Xj99NPfSZEYjRul
			//
			//  造成了 code 参数劫持
			//  所以此处需要禁止在 url 中出现 @ 字符
			//
			//  这么一刀切的做法，可能会导致一些特殊的正常url也无法通过校验，例如：
			//       http://sa-oauth-server.com:8000/oauth2/authorize?response_type=code&client_id=1001&redirect_uri=http://sa-oauth-client.com/@getInfo
			//
			//  但是为了安全起见，这么做还是有必要的
			throw new SaOAuth2Exception("无效 redirect_url（不允许出现@字符）：" + url);
		}

		// 4、是否在[允许地址列表]之中
		SaClientModel clientModel = checkClientModel(clientId);
		List<String> allowList = SaOAuth2Manager.getDataConverter().convertAllowUrlStringToList(clientModel.allowUrl);
		checkAllowUrlList(allowList);
		if( ! SaStrategy.instance.hasElement.apply(allowList, url)) {
			throw new SaOAuth2Exception("非法 redirect_url: " + url).setCode(SaOAuth2ErrorCode.CODE_30114);
		}
	}

	/**
	 * 校验配置的 AllowUrl 是否合规，如果不合规则抛出异常
	 * @param allowUrlList 待校验的 allow-url 地址列表
	 */
	public void checkAllowUrlList(List<String> allowUrlList){
		checkAllowUrlListStaticMethod(allowUrlList);
	}

	/**
	 * 校验配置的 AllowUrl 是否合规，如果不合规则抛出异常
	 * @param allowUrlList 待校验的 allow-url 地址列表
	 */
	public static void checkAllowUrlListStaticMethod(List<String> allowUrlList){
		for (String url : allowUrlList) {
			int index = url.indexOf("*");
			// 如果配置了 * 字符，则必须出现在最后一位，否则属于无效配置项
			if(index != -1 && index != url.length() - 1) {
				//  为什么不允许 * 字符出现在中间位置呢，因为这有可能导致 redirect 参数绕过 allow-url 列表的校验
				//
				//  举个例子 SaClientModel 配置：
				//      allow-url=http://*.sa-oauth-client.com/
				//
				//  开发者原意是为了允许 sa-oauth-client.com 下的所有子域名都可以下放 ticket
				//      例如：http://shop.sa-oauth-client.com/
				//
				//  但是如果攻击者精心构建一个url：
				//       http://sa-oauth-server.com:8000/oauth2/authorize?response_type=code&client_id=1001&redirect_uri=http://sa-token.cc/a.sa-oauth-client.com/
				//
				//  那么这个 url 就会绕过 allow-url 的校验，ticket 被下发到了第三方服务器地址：
				//       http://sa-token.cc/a.sa-oauth-client.com/?code=v2KKMUFK7dDsMMzXLQ3aWGsyGUjrA0dBB2jeOWrpCnC8b5ScmXXQSv20mIwPK7Cx
				//
				//  造成了 ticket 参数劫持
				//  所以此处需要禁止 allow-url 配置项的中间位置出现 * 字符（出现在末尾是没有问题的）
				//
				//  这么一刀切的做法，可能会导致正常场景下的子域名url也无法通过校验，例如：
				//       http://sa-oauth-server.com:8000/oauth2/authorize?response_type=code&client_id=1001&redirect_uri=http://shop.sa-oauth2-client.com/
				//
				//  但是为了安全起见，这么做还是有必要的
				throw new SaOAuth2Exception("无效的 allow-url 配置（*通配符只允许出现在最后一位）：" + url);
			}
		}
	}

	/**
	 * 校验：clientId 与 clientSecret 是否正确
	 * @param clientId 应用id
	 * @param clientSecret 秘钥
	 * @return SaClientModel对象
	 */
	public SaClientModel checkClientSecret(String clientId, String clientSecret) {
		SaClientModel cm = checkClientModel(clientId);
		SaOAuth2Exception.throwBy(cm.clientSecret == null || ! cm.clientSecret.equals(clientSecret),
				"无效client_secret: " + clientSecret, SaOAuth2ErrorCode.CODE_30115);
		return cm;
	}
	/**
	 * 校验：clientId 与 clientSecret 是否正确，并且是否签约了指定 scopes 
	 * @param clientId 应用id
	 * @param clientSecret 秘钥
	 * @param scopes 权限
	 * @return SaClientModel对象
	 */
	public SaClientModel checkClientSecretAndScope(String clientId, String clientSecret, List<String> scopes) {
		// 先校验 clientSecret
		SaClientModel cm = checkClientSecret(clientId, clientSecret);
		// 再校验 是否签约 
		List<String> clientScopeList = cm.contractScopes;
        if( ! new HashSet<>(clientScopeList).containsAll(scopes)) {
			throw new SaOAuth2Exception("请求的Scope暂未签约").setCode(SaOAuth2ErrorCode.CODE_30116);
		}
		// 返回数据
		return cm;
	}
	/**
	 * 校验：使用 code 获取 token 时提供的参数校验
	 * @param code 授权码
	 * @param clientId 应用id
	 * @param clientSecret 秘钥
	 * @param redirectUri 重定向地址
	 * @return CodeModel对象
	 */
	public CodeModel checkGainTokenParam(String code, String clientId, String clientSecret, String redirectUri) {

		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 校验：Code是否存在
		CodeModel cm = dao.getCode(code);
		SaOAuth2Exception.throwBy(cm == null, "无效code: " + code, SaOAuth2ErrorCode.CODE_30117);

		// 校验：ClientId是否一致
		SaOAuth2Exception.throwBy( ! cm.clientId.equals(clientId), "无效client_id: " + clientId, SaOAuth2ErrorCode.CODE_30118);

		// 校验：Secret是否正确
		String dbSecret = checkClientModel(clientId).clientSecret;
		SaOAuth2Exception.throwBy(dbSecret == null || ! dbSecret.equals(clientSecret), "无效client_secret: " + clientSecret, SaOAuth2ErrorCode.CODE_30119);

		// 如果提供了redirectUri，则校验其是否与请求Code时提供的一致
		if( ! SaFoxUtil.isEmpty(redirectUri)) {
			SaOAuth2Exception.throwBy( ! redirectUri.equals(cm.redirectUri), "无效redirect_uri: " + redirectUri, SaOAuth2ErrorCode.CODE_30120);
		}

		// 返回CodeModel
		return cm;
	}
	/**
	 * 校验：使用 Refresh-Token 刷新 Access-Token 时提供的参数校验
	 * @param clientId 应用id
	 * @param clientSecret 秘钥
	 * @param refreshToken Refresh-Token
	 * @return CodeModel对象
	 */
	public RefreshTokenModel checkRefreshTokenParam(String clientId, String clientSecret, String refreshToken) {

		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 校验：Refresh-Token是否存在
		RefreshTokenModel rt = dao.getRefreshToken(refreshToken);
		SaOAuth2Exception.throwBy(rt == null, "无效refresh_token: " + refreshToken, SaOAuth2ErrorCode.CODE_30121);

		// 校验：ClientId是否一致
		SaOAuth2Exception.throwBy( ! rt.clientId.equals(clientId), "无效client_id: " + clientId, SaOAuth2ErrorCode.CODE_30122);

		// 校验：Secret是否正确
		String dbSecret = checkClientModel(clientId).clientSecret;
		SaOAuth2Exception.throwBy(dbSecret == null || ! dbSecret.equals(clientSecret), "无效client_secret: " + clientSecret, SaOAuth2ErrorCode.CODE_30123);

		// 返回Refresh-Token
		return rt;
	}
	/**
	 * 校验：Access-Token、clientId、clientSecret 三者是否匹配成功
	 * @param clientId 应用id
	 * @param clientSecret 秘钥
	 * @param accessToken Access-Token
	 * @return SaClientModel对象
	 */
	public AccessTokenModel checkAccessTokenParam(String clientId, String clientSecret, String accessToken) {
		AccessTokenModel at = checkAccessToken(accessToken);
		SaOAuth2Exception.throwBy( ! at.clientId.equals(clientId), "无效client_id：" + clientId, SaOAuth2ErrorCode.CODE_30124);
		checkClientSecret(clientId, clientSecret);
		return at;
	}

	// ------------------- convert 数据转换
	/**
	 * 将 Code 转换为 Access-Token
	 * @param cm CodeModel对象
	 * @return AccessToken对象
	 */
	public AccessTokenModel convertCodeToAccessToken(CodeModel cm) {
		AccessTokenModel at = new AccessTokenModel();
		at.accessToken = randomAccessToken(cm.clientId, cm.loginId, cm.scopes);
		// at.refreshToken = randomRefreshToken(cm.clientId, cm.loginId, cm.scope);
		at.clientId = cm.clientId;
		at.loginId = cm.loginId;
		at.scopes = cm.scopes;
		at.openid = getOpenid(cm.clientId, cm.loginId);
		at.expiresTime = System.currentTimeMillis() + (checkClientModel(cm.clientId).getAccessTokenTimeout() * 1000);
		// at.refreshExpiresTime = System.currentTimeMillis() + (checkClientModel(cm.clientId).getRefreshTokenTimeout() * 1000);
		return at;
	}
	/**
	 * 将 Access-Token 转换为 Refresh-Token
	 * @param at .
	 * @return .
	 */
	public RefreshTokenModel convertAccessTokenToRefreshToken(AccessTokenModel at) {
		RefreshTokenModel rt = new RefreshTokenModel();
		rt.refreshToken = randomRefreshToken(at.clientId, at.loginId, at.scopes);
		rt.clientId = at.clientId;
		rt.loginId = at.loginId;
		rt.scopes = at.scopes;
		rt.openid = at.openid;
		rt.expiresTime = System.currentTimeMillis() + (checkClientModel(at.clientId).getRefreshTokenTimeout() * 1000);
		// 改变at属性
		at.refreshToken = rt.refreshToken;
		at.refreshExpiresTime = rt.expiresTime;
		return rt;
	}
	/**
	 * 将 Refresh-Token 转换为 Access-Token
	 * @param rt .
	 * @return .
	 */
	public AccessTokenModel convertRefreshTokenToAccessToken(RefreshTokenModel rt) {
		AccessTokenModel at = new AccessTokenModel();
		at.accessToken = randomAccessToken(rt.clientId, rt.loginId, rt.scopes);
		at.refreshToken = rt.refreshToken;
		at.clientId = rt.clientId;
		at.loginId = rt.loginId;
		at.scopes = rt.scopes;
		at.openid = rt.openid;
		at.expiresTime = System.currentTimeMillis() + (checkClientModel(rt.clientId).getAccessTokenTimeout() * 1000);
		at.refreshExpiresTime = rt.expiresTime;
		return at;
	}
	/**
	 * 根据 Refresh-Token 创建一个新的 Refresh-Token
	 * @param rt .
	 * @return .
	 */
	public RefreshTokenModel convertRefreshTokenToRefreshToken(RefreshTokenModel rt) {
		RefreshTokenModel newRt = new RefreshTokenModel();
		newRt.refreshToken = randomRefreshToken(rt.clientId, rt.loginId, rt.scopes);
		newRt.expiresTime = System.currentTimeMillis() + (checkClientModel(rt.clientId).getRefreshTokenTimeout() * 1000);
		newRt.clientId = rt.clientId;
		newRt.scopes = rt.scopes;
		newRt.loginId = rt.loginId;
		newRt.openid = rt.openid;
		return newRt;
	}

	// ------------------- Random数据
	/**
	 * 随机一个 Code
	 * @param clientId 应用id
	 * @param loginId 账号id
	 * @param scopes 权限
	 * @return Code
	 */
	public String randomCode(String clientId, Object loginId, List<String> scopes) {
		return SaFoxUtil.getRandomString(60);
	}
	/**
	 * 随机一个 Access-Token
	 * @param clientId 应用id
	 * @param loginId 账号id
	 * @param scopes 权限
	 * @return Access-Token
	 */
	public String randomAccessToken(String clientId, Object loginId, List<String> scopes) {
		return SaFoxUtil.getRandomString(60);
	}
	/**
	 * 随机一个 Refresh-Token
	 * @param clientId 应用id
	 * @param loginId 账号id
	 * @param scopes 权限
	 * @return Refresh-Token
	 */
	public String randomRefreshToken(String clientId, Object loginId, List<String> scopes) {
		return SaFoxUtil.getRandomString(60);
	}
	/**
	 * 随机一个 Client-Token
	 * @param clientId 应用id
	 * @param scopes 权限
	 * @return Client-Token
	 */
	public String randomClientToken(String clientId, List<String> scopes) {
		return SaFoxUtil.getRandomString(60);
	}


	// ------------------- 包装其它 bean 的方法

	/**
	 * 获取：Access-Token Model
	 * @param accessToken /
	 * @return /
	 */
	public AccessTokenModel getAccessToken(String accessToken) {
		return SaOAuth2Manager.getDao().getAccessToken(accessToken);
	}

	/**
	 * 持久化：用户授权记录
	 * @param clientId 应用id
	 * @param loginId 账号id
	 * @param scopes 权限列表
	 */
	public void saveGrantScope(String clientId, Object loginId, List<String> scopes) {
		SaOAuth2Manager.getDao().saveGrantScope(clientId, loginId, scopes);
	}





}

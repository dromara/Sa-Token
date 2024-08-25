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

import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.dao.SaOAuth2Dao;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.RefreshTokenModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.*;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.List;

/**
 * Sa-Token-OAuth2 模块 代码实现
 *
 * @author click33
 * @since 1.23.0
 */
public class SaOAuth2Template {

	// ----------------- ClientModel 相关 -----------------

	/**
	 * 获取 ClientModel，根据 clientId
	 *
	 * @param clientId /
	 * @return /
	 */
	public SaClientModel getClientModel(String clientId) {
		return SaOAuth2Manager.getDataLoader().getClientModel(clientId);
	}

	/**
	 * 校验 clientId 信息并返回 ClientModel，如果找不到对应 Client 信息则抛出异常
	 * @param clientId /
	 * @return /
	 */
	public SaClientModel checkClientModel(String clientId) {
		SaClientModel clientModel = getClientModel(clientId);
		if(clientModel == null) {
			throw new SaOAuth2ClientModelException("无效 client_id: " + clientId)
					.setClientId(clientId)
					.setCode(SaOAuth2ErrorCode.CODE_30105);
		}
		return clientModel;
	}

	/**
	 * 校验：clientId 与 clientSecret 是否正确
	 * @param clientId 应用id
	 * @param clientSecret 秘钥
	 * @return SaClientModel对象
	 */
	public SaClientModel checkClientSecret(String clientId, String clientSecret) {
		SaClientModel cm = checkClientModel(clientId);
		if(cm.clientSecret == null || ! cm.clientSecret.equals(clientSecret)) {
			throw new SaOAuth2ClientModelException("无效 client_secret: " + clientSecret)
					.setClientId(clientId)
					.setCode(SaOAuth2ErrorCode.CODE_30115);
		}
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
		SaClientModel cm = checkClientSecret(clientId, clientSecret);
		checkContractScope(cm, scopes);
		return cm;
	}

	/**
	 * 判断：该 Client 是否签约了指定的 Scope，返回 true 或 false
	 * @param clientId 应用id
	 * @param scopes 权限
	 * @return /
	 */
	public boolean isContractScope(String clientId, List<String> scopes) {
		try {
			checkContractScope(clientId, scopes);
			return true;
		} catch (SaOAuth2ClientModelException e) {
			return false;
		}
	}

	/**
	 * 校验：该 Client 是否签约了指定的 Scope，如果没有则抛出异常
	 * @param clientId 应用id
	 * @param scopes 权限列表
	 * @return /
	 */
	public SaClientModel checkContractScope(String clientId, List<String> scopes) {
		return checkContractScope(checkClientModel(clientId), scopes);
	}

	/**
	 * 校验：该 Client 是否签约了指定的 Scope，如果没有则抛出异常
	 * @param cm 应用
	 * @param scopes 权限列表
	 * @return /
	 */
	public SaClientModel checkContractScope(SaClientModel cm, List<String> scopes) {
		if(SaFoxUtil.isEmptyList(scopes)) {
			return cm;
		}
		for (String scope : scopes) {
			if(! cm.contractScopes.contains(scope)) {
				throw new SaOAuth2ClientModelScopeException("该 client 暂未签约 scope: " + scope)
						.setClientId(cm.clientId)
						.setScope(scope)
						.setCode(SaOAuth2ErrorCode.CODE_30112);
			}
		}
		return cm;
	}

	// --------- redirect_uri 相关

	/**
	 * 校验：该 Client 使用指定 url 作为回调地址，是否合法
	 * @param clientId 应用id
	 * @param url 指定url
	 */
	public void checkRedirectUri(String clientId, String url) {
		// 1、是否是一个有效的url
		if( ! SaFoxUtil.isUrl(url)) {
			throw new SaOAuth2ClientModelException("无效 redirect_url：" + url)
					.setClientId(clientId)
					.setCode(SaOAuth2ErrorCode.CODE_30113);
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
			throw new SaOAuth2ClientModelException("无效 redirect_url（不允许出现@字符）：" + url)
					.setClientId(clientId)
					.setCode(SaOAuth2ErrorCode.CODE_30113);
		}

		// 4、是否在[允许地址列表]之中
		SaClientModel clientModel = checkClientModel(clientId);
		checkRedirectUriListNormal(clientModel.allowRedirectUris);
		if( ! SaStrategy.instance.hasElement.apply(clientModel.allowRedirectUris, url)) {
			throw new SaOAuth2ClientModelException("非法 redirect_url: " + url)
					.setClientId(clientId)
					.setCode(SaOAuth2ErrorCode.CODE_30114);
		}
	}

	/**
	 * 校验配置的 allowRedirectUris 是否合规，如果不合规则抛出异常
	 * @param redirectUriList 待校验的 allow-url 地址列表
	 */
	public void checkRedirectUriListNormal(List<String> redirectUriList){
		checkRedirectUriListNormalStaticMethod(redirectUriList);
	}

	/**
	 * 校验配置的 allowRedirectUris 是否合规，如果不合规则抛出异常，静态方法内部实现
	 * @param redirectUriList 待校验的 allow-url 地址列表
	 */
	public static void checkRedirectUriListNormalStaticMethod(List<String> redirectUriList){
		for (String url : redirectUriList) {
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
				throw new SaOAuth2Exception("无效的 allow-url 配置（*通配符只允许出现在最后一位）：" + url)
						.setCode(SaOAuth2ErrorCode.CODE_30114);
			}
		}
	}

	// --------- 授权相关

	/**
	 * 判断：指定 loginId 是否对一个 Client 授权给了指定 Scope
	 * @param loginId 账号id
	 * @param clientId 应用id
	 * @param scopes 权限
	 * @return 是否已经授权
	 */
	public boolean isGrantScope(Object loginId, String clientId, List<String> scopes) {
		List<String> grantScopeList = SaOAuth2Manager.getDao().getGrantScope(clientId, loginId);
		return SaFoxUtil.list1ContainList2AllElement(grantScopeList, scopes);
	}

	/**
	 * 判断：指定 loginId 在指定 Client 请求指定 Scope 时，是否需要手动确认授权
	 * @param loginId 账号id
	 * @param clientId 应用id
	 * @param scopes 权限
	 * @return 是否已经授权
	 */
	public boolean isNeedCarefulConfirm(Object loginId, String clientId, List<String> scopes) {
		// 如果请求的权限为空，则不需要确认
		if(scopes == null || scopes.isEmpty()) {
			return false;
		}

		// 如果包含高级权限，则必须手动确认授权
		List<String> higherScopeList = getHigherScopeList();
		if(SaFoxUtil.list1ContainList2AnyElement(scopes, higherScopeList)) {
			return true;
		}

		// 如果包含低级权限，则先将低级权限剔除掉
		List<String> lowerScopeList = getLowerScopeList();
		scopes = SaFoxUtil.list1RemoveByList2(scopes, lowerScopeList);

		// 如果剔除后的权限为空，则不需要确认
		if(scopes.isEmpty()) {
			return false;
		}

		// 根据近期授权记录，判断是否需要确认
		return !isGrantScope(loginId, clientId, scopes);
	}


	// --------- 请求数据校验相关

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
		SaOAuth2AuthorizationCodeException.throwBy(cm == null, "无效 code: " + code, code, SaOAuth2ErrorCode.CODE_30110);

		// 校验：ClientId是否一致
		SaOAuth2ClientModelException.throwBy( ! cm.clientId.equals(clientId), "无效 client_id: " + clientId, clientId, SaOAuth2ErrorCode.CODE_30105);

		// 校验：Secret是否正确
		String dbSecret = checkClientModel(clientId).clientSecret;
		SaOAuth2ClientModelException.throwBy(dbSecret == null || ! dbSecret.equals(clientSecret), "无效 client_secret: " + clientSecret, clientId, SaOAuth2ErrorCode.CODE_30115);

		// 如果提供了redirectUri，则校验其是否与请求Code时提供的一致
		if( ! SaFoxUtil.isEmpty(redirectUri)) {
			SaOAuth2ClientModelException.throwBy( ! redirectUri.equals(cm.redirectUri), "无效 redirect_uri: " + redirectUri, clientId, SaOAuth2ErrorCode.CODE_30120);
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
		SaOAuth2RefreshTokenException.throwBy(rt == null, "无效 refresh_token: " + refreshToken, refreshToken, SaOAuth2ErrorCode.CODE_30111);

		// 校验：ClientId是否一致
		SaOAuth2ClientModelException.throwBy( ! rt.clientId.equals(clientId), "无效 client_id: " + clientId, clientId, SaOAuth2ErrorCode.CODE_30122);

		// 校验：Secret是否正确
		String dbSecret = checkClientModel(clientId).clientSecret;
		SaOAuth2ClientModelException.throwBy(dbSecret == null || ! dbSecret.equals(clientSecret), "无效 client_secret: " + clientSecret,
				clientId, SaOAuth2ErrorCode.CODE_30115);

		// 返回 Refresh-Token
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
		SaOAuth2ClientModelException.throwBy( ! at.clientId.equals(clientId), "无效 client_id：" + clientId, clientId, SaOAuth2ErrorCode.CODE_30122);
		checkClientSecret(clientId, clientSecret);
		return at;
	}


	// ----------------- Access-Token 相关 -----------------

	/**
	 * 获取 AccessTokenModel，无效的 AccessToken 会返回 null
	 * @param accessToken /
	 * @return /
	 */
	public AccessTokenModel getAccessToken(String accessToken) {
		return SaOAuth2Manager.getDao().getAccessToken(accessToken);
	}

	/**
	 * 校验 Access-Token，成功返回 AccessTokenModel，失败则抛出异常
	 * @param accessToken /
	 * @return /
	 */
	public AccessTokenModel checkAccessToken(String accessToken) {
		AccessTokenModel at = SaOAuth2Manager.getDao().getAccessToken(accessToken);
		if(at == null) {
			throw new SaOAuth2AccessTokenException("无效 access_token: " + accessToken)
					.setAccessToken(accessToken)
					.setCode(SaOAuth2ErrorCode.CODE_30106);
		}
		return at;
	}

	/**
	 * 获取 Access-Token，根据索引： clientId、loginId
	 * @param clientId /
	 * @param loginId /
	 * @return /
	 */
	public String getAccessTokenValue(String clientId, Object loginId) {
		return SaOAuth2Manager.getDao().getAccessTokenValue(clientId, loginId);
	}

	/**
	 * 判断：指定 Access-Token 是否具有指定 Scope 列表，返回 true 或 false
	 * @param accessToken Access-Token
	 * @param scopes 需要校验的权限列表
	 */
	public boolean hasAccessTokenScope(String accessToken, String... scopes) {
		try {
			checkAccessTokenScope(accessToken, scopes);
			return true;
		} catch (SaOAuth2AccessTokenException e) {
			return false;
		}
	}

	/**
	 * 校验：指定 Access-Token 是否具有指定 Scope 列表，如果不具备则抛出异常
	 * @param accessToken Access-Token
	 * @param scopes 需要校验的权限列表
	 */
	public void checkAccessTokenScope(String accessToken, String... scopes) {
		AccessTokenModel at = checkAccessToken(accessToken);
		if(SaFoxUtil.isEmptyArray(scopes)) {
			return;
		}
		for (String scope : scopes) {
			if(! at.scopes.contains(scope)) {
				throw new SaOAuth2AccessTokenScopeException("该 access_token 不具备 scope：" + scope)
						.setAccessToken(accessToken)
						.setScope(scope)
						.setCode(SaOAuth2ErrorCode.CODE_30108);
			}
		}
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
	 * 获取 Access-Token 所代表的 clientId
	 * @param accessToken Access-Token
	 * @return LoginId
	 */
	public Object getClientIdByAccessToken(String accessToken) {
		return checkAccessToken(accessToken).clientId;
	}

	/**
	 * 回收 Access-Token
	 * @param accessToken Access-Token值
	 */
	public void revokeAccessToken(String accessToken) {
		AccessTokenModel at = getAccessToken(accessToken);
		if(at == null) {
			return;
		}

		// 删 at、索引
		SaOAuth2Dao dao = SaOAuth2Manager.getDao();
		dao.deleteAccessToken(accessToken);
		dao.deleteAccessTokenIndex(at.clientId, at.loginId);
	}

	/**
	 * 回收 Access-Token，根据索引： clientId、loginId
	 * @param clientId /
	 * @param loginId /
	 */
	public void revokeAccessTokenByIndex(String clientId, Object loginId) {
		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 删 at、删索引
		String accessToken = getAccessTokenValue(clientId, loginId);
		if(accessToken != null) {
			dao.deleteAccessToken(accessToken);
			dao.deleteAccessTokenIndex(clientId, loginId);
		}
	}


	// ----------------- Refresh-Token 相关 -----------------

	/**
	 * 获取 RefreshTokenModel，无效的 RefreshToken 会返回 null
	 * @param refreshToken /
	 * @return /
	 */
	public RefreshTokenModel getRefreshToken(String refreshToken) {
		return SaOAuth2Manager.getDao().getRefreshToken(refreshToken);
	}

	/**
	 * 校验 Refresh-Token，成功返回 RefreshTokenModel，失败则抛出异常
	 * @param refreshToken /
	 * @return /
	 */
	public RefreshTokenModel checkRefreshToken(String refreshToken) {
		RefreshTokenModel rt = SaOAuth2Manager.getDao().getRefreshToken(refreshToken);
		if(rt == null) {
			throw new SaOAuth2RefreshTokenException("无效 refresh_token: " + refreshToken)
					.setRefreshToken(refreshToken)
					.setCode(SaOAuth2ErrorCode.CODE_30111);
		}
		return rt;
	}

	/**
	 * 获取 Refresh-Token，根据索引： clientId、loginId
	 * @param clientId /
	 * @param loginId /
	 * @return /
	 */
	public String getRefreshTokenValue(String clientId, Object loginId) {
		return SaOAuth2Manager.getDao().getRefreshTokenValue(clientId, loginId);
	}

	/**
	 * 根据 RefreshToken 刷新出一个 AccessToken
	 * @param refreshToken /
	 * @return /
	 */
	public AccessTokenModel refreshAccessToken(String refreshToken) {
		return SaOAuth2Manager.getDataGenerate().refreshAccessToken(refreshToken);
	}


	// ----------------- Client-Token 相关 -----------------

	/**
	 * 获取 ClientTokenModel，无效的 ClientToken 会返回 null
	 * @param clientToken /
	 * @return /
	 */
	public ClientTokenModel getClientToken(String clientToken) {
		return SaOAuth2Manager.getDao().getClientToken(clientToken);
	}

	/**
	 * 校验 Client-Token，成功返回 ClientTokenModel，失败则抛出异常
	 * @param clientToken /
	 * @return /
	 */
	public ClientTokenModel checkClientToken(String clientToken) {
		ClientTokenModel ct = getClientToken(clientToken);
		if(ct == null) {
			throw new SaOAuth2ClientTokenException("无效 client_token: " + clientToken)
					.setClientToken(clientToken)
					.setCode(SaOAuth2ErrorCode.CODE_30107);
		}
		return ct;
	}

	/**
	 * 获取 ClientToken，根据索引： clientId
	 * @param clientId /
	 * @return /
	 */
	public String getClientTokenValue(String clientId) {
		return SaOAuth2Manager.getDao().getClientTokenValue(clientId);
	}

	/**
	 * 判断：指定 Client-Token 是否具有指定 Scope 列表，返回 true 或 false
	 * @param clientToken Client-Token
	 * @param scopes 需要校验的权限列表
	 */
	public boolean hasClientTokenScope(String clientToken, String... scopes) {
		try {
			checkClientTokenScope(clientToken, scopes);
			return true;
		} catch (SaOAuth2ClientTokenException e) {
			return false;
		}
	}

	/**
	 * 校验：指定 Client-Token 是否具有指定 Scope 列表，如果不具备则抛出异常
	 * @param clientToken Client-Token
	 * @param scopes 需要校验的权限列表
	 */
	public void checkClientTokenScope(String clientToken, String... scopes) {
		ClientTokenModel ct = checkClientToken(clientToken);
		if(SaFoxUtil.isEmptyArray(scopes)) {
			return;
		}
		for (String scope : scopes) {
			if(! ct.scopes.contains(scope)) {
				throw new SaOAuth2ClientTokenScopeException("该 client_token 不具备 scope：" + scope)
						.setClientToken(clientToken)
						.setScope(scope)
						.setCode(SaOAuth2ErrorCode.CODE_30109);
			}
		}
	}

	/**
	 * 回收 ClientToken
	 *
	 * @param clientToken /
	 */
	public void revokeClientToken(String clientToken) {
		ClientTokenModel ct = getClientToken(clientToken);
		if(ct == null) {
			return;
		}
		// 删 ct、删索引
		SaOAuth2Dao dao = SaOAuth2Manager.getDao();
		dao.deleteClientToken(clientToken);
		dao.deleteClientTokenIndex(ct.clientId);
	}

	/**
	 * 回收 ClientToken，根据索引： clientId
	 *
	 * @param clientId /
	 */
	public void revokeClientTokenByIndex(String clientId) {
		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 删 clientToken
		String clientToken = getClientTokenValue(clientId);
		if(clientToken != null) {
			dao.deleteClientToken(clientToken);
			dao.deleteClientTokenIndex(clientId);
		}
	}

	/**
	 * 回收 Lower-Client-Token，根据索引： clientId
	 *
	 * @param clientId /
	 */
	public void revokeLowerClientTokenByIndex(String clientId) {
		SaOAuth2Dao dao = SaOAuth2Manager.getDao();
		// 删 Lower-Client-Token
		String lowerClientToken = dao.getLowerClientTokenValue(clientId);
		if(lowerClientToken != null) {
			dao.deleteLowerClientToken(lowerClientToken);
			dao.deleteLowerClientTokenIndex(clientId);
		}
	}


	// ----------------- 包装其它 bean 的方法 -----------------

	/**
	 * 持久化：用户授权记录
	 * @param clientId 应用id
	 * @param loginId 账号id
	 * @param scopes 权限列表
	 */
	public void saveGrantScope(String clientId, Object loginId, List<String> scopes) {
		SaOAuth2Manager.getDao().saveGrantScope(clientId, loginId, scopes);
	}

	/**
	 * 获取高级权限列表
	 * @return /
	 */
	public List<String> getHigherScopeList() {
		String higherScope = SaOAuth2Manager.getServerConfig().getHigherScope();
		return SaOAuth2Manager.getDataConverter().convertScopeStringToList(higherScope);
	}

	/**
	 * 获取低级权限列表
	 * @return /
	 */
	public List<String> getLowerScopeList() {
		String lowerScope = SaOAuth2Manager.getServerConfig().getLowerScope();
		return SaOAuth2Manager.getDataConverter().convertScopeStringToList(lowerScope);
	}

}

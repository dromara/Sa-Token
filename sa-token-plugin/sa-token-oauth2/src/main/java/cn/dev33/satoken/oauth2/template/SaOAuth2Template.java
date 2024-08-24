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
		return !isGrant(loginId, clientId, scopes);
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
		checkAllowUrlList(clientModel.allowRedirectUris);
		if( ! SaStrategy.instance.hasElement.apply(clientModel.allowRedirectUris, url)) {
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


	/**
	 * 回收指定的 ClientToken
	 *
	 * @param clientToken /
	 */
	public void revokeClientToken(String clientToken) {
		SaOAuth2Dao dao = SaOAuth2Manager.getDao();
		ClientTokenModel ctModel = dao.getClientToken(clientToken);
		if(ctModel == null) {
			return;
		}
		// 删 ct、索引
		dao.deleteClientToken(clientToken);
		dao.deleteClientTokenIndex(ctModel.clientId);
	}

	/**
	 * 回收指定的 ClientToken，根据索引： clientId
	 *
	 * @param clientId /
	 */
	public void revokeClientTokenByIndex(String clientId) {
		SaOAuth2Dao dao = SaOAuth2Manager.getDao();

		// 删 clientToken
		String clientToken = dao.getClientTokenValue(clientId);
		if(clientToken != null) {
			dao.deleteClientToken(clientToken);
			dao.deleteClientTokenIndex(clientId);
		}

		// 删 pastToken
		String pastToken = dao.getPastTokenValue(clientId);
		if(pastToken != null) {
			dao.deletePastToken(pastToken);
			dao.deletePastTokenIndex(clientId);
		}

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

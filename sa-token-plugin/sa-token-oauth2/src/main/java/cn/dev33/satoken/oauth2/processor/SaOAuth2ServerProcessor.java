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
package cn.dev33.satoken.oauth2.processor;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts.Api;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts.GrantType;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts.Param;
import cn.dev33.satoken.oauth2.consts.SaOAuth2Consts.ResponseType;
import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerate;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.data.model.CodeModel;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.data.model.request.ClientIdAndSecretModel;
import cn.dev33.satoken.oauth2.data.model.request.RequestAuthModel;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.template.SaOAuth2Template;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

import java.util.List;

/**
 * Sa-Token OAuth2 请求处理器
 *
 * @author click33
 * @since 1.23.0
 */
public class SaOAuth2ServerProcessor {

	/**
	 * 全局默认实例
	 */
	public static SaOAuth2ServerProcessor instance = new SaOAuth2ServerProcessor();

	/**
	 * 底层 SaOAuth2Template 对象
	 */
	public SaOAuth2Template oauth2Template =  new SaOAuth2Template();

	/**
	 * 处理 Server 端请求， 路由分发
	 * @return 处理结果
	 */
	public Object dister() {

		// 获取变量
		SaRequest req = SaHolder.getRequest();
		SaOAuth2Config cfg = SaOAuth2Manager.getConfig();

		// ------------------ 路由分发 ------------------

		// 模式一：Code授权码 || 模式二：隐藏式
		if(req.isPath(Api.authorize)) {
			return authorize();
		}

		// Code 换 Access-Token || 模式三：密码式
		if(req.isPath(Api.token)) {
			return tokenOrPassword();
		}

		// Refresh-Token 刷新 Access-Token
		if(req.isPath(Api.refresh)) {
			return refresh();
		}

		// 回收 Access-Token
		if(req.isPath(Api.revoke)) {
			return revoke();
		}

		// doLogin 登录接口
		if(req.isPath(Api.doLogin)) {
			return doLogin();
		}

		// doConfirm 确认授权接口
		if(req.isPath(Api.doConfirm)) {
			return doConfirm();
		}

		// 模式四：凭证式
		if(req.isPath(Api.client_token)) {
			return clientToken();
		}

		// 默认返回
		return SaOAuth2Consts.NOT_HANDLE;
	}

	/**
	 * 模式一：Code授权码 / 模式二：隐藏式
	 * @return 处理结果
	 */
	public Object authorize() {

		// 获取变量
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaOAuth2Config cfg = SaOAuth2Manager.getConfig();
		SaOAuth2DataGenerate dataGenerate = SaOAuth2Manager.getDataGenerate();
		String responseType = req.getParamNotNull(Param.response_type);

		// 1、先判断是否开启了指定的授权模式
		checkAuthorizeResponseType(responseType, req, cfg);

		// 2、如果尚未登录, 则先去登录
		if( ! getStpLogic().isLogin()) {
			return cfg.notLoginView.get();
		}

		// 3、构建请求 Model
		RequestAuthModel ra = SaOAuth2Manager.getDataResolver().readRequestAuthModel(req, getStpLogic().getLoginId());

		// 4、校验：重定向域名是否合法
		oauth2Template.checkRightUrl(ra.clientId, ra.redirectUri);

		// 5、校验：此次申请的Scope，该Client是否已经签约
		oauth2Template.checkContract(ra.clientId, ra.scopes);

		// 6、判断：如果此次申请的Scope，该用户尚未授权，则转到授权页面
		boolean isNeedCarefulConfirm = oauth2Template.isNeedCarefulConfirm(ra.loginId, ra.clientId, ra.scopes);
		if(isNeedCarefulConfirm) {
			return cfg.confirmView.apply(ra.clientId, ra.scopes);
		}

		// 7、判断授权类型，重定向到不同地址
		// 		如果是 授权码式，则：开始重定向授权，下放code
		if(ResponseType.code.equals(ra.responseType)) {
			CodeModel codeModel = dataGenerate.generateCode(ra);
			String redirectUri = dataGenerate.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);
			return res.redirect(redirectUri);
		}
		
		// 		如果是 隐藏式，则：开始重定向授权，下放 token
		if(ResponseType.token.equals(ra.responseType)) {
			AccessTokenModel at = dataGenerate.generateAccessToken(ra, false);
			String redirectUri = dataGenerate.buildImplicitRedirectUri(ra.redirectUri, at.accessToken, ra.state);
			return res.redirect(redirectUri);
		}

		// 默认返回
		throw new SaOAuth2Exception("无效response_type: " + ra.responseType).setCode(SaOAuth2ErrorCode.CODE_30125);
	}

	/**
	 * Code 换 Access-Token || 模式三：密码式
	 * @return 处理结果
	 */
	public Object tokenOrPassword() {

		String grantType = SaHolder.getRequest().getParamNotNull(Param.grant_type);

		// Code 换 Access-Token
		if(grantType.equals(GrantType.authorization_code)) {
			return token();
		}

		// 模式三：密码式
		if(grantType.equals(GrantType.password)) {
			SaOAuth2Config cfg = SaOAuth2Manager.getConfig();
			if(!cfg.enablePassword) {
				throwErrorSystemNotEnableModel();
			}
			if(!currClientModel().enablePassword) {
				throwErrorClientNotEnableModel();
			}
			return password();
		}

		throw new SaOAuth2Exception("无效 grant_type：" + grantType);
	}

	/**
	 * Code授权码 获取 Access-Token
	 * @return 处理结果
	 */
	public Object token() {
		// 获取变量
		SaRequest req = SaHolder.getRequest();

		// 获取参数
		ClientIdAndSecretModel clientIdAndSecret = SaOAuth2Manager.getDataResolver().readClientIdAndSecret(req);
		String clientId = clientIdAndSecret.clientId;
		String clientSecret = clientIdAndSecret.clientSecret;
		String code = req.getParamNotNull(Param.code);
		String redirectUri = req.getParam(Param.redirect_uri);

		// 校验参数
		oauth2Template.checkGainTokenParam(code, clientId, clientSecret, redirectUri);

		// 构建 Access-Token
		AccessTokenModel accessTokenModel = SaOAuth2Manager.getDataGenerate().generateAccessToken(code);

		// 返回
		return SaOAuth2Manager.getDataResolver().buildTokenReturnValue(accessTokenModel);
	}

	/**
	 * Refresh-Token 刷新 Access-Token
	 * @return 处理结果
	 */
	public Object refresh() {
		// 获取变量
		SaRequest req = SaHolder.getRequest();
		String grantType = req.getParamNotNull(Param.grant_type);
		if(!grantType.equals(GrantType.refresh_token)) {
			throw new SaOAuth2Exception("无效 grant_type：" + grantType).setCode(SaOAuth2ErrorCode.CODE_30126);
		}

		// 获取参数

		ClientIdAndSecretModel clientIdAndSecret = SaOAuth2Manager.getDataResolver().readClientIdAndSecret(req);
		String clientId = clientIdAndSecret.clientId;
		String clientSecret = clientIdAndSecret.clientSecret;
		String refreshToken = req.getParamNotNull(Param.refresh_token);

		// 校验参数
		oauth2Template.checkRefreshTokenParam(clientId, clientSecret, refreshToken);

		// 获取新 Access-Token
		AccessTokenModel accessTokenModel = SaOAuth2Manager.getDataGenerate().refreshAccessToken(refreshToken);

		// 返回
		return SaOAuth2Manager.getDataResolver().buildRefreshTokenReturnValue(accessTokenModel);
	}

	/**
	 * 回收 Access-Token
	 * @return 处理结果
	 */
	public Object revoke() {
		// 获取变量
		SaRequest req = SaHolder.getRequest();

		// 获取参数
		ClientIdAndSecretModel clientIdAndSecret = SaOAuth2Manager.getDataResolver().readClientIdAndSecret(req);
		String clientId = clientIdAndSecret.clientId;
		String clientSecret = clientIdAndSecret.clientSecret;
		String accessToken = req.getParamNotNull(Param.access_token);

		// 如果 Access-Token 不存在，直接返回
		if(oauth2Template.getAccessToken(accessToken) == null) {
			return SaResult.ok("access_token不存在：" + accessToken);
		}

		// 校验参数
		oauth2Template.checkAccessTokenParam(clientId, clientSecret, accessToken);

		// 回收 Access-Token
		SaOAuth2Manager.getDataGenerate().revokeAccessToken(accessToken);

		// 返回
		return SaOAuth2Manager.getDataResolver().buildRevokeTokenReturnValue();
	}

	/**
	 * doLogin 登录接口
	 * @return 处理结果
	 */
	public Object doLogin() {
		// 获取变量
		SaRequest req = SaHolder.getRequest();
		SaOAuth2Config cfg = SaOAuth2Manager.getConfig();

		return cfg.doLoginHandle.apply(req.getParam(Param.name), req.getParam(Param.pwd));
	}

	/**
	 * doConfirm 确认授权接口
	 * @return 处理结果
	 */
	public Object doConfirm() {
		// 获取变量
		SaRequest req = SaHolder.getRequest();
		String clientId = req.getParamNotNull(Param.client_id);
		Object loginId = getStpLogic().getLoginId();
		String scope = req.getParamNotNull(Param.scope);
		List<String> scopes = SaOAuth2Manager.getDataConverter().convertScopeStringToList(scope);
		SaOAuth2DataGenerate dataGenerate = SaOAuth2Manager.getDataGenerate();

		// 此请求只允许 POST 方式
		if(!req.isMethod(SaHttpMethod.POST)) {
			throw new SaOAuth2Exception("无效请求方式：" + req.getMethod()).setCode(SaOAuth2ErrorCode.CODE_30141);
		}

		// 确认授权
		oauth2Template.saveGrantScope(clientId, loginId, scopes);

		// 判断所需的返回结果模式
		boolean buildRedirectUri = req.isParam(Param.build_redirect_uri, "true");

		// -------- 情况1：只返回确认结果即可
		if( ! buildRedirectUri ) {
			oauth2Template.saveGrantScope(clientId, loginId, scopes);
			return SaResult.ok();
		}

		// -------- 情况2：需要返回最终的 redirect_uri 地址

		// s3、构建请求 Model
		RequestAuthModel ra = SaOAuth2Manager.getDataResolver().readRequestAuthModel(req, loginId);

		// 7、判断授权类型，构建不同的重定向地址
		// 		如果是 授权码式，则：开始重定向授权，下放code
		if(ResponseType.code.equals(ra.responseType)) {
			CodeModel codeModel = dataGenerate.generateCode(ra);
			String redirectUri = dataGenerate.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);
			return SaResult.ok().set(Param.redirect_uri, redirectUri);
		}

		// 		如果是 隐藏式，则：开始重定向授权，下放 token
		if(ResponseType.token.equals(ra.responseType)) {
			AccessTokenModel at = dataGenerate.generateAccessToken(ra, false);
			String redirectUri = dataGenerate.buildImplicitRedirectUri(ra.redirectUri, at.accessToken, ra.state);
			return SaResult.ok().set(Param.redirect_uri, redirectUri);
		}

		// 默认返回
		throw new SaOAuth2Exception("无效response_type: " + ra.responseType).setCode(SaOAuth2ErrorCode.CODE_30125);
	}

	/**
	 * 模式三：密码式
	 * @return 处理结果
	 */
	public Object password() {
		// 获取变量
		SaRequest req = SaHolder.getRequest();
		SaOAuth2Config cfg = SaOAuth2Manager.getConfig();

		// 1、获取请求参数
		String username = req.getParamNotNull(Param.username);
		String password = req.getParamNotNull(Param.password);
		ClientIdAndSecretModel clientIdAndSecret = SaOAuth2Manager.getDataResolver().readClientIdAndSecret(req);
		String clientId = clientIdAndSecret.clientId;
		String clientSecret = clientIdAndSecret.clientSecret;
		String scope = req.getParam(Param.scope, "");
		List<String> scopes = SaOAuth2Manager.getDataConverter().convertScopeStringToList(scope);

		// 2、校验 ClientScope 和 scope 
		oauth2Template.checkClientSecretAndScope(clientId, clientSecret, scopes);

		// 3、防止因前端误传token造成逻辑干扰
		// SaHolder.getStorage().set(getStpLogic().stpLogic.splicingKeyJustCreatedSave(), "no-token");

		// 3、调用API 开始登录，如果没能成功登录，则直接退出
		Object retObj = cfg.doLoginHandle.apply(username, password);
		if( ! getStpLogic().isLogin()) {
			return retObj;
		}

		// 4、构建 ra对象
		RequestAuthModel ra = new RequestAuthModel();
		ra.clientId = clientId;
		ra.loginId = getStpLogic().getLoginId();
		ra.scopes = scopes;

		// 5、生成 Access-Token
		AccessTokenModel at = SaOAuth2Manager.getDataGenerate().generateAccessToken(ra, true);

		// 6、返回 Access-Token
		return SaOAuth2Manager.getDataResolver().buildPasswordReturnValue(at);
	}

	/**
	 * 模式四：凭证式
	 * @return 处理结果
	 */
	public Object clientToken() {
		// 获取变量
		SaRequest req = SaHolder.getRequest();
		SaOAuth2Config cfg = SaOAuth2Manager.getConfig();

		String grantType = req.getParamNotNull(Param.grant_type);
		if(!grantType.equals(GrantType.client_credentials)) {
			throw new SaOAuth2Exception("无效 grant_type：" + grantType).setCode(SaOAuth2ErrorCode.CODE_30126);
		}
		if(!cfg.enableClient) {
			throwErrorSystemNotEnableModel();
		}
		if(!currClientModel().enableClient) {
			throwErrorClientNotEnableModel();
		}

		// 获取参数
		ClientIdAndSecretModel clientIdAndSecret = SaOAuth2Manager.getDataResolver().readClientIdAndSecret(req);
		String clientId = clientIdAndSecret.clientId;
		String clientSecret = clientIdAndSecret.clientSecret;
		String scope = req.getParam(Param.scope, "");
		List<String> scopes = SaOAuth2Manager.getDataConverter().convertScopeStringToList(scope);

		//校验 ClientScope
		oauth2Template.checkContract(clientId, scopes);

		// 校验 ClientSecret
		oauth2Template.checkClientSecret(clientId, clientSecret);

		// 生成
		ClientTokenModel ct = SaOAuth2Manager.getDataGenerate().generateClientToken(clientId, scopes);

		// 返回
		return SaOAuth2Manager.getDataResolver().buildClientTokenReturnValue(ct);
	}


	// ----------- 代码块封装 --------------

	/**
	 * 根据当前请求提交的 client_id 参数获取 SaClientModel 对象 
	 * @return / 
	 */
	public SaClientModel currClientModel() {
		ClientIdAndSecretModel clientIdAndSecret = SaOAuth2Manager.getDataResolver().readClientIdAndSecret(SaHolder.getRequest());
		return oauth2Template.checkClientModel(clientIdAndSecret.clientId);
	}

	/**
	 * 校验 authorize 路由的 ResponseType 参数
	 */
	public void checkAuthorizeResponseType(String responseType, SaRequest req, SaOAuth2Config cfg) {
		// 模式一：Code授权码
		if(responseType.equals(ResponseType.code)) {
			if(!cfg.enableCode) {
				throwErrorSystemNotEnableModel();
			}
			if(!currClientModel().enableCode) {
				throwErrorClientNotEnableModel();
			}
		}
		// 模式二：隐藏式
		else if(responseType.equals(ResponseType.token)) {
			if(!cfg.enableImplicit) {
				throwErrorSystemNotEnableModel();
			}
			if(!currClientModel().enableImplicit) {
				throwErrorClientNotEnableModel();
			}
		}
		// 其它
		else {
			throw new SaOAuth2Exception("无效 response_type: " + req.getParam(Param.response_type)).setCode(SaOAuth2ErrorCode.CODE_30125);
		}
	}

	/**
	 * 获取底层使用的会话对象
	 *
	 * @return /
	 */
	public StpLogic getStpLogic() {
		return StpUtil.stpLogic;
	}

	/**
	 * 系统未开放此授权模式时抛出异常
	 */
	public void throwErrorSystemNotEnableModel() {
		throw new SaOAuth2Exception("系统暂未开放此授权模式").setCode(SaOAuth2ErrorCode.CODE_30131);
	}

	/**
	 * 应用未开放此授权模式时抛出异常
	 */
	public void throwErrorClientNotEnableModel() {
		throw new SaOAuth2Exception("应用暂未开放此授权模式").setCode(SaOAuth2ErrorCode.CODE_30131);
	}

}

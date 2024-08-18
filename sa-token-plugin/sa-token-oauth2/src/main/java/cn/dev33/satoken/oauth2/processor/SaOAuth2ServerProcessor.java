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

		// 模式一：Code授权码
		if(req.isPath(Api.authorize) && req.isParam(Param.response_type, ResponseType.code)) {
			SaClientModel cm = currClientModel();
			if(cfg.getIsCode() && (cm.isCode || cm.isAutoMode)) {
				return authorize();
			}
			throw new SaOAuth2Exception("暂未开放的授权模式").setCode(SaOAuth2ErrorCode.CODE_30131);
		}
		
		// Code授权码 获取 Access-Token
		if(req.isPath(Api.token) && req.isParam(Param.grant_type, GrantType.authorization_code)) {
			return token();
		}

		// Refresh-Token 刷新 Access-Token
		if(req.isPath(Api.refresh) && req.isParam(Param.grant_type, GrantType.refresh_token)) {
			return refreshToken();
		}

		// 回收 Access-Token
		if(req.isPath(Api.revoke)) {
			return revokeToken();
		}

		// doLogin 登录接口
		if(req.isPath(Api.doLogin)) {
			return doLogin();
		}

		// doConfirm 确认授权接口
		if(req.isPath(Api.doConfirm)) {
			return doConfirm();
		}

		// 模式二：隐藏式
		if(req.isPath(Api.authorize) && req.isParam(Param.response_type, ResponseType.token)) {
			SaClientModel cm = currClientModel();
			if(cfg.getIsImplicit() && (cm.isImplicit || cm.isAutoMode)) {
				return authorize();
			}
			throw new SaOAuth2Exception("暂未开放的授权模式").setCode(SaOAuth2ErrorCode.CODE_30132);
		}

		// 模式三：密码式
		if(req.isPath(Api.token) && req.isParam(Param.grant_type, GrantType.password)) {
			SaClientModel cm = currClientModel();
			if(cfg.getIsPassword() && (cm.isPassword || cm.isAutoMode)) {
				return password();
			}
			throw new SaOAuth2Exception("暂未开放的授权模式").setCode(SaOAuth2ErrorCode.CODE_30133);
		}

		// 模式四：凭证式
		if(req.isPath(Api.client_token) && req.isParam(Param.grant_type, GrantType.client_credentials)) {
			SaClientModel cm = currClientModel();
			if(cfg.getIsClient() && (cm.isClient || cm.isAutoMode)) {
				return clientToken();
			}
			throw new SaOAuth2Exception("暂未开放的授权模式").setCode(SaOAuth2ErrorCode.CODE_30134);
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

		// 1、如果尚未登录, 则先去登录
		if( ! getStpLogic().isLogin()) {
			return cfg.notLoginView.get();
		}

		// 2、构建请求 Model
		RequestAuthModel ra = SaOAuth2Manager.getDataResolver().readRequestAuthModel(req, getStpLogic().getLoginId());

		// 3、校验：重定向域名是否合法
		oauth2Template.checkRightUrl(ra.clientId, ra.redirectUri);

		// 4、校验：此次申请的Scope，该Client是否已经签约
		oauth2Template.checkContract(ra.clientId, ra.scopes);

		// 5、判断：如果此次申请的Scope，该用户尚未授权，则转到授权页面
		boolean isGrant = oauth2Template.isGrant(ra.loginId, ra.clientId, ra.scopes);
		if( ! isGrant) {
			return cfg.confirmView.apply(ra.clientId, ra.scopes);
		}


		// 6、判断授权类型
		// 如果是 授权码式，则：开始重定向授权，下放code
		if(ResponseType.code.equals(ra.responseType)) {
			CodeModel codeModel = dataGenerate.generateCode(ra);
			String redirectUri = dataGenerate.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);
			return res.redirect(redirectUri);
		}
		
		// 如果是 隐藏式，则：开始重定向授权，下放 token
		if(ResponseType.token.equals(ra.responseType)) {
			AccessTokenModel at = dataGenerate.generateAccessToken(ra, false);
			String redirectUri = dataGenerate.buildImplicitRedirectUri(ra.redirectUri, at.accessToken, ra.state);
			return res.redirect(redirectUri);
		}

		// 默认返回
		throw new SaOAuth2Exception("无效response_type: " + ra.responseType).setCode(SaOAuth2ErrorCode.CODE_30125);
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
	public Object refreshToken() {
		// 获取变量
		SaRequest req = SaHolder.getRequest();

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
	public Object revokeToken() {
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

		return cfg.doLoginHandle.apply(req.getParamNotNull(Param.name), req.getParamNotNull(Param.pwd));
	}

	/**
	 * doConfirm 确认授权接口
	 * @return 处理结果
	 */
	public Object doConfirm() {
		// 获取变量
		SaRequest req = SaHolder.getRequest();

		String clientId = req.getParamNotNull(Param.client_id);
		String scope = req.getParamNotNull(Param.scope);
		List<String> scopes = SaOAuth2Manager.getDataConverter().convertScopeStringToList(scope);
		Object loginId = getStpLogic().getLoginId();
		oauth2Template.saveGrantScope(clientId, loginId, scopes);
		return SaResult.ok();
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

	/**
	 * 根据当前请求提交的 client_id 参数获取 SaClientModel 对象 
	 * @return / 
	 */
	public SaClientModel currClientModel() {
		ClientIdAndSecretModel clientIdAndSecret = SaOAuth2Manager.getDataResolver().readClientIdAndSecret(SaHolder.getRequest());
		return oauth2Template.checkClientModel(clientIdAndSecret.clientId);
	}

	/**
	 * 获取底层使用的会话对象
	 *
	 * @return /
	 */
	public StpLogic getStpLogic() {
		return StpUtil.stpLogic;
	}

}

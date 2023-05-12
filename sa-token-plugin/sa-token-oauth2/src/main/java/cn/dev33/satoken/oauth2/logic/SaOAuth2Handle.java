package cn.dev33.satoken.oauth2.logic;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.error.SaOAuth2ErrorCode;
import cn.dev33.satoken.oauth2.exception.SaOAuth2Exception;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Consts.Api;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Consts.GrantType;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Consts.Param;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Consts.ResponseType;
import cn.dev33.satoken.oauth2.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.model.CodeModel;
import cn.dev33.satoken.oauth2.model.RequestAuthModel;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token-OAuth2 请求处理类封装
 *
 * @author click33
 * @since <= 1.34.0
 */
public class SaOAuth2Handle {

	/**
	 * 处理Server端请求， 路由分发
	 * @return 处理结果
	 */
	public static Object serverRequest() {

		// 获取变量
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaOAuth2Config cfg = SaOAuth2Manager.getConfig();

		// ------------------ 路由分发 ------------------

		// 模式一：Code授权码
		if(req.isPath(Api.authorize) && req.isParam(Param.response_type, ResponseType.code)) {
			SaClientModel cm = currClientModel();
			if(cfg.getIsCode() && (cm.isCode || cm.isAutoMode)) {
				return authorize(req, res, cfg);
			}
			throw new SaOAuth2Exception("暂未开放的授权模式").setCode(SaOAuth2ErrorCode.CODE_30131);
		}
		
		// Code授权码 获取 Access-Token
		if(req.isPath(Api.token) && req.isParam(Param.grant_type, GrantType.authorization_code)) {
			return token(req, res, cfg);
		}

		// Refresh-Token 刷新 Access-Token
		if(req.isPath(Api.refresh) && req.isParam(Param.grant_type, GrantType.refresh_token)) {
			return refreshToken(req);
		}

		// 回收 Access-Token
		if(req.isPath(Api.revoke)) {
			return revokeToken(req);
		}

		// doLogin 登录接口
		if(req.isPath(Api.doLogin)) {
			return doLogin(req, res, cfg);
		}

		// doConfirm 确认授权接口
		if(req.isPath(Api.doConfirm)) {
			return doConfirm(req);
		}

		// 模式二：隐藏式
		if(req.isPath(Api.authorize) && req.isParam(Param.response_type, ResponseType.token)) {
			SaClientModel cm = currClientModel();
			if(cfg.getIsImplicit() && (cm.isImplicit || cm.isAutoMode)) {
				return authorize(req, res, cfg);
			}
			throw new SaOAuth2Exception("暂未开放的授权模式").setCode(SaOAuth2ErrorCode.CODE_30132);
		}

		// 模式三：密码式
		if(req.isPath(Api.token) && req.isParam(Param.grant_type, GrantType.password)) {
			SaClientModel cm = currClientModel();
			if(cfg.getIsPassword() && (cm.isPassword || cm.isAutoMode)) {
				return password(req, res, cfg);
			}
			throw new SaOAuth2Exception("暂未开放的授权模式").setCode(SaOAuth2ErrorCode.CODE_30133);
		}

		// 模式四：凭证式
		if(req.isPath(Api.client_token) && req.isParam(Param.grant_type, GrantType.client_credentials)) {
			SaClientModel cm = currClientModel();
			if(cfg.getIsClient() && (cm.isClient || cm.isAutoMode)) {
				return clientToken(req, res, cfg);
			}
			throw new SaOAuth2Exception("暂未开放的授权模式").setCode(SaOAuth2ErrorCode.CODE_30134);
		}

		// 默认返回
		return SaOAuth2Consts.NOT_HANDLE;
	}

	/**
	 * 模式一：Code授权码 / 模式二：隐藏式
	 * @param req 请求对象
	 * @param res 响应对象
	 * @param cfg 配置对象
	 * @return 处理结果
	 */
	public static Object authorize(SaRequest req, SaResponse res, SaOAuth2Config cfg) {
		
		// 1、如果尚未登录, 则先去登录
		if(StpUtil.isLogin() == false) {
			return cfg.getNotLoginView().get();
		}

		// 2、构建请求Model
		RequestAuthModel ra = SaOAuth2Util.generateRequestAuth(req, StpUtil.getLoginId());

		// 3、校验：重定向域名是否合法
		SaOAuth2Util.checkRightUrl(ra.clientId, ra.redirectUri);

		// 4、校验：此次申请的Scope，该Client是否已经签约
		SaOAuth2Util.checkContract(ra.clientId, ra.scope);

		// 5、判断：如果此次申请的Scope，该用户尚未授权，则转到授权页面
		boolean isGrant = SaOAuth2Util.isGrant(ra.loginId, ra.clientId, ra.scope);
		if(isGrant == false) {
			return cfg.getConfirmView().apply(ra.clientId, ra.scope);
		}

		// 6、判断授权类型
		// 如果是 授权码式，则：开始重定向授权，下放code
		if(ResponseType.code.equals(ra.responseType)) {
			CodeModel codeModel = SaOAuth2Util.generateCode(ra);
			String redirectUri = SaOAuth2Util.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);
			return res.redirect(redirectUri);
		}
		
		// 如果是 隐藏式，则：开始重定向授权，下放 token
		if(ResponseType.token.equals(ra.responseType)) {
			AccessTokenModel at = SaOAuth2Util.generateAccessToken(ra, false);
			String redirectUri = SaOAuth2Util.buildImplicitRedirectUri(ra.redirectUri, at.accessToken, ra.state);
			return res.redirect(redirectUri);
		}

		// 默认返回
		throw new SaOAuth2Exception("无效response_type: " + ra.responseType).setCode(SaOAuth2ErrorCode.CODE_30125);
	}

	/**
	 * Code授权码 获取 Access-Token
	 * @param req 请求对象
	 * @param res 响应对象
	 * @param cfg 配置对象
	 * @return 处理结果
	 */
	public static Object token(SaRequest req, SaResponse res, SaOAuth2Config cfg) {
		// 获取参数
		String code = req.getParamNotNull(Param.code);
		String clientId = req.getParamNotNull(Param.client_id);
		String clientSecret = req.getParamNotNull(Param.client_secret);
		String redirectUri = req.getParam(Param.redirect_uri);

		// 校验参数
		SaOAuth2Util.checkGainTokenParam(code, clientId, clientSecret, redirectUri);

		// 构建 Access-Token
		AccessTokenModel token = SaOAuth2Util.generateAccessToken(code);

		// 返回
		return SaResult.data(token.toLineMap());
	}

	/**
	 * Refresh-Token 刷新 Access-Token
	 * @param req 请求对象
	 * @return 处理结果
	 */
	public static Object refreshToken(SaRequest req) {
		// 获取参数
		String clientId = req.getParamNotNull(Param.client_id);
		String clientSecret = req.getParamNotNull(Param.client_secret);
		String refreshToken = req.getParamNotNull(Param.refresh_token);

		// 校验参数
		SaOAuth2Util.checkRefreshTokenParam(clientId, clientSecret, refreshToken);

		// 获取新Token返回
		Object data = SaOAuth2Util.refreshAccessToken(refreshToken).toLineMap();
		return SaResult.data(data);
	}

	/**
	 * 回收 Access-Token
	 * @param req 请求对象
	 * @return 处理结果
	 */
	public static Object revokeToken(SaRequest req) {
		// 获取参数
		String clientId = req.getParamNotNull(Param.client_id);
		String clientSecret = req.getParamNotNull(Param.client_secret);
		String accessToken = req.getParamNotNull(Param.access_token);

		// 如果 Access-Token 不存在，直接返回
		if(SaOAuth2Util.getAccessToken(accessToken) == null) {
			return SaResult.ok("access_token不存在：" + accessToken);
		}

		// 校验参数
		SaOAuth2Util.checkAccessTokenParam(clientId, clientSecret, accessToken);

		// 回收 Access-Token
		SaOAuth2Util.revokeAccessToken(accessToken);
		return SaResult.ok();
	}

	/**
	 * doLogin 登录接口
	 * @param req 请求对象
	 * @param res 响应对象
	 * @param cfg 配置对象
	 * @return 处理结果
	 */
	public static Object doLogin(SaRequest req, SaResponse res, SaOAuth2Config cfg) {
		return cfg.getDoLoginHandle().apply(req.getParamNotNull(Param.name), req.getParamNotNull(Param.pwd));
	}

	/**
	 * doConfirm 确认授权接口
	 * @param req 请求对象
	 * @return 处理结果
	 */
	public static Object doConfirm(SaRequest req) {
		String clientId = req.getParamNotNull(Param.client_id);
		String scope = req.getParamNotNull(Param.scope);
		Object loginId = StpUtil.getLoginId();
		SaOAuth2Util.saveGrantScope(clientId, loginId, scope);
		return SaResult.ok();
	}

	/**
	 * 模式三：密码式
	 * @param req 请求对象
	 * @param res 响应对象
	 * @param cfg 配置对象
	 * @return 处理结果
	 */
	public static Object password(SaRequest req, SaResponse res, SaOAuth2Config cfg) {

		// 1、获取请求参数
		String username = req.getParamNotNull(Param.username);
		String password = req.getParamNotNull(Param.password);
		String clientId = req.getParamNotNull(Param.client_id);
		String clientSecret = req.getParamNotNull(Param.client_secret);
		String scope = req.getParam(Param.scope, "");

		// 2、校验 ClientScope 和 scope 
		SaOAuth2Util.checkClientSecretAndScope(clientId, clientSecret, scope);

		// 3、防止因前端误传token造成逻辑干扰
		// SaHolder.getStorage().set(StpUtil.stpLogic.splicingKeyJustCreatedSave(), "no-token");

		// 3、调用API 开始登录，如果没能成功登录，则直接退出
		Object retObj = cfg.getDoLoginHandle().apply(username, password);
		if(StpUtil.isLogin() == false) {
			return retObj;
		}

		// 4、构建 ra对象
		RequestAuthModel ra = new RequestAuthModel();
		ra.clientId = clientId;
		ra.loginId = StpUtil.getLoginId();
		ra.scope = scope;

		// 5、生成 Access-Token
		AccessTokenModel at = SaOAuth2Util.generateAccessToken(ra, true);

		// 6、返回 Access-Token
		return SaResult.data(at.toLineMap());
	}

	/**
	 * 模式四：凭证式
	 * @param req 请求对象
	 * @param res 响应对象
	 * @param cfg 配置对象
	 * @return 处理结果
	 */
	public static Object clientToken(SaRequest req, SaResponse res, SaOAuth2Config cfg) {

		// 获取参数
		String clientId = req.getParamNotNull(Param.client_id);
		String clientSecret = req.getParamNotNull(Param.client_secret);
		String scope = req.getParam(Param.scope);

		//校验 ClientScope
		SaOAuth2Util.checkContract(clientId, scope);

		// 校验 ClientSecret
		SaOAuth2Util.checkClientSecret(clientId, clientSecret);

		// 返回 Client-Token
		ClientTokenModel ct = SaOAuth2Util.generateClientToken(clientId, scope);

		// 返回 Client-Token
		return SaResult.data(ct.toLineMap());
	}

	/**
	 * 根据当前请求提交的 client_id 参数获取 SaClientModel 对象 
	 * @return / 
	 */
	public static SaClientModel currClientModel() {
		String clientId = SaHolder.getRequest().getParam(Param.client_id);
		return SaOAuth2Util.checkClientModel(clientId);
	}
	
}

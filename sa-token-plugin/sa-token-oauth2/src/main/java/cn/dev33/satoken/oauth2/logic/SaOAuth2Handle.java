package cn.dev33.satoken.oauth2.logic;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Consts.AuthType;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Consts.Param;
import cn.dev33.satoken.oauth2.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.model.CodeModel;
import cn.dev33.satoken.oauth2.model.RequestAuthModel;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token-OAuth2 请求处理类封装 
 * @author kong
 *
 */
public class SaOAuth2Handle {
	
	/**
	 * 处理Server端请求 
	 * @return 处理结果 
	 */
	public static Object authorize() {
		
		// 获取变量 
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaOAuth2Config cfg = SaOAuth2Manager.getConfig();
		// StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		// match(Api.authorize) && 
		
		// 授权  
		if(req.isParam(Param.response_type, AuthType.code)) {
			// 1、构建请求Model  TODO： 貌似这个RequestAuthModel对象也可以省略掉 
			RequestAuthModel ra = SaOAuth2Util.generateRequestAuth(req, StpUtil.getLoginId());
			
			// 2、如果尚未登录, 则先去登录 
			if(StpUtil.isLogin() == false) {
				return cfg.notLoginView.get();
			}
			
			// 3、判断：重定向域名的格式是否合法 
			boolean isRigh = SaOAuth2Util.isRightUrl(ra.clientId, ra.redirectUri);
			if(isRigh == false) {
				return cfg.invalidUrlView.apply(ra.clientId, ra.redirectUri);
			}
			
			// 4、判断：此次申请的Scope，该Client是否已经签约 
			boolean isContract = SaOAuth2Util.isContract(ra.clientId, ra.scope);
			if(isContract == false) {
				return cfg.invalidScopeView.apply(ra.clientId, ra.scope);
			}
			
			// 5、判断：此次申请的Scope，该用户是否已经授权过了 
			boolean isGrant = SaOAuth2Util.isGrant(StpUtil.getLoginId(), ra.clientId, ra.scope);
			if(isGrant == false) {
				// 如果尚未授权，则转到授权页面，开始授权操作  
				return cfg.confirmView.apply(ra.clientId, ra.scope);
			}

			// 6、开始重定向授权，下放code 
			CodeModel codeModel = SaOAuth2Util.generateCode(ra); 
			String redirectUri = SaOAuth2Util.buildRedirectUri(ra.redirectUri, codeModel.code, ra.state);
			return res.redirect(redirectUri); 
		}
		
		
		

		// 默认返回 
		return SaOAuth2Consts.NOT_HANDLE;
	}
	
	/**
	 * 获取Token 
	 * @return
	 */
	public static Object token() {
		
		// 获取变量 
		SaRequest req = SaHolder.getRequest();
		// SaResponse res = SaHolder.getResponse();
		// SaOAuth2Config cfg = SaOAuth2Manager.getConfig();

		// 根据code换token  
		if(req.isParam(Param.grant_type, AuthType.authorization_code)) {
			System.out.println("------------获取token，，，");
			
			// 获取参数 
			String code = req.getParamNotNull(Param.code); 					// code码 
			String clientId = req.getParamNotNull(Param.client_id); 		// 应用id
			String clientSecret = req.getParamNotNull(Param.client_secret); // 应用秘钥 
			String redirectUri = req.getParam(Param.redirect_uri); 	// 应用秘钥 
			
			// 校验参数 
			SaOAuth2Util.checkCodeIdSecret(code, clientId, clientSecret, redirectUri);
			
			// 构建 access_token 
			AccessTokenModel token = SaOAuth2Util.generateAccessToken(code);

			// 返回 
			return SaResult.data(token.toLineMap()); 
		}
		
		// 默认返回 
		return SaOAuth2Consts.NOT_HANDLE;
	}
	
	
	

	/**
	 * 路由匹配算法 
	 * @param pattern 路由表达式 
	 * @return 是否可以匹配 
	 */
	static boolean match(String pattern) {
		return SaRouter.isMatch(pattern, SaHolder.getRequest().getRequestPath());
	}
}

package cn.dev33.satoken.sso;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.sso.SaSsoConsts.Api;
import cn.dev33.satoken.sso.SaSsoConsts.ParamName;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token-SSO 单点登录请求处理类封装 
 * @author kong
 *
 */
public class SaSsoHandle {

	/**
	 * 处理Server端请求 
	 * @return 处理结果 
	 */
	public static Object serverRequest() {
		
		// 获取变量 
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaSsoConfig sso = SaManager.getConfig().getSso();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// ---------- SSO-Server端：单点登录授权地址 
		if(req.isPath(Api.ssoAuth)) {
			// ---------- 此处两种情况分开处理：
			// 情况1：在SSO认证中心尚未登录，则先去登登录 
			if(stpLogic.isLogin() == false) {
				return sso.notLoginView.get();
			}
			// 情况2：在SSO认证中心已经登录，开始构建授权重定向地址，下放ticket
			String redirectUrl = SaSsoUtil.buildRedirectUrl(stpLogic.getLoginId(), req.getParam(ParamName.redirect));
			return res.redirect(redirectUrl);
		}

		// ---------- SSO-Server端：RestAPI 登录接口  
		if(req.isPath(Api.ssoDoLogin)) {
			return sso.doLoginHandle.apply(req.getParam("name"), req.getParam("pwd"));
		}

		// ---------- SSO-Server端：校验ticket 获取账号id 
		if(req.isPath(Api.ssoCheckTicket) && sso.isHttp) {
			String ticket = req.getParam(ParamName.ticket);
			String sloCallback = req.getParam(ParamName.ssoLogoutCall);
			
			// 校验ticket，获取对应的账号id 
			Object loginId = SaSsoUtil.checkTicket(ticket);
			
			// 注册此客户端的单点注销回调URL 
			SaSsoUtil.registerSloCallbackUrl(loginId, sloCallback);
			
			// 返回给Client端 
			return loginId;
		}
		
		// ---------- SSO-Server端：单点注销 
		if(req.isPath(Api.ssoLogout) && sso.isSlo) {
			String loginId = req.getParam(ParamName.loginId);
			String secretkey = req.getParam(ParamName.secretkey);
			
			 // 遍历通知Client端注销会话 
	        SaSsoUtil.singleLogout(secretkey, loginId, url -> sso.sendHttp.apply(url)); 
	        	
	        // 完成
	        return SaSsoConsts.OK;
		}
		
		// 默认返回 
		return SaSsoConsts.NOT_HANDLE;
	}
	
	/**
	 * 处理Client端请求 
	 * @return 处理结果 
	 */
	public static Object clientRequest() {

		// 获取变量 
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaSsoConfig sso = SaManager.getConfig().getSso();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;

		// ---------- SSO-Client端：登录地址 
		if(req.isPath(Api.ssoLogin)) {
			String back = req.getParam(ParamName.back, "/");
			String ticket = req.getParam(ParamName.ticket);
			
			// 如果当前Client端已经登录，则无需访问SSO认证中心，可以直接返回 
			if(stpLogic.isLogin()) {
				return res.redirect(back);
			}
			/*
			 * 此时有两种情况: 
			 * 情况1：ticket无值，说明此请求是Client端访问，需要重定向至SSO认证中心
			 * 情况2：ticket有值，说明此请求从SSO认证中心重定向而来，需要根据ticket进行登录 
			 */
			if(ticket == null) {
				String serverAuthUrl = SaSsoUtil.buildServerAuthUrl(SaHolder.getRequest().getUrl(), back);
				return res.redirect(serverAuthUrl);
			} else {
				// ------- 1、校验ticket，获取账号id 
				Object loginId = null;
				if(sso.isHttp) {
					// 方式1：使用http请求校验ticket 
					String ssoLogoutCall = null; 
					if(sso.isSlo) {
						ssoLogoutCall = SaHolder.getRequest().getUrl().replace(Api.ssoLogin, Api.ssoLogoutCall); 
					}
					String checkUrl = SaSsoUtil.buildCheckTicketUrl(ticket, ssoLogoutCall);
					Object body = sso.sendHttp.apply(checkUrl);
					loginId = (SaFoxUtil.isEmpty(body) ? null : body);
				} else {
					// 方式2：直连Redis校验ticket 
					loginId = SaSsoUtil.checkTicket(ticket);
				}
				// ------- 2、如果loginId有值，说明ticket有效，进行登录并重定向至back地址 
				if(loginId != null ) {
					stpLogic.login(loginId); 
					return res.redirect(back);
				} else {
					// 如果ticket无效: 
					return sso.ticketInvalidView.apply(ticket);
				}
			}
		}

		// ---------- SSO-Client端：单点注销 [模式二]
		if(req.isPath(Api.ssoLogout) && sso.isSlo && sso.isHttp == false) {
			stpLogic.logout();
			if(req.getParam(ParamName.back) == null) {
				return SaResult.ok("单点注销成功");
			} else {
				return res.redirect(req.getParam(ParamName.back, "/"));
			}
		}

		// ---------- SSO-Client端：单点注销 [模式三]
		if(req.isPath(Api.ssoLogout) && sso.isSlo && sso.isHttp) {
			// 如果未登录，则无需注销 
	        if(stpLogic.isLogin() == false) {
	            return SaResult.ok();
	        }
	        // 调用SSO-Server认证中心API 
	        String url = SaSsoUtil.buildSloUrl(stpLogic.getLoginId());
	        String body = String.valueOf(sso.sendHttp.apply(url));
	        if(SaSsoConsts.OK.equals(body)) {
				if(req.getParam(ParamName.back) == null) {
					return SaResult.ok("单点注销成功");
				} else {
					return res.redirect(req.getParam(ParamName.back, "/"));
				}
	        }
	        return SaResult.error("单点注销失败"); 
		}

		// ---------- SSO-Client端：单点注销的回调 	[模式三]
		if(req.isPath(Api.ssoLogoutCall) && sso.isSlo && sso.isHttp) {
			String loginId = req.getParam(ParamName.loginId);
			String secretkey = req.getParam(ParamName.secretkey);
			
			SaSsoUtil.checkSecretkey(secretkey);
			stpLogic.logoutByTokenValue(stpLogic.getTokenValueByLoginId(loginId));
	        return SaSsoConsts.OK;
		}
		
		// 默认返回 
		return SaSsoConsts.NOT_HANDLE;
	}
	
}

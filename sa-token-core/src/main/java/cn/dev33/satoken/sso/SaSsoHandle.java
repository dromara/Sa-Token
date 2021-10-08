package cn.dev33.satoken.sso;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.SaTokenException;
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
	 * 处理Server端所有请求 
	 * @return 处理结果 
	 */
	public static Object serverRequest() {
		
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaSsoConfig cfg = SaManager.getConfig().getSso();

		// ------------------ 路由分发 ------------------ 
		
		// SSO-Server端：授权地址 
		if(req.isPath(Api.ssoAuth)) {
			return ssoAuth();
		}

		// SSO-Server端：RestAPI 登录接口 
		if(req.isPath(Api.ssoDoLogin)) {
			return ssoDoLogin();
		}

		// SSO-Server端：校验ticket 获取账号id 
		if(req.isPath(Api.ssoCheckTicket) && cfg.isHttp) {
			return ssoCheckTicket();
		}
		
		// SSO-Server端：单点注销 [模式一]   (不带loginId参数) 
		if(req.isPath(Api.ssoLogout) && cfg.isSlo && req.hasParam(ParamName.loginId) == false) {
			return ssoServerLogoutType1();
		}
		
		// SSO-Server端：单点注销 [模式三]   (带loginId参数) 
		if(req.isPath(Api.ssoLogout) && cfg.isHttp && cfg.isSlo && req.hasParam(ParamName.loginId)) {
			return ssoServerLogout();
		}
		
		// 默认返回 
		return SaSsoConsts.NOT_HANDLE;
	}
	
	/**
	 * SSO-Server端：授权地址
	 * @return 处理结果 
	 */
	public static Object ssoAuth() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaSsoConfig cfg = SaManager.getConfig().getSso();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// ---------- 此处有两种情况分开处理：
		// ---- 情况1：在SSO认证中心尚未登录，需要先去登录 
		if(stpLogic.isLogin() == false) {
			return cfg.notLoginView.get();
		}
		// ---- 情况2：在SSO认证中心已经登录，需要重定向回 Client 端，而这又分为两种方式：
		String mode = req.getParam(ParamName.mode, "");
		
		// 方式1：直接重定向回Client端 (mode=simple)
		if(mode.equals(SaSsoConsts.MODE_SIMPLE)) {
			String redirect = req.getParam(ParamName.redirect);
			SaSsoUtil.checkRedirectUrl(redirect);
			return res.redirect(redirect);
		} else {
			// 方式2：带着ticket参数重定向回Client端 (mode=ticket)  
			String redirectUrl = SaSsoUtil.buildRedirectUrl(stpLogic.getLoginId(), req.getParam(ParamName.redirect));
			return res.redirect(redirectUrl);
		}
	}

	/**
	 * SSO-Server端：RestAPI 登录接口 
	 * @return 处理结果 
	 */
	public static Object ssoDoLogin() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaSsoConfig cfg = SaManager.getConfig().getSso();
		
		// 处理 
		return cfg.doLoginHandle.apply(req.getParam(ParamName.name), req.getParam(ParamName.pwd));
	}

	/**
	 * SSO-Server端：校验ticket 获取账号id 
	 * @return 处理结果 
	 */
	public static Object ssoCheckTicket() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		
		// 获取参数 
		String ticket = req.getParam(ParamName.ticket);
		String sloCallback = req.getParam(ParamName.ssoLogoutCall);
		
		// 校验ticket，获取对应的账号id 
		Object loginId = SaSsoUtil.checkTicket(ticket);
		
		// 注册此客户端的单点注销回调URL 
		SaSsoUtil.registerSloCallbackUrl(loginId, sloCallback);
		
		// 返回给Client端 
		return loginId;
	}

	/**
	 * SSO-Server端：单点注销 [模式一] 
	 * @return 处理结果 
	 */
	public static Object ssoServerLogoutType1() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// 开始处理 
		stpLogic.logout();
		
		// 返回
		return ssoLogoutBack(req, res);
	}

	/**
	 * SSO-Server端：单点注销 [模式三] 
	 * @return 处理结果 
	 */
	public static Object ssoServerLogout() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaSsoConfig cfg = SaManager.getConfig().getSso();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// 获取参数 
		String loginId = req.getParam(ParamName.loginId);
		String secretkey = req.getParam(ParamName.secretkey);
		
		// 遍历通知Client端注销会话 
		// step.1 校验秘钥 
		SaSsoUtil.checkSecretkey(secretkey);
		
		// step.2 遍历通知Client端注销会话 
		SaSsoUtil.forEachSloUrl(loginId, url -> cfg.sendHttp.apply(url));
		
		// step.3 Server端注销 
		stpLogic.logout(loginId);
        	
        // 完成
        return SaSsoConsts.OK;
	}
	

	/**
	 * 处理Client端所有请求 
	 * @return 处理结果 
	 */
	public static Object clientRequest() {

		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaSsoConfig cfg = SaManager.getConfig().getSso();

		// ------------------ 路由分发 ------------------ 
		
		// ---------- SSO-Client端：登录地址 
		if(req.isPath(Api.ssoLogin)) {
			return ssoLogin();
		}

		// ---------- SSO-Client端：单点注销 [模式二]
		if(req.isPath(Api.ssoLogout) && cfg.isSlo && cfg.isHttp == false) {
			return ssoLogoutType2();
		}

		// ---------- SSO-Client端：单点注销 [模式三]
		if(req.isPath(Api.ssoLogout) && cfg.isSlo && cfg.isHttp) {
			return ssoLogoutType3();
		}

		// ---------- SSO-Client端：单点注销的回调 	[模式三]
		if(req.isPath(Api.ssoLogoutCall) && cfg.isSlo && cfg.isHttp) {
			return ssoLogoutCall();
		}
		
		// 默认返回 
		return SaSsoConsts.NOT_HANDLE;
	}
	
	/**
	 * SSO-Client端：登录地址 
	 * @return 处理结果 
	 */
	public static Object ssoLogin() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaSsoConfig cfg = SaManager.getConfig().getSso();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// 获取参数 
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
			Object loginId = checkTicket(ticket, Api.ssoLogin);
			
			// Be: 如果开发者自定义了处理逻辑 
			if(cfg.ticketResultHandle != null) {
				return cfg.ticketResultHandle.apply(loginId, back);
			}
			// ------- 2、如果loginId有值，说明ticket有效，进行登录并重定向至back地址 
			if(loginId != null ) {
				stpLogic.login(loginId); 
				return res.redirect(back);
			} else {
				// 如果ticket无效: 
				throw new SaTokenException("无效ticket：" + ticket);
			}
		}
	}

	/**
	 * SSO-Client端：单点注销 [模式二] 
	 * @return 处理结果 
	 */
	public static Object ssoLogoutType2() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// 开始处理 
		stpLogic.logout();
		
		// 返回
		return ssoLogoutBack(req, res);
	}

	/**
	 * SSO-Client端：单点注销 [模式三]  
	 * @return 处理结果 
	 */
	public static Object ssoLogoutType3() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaSsoConfig cfg = SaManager.getConfig().getSso();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// 如果未登录，则无需注销 
        if(stpLogic.isLogin() == false) {
            return SaResult.ok();
        }
        
        // 调用SSO-Server认证中心API，进行注销
        String url = SaSsoUtil.buildSloUrl(stpLogic.getLoginId());
        String body = String.valueOf(cfg.sendHttp.apply(url));
        if(SaSsoConsts.OK.equals(body) == false) {
            return SaResult.error("单点注销失败"); 
        }
        
        // 返回 
        return ssoLogoutBack(req, res);
	}

	/**
	 * SSO-Client端：单点注销的回调  [模式三] 
	 * @return 处理结果 
	 */
	public static Object ssoLogoutCall() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// 获取参数 
		String loginId = req.getParam(ParamName.loginId);
		String secretkey = req.getParam(ParamName.secretkey);
		
		SaSsoUtil.checkSecretkey(secretkey);
		stpLogic.logoutByTokenValue(stpLogic.getTokenValueByLoginId(loginId));
        return SaSsoConsts.OK;
	}
	
	/**
	 * 封装：单点注销成功后返回结果 
	 * @param req SaRequest对象 
	 * @param res SaResponse对象 
	 * @return 返回结果 
	 */
	public static Object ssoLogoutBack(SaRequest req, SaResponse res) {
		/* 
		 * 三种情况：
		 * 	1. 有back参数，值为SELF -> 回退一级并刷新 
		 * 	2. 有back参数，值为url -> 跳转到此url地址 
		 * 	3. 无back参数 -> 返回json数据 
		 */
		String back = req.getParam(ParamName.back);
		if(SaFoxUtil.isNotEmpty(back)) {
			if(back.equals(SaSsoConsts.SELF)) {
				return "<script>if(document.referrer != location.href){ location.replace(document.referrer || '/'); }</script>";
			}
			return res.redirect(back);
		} else {
			return SaResult.ok("单点注销成功");
		}
	}

	/**
	 * 封装：校验ticket，取出loginId 
	 * @param ticket ticket码
	 * @param currUri 当前路由的uri，用于计算单点注销回调地址 
	 * @return loginId
	 */
	public static Object checkTicket(String ticket, String currUri) {
		SaSsoConfig cfg = SaManager.getConfig().getSso();
		// --------- 两种模式 
		if(cfg.isHttp) {
			// 模式三：使用http请求校验ticket 
			String ssoLogoutCall = null; 
			if(cfg.isSlo) {
				ssoLogoutCall = SaHolder.getRequest().getUrl().replace(currUri, Api.ssoLogoutCall); 
			}
			String checkUrl = SaSsoUtil.buildCheckTicketUrl(ticket, ssoLogoutCall);
			Object body = cfg.sendHttp.apply(checkUrl);
			return (SaFoxUtil.isEmpty(body) ? null : body);
		} else {
			// 模式二：直连Redis校验ticket 
			return SaSsoUtil.checkTicket(ticket);
		}
	}
	
}

package cn.dev33.satoken.sso;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.sso.SaSsoConsts.Api;
import cn.dev33.satoken.sso.SaSsoConsts.ParamName;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.exception.SaSsoExceptionCode;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token-SSO 单点登录请求处理类封装 
 * @author kong
 *
 */
public class SaSsoHandle {

	// ----------- SSO-Server 端路由分发 

	/**
	 * 处理Server端所有请求 
	 * @return 处理结果 
	 */
	public static Object serverRequest() {
		
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaSsoConfig cfg = SaSsoManager.getConfig();

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
		if(req.isPath(Api.ssoCheckTicket) && cfg.getIsHttp()) {
			return ssoCheckTicket();
		}
		
		// SSO-Server端：单点注销 [用户访问式]   (不带loginId参数) 
		if(req.isPath(Api.ssoLogout) && cfg.getIsSlo() && req.hasParam(ParamName.loginId) == false) {
			return ssoLogoutByUserVisit();
		}
		
		// SSO-Server端：单点注销 [Client调用式]  (带loginId参数 & isHttp=true) 
		if(req.isPath(Api.ssoLogout) && cfg.getIsHttp() && cfg.getIsSlo() && req.hasParam(ParamName.loginId)) {
			return ssoLogoutByClientHttp();
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
		SaSsoConfig cfg = SaSsoManager.getConfig();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// ---------- 此处有两种情况分开处理：
		// ---- 情况1：在SSO认证中心尚未登录，需要先去登录 
		if(stpLogic.isLogin() == false) {
			return cfg.getNotLoginView().get();
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
		SaSsoConfig cfg = SaSsoManager.getConfig();
		
		// 处理 
		return cfg.getDoLoginHandle().apply(req.getParam(ParamName.name), req.getParam(ParamName.pwd));
	}

	/**
	 * SSO-Server端：校验ticket 获取账号id [模式三]
	 * @return 处理结果 
	 */
	public static Object ssoCheckTicket() {
		// 获取参数 
		SaRequest req = SaHolder.getRequest();
		String ticket = req.getParamNotNull(ParamName.ticket);
		String sloCallback = req.getParam(ParamName.ssoLogoutCall);
		
		// 校验ticket，获取 loginId 
		Object loginId = SaSsoUtil.checkTicket(ticket);

		// 注册此客户端的单点注销回调URL 
		SaSsoUtil.registerSloCallbackUrl(loginId, sloCallback);
		
		// 给 client 端响应结果 
		if(SaFoxUtil.isEmpty(loginId)) {
			return SaResult.error("无效ticket：" + ticket);
		} else {
			return SaResult.data(loginId);
		}
	}

	/**
	 * SSO-Server端：单点注销 [用户访问式] 
	 * @return 处理结果 
	 */
	public static Object ssoLogoutByUserVisit() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		Object loginId = SaSsoUtil.saSsoTemplate.stpLogic.getLoginIdDefaultNull();

		// 单点注销 
		if(SaFoxUtil.isNotEmpty(loginId)) {
			SaSsoUtil.ssoLogout(loginId);
		}
		
		// 完成
		return ssoLogoutBack(req, res);
	}

	/**
	 * SSO-Server端：单点注销 [Client调用式] 
	 * @return 处理结果 
	 */
	public static Object ssoLogoutByClientHttp() {
		// 获取参数 
		SaRequest req = SaHolder.getRequest();
		String loginId = req.getParam(ParamName.loginId);
		
		// step.1 校验签名 
		SaSsoUtil.checkSign(req);
		
		// step.2 单点注销 
		SaSsoUtil.ssoLogout(loginId);
        
        // 响应 
		return SaResult.ok();
	}
	

	// ----------- SSO-Client 端路由分发 

	/**
	 * 处理Client端所有请求 
	 * @return 处理结果 
	 */
	public static Object clientRequest() {

		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		SaSsoConfig cfg = SaSsoManager.getConfig();

		// ------------------ 路由分发 ------------------ 
		
		// ---------- SSO-Client端：登录地址 
		if(req.isPath(Api.ssoLogin)) {
			return ssoLogin();
		}

		// ---------- SSO-Client端：单点注销 [模式二]
		if(req.isPath(Api.ssoLogout) && cfg.getIsSlo() && cfg.getIsHttp() == false) {
			return ssoLogoutType2();
		}

		// ---------- SSO-Client端：单点注销 [模式三]
		if(req.isPath(Api.ssoLogout) && cfg.getIsSlo() && cfg.getIsHttp()) {
			return ssoLogoutType3();
		}

		// ---------- SSO-Client端：单点注销的回调 	[模式三]
		if(req.isPath(Api.ssoLogoutCall) && cfg.getIsSlo() && cfg.getIsHttp()) {
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
		SaSsoConfig cfg = SaSsoManager.getConfig();
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
			// ------- 1、校验ticket，获取 loginId 
			Object loginId = checkTicket(ticket, Api.ssoLogin);
			
			// Be: 如果开发者自定义了处理逻辑 
			if(cfg.getTicketResultHandle() != null) {
				return cfg.getTicketResultHandle().apply(loginId, back);
			}
			
			// ------- 2、如果 loginId 无值，说明 ticket 无效
			if(SaFoxUtil.isEmpty(loginId)) {
				throw new SaSsoException("无效ticket：" + ticket).setCode(SaSsoExceptionCode.CODE_20004);
			} else {
				// 3、如果 loginId 有值，说明 ticket 有效，此时进行登录并重定向至back地址 
				stpLogic.login(loginId); 
				return res.redirect(back);
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
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// 如果未登录，则无需注销 
        if(stpLogic.isLogin() == false) {
            return SaResult.ok();
        }
        
        // 调用 sso-server 认证中心单点注销API 
        String url = SaSsoUtil.buildSloUrl(stpLogic.getLoginId());
        SaResult result = SaSsoUtil.request(url);
        
		// 校验响应状态码 
		if(result.getCode() == SaResult.CODE_SUCCESS) {
	        // 极端场景下，sso-server 中心的单点注销可能并不会通知到此 client 端，所以这里需要再补一刀
	        if(stpLogic.isLogin()) {
	        	stpLogic.logout();
	        }
	        return ssoLogoutBack(req, res);
		} else {
			// 将 sso-server 回应的消息作为异常抛出 
			throw new SaSsoException(result.getMsg()).setCode(SaSsoExceptionCode.CODE_20006);
		}
	}

	/**
	 * SSO-Client端：单点注销的回调 [模式三] 
	 * @return 处理结果 
	 */
	public static Object ssoLogoutCall() {
		// 获取对象 
		SaRequest req = SaHolder.getRequest();
		StpLogic stpLogic = SaSsoUtil.saSsoTemplate.stpLogic;
		
		// 获取参数 
		String loginId = req.getParamNotNull(ParamName.loginId);
		
		// 注销当前应用端会话
		SaSsoUtil.checkSign(req);
		stpLogic.logout(loginId);
		
		// 响应 
        return SaResult.ok("单点注销回调成功");
	}
	
	
	// ----------- 工具方法 

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
		SaSsoConfig cfg = SaSsoManager.getConfig();
		
		// --------- 两种模式 
		if(cfg.getIsHttp()) {
			// 模式三：使用 http 请求从认证中心校验ticket 
			String ssoLogoutCall = null; 
			if(cfg.getIsSlo()) {
				ssoLogoutCall = SaHolder.getRequest().getUrl().replace(currUri, Api.ssoLogoutCall); 
			}
			
			// 发起请求 
			String checkUrl = SaSsoUtil.buildCheckTicketUrl(ticket, ssoLogoutCall);
			SaResult result = SaSsoUtil.request(checkUrl);
			
			// 校验 
			if(result.getCode() == SaResult.CODE_SUCCESS) {
				return result.getData();
			} else {
				// 将 sso-server 回应的消息作为异常抛出 
				throw new SaSsoException(result.getMsg()).setCode(SaSsoExceptionCode.CODE_20005);
			}
		} else {
			// 模式二：直连Redis校验ticket 
			return SaSsoUtil.checkTicket(ticket);
		}
	}
	
}

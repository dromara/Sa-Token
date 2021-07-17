package com.pj.controller;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pj.utils.AjaxJson;
import com.pj.utils.SoMap;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.oauth2.SaOAuth2Manager;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Handle;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Consts.Param;
import cn.dev33.satoken.oauth2.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.model.CodeModel;
import cn.dev33.satoken.oauth2.model.RequestAuthModel;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

@RestController
//@RequestMapping("/oauth2/")
public class OAuth2Controller {

	// OAuth-Server端：处理所有OAuth相关请求 
	@RequestMapping("/oauth2/authorize")
	public Object request() {
		System.out.println("------------进入请求：" + SaHolder.getRequest().getUrl()); 
		return SaOAuth2Handle.authorize();
	}

	// OAuth-Server端：处理所有OAuth相关请求 
	@RequestMapping("/oauth2/token")
	public Object token() {
		System.out.println("------------进入请求：" + SaHolder.getRequest().getUrl()); 
		return SaOAuth2Handle.token();
	}

	// OAuth-Server端：刷新Token 
	@RequestMapping("/oauth2/ref")
	public Object ref(String refresh_token) {
		System.out.println("------------进入请求：" + SaHolder.getRequest().getUrl()); 
		return SaResult.data(
				SaOAuth2Util.saOAuth2Template.refreshAccessToken(refresh_token).toLineMap()
				);
	}
	
	// 隐藏式
	@RequestMapping("/oauth2/yc")
	public Object yc() {
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaOAuth2Config cfg = SaOAuth2Manager.getConfig();

		// ------------- 以下都是雷同代码 
		
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

		// ------------- 以上都是雷同代码 
		
		// 6、开始重定向授权，下放code 
		AccessTokenModel at = SaOAuth2Util.generateAccessToken(ra); 
		String redirectUri = SaOAuth2Util.buildRedirectUri2(ra.redirectUri, at.accessToken, ra.state);
		return res.redirect(redirectUri); 
	}

	// 密码式 
	@RequestMapping("/oauth2/password")
	public Object password() {
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaOAuth2Config cfg = SaOAuth2Manager.getConfig();

		// 1、构建请求Model  TODO： 貌似这个RequestAuthModel对象也可以省略掉 
		// RequestAuthModel ra = SaOAuth2Util.generateRequestAuth(req, StpUtil.getLoginId());
		String username = req.getParamNotNull("username");
		String password = req.getParamNotNull("password");
		String clientId = req.getParamNotNull("client_id");
		
		Object retObj = cfg.doLoginHandle.apply(username, password); 
		
		if(StpUtil.isLogin() == false) {
			return retObj;
		}
		
		RequestAuthModel ra = new RequestAuthModel();
		ra.clientId = req.getParamNotNull(Param.client_id);
		// ra.responseType = req.getParamNotNull(Param.response_type);
		// ra.redirectUri = req.getParamNotNull(Param.redirect_uri);
		// ra.state = req.getParam(Param.state);
		ra.scope = "";// 默认应该为空还是内个呢 SaOAuth2Util.saOAuth2Template.getClientScopeList(clientId);
		ra.loginId = StpUtil.getLoginId();
		
		// 6、开始重定向授权，下放code TODO 这里需要也生成 ref_token  
		AccessTokenModel at = SaOAuth2Util.generateAccessToken(ra); 
		
		// 
		return SaResult.data(at);
	}

	// 凭证式 
	@RequestMapping("/oauth2/appat")
	public Object appat() {
		SaRequest req = SaHolder.getRequest();
		SaResponse res = SaHolder.getResponse();
		SaOAuth2Config cfg = SaOAuth2Manager.getConfig();
		
		String clientId = req.getParamNotNull(Param.client_id);
		String scope = req.getParam(Param.scope);
		
		ClientTokenModel ct = SaOAuth2Util.generateClientToken(clientId, scope);
		
		// 
		return SaResult.data(ct.toLineMap()); 
	}
	

	@Autowired
	public void setSaOAuth2Config(SaOAuth2Config saOAuth2Config) {
		System.out.println("-----------123 " + saOAuth2Config);
		
		saOAuth2Config.
			// 未登录的视图 
			setNotLoginView(()->{
				// return "您暂未登录"; 
				HttpServletRequest request = SpringMVCUtil.getRequest();
				HttpServletResponse response = SpringMVCUtil.getResponse();
				response.setContentType("text/html");
				try {
					request.getRequestDispatcher("/login.html").forward(request, response);
				} catch (ServletException | IOException e) {
					e.printStackTrace();
				}
				return null;
			}).
			// 未登录的视图 
			setConfirmView((clientId, scope)->{
				return "本次操作需要授权"; 
			})
			// 登录处理函数 
			.setDoLoginHandle((name, pwd) -> {
				if("sa".equals(name) && "123456".equals(pwd)) {
					StpUtil.login(10001);
					return AjaxJson.getSuccess();
				}
				return SaResult.error();
			})
			;
	}
	
	
	
	
	// 获取授权码 
	@RequestMapping("/authorize")
	public AjaxJson authorize(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// 获取参数 
		System.out.println("------------------ 成功进入请求 ------------------");
		
		// 如果暂未登录，则先跳转到登录页 (转发)
		if(StpUtil.isLogin() == false) {
			response.setContentType("text/html");
			request.getRequestDispatcher("/login.html").forward(request, response);
			return AjaxJson.getSuccess();
		}
		
		// 构建Model 
		RequestAuthModel authModel = new RequestAuthModel()
				.setClientId(request.getParameter("client_id"))			// 应用id 
				.setScope(request.getParameter("scope"))				// 授权类型 
				.setLoginId(StpUtil.getLoginIdAsLong())					// 当前登录账号id 
				.setRedirectUri(URLDecoder.decode(request.getParameter("redirect_uri"), "utf-8"))	// 重定向地址 
				.setResponseType(request.getParameter("response_type")) // 返回类型 
				.setState(request.getParameter("state"))				// 状态值 
				.checkModel();				// 校验参数完整性 
		
		// 生成授权码Model
		CodeModel codeModel = SaOAuth2Util.generateCode(authModel); 
		
		// 打印调试 
		System.out.println("应用id=" + authModel.getClientId() + "请求授权，授权类型=" + authModel.getResponseType()); 
		System.out.println("重定向地址：" + authModel.getRedirectUri());
		// System.out.println("拼接完成的redirect_uri: " + codeModel.getRedirectUri());
		// System.out.println("如果用户拒绝授权，则重定向至： " + codeModel.getRejectUri());
		
		// 如果请求的权限用户已经确认，直接开始重定向授权 
//		if(codeModel.getIsConfirm() == true) {
//			response.sendRedirect(codeModel.getRedirectUri());
//		} else {
//			// 如果请求的权限用户尚未确认，则进入到确定页 
//			request.setAttribute("name", "sdd");
//			response.sendRedirect("/auth.html?code=" + codeModel.getCode());
//		}
		
		return AjaxJson.getSuccess();
	}
	
	// 根据授权码获取应用信息 
	@RequestMapping("/getCodeInfo")
	public AjaxJson getCodeInfo(String code) {
		// 获取codeModel
		CodeModel codeModel = SaOAuth2Util.getCode(code);
		System.out.println(code);
		System.out.println(codeModel);
		// 返回 
		return AjaxJson.getSuccessData(codeModel);
	}
	
	// 确认授权一个授权码
	@RequestMapping("/confirm")
	public AjaxJson confirm(String code) {
		// 获取codeModel
		CodeModel codeModel = SaOAuth2Util.getCode(code);
		if(codeModel == null) {
			return AjaxJson.getError("无效code码");
		}
		// 此处的判断是为了保证当前账号id 和 创建授权码的账号id一致 才可以进行确认
		if(codeModel.getLoginId().toString().equals(StpUtil.getLoginIdAsString()) == false) {
			return AjaxJson.getError("暂无权限");
		}
		// 进行确认
		// SaOAuth2Util.confirmCode(code);
		
		// 返回ok
		return AjaxJson.getSuccess();
	}
	
	// 根据授权码等参数，获取 access_token 等信息 
	@RequestMapping("/getAccessToken")
	public SoMap getAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取参数 
		System.out.println("------------------ 成功进入请求 ------------------");
		String code = request.getParameter("code"); 			// code码 
		String clientId = request.getParameter("client_id"); 			// 应用id
		String clientSecret = request.getParameter("client_secret"); 			// 应用秘钥 

		// 校验参数 
		// SaOAuth2Util.checkCodeIdSecret(code, clientId, clientSecret);
		
		// 生成 
		CodeModel codeModel = SaOAuth2Util.getCode(code);
		AccessTokenModel tokenModel = SaOAuth2Util.generateAccessToken(code);
		
		// 生成AccessToken之后，将授权码立即销毁 
		SaOAuth2Util.deleteCode(code); 

		// 返回 
		return SoMap.getSoMap()
				.setModel(tokenModel)
				.set("code", 200)
				.set("msg", "ok");
	}
	
	// 根据access_token返回指定的资源 
	@RequestMapping("/getResources")
	public SoMap getResources(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		// 获取信息 
		String accessToken = request.getParameter("access_token");
		Object LoginId = SaOAuth2Util.getLoginIdByAccessToken(accessToken);
		System.out.println("LoginId=" + LoginId);
		
		// 根据LoginId获取相应信息...
		// 此处仅做模拟  
		return new SoMap()
				.set("nickname", "shengzhang")
				.set("acatar", "xxx")
				.set("sex", 1);
	}
	
	
	
	
}

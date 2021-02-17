package com.pj.controller;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pj.utils.AjaxJson;
import com.pj.utils.SoMap;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import cn.dev33.satoken.oauth2.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.model.CodeModel;
import cn.dev33.satoken.oauth2.model.RequestAuthModel;
import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/oauth2/")
public class OAuth2Controller {


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
		System.out.println("拼接完成的redirect_uri: " + codeModel.getRedirectUri());
		System.out.println("如果用户拒绝授权，则重定向至： " + codeModel.getRejectUri());
		
		// 如果请求的权限用户已经确认，直接开始重定向授权 
		if(codeModel.getIsConfirm() == true) {
			response.sendRedirect(codeModel.getRedirectUri());
		} else {
			// 如果请求的权限用户尚未确认，则进入到确定页 
			request.setAttribute("name", "sdd");
			response.sendRedirect("/auth.html?code=" + codeModel.getCode());
		}
		
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
		SaOAuth2Util.confirmCode(code);
		
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
		SaOAuth2Util.checkCodeIdSecret(code, clientId, clientSecret);
		
		// 生成 
		CodeModel codeModel = SaOAuth2Util.getCode(code);
		AccessTokenModel tokenModel = SaOAuth2Util.generateAccessToken(codeModel);
		
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

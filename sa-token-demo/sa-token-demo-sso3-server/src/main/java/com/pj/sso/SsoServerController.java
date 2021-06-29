package com.pj.sso;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.pj.util.AjaxJson;

import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;

/**
 * Sa-Token-SSO Server端 Controller 
 * @author kong
 *
 */
@RestController
public class SsoServerController {

	// SSO-Server端：授权地址，跳转到登录页面 
	@RequestMapping("ssoAuth")
	public Object ssoAuth(String redirect) {
		/*
		 * 此处两种情况分开处理：
		 * 1、如果在SSO认证中心尚未登录，则先去登登录 
		 * 2、如果在SSO认证中心尚已登录，则开始对redirect地址下放ticket引导授权 
		 */
		// 情况1：尚未登录 
		if(StpUtil.isLogin() == false) {
			return new ModelAndView("sa-login.html");
		}
		// 情况2：已经登录，开始构建授权重定向地址，下放ticket
		String redirectUrl = SaSsoUtil.buildRedirectUrl(StpUtil.getLoginId(), redirect);
		return new ModelAndView("redirect:" + redirectUrl);
	}
	
	// SSO-Server端：登录接口 
	@RequestMapping("doLogin")
	public AjaxJson doLogin(String name, String pwd) {
		// 此处仅做模拟登录，真实环境应该查询数据进行登录 
		if("sa".equals(name) && "123456".equals(pwd)) {
			StpUtil.login(10001);
			return AjaxJson.getSuccess("登录成功！");
		}
		return AjaxJson.getError("登录失败！");
	}
	
	// SSO-Server端：校验ticket 获取账号id 
	@RequestMapping("checkTicket")
	public Object checkTicket(String ticket, String sloCallback) {
		// 校验ticket，获取对应的账号id 
		Object loginId = SaSsoUtil.checkTicket(ticket);
		
		// 注册此客户端的单点注销回调URL（不需要单点注销功能可删除此行代码）
		SaSsoUtil.registerSloCallbackUrl(loginId, sloCallback);
		
		// 返回给Client端 
		return loginId;
	}
	
}

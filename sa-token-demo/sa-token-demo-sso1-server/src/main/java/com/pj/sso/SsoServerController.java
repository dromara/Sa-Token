package com.pj.sso;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token-SSO Server端 Controller 
 * @author kong
 *
 */
@RestController
public class SsoServerController {

	/*
	 * SSO-Server端：统一登录地址 
	 */
	@RequestMapping("/sso/auth")
	public Object ssoAuth(String redirect) {
		// 如果尚未登录，则返回登录视图进行登录 
		if(StpUtil.isLogin() == false) {
			return new ModelAndView("sa-login.html");
		}
		// 如果已登录，则原路返回到 Client端 
		return SaHolder.getResponse().redirect(redirect);
	}
	
	// 处理登录请求  
	@RequestMapping("/sso/doLogin")
	public SaResult ssoDoLogin(String name, String pwd) {
		// 模拟登录 
	    if("sa".equals(name) && "123456".equals(pwd)) {
	        StpUtil.login(10001);
	        return SaResult.ok("登录成功！");
	    }
	    return SaResult.error("登录失败！");
	}

	// 单点注销地址 
	@RequestMapping("/sso/logout")
	public Object ssoLogout(String back) {
		StpUtil.logout();
		if(SaFoxUtil.isEmpty(back)) {
			return SaResult.ok();
		}
		return SaHolder.getResponse().redirect(back);
	}
	
	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}

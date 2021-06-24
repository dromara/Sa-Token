package com.pj.sso;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;

/**
 * Sa-Token-SSO Client端 Controller 
 * @author kong
 */
@RestController
public class SsoClientController {

	// Client端登录地址 
	@RequestMapping("ssoLogin")
	public Object login(String back, String ticket) {
		// 如果当前已经登录，则无需访问SSO认证中心，可以直接返回 
		System.out.println("是否已登录：" + StpUtil.isLogin());
		if(StpUtil.isLogin()) {
			return new ModelAndView("redirect:" + back);
		}
		/*
		 * 接下来两种情况：
		 * ticket有值，说明此请求从SSO认证中心重定向而来，需要根据ticket进行登录 
		 * ticket无值，说明此请求是Client端访问，需要重定向至SSO认证中心
		 */
		if(ticket != null) {
			Object loginId = SaSsoUtil.getLoginId(ticket);
			if(loginId != null ) {
				// 如果ticket是有效的 (可以获取到值)，需要就此登录 且清除此ticket
				StpUtil.login(loginId); 
				SaSsoUtil.deleteTicket(ticket);
				// 最后重定向回back地址 
				return new ModelAndView("redirect:" + back);
			}
			// 此处向客户端提示ticket无效即可，不要重定向到SSO认证中心，否则容易引起无限重定向 
			return "ticket无效: " + ticket;
		}

		// 重定向至 SSO-Server端 认证地址 
		String serverAuthUrl = SaSsoUtil.buildServerAuthUrl(SaHolder.getRequest().getUrl(), back);
		return new ModelAndView("redirect:" + serverAuthUrl);
	}
	
}

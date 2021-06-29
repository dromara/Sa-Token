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

	// SSO-Client端：首页
	@RequestMapping("/")
	public String index() {
		String str = "<h2>Sa-Token SSO-Client 应用端</h2>" + 
					"<p>当前会话是否登录：" + StpUtil.isLogin() + "</p>" + 
					"<p><a href=\"javascript:location.href='/ssoLogin?back=' + encodeURIComponent(location.href);\">登录</a></p>";
		return str;
	}
	
	// SSO-Client端：登录地址 
	@RequestMapping("ssoLogin")
	public Object ssoLogin(String back, String ticket) {
		// 如果当前Client端已经登录，则无需访问SSO认证中心，可以直接返回 
		if(StpUtil.isLogin()) {
			return new ModelAndView("redirect:" + back);
		}
		/*
		 * 接下来两种情况：
		 * ticket无值，说明此请求是Client端访问，需要重定向至SSO认证中心
		 * ticket有值，说明此请求从SSO认证中心重定向而来，需要根据ticket进行登录 
		 */
		if(ticket == null) {
			String serverAuthUrl = SaSsoUtil.buildServerAuthUrl(SaHolder.getRequest().getUrl(), back);
			return new ModelAndView("redirect:" + serverAuthUrl);
		} else {
			Object loginId = checkTicket(ticket);
			if(loginId != null ) {
				// loginId有值，说明ticket有效
				StpUtil.login(loginId); 
				return new ModelAndView("redirect:" + back);
			}
			// 此处向客户端提示ticket无效即可，不要重定向到SSO认证中心，否则容易引起无限重定向 
			return "ticket无效: " + ticket;
		}
	}
	
	// SSO-Client端：校验ticket，获取账号id 
	private Object checkTicket(String ticket) {
		return SaSsoUtil.checkTicket(ticket);
	}

}

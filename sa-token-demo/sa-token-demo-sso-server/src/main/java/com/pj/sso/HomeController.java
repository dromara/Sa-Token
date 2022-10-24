package com.pj.sso;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;

/**
 * SSO 平台中心模式示例，跳连接进入子系统 
 * 
 * @author kong
 * @since 2022-10-24
 */
@RestController
public class HomeController {

	/**
	 * 平台化首页
	 * @return
	 */
	@RequestMapping("/home")
	public Object index() {
		// 如果未登录，则先去登录
		if(StpUtil.isLogin() == false) {
			return SaHolder.getResponse().redirect("/sso/auth");
		}
		
		// 拼接各个子系统的地址，格式形如：/sso/auth?redirect=${子系统首页}/sso/login?back=${子系统首页}
		String link1 = "/sso/auth?redirect=http://sa-sso-client1.com:9001/sso/login?back=http://sa-sso-client1.com:9001/";
		String link2 = "/sso/auth?redirect=http://sa-sso-client2.com:9001/sso/login?back=http://sa-sso-client2.com:9001/";
		String link3 = "/sso/auth?redirect=http://sa-sso-client3.com:9001/sso/login?back=http://sa-sso-client3.com:9001/";

		// 组织网页结构返回到前端 
		String title = "<h2>SSO 平台首页</h2>";
		String client1 = "<p><a href='" + link1 + "' target='_blank'> 进入Client1系统 </a></p>";
		String client2 = "<p><a href='" + link2 + "' target='_blank'> 进入Client2系统 </a></p>";
		String client3 = "<p><a href='" + link3 + "' target='_blank'> 进入Client3系统 </a></p>";
		
		return title + client1 + client2 + client3;
	}
	
}

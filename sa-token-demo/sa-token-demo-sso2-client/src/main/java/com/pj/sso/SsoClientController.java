package com.pj.sso;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.sso.SaSsoHandle;
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
					"<p><a href=\"javascript:location.href='/ssoLogin?back=' + encodeURIComponent(location.href);\">登录</a> " + 
					"<a href=\"javascript:location.href='/ssoLogout?back=' + encodeURIComponent(location.href);\">注销</a></p>";
					// "<a href='/ssoLogout' target='_blank'>注销</a></p>";		// 上面是[跳页面]方式，这个是[RestAPI]方式 区别在于是否加了back参数 
		return str;
	}
	
	// SSO-Client端：处理所有SSO相关请求 
	@RequestMapping("/sso*")
	public Object ssoRequest() {
		return SaSsoHandle.clientRequest();
	}

}

package com.pj.sso;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token-SSO Client端 Controller 
 * @author click33
 */
@RestController
public class SsoClientController {

	// SSO-Client端：首页 
	@RequestMapping("/")
	public String index() {
		String authUrl = SaSsoManager.getConfig().splicingAuthUrl();
		String solUrl = SaSsoManager.getConfig().splicingSloUrl();
		String str = "<h2>Sa-Token SSO-Client 应用端</h2>" + 
					"<p>当前会话是否登录：" + StpUtil.isLogin() + "</p>" + 
					"<p><a href=\"javascript:location.href='" + authUrl + "?mode=simple&redirect=' + encodeURIComponent(location.href);\">登录</a> " + 
					"<a href=\"javascript:location.href='" + solUrl + "?back=' + encodeURIComponent(location.href);\">注销</a> </p>";
		return str;
	}
	
	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}

package com.pj.sso;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sso.SaSsoManager;
import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Sa-Token-SSO Client端 Controller 
 * @author click33
 */
@RestController
public class SsoClientController {

	// SSO-Client端：首页 
	@RequestMapping("/")
	public String index(HttpServletRequest request) {
		String url = SaFoxUtil.encodeUrl( SaFoxUtil.joinParam(SaHolder.getRequest().getUrl(), request.getQueryString()) );
		SaSsoClientConfig cfg = SaSsoManager.getClientConfig();

		String str = "<h2>Sa-Token SSO-Client 应用端 (模式一)</h2>" +
					"<p>当前会话是否登录：" + StpUtil.isLogin() + " (" + StpUtil.getLoginId("") + ")</p>" +
					"<p>" +
						"<a href='" + cfg.splicingAuthUrl() + "?mode=simple&client=" + cfg.getClient() + "&redirect=" + url + "'>登录</a> - " +
						"<a href='" + cfg.splicingSignoutUrl() + "?singleDeviceIdLogout=true&back=" + url + "'>单浏览器注销</a> - " +
						"<a href='" + cfg.splicingSignoutUrl() + "?back=" + url + "'>全端注销</a> " +
					"</p>";
		return str;
	}
	
	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}

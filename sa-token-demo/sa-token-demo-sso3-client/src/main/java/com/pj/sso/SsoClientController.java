package com.pj.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejlchina.okhttps.OkHttps;

import cn.dev33.satoken.config.SaTokenConfig;
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
					"<p><a href=\"javascript:location.href='/ssoLogin?back=' + encodeURIComponent(location.href);\">登录</a>" + 
					" <a href='/ssoLogout' target='_blank'>注销</a></p>";
		return str;
	}
	
	// SSO-Client端：处理所有SSO相关请求 
	@RequestMapping("/sso*")
	public Object ssoRequest() {
		return SaSsoHandle.clientRequest();
	}

	// 配置SSO相关参数 
	@Autowired
	private void configSso(SaTokenConfig cfg) {
		cfg.sso
			// 配置Http请求处理器 
			.setSendHttp(url -> {
				return OkHttps.sync(url).get().getBody().toString();
			})
			;
	}
	
}

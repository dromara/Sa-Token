package com.pj.sso;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejlchina.okhttps.OkHttps;

import cn.dev33.satoken.sso.SaSsoUtil;

/**
 * Sa-Token-SSO Server端 单点注销 Controller 
 * @author kong
 */
@RestController
public class SsoServerLogoutController {

	// SSO-Server端：单点注销
	@RequestMapping("ssoLogout")
	public String ssoLogout(String loginId, String secretkey) {
		
		// 遍历通知Client端注销会话 (为了提高响应速度这里可将sync换为async)
		SaSsoUtil.singleLogout(secretkey, loginId, url -> OkHttps.sync(url).get());
		
		// 完成
		return "ok";
	}
	
}

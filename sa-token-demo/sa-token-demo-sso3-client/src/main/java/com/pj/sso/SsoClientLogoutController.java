package com.pj.sso;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejlchina.okhttps.OkHttps;
import com.pj.util.AjaxJson;

import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;

/**
 * Sa-Token-SSO Client端 单点注销 Controller 
 * @author kong
 */
@RestController
public class SsoClientLogoutController {

	// SSO-Client端：单端注销 (其它Client端会话不受影响)
	@RequestMapping("logout")
	public AjaxJson logout() {
		StpUtil.logout();
		return AjaxJson.getSuccess();
	}
	
	// SSO-Client端：单点注销 (所有端一起下线)
	@RequestMapping("ssoLogout")
	public AjaxJson ssoLogout() {
		// 如果未登录，则无需注销 
		if(StpUtil.isLogin() == false) {
			return AjaxJson.getSuccess();
		}
		// 调用SSO-Server认证中心API 
		String url = SaSsoUtil.buildSloUrl(StpUtil.getLoginId());
		String res = OkHttps.sync(url).get().getBody().toString();
		if(res.equals("ok")) {
			return AjaxJson.getSuccess("单点注销成功");
		}
		return AjaxJson.getError("单点注销失败"); 
	}
	
	// 单点注销的回调
	@RequestMapping("sloCallback")
	public String sloCallback(String loginId, String secretkey) {
		SaSsoUtil.checkSecretkey(secretkey);
		StpUtil.logoutByLoginId(loginId);
		return "ok";
	}
	
}

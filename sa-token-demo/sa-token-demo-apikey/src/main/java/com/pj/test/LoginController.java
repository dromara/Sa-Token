package com.pj.test;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录 Controller
 *
 * @author click33
 */
@RestController
public class LoginController {

	// 登录 
	@RequestMapping("login")
	public SaResult login(@RequestParam(defaultValue="10001") String id) {
		StpUtil.login(id);
		return SaResult.ok().set("satoken", StpUtil.getTokenValue());
	}

	// 查询当前登录人
	@RequestMapping("getLoginId")
	public SaResult getLoginId() {
		return SaResult.data(StpUtil.getLoginId());
	}

	// 注销 
	@RequestMapping("logout")
	public SaResult logout() {
		StpUtil.logout();
		return SaResult.ok();
	}
	
}

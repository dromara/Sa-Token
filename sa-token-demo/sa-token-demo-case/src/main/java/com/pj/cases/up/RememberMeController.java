package com.pj.cases.up;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 记住我模式登录 
 * 
 * @author kong
 * @since 2022-10-17 
 */
@RestController
@RequestMapping("/RememberMe/")
public class RememberMeController {

	// 记住我登录    ---- http://localhost:8081/RememberMe/doLogin?name=zhang&pwd=123456
	@RequestMapping("doLogin")
	public SaResult doLogin(String name, String pwd) {
		if("zhang".equals(name) && "123456".equals(pwd)) {
			StpUtil.login(10001, true);
			return SaResult.ok("登录成功");
		}
		return SaResult.error("登录失败");
	}
	
	// 不记住我登录    ---- http://localhost:8081/RememberMe/doLogin2?name=zhang&pwd=123456
	@RequestMapping("doLogin2")
	public SaResult doLogin2(String name, String pwd) {
		if("zhang".equals(name) && "123456".equals(pwd)) {
			StpUtil.login(10001, false);
			return SaResult.ok("登录成功");
		}
		return SaResult.error("登录失败");
	}

	// 七天免登录    ---- http://localhost:8081/RememberMe/doLogin3?name=zhang&pwd=123456
	@RequestMapping("doLogin3")
	public SaResult doLogin3(String name, String pwd) {
		if("zhang".equals(name) && "123456".equals(pwd)) {
			StpUtil.login(10001, 60 * 60 * 24 * 7);
			return SaResult.ok("登录成功");
		}
		return SaResult.error("登录失败");
	}
	
}

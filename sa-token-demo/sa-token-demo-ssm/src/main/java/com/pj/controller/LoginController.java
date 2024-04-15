package com.pj.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录测试 
 * @author click33
 *
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

	// 测试登录  ---- http://localhost:8080/sa_token_demo_ssm_war/acc/doLogin?name=zhang&pwd=123456
	@RequestMapping("doLogin")
	public SaResult doLogin(String name, String pwd) {
		System.out.println("-------- 12344");
		// 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对 
		if("zhang".equals(name) && "123456".equals(pwd)) {
			StpUtil.login(10001);
			return SaResult.ok("登录成功");
		}
		return SaResult.error("登录失败");
	}

	// 查询登录状态  ---- http://localhost:8080/sa_token_demo_ssm_war/acc/isLogin
	@RequestMapping("isLogin")
	public SaResult isLogin() {
		return SaResult.ok("是否登录：" + StpUtil.isLogin());
	}

	// 校验登录  ---- http://localhost:8080/sa_token_demo_ssm_war/acc/checkLogin
	@RequestMapping("checkLogin")
	public SaResult checkLogin() {
		StpUtil.checkLogin();
		return SaResult.ok();
	}

	// 查询 Token 信息  ---- http://localhost:8080/sa_token_demo_ssm_war/acc/tokenInfo
	@RequestMapping("tokenInfo")
	public SaResult tokenInfo() {
		return SaResult.data(StpUtil.getTokenInfo());
	}
	
	// 测试注销  ---- http://localhost:8080/sa_token_demo_ssm_war/acc/logout
	@RequestMapping("logout")
	public SaResult logout() {
		StpUtil.logout();
		return SaResult.ok();
	}
	
}

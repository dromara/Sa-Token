package com.pj.test;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录测试
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

	// 登录  ---- http://localhost:8081/acc/doLogin?name=zhang&pwd=123456
	@RequestMapping("doLogin")
	public SaResult doLogin(String name, String pwd) {
		if("zhang".equals(name) && "123456".equals(pwd)) {
			StpUtil.login(10001);
			// 要点：通过请求响应体返回 token 信息
			return SaResult.ok("登录成功");
		}
		return SaResult.error("登录失败");
	}

	// 注销  ---- http://localhost:8081/acc/logout
	@RequestMapping("logout")
	public SaResult logout() {
		StpUtil.logout();
		return SaResult.ok();
	}

	// 查询登录状态  ---- http://localhost:8081/acc/isLogin
	@RequestMapping("isLogin")
	public SaResult isLogin() {
		boolean isLogin = StpUtil.isLogin();
		System.out.println("当前会话是否登录：" + isLogin);
		return SaResult.data(isLogin);
	}

}

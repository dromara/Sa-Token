package com.pj.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 登录测试
 * @author kong
 *
 */
@RestController
@RequestMapping("/user/")
public class UserController {

	// 测试登录，浏览器访问： http://localhost:8081/user/doLogin?username=zhang&password=123456
	@RequestMapping("doLogin")
	public String doLogin(String username, String password) {
		// 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对 
		if("zhang".equals(username) && "123456".equals(password)) {
			StpUtil.login(10001);
			return "登录成功";
		}
		return "登录失败";
	}

	// 查询登录状态，浏览器访问： http://localhost:8081/user/isLogin
	@RequestMapping("isLogin")
	public String isLogin(String username, String password) {
		return "当前会话是否登录：" + StpUtil.isLogin();
	}
	
}

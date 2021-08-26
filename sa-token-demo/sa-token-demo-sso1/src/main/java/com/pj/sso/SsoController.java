package com.pj.sso;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 测试: 同域单点登录
 * @author kong
 */
@RestController
@RequestMapping("/sso/")
public class SsoController {
	
	// 测试：进行登录
	@RequestMapping("doLogin")
	public SaResult doLogin(@RequestParam(defaultValue = "10001") String id) {
		System.out.println("---------------- 进行登录 ");
		StpUtil.login(id);
		return SaResult.ok("登录成功: " + id);
	}

	// 测试：是否登录
	@RequestMapping("isLogin")
	public SaResult isLogin() {
		System.out.println("---------------- 是否登录 ");
		boolean isLogin = StpUtil.isLogin();
		return SaResult.ok("是否登录: " + isLogin);
	}

}

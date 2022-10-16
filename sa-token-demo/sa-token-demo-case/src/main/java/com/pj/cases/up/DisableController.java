package com.pj.cases.up;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 账号封禁示例 
 * 
 * @author kong
 * @since 2022-10-17 
 */
@RestController
@RequestMapping("/disable/")
public class DisableController {

	/*
	 * 测试步骤：
	 	1、访问登录接口，可以正常登录    ---- http://localhost:8081/disable/login?userId=10001
	 	2、注销登录    ---- http://localhost:8081/disable/logout
	 	3、禁用账号    ---- http://localhost:8081/disable/disable?userId=10001
	 	4、再次访问登录接口，登录失败    ---- http://localhost:8081/disable/login?userId=10001
	 */

	// 会话登录接口  ---- http://localhost:8081/disable/login?userId=10001
	@RequestMapping("login")
	public SaResult login(long userId) {
		// 1、先检查此账号是否已被封禁 
		StpUtil.checkDisable(userId);
		// 2、检查通过后，再登录 
		StpUtil.login(userId);
		return SaResult.ok("账号登录成功");
	}

	// 会话注销接口  ---- http://localhost:8081/disable/logout
	@RequestMapping("logout")
	public SaResult logout() {
		StpUtil.logout();
		return SaResult.ok("账号退出成功");
	}

	// 封禁指定账号  ---- http://localhost:8081/disable/disable
	@RequestMapping("disable")
	public SaResult disable(long userId) {
		/*
		 * 账号封禁：
		 * 	参数1：要封禁的账号id
		 * 	参数2：要封禁的时间，单位：秒，86400秒=1天
		 */
		StpUtil.disable(userId, 86400);
		return SaResult.ok("账号 " + userId + " 封禁成功");
	}

}

package com.pj.cases.up;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 同端互斥登录示例 
 * 
 * @author click33
 * @since 2022-10-17 
 */
@RestController
@RequestMapping("/mutex/")
public class MutexLoginController {

	/*
	 * 前提1：准备至少四个浏览器：A、B、C、D
	 * 前提2：配置文件需要把 sa-token.is-concurrent 的值改为 false 
	 * 
	 * 测试步骤：
	 	1、在浏览器A上登录账号10001，设备为PC    ---- http://localhost:8081/mutex/login?userId=10001&device=PC
	 		检查是否登录成功，返回true：			---- http://localhost:8081/mutex/isLogin
	 		
	 	2、在浏览器B上登录账号10001，设备为APP    ---- http://localhost:8081/mutex/login?userId=10001&device=APP
	 		检查是否登录成功，返回true：			---- http://localhost:8081/mutex/isLogin
	 		
	 	3、复查一下览器A上的账号是否登录，发现并没有顶替下去，原因是两个浏览器指定的登录设备不同，允许同时在线 
	 			---- http://localhost:8081/mutex/isLogin
	 			
	 	4、在浏览器C上登录账号10001，设备为PC    ---- http://localhost:8081/mutex/login?userId=10001&device=PC
	 		检查是否登录成功，返回true：			---- http://localhost:8081/mutex/isLogin
	 		
	 	5、复查一下浏览器A上的账号是否登录，发现账号已被顶替下线
	 			---- http://localhost:8081/mutex/isLogin
	 		
	 	6、再复查一下浏览器B上的账号是否登录，发现账号未被顶替下线，因为浏览器B上登录的设备是APP，而浏览器C顶替的设备是PC
	 			---- http://localhost:8081/mutex/isLogin
	 	
	 	7、此时再从浏览器D上登录账号10001，设备为APP    ---- http://localhost:8081/mutex/login?userId=10001&device=APP
	 		检查是否登录成功，返回true：			---- http://localhost:8081/mutex/isLogin
	 		
	 	8、此时再复查一下浏览器B上的账号是否登录，发现账号已被顶替下线
	 			---- http://localhost:8081/mutex/isLogin
	 */

	// 会话登录接口  ---- http://localhost:8081/mutex/doLogin?userId=10001&device=PC
	@RequestMapping("login")
	public SaResult login(long userId, String device) {
		/*
		 * 参数1：要登录的账号
		 * 参数2：此账号在什么设备上登录的 
		 */
		StpUtil.login(userId, device);
		return SaResult.ok("登录成功");
	}

	// 查询当前登录状态  ---- http://localhost:8081/mutex/isLogin
	@RequestMapping("isLogin")
	public SaResult isLogin() {
		// StpUtil.isLogin() 查询当前客户端是否登录，返回 true 或 false 
		boolean isLogin = StpUtil.isLogin();
		return SaResult.ok("当前客户端是否登录：" + isLogin + "，登录的设备是：" + StpUtil.getLoginDevice());
	}

}

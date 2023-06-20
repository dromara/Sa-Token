package com.pj.cases.up;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 身份切换 
 * 
 * @author click33
 * @since 2022-10-17 
 */
@RestController
@RequestMapping("/SwitchTo/")
public class SwitchToController {

	/*
	 * 前提：首先调用登录接口进行登录，代码在 com.pj.cases.use.LoginAuthController 中有详细解释，此处不再赘述 
	 * 		---- http://localhost:8081/acc/doLogin?name=zhang&pwd=123456
	 */
	
	// 身份切换    ---- http://localhost:8081/SwitchTo/switchTo?userId=10044
	@RequestMapping("switchTo")
	public SaResult switchTo(long userId) {
		// 将当前会话 [身份临时切换] 为其它账号
		// 		--- 切换的有效期为本次请求 
		StpUtil.switchTo(userId);

		// 此时再调用此方法会返回 10044 (我们临时切换到的账号id)
		System.out.println(StpUtil.getLoginId());;

		// 结束 [身份临时切换]
		StpUtil.endSwitch();
		
		// 此时再打印账号，就又回到了原来的值：10001 
		System.out.println(StpUtil.getLoginId());
		
		return SaResult.ok();
	}

	// 以 lambda 表达式的方式身份切换    ---- http://localhost:8081/SwitchTo/switchTo2?userId=10044
	@RequestMapping("switchTo2")
	public SaResult switchTo2(long userId) {
		
		// 输出 10001
		System.out.println("------- [身份临时切换] 调用前，当前登录账号id是：" + StpUtil.getLoginId());
		
		// 以 lambda 表达式的方式身份切换，作用范围只在这个 lambda 表达式内有效 
		StpUtil.switchTo(userId, () -> {
		    System.out.println("是否正在身份临时切换中: " + StpUtil.isSwitch());  // 输出 true
		    System.out.println("获取当前登录账号id: " + StpUtil.getLoginId());   // 输出 10044
		});
		
		// 结束后，再次获取当前登录账号，输出10001
		System.out.println("------- [身份临时切换] 调用结束，当前登录账号id是：" + StpUtil.getLoginId()); 

		return SaResult.ok();
	}
	
}


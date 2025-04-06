package com.pj.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.pj.service.DemoService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	
	@DubboReference
    private DemoService demoService;

	// Consumer端登录，状态传播到Provider端    --- http://localhost:8081/test
    @RequestMapping("test")
    public SaResult test() {
		demoService.isLogin("----------- 登录前 ");
		
		StpUtil.login(10001);
		
		demoService.isLogin("----------- 登录后 ");
		
        return SaResult.ok();
    }

	// Provider端登录，状态回传到Consumer端     --- http://localhost:8081/test2
    @RequestMapping("test2")
    public SaResult test2() {
    	System.out.println("----------- 登录前 ");
		System.out.println("Token值：" + StpUtil.getTokenValue()); 
		System.out.println("是否登录：" + StpUtil.isLogin()); 
    	
    	demoService.doLogin(10002);

    	System.out.println("----------- 登录后 ");
		System.out.println("Token值：" + StpUtil.getTokenValue()); 
		System.out.println("是否登录：" + StpUtil.isLogin());

		return SaResult.ok();
    }

	// Consumer端登录，状态在Consumer端保持     --- http://localhost:8081/test3
    @RequestMapping("test3")
    public SaResult test3() {
    	System.out.println("----------- 登录前 ");
		System.out.println("Token值：" + StpUtil.getTokenValue());
		System.out.println("是否登录：" + StpUtil.isLogin());

		StpUtil.login(10003);
		demoService.isLogin("----------- Provider状态");

    	System.out.println("----------- 登录后 ");
		System.out.println("Token值：" + StpUtil.getTokenValue());
		System.out.println("是否登录：" + StpUtil.isLogin());

		return SaResult.ok();
    }

	// Provider端登录，状态在Provider端保持     --- http://localhost:8081/test4
    @RequestMapping("test4")
    public SaResult test4() {
    	// 登录 
    	demoService.doLogin(10004);
		
    	// 打印一下 
		demoService.isLogin("----------- 会话信息 ");

		return SaResult.ok();
    }
    
}
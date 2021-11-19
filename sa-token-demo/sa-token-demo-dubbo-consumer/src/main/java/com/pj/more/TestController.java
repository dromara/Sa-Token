package com.pj.more;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.StpUtil;

@RestController
public class TestController {
	
	@DubboReference
    private DemoService demoService;

	// Consumer端登录，状态传播到Provider端 
    @RequestMapping("test")
    public String test() {
		demoService.isLogin("----------- 登录前 ");
		
		StpUtil.login(10001);
		
		demoService.isLogin("----------- 登录后 ");
		
        return "ok";
    }

	// Provider端登录，状态回传到Consumer端 
    @RequestMapping("test2")
    public String test2() {
    	System.out.println("----------- 登录前 ");
		System.out.println("Token值：" + StpUtil.getTokenValue()); 
		System.out.println("是否登录：" + StpUtil.isLogin()); 
    	
    	demoService.doLogin(10002);

    	System.out.println("----------- 登录后 ");
		System.out.println("Token值：" + StpUtil.getTokenValue()); 
		System.out.println("是否登录：" + StpUtil.isLogin()); 
		
        return "ok";
    }

	// Consumer端登录，状态在Consumer端保持 
    @RequestMapping("test3")
    public String test3() {
    	System.out.println("----------- 登录前 ");
		System.out.println("Token值：" + StpUtil.getTokenValue()); 
		System.out.println("是否登录：" + StpUtil.isLogin()); 

		StpUtil.login(10003);
		demoService.isLogin("----------- Provider状态");
    	
    	System.out.println("----------- 登录后 ");
		System.out.println("Token值：" + StpUtil.getTokenValue()); 
		System.out.println("是否登录：" + StpUtil.isLogin()); 
		
        return "ok";
    }
    
}
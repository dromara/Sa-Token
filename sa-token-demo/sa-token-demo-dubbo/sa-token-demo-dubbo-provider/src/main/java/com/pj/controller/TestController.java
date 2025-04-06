package com.pj.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.pj.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	
	@Autowired
    private DemoService demoService;

	// test
    @RequestMapping("test")
    public SaResult test() {
		demoService.isLogin("----------- 登录前 " + StpUtil.isLogin());
		
		StpUtil.login(10001);
		
		demoService.isLogin("----------- 登录后 " + StpUtil.isLogin());
		
        return SaResult.ok();
    }

}
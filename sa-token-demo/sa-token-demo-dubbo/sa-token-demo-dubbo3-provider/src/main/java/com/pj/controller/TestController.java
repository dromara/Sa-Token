package com.pj.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.pj.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


//    如果把 @Autowired 改为 @DubboReference
//    则可能在首次调用 dubbo 服务时控制台出现以下异常（只打印异常信息，不影响调用）：
//          java.lang.reflect.InaccessibleObjectException: Unable to make field private byte java.lang.StackTraceElement.format accessible:
//          module java.base does not "opens java.lang" to unnamed module @3a52dba3
//
//    在启动参数上加上如下即可解决：
//          --add-opens java.base/java.math=ALL-UNNAMED

    @Autowired
    public DemoService demoService;

	// test
    @RequestMapping("test")
    public SaResult test() {
		demoService.isLogin("----------- 登录前 " + StpUtil.isLogin());
		
		StpUtil.login(10001);
		
		demoService.isLogin("----------- 登录后 " + StpUtil.isLogin());
		
        return SaResult.ok();
    }

}
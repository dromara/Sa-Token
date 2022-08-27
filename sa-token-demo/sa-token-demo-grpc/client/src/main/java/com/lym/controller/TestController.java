package com.lym.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lym.grpc.client.GrpcAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lym
 * @description
 * @date 2022/8/26 11:01
 **/
@RestController
public class TestController {
    @Autowired
    private GrpcAuthService grpcAuthService;

    // 客户端登录，状态带到服务端。
    @RequestMapping("test")
    public void test() {
        System.out.println("登录前：" + grpcAuthService.isLogin());
        System.out.println("登录前：" + StpUtil.isLogin());

        StpUtil.login(1);

        System.out.println("登录后：" + grpcAuthService.isLogin());
        System.out.println("登录后：" + StpUtil.getTokenValue());
        System.out.println("登录后：" + StpUtil.getLoginId());
    }

    // 服务端登录，登录状态带回客户端
    @RequestMapping("test2")
    public String test2() {
        System.out.println("登录前：" + grpcAuthService.isLogin());
        System.out.println("登录前：" + StpUtil.isLogin());

        String token = grpcAuthService.login(3);

        System.out.println("登录后：" + grpcAuthService.isLogin());
        System.out.println("登录后：" + StpUtil.getTokenValue());
        System.out.println("登录后：" + StpUtil.getLoginId());
        assert StpUtil.getTokenValue().equals(token);

        return token;
    }

}

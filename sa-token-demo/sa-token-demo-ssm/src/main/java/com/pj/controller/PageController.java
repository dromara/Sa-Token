package com.pj.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 页面访问测试
 * @author click33
 * @since 2024/4/14
 */
@Controller
public class PageController {

    // http://localhost:8080/sa_token_demo_ssm_war/home
    @RequestMapping("/home")
    public String index() {
        System.out.println("------- home页，所有游客可访问");
        return "home";
    }

    // http://localhost:8080/sa_token_demo_ssm_war/user
    @RequestMapping("/user")
    public String user() {
        System.out.println("------- user页，登录后才能访问");
        StpUtil.checkLogin();
        return "user";
    }

    // http://localhost:8080/sa_token_demo_ssm_war/admin
    @RequestMapping("/admin")
    public String admin() {
        System.out.println("------- admin页，具有admin角色才能访问");
        StpUtil.checkRole("admin");
        return "admin";
    }

}
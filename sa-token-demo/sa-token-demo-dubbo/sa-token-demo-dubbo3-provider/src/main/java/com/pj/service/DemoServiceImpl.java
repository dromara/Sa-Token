package com.pj.service;

import org.apache.dubbo.config.annotation.DubboService;

import cn.dev33.satoken.stp.StpUtil;

@DubboService()
public class DemoServiceImpl implements DemoService {

	@Override
	public void doLogin(Object loginId) {
		StpUtil.login(loginId);
	}

	@Override
	public void isLogin(String str) {
		System.out.println(str);
		System.out.println("Token值：" + StpUtil.getTokenValue()); 
		System.out.println("是否登录：" + StpUtil.isLogin()); 
	}

}


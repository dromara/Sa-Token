package com.pj.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pj.utils.AjaxJson;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 服务端登录Controller 
 * @author kong 
 */
@RestController
public class ServerAccController {

	// 登录方法 
	@RequestMapping("/doLogin")
	public AjaxJson test(String username, String password) {
		System.out.println("------------------ 成功进入请求 ------------------");
		if("test".equals(username) && "test".equals(password)) {
			StpUtil.setLoginId(10001);
			return AjaxJson.getSuccess();
		}
		return AjaxJson.getError();
	}
	
}

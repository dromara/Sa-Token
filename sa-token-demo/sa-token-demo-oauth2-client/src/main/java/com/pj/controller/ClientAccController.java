package com.pj.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejlchina.okhttps.OkHttps;
import com.pj.utils.AjaxJson;
import com.pj.utils.SoMap;

import cn.dev33.satoken.stp.StpUtil;

/**
 * 登录注册Controller 
 * @author kong 
 */
@RestController
public class ClientAccController {
	
	
	// 返回当前登录者的账号id, 如果未登录, 返回null
	@RequestMapping("/getLoginInfo")
	public AjaxJson getLoginInfo() {
		Object loginId = StpUtil.getLoginIdDefaultNull();
		return AjaxJson.getSuccessData(loginId);
	}

	// 注销登录 
	@RequestMapping("/logout")
	public AjaxJson logout() {
		StpUtil.logout();
		return AjaxJson.getSuccess();
	}
	
	// 根据code码进行登录 
	@RequestMapping("/doCodeLogin")
	public AjaxJson doCodeLogin(String code) {
		System.out.println("------------------ 成功进入请求 ------------------");
		
		// 请求服务提供方接口地址，获取 access_token 以及其他信息
		// 携带三个关键参数: code、client_id、client_secret
		String str = OkHttps.sync("http://localhost:8001/oauth2/getAccessToken")
				.addBodyPara("code", code)
				.addBodyPara("client_id", "123123123")
				.addBodyPara("client_secret", "aaaa-bbbb-cccc-dddd-eeee")
				.post()
				.getBody()
				.toString();
		SoMap so = SoMap.getSoMap().setJsonString(str);
		System.out.println("返回结果: " + so);
		
		// code不等于200  代表请求失败
		if(so.getInt("code") != 200) {
			return AjaxJson.getError(so.getString("msg"));
		}
		
		// 根据openid获取其对应的userId  
		String openid = so.getString("openid");
		long userId = getUserIdByOpenid(openid);
		
		// 登录并返回账号信息 
		StpUtil.login(userId);
		return AjaxJson.getSuccessData(userId).set("openid", openid);
	}
	
	
	
	// ------------ 模拟方法 ------------------ 
	
	// 模拟方法：根据openid获取userId 
	private long getUserIdByOpenid(String openid) {
		// 此方法仅做模拟，实际开发要根据具体业务逻辑来获取userId
		return 10001;
	}
	
	
}

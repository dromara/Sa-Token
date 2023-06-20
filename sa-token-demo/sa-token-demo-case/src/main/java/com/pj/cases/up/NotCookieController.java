package com.pj.cases.up;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token 前后端分离模式示例 
 * 
 * @author click33
 * @since 2022-10-17 
 */
@RestController
@RequestMapping("/NotCookie/")
public class NotCookieController {

	// 前后端一体模式的登录样例    ---- http://localhost:8081/NotCookie/doLogin?name=zhang&pwd=123456
	@RequestMapping("doLogin")
	public SaResult doLogin(String name, String pwd) {
		if("zhang".equals(name) && "123456".equals(pwd)) {
			// 会话登录 
			StpUtil.login(10001);
		    return SaResult.ok();
		}
		return SaResult.error("登录失败");
	}
	
	// 前后端分离模式的登录样例    ---- http://localhost:8081/NotCookie/doLogin2?name=zhang&pwd=123456
	@RequestMapping("doLogin2")
	public SaResult doLogin2(String name, String pwd) {
		
		if("zhang".equals(name) && "123456".equals(pwd)) {
			
			// 会话登录 
			StpUtil.login(10001);
			
			// 与常规登录不同点之处：这里需要把 Token 信息从响应体中返回到前端 
			SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
		    return SaResult.data(tokenInfo);
		}
		return SaResult.error("登录失败");
	}
	
}


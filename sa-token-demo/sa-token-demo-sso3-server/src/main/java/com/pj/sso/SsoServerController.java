package com.pj.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.ejlchina.okhttps.OkHttps;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.sso.SaSsoHandle;
import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token-SSO Server端 Controller 
 * @author kong
 *
 */
@RestController
public class SsoServerController {

	// SSO-Server端：处理所有SSO相关请求 
	@RequestMapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoHandle.serverRequest();
	}

	// 自定义接口：获取userinfo 
	@RequestMapping("/sso/userinfo")
	public Object userinfo(String loginId, String secretkey) {
		System.out.println("---------------- 获取userinfo --------");
		
		// 校验调用秘钥 
		SaSsoUtil.checkSecretkey(secretkey);
		
		// 自定义返回结果（模拟）
		return SaResult.ok()
				.set("id", loginId)
				.set("name", "linxiaoyu")
				.set("sex", "女")
				.set("age", 18);
	}
	
	// 配置SSO相关参数 
	@Autowired
	private void configSso(SaTokenConfig cfg) {
		cfg.sso
			// SSO-Server端：未登录时返回的View 
			.setNotLoginView(() -> {
				return new ModelAndView("sa-login.html");
			})
			// SSO-Server端：登录函数 
			.setDoLoginHandle((name, pwd) -> {
				// 此处仅做模拟登录，真实环境应该查询数据进行登录 
				if("sa".equals(name) && "123456".equals(pwd)) {
					StpUtil.login(10001);
					return SaResult.ok("登录成功！");
				}
				return SaResult.error("登录失败！");
			})
			// 配置Http请求处理器 
			.setSendHttp(url -> {
				// 此处为了提高响应速度这里可将sync换为async
				return OkHttps.sync(url).get();
			})
			;
	}

	// 全局异常拦截 
	@ExceptionHandler
	public SaResult handlerException(Exception e) {
		e.printStackTrace(); 
		return SaResult.error(e.getMessage());
	}
	
}

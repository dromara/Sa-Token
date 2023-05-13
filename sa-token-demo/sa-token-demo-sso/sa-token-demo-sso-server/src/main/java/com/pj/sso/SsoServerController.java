package com.pj.sso;

import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sign.SaSignUtil;
import cn.dev33.satoken.sso.SaSsoProcessor;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.dtflys.forest.Forest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Sa-Token-SSO Server端 Controller 
 * @author click33
 *
 */
@RestController
public class SsoServerController {

	/*
	 * SSO-Server端：处理所有SSO相关请求 
	 * 		http://{host}:{port}/sso/auth			-- 单点登录授权地址，接受参数：redirect=授权重定向地址 
	 * 		http://{host}:{port}/sso/doLogin		-- 账号密码登录接口，接受参数：name、pwd 
	 * 		http://{host}:{port}/sso/checkTicket	-- Ticket校验接口（isHttp=true时打开），接受参数：ticket=ticket码、ssoLogoutCall=单点注销回调地址 [可选] 
	 * 		http://{host}:{port}/sso/signout		-- 单点注销地址（isSlo=true时打开），接受参数：loginId=账号id、secretkey=接口调用秘钥 
	 */
	@RequestMapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoProcessor.instance.serverDister();
	}

	// 配置SSO相关参数 
	@Autowired
	private void configSso(SaSsoConfig sso) {
		
		// 配置：未登录时返回的View 
		sso.setNotLoginView(() -> {
			return new ModelAndView("sa-login.html");
		});
		
		// 配置：登录处理函数 
		sso.setDoLoginHandle((name, pwd) -> {
			// 此处仅做模拟登录，真实环境应该查询数据进行登录 
			if("sa".equals(name) && "123456".equals(pwd)) {
				StpUtil.login(10001);
				return SaResult.ok("登录成功！").setData(StpUtil.getTokenValue());
			}
			return SaResult.error("登录失败！");
		});
		
		// 配置 Http 请求处理器 （在模式三的单点注销功能下用到，如不需要可以注释掉） 
		sso.setSendHttp(url -> {
			try {
				// 发起 http 请求 
				System.out.println("------ 发起请求：" + url);
				return Forest.get(url).executeAsString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	// 示例：获取数据接口（用于在模式三下，为 client 端开放拉取数据的接口）
	@RequestMapping("/sso/getData")
	public SaResult getData(String apiType, String loginId) {
		System.out.println("---------------- 获取数据 ----------------");
		System.out.println("apiType=" + apiType);
		System.out.println("loginId=" + loginId);

		// 校验签名：只有拥有正确秘钥发起的请求才能通过校验
		SaSignUtil.checkRequest(SaHolder.getRequest());

		// 自定义返回结果（模拟）
		return SaResult.ok()
				.set("id", loginId)
				.set("name", "LinXiaoYu")
				.set("sex", "女")
				.set("age", 18);
	}

}

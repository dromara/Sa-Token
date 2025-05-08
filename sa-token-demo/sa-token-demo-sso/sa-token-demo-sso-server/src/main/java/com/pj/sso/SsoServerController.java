package com.pj.sso;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sso.processor.SaSsoServerProcessor;
import cn.dev33.satoken.sso.template.SaSsoServerTemplate;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
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

	/**
	 * SSO-Server端：处理所有SSO相关请求 
	 * 		http://{host}:{port}/sso/auth			-- 单点登录授权地址
	 * 		http://{host}:{port}/sso/doLogin		-- 账号密码登录接口，接受参数：name、pwd
	 * 		http://{host}:{port}/sso/signout		-- 单点注销地址（isSlo=true时打开）
	 */
	@RequestMapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoServerProcessor.instance.dister();
	}

	// 配置SSO相关参数 
	@Autowired
	private void configSso(SaSsoServerTemplate ssoServerTemplate) {

		// 配置：未登录时返回的View 
		ssoServerTemplate.strategy.notLoginView = () -> {
			return new ModelAndView("sa-login.html");
		};
		
		// 配置：登录处理函数 
		ssoServerTemplate.strategy.doLoginHandle = (name, pwd) -> {
			// 此处仅做模拟登录，真实环境应该查询数据库进行登录
			if("sa".equals(name) && "123456".equals(pwd)) {
				String deviceId = SaHolder.getRequest().getParam("deviceId", SaFoxUtil.getRandomString(32));
				StpUtil.login(10001, new SaLoginParameter().setDeviceId(deviceId));
				return SaResult.ok("登录成功！").setData(StpUtil.getTokenValue());
			}
			return SaResult.error("登录失败！");
		};

		// 添加消息处理器：userinfo (获取用户资料) （用于为 client 端开放拉取数据的接口）
		ssoServerTemplate.messageHolder.addHandle("userinfo", (ssoTemplate, message) -> {
			System.out.println("收到消息：" + message);

			// 自定义返回结果（模拟）
			return SaResult.ok()
					.set("id", message.get("loginId"))
					.set("name", "LinXiaoYu")
					.set("sex", "女")
					.set("age", 18);
		});

	}

}

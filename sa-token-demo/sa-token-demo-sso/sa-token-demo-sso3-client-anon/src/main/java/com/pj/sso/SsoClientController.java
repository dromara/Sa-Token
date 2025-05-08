package com.pj.sso;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoClientUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sa-Token-SSO Client端 Controller 
 * @author click33
 */
@RestController
public class SsoClientController {

	// 首页 
	@RequestMapping("/")
	public String index() {
		String str = "<h2>Sa-Token SSO-Client 应用端 (模式三-匿名应用)</h2>" +
					"<p>当前会话是否登录：" + StpUtil.isLogin() + " (" + StpUtil.getLoginId("") + ")</p>" +
					"<p> " +
						"<a href='/sso/login?back=/'>登录</a> - " +
						"<a href='/sso/logoutByAlone?back=/'>单应用注销</a> - " +
						"<a href='/sso/logout?back=self&singleDeviceIdLogout=true'>单浏览器注销</a> - " +
						"<a href='/sso/logout?back=self'>全端注销</a> - " +
						"<a href='/sso/myInfo' target='_blank'>账号资料</a>" +
					"</p>";
		return str;
	}

	/*
	 * SSO-Client端：处理所有SSO相关请求 
	 * 		http://{host}:{port}/sso/login			-- Client 端登录地址
	 * 		http://{host}:{port}/sso/logout			-- Client 端注销地址（isSlo=true时打开）
	 * 		http://{host}:{port}/sso/pushC			-- Client 端接收消息推送地址
	 */
	@RequestMapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoClientProcessor.instance.dister();
	}

	// 配置SSO相关参数
	@Autowired
	private void configSso(SaSsoClientTemplate ssoClientTemplate) {
		// 重写 loginId 与 centerId 转换策略函数，做到本地应用 userId 与认证中心 userId 的互相映射

//		// 将 centerId 转换为 loginId 的函数
//		ssoClientTemplate.strategy.convertCenterIdToLoginId = (centerId) -> {
//			return "Stu" + centerId;
//		};
//		// 将 loginId 转换为 centerId 的函数
//		ssoClientTemplate.strategy.convertLoginIdToCenterId = (loginId) -> {
//			return loginId.toString().substring(3);
//		};
	}

	// 当前应用独自注销 (不退出其它应用)
	@RequestMapping("/sso/logoutByAlone")
	public Object logoutByAlone() {
		StpUtil.logout();
		return SaSsoClientProcessor.instance._ssoLogoutBack(SaHolder.getRequest(), SaHolder.getResponse());
	}

	// 查询我的账号信息：sso-client 前端 -> sso-center 后端 -> sso-server 后端
	@RequestMapping("/sso/myInfo")
	public Object myInfo() {
		// 如果尚未登录
		if( ! StpUtil.isLogin()) {
			return "尚未登录，无法获取";
		}

		// 原写法：直接调用 StpUtil.getLoginId() 当做 centerId 来提交
		// Object centerId = StpUtil.getLoginId();

		// 新写法：获取本地 loginId 对应的认证中心 centerId
		Object centerId = SaSsoClientUtil.getSsoTemplate().strategy.convertLoginIdToCenterId.run(StpUtil.getLoginId());

		// 推送消息
		SaSsoMessage message = new SaSsoMessage();
		message.setType("userinfo");
		message.set("loginId", centerId);
		SaResult result = SaSsoClientUtil.pushMessageAsSaResult(message);

		// 返回给前端
		return result;
	}

}

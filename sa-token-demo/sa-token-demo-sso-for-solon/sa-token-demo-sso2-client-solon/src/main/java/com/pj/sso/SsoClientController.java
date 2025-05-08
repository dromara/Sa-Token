package com.pj.sso;


import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoClientTemplate;
import cn.dev33.satoken.sso.template.SaSsoClientUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.noear.solon.annotation.*;
import org.noear.solon.boot.web.MimeType;

/**
 * Sa-Token-SSO Client端 Controller 
 * @author click33
 */
@Controller
@Configuration
public class SsoClientController {

	// 首页
	@Produces(MimeType.TEXT_HTML_VALUE)
	@Mapping("/")
	public String index() {
		String str = "<h2>Sa-Token SSO-Client 应用端 (模式二)</h2>" +
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
	@Mapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoClientProcessor.instance.dister();
	}

	// 配置SSO相关参数
	@Bean
	private void configSso(SaSsoClientTemplate ssoClientTemplate) {

	}

	// 当前应用独自注销 (不退出其它应用)
	@Mapping("/sso/logoutByAlone")
	public Object logoutByAlone() {
		StpUtil.logout();
		return SaSsoClientProcessor.instance._ssoLogoutBack(SaHolder.getRequest(), SaHolder.getResponse());
	}

	// 查询我的账号信息：sso-client 前端 -> sso-center 后端 -> sso-server 后端
	@Mapping("/sso/myInfo")
	public Object myInfo() {
		// 如果尚未登录
		if( ! StpUtil.isLogin()) {
			return "尚未登录，无法获取";
		}

		// 获取本地 loginId
		Object loginId = StpUtil.getLoginId();

		// 推送消息
		SaSsoMessage message = new SaSsoMessage();
		message.setType("userinfo");
		message.set("loginId", loginId);
		SaResult result = SaSsoClientUtil.pushMessageAsSaResult(message);

		// 返回给前端
		return result;
	}

}

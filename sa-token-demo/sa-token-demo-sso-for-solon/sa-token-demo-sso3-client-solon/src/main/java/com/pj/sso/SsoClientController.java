package com.pj.sso;

import cn.dev33.satoken.sso.processor.SaSsoClientProcessor;
import cn.dev33.satoken.sso.template.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

import java.util.HashMap;
import java.util.Map;

/**
 * Sa-Token-SSO Client端 Controller 
 * @author click33
 */
@Controller
public class SsoClientController implements Render {

	// SSO-Client端：首页
	@Produces(MimeType.TEXT_HTML_VALUE)
	@Mapping("/")
	public String index() {
		String str = "<h2>Sa-Token SSO-Client 应用端</h2>" + 
					"<p>当前会话是否登录：" + StpUtil.isLogin() + "</p>" + 
					"<p><a href=\"javascript:location.href='/sso/login?back=' + encodeURIComponent(location.href);\">登录</a>" + 
					" <a href='/sso/logout?back=self'>注销</a></p>";
		return str;
	}
	
	/*
	 * SSO-Client端：处理所有SSO相关请求 
	 * 		http://{host}:{port}/sso/login			-- Client端登录地址，接受参数：back=登录后的跳转地址 
	 * 		http://{host}:{port}/sso/logout			-- Client端单点注销地址（isSlo=true时打开），接受参数：back=注销后的跳转地址 
	 * 		http://{host}:{port}/sso/logoutCall		-- Client端单点注销回调地址（isSlo=true时打开），此接口为框架回调，开发者无需关心
	 */
	@Mapping("/sso/*")
	public Object ssoRequest() {
		return SaSsoClientProcessor.instance.dister();
	}
	
	// 查询我的账号信息 
	@Mapping("/sso/myInfo")
	public Object myInfo() {
		// 组织请求参数
		Map<String, Object> map = new HashMap<>();
		map.put("apiType", "userinfo");
		map.put("loginId", StpUtil.getLoginId());

		// 发起请求
		Object resData = SaSsoUtil.getData(map);
		System.out.println("sso-server 返回的信息：" + resData);
		return resData;
	}

	// 全局异常拦截并转换
	@Override
	public void render(Object data, Context ctx) throws Throwable {
		if(data instanceof Exception){
			data = SaResult.error(((Exception)data).getMessage());
		}

		ctx.render(data);
	}
}
